package com.csc.fsg.life.xg.utils;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.db.*;
import com.csc.fsg.life.xg.exportimport.*;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Archives the maps with running the server.
 */
public class StaticExport {

	private static final Log log = LogFactory.getLog(StaticExport.class);

	public static void main(String[] args) throws IOException, Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"config/xmlg-config.xml");
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
		EnvironmentInitializer.setServerDocRoot("D:/Projects/Xg/wmaBase/.metadata/.plugins/org.eclipse.wst.server.core/tmp5/wtpwebapps/lifewsWeb");
		EnvironmentInitializer.init(null);

		if (args.length < 2) {
			log.info("USAGE: 'List of maps in a File' 'Export-name'");
			return;
		}
		final String exportName = args[1];
		final String envKey = "WMA";
		log.info("Exporting from environment : " + envKey);
		Export me = new Export(envKey);
		me.export(getMaps(args[0]), exportName);
		log.info("Successfully exported the maps.");
	}

	/**
	 * Reads the maps from a flat file.
	 * 
	 * @param mapsFile the maps file
	 * 
	 * @return the maps
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static List<String> getMaps(String mapsFile) throws IOException {

		List<String> mapsList = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mapsFile));
			String mapName = null;
			while ((mapName = reader.readLine()) != null) {
				if (mapName.trim().length() > 0) {
					mapsList.add(mapName);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return mapsList;
	}
}
