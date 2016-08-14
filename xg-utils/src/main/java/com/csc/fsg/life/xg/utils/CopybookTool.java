package com.csc.fsg.life.xg.utils;

import java.util.Enumeration;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.xg.controller.CopyBookController;
import com.csc.fsg.life.xg.copybook.output.include.tree.IncludeNode;
import com.csc.fsg.life.xg.copybook.translator.tree.composite.CopyBookNode;
import com.csc.fsg.life.xg.copybook.translator.tree.composite.FlyweightNode;
import com.csc.fsg.life.xg.copybook.translator.tree.template.CopyBookDataModel;
import com.csc.fsg.life.xg.copybook.translator.tree.template.CopyBookDataNode;
import com.csc.fsg.life.xg.copybook.translator.tree.template.TemplateNode;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.treemodels.copybook.CopyBook;

public class CopybookTool {

	private void getCopyBookTree() throws Exception {

		CopyBook cpyBook = new CopyBook();
		cpyBook.setIdentifier("PML-CIUAFNA1");
		cpyBook.setName("CIUAFEA1");
		cpyBook.setSystem("NBA");

		long st = System.currentTimeMillis();
		final CopyBookDataModel model = CopyBookController.getInstance("BASE").getTreeWithData(cpyBook, null);
		// System.out.println("Built Model:"+model);
		System.out.println(buildSchema(model));
		long end = System.currentTimeMillis();
		System.out.println("Time taken :" + (end - st));
		// final JTree tree = new JTree(model);
		// return tree;
	}

	public String buildSchema(CopyBookDataModel model) {
		StringBuffer schema = new StringBuffer();

		schema.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		schema.append("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n\n");

		schema.append("<xsd:element name=\"" + model.getRoot() + "\" "
				+ "type=\"" + model.getRoot() + "Type\"/>\n\n");

		buildSchema((CopyBookDataNode) model.getRoot(), schema);

		schema.append("</xsd:schema>\n");

		return schema.toString();
	}

	private void buildSchema(CopyBookDataNode parent, StringBuffer schema) {
		buildNode(parent, schema);
		// CHILDREN
		Enumeration enumeration = parent.children();
		while (enumeration.hasMoreElements()) {
			CopyBookDataNode child = (CopyBookDataNode) enumeration.nextElement();
			TemplateNode tn = (TemplateNode) child.getUserObject();
			if (tn.getIncludeNode().getOccuranceNbr() > 1)
				continue;
			buildSchema(child, schema);
		}
	}

	private void buildNode(CopyBookDataNode parent, StringBuffer schema) {
		TemplateNode tn = (TemplateNode) parent.getUserObject();
		IncludeNode in = tn.getIncludeNode();
		CopyBookNode cn = in.getCopyBookNode();
		int occrCount = 1;
		if (cn instanceof FlyweightNode) {
			FlyweightNode fn = (FlyweightNode) cn;
			occrCount = fn.getNbrOfOccurs();
		}

		schema.append("<xsd:complexType name=\"" + parent + "Type\">\n\t");

		// ADD PROPERTIES AS ATTRIBUTES
		schema.append("<xsd:attribute name=\"id\" type=\"xsd:ID\" fixed=\""
				+ in.getFieldId() + "\"/>\n\t");
		if (in.getParent() != null && in.getParent().getFieldId() != -1)
			schema.append("<xsd:attribute name=\"parentId\" type=\"xsd:IDREF\" fixed=\""
					+ in.getParent().getFieldId() + "\"/>\n\t");
		schema.append("<xsd:attribute name=\"dataType\" type=\"xsd:string\" fixed=\""
				+ in.getDataType() + "\"/>\n\t");
		schema.append("<xsd:attribute name=\"occurs\" type=\"xsd:string\" fixed=\""
				+ occrCount + "\"/>\n\t");
		schema.append("<xsd:attribute name=\"dataLength\" type=\"xsd:string\" fixed=\""
				+ in.getDataLength() + "\"/>\n\t");
		schema.append("<xsd:attribute name=\"dataScale\" type=\"xsd:string\" fixed=\""
				+ in.getDataScale() + "\"/>\n\t");
		schema.append("<xsd:attribute name=\"offset\" type=\"xsd:string\" fixed=\""
				+ in.getCalcOffset() + "\"/>\n\t");

		if (parent.getChildCount() > 0) {
			schema.append("<xsd:sequence>\n\t\t");
			Enumeration enumeration = parent.children();
			while (enumeration.hasMoreElements()) {
				CopyBookDataNode child = (CopyBookDataNode) enumeration.nextElement();
				tn = (TemplateNode) child.getUserObject();
				if (tn.getIncludeNode().getOccuranceNbr() > 1)
					continue;
				schema.append("<xsd:element name=\"" + child + "\" type=\""
						+ child + "Type\"/>\n\t\t");
			}
			schema.append("</xsd:sequence>\n");

		}
		schema.append("</xsd:complexType>\n\n");
	}

	private void buildSchema2(CopyBookDataNode parent, StringBuffer schema) {

		if (parent.getChildCount() > 0) {
			schema.append("<xsd:complexType name=\"" + parent + "Type\">\n");
			schema.append("<xsd:sequence>\n");
			Enumeration enumeration = parent.children();
			while (enumeration.hasMoreElements()) {
				CopyBookDataNode child = (CopyBookDataNode) enumeration.nextElement();
				TemplateNode tn = (TemplateNode) child.getUserObject();
				if (tn.getIncludeNode().getOccuranceNbr() > 1)
					continue;
				if (child.getChildCount() > 0)
					schema.append("<xsd:element name=\"" + child + "\" type=\""
							+ child + "Type\"/>\n\n");
				else
					schema.append("<xsd:element name=\"" + child
							+ "\" type=\"xsd:string\"/>\n\n");
			}
			schema.append("</xsd:sequence>\n");

			schema.append("</xsd:complexType>\n\n");
			enumeration = parent.children();
			while (enumeration.hasMoreElements()) {
				CopyBookDataNode child = (CopyBookDataNode) enumeration.nextElement();
				TemplateNode tn = (TemplateNode) child.getUserObject();
				if (tn.getIncludeNode().getOccuranceNbr() > 1)
					continue;
				buildSchema(child, schema);
			}
		}

	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "config/xmlg-config.xml",
						"config/jndi-config.xml" });
		EnvironmentFactory.setAppContext(appContext);
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) appContext.getBean("xmlgConfig"));
		EnvironmentInitializer.init(null);

		try {
			new CopybookTool().getCopyBookTree();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
