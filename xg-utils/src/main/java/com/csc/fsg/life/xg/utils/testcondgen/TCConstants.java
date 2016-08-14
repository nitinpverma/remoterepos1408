package com.csc.fsg.life.xg.utils.testcondgen;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.*;

/**
 * The Class Test Conditions generator Constants.
 */
public final class TCConstants {

	private static final Log log = LogFactory.getLog(TCConstants.class);
	private static Properties props = new Properties();

	static {

		try {
			InputStream ls = TCConstants.class.getResourceAsStream("TestConditions.properties");
			if (ls == null) {
				throw new IllegalArgumentException(
						"testDescriptions.properties not found.");
			}
			props.load(ls);
		} catch (IOException e) {
			log.error("", e);
		}

	}

	/** Descripion for the Target which has source element mapping. */
	public static final String TRGT_SRC = props.getProperty("TRGT_SRC", "");

	/**
	 * Mapping result description for the Target which has source element
	 * mapping.
	 */
	public static final String TRGT_SRC_EXPRSLT = props.getProperty("TRGT_SRC_EXPRSLT", "");

	/** Description for the Target which has constant value mapping. */
	public static final String TRGT_CONST = props.getProperty("TRGT_CONST", "");

	/**
	 * Mapping result description for the Target which has Constant value
	 * mapping.
	 */
	public static final String TRGT_CONST_EXPRSLT = props.getProperty("TRGT_CONST_EXPRSLT", "");

	/** Description for the Target which has function applied on it. */
	public static final String TRGT_FUNC = props.getProperty("TRGT_FUNC", "");

	/**
	 * Mapping result description for the Target which has function applied on
	 * it.
	 */
	public static final String TRGT_FUNC_EXPRSLT = props.getProperty("TRGT_FUNC_EXPRSLT", "");

	/** Description for the Source element which has function applied on it. */
	public static final String SRC_FUNC = props.getProperty("SRC_FUNC", "");

	/**
	 * Mapping result description for the Source element which has function
	 * applied on it.
	 */
	public static final String SRC_FUNC_EXPRSLT = props.getProperty("SRC_FUNC_EXPRSLT", "");

	/** Description for the Target which has rule applied on it. */
	public static final String TRGT_RULE = props.getProperty("TRGT_RULE", "");

	/**
	 * Mapping result description for the Target element which has rule applied
	 * on it.
	 */
	public static final String TRGT_RULE_EXPRSLT = props.getProperty("TRGT_RULE_EXPRSLT", "");

	/** Description for the Source element which has rule applied on it. */
	public static final String SRC_RULE = props.getProperty("SRC_RULE", "");

	/**
	 * Mapping result description for the Source element which has rule applied
	 * on it.
	 */
	public static final String SRC_RULE_EXPRSLT = props.getProperty("SRC_RULE_EXPRSLT", "");

	/** Description for the Constant value which has rule applied on it. */
	public static final String CONST_RULE = props.getProperty("CONST_RULE", "");

	/**
	 * Mapping result description for the Constant value which has rule applied
	 * on it.
	 */
	public static final String CONST_RULE_EXPRSLT = props.getProperty("CONST_RULE_EXPRSLT", "");

	/** Description for the Copybook element which has rule applied on it. */
	public static final String CPB_RULE = props.getProperty("CPB_RULE", "");

	/**
	 * Mapping result description for the Copybook element which has rule
	 * applied on it.
	 */
	public static final String CPB_RULE_EXPRSLT = props.getProperty("CPB_RULE_EXPRSLT", "");

	/**
	 * Description for the Target group element which has filter rule applied on
	 * it.
	 */
	public static final String TRGT_FLTR_RULE = props.getProperty("TRGT_FLTR_RULE", "");

	/**
	 * Mapping result description for the target element which has filter rule
	 * applied on it.
	 */
	public static final String TRGT_FLTR_RULE_EXPRSLT = props.getProperty("TRGT_FLTR_RULE_EXPRSLT", "");

	/** Description for the target element which has primary applied on it. */
	public static final String TRGT_PRIM = props.getProperty("TRGT_PRIM", "");

