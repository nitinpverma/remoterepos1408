package com.csc.fsg.life.xg.plugins.ftp;

/**
 * 
 * @author Sujesh Menon
 * @version %G%
 * 
 *          This class enumerates the connect modes that are possible, active &
 *          PASV.
 * 
 */
public class FTPConnectMode {

	/**
	 * Represents active connect mode
	 */
	public static FTPConnectMode ACTIVE = new FTPConnectMode();

	/**
	 * Represents PASV connect mode
	 */
	public static FTPConnectMode PASV = new FTPConnectMode();

	/**
	 * Private so no-one else can instantiate this class
	 */
	private FTPConnectMode() {
	}
}
