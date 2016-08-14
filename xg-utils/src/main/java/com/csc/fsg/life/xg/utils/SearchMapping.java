package com.csc.fsg.life.xg.utils;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.xg.exportimport.ExportImport;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Finds all mappings for a field.
 */
public class SearchMapping {

	private static final Log log = LogFactory.getLog(SearchMapping.class);
	private String envKey;

	public SearchMapping(String envKey) {
		this.envKey = envKey;
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * field mappings referenced by the map(s).
	 *
	 * @param mapsDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void findMappings(String field) throws Exception {

		String mapsDir = EnvironmentInitializer.getInstance(envKey).getMapsLocation();
		File file = new File(mapsDir);
		if (file.isDirectory()) {
			findMappingsInMaps(mapsDir, field);
			return;
		}

		log.error("Not a valid maps directory");
		log.info("Scanning is Done.");
	}

	/**
	 * Gets the list of maps from the XML Gateway Directory and checks for the
	 * field mappings referenced by the maps.
	 *
	 * @param mapDir Directory List of map names to be checked.
	 * @throws Exception the exception
	 */
	public void findMappingsInMaps(String mapDir, String field)
			throws Exception {

		File file = new File(mapDir);
		File mapFiles[] = file.listFiles();

		for (int it = 0; it < mapFiles.length; it++) {

			if (mapFiles[it].isDirectory()) {
				continue;
			}
			String mapName = mapFiles[it].getAbsolutePath();
			// log.info(it + " Map : " + mapName);
			log.info(" Map : " + mapName);
			Element root = ExportImport.parseInput(new File(mapName));
			try {
				findMappingsInMap(root, field);
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		log.info("Scanning is Done.");
	}

	/**
	 * Prints all field mappings in a Map.
	 *
	 * @param root the root
	 * @param field
	 * @throws Exception the exception
	 */
	private void findMappingsInMap(Element root, String field) throws Exception {

		// Get all mappings from the map
		NodeList nl = root.getElementsByTagName("mapElement");
		int len = nl.getLength();
		log.debug("  " + len + " mappings.");

		for (int i = 0; i < len; i++) {

			Element e = (Element) nl.item(i);
			String targetPath = e.getAttribute("targetPath");
			if (targetPath.contains(field)) {
				NodeList childNodes = e.getElementsByTagName("srcElement");
				for (int j = 0; j < childNodes.getLength(); j++) {
					Element child = (Element) childNodes.item(j);
					String sourcePath = child.getAttribute("sourcePath");
					log.info("\t" + targetPath + " : " + sourcePath);
				}
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
			String field = "COMPANY";
			SearchMapping mapDataSources = new SearchMapping("WMA");
			mapDataSources.findMappings(field);
		} catch (Exception e) {
			log.error("", e);
		}
	}
}