package com.csc.fsg.life.xg.utils.testcondgen;

import java.util.*;

import org.apache.commons.logging.*;

import com.csc.fsg.life.common.Constants;
import com.csc.fsg.life.xg.api.XGEngineInput;
import com.csc.fsg.life.xg.dbtable.*;
import com.csc.fsg.life.xg.engine.GatewayInput;
import com.csc.fsg.life.xg.engine.core.*;
import com.csc.fsg.life.xg.engine.utils.*;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.schema.Schema;

/* Modifications: T0127 */

/**
 * Test Conditions Generator.
 */
public class XGTestConditionsGen {

	private static final Log log = LogFactory.getLog(XGTestConditionsGen.class);
	private Map<String, List<String>> testConditions;
	private String envKey;

	/**
	 * The Constructor.
	 * 
	 * @param envmKey the environment key
	 */
	public XGTestConditionsGen(String envmKey) {

		testConditions = new TreeMap<String, List<String>>();
		this.envKey = envmKey;
	}

	/**
	 * Generates test conditions for a map.
	 * 
	 * @param mapName the map name
	 * @throws Exception the exception
	 */
	public void generate(String mapName) throws Exception {

//T0127		XGEngineInput engineInput = new XGEngineInput();
		XGEngineInput engineInput = new XGEngineInput(envKey);//T0127
		engineInput.setMap(mapName);
		GatewayInput input = new GatewayInput(engineInput);

		// First get map meta-data.
		List<MapSourceRecord> mapSources = DatabaseCache.getMapSources(input);

		// Iterate over the source(s) and target(s).
		Schema targetSchema = null;
		for (MapSourceRecord nextDsrc : mapSources) {

			if ("X".equals(nextDsrc.getDsrcType())) {
				targetSchema = new Schema();
				targetSchema.setName(nextDsrc.getDataSource().get(0));
				targetSchema.setRoot(nextDsrc.getSchemaRoot());
				targetSchema.setRoot(nextDsrc.getDataSourceObj().getSystem());
				break;
			}
		}
		// Get the mappings
		MapTreeBuilder mtnBuilder = new MapTreeBuilder(envKey);
		List<Mapping> allMappings = mtnBuilder.buildMappings(mapName, targetSchema);

		// Build the Mapping Tree.
		MapTreeNode rootMTN = mtnBuilder.build(allMappings);

		// Recursively generate TestConditions.
		log.info("Generating TestConditions for '" + mapName + "' ...");
		StringBuffer sb = new StringBuffer();
		sb.append(TCConstants.TEMPLATE).append(Constants.NEXTLINE);
		generate(rootMTN);
		String one = TCWriter.testCondsAsString(testConditions);
		sb.append(one);
		// log.info(sb);
		TCWriter.writeToFile(mapName, sb.toString());
	}

