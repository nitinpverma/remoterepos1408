/*
 * Modifications: Technical Enhancement 
 * T0126 , T0127, T0128 -CXF Upgrade,SOAP Faults
 */

package com.csc.fsg.life.webservices.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.csc.fsg.life.webservices.handlers.SoapErrHandler;
import com.csc.fsg.life.webservices.util.WebservicesUtil;

public class JaxWsTestClient {

	//DEFAULT TIME-OUT FOR CXF CONNECTION
	private static final int TIME_OUT = 300000;
	private static final String MAP = "Map";
	private static final String URN = "urn:";
	private static final String SERVICE = "nitin serviec3";
	private static final String MAP_NAMESPACE = "urn:TxMapService";
	private static final String MAP_LOCATOR = "MapLocator";
	private static final String MAP_LOCATOR_NAMESPACE = "urn:MapLocatorService";
	private static final String WEBSERVICES_NAMESPACE = "http://ws.xg.life.fsg.csc.com/";
	private static final String XG_FAULT_DIR = "XG_FAULT_DIRR";
	public JaxWsTestClient() {
	}

	public JaxWsTestClient(String inputFileDir) {
		this.inputFileDir = inputFileDir;
	}

	private String inputFileDir;
	private String NAME_SPACE;

	/**
	 * invoke webservices with the given file, given configurations.
	 * 
	 * @param filename
	 *            the filename
	 * @param variables
	 *            the variables
	 * @param data
	 *            the data
	 * @return the element
	 * @throws Exception
	 *             the exception
	 */
	public Element processDoc(String filename, WSClientVariables variables,	TimeData data) throws Exception {

		Document doc = getInputDocument(filename);
		return processDoc(doc, variables, data ,filename);
	}

