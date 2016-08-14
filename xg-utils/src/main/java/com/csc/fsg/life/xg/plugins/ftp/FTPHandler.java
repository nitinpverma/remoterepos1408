package com.csc.fsg.life.xg.plugins.ftp;

import com.csc.fsg.life.xg.plugins.*;

/**
 * @author Sujesh Menon
 * @version %G%
 * 
 *          This class implements IProcess object
 * 
 */

public class FTPHandler {

	private String mfdir = null;
	private String mfdirtype = null;

	private boolean DEBUG_RESPONSE = false;

	private int portnum = 0;
	private String host = null;
	private String username = null;
	private String pwd = null;
	private String port = null;
	private String mode = null;
	private String type = null;

	FTPClient ftpClient = null;

	public FTPHandler() {

	}

	/**
	 * @param Object obj
	 * 
	 */
	public void process(Object obj) throws PluginException {

		try {

			if (!(obj instanceof FTPByteArray))
				throw new PluginException(
						"Invalid object passed to the FTPHandler");

			FTPByteArray input = (FTPByteArray) obj;
			FTPConfig fc = new FTPConfig();

			// mfdir = fc.getMfDir();
			mfdir = input.getFileName(); // should come from output manager
			mfdirtype = fc.getMfDirType();

			host = fc.getHost();
			username = fc.getUsername();
			pwd = fc.getPwd();
			port = fc.getPort();
			mode = fc.getTransferMode();
			type = fc.getTransferType();

			if (host == null || username == null || pwd == null || port == null
					|| mode == null || type == null)
				throw new PluginException(
						"Nulls encountered in one or more Security/Transfer parameters OR Unable to read FTP Config File");

			try {
				Integer portNum = new Integer(port);
				portnum = portNum.intValue();
			} catch (Exception e) {
				e.printStackTrace();
				throw new PluginException(
						"Please specify a numeric value for the port address in the FTP Config file");
			}

			if (initialSetupOK()) {
				if (input.getByteArray() == null
						|| input.getByteArray().length == 0)
					throw new PluginException(
							"File you are trying to write out, is empty");

				String mfwdinquotes = "'" + mfdir + "'";

				try {
					ftpClient.chdir(mfwdinquotes);

				} catch (Exception e) {
					e.printStackTrace();
					throw new PluginException(
							"Unable to change working directory on mainframe");
				}

				if (!((mfdirtype.equalsIgnoreCase("PS")) || (mfdirtype.equalsIgnoreCase("PDS"))))
					throw new PluginException(
							"Only PS (Sequential) or PDS (Partitioned) mainframe datasets permissible at this point");

				uploadByteArray(input);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new PluginException(e);
		}

		finally {

			try {
				ftpClient.quit();
			} catch (Exception e) {
				e.printStackTrace();
				throw new PluginException("Error while trying to quit from FTP");
			}

		}

	}

	/**
	 * @return boolean This method finds out the truth value whether the host
	 *         name and port number from the FTPClient object are valid,
	 *         username and password are valid, FTP type...etc. . If invalid, an
	 *         exception will be thrown.
	 * 
	 * 
	 */

	private boolean initialSetupOK() throws PluginException {

		try {

			ftpClient = new FTPClient(host, portnum);

		} catch (Exception e) {

			e.printStackTrace();

			throw new PluginException(
					"Unable to connect to FTP Server, please verify the host and port number");

		}

		try {

			ftpClient.debugResponses(DEBUG_RESPONSE);

			ftpClient.login(username, pwd);

		} catch (Exception e) {

			e.printStackTrace();

			throw new PluginException(
					"Unable to connect to FTP Server, please verify the username and password");

		}

		try {
			if (type.equalsIgnoreCase("BINARY")) {

				ftpClient.setType(FTPTransferType.BINARY);
			} else if (type.equalsIgnoreCase("ASCII")) {
				ftpClient.setType(FTPTransferType.ASCII);
			} else {

				throw new PluginException("Unknown Transfer Type, "
						+ "Only BINARY or ASCII permissible at this time");

			}

		} catch (Exception e) {

			e.printStackTrace();

			throw new PluginException("Unable to set Transfer Type");

		}

		if (mode.equalsIgnoreCase("PASV")) {
			ftpClient.setConnectMode(FTPConnectMode.PASV);
			return true;

		} else if (mode.equalsIgnoreCase("ACTIVE")) {
			ftpClient.setConnectMode(FTPConnectMode.ACTIVE);
			return true;

		} else {

			throw new PluginException("Unknown Transfer mode, "
					+ "Only PASV or ACTIVE permissible at this time");

		}

	}

	/**
	 * @param FTPByteArray instance
	 * @exception PluginException This method upload file(in byte[] format) back
	 *            to mainframe.
	 * 
	 */

	private void uploadByteArray(FTPByteArray fileOutput)
			throws PluginException {

		String mfilename = null;

		if (mfdirtype.equalsIgnoreCase("PDS")) {
			mfilename = "'" + mfdir + "(" + fileOutput.getCopyBookName() + ")"
					+ "'";
			System.out.println("output dataset is a 'pds( A PARTITIONED DATASET )'");
		} else {
			mfilename = "'" + mfdir + "'";
			System.out.println("output dataset is a 'ps( A SEQUENTIAL DATASET )'");
		}

		try {

			System.out.println("putting copybook data to "
					+ mfilename.toUpperCase());
			ftpClient.put(fileOutput.getByteArray(), mfilename.toUpperCase(), true);
			// ftpClient.put(fileOutput.getByteArray(),
			// "'Z000747.XMLGATEW.COPYDATA'");
			System.out.println("FTP Successful");
			System.out.println("FTP Successful");

		} catch (Exception e) {
			System.out.println("Error dring FTP : " + e.getMessage());
			throw new PluginException("Unable to store file on mainframe");
		}

	}

}