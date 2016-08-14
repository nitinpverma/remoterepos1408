package com.csc.fsg.life.xg.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author mgandham
 */
public class DBTest {

	private static final String SCHEMA = "VCSTST58";
	private Connection conn = null;
	private Statement stmt = null;
	static {
		try {
			Class driverClass = Class.forName("com.ibm.db2.jcc.DB2Driver");
			System.out.println("Driver loaded: " + driverClass);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	DBTest() {
		try {
			conn = DriverManager.getConnection("jdbc:db2:DB2G", "DB2XUSR", "ANCHOR");
			stmt = conn.createStatement();
		} catch (SQLException e) {
			System.out.println("Connection could not be created: "
					+ e.getMessage());
			System.exit(0);
		}
		System.out.println("Initialized");
	}

	DBTest(boolean type4) {
		try {
			conn = DriverManager.getConnection("jdbc:db2://20.17.189.71:5000/DB2G", "DB2XUSR", "ANCHOR");
			stmt = conn.createStatement();
		} catch (SQLException e) {
			System.out.println("Connection could not be created: "
					+ e.getMessage());
			System.exit(0);
		}
		System.out.println("Initialized");
	}

	private void test() throws Exception {
		try {
			String sql = "SELECT XGID,XGLABEL FROM VCSTST58.XGFUNC WHERE XGID < 1000 FOR UPDATE";
			ResultSet resultSet = stmt.executeQuery(sql);
			Statement stmt2 = conn.createStatement();
			String sql2 = null;
			while (resultSet.next()) {
				if (sql2 == null) {
					sql2 = "SELECT XGID,XGLABEL FROM VCSTST58.XGFUNC WHERE XGID < 100 FOR UPDATE";
					ResultSet resultSet2 = stmt2.executeQuery(sql2);
					resultSet2.next();
					System.out.println(resultSet2.getString(1) + ":"
							+ resultSet2.getString(2));
				}
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public static void main(String[] args) {
		try {
			DBTest tester = new DBTest(true);
			tester.test();
			tester.cleanUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cleanUp() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Statement could not be closed: "
						+ e.getMessage());
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Connection could not be closed: "
						+ e.getMessage());
			}
		}
		System.out.println("CleanUp complete.");
	}

}
