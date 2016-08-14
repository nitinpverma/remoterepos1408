package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.net.URL;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;

/**
 * Loads the Spring configurations of xgd-web or xgws-web with out the need of web container.
 */
public class ConfigLoader {

	static char sepchar = File.separatorChar;

	/**
	 * Inits the designer config.
	 */
	public static final void initDesignerConfig() {

		String[] configs = {
				"classpath*:config/xgd-web/default/xmlg-config.xml",
				"classpath*:com/csc/fsg/life/security/authorization/PdpConfiguration.xml" };
		ApplicationContext appContext = new ClassPathXmlApplicationContext(configs);
		EnvironmentFactory.setAppContext(appContext);
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);

	}

	/**
	 * Inits the runtime config.
	 */
	public static final void initRuntimeConfig() {

		String[] configs = {
				"classpath*:com/csc/fsg/life/security/authorization/PdpConfiguration.xml",
				"classpath*:config/xgws-web/default/xmlg-config-context.xml",
				"classpath*:config/xgws-web/default/xmlg-env-context.xml",
				"classpath*:config/moduleContext.xml",
				"classpath*:config/moduleLoaderContext.xml",
				"classpath*:config/data-filters.xml",
				"classpath*:config/*-service-context.xml",
				"classpath*:config/*-events.xml",
				"classpath*:config/xgws-web/default/jndi-config.xml",
				"classpath*:config/xgws-web/default/environment/*.xml",
				"classpath*:config/xgws-web/default/xmlg-config.xml" };
		ApplicationContext appContext = new ClassPathXmlApplicationContext(configs);
		EnvironmentFactory.setAppContext(appContext);
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);

		EnvironmentFactory.setAppContext(appContext);
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);

	}

	/**
	 * Gets the workspace loc.
	 * 
	 * @return the workspace loc
	 */
	public static String getWorkspaceLoc() {
		// get the compiled class location
		URL currentLoc = ConfigLoader.class.getProtectionDomain().getCodeSource().getLocation();
		File thisFile = new File(currentLoc.getPath());

		// move 2 levels up to workspace directory level
		String workspaceDir = thisFile.getParentFile().getParentFile().getParent();
		System.out.println(ConfigLoader.class.getName() + " is loaded from "
				+ currentLoc);
		return workspaceDir;
	}
}
