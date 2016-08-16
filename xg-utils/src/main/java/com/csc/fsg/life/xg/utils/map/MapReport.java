package com.csc.fsg.life.xg.utils.map;

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.xg.dbtable.RuleFunction;
import com.csc.fsg.life.xg.exportimport.ExportImport;

/**
 * Prints all rules and functions used by a Map(s).
 */
public class MapReport {

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * XML entries for all rules and functions referenced by the map. One sub
	 * directory will be scanned.
	 *
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void checkEntriesForMap(String mapDir) throws Exception {

		File file = new File(mapDir);
		if (file.isDirectory()) {
			checkEntriesForMaps(mapDir);
			return;
		}

		String mapName = file.getAbsolutePath();
		System.out.println("Map change 1 : " + mapName);
		Element root = ExportImport.parseInput(new File(mapName));
		try {
			checkExpressions(root);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("Scanning is Done.");
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * XML entries for all rules and functions referenced by the map. Sub
	 * directories will be skipped.
	 *
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void checkEntriesForMaps(String mapDir) throws Exception {

		File file = new File(mapDir);
		File mapFiles[] = file.listFiles();

		for (int i = 0; i < mapFiles.length; i++) {

			if (mapFiles[i].isDirectory()) {
				continue;
			}
			String mapName = mapFiles[i].getAbsolutePath();
			System.out.println((i + 1) + " Map : " + mapName);
			Element root = ExportImport.parseInput(new File(mapName));
			try {
				checkExpressions(root);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		System.out.println("Scanning is Done.");
	}

	/**
	 * Checks all expressions in a Map.
	 *
	 * @param root the root
	 * @throws Exception the exception
	 */
	private void checkExpressions(Element root) throws Exception {

		// Get all expressions from the map and check if they exist
		NodeList nl = root.getElementsByTagName(RuleFunction.ELM_RULE);
		int len = nl.getLength();
		System.out.println("  " + len + " Rule(s).");

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String handlerClass = e.getAttribute(RuleFunction.ATTR_HANDLER_CLASS);
			System.out.println("\tRule " + (i + 1) + " : " + handlerClass);
		}
		nl = root.getElementsByTagName(RuleFunction.ELM_FUNCTION);
		len = nl.getLength();
		System.out.println("  " + len + " Function(s).");
		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String handlerClass = e.getAttribute(RuleFunction.ATTR_HANDLER_CLASS);
			System.out.println("\tFunction " + (i + 1) + " : " + handlerClass);
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the args
	 */
	public static void main(String[] args) {

		try {
			MapReport mapReport = new MapReport();
			mapReport.checkEntriesForMaps("D:/XMLG/Test/MAP");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}