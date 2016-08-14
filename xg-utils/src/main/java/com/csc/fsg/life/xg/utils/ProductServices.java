package com.csc.fsg.life.xg.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class used to convert the service-config.xml into product specific configs prior to 0910.
 * The Class ServicesTypes.
 * 
 * @deprecated not useful for the 0920 and later
 */
public class ProductServices {

	private static final String VALUE_ATTR = "value";
	private static final String NAME_ATTR = "name";
	private static int totalServicesPerSys = 0;
	private static final String ANN = "ANN";
	private static final String PAYOUT = "PAYOUT";
	private static final String UL = "UL";
	private static final String TRD = "TRD";
	private static final String TRAD = "TRAD";
	private static final String ARCH = "ARCH";
	private static String serviceType = null;

	private static DocumentBuilder docBuild = null;

	static {
		try {
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
		FileReader fileReader = null;
		char data[];
		try {
			fileReader = new FileReader(xmlgConfig);
			int size = (int) xmlgConfig.length();
			data = new char[size];
			for (int charsRead = 0; charsRead < size; charsRead += fileReader.read(data, charsRead, size)) {
			}
		} finally {
			if (fileReader != null)
				fileReader.close();
		}
		InputSource source = new InputSource(new ByteArrayInputStream((new String(data)).getBytes()));
		Element element = docBuild.parse(source).getDocumentElement();
		// ExportImport.parseInput(new File(mapName));
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

			// totalServicesPerSys = 0;
			Element systemEle = (Element) systems.item(i);
			String systemId = systemEle.getAttribute(NAME_ATTR);
			System.out.println(systemId);
			// parse all events
			parseEvents(systemEle);
			// System.out.println(totalServicesPerSys + " Services in " +
			// systemId);
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
		String eventKey = eventEle.getAttribute("type");
		// System.out.println("\t" + eventKey + " - " + length);

		for (int i = 0; i < length; i++) {
			totalServicesPerSys++;
			Element serviceEle = (Element) services.item(i);
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

		String serviceKey = serviceEle.getAttribute("rule");

		NodeList params = serviceEle.getElementsByTagName("parameter");
		int paramsLength = params.getLength();

		for (int j = 0; j < paramsLength; j++) {

			Element paramEle = (Element) params.item(j);
			String value = paramEle.getAttribute(VALUE_ATTR);
			String name = paramEle.getAttribute(NAME_ATTR);
			String serviceKeyUpp = serviceKey.toUpperCase();
			int index = serviceKeyUpp.indexOf("RULE");
			if (index != -1) {
				serviceKeyUpp = serviceKeyUpp.substring(0, index);
			}
			if (name.indexOf("map") > 0) {
				if (serviceType.equals(ANN)) {

					if (serviceKeyUpp.indexOf(serviceType) != -1
							|| serviceKeyUpp.indexOf(PAYOUT) != -1
							|| value.toUpperCase().indexOf(serviceType) != -1
							|| value.toUpperCase().indexOf(PAYOUT) != -1) {

						System.out.println("\t\t" + serviceType + "\t: "
								+ value + "\t\t\t: " + serviceKey);
					}
				} else if (serviceType.equals(TRAD)) {
					if ((serviceKeyUpp.indexOf(TRD) != -1)
							|| (serviceKeyUpp.indexOf(TRAD) != -1)
							|| (value.toUpperCase().indexOf(TRD) != -1)
							|| (value.toUpperCase().indexOf(TRAD) != -1)) {

						System.out.println("\t\t" + serviceType + "\t: "
								+ value + "\t\t\t: " + serviceKey);
					}
				} else if (serviceType.equals(ARCH)) {
					if ((serviceKeyUpp.indexOf(TRD) == -1)
							&& (serviceKeyUpp.indexOf(TRAD) == -1)
							&& (serviceKeyUpp.indexOf(ANN) == -1)
							&& (serviceKeyUpp.indexOf(PAYOUT) == -1)
							&& (serviceKeyUpp.indexOf(UL) == -1)
							&& (value.toUpperCase().indexOf(TRD) == -1)
							&& (value.toUpperCase().indexOf(TRAD) == -1)
							&& (value.toUpperCase().indexOf(ANN) == -1)
							&& (value.toUpperCase().indexOf(PAYOUT) == -1)
							&& (value.toUpperCase().indexOf(UL) == -1)) {

						System.out.println("\t\t" + serviceType + "\t: "
								+ value + "\t\t\t: " + serviceKey);
					}
				} else if (serviceType.equals(UL)) {
					if (serviceKeyUpp.indexOf(serviceType) != -1
							|| (value.toUpperCase().indexOf(serviceType) != -1)) {

						System.out.println("\t\t" + serviceType + "\t: "
								+ value + "\t\t\t: " + serviceKey);
					}
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
		// serviceType = ANN;
		// convertConfig(xmlgConfig);

		// serviceType = TRAD;
		// convertConfig(xmlgConfig);

		serviceType = UL;
		convertConfig(xmlgConfig);

		// serviceType = ARCH;
		// convertConfig(xmlgConfig);
		System.out.println("Completed.");
		return;
	}
}
