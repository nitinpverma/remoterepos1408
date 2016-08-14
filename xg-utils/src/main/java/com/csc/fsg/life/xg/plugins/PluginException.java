package com.csc.fsg.life.xg.plugins;

import com.csc.fsg.life.exceptions.BaseException;

public class PluginException extends BaseException {

	private static final long serialVersionUID = 5157874074556818265L;

	public PluginException() {

		super();
	}

	public PluginException(Exception e) {

		super("" + e);
	}

	public PluginException(String s) {

		super(s);
	}
}