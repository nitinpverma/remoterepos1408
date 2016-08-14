package com.csc.fsg.life.xg.utils.jbo.wrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csc.fsg.life.common.Constants;

/**
 * The Class Class which represents a wrapper class.
 */
public class Class extends Field {

	public static final String LIST_TYPE = "List";
	public static final String STRING_TYPE = "String";
	private static final Log log = LogFactory.getLog(Class.class);
	private static Map<String, Field> allConcreteClasses = new HashMap<String, Field>();

	private Map<String, Field> fields;
	private List<String> concreteClasses;
	private boolean concreteClass;
	private String outDir = "D:/workspace1/";

	/**
	 * Instantiates a new class.
	 * 
	 * @param name the name
	 */
	public Class(String name) {
		this.name = name;
		fields = new HashMap<String, Field>();
		concreteClasses = new ArrayList<String>();
	}

	/**
	 * Gets the fields.
	 * 
	 * @return the fields
	 */
	public Map<String, Field> getFields() {
		return fields;
	}

	/**
	 * Sets the fields.
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	/**
	 * Adds the field.
	 * 
	 * @param field the field
	 */
	public void addField(Field field) {
		// check if exists
		if (!exists(field)) {
			// add to the list
			fields.put(field.getName(), field);
		}
	}

	/**
	 * Exists.
	 * 
	 * @param field the field
	 * 
	 * @return true, if successful
	 */
	public boolean exists(Field field) {
		return fields.containsKey(field.getName());
	}

	/**
	 * Exists.
	 * 
	 * @param fieldName the field name
	 * 
	 * @return true, if successful
	 */
	public boolean exists(String fieldName) {
		return fields.containsKey(fieldName);
	}

	/**
	 * Gets the field.
	 * 
	 * @param fieldName the field name
	 * 
	 * @return the field
	 */
	public Field getField(String fieldName) {
		return fields.get(fieldName);
	}

	/**
	 * Concrete class exists.
	 * 
	 * @param className the class name
	 * 
	 * @return true, if successful
	 */
	private static boolean concreteClassExists(String className) {
		return allConcreteClasses.containsKey(className);
	}

	/**
	 * Adds the concrete class.
	 * 
	 * @param concreteClass the concrete class
	 */
	public static void addConcreteClass(Field concreteClass) {
		if (!concreteClassExists(concreteClass.getName())) {
			allConcreteClasses.put(concreteClass.getName(), concreteClass);
		}
	}

	/**
	 * Gets the concrete class.
	 * 
	 * @param className the class name
	 * 
	 * @return the concrete class
	 */
	public static Field getConcreteClass(String className) {
		return allConcreteClasses.get(className);
	}

	/**
	 * Gets the concrete refs.
	 * 
	 * @return the concrete refs
	 */
	public List<String> getConcreteRefs() {
		return concreteClasses;
	}

	/**
	 * Adds the concrete class ref.
	 * 
	 * @param concreteClassName the concrete class name
	 */
	public void addConcreteClassRef(String concreteClassName) {
		if (!concreteClasses.contains(concreteClassName)) {
			concreteClasses.add(concreteClassName);
		}
	}

	/**
	 * Prints the.
	 * 
	 * @param indent the indent
	 */
	public void print(String indent) {
		log.info(indent + name);
		Collection<Field> values = fields.values();
		for (Field field : values) {
			if (field instanceof Class) {
				Class cls = (Class) field;
				cls.print(indent + "  ");
				if (LIST_TYPE.equals(cls.getType())) {
					List<String> refs = cls.getConcreteRefs();
					for (String cClass : refs) {
						((Class) Class.getConcreteClass(cClass)).print(indent
								+ "  ");
					}
				}
			} else {
				log.info(indent + "  " + field);
			}
		}
	}

	/**
	 * Checks if is concrete class.
	 * 
	 * @return the concreteClass
	 */
	public boolean isConcreteClass() {
		return concreteClass;
	}

