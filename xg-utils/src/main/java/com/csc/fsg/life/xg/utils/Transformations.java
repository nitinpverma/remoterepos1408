package com.csc.fsg.life.xg.utils;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.common.Constants;
import com.csc.fsg.life.tools.xml.XmlWriter;
import com.csc.fsg.life.xg.dbtable.BaseTableObject;
import com.csc.fsg.life.xg.dbtable.Primitive;
import com.csc.fsg.life.xg.exportimport.ExportImport;
import com.csc.fsg.life.xg.serverutils.FileUtil;

/**
 * Provides utilities to find out the label and handler class conflicts.
 */
public class Transformations {

	private static final Log log = LogFactory.getLog(Transformations.class);

	/**
	 * Verifies handler class name is same as transformation file name.
	 *
	 * @param transfDir the transf dir
	 * @throws Exception the exception
	 */
	public void verifyHandlerClass(String transfDir) throws Exception {

		File file = new File(transfDir);
		File transfFiles[] = file.listFiles();

		for (File trfFile : transfFiles) {

			if (trfFile.isDirectory()) {
				continue;
			}
			Element root = ExportImport.parseInput(trfFile);
			try {
				String handlerClass = getHandlerClass(root);
				if (!handlerClass.equals(FileUtil.getName(trfFile.getName()))) {

					log.info(FileUtil.getName(trfFile.getName()) + " : "
							+ handlerClass);
				}
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		log.info("Scanning is Done.");
	}

	/**
	 * Verifies handler class name is same as label name.
	 *
	 * @param transfDir the transf dir
	 * @throws Exception the exception
	 */
	public void verifyLabelAndHandlerClass(String transfDir) throws Exception {

		File file = new File(transfDir);
		File transfFiles[] = file.listFiles();

		for (File trfFile : transfFiles) {

			if (trfFile.isDirectory()) {
				continue;
			}
			Element root = ExportImport.parseInput(trfFile);
			try {
				String handlerClass = getHandlerClass(root);
				String label = getLabel(root);
				if (!handlerClass.equals(label)) {

					log.info(label + " : " + handlerClass);
					root.setAttribute("label", handlerClass);

					String newDir = transfDir + "/New/";
					FileUtil.createDirs(newDir);
					File newListFile = new File(newDir, trfFile.getName());
					XmlWriter writer = new XmlWriter(root.getOwnerDocument());
					FileUtil.writeFile(newListFile, writer.outputString(), false);
				}
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		log.info("Scanning is Done.");
	}

	/**
	 * Verifies handler class name is same as label name in list.trf file.
	 *
	 * @param transfDir the transf dir
	 * @throws Exception the exception
	 */
	public void verifyLabelAndHCInListFile(String transfDir) throws Exception {

		File listFile = new File(transfDir, Constants.LIST_FILENAME
				+ Constants.TRANSF_LIST_EXTN);
		Element transfRoot = ExportImport.parseInput(listFile);
		NodeList transfNL = transfRoot.getElementsByTagName(Primitive.ELM_TRANSFORMATION);
		int transfLen = transfNL.getLength();
		for (int j = 0; j < transfLen; j++) {
			Element transf = (Element) transfNL.item(j);
			String complexInd = transf.getAttribute(BaseTableObject.ATTR_IS_COMPLEX);
			String handlerClass = transf.getAttribute(BaseTableObject.ATTR_HANDLER_CLASS);
			if ("N".equalsIgnoreCase(complexInd)) {
				// Primitive, handler class name includes package name, strip
				// off package name
				handlerClass = handlerClass.substring(handlerClass.lastIndexOf('.') + 1);
			}
			String label = transf.getAttribute(BaseTableObject.ATTR_LABEL);
			if (!handlerClass.equals(label)) {
				log.info(label + " : " + handlerClass);
				transf.setAttribute("label", handlerClass);
			}
		}
		File newListFile = new File(transfDir, "listnew" + Constants.TRANSF_LIST_EXTN);
		//FileWriter fw = new FileWriter(newListFile);
		//XMLUtils.getInstance().printDocument(transfRoot, fw);
		//FileUtil.safeClose(fw);
		XmlWriter writer = new XmlWriter(transfRoot.getOwnerDocument());
		FileUtil.writeFile(newListFile, writer.outputString(), false);
		log.info("Scanning is Done.");
	}

	private String getHandlerClass(Element root) throws Exception {

		// Get all mappings from the map
		String handlerClass = root.getAttribute("handlerClass");
		if (handlerClass == null) {
			handlerClass = "";
		}
		return handlerClass;
	}

	private String getLabel(Element root) throws Exception {

		// Get all mappings from the map
		String label = root.getAttribute("label");
		if (label == null) {
			label = "";
		}
		return label;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			String transfDir = "D:/XMLG/WMA/Expressions/Transformations";
			Transformations mapDataSources = new Transformations();
			// mapDataSources.verifyHandlerClass(transfDir);
			mapDataSources.verifyLabelAndHandlerClass(transfDir);
			mapDataSources.verifyLabelAndHCInListFile(transfDir);
		} catch (Exception e) {
			log.error("", e);
		}
	}
}