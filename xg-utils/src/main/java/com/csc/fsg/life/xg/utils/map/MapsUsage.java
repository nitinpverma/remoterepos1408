package com.csc.fsg.life.xg.utils.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.serverutils.FileUtil;

/**
 * The Class provides functionality to identify used and unused maps in XMLG by
 * comparing xmlg-services.xml and list.maps.
 */
public class MapsUsage {

	private static final Log log = LogFactory.getLog(MapsUsage.class);

	private static final String ELM_SYSTEM = "System chane 3";
	private static final String ELM_EVENT = "Event";
	private static final String ELM_PARAMETER = "parameter";
	private static final String ELM_SERVICE = "Service";
	private static final String ELM_MAP = "map";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_RULE = "rule";
	private static DocumentBuilder docBuild = null;

	private static List<String> systemMaps = new ArrayList<String>();
	static {
		try {
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			systemMaps.add("AccordSuccess");
			systemMaps.add("CopyObjectErrorResponse");
			systemMaps.add("ErrorResponse");
			systemMaps.add("MissingElementError");
			systemMaps.add("ObjectNotFoundError");
			systemMaps.add("SystemNotAvailableError");
			systemMaps.add("VantageErrorResponse");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes xmlg-services.xml file and returns all systems used.
	 * 
	 * @param configFile the config file
	 * 
	 * @return the map< string, list< event>>
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static Map<String, List<Event>> processServiceConfig(
			String configFile) throws IOException, SAXException {

		Map<String, List<Event>> systems = new HashMap<String, List<Event>>();
		FileInputStream fileReader = null;
		try {
			// parse xmlg-services.xml
			File xmlgConfig = new File(configFile);
			fileReader = new FileInputStream(xmlgConfig);
			InputSource source = new InputSource(fileReader);
			Element element = docBuild.parse(source).getDocumentElement();

			// systems
			NodeList systemElems = element.getElementsByTagName(ELM_SYSTEM);
			int systemsLen = systemElems.getLength();
			for (int s = 0; s < systemsLen; s++) {

				Element systemsElem = (Element) systemElems.item(s);
				String systemId = systemsElem.getAttribute(ATTR_NAME);
				// log.info("System : " + systemId);

				// events
				List<Event> events = new ArrayList<Event>();

				NodeList eventElems = systemsElem.getElementsByTagName(ELM_EVENT);
				int eventsLen = eventElems.getLength();
				for (int i = 0; i < eventsLen; i++) {

					Element eventElem = (Element) eventElems.item(i);
					String eventId = eventElem.getAttribute(ATTR_TYPE);
					// log.info("\tService : " + eventId);
					// services
					Event event = new Event(eventId);

					NodeList serviceElems = eventElem.getElementsByTagName(ELM_SERVICE);
					int servicesLen = serviceElems.getLength();
					for (int j = 0; j < servicesLen; j++) {

						Element serviceElem = (Element) serviceElems.item(j);
						String serviceRule = serviceElem.getAttribute(ATTR_RULE);
						// log.info("\t\tService Rule/Product : " +
						// serviceRule);

						Service service = new Service(serviceRule);
						// get list of parameters in the config file
						// maps
						NodeList paramElems = serviceElem.getElementsByTagName(ELM_PARAMETER);
						int length = paramElems.getLength();
						for (int k = 0; k < length; k++) {

							// create beans element as root element
							Element paramElem = (Element) paramElems.item(k);
							String name = paramElem.getAttribute(ATTR_NAME);
							String value = paramElem.getAttribute(ATTR_VALUE);
							if (name.indexOf("map") > 0) {
								// log.info(name + " : " + value);
								// log.info("\t\t\tMap : " + value);
								service.addMap(value);
							}
						}
						event.addService(service);
					}
					events.add(event);
				}
				systems.put(systemId, events);
			}
		} finally {
			FileUtil.safeClose(fileReader);
		}
		return systems;
	}

	/**
	 * Processes list.maps file and returns list of all map names.
	 * 
	 * @param mapListFile the map list file
	 * 
	 * @return the list< string>
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static List<String> processMapsList(String mapListFile)
			throws IOException, SAXException {

		List<String> allMaps = new ArrayList<String>();
		FileInputStream fileReader = null;
		try {
			// parse list.maps
			File mapsList = new File(mapListFile);
			fileReader = new FileInputStream(mapsList);
			InputSource source = new InputSource(fileReader);
			Element element = docBuild.parse(source).getDocumentElement();

			NodeList maps = element.getElementsByTagName(ELM_MAP);
			int mapsLen = maps.getLength();
			for (int i = 0; i < mapsLen; i++) {

				Element mapElem = (Element) maps.item(i);
				String mapName = mapElem.getAttribute(ATTR_NAME);
				allMaps.add(mapName);
				// log.info(i + " Map Name : " + mapName);
			}
		} finally {
			FileUtil.safeClose(fileReader);
		}
		return allMaps;
	}

	/**
	 * Prints all used maps in XMLG excluding system maps and OM maps.
	 * 
	 * @param servicesConfig the services config
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static void usedMaps(String servicesConfig) throws IOException,
			SAXException {
		log.info("Used Maps : ");
		Map<String, List<Event>> systems = processServiceConfig(servicesConfig);
		Set<Entry<String, List<Event>>> systemsSet = systems.entrySet();
		for (Entry<String, List<Event>> system : systemsSet) {

			String systemName = system.getKey();
			log.info("System : " + systemName);
			List<Event> events = system.getValue();

			for (Event event : events) {

				String eventName = event.getName();
				log.info("\tEvent : " + eventName);
				List<Service> services = event.getServices();

				for (Service service : services) {
					String serviceRule = service.getServiceRule();
					log.info("\t\tService Rule: " + serviceRule);
					List<String> usedMaps = service.getMaps();

					for (String usedMap : usedMaps) {
						log.info("\t\t\tMap : " + usedMap);
					}
				}
			}
		}
		// System maps
		log.info("\tSystem Maps");
		for (String systemMap : systemMaps) {
			log.info("\t\tSystem Map" + systemMap);
		}
	}

	/**
	 * Prints all unused maps excluding OM maps.
	 * 
	 * @param servicesConfig the services config
	 * @param mapListFile the map list file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static void unusedMaps(String servicesConfig, String mapListFile)
			throws IOException, SAXException {
		log.info("Unused Maps : ");
		Map<String, List<Event>> systems = processServiceConfig(servicesConfig);
		Set<Entry<String, List<Event>>> systemsSet = systems.entrySet();
		List<String> maps = processMapsList(mapListFile);
		for (String map : maps) {
			boolean found = false;
			for (Entry<String, List<Event>> system : systemsSet) {

				List<Event> events = system.getValue();

				for (Event event : events) {

					List<Service> services = event.getServices();

					for (Service service : services) {
						List<String> usedMaps = service.getMaps();
						for (String usedMap : usedMaps) {
							if (usedMap.equalsIgnoreCase(map)){
								found = true;
								break;
							}
						}
//						if (usedMaps.contains(map)) {
//							found = true;
//							break;
//						}
						if (found)
							break;
					}
					if (found)
						break;
				}
				if (found)
					break;
			}
			if (!found && !systemMaps.contains(map)) {
				log.info(map);
			}
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static void main(String args[]) throws IOException, SAXException {

		if (args.length < 1) {
			System.out.println("Usage: <XMLG Services Config file> <maps list file>");
			return;
		}
		String servicesConfig = args[0];
		if (args.length == 1) {
			usedMaps(servicesConfig);
		} else {
			String mapListFile = args[1];
			unusedMaps(servicesConfig, mapListFile);
		}
		return;
	}
}
