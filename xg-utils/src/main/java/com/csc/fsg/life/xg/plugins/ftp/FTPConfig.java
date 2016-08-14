package com.csc.fsg.life.xg.plugins.ftp;

import com.csc.fsg.life.xg.plugins.*;

/**
 * FTPConfig class This class uses the Configuration Toolkit to access the user
 * and system configuration files. It requests information by passing the scope
 * (section names) and pattern (keys or literals) to the configuration toolkit.
 * Creation date: (10/25/01 11:50:00 AM)
 * 
 * @author Sujesh Menon , Debashish Das
 * @version %G%
 */

public class FTPConfig {

	public static final String FTP_SECURITY_TARGET = "INS1";

	private static final String mfdir = "Z000747.ANNPAY.COPYDATA";
	private static final String mfdirtype = "PS";

	// The constructor instantiates the ftp by passing the ftp config filenames.

	public FTPConfig() {
	}

	/**
	 * @return String[]
	 * @exception PluginException This method returns an array of mainframe
	 *            files, if there are no mainframe datasets specified in the FTP
	 *            config. file, then throw an exception.
	 * 
	 * */
	public String getMfDir() {

		return mfdir;

	}

	/**
	 * @return String []
	 * @exception PluginException This method returns an array of mainframe
	 *            directory. If there are no value specifiy in the FTP config
	 *            file, then throw an exception.
	 * 
	 * */
	public String getMfDirType() {
		return mfdirtype;
	}

	/**
	 * @return String
	 * @exception PluginException This method returns a string representation of
	 *            the security information(host..etc.) that stored under the
	 *            config files. If no host info. was found under the FTP Config
	 *            file, an exception will be thrown.
	 * 
	 * */

	public String getHost() {

		return "sd-ins1";
	}

	/**
	 * @return String
	 * @exception PluginException This method returns a string - username. If no
	 *            username found in the config. file, then throw an exception.
	 * 
	 * */

	public String getUsername() {

		return "Z000747";
	}

	/**
	 * @return String
	 * @exception PluginException This method returns a String - password If no
	 *            password info. found under the config file, then throw an
	 *            exception.
	 * 
	 * */
	public String getPwd() {
		return "XXX";

	}

	/**
	 * @return String
	 * @exception PluginException This method obtains port info. specify from
	 *            FTP config file. If no port info. found from config. file,
	 *            throw an exception.
	 * 
	 * */

	public String getPort() {
		return "21";
	}

	/**
	 * @return String
	 * @exception PluginException This method returns a string representation of
	 *            the transfer mode that obtain from FTP Config file. If
	 *            transfer mode not found from config. file, then an exception
	 *            will be thrown.
	 * 
	 * */
	public String getTransferMode() {
		return "PASV";
	}

	/**
	 * @return String
	 * @exception PluginException This method returs transfer type info. If
	 *            transfer type is not found in config file, then an exception
	 *            will be thrown.
	 * 
	 * */
	public String getTransferType() {
		return "BINARY";

	}

}