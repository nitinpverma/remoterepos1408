package com.csc.fsg.life.xg.plugins.ftp;

/**
 * This class extends the JAVA Exception object FTP specific exceptions
 * 
 * @author Sujesh Menon
 */
public class FTPException extends Exception {

	private static final long serialVersionUID = -6282495595231009873L;
	/**
	 * Integer reply code
	 */
	private int replyCode = -1;

	/**
	 * Constructor. Delegates to super.
	 * 
	 * @param msg Message that the user will be able to retrieve
	 */
	public FTPException(String msg) {
		super(msg);
	}

	/**
	 * Constructor. Permits setting of reply code
	 * 
	 * @param msg message that the user will be able to retrieve
	 * @param replyCode string form of reply code
	 */
	public FTPException(String msg, String replyCode) {

		super(msg);

		// extract reply code if possible
		try {
			this.replyCode = Integer.parseInt(replyCode);
		} catch (NumberFormatException ex) {
			this.replyCode = -1;
		}
	}

	/**
	 * Get the reply code if it exists
	 * 
	 * @return reply if it exists, -1 otherwise
	 */
	public int getReplyCode() {
		return replyCode;
	}
}