	/**
	 * Sets the concrete class.
	 * 
	 * @param concreteClass the concreteClass to set
	 */
	public void setConcreteClass(boolean concreteClass) {
		this.concreteClass = concreteClass;
	}

	/**
	 * Creates the class files.
	 * 
	 * @param pkg the pkg
	 */
	public void createClassFiles(String pkg) {
		createClassTree(pkg);
	}

	/**
	 * Creates the class tree.
	 * 
	 * @param pkg the pkg
	 */
	private void createClassTree(String pkg) {
		StringBuilder sb = new StringBuilder();

		sb.append("package " + pkg + ";");
		sb.append(Constants.NEXTLINE);
		sb.append(Constants.NEXTLINE);
		sb.append("import java.util.List;");
		sb.append(Constants.NEXTLINE);
		sb.append(Constants.NEXTLINE);
		sb.append("public class " + getName() + " {");
		sb.append(Constants.NEXTLINE);
		sb.append(Constants.NEXTLINE);

		Collection<Field> values = fields.values();
		for (Field field : values) {

			if (field instanceof Class) {
				Class subClass = (Class) field;
				subClass.createClassTree(pkg);
				// if (LIST_TYPE.equals(subClass.getType())) {
				List<String> refs = subClass.getConcreteRefs();
				if (!refs.isEmpty()) {
					sb.append(Constants.TAB);
					sb.append("// Concrete class(s)" + refs);
					sb.append(Constants.NEXTLINE);
					for (String cClass : refs) {
						Class concreteClass = (Class) Class.getConcreteClass(cClass);
						// create concrete class
						concreteClass.createClassTree(pkg);
					}
				}
				// }
			}
			sb.append(Constants.TAB);
			sb.append("private " + field.getType() + " "
					+ caseChangeFirstLetter(field.getName(), false)
					+ " = null;");
			sb.append(Constants.NEXTLINE);
		}

		// getters
		for (Field field : values) {
			String lowerFirstLetter = caseChangeFirstLetter(field.getName(), false);
			String upperFirstLetter = caseChangeFirstLetter(field.getName(), true);
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			sb.append("public " + field.getType() + " " + "get"
					+ upperFirstLetter + "() {");
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			sb.append(Constants.TAB);
			sb.append("return " + lowerFirstLetter + ";");
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			sb.append("}");
			sb.append(Constants.NEXTLINE);
		}
		// setters
		for (Field field : values) {
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			String lowerFirstLetter = caseChangeFirstLetter(field.getName(), false);
			String upperFirstLetter = caseChangeFirstLetter(field.getName(), true);
			sb.append("public void set" + upperFirstLetter + "("
					+ field.getType() + " " + lowerFirstLetter + ") {");
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			sb.append(Constants.TAB);
			sb.append("this." + lowerFirstLetter + " = " + lowerFirstLetter
					+ ";");
			sb.append(Constants.NEXTLINE);
			sb.append(Constants.TAB);
			sb.append("}");
			sb.append(Constants.NEXTLINE);
		}
		sb.append("}");
		if (!isIgnore()) {
			// log.info(sb.toString());
			// write to file
			write(pkg, getName(), sb.toString());
		}
	}

	/**
	 * Case change first letter.
	 * 
	 * @param str the str
	 * @param capitalize the capitalize
	 * 
	 * @return the string
	 */
	private static String caseChangeFirstLetter(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}

	/**
	 * Writes the current document out as an XML file.
	 * 
	 * @param outFile Name of output file
	 * @param pkg the pkg
	 * @param content the content
	 * 
	 * @throws IOException
	 */
	public void write(String pkg, String outFile, String content) {

		FileWriter fileWriter = null;
		try {
			// create output stream and write file
			String pkgDir = pkg.replace('.', '/');
			File fileDir = new File(outDir + pkgDir);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			fileWriter = new FileWriter(new File(fileDir, outFile + ".java"));
			fileWriter.write(content);

			// flush and close the buffer
			fileWriter.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				log.error("", e);
			}
		}
	}
}
