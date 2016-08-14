package com.csc.fsg.life.xg.utils.jbo.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * The Class JBO to Wrapper classes generator.
 */
public class JboToWrappers {

	private static final String ELM_MAP_ELEMENT = "mapElement";
	private static final String ELM_SRC_ELEMENT = "srcElement";
	private static final String ELM_PARAMETER = "parameter";
	private static final String ELM_SERVICE = "Service";
	private static final String ATTR_TARGET_PATH = "targetPath";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_SOURCE_PATH = "sourcePath";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_NAME = "name";

	private static final Log log = LogFactory.getLog(JboToWrappers.class);
	private static DocumentBuilder docBuild = null;
	private String envKey;
	private String pkg;

	static {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"config/xmlg-config.xml");
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);
		try {
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates a new jbo to wrappers.
	 * 
	 * @param envKey the env key
	 */
	public JboToWrappers(String envKey) {
		this.envKey = envKey;
	}

	/**
	 * Checks Mapped elements.
	 * 
	 * @param maps the maps
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public void checkMappedElements(List<String> maps) throws IOException,
			SAXException {

		final String mapDir = EnvironmentInitializer.getInstance(envKey).getMapsLocation();
		Document outputDoc = docBuild.newDocument();
		Element root = outputDoc.createElement("maps");
		outputDoc.appendChild(root);
		// include all maps in one file
		for (String map : maps) {

			String mapPath = mapDir + map + ".xml";
			log.info(" Map : " + mapPath);
			Element mapEle = parseInput(new File(mapPath));
			Node mapNode = outputDoc.importNode(mapEle, true);
			root.appendChild(mapNode);
		}
		// search for target and source element paths
		processElements(root);
	}

	/**
	 * Parses the input.
	 * 
	 * @param file the file
	 * 
	 * @return the element
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public Element parseInput(File file) throws IOException, SAXException {

		FileInputStream fileReader = null;
		try {
			fileReader = new FileInputStream(file);
			// Parse the file's content into an xml document.
			InputSource source = new InputSource(fileReader);
			return docBuild.parse(source).getDocumentElement();
		} finally {
			FileUtil.safeClose(fileReader);
		}
	}

	/**
	 * Process elements, srcElement and targetElements in the map.xml file.
	 * 
	 * @param root the root
	 */
	private void processElements(Element root) {

		// Get all expressions from the map and check if they exist
		NodeList nl = root.getElementsByTagName(ELM_MAP_ELEMENT);
		int len = nl.getLength();
		Class dummyRoot = new Class("Dummy");
		dummyRoot.setIgnore(true);
		log.debug("  " + len + " map elements.");

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String targetPath = e.getAttribute(ATTR_TARGET_PATH);
			if (!targetPath.startsWith("TXLife")) {
				// log.info(targetPath);
				processPath(targetPath, "/", dummyRoot);
			}
		}
		nl = root.getElementsByTagName(ELM_SRC_ELEMENT);
		len = nl.getLength();
		log.debug("  " + len + " source maps elements.");
		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String sourcePath = e.getAttribute(ATTR_SOURCE_PATH);
			String type = e.getAttribute(ATTR_TYPE);
			if (!sourcePath.startsWith("TXLife") && type.equals("P")) {
				// log.info(sourcePath);
				processPath(sourcePath, "/", dummyRoot);
			}
		}
		// dummyRoot.print("  ");
		dummyRoot.createClassFiles(pkg + ".holding");
	}

	/**
	 * Process srouce/target path.
	 * 
	 * @param path the path
	 * @param delim the delim
	 * @param parent the parent
	 */
	private void processPath(String path, String delim, Class parent) {

		int indexOf = path.indexOf(delim);
		if (indexOf == -1) {
			// field, end of path
			String childPath = path.substring(indexOf + 2);
			if (!parent.exists(childPath)) {

				Field field = createField(childPath);
				parent.addField(field);
			}
		} else {

			String name = path.substring(0, indexOf);
			String childPath = path.substring(indexOf + 1);
			// handle the dots
			int dotIndex = name.indexOf('.');
			if (dotIndex != -1) {
				// name after the dot is concrete class
				String concreteClassName = getName(name.substring(dotIndex + 1));
				Class concreteClass = (Class) Class.getConcreteClass(concreteClassName);
				if (concreteClass == null) {

					concreteClass = createClass(concreteClassName, false, true);
					// add to global concrete class list
					Class.addConcreteClass(concreteClass);
				}
				if (parent.isConcreteClass()) {
					// if the parent is concrete class, add the name before dot
					// as parent of concrete class
					String concreteClassParent = getName(name.substring(0, dotIndex));
					Class subParent = (Class) parent.getField(concreteClassParent);
					if (subParent == null) {

						subParent = createClass(concreteClassParent, false, false);
						// subParent.setIgnore(true);
						parent.addField(subParent);
					}
					subParent.addConcreteClassRef(concreteClassName);
				} else {
					// parent not a concrete class, type should be List
					parent.setType(Class.LIST_TYPE);
					parent.setIgnore(true);
					parent.addConcreteClassRef(concreteClassName);
				}
				processPath(childPath, delim, concreteClass);
			} else {

				name = getName(name);
				Class subParent = (Class) parent.getField(name);

				if (subParent == null) {
					subParent = createClass(name, false, false);
					parent.addField(subParent);
				}
				processPath(childPath, delim, subParent);
			}
		}
	}

	/**
	 * Creates the field.
	 * 
	 * @param fieldName the field name
	 * 
	 * @return the field
	 */
	private Field createField(String fieldName) {

		Field field = new Field();
		field.setName(fieldName);
		field.setType(Class.STRING_TYPE);
		field.setGetter(true);
		field.setSetter(true);
		return field;
	}

	/**
	 * Gets the name, trims out the occurs numbers.
	 * 
	 * @param className the class name
	 * 
	 * @return the name
	 */
	private String getName(String className) {

		String name = className;
		int openBraceInd = name.indexOf('(');
		if (openBraceInd != -1) {
			name = name.substring(0, openBraceInd);
		}
		return name;
	}

	/**
	 * Creates the class.
	 * 
	 * @param className the class name
	 * @param isList the is list
	 * @param isConcreteClass the is concrete class
	 * 
	 * @return the class
	 */
	private Class createClass(String className, boolean isList,
			boolean isConcreteClass) {

		Class cls = new Class(className);
		if (isList) {
			cls.setType(Class.LIST_TYPE);
			cls.setIgnore(true);
		} else {
			cls.setType(className);
		}
		cls.setConcreteClass(isConcreteClass);
		return cls;
	}

	/**
	 * Processes the xmlg-services.xml config file.
	 * 
	 * @param configFile the config file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private void processConfig(String configFile) throws SAXException,
			IOException {

		// parse xmlg-services.xml
		File xmlgConfig = new File(configFile);
		Element element = parseInput(xmlgConfig);

		NodeList services = element.getElementsByTagName(ELM_SERVICE);
		int servicesLen = services.getLength();
		for (int i = 0; i < servicesLen; i++) {

			Element serviceElem = (Element) services.item(i);
			// get list of parameters in the config file
			NodeList parameters = serviceElem.getElementsByTagName(ELM_PARAMETER);
			int length = parameters.getLength();
			List<String> maps = new ArrayList<String>();
			for (int j = 0; j < length; j++) {

				// craete beans element as root element
				Element propElem = (Element) parameters.item(j);
				String name = propElem.getAttribute(ATTR_NAME);
				String value = propElem.getAttribute(ATTR_VALUE);
				if (name.indexOf("map") > 0) {
					log.info(name + " : " + value);
					maps.add(value);
					if (value.toUpperCase().indexOf("ANN") != -1) {
						pkg = "com.csc.fsg.life.wma.sm.annuity";
					} else if (value.toUpperCase().indexOf("UL") != -1) {
						pkg = "com.csc.fsg.life.wma.sm.ul";
					} else if (value.toUpperCase().indexOf("TRAD") != -1) {
						pkg = "com.csc.fsg.life.wma.sm.trad";
					} else {
						pkg = "com.csc.fsg.life.wma.sm.arch";
					}
				}
			}
			checkMappedElements(maps);
		}
	}

	public static void main(String args[]) throws IOException, SAXException {

		if (args.length < 1) {
			System.out.println("Usage: XMLG Services Config file");
			return;
		}
		JboToWrappers jboToWrappers = new JboToWrappers("TST");
		String xmlgConfig = args[0];
		jboToWrappers.processConfig(xmlgConfig);
		return;
	}
}
