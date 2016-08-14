package com.csc.fsg.life.xg.utils;

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The Class ServicesTypes. This program has been built based on
 * xmlg-services.xml (prior to 0810). To make it work with latest version, see
 * {@link Services}
 */
public class ServicesTypes {

	private static final String VALUE_ATTR = "value";
	private static final String NAME_ATTR = "name";
	private static final String CICS = "com.csc.fsg.life.webservices.CICSWebService";
	private static final String JBO = "com.csc.fsg.life.webservices.ObjectWebService";
	private static final String COPYOBJECT = "com.csc.fsg.life.webservices.CopyObjectWebService";
	private static int totalServicesPerSys = 0;

	private static DocumentBuilder docBuild = null;

	static {
		try {
			docBuild = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert config.
	 * 
	 * @param configFile the config file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static void convertConfig(String configFile) throws IOException,
			SAXException {

		File xmlgConfig = new File(configFile);
		FileReader fileReader = new FileReader(xmlgConfig);
		int size = (int) xmlgConfig.length();
		char data[] = new char[size];
		for (int charsRead = 0; charsRead < size; charsRead += fileReader.read(
				data, charsRead, size)) {
		}
		fileReader.close();
		InputSource source = new InputSource(new ByteArrayInputStream(
				(new String(data)).getBytes()));
		Element element = docBuild.parse(source).getDocumentElement();
		// Parse all system elements and convert 'em as beans
		parseSystems(element);

	}

	/**
	 * Parses the systems.
	 * 
	 * @param outputDoc the output doc
	 * @param root the root
	 * @param element the element
	 * @throws IOException
	 */
	private static void parseSystems(Element element) throws IOException {

		// parse all systems
		NodeList systems = element.getElementsByTagName("System");
		int length = systems.getLength();

		for (int i = 0; i < length; i++) {

			totalServicesPerSys = 0;
			Element systemEle = (Element) systems.item(i);
			String systemId = systemEle.getAttribute(NAME_ATTR);
			System.out.println(systemId);
			// parse all events
			parseEvents(systemEle);
			System.out
					.println(totalServicesPerSys + " Services in " + systemId);
		}
	}

	/**
	 * Parses the events.
	 * 
	 * @param outputDoc the output doc
	 * @param root the root
	 * @param system the system
	 * @param systemEle the system ele
	 */
	private static void parseEvents(Element systemEle) {

		// parse all events
		NodeList events = systemEle.getElementsByTagName("Event");
		int length = events.getLength();

		for (int i = 0; i < length; i++) {

			Element eventEle = (Element) events.item(i);
			String eventKey = eventEle.getAttribute("type");
			System.out.print("\t" + eventKey);
			// parses all services
			parseServices(eventEle);
		}
	}

	/**
	 * Parses the services.
	 * 
	 * @param outputDoc the output doc
	 * @param root the root
	 * @param event the event
	 * @param eventEle the event ele
	 */
	private static void parseServices(Element eventEle) {

		// parse all services
		NodeList services = eventEle.getElementsByTagName("Service");
		int length = services.getLength();
		System.out.println(" - " + length);
		for (int i = 0; i < length; i++) {
			totalServicesPerSys++;
			Element serviceEle = (Element) services.item(i);
			String serviceKey = serviceEle.getAttribute("rule");
			System.out.print("\t\t" + serviceKey);
			parseParameters(serviceEle);
		}
	}

	/**
	 * Parses the parameters.
	 * 
	 * @param outputDoc the output doc
	 * @param serviceEle the service ele
	 * @param service the service
	 */
	private static void parseParameters(Element serviceEle) {

		NodeList params = serviceEle.getElementsByTagName("parameter");
		int paramsLength = params.getLength();

		for (int j = 0; j < paramsLength; j++) {

			Element paramEle = (Element) params.item(j);
			String value = paramEle.getAttribute(VALUE_ATTR);
			String name = paramEle.getAttribute(NAME_ATTR);
			if (name.equalsIgnoreCase("serviceClassName")) {
				// System.out.println(name +" : " + value);
				if (value.equals(CICS)) {
					System.out.println(" - COPYBOOK");
				} else if (value.equals(JBO)) {
					System.out.println(" - JBO");
				} else if (value.equals(COPYOBJECT)) {
					System.out.println(" - COPYBOOK/COPYOBJECT");
				} else {
					System.out.println(" - UNKOWN");
				}
			}
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static void main(String args[]) throws IOException, SAXException {

		if (args.length < 1) {
			System.out.println("Usage: XMLG Services Config file");
			return;
		}
		String xmlgConfig = args[0];
		convertConfig(xmlgConfig);
		System.out.println("Migration Completed.");
		return;
	}
}
