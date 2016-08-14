package com.csc.fsg.life.xg.utils.legacy;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PhysicalPathRemove {

	private static DocumentBuilder docBuild = null;
	private static String lineEnd = System.getProperty("line.separator");

	static {
		try {
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	private static void replaceInMaps(String xmlgDirName) throws IOException,
			SAXException {

		String mapsDirName = xmlgDirName + File.separator + "Map";
		if (!isDirExist(mapsDirName)) {
			System.out.println("Not a valid XMLG Directory, no Map folder.");
			return;
		}
		File mapsDir = new File(mapsDirName);
		File files[] = mapsDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getAbsolutePath();
			System.out.println("Replacing in :" + fileName);
			String fileContents = parseMap(file);
			moveTo(file, mapsDirName + "backup");
			FileOutputStream outfile = null;
			outfile = new FileOutputStream(fileName);
			byte bytes[] = fileContents.getBytes();
			outfile.write(bytes, 0, bytes.length);
			outfile.close();
		}

	}

	private static void replaceInSchemas(String xmlgDirName)
			throws IOException, SAXException {

		String schemasDirName = xmlgDirName + File.separator + "schemafiles";
		if (!isDirExist(schemasDirName)) {
			System.out.println("Not a valid XMLG Directory, no schemafiles folder.");
			return;
		}
		File schemasDir = new File(schemasDirName);
		File schemaFiles[] = schemasDir.listFiles();
		for (int i = 0; i < schemaFiles.length; i++) {
			File file = schemaFiles[i];
			String fileName = file.getAbsolutePath();
			System.out.println("Replacing in :" + fileName);
			String fileContents = parseSchema(file);
			moveTo(file, schemasDirName + "backup");
			FileOutputStream outfile = null;
			outfile = new FileOutputStream(fileName);
			byte bytes[] = fileContents.getBytes();
			outfile.write(bytes, 0, bytes.length);
		}

	}

	private static boolean isDirExist(String dirName) {

		dirName = getDirName(dirName);
		File file = new File(dirName);
		return file.exists();
	}

	private static String getDirName(String dirName) {

		dirName = dirName.replace('/', File.separatorChar);
		dirName = dirName.replace('\\', File.separatorChar);
		return dirName;
	}

	public static String parseMap(File f) throws IOException, SAXException {

		FileReader fileReader = new FileReader(f);
		int size = (int) f.length();
		char data[] = new char[size];
		for (int charsRead = 0; charsRead < size; charsRead += fileReader.read(data, charsRead, size)) {
		}
		fileReader.close();
		InputSource source = new InputSource(new ByteArrayInputStream(
				(new String(data)).getBytes()));
		Element element = docBuild.parse(source).getDocumentElement();
		NodeList dataSources = element.getElementsByTagName("datasource");
		int length = dataSources.getLength();
		for (int i = 0; i < length; i++) {
			Element e = (Element) dataSources.item(i);
			if ("X".equals(e.getAttribute("type"))) {
				String schemaName = e.getAttribute("filePath");
				int index = schemaName.lastIndexOf('\\');
				if (index == -1) {
					index = schemaName.lastIndexOf('/');
				}
				if (index != -1) {
					schemaName = schemaName.substring(index + 1);
					e.setAttribute("filePath", schemaName);
				}
			}
		}

		return "<?xml version=\"1.0\"?>" + lineEnd + element.toString();
	}

	public static String parseSchema(File f) throws IOException, SAXException {

		FileReader fileReader = new FileReader(f);
		int size = (int) f.length();
		char data[] = new char[size];
		for (int charsRead = 0; charsRead < size; charsRead += fileReader.read(data, charsRead, size)) {
		}
		fileReader.close();
		InputSource source = new InputSource(new ByteArrayInputStream(
				(new String(data)).getBytes()));
		Element element = docBuild.parse(source).getDocumentElement();
		NodeList dataSources = element.getElementsByTagName("xsd:include");
		int length = dataSources.getLength();
		for (int i = 0; i < length; i++) {
			Element e = (Element) dataSources.item(i);
			String schemaName = e.getAttribute("schemaLocation");
			int index = schemaName.lastIndexOf('\\');
			if (index == -1) {
				index = schemaName.lastIndexOf('/');
			}
			if (index != -1) {
				schemaName = schemaName.substring(index + 1);
				e.setAttribute("schemaLocation", schemaName);
			}
		}

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lineEnd
				+ element.toString();
	}

	public static void main(String args[]) throws IOException, SAXException {

		if (args.length < 1) {
			System.out.println("Usage: XMLGDir");
			return;
		}
		String xmlgDirName = args[0];
		if (!isDirExist(xmlgDirName)) {
			System.out.println("Not a valid dir.");
			return;
		}
		replaceInMaps(xmlgDirName);
		replaceInSchemas(xmlgDirName);
		System.out.println("Migration Completed.");
		return;
	}

	public static void moveTo(File sourceFile, String targetDir)
			throws IOException {

		createDirs(targetDir);
		File outFile = new File(targetDir, sourceFile.getName());
		boolean success = sourceFile.renameTo(outFile);
		if (!success) {
			throw new IOException("Moving file failed: "
					+ sourceFile.getAbsolutePath() + " to "
					+ outFile.getAbsolutePath());
		}
		return;
	}

	public static void createDirs(String dirName) {

		dirName = dirName.replace('/', File.separatorChar);
		dirName = dirName.replace('\\', File.separatorChar);
		File file = new File(dirName);
		if (!file.exists()) {
			if (dirName.indexOf('.') == -1) {
				file.mkdirs();
			} else {
				String dir = file.getParent();
				file = new File(dir);
				file.mkdirs();
			}
		}
	}
}
