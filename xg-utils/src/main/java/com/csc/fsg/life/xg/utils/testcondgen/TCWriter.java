package com.csc.fsg.life.xg.utils.testcondgen;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.csc.fsg.life.common.Constants;

/**
 * Utility to write Test Conditions to a file.
 * 
 * @author mchintal
 */
public class TCWriter {

	/**
	 * Gets the test condition.
	 * 
	 * @param expRslt the exp rslt
	 * @param testCond the test cond
	 * 
	 * @return the test cond
	 */
	public static String getTestCond(String testCond, String expRslt) {

		StringBuffer sb = new StringBuffer();
		sb.append(TCConstants.PRIORITY).append(Constants.TAB);
		sb.append(TCConstants.TESTPLAN).append(Constants.TAB);
		sb.append(TCConstants.PRODUCT).append(Constants.TAB);
		sb.append(TCConstants.TESTER).append(Constants.TAB);
		sb.append(TCConstants.TRXCODE).append(Constants.TAB);
		sb.append(TCConstants.TESTNAME).append(Constants.TAB);
		sb.append(Constants.QUOTES).append(testCond);
		sb.append(Constants.QUOTES).append(Constants.TAB);
		// sb.append(TCConstants.OTHERINFO).append(TCConstants.TAB);
		sb.append(expRslt).append(Constants.NEXTLINE);
		return sb.toString();
	}

	/**
	 * Writes the test conditions to file.
	 * 
	 * @param testConditions the test conditions
	 * @param mapName the map name
	 * 
	 * @throws Exception the exception
	 */
	public static void writeToFile(String mapName, String testConditions)
			throws Exception {
		BufferedWriter out = null;
		try {

			File outputDir = new File(TCConstants.OUTPUT_DIR);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			String fileName = mapName + ".xls";
			File file = new File(outputDir, fileName);
			FileWriter fw = new FileWriter(file);
			out = new BufferedWriter(fw);
			out.write(testConditions);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Gives Test conditions as string.
	 * 
	 * @param testConditions the test conditions
	 * 
	 * @return the string
	 */
	public static String testCondsAsString(
			Map<String, List<String>> testConditions) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, List<String>>> entrySet = testConditions.entrySet();
		for (Entry<String, List<String>> entry : entrySet) {
			List<String> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}
}
