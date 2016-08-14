package com.csc.fsg.life.xg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides utility methods to find the references and dependencies.
 * 
 * @author mchintal
 */
public class CopybookRefs {

	private static Log log = LogFactory.getLog(CopybookRefs.class);
	private static Map<String, List<String>> cbMap = new HashMap<String, List<String>>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String copybookDir = "D:/projects/wma/wmaxg-data/Datasources/Copybooks/Source/WMA";
		String copybookName = "CIFEKMAX";
		scanCopybooks(copybookDir);
		findUses(copybookName, copybookDir);
	}

	/**
	 * Scans all copy books in a given copy book folder for dependencies (inner
	 * copy books) and dependencies of dependencies and so forth.
	 * 
	 * @param copybookDir the copybook dir
	 */
	private static void scanCopybooks(String copybookDir) {
		// read all copybooks (.cpy) in a single system.
		// for each copybook look for copy statement and read the copybook name

		File cbDir = new File(copybookDir);
		if (!cbDir.isDirectory()) {
			System.out.println("Not a directory");
		}

		File[] list = cbDir.listFiles();

		int i = 1;
		for (File cb : list) {

			if (cb.isDirectory())
				continue;

			findDependencies(cb, "", i++);
		}
	}

	/**
	 * Scans for dependencies (inner copy books) and dependencies of
	 * dependencies and so forth.
	 * 
	 * @param copybook the copybook
	 * @param indent the indent
	 * @param count the count
	 */
	private static void findDependencies(File copybook, String indent, int count) {

		BufferedReader in = null;
		try {
			log.info(indent + copybook.getName() + " : " + count);
			in = new BufferedReader(new FileReader(copybook));

			if (!in.ready())
				throw new IOException(copybook.getName()
						+ " not ready for reading");

			int innerCount = 0;
			String line;
			indent += "  ";
			while ((line = in.readLine()) != null) {

				// remove leading line numbers if they exist
				String lineWithNoNumbers = line;
				if (Character.isDigit(line.charAt(0)))
					lineWithNoNumbers = line.substring(6).trim();

				// if the line represents a comment, skip processing
				if (lineWithNoNumbers.startsWith("*"))
					continue;

				// check for existence of COPY statement
				int copyIndex = line.indexOf("COPY ");
				if (copyIndex >= 0) {
					// read included copybook name
					String innerCBName = line.substring(copyIndex + 4).trim();
					innerCBName = innerCBName.substring(0, 8) + ".CPY";
					List<String> list = cbMap.get(copybook.getName());
					if (list == null) {
						list = new ArrayList<String>();
						list.add(innerCBName);
						cbMap.put(copybook.getName(), list);
						findDependencies(new File(copybook.getParent(), innerCBName), indent, ++innerCount);
					} else {
						if (!list.contains(innerCBName)) {
							list.add(innerCBName);
							findDependencies(new File(copybook.getParent(), innerCBName), indent, ++innerCount);
						}
					}
				}
			}

		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					log.error("", e);
				}

		}
	}

	/**
	 * Finds uses of a copybook(as a inner copybook).
	 * 
	 * @param copybookName the copybook name
	 */
	private static void findUses(String copybookName, String copybookDir) {

		// read all copybooks (.cpy) in a single system.
		// for each copybook look for copy statement and read the copybook name

		log.info("---------------------------------");
		log.info(copybookName + " is used in the following copybooks");
		File cbDir = new File(copybookDir);
		if (!cbDir.isDirectory()) {
			log.error("Not a directory");
		}

		File[] list = cbDir.listFiles();

		String indent = "  ";
		for (File cb : list) {

			if (cb.isDirectory())
				continue;

			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(cb));

				if (!in.ready())
					throw new IOException(cb.getName()
							+ " not ready for reading");

				String line;
				while ((line = in.readLine()) != null) {

					// remove leading line numbers if they exist
					String lineWithNoNumbers = line;
					if (Character.isDigit(line.charAt(0)))
						lineWithNoNumbers = line.substring(6).trim();

					// if the line represents a comment, skip processing
					if (lineWithNoNumbers.startsWith("*"))
						continue;

					// check for existence of COPY statement
					int copyIndex = line.indexOf("COPY ");
					if (copyIndex >= 0) {
						// read included copybook name
						String innerCBName = line.substring(copyIndex + 4).trim();
						innerCBName = innerCBName.substring(0, 8);
						if (innerCBName.equalsIgnoreCase(copybookName)) {
							log.info(indent + cb.getName());
							break;
						}
					}
				}

			} catch (IOException e) {
				log.error("", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.error("", e);
					}
				}
			}
		}
	}
}
