package com.csc.fsg.life.xg.utils.map;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Event. Holds an Event info of xmlg-services.xml.
 */
public class Event {

	private String name;
	// service rules/products
	private List<Service> services = new ArrayList<Service>();

	/**
	 * Instantiates a new event.
	 * 
	 * @param name the name
	 */
	public Event(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the services.
	 * 
	 * @return the products
	 */
	public List<Service> getServices() {
		return services;
	}

	/**
	 * Adds the service.
	 * 
	 * @param service the product to add
	 */
	public void addService(Service service) {
		services.add(service);
	}

	/**
	 * To string.
	 * 
	 * @return String returns this object in a String
	 * 
	 * @overwrite toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Event::[");
		sb.append(" name:=");
		sb.append(name);
		sb.append(" products:=");
		sb.append(services);
		sb.append("]");
		return sb.toString();
	}
}
