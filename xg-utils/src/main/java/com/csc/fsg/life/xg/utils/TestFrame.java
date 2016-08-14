package com.csc.fsg.life.xg.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.controller.CopyBookController;
import com.csc.fsg.life.xg.copybook.translator.tree.template.CopyBookDataModel;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.managers.SchemaModelManager;
import com.csc.fsg.life.xg.numeric.utils.FormatHandler;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.XGTreeModel;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBook;
import com.csc.fsg.life.xg.treemodels.schema.Schema;
import com.csc.fsg.life.xg.treemodels.schema.SchemaTreeModel;
//import com.csc.fsg.life.xg.treenodes.XgTreePath;
import com.csc.fsg.life.xg.treenodes.copybook.CopyBookTreeNode;
import com.csc.fsg.life.xg.treenodes.schema.ElementTypeNode;
import com.csc.fsg.life.xg.treenodes.schema.RootTypeNode;
import com.csc.fsg.life.xg.treenodes.schema.ValueMutableTreeNode;

/**
 * @author mgandham
 */
public class TestFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7496196841810264237L;
	private String envKey = "WMA";

	private class TreeMouseAdapter extends MouseAdapter {
		private JPopupMenu menu;
		private JTree tree;

		TreeMouseAdapter(JTree tree, JPopupMenu menu) {
			this.tree = tree;
			this.menu = menu;
		}

		public void mousePressed(MouseEvent evt) {

			// SELECT THE NODE
			int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
			TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
			if (selRow != -1) {
				tree.setSelectionPath(selPath);
			}
			// SHOW
			if (evt.isPopupTrigger()) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}

		public void mouseReleased(MouseEvent evt) {
			if (evt.isPopupTrigger()) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private JScrollPane jsp = null;

	public TestFrame() {
		this.setSize(600, 600);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		initialize();
	}

	private void initialize() {
		try {
			jsp = new JScrollPane();
			this.getContentPane().add(jsp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JTree getCopyBookTree(boolean withData) throws Exception {

		if (!withData)
			return getTree();
		CopyBook cpyBook = new CopyBook();
		cpyBook.setIdentifier("NBA-CXGMXIFD");
		cpyBook.setName("CXGMXIFD");
		cpyBook.setSystem("NBA");

		// File dataFile = new File("C:\\temp\\inquiryresult.dat");
		// byte[] fileDataBytes = getBytesFromFile(dataFile);
		FormatHandler.setAscii(false);
		long st = System.currentTimeMillis();
		final CopyBookDataModel model = CopyBookController.getInstance(envKey).getTreeWithData(cpyBook, null);
		long end = System.currentTimeMillis();
		System.out.println("Time taken :" + (end - st));
		return null;
		// final JTree tree = new JTree(model);
		// return tree;
	}

	private JTree getSchemaTree() throws Exception {
		// BUILD MODEL
		SchemaModelManager m = SchemaModelManager.getInstance(envKey);
		Schema s = new Schema();
		s.setName("c:\\xmlGateway\\schemafiles\\TXLife2.8.92.xsd");
		s.setRoot("TXLife");
		RootTypeNode rtn = (RootTypeNode) m.getModel(s);
		SchemaTreeModel model = new SchemaTreeModel(rtn.getValueNode(null));

		// BUILD TREE
		final JTree tree = new JTree(model);
		// BUILD MENUITEM
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem showItem = new JMenuItem("Set Dummy Parent...");
		showItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ValueMutableTreeNode treeNode = (ValueMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				int index = ((ValueMutableTreeNode) treeNode.getParent()).getIndex(treeNode);
				String out = JOptionPane.showInputDialog(TestFrame.this, "Dummy parent name ?");
				if (out != null && out.trim().length() != 0) {
					ElementTypeNode etn = new ElementTypeNode(
							treeNode.getTNode().getParent(), out);
					etn.setChildNodes(treeNode.getTNode().getChildNodes());
					ValueMutableTreeNode vmtn = new ValueMutableTreeNode(etn);
					((ValueMutableTreeNode) treeNode.getParent()).insert(vmtn, index);
					vmtn.add(treeNode);
					//TODO replace treepath with XgTreepath (currently in patch not in HEAD) from life-build-patch project
					TreePath sp = new TreePath(vmtn.getPath()).pathByAddingChild(vmtn);
					tree.expandPath(sp);
					tree.setSelectionPath(sp);
					tree.updateUI();
				}
			}
		});
		menu.add(showItem);

		// CREATE AND ADD POPUPMENU
		tree.add(menu);
		tree.addMouseListener(new TreeMouseAdapter(tree, menu));
		return tree;
	}

	private JTree getTree() throws Exception {

		// BUILD MODEL
		CopyBook cpyBook = new CopyBook();
		cpyBook.setIdentifier("NBA-CXGMXIFD");
		cpyBook.setName("CXGMXIFD");
		cpyBook.setSystem("NBA");
		final XGTreeModel model = CopyBookController.getInstance(envKey).getCopyBookTreeModel(cpyBook);

		// BUILD TREE
		final JTree tree = new JTree(model);

		// BUILD MENUITEM
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem showItem = new JMenuItem("Show more...");
		showItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				CopyBookTreeNode treeNode = (CopyBookTreeNode) tree.getSelectionPath().getLastPathComponent();
				if (treeNode.isCopybookRoot()) {
					JOptionPane.showMessageDialog(TestFrame.this, "To have another similar copybook, load it again", "Gateway Message", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				String out = JOptionPane.showInputDialog(TestFrame.this, "How many ? (total allowed "
						+ treeNode.getNbrOfOccurs() + ")");
				if (out != null && out.trim().length() != 0) {
					int count = -1;
					try {
						count = Integer.parseInt(out);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(TestFrame.this, "Enter a numeric value", "Gateway Error Message", JOptionPane.ERROR_MESSAGE);
						return;
					}
					treeNode.addAdditionalSiblings(count);
					model.getTreePathWithOccurs("CXGMXIFD(00)/CONTRACT-DATA(006)/COVERAGE-DATA(010)");
					tree.updateUI();
				}
			}
		});
		menu.add(showItem);

		// CREATE AND ADD POPUPMENU
		tree.add(menu);
		tree.addMouseListener(new TreeMouseAdapter(tree, menu));
		return tree;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "config/xmlg-config.xml",
						"config/jndi-config.xml" });
		EnvironmentFactory.setAppContext(appContext);
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);

		TestFrame frame = new TestFrame();
		JTree tree = null;
		try {
			tree = frame.getSchemaTree();
			// JTree tree = frame.getCopyBookTree(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (tree == null)
			System.exit(0);
		frame.updateView(tree);
		frame.setVisible(true);
	}

	private void updateView(JTree tree) {
		jsp.setViewportView(tree);
	}
}
