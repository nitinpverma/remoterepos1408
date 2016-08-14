package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.serverutils.MapsUtil;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Finds all unused maps.
 */
public class OrphanMaps {

	private static final Log log = LogFactory.getLog(OrphanMaps.class);
	private String envKey;

	public OrphanMaps(String envKey) {
		this.envKey = envKey;
	}

	/**
	 * Searches for Orphan maps.
	 *
	 * @throws Exception
	 */

	public void orphanMaps() throws Exception {

		orphanMaps(new File(EnvironmentInitializer.getInstance(envKey).getMapsLocation()));
	}

	/**
	 * Searches for Orphan maps in the specified directory.
	 *
	 * @param fileOrDir - Map File or Directory Name.
	 * @throws Exception
	 */
	private void orphanMaps(File fileOrDir) throws Exception {

		MapsUtil mapsUtil = MapsUtil.getInstance(envKey);
		List<String> maps = mapsUtil.getMapLabels();
		// check if the file is a directory
		if (fileOrDir.isDirectory()) {
			// loop thro' all files and check if there are any sub directories
			File[] allFiles = fileOrDir.listFiles();

			for (File file : allFiles) {
				if (!file.getName().contains("backup")
						&& !file.getName().contains("CVS")) {
					orphanMaps(file);
				}
			}
		} else {
			String map = FileUtil.getName(fileOrDir.getName());
			if (!map.contains("list") && !maps.contains(map)) {
				log.info(map + " Map file not listed in list.maps, an orphan");
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
			OrphanMaps mapDataSources = new OrphanMaps("TEST");
			mapDataSources.orphanMaps();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}