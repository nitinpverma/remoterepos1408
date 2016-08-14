package com.csc.fsg.life.xg.utils;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Generic Velocity Utility class that can be used to run velocity template.
 */
public class VelocityUtils {

	private static final Log log = LogFactory.getLog(VelocityUtils.class);
	private static VelocityEngine vEngine = new VelocityEngine();

	static {
		// initialize Velocity
		try {
			vEngine.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
			vEngine
					.setProperty("class.resource.loader.class",
							"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			vEngine.setProperty("class.resource.loader.cache", "true");
			vEngine.init();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * Merge template and the context object and return the output.
	 * 
	 * @param context the context
	 * @param templateName the template name
	 * @return the string
	 * @throws ResourceNotFoundException the resource not found exception
	 * @throws ParseErrorException the parse error exception
	 * @throws Exception the exception
	 */
	public static String merge(VelocityContext context, String templateName)
			throws ResourceNotFoundException, ParseErrorException, Exception {

		StringWriter writer;
		// Get template
		Template template = vEngine.getTemplate(templateName);
		writer = new StringWriter();
		// Fill template with values.
		template.merge(context, writer);
		return writer.toString();
	}
}
