package com.csc.fsg.life.xg.utils;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.serverutils.FileUtil;

/**
 * This Class identifies the duplicate service rules defined in rules section of
 * list.rules.
 */
public class ServiceRules {

	private static final Log log = LogFactory.getLog(ServiceRules.class);
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
	 * Processes rules list file and returns all rules.
	 * 
	 * @param rulesListFile the rules list file
	 * @return the list< string>
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static List<String> processRules(String rulesListFile)
			throws IOException, SAXException {

		FileInputStream fileReader = null;
		List<String> rules = new ArrayList<String>();
		try {
			// parse list.rules
			File xmlgConfig = new File(rulesListFile);
			fileReader = new FileInputStream(xmlgConfig);
			InputSource source = new InputSource(fileReader);
			Element element = docBuild.parse(source).getDocumentElement();

			// rules element
			NodeList rulesElems = element.getElementsByTagName("rules");
			int systemsLen = rulesElems.getLength();
			for (int s = 0; s < systemsLen; s++) {

				Element rulesElem = (Element) rulesElems.item(s);
				// rule element
				NodeList ruleList = rulesElem.getElementsByTagName("rule");
				for (int i = 0; i < ruleList.getLength(); i++) {
					Element rule = (Element) ruleList.item(i);
					String handlerClass = rule.getAttribute("handlerClass");
					rules.add(handlerClass);
				}
			}
		} finally {
			FileUtil.safeClose(fileReader);
		}
		return rules;
	}

	/**
	 * Processes rules list file and returns all service rules.
	 * 
	 * @param rulesListFile the rules list file
	 * @return the list< string>
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static List<String> processServiceRules(String rulesListFile)
			throws IOException, SAXException {

		List<String> serviceRules = new ArrayList<String>();
		FileInputStream fileReader = null;
		try {
			// parse list.rules
			File xmlgConfig = new File(rulesListFile);
			fileReader = new FileInputStream(xmlgConfig);
			InputSource source = new InputSource(fileReader);
			Element element = docBuild.parse(source).getDocumentElement();

			// systems
			NodeList serviceRulesElems = element.getElementsByTagName("serviceRules");
			int systemsLen = serviceRulesElems.getLength();
			for (int s = 0; s < systemsLen; s++) {

				Element serviceRulesElem = (Element) serviceRulesElems.item(s);
				NodeList sRuleList = serviceRulesElem.getElementsByTagName("rule");
				for (int i = 0; i < sRuleList.getLength(); i++) {
					Element rule = (Element) sRuleList.item(i);
					String handlerClass = rule.getAttribute("handlerClass");
					serviceRules.add(handlerClass);
				}
			}
		} finally {
			FileUtil.safeClose(fileReader);
		}
		return serviceRules;
	}

	/**
	 * Prints duplicate service rules listed in rules section of list.rules.
	 * 
	 * @param rulesListFile the rules list file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static void findDuplicateServiceRules(String rulesListFile)
			throws IOException, SAXException {

		List<String> rules = processRules(rulesListFile);
		List<String> serviceRules = processServiceRules(rulesListFile);
		for (int i = 0; i < serviceRules.size(); i++) {
			String serviceRule = serviceRules.get(i);
			if (rules.contains(serviceRule)) {
				log.info(i + " : " + serviceRule + " is duplicate.");
			}
		}
	}

	/**
	 * Prints duplicate service rules listed in rules section of list.rules.
	 * 
	 * @param rulesListFile the rules list file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static void printServiceRules(String rulesListFile)
			throws IOException, SAXException {

		List<String> serviceRules = processServiceRules(rulesListFile);
		for (int i = 0; i < serviceRules.size(); i++) {
			String serviceRule = serviceRules.get(i);
			log.info(i + " : " + serviceRule);
		}
	}

	/**
	 * Removes the duplicate service rules from rules section in list.rules .Be
	 * careful before using this method. This will alter the list.rules file.
	 * 
	 * @param rulesListFile the rules list file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 */
	public static void removeDuplicateServiceRules(String rulesListFile)
			throws IOException, SAXException {

		List<String> serviceRules = processServiceRules(rulesListFile);
		FileInputStream fileReader = null;
		List<String> rules = new ArrayList<String>();
		try {
			// parse list.rules
			File xmlgConfig = new File(rulesListFile);
			fileReader = new FileInputStream(xmlgConfig);
			InputSource source = new InputSource(fileReader);
			Element element = docBuild.parse(source).getDocumentElement();

			// rules element
			NodeList rulesElems = element.getElementsByTagName("rules");
			int systemsLen = rulesElems.getLength();
			for (int s = 0; s < systemsLen; s++) {

				Element rulesElem = (Element) rulesElems.item(s);
				// rule element
				NodeList ruleList = rulesElem.getElementsByTagName("rule");
				for (int i = 0; i < ruleList.getLength(); i++) {
					Element rule = (Element) ruleList.item(i);
					String handlerClass = rule.getAttribute("handlerClass");
					rules.add(handlerClass);
					if (serviceRules.contains(handlerClass)) {
						rulesElem.removeChild(rule);
					}
				}
			}
		} finally {
			FileUtil.safeClose(fileReader);
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

		String rulesListFile = "D:/XMLG/Test/Expressions/Rules/list.rules";
		// printServiceRules(rulesListFile);
		findDuplicateServiceRules(rulesListFile);
		// removeDuplicateServiceRules(rulesListFile);
		return;
	}
}
