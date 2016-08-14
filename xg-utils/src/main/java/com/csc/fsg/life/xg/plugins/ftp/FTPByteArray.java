package com.csc.fsg.life.xg.plugins.ftp;

public class FTPByteArray {

	private byte[] byteArray = null;
	private String fileName = null;
	private String copyBookName = null;

	/**
	 * Constructor for FTPByteArray
	 */
	public FTPByteArray() {
		super();
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public byte[] getByteArray() {
		return this.byteArray;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setCopyBookName(String copyBookName) {
		this.copyBookName = copyBookName;
	}

	public String getCopyBookName() {
		return this.copyBookName;
	}

}
