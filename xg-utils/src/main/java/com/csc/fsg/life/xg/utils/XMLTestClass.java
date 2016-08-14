package com.csc.fsg.life.xg.utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLTestClass {

	private Document parse() throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = fac.newDocumentBuilder();
		Document document = docBuilder.parse("c:\\testclient\\input\\wma\\eaonly.xml");
		return document;
	}

	public void test() throws SAXException, IOException,
			ParserConfigurationException, XPathExpressionException {
		Document doc = parse();
		// XObject eval = XPathAPI.eval(doc, "//SituationCode");
		// System.out.println("Value: "+eval);
		// NodeList nodeList = null;//XPathAPI.selectNodeList(doc,
		// "//Relation/RelationRoleCode[@tc != \"121\"]");

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		// XPathExpression expr =
		// xpath.compile("//Person[FirstName='Murali']/LastName/text()");
		XPathExpression expr = xpath.compile("//Party[@id='Party_5']//CompanyProducerID/text()");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}
		// int length = nodeList.getLength();
		// for (int i = 0; i < length; i++) {
		// Node n = nodeList.item(i);
		// if( n instanceof Element ){
		// Element elem = (Element) n;
		// String attrVal = elem.getAttribute("id");
		// System.out.println("Node value: "+attrVal);
		// attrVal = XPathAPI.eval(elem,
		// "//OriginatingObjectID[@tc]").toString();
		// // System.out.println("Node value 2: "+attrVal);
		// }
		//				
		// }
	}

	public static void main(String[] args) {
		try {
			new XMLTestClass().test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
