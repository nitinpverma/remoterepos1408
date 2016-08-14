package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.xg.dbtable.BaseTableObject;
import com.csc.fsg.life.xg.dbtable.Primitive;
import com.csc.fsg.life.xg.exceptions.XGException;
import com.csc.fsg.life.xg.exportimport.ExportImport;

/**
 * The Class Primitives.
 */
public class Primitives {

	private static final List<String> list = new ArrayList<String>();
	private static final Log log = LogFactory.getLog(Primitives.class);
	static {
		list.add(">");
		list.add(">=");
		list.add("<");
		list.add("<=");
		list.add("==");
		list.add("!=");
		list.add("AND");
		list.add("OR");

		String transfList = "D:/XMLG/WMA/Expressions/Transformations/list.trfs";
		Element transfRoot;
		try {
			transfRoot = ExportImport.parseInput(new File(transfList));
			NodeList primRoot = transfRoot.getElementsByTagName(BaseTableObject.ELM_PRIMITIVE);
			int len = primRoot.getLength();
			for (int i = 0; i < len; i++) {
				Element e = (Element) primRoot.item(i);
				NodeList transfNL = e.getElementsByTagName(Primitive.ELM_TRANSFORMATION);
				int transfLen = transfNL.getLength();
				for (int j = 0; j < transfLen; j++) {
					Element transf = (Element) transfNL.item(j);
					String handlerClass = transf.getAttribute(BaseTableObject.ATTR_HANDLER_CLASS);
					String transfName = handlerClass.substring(handlerClass.lastIndexOf('.') + 1);
					list.add(transfName);
				}
			}
		} catch (XGException e1) {
			log.error("", e1);
		}
	}

	/**
	 * Checks if the function is primitive.
	 * 
	 * @param funcName the func name
	 * @return true, if is primitive
	 */
	public static boolean isPrimitive(String funcName) {
		return list.contains(funcName);
	}
}
