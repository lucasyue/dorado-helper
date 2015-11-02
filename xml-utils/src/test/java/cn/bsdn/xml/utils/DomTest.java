package cn.bsdn.xml.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.bsdn.dorado.xml.DoradoXmlUtils;
import cn.bsdn.temp.EntityVistor;
import cn.bsdn.utils.FileUtils;

public class DomTest{
	@Test
	public void testDom() {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(DomTest.class.getClassLoader().getResourceAsStream("read.xml"));
			NodeList stuList = doc.getElementsByTagName("Student");
			for (int i = 0; i < stuList.getLength(); i++) {
				Element node = (Element) stuList.item(i);
				System.out.println("Name: "
						+ node.getElementsByTagName("Name").item(0)
								.getFirstChild().getNodeValue());
				System.out.println("Num: "
						+ node.getElementsByTagName("Num").item(0)
								.getFirstChild().getNodeValue());
				System.out.println("Classes: "
						+ node.getElementsByTagName("Classes").item(0)
								.getFirstChild().getNodeValue());
				System.out.println("Address: "
						+ node.getElementsByTagName("Address").item(0)
								.getFirstChild().getNodeValue());
				System.out.println("Tel: "
						+ node.getElementsByTagName("Tel").item(0)
								.getFirstChild().getNodeValue());
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	//@Test
	public void testDom4jDom() throws DocumentException, IOException {
		String fileName = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/main/java/Branch.view.xml";
		String outFile = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/test1.xml";
		// 获取文件内容
		String content = FileUtils.getFileContent(fileName);
		org.dom4j.Document doc = DocumentHelper.parseText(content);
		// SAXReader saxReader = new SAXReader();
		// org.dom4j.Document doc = saxReader.read(new File(fileName));
		// 获取根节点
		// doc.accept(new EntityVistor());
		org.dom4j.Element root = doc.getRootElement();
		// doc.setEntityResolver(entityResolver);
		// 获取根节点下面的所有子节点，遍历
		Iterator<org.dom4j.Element> iter = root.elementIterator();
		while (iter.hasNext()) {
			org.dom4j.Element ele = (org.dom4j.Element) iter.next();
			logger.info(ele.getName());
			if (ele.getName().equals("Model")) {
				// 给要修改节点内容赋值
				ele.addAttribute("attr1", "xxxxx1");
				// break;
			}
			if (ele.getName().equals("View")) {
				Iterator iterator = ele.elementIterator("Action");
				while (iterator.hasNext()) {
					org.dom4j.Element titleElement = (org.dom4j.Element) iterator
							.next();
					QName qname = titleElement.getQName();
					logger.info(titleElement.getText());
				}
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		// 利用格式化类对编码进行设置
		format.setNewLineAfterDeclaration(false);
		format.setEncoding("UTF-8");
		format.setExpandEmptyElements(true);
		// 输出
		StringWriter strWriter = new StringWriter();
		// FileOutputStream output = new FileOutputStream(new File(outFile));
		XMLWriter writer = new XMLWriter(strWriter, format);
		writer.setEscapeText(true);
		writer.write(doc);
		writer.flush();
		writer.close();
		String xml = strWriter.toString();
		FileUtils.writeContentToFile(xml,outFile);
	}
	@Test
	public  void testDom4jSax() throws DocumentException, IOException {
		String fileName = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/main/java/1/Branch.view.xml";
		String outFile = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/test1.xml";
		// 获取文件内容
		SAXReader saxReader = new SAXReader();
		org.dom4j.Document doc = saxReader.read(new File(fileName));
		// 获取根节点
		doc.accept(new EntityVistor());
		org.dom4j.Element root = doc.getRootElement();
		// doc.setEntityResolver(entityResolver);
		// 获取根节点下面的所有子节点，遍历
		Iterator<org.dom4j.Element> iter = root.elementIterator();
		while (iter.hasNext()) {
			org.dom4j.Element ele = (org.dom4j.Element) iter.next();
			logger.info(ele.getName());
			if (ele.getName().equals("Model")) {
				// 给要修改节点内容赋值
				ele.addAttribute("attr1", "xxxxx1");
				// break;
			}
			if (ele.getName().equals("View")) {
				Iterator iterator = ele.elementIterator("Action");
				while (iterator.hasNext()) {
					org.dom4j.Element titleElement = (org.dom4j.Element) iterator
							.next();
					QName qname = titleElement.getQName();
					logger.info(titleElement.getText());
				}
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		// 利用格式化类对编码进行设置
		format.setNewLineAfterDeclaration(false);
		format.setEncoding("UTF-8");
		format.setExpandEmptyElements(true);
		// 输出
		FileOutputStream output = new FileOutputStream(new File(outFile));
		XMLWriter writer = new XMLWriter(output, format);
		writer.setEscapeText(true);
		writer.write(doc);
		writer.flush();
		writer.close();
	}
	//@Test
	public  void testStax() throws SAXException, IOException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		//E:\projects\shangqi\dorado-xml-utils\xml-utils\src\main\java\Broker.model.xml
		String fileName = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/main/java/Branch.view.xml";
		InputStream is = new FileInputStream(fileName);
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(is);
		NodeList list = document.getElementsByTagName("DataType");
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			e.setAttribute("test", "value11");
			break;
		}
		Transformer tran = TransformerFactory.newInstance().newTransformer();
		tran.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tran.setOutputProperty(OutputKeys.INDENT, "yes");
		String fileOutput = "E:/projects/shangqi/dorado-xml-utils/xml-utils/src/test2.xml";
		// Result consoleResult = new StreamResult(System.out);
		File fo = new File(fileOutput);
		if (!fo.exists()) {
			if (!fo.getParentFile().exists()) {
				fo.getParentFile().mkdirs();
			}
			fo.createNewFile();
		}
		Result fileResult = new StreamResult(fileOutput);
		tran.transform(new DOMSource(document), fileResult);

	}

	// 使用XMLStream 解析xml文件 （基于遍历的方式）
	//@Test 
	public void test2() throws Exception {
		InputStream is = DoradoXmlUtils.class.getClassLoader().getResourceAsStream(
				"build.xml"); // 获取xml文件流
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		XMLEventReader reader = xmlFactory.createXMLEventReader(is);
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) { // 如果是开始节点
				StartElement startEle = event.asStartElement();
				String eleName = startEle.getName().toString(); // 得到节点名称
				if ("property".equals(eleName)) {
					Iterator it = startEle.getAttributes();
					while (it.hasNext()) {
						Attribute attr = (Attribute) it.next();
						System.out.printf("%s:%s\t", attr.getName(),
								attr.getValue());
					}
					System.out.println();
				}
			}

		}
	}

	//@Test 
	private static String getFileContent2(String fileName) throws IOException {
		File f = new File(fileName);
		FileReader fr = new FileReader(f);
		// char[]chars=new char[fr.];
		// fr.read(cbuf);
		// FileInputStream fis=new FileInputStream(f);
		// byte[] bytes=new byte[fis.available()];
		// fis.read(bytes);
		// return new String(bytes);
		return fileName;
	}
	static Logger logger = LoggerFactory.getLogger(DomTest.class);

}
