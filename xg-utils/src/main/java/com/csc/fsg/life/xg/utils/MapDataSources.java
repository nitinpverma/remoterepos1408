package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.common.Constants;
import com.csc.fsg.life.xg.dbtable.BaseTableObject;
import com.csc.fsg.life.xg.dbtable.RuleFunction;
import com.csc.fsg.life.xg.exceptions.XGException;
import com.csc.fsg.life.xg.exportimport.ExportImport;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Finds all data sources used/unused in a map or all maps by searching mappings
 * and all expressions.
 */
public class MapDataSources {

	private static final Log log = LogFactory.getLog(MapDataSources.class);
	private static final String FILE_PATH = "filePath";
	private static final String TYPE = "type";
	private EnvironmentInitializer envInit;
	

	public MapDataSources(String envKey) {
		envInit = EnvironmentInitializer.getInstance(envKey);
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * data source entries referenced by the map(s).
	 * 
	 * @param mapsDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void findDataSources(String mapName) throws Exception {

		String mapPath = envInit.getMapsLocation() + mapName;
		log.info("Map : " + mapPath);
		Element root = ExportImport.parseInput(new File(mapPath));
		try {
			findDataSourcesInMap(root);
		} catch (Throwable e) {
			log.error("", e);
		}
		log.info("Scanning is Done.");
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * data sources referenced by the maps.
	 * 
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void findDataSourcesInMaps() throws Exception {

		String mapDir = envInit.getMapsLocation();
		File file = new File(mapDir);
		File mapFiles[] = file.listFiles();

		for (int it = 0; it < mapFiles.length; it++) {

			if (mapFiles[it].isDirectory()) {
				continue;
			}
			String mapName = mapFiles[it].getAbsolutePath();
			log.info((it + 1) + " Map : " + mapName);
			Element root = ExportImport.parseInput(new File(mapName));
			try {
				findDataSourcesInMap(root);
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		log.info("Scanning is Done.");
	}

	/**
	 * Prints all data sources in a Map.
	 * 
	 * @param root the root
	 * @throws Exception the exception
	 */
	private void findDataSourcesInMap(Element root) throws Exception {

		// Get all data sources from the map
		NodeList nl = root.getElementsByTagName("datasource");
		int len = nl.getLength();
		NodeList menl = root.getElementsByTagName("mapElement");
		int meLen = menl.getLength();
		NodeList senl = root.getElementsByTagName("srcElement");
		int seLen = senl.getLength();
		log.debug("  " + len + " Datasources.");

		for (int i = 0; i < len; i++) {

			Element e = (Element) nl.item(i);
			String dType = e.getAttribute(TYPE);
			String filePath = e.getAttribute(FILE_PATH);

			boolean isSchema = dType.equalsIgnoreCase("X");
			boolean dsUsed = false;
			for (int j = 0; j < meLen; j++) {

				Element me = (Element) menl.item(j);
				String targetPath = me.getAttribute("targetPath");
				if (targetPath.contains(filePath)) {
					dsUsed = true;
				}
			}
			for (int k = 0; k < seLen; k++) {
				Element se = (Element) senl.item(k);
				String sourcePath = se.getAttribute("sourcePath");
				if (sourcePath.contains(filePath) || isSchema) {
					dsUsed = true;
				}
			}
			if (dsUsed) {
				log.info("\tDataSource " + (i + 1) + " : " + filePath
						+ (isSchema ? " Schema" : " Copybook"));
			} else {
				log.info("\tDataSource " + (i + 1) + " : " + filePath
						+ (isSchema ? " Schema" : " Copybook") + "(NOT USED)");
				// search rules and functions used in the map
				searchInRF(root, filePath);
			}
		}
		// check if the datasource is used in the map
	}

	private void searchInRF(Element root, String searchStr) throws IOException,
			XGException {

		// Get all expressions from the map and check if they exist
		NodeList nl = root.getElementsByTagName(RuleFunction.ELM_RULE);
		int len = nl.getLength();

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String handlerClass = e.getAttribute(RuleFunction.ATTR_HANDLER_CLASS);
			// log.info("\t\tRule " + (i + 1) + " : " + handlerClass);

			String expFile = envInit.getRuleExpressionsDir() + handlerClass + ".xml";
			// log.info((it + 1) + " Exp : " + expFile);
			boolean usedInFile = Search.isUsedInFile(expFile, searchStr);
			if (usedInFile) {
				log.info("\t\t" + searchStr + " used in " + expFile);
			} else {
				// log.info("\t\t" + searchStr + " NOT used in " + expFile);
				// search deep in all used transformations
				Element expRoot = ExportImport.parseInput(new File(expFile));
				deepSearchInExps(expRoot, searchStr);
			}
		}

		nl = root.getElementsByTagName(RuleFunction.ELM_FUNCTION);
		len = nl.getLength();

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String handlerClass = e.getAttribute(RuleFunction.ATTR_HANDLER_CLASS);
			// log.info("\t\tFunction " + (i + 1) + " : " + handlerClass);

			String expFile = envInit.getRuleExpressionsDir() + handlerClass + ".xml";
			// log.info((it + 1) + " Exp : " + expFile);
			boolean usedInFile = Search.isUsedInFile(expFile, searchStr);
			if (usedInFile) {
				log.info("\t\t" + searchStr + " used in " + expFile);
			} else {
				// log.info("\t\t" + searchStr + " NOT used in " + expFile);
				// search deep in all used transformations
				Element expRoot = ExportImport.parseInput(new File(expFile));
				deepSearchInExps(expRoot, searchStr);
			}
		}
	}

	private void deepSearchInExps(Element expRoot, String searchStr)
			throws IOException, XGException {

		NodeList nl = expRoot.getElementsByTagName(BaseTableObject.ELM_EXPRESSION);
		int len = nl.getLength();

		for (int i = 0; i < len; i++) {
			Element e = (Element) nl.item(i);
			String transfName = e.getAttribute(RuleFunction.ATTR_NAME);
			String transfType = e.getAttribute(RuleFunction.ATTR_TYPE);
			if (Constants.TRANSLATION.equalsIgnoreCase(transfType)
					|| Primitives.isPrimitive(transfName)) {
				continue;
			}
			String expFile = envInit.getTransformationsDir() + transfName + ".trf";
			File file = new File(expFile);
			if (!file.exists()) {
				log.info("\t\t" + transfName + " does not exist");
				continue;
			}

			boolean usedInFile = Search.isUsedInFile(expFile, searchStr);
			if (usedInFile) {
				log.info("\t\t" + searchStr + " used in " + expFile);
			} else {
				// log.info("\t\t" + searchStr + " NOT used in " + expFile);
				// search deep in all used transformations
				Element subExpRoot = ExportImport.parseInput(new File(expFile));
				deepSearchInExps(subExpRoot, searchStr);
			}
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String[] args) {

		try {
			ConfigLoader.initDesignerConfig();
			MapDataSources mapDataSources = new MapDataSources("WMA");
			mapDataSources.findDataSourcesInMaps();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}