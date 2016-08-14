package com.csc.fsg.life.xg.utils.reports;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.csc.fsg.life.xg.dbtable.ColumnMetaData;
import com.csc.fsg.life.xg.dbtable.Primitive;
import com.csc.fsg.life.xg.dbtable.TLColumn;
import com.csc.fsg.life.xg.dbtable.TLJets;
import com.csc.fsg.life.xg.dbtable.TranslationModel;
import com.csc.fsg.life.xg.dbtable.Variation;
import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.serverutils.FunctionsUtil;
import com.csc.fsg.life.xg.utils.ConfigLoader;
import com.csc.fsg.life.xg.utils.VelocityUtils;

/**
 * The Class TranslationsReport.
 */
public class TranslationsReport {

	private static final Log log = LogFactory.getLog(TranslationsReport.class);
	private static final String REPORT_TXT = "com/csc/fsg/life/xg/utils/reports/translationsReport.vm";
	private static final String REPORT_HTML = "com/csc/fsg/life/xg/utils/reports/translationsReport-html.vm";
	private static String envKey;
	/**
	 * The main method.
	 * 
	 * @param args the args
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		envKey = "PW-PAInt-9128";
		ConfigLoader.initDesignerConfig();
		// generateReport1();
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
		List<String> translationNames = funcsUtil.getTranslationNames();
		Collections.sort(translationNames);

		generateReport(translationNames, funcsUtil);
		context.put("translationNames", translationNames);
		context.put("funcsUtil", funcsUtil);

		String output = VelocityUtils.merge(context, REPORT_TXT);
		FileUtil.writeFile("TranslationsReport.txt", output, false);
		log.debug(output);
		output = VelocityUtils.merge(context, REPORT_HTML);
		FileUtil.writeFile("TranslationsReport.html", output, false);
		log.debug(output);
	}

	/**
	 * Generates report using java code.
	 * 
	 * @param translationNames
	 * @param funcsUtil
	 * @throws Exception the exception
	 */
	private static void generateReport(List<String> translationNames,
			FunctionsUtil funcsUtil) throws Exception {
		if (!log.isDebugEnabled())
			return;
		// loop thro' the translations list
		for (String translName : translationNames) {
			Primitive translation = funcsUtil.getTranslation(translName);
			TranslationModel translationModel = funcsUtil.getTranslationExpression(translation);
			String functionName = translationModel.getFunctionName();
			System.out.println("Translation Name : " + functionName);
			String desc = translationModel.getDesc();
			System.out.println("Description : " + desc);

			ColumnMetaData columnMetaData = translationModel.getColumnMetaData();
			List<Variation> variations = translationModel.getVariations();
			System.out.println("\tVariations : ");
			System.out.println("\t\tInput columns,Output Column,Variation Name,Description");
			for (Variation variation : variations) {
				List<String> inputColumns = variation.getInputColumns();
				StringBuilder sb = new StringBuilder();
				sb.append("\t\t");
				for (String inputColumn : inputColumns) {
					//TODO below needs the file checked into life-build-patch
					//sb.append(columnMetaData.getColumnDisplayName(inputColumn)).append(";");
				}
				sb.replace(sb.length() - 1, sb.length(), "");
				sb.append(","
						//TODO below needs the file checked into life-build-patch
						//+ columnMetaData.getColumnDisplayName(variation.getOutputColumn())
						+ "," + variation.getHandlerClass() + ","
						+ variation.getVarDesc());
				System.out.println(sb.toString());
			}

			List<TLColumn> columns = columnMetaData.getColumns();
			StringBuilder sb = new StringBuilder();
			sb.append("\tData : \n");
			sb.append("\t\t");
			for (TLColumn tlColumn : columns) {
				sb.append(tlColumn.getDisplayName() + ",");
			}
			if (sb.toString().endsWith(",")) {
				System.out.println(sb.toString().substring(0, sb.toString().lastIndexOf(",")));
			} else {
				System.out.println(sb.toString());
			}

			List<TLJets> data = translationModel.getData();
			for (TLJets tlJets : data) {
				//TODO below needs the file checked into life-build-patch
				//System.out.println("\t\t" + tlJets.toData(","));
			}
		}
	}
}
