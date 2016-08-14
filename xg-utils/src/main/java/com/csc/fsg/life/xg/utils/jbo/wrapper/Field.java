package com.csc.fsg.life.xg.utils.jbo.wrapper;

/**
 * The Class Field of a Wrapper Class.
 */
public class Field {

	protected String name;
	private String type;
	private boolean setter;
	private boolean getter;
	private boolean ignore;

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Checks if is setter.
	 * 
	 * @return the isSetter
	 */
	public boolean isSetter() {
		return setter;
	}

	/**
	 * Sets the setter.
	 * 
	 * @param isSetter the isSetter to set
	 */
	public void setSetter(boolean isSetter) {
		this.setter = isSetter;
	}

	/**
	 * Checks if is getter.
	 * 
	 * @return the isGetter
	 */
	public boolean isGetter() {
		return getter;
	}

	/**
	 * Sets the getter.
	 * 
	 * @param isGetter the isGetter to set
	 */
	public void setGetter(boolean isGetter) {
		this.getter = isGetter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return name;
	}

	/**
	 * Returns <code>true</code> if this <code>Field</code> is the same as the o
	 * argument.
	 * 
	 * @param o the o
	 * 
	 * @return <code>true</code> if this <code>Field</code> is the same as the o
	 *         argument.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		Field castedObj = (Field) o;
		return name == null ? castedObj.name == null
				: this.name.equals(castedObj.name);
	}

	/**
	 * Checks if is ignore.
	 * 
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * Sets the ignore.
	 * 
	 * @param ignore the ignore to set
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
}