	/**
	 * Generates test conditions for each mapping.
	 * 
	 * @param node the MapTreeNode
	 * @throws Exception the exception
	 */
	private void generate(MapTreeNode node) throws Exception {

		Mapping groupMapping = node.getGroupMapping();
		if (groupMapping != null) {
			String primary = groupMapping.getPrimary();
			String targetNode = groupMapping.getTargetNode();
			if (primary != null) {
				String targetPrimary = replaceString(TCConstants.TRGT_PRIM, "DESTINATION", targetNode, true);
				targetPrimary = replaceString(targetPrimary, "PRIMARY", primary, true);
				addToList(targetNode, TCWriter.getTestCond(targetPrimary, TCConstants.TRGT_PRIM_EXPRSLT));

			}

			RuleFunction filterRule = groupMapping.getTargetRule();
			if ((filterRule != null) && (!filterRule.isDefault())) {
				String targetRule = replaceString(TCConstants.TRGT_FLTR_RULE, "DESTINATION", targetNode, true);
				targetRule = replaceString(targetRule, "RULE", getRuleFunction(filterRule), false);
				addToList(targetNode, TCWriter.getTestCond(targetRule, TCConstants.TRGT_FLTR_RULE_EXPRSLT));
			}
		}

		List<Mapping> mtnMappings = node.getMappings();
		if (mtnMappings.size() > 0) {

			for (Mapping mapping : mtnMappings) {

				RuleFunction rule = mapping.getTargetRule();
				RuleFunction sourceRule = mapping.getSourceRule();
				RuleFunction func = mapping.getTargetFunction();
				RuleFunction sourceFunc = mapping.getSourceFunction();

				String targetNode = mapping.getTargetNode();
				if (mapping.isPath()) {
					String targetsrcMapping = replaceString(TCConstants.TRGT_SRC, "DESTINATION", targetNode, true);
					targetsrcMapping = replaceString(targetsrcMapping, "SOURCE", mapping.getSourceNode(), true);
					addToList(targetNode, TCWriter.getTestCond(targetsrcMapping, TCConstants.TRGT_SRC_EXPRSLT));
				} else {
					String targetConstant = replaceString(TCConstants.TRGT_CONST, "DESTINATION", targetNode, true);
					targetConstant = replaceString(targetConstant, "CONSTANT", mapping.getSourceNode(), true);
					addToList(targetNode, TCWriter.getTestCond(targetConstant, TCConstants.TRGT_CONST_EXPRSLT));
				}

				// Rule 1 is the NO Rule rule, so don't add an attribute for
				// that.
				if (isValidExpr(rule)) {
					String targetRule = replaceString(TCConstants.TRGT_RULE, "DESTINATION", targetNode, true);
					targetRule = replaceString(targetRule, "RULE", getRuleFunction(rule), false);
					addToList(targetNode, TCWriter.getTestCond(targetRule, TCConstants.TRGT_RULE_EXPRSLT));
				}
				if (isValidExpr(sourceRule)) {
					String srcRule = replaceString(TCConstants.SRC_RULE, "SOURCE", mapping.getSourceNode(), true);
					srcRule = replaceString(srcRule, "RULE", getRuleFunction(sourceRule), false);
					addToList(targetNode, TCWriter.getTestCond(srcRule, TCConstants.SRC_RULE_EXPRSLT));
				}
				if (isValidExpr(func)) {
					String targetFn = replaceString(TCConstants.TRGT_FUNC, "DESTINATION", targetNode, true);
					targetFn = replaceString(targetFn, "FUNCTION", getRuleFunction(func), false);
					addToList(targetNode, TCWriter.getTestCond(targetFn, TCConstants.TRGT_FUNC_EXPRSLT));
				}
				if (isValidExpr(sourceFunc)) {
					String srcFn = replaceString(TCConstants.SRC_FUNC, "SOURCE", mapping.getSourceNode(), true);
					srcFn = replaceString(srcFn, "FUNCTION", getRuleFunction(sourceFunc), false);
					addToList(targetNode, TCWriter.getTestCond(srcFn, TCConstants.SRC_FUNC_EXPRSLT));
				}
			}
		}
		for (MapTreeNode nextChild : node.getChildren()) {
			generate(nextChild);
		}
	}

	/**
	 * Checks if the Expression is valid.
	 * 
	 * @param rf the rule or function
	 * @return true, if expression is valid
	 */
	private boolean isValidExpr(RuleFunction rf) {

		if (rf == null) {
			return false;
		}
		if (rf.isDefault()) {
			return false;
		}
		return true;
	}

	/**
	 * Adds the test condition to list.
	 * 
	 * @param targetNode the target node
	 * @param testCondition the test condition
	 */
	private void addToList(String targetNode, String testCondition) {

		if (testConditions.containsKey(targetNode)) {
			List<String> list = testConditions.get(targetNode);
			list.add(testCondition);
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(testCondition);
			testConditions.put(targetNode, list);
		}
	}

