package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.api.IXGContext;
import com.csc.fsg.life.xg.api.XGEngineInput;
import com.csc.fsg.life.xg.dbtable.Expression;
import com.csc.fsg.life.xg.dbtable.Primitive;
import com.csc.fsg.life.xg.dbtable.RuleFunction;
import com.csc.fsg.life.xg.dbtable.TranslationModel;
import com.csc.fsg.life.xg.engine.GatewayInput;
import com.csc.fsg.life.xg.engine.core.MapElementHandler2;
import com.csc.fsg.life.xg.engine.core.SelectorHandler;
import com.csc.fsg.life.xg.engine.core.XGContext;
import com.csc.fsg.life.xg.exceptions.XGException;
import com.csc.fsg.life.xg.exportimport.ExportImport;
import com.csc.fsg.life.xg.serverutils.ClassRebuilder;
import com.csc.fsg.life.xg.serverutils.FunctionsUtil;
import com.csc.fsg.life.xg.serverutils.RulesUtil;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.servlet.common.DynamicClassCreator;
import com.csc.fsg.life.xg.treemodels.schema.XMLTreeModel;
import com.csc.fsg.life.xg.treenodes.schema.XMLNode;

/* Modifications: T0127 */

/**
 * The Class MapEntriesUtil provides utilities to validate rules, functions,
 * translations, transformations and Maps.
 */
public class MapEntriesUtil {

	private static final Log log = LogFactory.getLog(MapEntriesUtil.class);
	private static DocumentBuilder docBuild;
	private String envKey;
	private EnvironmentInitializer envInit;

	static {
		ConfigLoader.initRuntimeConfig();
		try {
			// Establish an xml document builder.
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("Unable to build document handler: ", e);
		}
	}

