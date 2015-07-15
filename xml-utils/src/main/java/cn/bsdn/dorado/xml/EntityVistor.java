package cn.bsdn.dorado.xml;

import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.VisitorSupport;

public class EntityVistor extends VisitorSupport{

	@Override
	public void visit(Element node) {
		System.out.println(node.getName());
	}

	@Override
	public void visit(Entity node) {
		System.out.println(node.getText());
		System.out.println(node.getStringValue());
	}

}
