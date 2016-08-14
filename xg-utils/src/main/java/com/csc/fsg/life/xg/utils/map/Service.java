package com.csc.fsg.life.xg.utils.map;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Service. Holds an Service info of xmlg-services.xml.
 */
public class Service {

	private String serviceRule;
	private List<String> maps = new ArrayList<String>();

	/**
	 * Instantiates a new service.
	 * 
	 * @param serviceRule the service rule
	 */
	public Service(String serviceRule) {
		this.serviceRule = serviceRule;
	}

	/**
	 * Gets the service rule.
	 * 
	 * @return the serviceRule
	 */
	public String getServiceRule() {
		return serviceRule;
	}

	/**
	 * Gets the maps.
	 * 
	 * @return the maps
	 */
	public List<String> getMaps() {
		return maps;
	}

	/**
	 * Adds the map.
	 * 
	 * @param map the map to add
	 */
	public void addMap(String map) {
		maps.add(map);
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
		sb.append("Service::[");
		sb.append(" serviceRule:=");
		sb.append(serviceRule);
		sb.append(" maps:=");
		sb.append(maps);
		sb.append("]");
		return sb.toString();
	}
}