	/**
	 * Mapping result description for the target element which has primary
	 * applied on it.
	 */
	public static final String TRGT_PRIM_EXPRSLT = props.getProperty("TRGT_PRIM_EXPRSLT", "");

	/** The header columns template . */
	public static String TEMPLATE = props.getProperty("TEMPLATE", "");

	// public static String PRIORITY = props.getProperty("PRIORITY", "H");
	// public static String TESTPLAN = props.getProperty("testPlan", "");
	// public static String PRODUCT = props.getProperty("product", "");
	// public static String TESTER = props.getProperty("tester", "");
	// public static String TRXCODE = props.getProperty("trxCode", "");
	// public static String TESTNAME = props.getProperty("testName", "");
	// public static String OTHERINFO = props.getProperty("otherInfo", "");
	// public static String OUTPUT_DIR = props.getProperty("outputdir", "");

	/** The PRIORITY. */
	static String PRIORITY = "H";

	/** The TESTPLAN. */
	static String TESTPLAN = "";

	/** The PRODUCT. */
	static String PRODUCT = "";

	/** The TESTER. */
	static String TESTER = "";

	/** The TRXCODE. */
	static String TRXCODE = "";

	/** The TESTNAME. */
	static String TESTNAME = "";

	/** The OTHERINFO. */
	static String OTHERINFO = "";

	/** The OUTPU t_ DIR. */
	static String OUTPUT_DIR = "";

	public static void main(String[] args) {

		log.info("TRGT_SRC:[" + TRGT_SRC + "]");
		log.info("TRGT_SRC_EXPRSLT:[" + TRGT_SRC_EXPRSLT + "]");

		log.info("TRGT_CONST:[" + TRGT_CONST + "]");
		log.info("TRGT_CONST_EXPRSLT:[" + TRGT_CONST_EXPRSLT + "]");

		log.info("TRGT_FUNC:[" + TRGT_FUNC + "]");
		log.info("TRGT_FUNC_EXPRSLT:[" + TRGT_FUNC_EXPRSLT + "]");

		log.info("SRC_FUNC:[" + SRC_FUNC + "]");
		log.info("SRC_FUNC_EXPRSLT:[" + SRC_FUNC_EXPRSLT + "]");

		log.info("TRGT_RULE:[" + TRGT_RULE + "]");
		log.info("TRGT_RULE_EXPRSLT:[" + TRGT_RULE_EXPRSLT + "]");

		log.info("SRC_RULE:[" + SRC_RULE + "]");
		log.info("SRC_RULE_EXPRSLT:[" + SRC_RULE_EXPRSLT + "]");

		log.info("CONST_RULE:[" + CONST_RULE + "]");
		log.info("CONST_RULE_EXPRSLT:[" + CONST_RULE_EXPRSLT + "]");

		log.info("CPB_RULE:[" + CPB_RULE + "]");
		log.info("CPB_RULE_EXPRSLT:[" + CPB_RULE_EXPRSLT + "]");

		log.info("TRGT_FLTR_RULE:[" + TRGT_FLTR_RULE + "]");
		log.info("TRGT_FLTR_RULE_EXPRSLT:[" + TRGT_FLTR_RULE_EXPRSLT + "]");

		log.info("TRGT_PRIM:[" + TRGT_PRIM + "]");
		log.info("TRGT_PRIM_EXPRSLT:[" + TRGT_PRIM_EXPRSLT + "]");

		log.info("PRIORITY:[" + PRIORITY + "]");
		log.info("TESTPLAN:[" + TESTPLAN + "]");
		log.info("PRIORITY:[" + PRIORITY + "]");
		log.info("TESTER:[" + TESTER + "]");
		log.info("TRXCODE:[" + TRXCODE + "]");
		log.info("TESTNAME:[" + TESTNAME + "]");
		log.info("OTHERINFO:[" + OTHERINFO + "]");
		log.info("OUTPUT DIR :[" + OUTPUT_DIR + "]");
		log.info("TEMPLATE:[" + TEMPLATE + "]");
	}
}