	/**
	 * Replace string.
	 * 
	 * @param message the message
	 * @param replaceLiteral the replace literal
	 * @param replaceValue the replace value
	 * @param quotes the quotes
	 * @return the string
	 */
	private String replaceString(String message, String replaceLiteral,
			String replaceValue, boolean quotes) {

		replaceLiteral = "<" + replaceLiteral + ">";
		int literalIndex = message.indexOf(replaceLiteral);
		int endIndex = literalIndex + replaceLiteral.length();
		StringBuffer messageSB = new StringBuffer(message);

		// replaceValue = "\"" + replaceValue + "\"";
		if (quotes) {
			replaceValue = "'" + replaceValue + "'";
		}

		while (literalIndex > -1) {
			messageSB.replace(literalIndex, endIndex, replaceValue);
			message = messageSB.toString();
			literalIndex = message.indexOf(replaceLiteral);
			endIndex = literalIndex + replaceLiteral.length();
		}
		return messageSB.toString();
	}

	/**
	 * Gets the rule function.
	 * 
	 * @param rf the rule or function
	 * @return the rule or function
	 * @throws Exception the exception
	 */
	private String getRuleFunction(RuleFunction rf) throws Exception {

		String ruleFunc = "";
		String expString = rf.getExpression();
		Expression expression = null;
		if ("not used".equals(expString)) {
			ruleFunc = rf.getName();
		} else {
			expression = Expression.parse(expString);
			ruleFunc = expression.toDisplayString(" |--");
		}

		ruleFunc = ruleFunc.replace('"', '\'');

		return ruleFunc;
	}

	public static void main(String argv[]) throws Exception {

		try {
			// log.info("argv.length:"+argv.length);
			if (argv.length < 12) {
				log.info("Usage:java XGTestConditionsGen sys-config-file user pwd map-name priority testplan product tester trxcode testname otherinfo outputdirectory");
				System.exit(1);
			}

			// String configFile = argv[0];
			// String user = argv[1];
			// String password = argv[2];
			String mapName = argv[3];

			String temp = argv[4];
			if (temp != null && !"".equals(temp)) {
				TCConstants.PRIORITY = temp;
			}

			// log.info("Priority:"+temp);
			temp = argv[5];
			if (temp != null && !"".equals(temp)) {
				TCConstants.TESTPLAN = temp;
			}

			// log.info("test plan:"+temp);
			temp = argv[6];
			if (temp != null && !"".equals(temp)) {
				TCConstants.PRODUCT = temp;
			}

			// log.info("Product:"+temp);
			temp = argv[7];
			if (temp != null && !"".equals(temp)) {
				TCConstants.TESTER = temp;
			}

			// log.info("tester:"+temp);
			temp = argv[8];
			if (temp != null && !"".equals(temp)) {
				TCConstants.TRXCODE = temp;
			}

			// log.info("trx code:"+temp);
			temp = argv[9];
			if (temp != null && !"".equals(temp)) {
				TCConstants.TESTNAME = temp;
			}

			// log.info("test name:"+temp);
			temp = argv[10];
			if (temp != null && !"".equals(temp)) {
				TCConstants.OTHERINFO = temp;
			}

			// log.info("other info:"+temp);
			temp = argv[11];
			// log.info("out dir:"+temp);
			if (temp != null && !"".equals(temp)) {
				TCConstants.OUTPUT_DIR = temp;
			}

			EnvironmentInitializer.init(XGTestConditionsGen.class.getClassLoader());
			// DatabaseCache.setUser(user, password);

			XGTestConditionsGen tool = new XGTestConditionsGen("DB2");
			long startTime = System.currentTimeMillis();
			tool.generate(mapName);
			long endTime = System.currentTimeMillis();
			log.info("Export Time: " + ((endTime - startTime) / 1000.0));

		} catch (Exception e) {
			log.error("", e);
		} finally {
			System.exit(0);
		}
	}
}
