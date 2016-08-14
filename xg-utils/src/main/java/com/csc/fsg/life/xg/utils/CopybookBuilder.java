package com.csc.fsg.life.xg.utils;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.tools.copybook.model.FieldDefinitionEntry;
import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBook;

/**
 * Generates Copybook definition from the XMLG copybook meta-data XML.
 */
public class CopybookBuilder {

	private static final Log log = LogFactory.getLog(CopybookBuilder.class);
	private static String envKey;
	private StringBuffer copybookStr = new StringBuffer();
	private static final String SPACE = "  ";
	private static final String SEVEN_SPACES = "000000 ";

	/**
	 * Gets the field def entries.
	 * 
	 * @param copybook the copybook
	 * @return the field def entries
	 * @throws Exception the exception
	 */
	private synchronized Hashtable<String, FieldDefinitionEntry> getFieldDefEntries(
			CopyBook copybook) throws Exception {

		Hashtable<String, FieldDefinitionEntry> fdeHash = new Hashtable<String, FieldDefinitionEntry>();
		try {
			EnvironmentInitializer instance = EnvironmentInitializer.getInstance(envKey);
			File copybookMetaDir = new File(instance.getCopybookMetaDir(), copybook.getSystem());
			File copybookFile = new File(copybookMetaDir, copybook.getName() + ".xml");
			if (!copybookFile.exists())
				throw new Exception(copybook.getName() + " Copybook XML doesnot exist");

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document copybookDOM = builder.parse(copybookFile);
			Element root = copybookDOM.getDocumentElement();

			NodeList childTags = root.getChildNodes();
			FieldDefinitionEntry fdeObject = null;
			for (int i = 0; i < childTags.getLength(); i++) {
				if (!(childTags.item(i) instanceof Element))
					continue;
				Element childNode = (Element) childTags.item(i);
				if (childNode.getTagName().equals("FDE")) {
					fdeObject = buildFDEXMLElement(copybook, childNode);
					fdeHash.put(String.valueOf(fdeObject.getFieldId()), fdeObject);
				}
			}
			if (fdeHash.isEmpty())
				throw new Exception("Unable to build objects for copybook :"
						+ copybook);
		} catch (Exception e) {
			log.error("", e);
		}
		return fdeHash;
	}

	private FieldDefinitionEntry buildFDEXMLElement(CopyBook copybook,
			Element childNode) {
		FieldDefinitionEntry tdElement;

		String copybookName = safeTrim(copybook.getName());
		int fieldId = Integer.parseInt(safeTrim(childNode.getAttribute("fieldId")));
		String fieldName = safeTrim(childNode.getAttribute("fieldName"));
		int parentFieldId = Integer.parseInt(safeTrim(childNode.getAttribute("parentFieldId")));
		String fieldType = safeTrim(childNode.getAttribute("fieldType"));
		int fieldOffset = Integer.parseInt(safeTrim(childNode.getAttribute("offSet")));
		int dataLength = Integer.parseInt(safeTrim(childNode.getAttribute("dataLength")));
		String dataType = safeTrim(childNode.getAttribute("dataType"));
		int dataScale = Integer.parseInt(safeTrim(childNode.getAttribute("dataScale")));
		int numOfOccurs = Integer.parseInt(safeTrim(childNode.getAttribute("nbrOfOccurs")));
		int redefFieldId = Integer.parseInt(safeTrim(childNode.getAttribute("redefinesFieldId")));
		tdElement = new FieldDefinitionEntry(copybookName, dataLength,
				dataScale, dataType, null, null, fieldId, fieldName, fieldType,
				numOfOccurs, fieldOffset, parentFieldId, redefFieldId);

		return tdElement;
	}

	/**
	 * Safe trim.
	 * 
	 * @param string the string
	 * @return the string
	 */
	private static String safeTrim(String string) {
		if (string == null)
			return ("");
		else if (string.equals(" "))
			return (string);
		else
			return (string.trim());
	}

	private void writeFDEEntries(
			Hashtable<String, FieldDefinitionEntry> fdeHash, CopyBook copyBook) throws IOException {
		int fdeCount = fdeHash.size();
		Hashtable<String, StringBuffer> bufferHash = new Hashtable<String, StringBuffer>();
		for (int i = 0; i < fdeCount; i++) {
			FieldDefinitionEntry fde = fdeHash.get(String.valueOf(i));
			StringBuffer fdeBuffer = new StringBuffer();
			if (fde.getFieldId() == fde.getParentFieldId()) {
				fdeBuffer.append(SEVEN_SPACES).append("05").append(SPACE).append(fde.getFieldName());
				addOtherAttribs(fdeBuffer, fdeHash, fde);
				bufferHash.put(String.valueOf(fde.getFieldId()), fdeBuffer);
			} else {
				StringBuffer parentBuffer = bufferHash.get(String.valueOf(fde.getParentFieldId()));
				int depth = calcualteParentDepth(fde, fdeHash);
				int level = 5;
				parentBuffer.append(SEVEN_SPACES);
				for (int j = 0; j < depth; j++) {
					parentBuffer.append(SPACE);
					level += 5;
				}
				parentBuffer.append(level).append(SPACE).append(fde.getFieldName());
				if (fde.getFieldType().equals("E")
						|| fde.getFieldType().equals("FE")) {
					addAdditionalSpace(parentBuffer, depth);
					addDatatypes(parentBuffer, fde);
				}
				addOtherAttribs(parentBuffer, fdeHash, fde, level);
				bufferHash.put(String.valueOf(fde.getFieldId()), parentBuffer);
				fdeBuffer.append(parentBuffer.toString());
			}
		}
		for (int i = 0; i < fdeCount; i++) {
			FieldDefinitionEntry fde = fdeHash.get(String.valueOf(i));
			if (fde.getFieldId() == fde.getParentFieldId()) {
				StringBuffer buff = bufferHash.get(String.valueOf(i));
				copybookStr.append(buff.toString());
			}
		}
		log.debug(copybookStr.toString());
		FileUtil.writeFile("c:\\temp\\" + copyBook.getName() + ".cpy", copybookStr.toString(), false);
	}

