package com.csc.fsg.life.xg.plugins.ftp;

/**
 * @author Sujesh Menon
 * @version %G%
 * 
 *          This class enumerates the transfer types possible. We support only
 *          the two common types, ASCII and Image (often called binary).
 * 
 */
public class FTPTransferType {

	/**
	 * Represents ASCII transfer type
	 */
	public static FTPTransferType ASCII = new FTPTransferType();

	/**
	 * Represents Image (or binary) transfer type
	 */
	public static FTPTransferType BINARY = new FTPTransferType();

	/**
	 * The char sent to the server to set ASCII
	 */
	static String ASCII_CHAR = "A";

	/**
	 * The char sent to the server to set BINARY
	 */
	static String BINARY_CHAR = "I";

	/**
	 * Private so no-one else can instantiate this class
	 */
	private FTPTransferType() {
	}
}
