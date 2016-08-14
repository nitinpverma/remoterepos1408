/*********************************************************************************
 * XGParser class looks for the well-formed & valid ness of the input XML
 * document* Creation date: (08/06/02 01:53:00 PM) *
 * 
 * @author: Muralidhar R Gandham *
 *********************************************************************************/

package com.csc.fsg.life.xg.utils.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class XGParser {

	public static void validate(File inputFile) throws SAXException,
			IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// SAXBuilder factory = new SAXBuilder();
		// factory.setValidation(true);
		factory.setFeature("http://xml.org/sax/features/validation", true);
		factory.setFeature("http://apache.org/xml/features/validation/schema", true);
		factory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
		factory.setFeature("http://xml.org/sax/features/namespaces", true);

		factory.setValidating(false);
		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		System.out.println("Validating XML ..." + inputFile.getAbsolutePath());
		builder.parse(inputFile);
		// builder.build(inputFile);

		System.out.println("Document is valid & well-formed");
	}

	public static void validateUsingInternalSchema(File inputFile)
			throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

		DocumentBuilder builder = factory.newDocumentBuilder();
		// builder.setErrorHandler(new SimpleErrorHandler());
		builder.parse(inputFile);

	}

	public static void validateUsingExternalSchema(File inputFile, File schema)
			throws SAXException, ParserConfigurationException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		factory.setSchema(schemaFactory.newSchema(schema));

		DocumentBuilder builder = factory.newDocumentBuilder();
		// builder.setErrorHandler(new SimpleErrorHandler());
		builder.parse(inputFile);
	}

	public static void main(String ar[]) {

		try {
			if (ar.length < 1) {
				System.out.println("USAGE: java XGParser inputXMLFileName [XMLschema]");
				System.out.println("NOTE: full path to the file name is required");
			}
			File input = new File(ar[0]);
			if (!(input.exists())) {
				System.out.println("Specified inputXMLFile doesnot exist");
				System.out.println("NOTE: full path to the file name is required");
			}
			File schema = new File(ar[1]);
			if (!(schema.exists())) {
				System.out.println("Specified Schema doesnot exist");
				System.out.println("NOTE: full path to the file name is required");
			}
			XGParser.validateUsingExternalSchema(input, schema);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
