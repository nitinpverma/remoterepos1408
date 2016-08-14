package com.csc.fsg.life.xg.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

public class CopybookMetadataGen {
	public static void main(String[] args) {

		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"config/xmlg-config.xml");
			EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
			EnvironmentInitializer.setServerDocRoot("C:/Projects/XG/lifewsWeb/webApplication");
			EnvironmentInitializer.init(null);

			// TODO Make saveAllCopybooks method public before running this
			// class.
			// CopyBookInfoGetter.getInstance("PNX-DCMS2U").saveAllCopybooks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
