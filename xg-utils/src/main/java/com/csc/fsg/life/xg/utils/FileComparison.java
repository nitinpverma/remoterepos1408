package com.csc.fsg.life.xg.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FileComparison. Provides basic line compare in two files.
 */
public class FileComparison {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		List<String> file1Lines = getLines(args[0]);
		List<String> file2Lines = getLines(args[1]);
		for (String file1Line : file1Lines) {
			for (String file2Line : file2Lines) {
				if (file1Line.equalsIgnoreCase(file2Line)) {
					System.out.println(file1Line);
				}
			}
		}
	}

	private static List<String> getLines(String fileName) {

		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					lines.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return lines;
	}
}
