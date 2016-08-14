package com.csc.fsg.life.xg.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csc.fsg.life.xg.serverutils.FileUtil;

/**
 * Generic search functionalities.
 */
public class Search {

	/**
	 * Checks if searchString is used in file.
	 *
	 * @param fileName the file
	 * @param searchStr the search string
	 * @return true, if is used in file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean isUsedInFile(String fileName, String searchStr) throws IOException {

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String line = null;
			Pattern pattern = Pattern.compile(searchStr, Pattern.CASE_INSENSITIVE);
			while ((line = br.readLine()) != null) {

				Matcher matcher = pattern.matcher(line);
				boolean found = matcher.find();
				if (found) {
					return true;
				}
			}
		} finally {
			FileUtil.safeClose(br);
			FileUtil.safeClose(fr);
		}
		return false;
	}

	/**
	 * Finds all lines where the search string is used.
	 *
	 * @param file the file
	 * @param searchStr the search string
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static List<String> find(String file, String searchStr) throws IOException {

		List<String> searchResults = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;
			Pattern pattern = Pattern.compile(searchStr, Pattern.CASE_INSENSITIVE);
			while ((line = br.readLine()) != null) {

				Matcher matcher = pattern.matcher(line);
				boolean found = matcher.find();
				if (found) {
					searchResults.add(line);
				}
			}
		} finally {
			FileUtil.safeClose(br);
			FileUtil.safeClose(fr);
		}
		return searchResults;
	}
}
