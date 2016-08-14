package com.csc.fsg.life.xg.utils.map;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;
import javax.xml.transform.TransformerException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.csc.fsg.life.xg.copybook.install.CIFtpDriver;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.managers.CopyBookModelManager;
import com.csc.fsg.life.xg.serverutils.FileUtil;
import com.csc.fsg.life.xg.serverutils.RegExFilenameFilter;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.XGTreeModel;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBook;
import com.csc.fsg.life.xg.treenodes.XGTreeNode;
import com.csc.fsg.life.xg.ws.XMLUtils;
import com.sun.org.apache.xpath.internal.XPathAPI;

public class ReMapReporter {

	private static final String CPYEXT = ".CPY";
	private static final String COPYBOOKS_SOURCE_DIRECTORY = "L:\\WMA-06-20\\wmaCOBOL\\";
	private static final String COPYBOOKS_TARGET_DIRECTORY = "D:\\XMLG\\WMA\\Copybooks";
	private static final String MAPS_DIRECTORY = "C:\\XMLG\\WMA\\WORK";

	private static final String DATASOURCE_NODE = "//datasource";
	private static final String MAPELEMENT_NODE = "//mapElement";

	private static final String FILEPATH_ATTR = "filePath";
	private static final String TYPE_ATTR = "type";
	private static final String ISSOURCE_ATTR = "isSource";
	private static final String TARGETPATH_ATTR = "targetPath";

	private RegExFilenameFilter filter = null;
	private List copyBooksFound = new ArrayList();
	private Map mapxCopybooks = new HashMap();

	public void printReport() throws Exception {

		// List copyBooksList = listCopybooksFromMaps();
		// printMapxCopybooks(mapxCopybooks);
		//              
		// filter = new RegularFilenameFilter(copyBooksList);
		// locateCopyBooks(new File(COPYBOOKS_SOURCE_DIRECTORY));
		//        
		// printUnavilableCopybooks(copyBooksList);
		//      
		// loadCopyBooksIntoCIP(new File(COPYBOOKS_TARGET_DIRECTORY));
		searchMissingPaths();

	}

	private void printUnavilableCopybooks(List copyBooksList) {
		for (int i = 0; i < copyBooksList.size(); i++) {
			String cpName = (String) copyBooksList.get(i);
			if (!copyBooksFound.contains(cpName))
				System.out.println(cpName + " not found ");
		}
	}

