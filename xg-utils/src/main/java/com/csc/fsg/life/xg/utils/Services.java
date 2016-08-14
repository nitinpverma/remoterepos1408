package com.csc.fsg.life.xg.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.ws.ServiceConfigBean;

/**
 * Prints info about web services like maps used in a web service or list of ACORD
 * web services, list of request maps etc...
 */
public class Services {

	private static Log log = LogFactory.getLog(Services.class);

	private static ApplicationContext context;
	static {

		ConfigLoader.initRuntimeConfig();
		context = EnvironmentFactory.getAppContext();
	}

	static class SCBByRequestMapComparator implements
			Comparator<ServiceConfigBean> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(ServiceConfigBean scb1, ServiceConfigBean scb2) {

			return scb1.getRequestMap().compareTo(scb2.getRequestMap());
		}
	}

	static class SCBByAcordServiceComparator implements
			Comparator<ServiceConfigBean> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(ServiceConfigBean scb1, ServiceConfigBean scb2) {
			return scb1.getEventID().compareTo(scb2.getEventID());
		}
	}

	public static void main(String[] args) {

		printServicesInfo();
		printRequestMaps();
		printAcordServices();
	}

	/**
	 * Prints the all configured web services with service rule, maps etc...
	 */
	public static void printServicesInfo() {
		Map<String, List<ServiceConfigBean>> serviceNames = new TreeMap<String, List<ServiceConfigBean>>();
		String[] serviceBeanNames = context
				.getBeanNamesForType(ServiceConfigBean.class);
		for (String sBeanName : serviceBeanNames) {
			ServiceConfigBean serviceBean = (ServiceConfigBean) context
					.getBean(sBeanName);
			String serviceEventID = serviceBean.getEventID();
			List<ServiceConfigBean> serviceList = serviceNames
					.get(serviceEventID);
			if (serviceList == null) {
				serviceList = new ArrayList<ServiceConfigBean>();
				serviceNames.put(serviceEventID, serviceList);
			}
			serviceList.add(serviceBean);
		}
		Set<Entry<String, List<ServiceConfigBean>>> entrySet = serviceNames
				.entrySet();
		for (Entry<String, List<ServiceConfigBean>> entry : entrySet) {

			log.info(entry.getKey());
			for (ServiceConfigBean scb : entry.getValue()) {

				log.info("\tServiceRul:" + scb.getServiceRule());
				log.info("\tRequest Map:" + scb.getRequestMap());
				if (scb.getResponseMap() != null) {
					log.info("\tResponse Map:" + scb.getResponseMap());
				}
				log.info("");
			}
		}
		log.info("----------------");
	}

	/**
	 * Prints the all Request maps configured in web services.
	 */
	public static void printRequestMaps() {
		Map<String, ServiceConfigBean> beans = context
				.getBeansOfType(ServiceConfigBean.class);
		Collection<ServiceConfigBean> values = beans.values();
		Set<String> reqMaps = new TreeSet<String>();
		for (ServiceConfigBean serviceConfigBean : values) {
			reqMaps.add(serviceConfigBean.getRequestMap());
		}
		for (String mapName : reqMaps) {
			log.info(mapName);
		}
		log.info("----------------");
	}

	/**
	 * Prints the all ACORD web service names.
	 */
	public static void printAcordServices() {
		Map<String, ServiceConfigBean> beans = context
				.getBeansOfType(ServiceConfigBean.class);
		Collection<ServiceConfigBean> values = beans.values();
		Set<String> acordServices = new TreeSet<String>();
		for (ServiceConfigBean serviceConfigBean : values) {
			acordServices.add(serviceConfigBean.getEventID());
		}
		for (String serviceName : acordServices) {
			log.info(serviceName);
		}
		log.info("----------------");
	}
}
