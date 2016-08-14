package com.csc.fsg.life.xg.plugins.ftp;

import java.io.*;
import java.net.*;

/**
 * 
 * @author Sujesh Menon
 * @version %G%
 * 
 *          Supports client-side FTP DataSocket in Passive and Active Mode.
 *          Wrapper for Socket and ServerSocket. Methods are package access only
 *          - not for public use.
 * 
 */

public class FTPDataSocket {

	/**
	 * The underlying socket for Active connection.
	 */
	private ServerSocket activeSocket = null;

	/**
	 * The underlying socket for PASV connection or Socket acepted from server.
	 */
	private Socket passiveSocket = null;

	/**
	 * @param ServerSocket s Create socket wrapper for Active connection.
	 */
	FTPDataSocket(ServerSocket s) {
		activeSocket = s;
	}

	/**
	 * @param Socket s Create socket pper for PASV connection.
	 * 
	 */
	FTPDataSocket(Socket s) {
		passiveSocket = s;
	}

	/**
	 * Set the TCP timeout on the underlying control socket.
	 * 
	 * If a timeout is set, then any operation which takes longer than the
	 * timeout value will be killed with a java.io.InterruptedException.
	 * 
	 * @param millis The length of the timeout, in milliseconds
	 */
	void setTimeout(int millis) throws IOException {

		if (passiveSocket != null)
			passiveSocket.setSoTimeout(millis);
		else if (activeSocket != null)
			activeSocket.setSoTimeout(millis);
	}

	/**
	 * If active mode, accepts the FTP server's connection - in PASV, we are
	 * already connected. Then gets the output stream of the connection
	 * 
	 * @return output stream for underlying socket.
	 */
	OutputStream getOutputStream() throws IOException {

		if (passiveSocket != null) {
			return passiveSocket.getOutputStream();
		}
		// accept socket from server, in Active mode
		passiveSocket = activeSocket.accept();
		// get and return its OutputStream
		return passiveSocket.getOutputStream();
	}

	/**
	 * If active mode, accepts the FTP server's connection - in PASV, we are
	 * already connected. Then gets the input stream of the connection
	 * 
	 * @return input stream for underlying socket.
	 */
	InputStream getInputStream() throws IOException {

		if (passiveSocket != null) {
			return passiveSocket.getInputStream();
		}
		// accept socket from server, in Active mode
		passiveSocket = activeSocket.accept();
		// get and return it's InputStream
		return passiveSocket.getInputStream();
	}

	/**
	 * Closes underlying sockets.
	 */
	void close() throws IOException {

		if (passiveSocket != null)
			passiveSocket.close();
		if (activeSocket != null)
			activeSocket.close();
	}
}