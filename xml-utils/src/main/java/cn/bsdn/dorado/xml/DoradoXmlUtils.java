package cn.bsdn.dorado.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import cn.bsdn.utils.FileUtils;

import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

/**通过java代码全局性修改dorado model.xml或view.xml文件
 * @author rd-yue.yanjie
 *
 */
public class DoradoXmlUtils {
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, SAXException {
		// String
		// fileName="E:/projects/shangqi/xml-utils/src/main/java/Branch.view.xml";
		// String fileName2="E:/projects/shangqi/xml-utils/src/1.view.xml";
		// String content=getFileContent(fileName);
		// String convertContent=convertBefore(content);
		// writeContentToFile(convertContent, fileName2);
		// testStaxDom();
		String modelfolder = "E:/projects/shangqi/dorado7/dorado7/settle/model";
		String folder = "E:/projects/shangqi/CTP2_UI/CTP2/src/dorado/";
		String folder1 = "E:/projects/shangqi/CTP2_UI/CTP2/src/dorado/com/sfit/dorado/view/account";
		String folder2="E:/projects/shangqi/CTP2_UI/CTP2/src/dorado/com/sfit/stockopt/dorado/models";
		String desiFolder = "E:/projects/shangqi/dorado7-gen/";
		allDataTypePropertyDefs = getAllDataType(modelfolder, ".xml");
		compareToAddValidator(folder, desiFolder, ".xml");
		//testSTAX();
		removeNode(folder,".xml",desiFolder);
	}
	private static void compareToAddValidator(String folder, String desiFolder,
			String fileExt) throws IOException, TransformerFactoryConfigurationError, TransformerException {
		List<File> fileList = FileUtils.getFiles(folder, fileExt,null);
		int countFile=0;
		for (int i = 0; i < fileList.size(); i++) {
			File f = fileList.get(i);
			Document document = loadDocument(f.getAbsolutePath());
			boolean addFlag=addValidator(document);
			if (!addFlag) {
				logger.info("ign file {}.{}", i, f.getAbsolutePath());
				continue;
			}else{
				countFile++;
				logger.info("No.{}file.{}", countFile, f.getAbsolutePath());
				saveDocument(folder, desiFolder, f, document);
			}
		}
	}
	private static void saveDocument(String folder, String desiFolder, File f,
			Document document) throws IOException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		String fileOutput = desiFolder + f.getAbsolutePath().substring(folder.length());
		File fo = new File(fileOutput);
		if (!fo.exists()) {
			if (!fo.getParentFile().exists()) {
				fo.getParentFile().mkdirs();
			}
			fo.createNewFile();
		}
		Transformer tran = TransformerFactory.newInstance()
				.newTransformer();
		tran.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tran.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter strWriter = new StringWriter();
		Result fileResult = new StreamResult(strWriter);
		document.setXmlStandalone(true);
		tran.transform(new DOMSource(document), fileResult);
		String newXml = convertAfter(strWriter.toString());
		FileUtils.writeContentToFile(newXml, fileOutput,null);
	}
	private static void removeNode(String inputFolder,String fileExt,String outputFolder) throws IOException, TransformerFactoryConfigurationError, TransformerException{
		List<File> fileList = FileUtils.getFiles(inputFolder, fileExt,null);
		int countFile=0;
		for (int i = 0; i < fileList.size(); i++) {
			File f = fileList.get(i);
			Document document = loadDocument(f.getAbsolutePath());
			boolean removeFlag = removeCustomNode(document);
			if (!removeFlag) {
				logger.info("ign file {}.{}", i, f.getAbsolutePath());
				continue;
			}else{
				countFile++;
				logger.info("No.{}file.{}", countFile, f.getAbsolutePath());
				saveDocument(inputFolder, outputFolder, f, document);
			}
		}
	}
	private static boolean removeCustomNode(Document document) {
		boolean hasRemove=false;
		Element element = document.getDocumentElement();
		NodeList dataTypeList = element.getElementsByTagName("DataType");
		for (int i = 0; i < dataTypeList.getLength(); i++) {// i
			Element dataType = (Element) dataTypeList.item(i);
			String dataTypeParent = dataType.getAttribute("parent");
			if (isNotEmpty(dataTypeParent)) {
				continue;
			}
			String dataTypeBeanName = getDataTypeBeanName(dataType);
			if (isEmpty(dataTypeBeanName)) {
				continue;
			}
			NodeList propertyDefList = dataType
					.getElementsByTagName("PropertyDef");
			for (int j = 0; j < propertyDefList.getLength(); j++) {
				Element propertyDef = (Element) propertyDefList.item(j);
				String propertyDefName=propertyDef.getAttribute("name");
				if(!ingorePropertys.contains(propertyDefName)){
					continue;
				}
				NodeList validatorList = propertyDef
						.getElementsByTagName("Validator");
				if (validatorList == null || validatorList.getLength() == 0) {
					continue;
				} else {
					for (int k = 0; k < validatorList.getLength(); k++) {
						Element validator = (Element) validatorList.item(k);
						String validatorType = validator
								.getAttribute("type");
						if ("custom".equals(validatorType)) {
							org.w3c.dom.Node node=validator
							.getElementsByTagName("ClientEvent")
							.item(0);
							String value=null;
							if(node!=null){
								if(node.getFirstChild()!=null){
									value =node.getFirstChild().getNodeValue();
								}
							}
							if (isNotEmpty(value)
									&& value.contains("commonValidator.checkStringLength")) {
								NodeList proList=validator.getElementsByTagName("Property");
								if(proList!=null&&proList.getLength()>0){
									org.w3c.dom.Node node1=proList.item(0).getFirstChild();
									if(node1.getNodeValue().contains("cvCheckStringLength")){
										continue;
									}
								}
								propertyDef.removeChild(validator);
								logger.info("rm:"+dataType.getAttribute("name")+","+propertyDef.getAttribute("name"));
								hasRemove=true;
							} 
						}
					}
				}
			}
		}
		return hasRemove;
	}
	public static Map<String, Map<String, Integer>> getAllDataType(
			String folder, String ext) {
		Map<String, Map<String, Integer>> dataTypePropertyMap = new HashMap<String, Map<String, Integer>>();
		List<File> fileList = FileUtils.getFiles(folder, ext,null);
		for (File f : fileList) {
			Map<String, Map<String, Integer>> dataTypeMap = getDataTypeStringPropertyDefs(f
					.getAbsolutePath());
			if (dataTypeMap != null) {
				dataTypePropertyMap.putAll(dataTypeMap);
			}
		}
		return dataTypePropertyMap;
	}


	private static Map<String, Map<String, Integer>> getDataTypeStringPropertyDefs(
			String fileInput) {
		Map<String, Map<String, Integer>> dataTypeMap = null;
		Document document = loadDocument(fileInput);
		Element element = document.getDocumentElement();
		NodeList dataTypeList = element.getElementsByTagName("DataType");
		for (int i = 0; i < dataTypeList.getLength(); i++) {
			Element dataType = (Element) dataTypeList.item(i);
			String dataTypeName = dataType.getAttribute("name");
			String dataTypeParent = dataType.getAttribute("parent");
			if (isNotEmpty(dataTypeParent)) {
				logger.info("1.忽略DataType:" + dataTypeName + ",parent="
						+ dataTypeParent);
				continue;
			}
			String dataTypeBeanName = getDataTypeBeanName(dataType);
			if (isEmpty(dataTypeBeanName)) {
				logger.info("2.忽略DataType:" + dataTypeName);
				continue;
			}
			Map<String, Integer> propertyDefMap = getPropertyDefs(dataType);
			if (propertyDefMap != null) {
				if (dataTypeMap == null) {
					dataTypeMap = new HashMap<String, Map<String, Integer>>();
				}
				if (isNotEmpty(dataTypeBeanName)) {
					dataTypeMap.put(dataTypeBeanName, propertyDefMap);
				}
			}
		}
		return dataTypeMap;
	}

	private static Map<String, Integer> getPropertyDefs(Element dataType) {
		Map<String, Integer> map = null;
		NodeList propertyDefList = dataType.getElementsByTagName("PropertyDef");
		for (int j = 0; j < propertyDefList.getLength(); j++) {
			Element propertyDef = (Element) propertyDefList.item(j);
			String propertyDefName = propertyDef.getAttribute("name");
			String propertyDefDataType = getPropertyDefDataType(propertyDef);
			// get Validator length
			String length = null;
			if (isEmpty(propertyDefDataType)
					|| "String".equals(propertyDefDataType.trim())) {
				NodeList validatorList = propertyDef
						.getElementsByTagName("Validator");
				if (validatorList.getLength() == 0) {
				} else {
					// check has Validator
					boolean hasMaxLength = false;
					for (int k = 0; k < validatorList.getLength(); k++) {
						Element validator = (Element) validatorList.item(k);
						String validatorType = validator.getAttribute("type");
						if (hasMaxLength) {
							break;
						}
						if ("length".equals(validatorType)) {
							NodeList pList2 = validator
									.getElementsByTagName("Property");
							for (int g = 0; g < pList2.getLength(); g++) {
								Element pT = (Element) pList2.item(g);
								String pAttr = pT.getAttribute("name");
								if ("maxLength".equals(pAttr)) {
									length = pT.getFirstChild().getNodeValue();
									hasMaxLength = true;
									break;
								}
							}
						}
					}
				}
			}
			if (isNotEmpty(length)) {
				if (map == null) {
					map = new HashMap<String, Integer>();
				}
				map.put(propertyDefName, Integer.parseInt(length.trim()));
			}
		}
		return map;
	}

	private static boolean addValidator(Document document) {
		boolean hasAdded=false;
		int countAddDataType=0;
		Element element = document.getDocumentElement();
		NodeList dataTypeList = element.getElementsByTagName("DataType");
		for (int i = 0; i < dataTypeList.getLength(); i++) {// i
			boolean hasAddedDataType=false;
			Element dataType = (Element) dataTypeList.item(i);
			String dataTypeName = dataType.getAttribute("name");
			String dataTypeParent = dataType.getAttribute("parent");
			if (isNotEmpty(dataTypeParent)) {
				logger.info("1.忽略DataType:" + dataTypeName + ",parent="
						+ dataTypeParent);
				continue;
			}
			String dataTypeBeanName = getDataTypeBeanName(dataType);
			if (isEmpty(dataTypeBeanName)) {
				logger.info("2.忽略DataType:" + dataTypeName);
				continue;
			}
			// 2.check and add Validator to PropertyDef
			logger.info("dt{}.{}", i, dataTypeName);
			NodeList propertyDefList = dataType
					.getElementsByTagName("PropertyDef");
			int countAddPropertyDef=0;
			for (int j = 0; j < propertyDefList.getLength(); j++) {
				Element propertyDef = (Element) propertyDefList.item(j);
				String propertyDefDataType = getPropertyDefDataType(propertyDef);
				// check to add Validator
				boolean needAddValidator = false;
				if (isEmpty(propertyDefDataType)
						|| "String".equals(propertyDefDataType.trim())) {
					NodeList validatorList = propertyDef
							.getElementsByTagName("Validator");
					if (validatorList == null || validatorList.getLength() == 0) {
						// add Validator
						needAddValidator = true;
					} else {
						// check has custom or length Validator 
						boolean checkFlag=true;
						for (int k = 0; k < validatorList.getLength(); k++) {
							Element validator = (Element) validatorList.item(k);
							//propertyDef.removeChild(validator);
							String validatorType = validator
									.getAttribute("type");
							if ("custom".equals(validatorType)) {
								org.w3c.dom.Node node=validator
								.getElementsByTagName("ClientEvent")
								.item(0);
								String value=null;
								if(node!=null){
									if(node.getFirstChild()!=null){
										value =node.getFirstChild().getNodeValue();
									}
								}
								if (isNotEmpty(value)
										&& value.contains("commonValidator.checkStringLength")) {
									// do nothing
									checkFlag=false;
									break;
								} else {
									// marked times
								}
							} else if ("length".equals(validatorType)) {
								checkFlag=false;
								break;
							}else if("regExp".equals(validatorType)){
								checkFlag=false;
								break;
							}
						}
						needAddValidator=checkFlag;
					}
				}
				if (needAddValidator) {
					// do add Validator
					boolean rs= addValidatorToPropertyDef(dataTypeBeanName, propertyDef,
							document);
					if(!hasAdded){//file
						hasAdded=rs;
					}
					if(!hasAddedDataType){//dataType
						hasAddedDataType=rs;
						if(rs){
							countAddDataType++;
						}
					}
					if(rs){
						countAddPropertyDef++;
					}
				}
			}
			if(countAddPropertyDef>0){
				logger.info("add summary:PropertyDef {}",countAddPropertyDef);
			}
		}
		if(countAddDataType>0){
			logger.info("add summary:DataType {}",countAddDataType);
		}
		return hasAdded;
	}

	private static String getPropertyDefDataType(Element propertyDef) {
		String propertyDefDataType = null;
		// get propertyDef's dataType
		NodeList pdpList = propertyDef.getElementsByTagName("Property");
		for (int k = 0; k < pdpList.getLength(); k++) {
			Element pdp = (Element) pdpList.item(k);
			String pdpName = pdp.getAttribute("name");
			if ("dataType".equals(pdpName)) {
				propertyDefDataType = pdp.getFirstChild().getNodeValue();
				break;
			}
		}
		return propertyDefDataType;
	}

	private static String getDataTypeBeanName(Element dataType) {
		String dataTypeBeanName = null;
		// 1.get creationType\matchType
		NodeList dataTypePropertyList = dataType
				.getElementsByTagName("Property");
		for (int j = 0; j < dataTypePropertyList.getLength(); j++) {// j
			Element property = (Element) dataTypePropertyList.item(j);
			String propertyName = property.getAttribute("name");
			if ("creationType".equals(propertyName)) {
				dataTypeBeanName = property.getFirstChild().getNodeValue();
				break;
			} else if ("matchType".equals(propertyName)) {
				dataTypeBeanName = property.getFirstChild().getNodeValue();
				break;
			}
		}
		return dataTypeBeanName;
	}

	private static boolean addValidatorToPropertyDef(String dataTypeBean,
			Element propertyDef, Document document) {
		Map<String, Integer> propertyDefsMap = allDataTypePropertyDefs
				.get(dataTypeBean);
		Integer length = null;
		String propertyDefName = propertyDef.getAttribute("name");
		if (propertyDefsMap != null) {
			length = propertyDefsMap.get(propertyDefName.trim());
		}
		if(ingorePropertys.contains(propertyDefName)){
			logger.info("ignoreProperty "+dataTypeBean+","+propertyDefName+","+length);
			return false;
		}
		if (length == null) {
			logger.info("ignore "+dataTypeBean+","+propertyDefName+","+length);
			return false;
		}
		logger.info("add    {} {} {}", dataTypeBean, propertyDefName, length);
		
		Element customValidator = document.createElement("Validator");
		customValidator.setAttribute("type", "custom");
		Element clientEvent = document.createElement("ClientEvent");
		clientEvent.setAttribute("name", "onValidate");
		clientEvent.appendChild(document
				.createTextNode("commonValidator.checkStringLength(self,arg,"
						+ length + ");"));
		customValidator.appendChild(clientEvent);
		propertyDef.appendChild(customValidator);
		return true;
	}

	public static Document loadDocument(String fileName) {
		Document document = null;
		try {
			String input = FileUtils.getFileContent(fileName);
			String inputConvert = convertBefore(input.trim());
			// StringReader reader=new StringReader(inputConvert);
			InputStream is = new ByteArrayInputStream(
					inputConvert.getBytes("utf-8"));
			InputSource source = new InputSource(is);
			// InputStream is = new FileInputStream(fileName);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			builder.setErrorHandler(new DefaultErrorHandler() {
				@Override
				public void fatalError(SAXParseException exception)
						throws SAXException {
					String msg = "Content is not allowed in prolog.";
					if (msg.equals(exception.getMessage())) {
						exception.printStackTrace();
					} else {
						// super.fatalError(exception);
					}
				}
				@Override
				public void fatalError(TransformerException exception)
						throws TransformerException {
					// super.fatalError(exception);
				}
			});
			document = builder.parse(source);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
	}

	static Logger logger = LoggerFactory.getLogger(DoradoXmlUtils.class);
	static Map<String, Map<String, Integer>> allDataTypePropertyDefs = null;
	static List<String> ingorePropertys=Arrays.asList("brokerID","investorID","investUnitID","accountID");
	public static String convertBefore(String input) {
		// &quot;&#xD;&amp;
		// $$quot;$$#xD;$$amp;
		String output = input.replace("&quot;", "$$quot;")
				.replace("&#xD;", "$$#xD;").replace("&amp;", "$$amp;");
		return output;
	}

	public static String convertAfter(String input) {
		String output = input.replace("$$quot;", "&quot;")
				.replace("$$#xD;", "&#xD;").replace("$$amp;", "&amp;");
		return output;
	}

	public static boolean isNotEmpty(String text) {
		return text != null && text.length() > 0;
	}

	public static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}
}