	private List listCopybooksFromMaps() throws TransformerException {
		List filesList = readMapsDirectory();

		List copyBooksList = new ArrayList();

		for (int fList = 0; fList < filesList.size(); fList++) {
			ArrayList clist = new ArrayList();

			Document doc = XMLUtils.buildDocument(MAPS_DIRECTORY + "\\"
					+ filesList.get(fList) + "");
			NodeList list = XPathAPI.selectNodeList(doc, DATASOURCE_NODE);
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				String type = XMLUtils.getAttributeValue(node, TYPE_ATTR);
				if (type.equalsIgnoreCase("C")) {
					String cpName = XMLUtils.getAttributeValue(node, FILEPATH_ATTR);
					if (!clist.contains(cpName.trim() + CPYEXT))
						clist.add(cpName.trim() + CPYEXT);
					if (!copyBooksList.contains(cpName.trim() + CPYEXT))
						copyBooksList.add(cpName.trim() + CPYEXT);
				}
			}

			mapxCopybooks.put(filesList.get(fList), clist);
		}
		return copyBooksList;
	}

	private void printMapxCopybooks(Map map) {
		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String mapName = (String) iter.next();
			System.out.println("\n" + mapName);
			ArrayList cpbooks = (ArrayList) map.get(mapName);
			for (int i = 0; i < cpbooks.size(); i++) {
				System.out.print(cpbooks.get(i) + "\t");
			}
		}
	}

	private void searchMissingPaths() throws Exception {
		List filesList = readMapsDirectory();
		StringBuffer missingNodesReport = new StringBuffer();
		StringBuffer newNonLeafNodesReport = new StringBuffer();

		Hashtable cpNamexPathsCache = new Hashtable();

		for (int fList = 0; fList < filesList.size(); fList++) {
			missingNodesReport.setLength(0);
			newNonLeafNodesReport.setLength(0);

			boolean isCopyBookOnSourceSide = false;
			String mapName = (String) filesList.get(fList);
			// System.out.println("**Processing "+ mapName +"**");
			Document doc = XMLUtils.buildDocument(MAPS_DIRECTORY + "\\"
					+ mapName + "");
			NodeList list = XPathAPI.selectNodeList(doc, DATASOURCE_NODE);

			ArrayList clist = new ArrayList();

			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				String type = XMLUtils.getAttributeValue(node, TYPE_ATTR);
				if (type.equalsIgnoreCase("C")) {
					isCopyBookOnSourceSide = XMLUtils.getAttributeValue(node, ISSOURCE_ATTR).equalsIgnoreCase("Y");
					String cpName = XMLUtils.getAttributeValue(node, FILEPATH_ATTR);
					if (!clist.contains(cpName.trim() + CPYEXT))
						clist.add(cpName.trim() + CPYEXT);
				}
			}

			if (clist.isEmpty())
				continue;

			// Get only the copybook paths which are not in cache
			ArrayList cpPathsToget = new ArrayList();
			for (int index = 0; index < clist.size(); index++) {
				String cpname = (String) clist.get(index);
				if (!cpNamexPathsCache.containsKey(cpname))
					cpPathsToget.add(cpname);
			}

			// put the new copy book paths in cache
			Map cpNamexPaths = getCopyBookPaths(cpPathsToget);
			cpNamexPathsCache.putAll(cpNamexPaths);

			// prepare the paths to search-Only the paths from copybooks which
			// are in the current map
			HashMap currentSearchPaths = new HashMap();
			Iterator cpIter = clist.iterator();
			while (cpIter.hasNext()) {
				String cname = (String) cpIter.next();
				currentSearchPaths.putAll((Hashtable) cpNamexPathsCache.get(cname.substring(0, cname.indexOf("."))));
			}

			if (currentSearchPaths.isEmpty())
				continue;

			list = XPathAPI.selectNodeList(doc, MAPELEMENT_NODE);

			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				HashMap pathToCheckAL = new HashMap();
				String pathToCheck = null;
				NodeList cnodes = node.getChildNodes();
				String srcType = null;
				for (int k = 0; k < cnodes.getLength(); k++) {
					Node cnode = cnodes.item(k);
					if (cnode.getNodeName().equalsIgnoreCase("srcElement")) {
						srcType = XMLUtils.getAttributeValue(cnode, "type");
						if (isCopyBookOnSourceSide) {
							if (srcType.equalsIgnoreCase("P")) {
								pathToCheck = XMLUtils.getAttributeValue(cnode, "sourcePath");
								pathToCheckAL.put(pathToCheck, srcType);
							}
						}
					}
				}

				if (!isCopyBookOnSourceSide) {
					pathToCheck = XMLUtils.getAttributeValue(node, TARGETPATH_ATTR);
					pathToCheckAL.put(pathToCheck, srcType);
				}
				if (!pathToCheckAL.isEmpty()) {
					for (Iterator iter = pathToCheckAL.keySet().iterator(); iter.hasNext();) {
						String eachPath = (String) iter.next();
						StringBuffer finalPath = new StringBuffer();
						update(eachPath, finalPath);

						if (!currentSearchPaths.containsKey(finalPath.toString())) {
							missingNodesReport.append(finalPath.toString()
									+ "\n");
							// printRulesNFunctions(node,report);
						} else {
							Object tp = currentSearchPaths.get(finalPath.toString());
							if (tp instanceof TreePath) {
								XGTreeNode treeNode = (XGTreeNode) ((TreePath) tp).getLastPathComponent();
								if (!treeNode.isLeaf()) {
									Object temp = pathToCheckAL.get(eachPath);
									if (temp == null)
										System.out.println("Error:" + eachPath);
									if (temp.toString().equalsIgnoreCase("P"))
										newNonLeafNodesReport.append(finalPath.toString()
												+ "\n");
								}
							} else {
								ArrayList al = (ArrayList) tp;
								for (Iterator iterator = al.iterator(); iterator.hasNext();) {
									TreePath element = (TreePath) iterator.next();
									XGTreeNode treeNode = (XGTreeNode) element.getLastPathComponent();
									if (!treeNode.isLeaf()) {
										Object temp = pathToCheckAL.get(eachPath);
										if (temp == null)
											System.out.println("Error:"
													+ eachPath);
										if (temp.toString().equalsIgnoreCase("P"))
											newNonLeafNodesReport.append(finalPath.toString()
													+ "\n");
									}
								}
							}
						}
					}
				}

			}

			if (missingNodesReport.length() > 0
					|| newNonLeafNodesReport.length() > 0) {

				System.out.println("--" + mapName + "---");
				// System.out.println("isCopyBookOnSourceSide
				// "+isCopyBookOnSourceSide);
				System.out.println("-- Missing Elements ---");
				System.out.println(missingNodesReport.toString());
				// System.out.println("-- New Non Leaf Elements --");
				// System.out.println(newNonLeafNodesReport.toString());
			}

			currentSearchPaths = null;
			doc = null;
			System.gc();
		}

		cpNamexPathsCache = null;
		System.gc();
	}

	/**
	 * @param pathToCheck
	 * @return
	 */
	private void update(String pathToCheck, StringBuffer finalPath) {
		if (pathToCheck == null) {
			System.out.println("ERROR********" + finalPath);
			;
			return;
		}
		int index = pathToCheck.indexOf("(");
		if (index != -1) {
			String occrs = pathToCheck.substring(index + 1, pathToCheck.indexOf(")"));
			if (occrs.toCharArray().length == 3)
				occrs = "001";
			if (occrs.toCharArray().length == 2)
				occrs = "00";
			finalPath.append(pathToCheck.substring(0, index + 1)).append(occrs).append(")");
			pathToCheck = pathToCheck.substring(pathToCheck.indexOf(")") + 1);
			update(pathToCheck, finalPath);
		} else {
			finalPath.append(pathToCheck);
		}
	}

	private void printRulesNFunctions(Node node, StringBuffer sb) {
		NodeList cnodes = node.getChildNodes();
		for (int k = 0; k < cnodes.getLength(); k++) {
			Node cnode = cnodes.item(k);
			if (cnode.getNodeName().equalsIgnoreCase("rule")
					|| cnode.getNodeName().equalsIgnoreCase("function")) {
				String label = XMLUtils.getAttributeValue(cnode, "label");
				String type = XMLUtils.getAttributeValue(cnode, "type");
				sb.append(type + "-" + label + "\t");
			}
		}
	}

	private Map getCopyBookPaths(List cpNames) throws Exception {

		Map hash = new HashMap();
		CopyBook cb = null;
		for (int i = 0; i < cpNames.size(); i++) {
			cb = new CopyBook();
			String cpName = (String) cpNames.get(i);
			cpName = cpName.substring(0, (cpName.indexOf(".")));
			cb.setName(cpName);
			cb.setSystem("WMA");
			cb.setAscii(true);
			XGTreeModel model = (XGTreeModel) CopyBookModelManager.getInstance("WMA").getModel(cb);
			hash.put(cb.getName(), model.getAllPathsWithOccurs());
		}
		return hash;
	}

	private void loadCopyBooksIntoCIP(File file) throws Exception {
		CIFtpDriver ciDriver = new CIFtpDriver("WMA");
		ciDriver.processCopybookListFromPC(file, "WMA");
	}

	private void locateCopyBooks(File dir) throws Exception {
		locateInDir(dir);
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
				locateCopyBooks(new File(dir, children[i]));
		}
	}

	private void locateInDir(File dir) throws Exception {
		File[] paths = dir.listFiles(filter);
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				FileUtil.copy(paths[i].getAbsoluteFile(), new File(
						COPYBOOKS_TARGET_DIRECTORY + paths[i].getName()));
				copyBooksFound.add(paths[i].getName());
			}
		}
	}

	private void printAllAttr(Node node) {
		NamedNodeMap attrList = node.getAttributes();
		for (int j = 0; j < attrList.getLength(); j++) {
			Node attrNode = attrList.item(j);
			String attrName = attrNode.getNodeName();
			String attrValue = attrNode.getNodeValue();
			System.out.println(attrName + "-" + attrValue + "\t");
		}
	}

	private static List readMapsDirectory() {
		List filesList = new ArrayList();
		File folder = new File(MAPS_DIRECTORY);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile())
				filesList.add(listOfFiles[i].getName());
			else if (listOfFiles[i].isDirectory()) {
				// out.write("Directory " + listOfFiles[i].getName());
			}
		}
		return filesList;
	}

	private ArrayList getCopyBooksForMap(String mapName,
			boolean isCopyBookOnSourceSide) throws Exception {
		Document doc = XMLUtils.buildDocument(MAPS_DIRECTORY + "\\" + mapName
				+ "");
		NodeList list = XPathAPI.selectNodeList(doc, DATASOURCE_NODE);

		ArrayList clist = new ArrayList();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			isCopyBookOnSourceSide = XMLUtils.getAttributeValue(node, ISSOURCE_ATTR).equalsIgnoreCase("Y");
			String type = XMLUtils.getAttributeValue(node, TYPE_ATTR);
			if (type.equalsIgnoreCase("C")) {
				String cpName = XMLUtils.getAttributeValue(node, FILEPATH_ATTR);
				if (!clist.contains(cpName.trim() + CPYEXT))
					clist.add(cpName.trim() + CPYEXT);
			}
		}
		return clist;
	}

	public static void main(String[] args) {
		try {
			// StringBuffer sb = new StringBuffer();
			// new
			// ReMapReporter().update("CIUAFOE8(02)/TRX-INFO/FUND-INFO-TEXT/FUND-INFO(002)/FUND-NUMBER",
			// sb);
			// System.out.println(sb.toString());

			ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
					new String[] { "config/xmlg-config.xml",
							"config/jndi-config.xml" });
			EnvironmentFactory.setAppContext(appContext);
			EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
			EnvironmentInitializer.init(null);

			new ReMapReporter().printReport();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
