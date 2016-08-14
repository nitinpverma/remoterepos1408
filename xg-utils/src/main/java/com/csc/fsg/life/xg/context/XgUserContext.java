package com.csc.fsg.life.xg.context;

import com.csc.fsg.life.context.UserContext;
import com.csc.fsg.life.security.BaseSecurityManager;

/**
 * Duplicate of the class in xgd-web, exist here for testing purpose, it should be removed once the class in xgd-web is moved to xg-db.
 */
public class XgUserContext extends UserContext {

	private static final long serialVersionUID = 6864860090704004870L;
	private BaseSecurityManager securityManager;

	/**
	 * The Constructor.
	 * 
	 * @param securityManager the security manager
	 */
	public XgUserContext(BaseSecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csc.fsg.life.context.UserContext#getSecurityManager()
	 */
	public BaseSecurityManager getSecurityManager() {
		return securityManager;
	}
}
