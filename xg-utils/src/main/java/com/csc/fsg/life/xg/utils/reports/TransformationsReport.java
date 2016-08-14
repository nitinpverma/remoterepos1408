package com.csc.fsg.life.xg.utils.reports;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.serverutils.FunctionsUtil;
import com.csc.fsg.life.xg.utils.ConfigLoader;
import com.csc.fsg.life.xg.utils.VelocityUtils;

public class TransformationsReport {

	private static final Log log = LogFactory.getLog(TransformationsReport.class);
	private static final String REPORT_TXT = "com/csc/fsg/life/xg/utils/reports/transformationsReport.vm";
	private static final String REPORT_HTML = "com/csc/fsg/life/xg/utils/reports/transformationsReport-html.vm";
	private static String envKey;

	/**
	 * The main method.
	 * 
	 * @param args the args
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		envKey = "PW-PAInt";
		ConfigLoader.initDesignerConfig();
		generateReport();
	}

	/**
	 * Generates report using velocity template.
	 * 
	 * @throws Exception the exception
	 */
	private static void generateReport() throws Exception {
		// create a context and add data
		VelocityContext context = new VelocityContext();
		FunctionsUtil funcsUtil = FunctionsUtil.getInstance(envKey);
		List<String> cTransformationNames = funcsUtil.getComplexTransformationNames();
		Collections.sort(cTransformationNames);
		// loop thro' the list

		// for (String transfName : cTransformationNames) {
		// Primitive transformation =
		// funcsUtil.getComplexTransformation(transfName);
		// System.out.println("Tranfomation Name : "
		// + transformation.getHandlerClassPath());
		// System.out.println("Decription : "
		// + transformation.getDescription());
		// System.out.println("\tExpression : "
		// + transformation.getExpressionClass());
		// }

		context.put("cTransformationNames", cTransformationNames);
		context.put("funcsUtil", funcsUtil);

		String output = VelocityUtils.merge(context, REPORT_TXT);
		FileUtil.writeFile("TransformationsReport.txt", output, false);
		log.debug(output);
		output = VelocityUtils.merge(context, REPORT_HTML);
		FileUtil.writeFile("TransformationsReport.html", output, false);
		log.debug(output);
	}
}
