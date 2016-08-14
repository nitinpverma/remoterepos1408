package com.csc.fsg.life.xg.utils.xml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSTypeDefinition;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.exceptions.XGException;
import com.csc.fsg.life.xg.managers.XercesClassLoader;
import com.csc.fsg.life.xg.schema.parser.BaseTypeNodeBuilder;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.XGTreeModel;
import com.csc.fsg.life.xg.treemodels.schema.Schema;
import com.csc.fsg.life.xg.treemodels.schema.SchemaTreeModel;
import com.csc.fsg.life.xg.treenodes.schema.RootTypeNode;
import com.csc.fsg.life.xg.treenodes.schema.ValueMutableTreeNode;

/**
 * Finds the duplicate definitions in schema files. 
 */
public class ExistingElementsInSchema {

	private static final Log log = LogFactory.getLog(ExistingElementsInSchema.class);

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
	 * Prints all unused maps excluding OM maps.
	 * 
	 * @param servicesConfig the services config
	 * @param mapListFile the map list file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private static void find(String servicesConfig, String mapListFile)
			throws IOException, SAXException {

		// parse new schema and collect the objects
		// parse extension file and collect the objects
		// search for each extension element in new schema just in the parent of
		// OLIFE EXTENION

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
			System.out.println("Usage: <New schema file> <Extensions file>");
			return;
		}
		String servicesConfig = args[0];
		String mapListFile = args[1];
		find(servicesConfig, mapListFile);
		return;
	}

	/**
	 * Serialize.
	 * 
	 * @param env the env
	 * @param schema the schema
	 * @throws Exception the exception
	 */
	public void serialize(EnvironmentInitializer env, Schema schema)
			throws Exception {

		String schemaFile = schema.getName();
		if (schemaFile.indexOf(env.getSchemasDir()) == -1) {
			schemaFile = env.getSchemasDir() + schema.getSystem()
					+ File.separator + schema.getName();
		}
		String selectedRoot = schema.getRoot();

		try {
			XercesClassLoader cl = getClassLoader();
			Thread th = Thread.currentThread();
			ClassLoader thcl = th.getContextClassLoader();
			try {
				// Set the context class loader to Xerces Class Loader
				// to load Xerces Factory classes.
				th.setContextClassLoader(cl);

				// Load the schema into memory.
				XSModel xsModel = loadSchemaGrammar(schemaFile);

				// Get the root elements from the model.
				List<String> roots = getRootElements(xsModel);

				// Only build the model if the root is non-null.
				SchemaTreeModel schemaModel = null;
				if (selectedRoot != null) {
					// Build a model from the schema.
					schemaModel = (SchemaTreeModel) buildRoot(schemaFile, selectedRoot, xsModel);
					schemaModel.setSchemaURI(schemaFile);
					Schema s = new Schema();
					s.setName(schemaFile);
					s.setRoot(selectedRoot);
					s.setSystem(schema.getSystem());
					schemaModel.setUserObject(s);

					// Build the tree model from the schema.
					ValueMutableTreeNode rootNode = (ValueMutableTreeNode) schemaModel.getRoot();
					rootNode.setModel(schemaModel);
				}

			} finally {
				// Be sure to re-set the class loader for this thread.
				th.setContextClassLoader(thcl);
			}
		} catch (Exception e) {
			log.error("Failed to build/serialize schema tree" + schema, e);
			throw new XGException("Failed to build/serialize schema tree: "
					+ e.getMessage());
		}
	}

	/**
	 * @param serverDocRoot
	 * @return
	 * @throws MalformedURLException
	 */
	private XercesClassLoader getClassLoader() throws MalformedURLException {

		// Get the lib directory.
		String serverLibDir = EnvironmentInitializer.getServerDocRoot()
				+ "WEB-INF" + File.separator + "lib" + File.separator;

		// Build the classpath
		String jars[] = { "xercesImpl.jar", "xml-apis.jar" };

		URL urls[] = new URL[jars.length];
		for (int i = 0; i < jars.length; i++) {
			String url = serverLibDir + jars[i];
			// File temp = new File(url);
			// if( !temp.exists() )
			// throw new
			// MalformedURLException("Unable to load xercesImpl.jar and xml-apis.jar");
			// urls[i] = temp.toURL();
			if (url.startsWith(File.separator))
				urls[i] = new URL("file:/" + url);
			else
				urls[i] = new URL("file:" + url);
		}
		log.info("Schema Class Path:" + Arrays.asList(urls));
		// Build the class loader.
		XercesClassLoader cl = new XercesClassLoader(urls, null);
		return cl;
	}

	private List<String> getRootElements(XSModel xsModel) {

		List<String> rootElementsAL = new ArrayList<String>();
		XSNamedMap rootElements = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
		int n = rootElements.getLength();
		for (int i = 0; i < n; i++) {
			XSElementDeclaration xsRootDecl = (XSElementDeclaration) rootElements.item(i);
			XSTypeDefinition xtd = xsRootDecl.getTypeDefinition();

			/*
			 * XSTypeDefinition.COMPLEX_TYPE 13 XSTypeDefinition.SIMPLE_TYPE 14
			 */

			if (xtd.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
				XSComplexTypeDefinition complexDecl = (XSComplexTypeDefinition) xtd;

				/*
				 * XSComplexTypeDefinition.CONTENTTYPE_ELEMENT 2
				 * XSComplexTypeDefinition.CONTENTTYPE_EMPTY 0
				 * XSComplexTypeDefinition.CONTENTTYPE_MIXED 3
				 * XSComplexTypeDefinition.CONTENTTYPE_SIMPLE 1
				 */

				if (complexDecl.getContentType() != 1) {
					rootElementsAL.add(xsRootDecl.getName().trim());
				}
			}
		}

		return rootElementsAL;
	}

	private XGTreeModel buildRoot(String schemaFile, String selectedRoot,
			XSModel xsModel) throws Exception {

		XSElementDeclaration selectedRootElemDecl = getXSElementDeclaration(xsModel, selectedRoot);

		XGTreeModel template = getTreeModel(selectedRootElemDecl);
		return template;
	}

	private SchemaTreeModel getTreeModel(XSElementDeclaration xsRootDecl)
			throws Exception {

		BaseTypeNodeBuilder builder = new BaseTypeNodeBuilder();
		RootTypeNode rootTypeNode = builder.buildRoot(xsRootDecl);
		ValueMutableTreeNode rootValueNode = rootTypeNode.getValueNode(null);
		SchemaTreeModel schemaTreeModel = new SchemaTreeModel(rootValueNode);
		return schemaTreeModel;
	}

	private XSElementDeclaration getXSElementDeclaration(XSModel xsModel,
			String elementName) {

		XSNamedMap rootElements = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
		int n = rootElements.getLength();
		for (int i = 0; i < n; i++) {
			XSElementDeclaration elemDecl = (XSElementDeclaration) rootElements.item(i);
			if (elemDecl.getName().equals(elementName)) {
				return elemDecl;
			}
		}

		return null;
	}

	private XSModel loadSchemaGrammar(String schemaFile) throws XGException {

		try {
			/**
			 * CAC: Don't use the registry b/c it uses the context class loader
			 * to load. System.setProperty( DOMImplementationRegistry.PROPERTY,
			 * "org.apache.xerces.dom.DOMXSImplementationSourceImpl");
			 * DOMImplementationRegistry registry =
			 * DOMImplementationRegistry.newInstance(); XSImplementation impl =
			 * (XSImplementation) registry.getDOMImplementation("XS-Loader");
			 */

			DOMXSImplementationSourceImpl xercesImpl = new DOMXSImplementationSourceImpl();
			XSImplementation impl = (XSImplementation) xercesImpl.getDOMImplementation("XS-Loader");

			XSLoader schemaLoader = impl.createXSLoader(null);

			XSModel model = schemaLoader.loadURI(schemaFile);
			return model;
		} catch (Exception e) {
			log.error("Exception while loading Schema Grammer" + schemaFile, e);
			throw new XGException(e.getMessage());
		} catch (LinkageError linkError) {
			log.error("Error while loading the Schema Grammer" + schemaFile, linkError);
			// locateFiles();
			throw new XGException(linkError.getMessage());
		}
	}

}