	/**
	 * Creates a new instance of MapEntriesUtil.
	 * 
	 * @param envKey the env key
	 * @throws XGException Thrown when the instantiation fails.
	 */
/*T0127
	public MapEntriesUtil() throws XGException {
		envInit = EnvironmentInitializer.getMapperEnv();
		this.envKey = envInit.getEnvKey();
	}
*/	
	public MapEntriesUtil(String envKey) throws XGException {
		this.envKey = envKey;
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * xml entries for all rules, functions,transformations, and translations
	 * referenced by the map.
	 * 
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void checkMapEntries() throws Exception {

		File file = new File(envInit.getMapsLocation());
		if (file.isDirectory()) {
			File mapFiles[] = file.listFiles();

			for (int it = 0; it < mapFiles.length; it++) {
				// Ignore all sub directories
				if (mapFiles[it].isDirectory()) {
					continue;
				}
				String mapName = mapFiles[it].getAbsolutePath();
				log.info(it + " Map : " + mapName);
				checkMapEntries(mapName);
			}
			log.info("Scanning is Done.");
			return;
		} 
		log.info(file.getAbsoluteFile() + " is not a directory");
		log.info("Scanning is Done.");
	}
	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * xml entries for all rules, functions,transformations, and translations
	 * referenced by the map.
	 * 
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void checkMapEntries(String mapName) throws XGException {
		Element root = ExportImport.parseInput(new File(mapName));
		try {
			checkExpressions(root);
		} catch (Throwable e) {
			log.error("", e);
		}
	}

	/**
	 * Checks all expressions in a Map.
	 * 
	 * @param root the root
	 * @throws Exception the exception
	 */
	private void checkExpressions(Element root) throws Exception {

		// Get all expressions from the map and check if they exist
		NodeList nl = root.getElementsByTagName("rule");
		int len = nl.getLength();
		log.info("  " + len + " Rules.");

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String label = e.getAttribute("label");
			checkExpression(label);
			log.info("\tRule " + i + " : " + label + " initialized");
		}
		nl = root.getElementsByTagName("function");
		len = nl.getLength();
		log.info("  " + len + " Functions.");
		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String label = e.getAttribute("label");
			checkExpression(label);
			log.info("\tFunction " + i + " : " + label + " initialized");
		}
	}

	/**
	 * Validates all translations.
	 * 
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	public void validateTranslations() throws SQLException, Exception {

		FunctionsUtil instance = FunctionsUtil.getInstance(envKey);
		Map<String, List<String>> translations = instance.getAllVariations();

		if (translations.size() == 0) {
			log.info("No Translations found.");
			return;
		}
		log.info(" Total Translations : " + translations.size());
		Set<Entry<String, List<String>>> entrySet = translations.entrySet();

		for (Entry<String, List<String>> entry : entrySet) {
			String funcName = entry.getKey();
			log.info("Function : " + funcName);
			Primitive translation = instance.getTranslation(funcName);
			TranslationModel translationModel = instance.getTranslationExpression(translation);
			List<String> variations = entry.getValue();
			for (String varName : variations) {
				try {
					// Variation variation =
					// translationModel.getVariation(varName);
					if (!DynamicClassCreator.classExists(varName, envKey)) {
						// DynamicClassCreator.createVariationClass(variation,
						// funcName, envKey);
						ClassRebuilder.dynamicallyCreateClass(varName, envKey);

					}
				} catch (Throwable e) {
					log.error("", e);
				}
			}
		}
	}

	/**
	 * Validates all transformations.
	 * 
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	public void validateTransformations() throws SQLException, Exception {

		FunctionsUtil instance = FunctionsUtil.getInstance(envKey);
		// List<String> transformations = instance.getTransformationHCNames();
		List<String> transformations = instance.getTransformationNames();

		if (transformations.size() == 0) {
			log.info("No Transformations found.");
			return;
		}
		log.info(" Total Transformations : " + transformations.size());
		for (String funcName : transformations) {

			// log.info("Function : " + funcName);
			try {
				if (!DynamicClassCreator.classExists(funcName, envKey)) {
					ClassRebuilder.dynamicallyCreateClass(funcName, envKey);
				}
			} catch (XGException e) {
				log.error("", e);
			}
		}
	}

	/**
	 * Validates a transformation.
	 * 
	 * @param funcName the func name
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	public void checkTransformation(String funcName) throws SQLException,
			Exception {

		// log.info("Function : " + funcName);
		try {
			if (!DynamicClassCreator.classExists(funcName, envKey)) {
				ClassRebuilder.dynamicallyCreateClass(funcName, envKey);
			}
		} catch (XGException e) {
			log.error("", e);
		}
	}

	/**
	 * Validates all expressions in the system.
	 * 
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	public void validateExpressions() throws SQLException, Exception {

		RulesUtil instance = RulesUtil.getInstance(envKey);
		List<RuleFunction> rulesAndFunctions = instance.getAllRulesAndFunctions(true);

		if (rulesAndFunctions.size() == 0) {
			log.info("No Rules and functions found.");
			return;
		}
		log.info(" Total rules/functions : " + rulesAndFunctions.size());
		for (RuleFunction ruleFunction : rulesAndFunctions) {
			// log.info(ruleFunction.getName());
			try {
				Expression expr = Expression.parse(ruleFunction.getExpression());
				log.info(expr);
				IXGContext context = getContext();
				evaluate(context, ruleFunction);
			} catch (Throwable e) {
				log.error("", e);
				log.error(ruleFunction.getExpression());
			}

		}
	}

	/**
	 * Checks the validity of an expression by instantiating the it's .class.
	 * 
	 * @param ruleName the rule name
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	public void checkExpression(String ruleName) {

		// Read the specified row.
		try {
			RulesUtil instance = RulesUtil.getInstance(envKey);
			RuleFunction ruleFunction = instance.getFullRuleFunction(ruleName);
			if (ruleFunction == null) {
				log.info("\t*** Could not find entry for rule : " + ruleName);
				return;
			}

			IXGContext context = getContext();
			evaluate(context, ruleFunction);
		} catch (Exception e) {
			//log.error("\t" + e.getMessage());
			log.error("\t", e);
		}
	}

	/**
	 * Evaluates an expression by instantiating the class file if class file
	 * exists. Creates class file if not exists.
	 * 
	 * @param context the context
	 * @param rule the rule
	 * @throws XGException the XG exception
	 */
	public void evaluate(IXGContext context, RuleFunction rule)
			throws XGException {

		// create instance of handler class and call handle method
		try {
			Object object = instantiate(rule);
			// if (rule.getType().equalsIgnoreCase("R")) {
			// ((IXGRule) object).evaluate(context);
			// } else {
			// ((IXGFunction) object).evaluate(context);
			// }
		} catch (Throwable ex) {
			if ((ex instanceof ClassNotFoundException)
					|| (ex instanceof NoClassDefFoundError)) {
				// If handler class does not exist, try to recreate it
				// from its XML expression
				try {
					boolean b = ClassRebuilder.dynamicallyCreateClass(rule, envKey);
					if (!b)
						throw new Exception("Expression not found on DB : "
								+ rule);

				} catch (Throwable e1) {
					//log.error("\t" + e1.getMessage());
					log.error("\t", e1);
					return;
				}
				try {
					Object object = instantiate(rule);
					// if (rule.getType().equalsIgnoreCase("R")) {
					// ((IXGRule) object).evaluate(context);
					// } else {
					// ((IXGFunction) object).evaluate(context);
					// }

				} catch (Throwable e) {
					throw new XGException("Error creating rule class: " + rule
							+ " " + e.getClass().getName() + " "
							+ e.getMessage());
				}
			} else {
				log.error("Error creating rule class: " + rule + " "
						+ ex.getClass().getName(), ex);
			}
		}

		// Evaluate the rule
		// log.info(" intialized.");
	}

	/**
	 * Instantiates the Rule or Function.
	 * 
	 * @param rule the rule
	 * @return the IXG rule
	 * @throws Throwable the throwable
	 */
	private Object instantiate(RuleFunction rule) throws Throwable {

		// Get the rule class.
		URLClassLoader xgClassLoader = EnvironmentInitializer.getInstance(envKey).getXgClassLoader();
		Class<?> clz = Class.forName(rule.getHandlerClassName(), false, xgClassLoader);

		// Create an instance.
		return clz.newInstance();
	}

	/**
	 * Gets the a dummy context.
	 * 
	 * @return the context
	 * @throws FactoryConfigurationError the factory configuration error
	 * @throws IOException the IO exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private IXGContext getContext() throws ParserConfigurationException,
			FactoryConfigurationError, SAXException, IOException {

//T0127		XGEngineInput input = new XGEngineInput();
		XGEngineInput input = new XGEngineInput(envKey); //T0127
		Document doc = docBuild.newDocument();
		Element root1 = doc.createElement("TXLife");
		doc.appendChild(root1);
		input.setInput(doc);
		MapElementHandler2 maphandler = new MapElementHandler2(new GatewayInput(input), null, null);

		XMLNode xmlRootNode = new XMLNode(doc.getDocumentElement());
		XMLTreeModel docModel = new XMLTreeModel(xmlRootNode);
		TreeNode root = (TreeNode) ((TreeModel) docModel).getRoot();
		ArrayList<TreeNode> prim = new ArrayList<TreeNode>();
		prim.add(root);

//T0127		XGContext context = new XGContext(new SelectorHandler(maphandler, null));
		XGContext context = new XGContext(new SelectorHandler(maphandler, null), envKey);//T0127
		context.reuse(null, null, prim, root, null);
		return context;
	}

	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String[] args) {

		try {
			MapEntriesUtil mapEntriesUtil = new MapEntriesUtil(args[0]);//T0127
			// mapEntriesUtil.checkMapEntries();
			 mapEntriesUtil.checkMapEntries("D:/XMLG/WMA/MAP/AccordSuccess.xml");
			// mapEntriesUtil.checkExpression("GetFundValByFundNum");
			// mapEntriesUtil.validateExpressions();
			// mapEntriesUtil.validateTranslations();
			// mapEntriesUtil.validateTransformations();
			// mapEntriesUtil.validateXMLG();
			// mapEntriesUtil.checkTransformation("GetPartyByReinsurance");
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * Validate XMLG work directory.
	 */
	public void validateXMLG() {
		try {
			validateTransformations();
			validateTranslations();
			validateExpressions();
			checkMapEntries();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}