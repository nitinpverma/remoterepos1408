package com.csc.fsg.life.xg.utils;

/**
 * @author mgandham
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.csc.fsg.life.xg.controller.CopyBookController;
import com.csc.fsg.life.xg.copybook.translator.tree.template.CopyBookDataModel;
import com.csc.fsg.life.xg.copybook.translator.tree.template.CopyBookDataNode;
import com.csc.fsg.life.xg.copybook.translator.tree.template.TemplateNode;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.managers.SchemaModelManager;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.XGTreeModel;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBook;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBookTreeModel;
import com.csc.fsg.life.xg.treemodels.schema.Schema;
import com.csc.fsg.life.xg.treemodels.schema.SchemaTreeModel;
import com.csc.fsg.life.xg.treemodels.schema.XMLTreeModel;
import com.csc.fsg.life.xg.treenodes.schema.RootTypeNode;
import com.csc.fsg.life.xg.treenodes.schema.ValueMutableTreeNode;
import com.csc.fsg.life.xg.treenodes.schema.XMLNode;

public class ModelTester extends JFrame implements ActionListener,
		TreeSelectionListener {

	private static final long serialVersionUID = 3411300144076852364L;
	private JTree currentTree = null;
	private String envKey = "WMA";

	public ModelTester() {
		setSize(600, 600);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
	}

	public XGTreeModel getCopyBookDataModel(CopyBook cpy) {
		try {
			CopyBookController instance = CopyBookController.getInstance(envKey);
			CopyBookDataModel model = instance.getTreeWithData(cpy, null);

			return model;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public XGTreeModel getCopyBookDataModel(CopyBook cpy, File f) {
		try {

			FileInputStream fis = new FileInputStream(f);

			byte[] b = new byte[fis.available()];
			for (int i = 0; i < b.length; i++) {
				b[i] = (byte) fis.read();
			}

			cpy.setAscii(false);
			CopyBookController instance = CopyBookController.getInstance(envKey);
			CopyBookDataModel model = instance.getTreeWithData(cpy, b);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public XGTreeModel getCopyBookModel(CopyBook cpy) {
		try {

			CopyBookController instance = CopyBookController.getInstance(envKey);
			CopyBookTreeModel model = (CopyBookTreeModel) instance.getCopyBookTreeModel(cpy);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public XGTreeModel getSchemaModel(Schema s) {
		try {
			SchemaModelManager instance = SchemaModelManager.getInstance(envKey);
			RootTypeNode rtn = (RootTypeNode) instance.getModel(s);
			SchemaTreeModel model = new SchemaTreeModel(rtn.getValueNode(null));

			return model;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Document parseXmlFile(String filename, boolean validating) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);

			// Create the builder and parse the file
			Document doc = factory.newDocumentBuilder().parse(new File(filename));
			return doc;
		} catch (SAXException e) {
			// A parsing error occurred; the xml input is not valid
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
		return null;
	}

	public XGTreeModel getModelFromXML(File f) {
		try {

			Document domDoc = parseXmlFile(f.getAbsolutePath(), false);
			XMLNode xmlRootNode = new XMLNode(domDoc.getDocumentElement());
			XMLTreeModel docModel = new XMLTreeModel(xmlRootNode);

			// Document d = (Document)model.getOutput();
			return docModel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private XGTreeModel expandModel(XGTreeModel schemaModel) {
		expandNode((ValueMutableTreeNode) schemaModel.getRoot());
		return schemaModel;
	}

	private void expandNode(ValueMutableTreeNode node) {
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				expandNode((ValueMutableTreeNode) node.getChildAt(i));
			}
		}
		String maxOccrs = node.getMaxOccursString();
		int expandCount = 0;
		if (maxOccrs.equals(" unbounded "))
			expandCount = 2;
		else {
			try {
				expandCount = Integer.parseInt(maxOccrs);
				if (expandCount == 0)
					return;
			} catch (Exception e) {
				// e.printStackTrace();
				expandCount = 0;
			}
		}
		if (expandCount > 1) {
			ValueMutableTreeNode parent = (ValueMutableTreeNode) node.getParent();
			for (int i = 0; i < expandCount; i++) {
				parent.add((ValueMutableTreeNode) node.clone());
			}
		}
	}

	private boolean showParent = true;

	public static void main(String[] args) {
		try {

			ConfigLoader.initDesignerConfig();
			ModelTester tf = new ModelTester();

			Schema s = new Schema();
			s.setName("TXLife2.8.92.xsd");
			s.setRoot("TXLife");
			s.setSystem("WMA");
			final XGTreeModel model = tf.getSchemaModel(s);

			// File xmlFile = new File("");
			// final XGTreeModel model = tf.getModelFromXML(xmlFile);
			//
			// CopyBook c = new CopyBook();
			// c.setSystem("WMA");
			// c.setIdentifier("WMA-CIUAFNA1");
			// c.setName("CIUAFNA1");
			// File dataFile = new File("c:\\temp\\eventImage.dat");
			// final XGTreeModel model = tf.getCopyBookDataModel(c, dataFile);
			// final XGTreeModel model = tf.getCopyBookDataModel(c);

			tf.show(model);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void show(XGTreeModel model) {
		currentTree = new JTree(model);
		currentTree.setCellRenderer(new ColoredNodeRenderer());
		currentTree.addTreeSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(currentTree);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JPanel sidePanel = new JPanel();
		getContentPane().add(sidePanel, BorderLayout.EAST);
		JButton button = new JButton("Hide/Show Parent");
		button.addActionListener(this);

		sidePanel.add(button);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
	}

	public void valueChanged(TreeSelectionEvent tse) {
		if (currentTree.getModel() instanceof SchemaTreeModel) {
			ValueMutableTreeNode currentNode = (ValueMutableTreeNode) (tse.getNewLeadSelectionPath().getLastPathComponent());
			List<String> list = currentNode.getEnumList();

			if (list == null)
				return;
			for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
				Object element = iter.next();
				System.out.println("" + element);
			}
		}
		if (currentTree.getModel() instanceof CopyBookDataModel) {
			CopyBookDataNode currentNode = (CopyBookDataNode) (tse.getNewLeadSelectionPath().getLastPathComponent());
			System.out.println(currentNode.toString()
					+ ":"
					+ ((TemplateNode) currentNode.getUserObject()).getIncludeNode().getFieldId()
					+ ":"
					+ ((TemplateNode) currentNode.getUserObject()).getIncludeNode().getAdjustedFieldId());
		}
	}

	public class ColoredNodeRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -3797717083161151680L;

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (value instanceof CopyBookDataNode) {
				CopyBookDataNode treeNode = (CopyBookDataNode) value;
				TemplateNode includeNode = (TemplateNode) treeNode.getUserObject();
				String offset = ""
						+ includeNode.getIncludeNode().getCalcOffset();
				if (hasData(treeNode)) {
					if (treeNode.getChildCount() > 0) {
						String fieldName = includeNode.getIncludeNode().getFieldName();

						JLabel compName = new JLabel("<HTML>"
								+ getColoredHTMLText("BLUE", fieldName));
						return compName;
					}
					String data = includeNode.getConvertedData();

					String fieldName = includeNode.getIncludeNode().getFieldName();
					String label = "<HTML>"
							+ getColoredHTMLText("BLUE", fieldName + "[")
							+ getColoredHTMLText("RED", data, 4)
							+ getColoredHTMLText("BLUE", "]");
					JLabel compVal = new JLabel(label);
					compVal.setToolTipText(offset);
					return compVal;
				}
				String fieldName = includeNode.getIncludeNode().getFieldName();

				JLabel compName = new JLabel(fieldName);
				return compName;
			}
			return this;
		}

		private String getColoredHTMLText(String c, String txt) {
			return getColoredHTMLText(c, txt, 3);
		}

		private String getColoredHTMLText(String c, String txt, int size) {
			return "<FONT size='" + size + "' COLOR='" + c + "'>" + txt
					+ "</FONT>";
		}

		private boolean hasData(CopyBookDataNode treeNode) {
			TemplateNode includeNode = (TemplateNode) treeNode.getUserObject();
			String data = includeNode.getConvertedData();
			if ((data != null) && (data.trim().length() > 0)) {
				double doubleData = 0.0;
				try {
					doubleData = Double.parseDouble(data.trim());
				} catch (Exception e) {
					System.out.println(e.getMessage());
					doubleData = 1;// TODO: handle exception
				}
				if (doubleData > 0) {
					return true;
				}
			}

			int childCount = treeNode.getChildCount();
			if (childCount > 0) {
				Enumeration enumeration = treeNode.children();
				while (enumeration.hasMoreElements()) {
					CopyBookDataNode child = (CopyBookDataNode) enumeration.nextElement();
					if (hasData(child))
						return true;
				}
			}
			return false;
		}
	}

}
