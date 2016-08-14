package com.csc.fsg.life.xg.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.exportimport.Import;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Imports the maps zip file with out running the server
 */
public class StaticImport {

	public static void main(String[] args) {
		final Log log = LogFactory.getLog(StaticImport.class);
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"config/xmlg-config.xml");
			EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
			// EnvironmentInitializer.setServerDocRoot("D:/Projects/XGBase/lifewsWeb/webApplication");
			EnvironmentInitializer.setServerDocRoot("D:/Projects/Xg/wmaBase/.metadata/.plugins/org.eclipse.wst.server.core/tmp5/wtpwebapps/lifewsWeb");
			EnvironmentInitializer.init(null);

			if (args.length < 2) {
				log.info("USAGE: 'extract-name' 'import-ZIP-Name'");
				return;
			}
			final String envKey = "WMA";
			log.info("Importing for environment : " + envKey);
			log.info("Import Name : " + args[0]);
			log.info("Importing zip : " + args[1]);

			List<String> mapsList = new ArrayList<String>();
			mapsList.add(args[1]);

			Import im = new Import(envKey, "superusr");
			im.importAll(mapsList, args[0]);
			log.info("Import process done.");

		} catch (Exception e) {
			log.error("", e);
		}
	}
}