	private void addAdditionalSpace(StringBuffer parentBuffer, int depth) {
		int index = parentBuffer.lastIndexOf("\n");
		int offset = 48 - (depth * 2);
		if (index != -1) {
			int currentLineLen = parentBuffer.substring(index + 1).trim().length();
			for (int i = 0; i < (offset - currentLineLen); i++) {
				parentBuffer.append(" ");
			}
		}
	}

	private void addDatatypes(StringBuffer parentBuffer,
			FieldDefinitionEntry fde) {
		int dataLength = fde.getDataLength();
		if (fde.getDataType().equals("ALPHANUM")) {
			if (dataLength > 3)
				parentBuffer.append(SPACE).append("PIC X(").append(dataLength).append(")");
			else
				parentBuffer.append(SPACE).append("PIC ").append(repreatChars('X', dataLength));
		}
		if (fde.getDataType().equals("NUMERIC"))
			if (dataLength > 3)
				parentBuffer.append(SPACE).append("PIC 9(").append(dataLength).append(")");
			else
				parentBuffer.append(SPACE).append("PIC ").append(repreatChars('9', dataLength));
		if (fde.getDataType().equals("SBINARY"))
			parentBuffer.append(SPACE).append("PIC S9(").append(dataLength * 2).append(") COMP");
		if (fde.getDataType().equals("SPACKED")) {
			parentBuffer.append(SPACE).append("PIC S9(");
			if (fde.getDataScale() > 0) {
				parentBuffer.append((dataLength - fde.getDataScale()) * 2 + 1).append(")V9(").append(fde.getDataScale());
			} else {
				parentBuffer.append((dataLength * 2) - 1);
			}
			parentBuffer.append(") COMP-3");
		}
	}

	private StringBuffer repreatChars(char c, int dataLength) {
		StringBuffer sb = new StringBuffer(dataLength);
		for (int i = 0; i < dataLength; i++) {
			sb.append(c);
		}
		return sb;
	}

	private void addOtherAttribs(StringBuffer buffer,
			Hashtable<String, FieldDefinitionEntry> fdeHash,
			FieldDefinitionEntry fde) {
		addOtherAttribs(buffer, fdeHash, fde, 1);
	}

	private void addOtherAttribs(StringBuffer buffer,
			Hashtable<String, FieldDefinitionEntry> fdeHash,
			FieldDefinitionEntry fde, int level) {
		if (fde.getNbrOfOccurs() > 1)
			buffer.append(SPACE).append("OCCURS ").append(fde.getNbrOfOccurs()).append(" TIMES");
		if (fde.getRedefinesFieldId() != -1) {
			FieldDefinitionEntry redFde = fdeHash.get(String.valueOf(fde.getRedefinesFieldId()));
			buffer.append(SPACE).append(" REDEFINES ").append("\n");
			buffer.append(SEVEN_SPACES).append("  ");
			buffer.append(repreatChars(' ', level / 5)).append(redFde.getFieldName());
		}
		buffer.append(".\n");
	}

	private int calcualteParentDepth(FieldDefinitionEntry fde,
			Hashtable<String, FieldDefinitionEntry> fdeHash) {
		int depth = 1;
		FieldDefinitionEntry temp = fdeHash.get(String.valueOf(fde.getParentFieldId()));
		while (temp != null) {
			if (temp.getParentFieldId() != temp.getFieldId()) {
				depth++;
				temp = fdeHash.get(String.valueOf(temp.getParentFieldId()));
			} else
				break;
		}
		return depth;
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			ConfigLoader.initDesignerConfig();
			envKey = "WMA";
			String system = "WMA";
			CopybookBuilder cbb = new CopybookBuilder();
			CopyBook cb = new CopyBook();
			cb.setName("CIUAFNA1");
			cb.setSystem(system);
			Hashtable<String, FieldDefinitionEntry> hash = cbb.getFieldDefEntries(cb);
			cbb.writeFDEEntries(hash, cb);
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