	@SuppressWarnings({ "restriction", "rawtypes" })
	public Element processDoc(Document payloadDoc, WSClientVariables variables,TimeData data, String filename) throws SOAPException, MalformedURLException {
		 
		Element resultDocument =null;
		DispatchImpl proxy =null;
		
		try {
			
			String endpointUrl =variables.getEp().trim();
			String serviceName = "LifeWebServiceService";
			String portName = "LifeWebServicePort";
			String WSDLFile = endpointUrl+"?wsdl";
			URL wsdlURL = new URL(WSDLFile);
			constructNameSpaceForService(endpointUrl);
			QName service = new QName(NAME_SPACE,	 serviceName);
			QName port = new QName(NAME_SPACE, portName	);
					
			/** Create a service and add at least one port to it. **/ 
			Service xgService = Service.create(wsdlURL,service);
			Dispatch<DOMSource> dispatch = null;
			/** Create a Dispatch instance from a service.**/ 
			try {
				 dispatch = xgService.createDispatch(port, 		DOMSource.class, Service.Mode.MESSAGE);
			} catch (WebServiceException e) {
				// TODO: handle exception
				 portName = endpointUrl.substring(endpointUrl.lastIndexOf('/')+1);
				 port = new QName(NAME_SPACE, portName	);
				 dispatch = xgService.createDispatch(port, 		DOMSource.class, Service.Mode.MESSAGE);
			}
			
			/** Create SOAPMessage request. **/
			// compose a request message
			MessageFactory mf = MessageFactory.newInstance();

			// Create a message.  This example works with the SOAPPART.
			SOAPMessage request = mf.createMessage();
			SOAPPart part = request.getSOAPPart();

			// Obtain the SOAPEnvelope and header and body elements.
			SOAPEnvelope env = part.getEnvelope();
			SOAPBody body = env.getBody();
			//Add any jax-ws handlers
			// String trx = endpointUrl.substring(endpointUrl.lastIndexOf('/')+1);
			 SoapErrHandler handler = new SoapErrHandler(filename);
		        List<Handler> chain = new ArrayList<Handler>();
		        chain.add(handler);
				((BindingProvider) dispatch).getBinding().setHandlerChain(chain);
			// Add UserName ,PassWord as HTTP Basic Authentication
	        Map<String, Object> requestContext = ((BindingProvider) dispatch).getRequestContext();
	        requestContext.put(BindingProvider.USERNAME_PROPERTY, variables.getUser());
	        requestContext.put(BindingProvider.PASSWORD_PROPERTY, variables.getPassword());
	        Map<String, List<String>> headers = new HashMap<String, List<String>>();
	        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
	        
	    	// cxf specific to resolve default 60 sec time-out
			proxy = (DispatchImpl) dispatch;
			Client client=proxy.getClient();
			HTTPConduit http = (HTTPConduit) client.getConduit();
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			httpClientPolicy.setConnectionTimeout(TIME_OUT);
			httpClientPolicy.setAllowChunking(false);
			httpClientPolicy.setReceiveTimeout(TIME_OUT);
			http.setClient(httpClientPolicy);
			
		/*	HTTPConduit conduit = (HTTPConduit) proxy.getClient().getConduit();
			conduit.getClient().setReceiveTimeout(300000);
	*/
			
	// Added to store soap-faults to file
	        String outputDir = variables.getOutputFileDir();
	        String lastChar =outputDir.substring(outputDir.length() - 1); 
	        if(lastChar.equalsIgnoreCase("/") || lastChar.equalsIgnoreCase("\\") 
	        		|| lastChar.equalsIgnoreCase( System.getProperty("file.separator"))){
	        	outputDir=  outputDir.substring(0, outputDir.length() - 1);
	        }
	        requestContext.put(XG_FAULT_DIR, outputDir);
	        // Add applicable headers.
			String nsPrefix = "xg";
			String nsUrl = WSClientVariables.NAME_SPACE;
			if (variables.getDebugLevel() > 0)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "debuglevel", String.valueOf(variables.getDebugLevel()));
			if (variables.getMap() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "map",variables.getMap());
			if (variables.getMroindicator() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "mroindicator",variables.getMroindicator());
			if (variables.getMrocontrolregion() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "mrocontrolregion",variables.getMrocontrolregion());	
			if (variables.getFilecodes() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "filecodes",variables.getFilecodes());
			if (variables.getSystem() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "system",variables.getSystem());
			if (variables.getXgenv() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "xgenv",variables.getXgenv());
			if (variables.getDestInd() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "destind",variables.getDestInd());
			if (variables.getErrorOverride() != null)
				WebservicesUtil.addSoapHeader(request, nsPrefix, nsUrl, "errorOverride",variables.getErrorOverride());
			
			// Construct the message payload.
			body.addDocument(payloadDoc);
			request.saveChanges();
			
			/*
				//log the input SOAP Message
				try {
					request.writeTo(System.out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/
			
			/** Invoke the service endpoint. **/
			DOMSource reqSource = (DOMSource) WebservicesUtil.toSource(request);
			DOMSource resSource = dispatch.invoke(reqSource);

			/** Process the response. **/
			Document resPayloadDoc =WebservicesUtil.getSoapPayloadAsDocument(resSource);
			resultDocument = resPayloadDoc.getDocumentElement();
			
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}finally{
			try {
				proxy.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultDocument;
		
	}


	/**
	 * Construct name space for service.
	 *
	 * @param endpointUrl the endpoint url
	 */
	private void constructNameSpaceForService(String endpointUrl) {
		String serviceID=endpointUrl.substring(endpointUrl.lastIndexOf('/')+1);
		if(serviceID.equalsIgnoreCase(MAP)){
			this.NAME_SPACE=MAP_NAMESPACE;
		}else if (serviceID.equalsIgnoreCase(MAP_LOCATOR)) {
			this.NAME_SPACE=MAP_LOCATOR_NAMESPACE;
		}else{
			this.NAME_SPACE=URN+serviceID+SERVICE;
		}
		
	}

	/**
	 * Convert xml file to Document.
	 *
	 * @param inputFile the input file
	 * @return the output Document
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws TransformerConfigurationException the transformer configuration exception
	 */
	public Document getInputDocument(String inputFile) throws IOException,	SAXException, ParserConfigurationException,	TransformerConfigurationException {

		String fname = inputFile;
		if (inputFileDir != null)
			fname = inputFileDir + File.separator + inputFile;
		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();   
		  docBuilderFactory.setNamespaceAware(true);   
		  DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();   
		  Document doc =docBuilder.parse(new File(fname));
		  doc.getDocumentElement().normalize();
		return doc;
	}

}
