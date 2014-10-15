package com.koobest.parse;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import com.koobest.constant.ConfigConstant;

import android.util.Xml;

public class DomParse {
	// 得到dom解析工厂对象
	private DocumentBuilderFactory domFactory;
	// dom解析对象实例，通过domfactory获得
	private DocumentBuilder domBuilder;
	// 创建document对象用于接收xml文件流，创建xml节点树
	private Document dom;

	private Element rootElement;

	private String writed_file;

	public DomParse(String path) throws Exception {
		// getResources().getAssets().open("mainconfig.cfg")
		BufferedInputStream buff = new BufferedInputStream(new FileInputStream(
				path));

		domFactory = DocumentBuilderFactory.newInstance();
		domBuilder = domFactory.newDocumentBuilder();

		writed_file = path;

		dom = domBuilder.parse(buff);
		rootElement = dom.getDocumentElement();

	}

	public DomParse(InputStream configStream) throws Exception {
		BufferedInputStream buff = new BufferedInputStream(configStream);

		domFactory = DocumentBuilderFactory.newInstance();
		domBuilder = domFactory.newDocumentBuilder();

		dom = domBuilder.parse(buff);
		rootElement = dom.getDocumentElement();
	}

	public HashMap<Integer, HashMap<String, String>> getAllWidgets(
			String tagSetName) {

		NodeList nodelist = rootElement.getElementsByTagName(tagSetName);
		HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();

		HashMap<String, String> childMap = null;

		for (int i = 0; i < nodelist.getLength(); i++) {
			Element buttonElement = (Element) nodelist.item(i);

			NodeList contentList = buttonElement.getChildNodes();

			childMap = new HashMap<String, String>();
			// system.out.println(buttonElement.getTextContent().length());
			// system.out.println(buttonElement.getTextContent());
			if (buttonElement.getTextContent().trim() != "") {
				for (int j = 0; j < contentList.getLength(); j++) {
					if (contentList.item(j).getNodeType() == Node.ELEMENT_NODE) {

						// System.out.println(contentList.item(j).getNodeName()
						// +
						// ":" + contentList.item(j).getTextContent());
						childMap.put(contentList.item(j).getNodeName(),
								contentList.item(j).getTextContent());

					}
				}
			} else {
				// System.out.println("is null");
				childMap = null;
			}
			map.put(i, childMap);
		}
		return map;
	}

	public void setWritedFilePath(String path) {
		this.writed_file = path;
	}

	public void writeXML() {
		System.out.print("Write XML " + this.writed_file + "\n");
		if (this.writed_file == null) {
			throw new NullPointerException("the path of writed file is null");
		}
		
		try {
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
//			t.setOutputProperty("indent", "yes");
			t.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(this.writed_file)));
			
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e){
			System.out.println(e.getMessage() + " DomParse 131");
		}
	}

	public static void createNewXml(String path, String rootTagName,
			HashMap<String, HashMap<String, String>> map, int num)
			throws IOException {

		// TODO Auto-generated
		
		try {
			FileManager.creatNewFileInSdcard(path);

		} catch (IOException e) {
			// system.out.println(e.getMessage());
			throw new IOException(e.getMessage());
		}

		OutputStreamWriter opw = null;

		BufferedWriter buffwriter = null;

		try {

			opw = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");

			buffwriter = new BufferedWriter(opw);
		} catch (Exception e) {
			// system.out.println(e.getMessage());
		}

		XmlSerializer serializer = Xml.newSerializer();

		try {
			serializer.setOutput(buffwriter);

			serializer.startDocument("UTF-8", true);

			serializer.startTag(null, rootTagName);

			if (map != null) {
				Set<String> keySet = map.keySet();
				Object[] keySetArr = keySet.toArray();

				for (int i = 0; i < num; i++) {
					serializer.startTag(null, keySetArr[0].toString());

					if (map.get(keySetArr[0].toString()) != null) {
						Object[] childKeys = map.get(keySetArr[0].toString())
								.keySet().toArray();

						for (int j = 0; j < map.get(keySetArr[0].toString())
								.size(); j++) {
							serializer.startTag(null, childKeys[j].toString());
							serializer.text(map.get(keySetArr[0].toString())
									.get(childKeys[j].toString()));
							serializer.endTag(null, childKeys[j].toString());

						}
					}
					serializer.endTag(null, keySetArr[0].toString());
				}
			}

			serializer.endTag(null, rootTagName);

			serializer.endDocument();

			serializer.flush();

			buffwriter.flush();
			
//			String filename = new File(path).getName().replace(".xml", "");
			DomParse.createRCOrder(path,null,true);
			
//			System.out.println("CreateNewXml " + filename + " " + filename.split("rc").length);
		} catch (Exception e) {
			// system.out.println(e.getMessage());
		}

	}
	
	public static void createRCOrder(String filePath,DomParse dp,boolean saveFile) throws Exception{
		
		String filename = new File(filePath).getName().replace(".xml", "");
		
		if(filename == null){
			throw new NullPointerException("No file found with the file path " + filePath);
		}
		if(filename.split("rc").length == 2){
			String tagName = filename + "order";
//			System.out.println("Create Order" + filename);
//			System.out.println("DomParse 220 Order TagName " + tagName);
			if(dp == null)
				dp = new DomParse(ConfigConstant.MAINCFGFILEPATH);
			
			dp.createElementWithUpdate("shelf", tagName, null, "0", null, saveFile);
		}
	}

	/**
	 * Get the text content with the tag name, the tag name should be the
	 * unique,if the tag name is not unique,the text content of the first found
	 * will be return
	 * 
	 * 
	 * @param exactTagName
	 * @return null if no element found
	 */
	public String getSingleContent(String exactTagName) {
		
		NodeList wantedList = rootElement.getElementsByTagName(exactTagName);
		if (wantedList.getLength() != 1)
			return null;

		return wantedList.item(0).getTextContent();
	}
	
	public void removeSingleContent(String exactTagName,boolean saveToFile){
//		System.out.print("remove " + exactTagName);
		NodeList wantedList = rootElement.getElementsByTagName(exactTagName);
		if (wantedList.getLength() != 1)
			return;
		
		rootElement.removeChild(wantedList.item(0));
		
		if(saveToFile)
			writeXML();
	}
	/**
	 * 
	 * @param parent
	 *            The parent node,must be known
	 * @param subTagName
	 *            The element's tagname that you want to find
	 * @param elementDescription
	 *            If there are many elements with the same tagname,this params
	 *            can help you to find the one you want,it could be null if you
	 *            want to create a new element
	 * @param subContent
	 *            The content you want to add to the element
	 * @param children
	 *            the children nodes defined in a HashMap,its parent node must
	 *            be sub element,it could not exist with the subcontent
	 * 
	 * 
	 * @author Ray
	 */
	public void createElementWithUpdate(String parent, String subTagName,
			String elementDescription, String subContent,
			HashMap<String, String> children, boolean save) {

		// System.out.println("function params " + parent + " " + subTagName);

		boolean isSubExist = true;
		if (subContent != null && children != null) {
			throw new RuntimeException(
					"SubContent and Children Could not be defined both");
		} else {
			NodeList parents = rootElement.getElementsByTagName(parent);

			if (parents.getLength() != 1) {
				throw new NullPointerException(
						"Could not find the parent node ");

			} else {
				NodeList sublist = dom.getElementsByTagName(subTagName);

				Element sub = null;

				if (sublist.getLength() == 0) {
					// 无子节点，创建一个
					sub = dom.createElement(subTagName);
					isSubExist = false;
				} else {
					if (sublist.getLength() == 1) {
						// do the op
						// 如果子节点数量为一个，则选中
						sub = (Element) sublist.item(0);
					} else {
						// do the op
						// 如果子节点数量多于一个
						for (int i = 0; i < sublist.getLength(); i++) {
							// if(sublist.item(i).hasChildNodes()){
							// 是否有ELEMENT_type的子节点
							if (sublist.item(i).getTextContent().trim()
									.length() != 0) {
								// 如果子节点中还有子节点,如果匹配，直接修改相应的值
								NodeList childrenlist = sublist.item(i)
										.getChildNodes();
								for (int j = 0; j < childrenlist.getLength(); j++) {

									if (childrenlist.item(j).getNodeType() == Node.ELEMENT_NODE
											&& childrenlist.item(j)
													.getTextContent().equals(
															elementDescription)) {
										if (subContent != null)
											sub = (Element) childrenlist
													.item(j);
										if (children != null)
											sub = (Element) sublist.item(i);
										// isSubSetted = true;
										break;
									}

								}
							} else {
								// 如果子节点中无子节点
								// 子节点匹配
								if (sublist.item(i).getNodeType() == Node.ELEMENT_NODE
										&& sublist.item(i).getTextContent()
												.equals(elementDescription)) {
									sub = (Element) sublist.item(i);
									break;
								} else {
									// 为空
									sub = (Element) sublist.item(i);
								}
							}
						}
					}
				}

				if (sub == null)
					throw new RuntimeException("Could not find the node");

				if (subContent != null) {
					sub.setTextContent(subContent);

				} else if (children != null) {
					Set<String> set = children.keySet();
					Element childrenE;
					Object[] key = set.toArray();

					for (int i = 0; i < set.size(); i++) {
						childrenE = dom.createElement(key[i].toString());

						childrenE.setTextContent(children.get(key[i]));

						sub.appendChild(childrenE);
					}
				}
				if (!isSubExist)
					parents.item(0).appendChild(sub);
				if (save)
					this.writeXML();
			}
		}

	}

	// Nao's
	public void addButton(HashMap<String, String> map) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		Node but = dom.createElement("button");
		Node des = dom.createElement("description");
		des.setTextContent(map.get("description"));
		Node code = dom.createElement("code");
		code.setTextContent(map.get("code"));
		Node icon = dom.createElement("icon");
		icon.setTextContent(map.get("icon"));
		// system.out.println(map.get("code"));
		but.appendChild(des);
		but.appendChild(code);
		but.appendChild(icon);
		button.appendChild(but);
	}

	public void deleteButton(int pos) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		Node node = buttons.item(pos);
		// system.out.println(node.getNodeName());
		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
		node.setTextContent("");
	}

	public void ChangePosition(int prePosition, int afterPosition) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		Node pre_buttons = buttons.item(prePosition);
		Node after_button = buttons.item(afterPosition);
		NodeList pre_nodelist = pre_buttons.getChildNodes();
		NodeList after_nodelist = after_button.getChildNodes();
		HashMap<String, String> pre_map = new HashMap<String, String>();
		HashMap<String, String> after_map = new HashMap<String, String>();

		for (int i = 0; i < pre_nodelist.getLength(); i++) {
			if (pre_nodelist.item(i).getNodeType() == Node.ELEMENT_NODE) {
				pre_map.put(pre_nodelist.item(i).getNodeName(), pre_nodelist
						.item(i).getTextContent());
			}
		}
		for (int i = 0; i < after_nodelist.getLength(); i++) {
			if (after_nodelist.item(i).getNodeType() == Node.ELEMENT_NODE) {
				after_map.put(after_nodelist.item(i).getNodeName(),
						after_nodelist.item(i).getTextContent());
			}
		}
		if (!after_button.hasChildNodes() && !pre_buttons.hasChildNodes()) {
		} else {
			if (!after_button.hasChildNodes()) {
				Node des = dom.createElement("description");
				Node icon = dom.createElement("icon");
				Node code = dom.createElement("code");
				after_button.appendChild(des);
				after_button.appendChild(code);
				after_button.appendChild(icon);
				after_nodelist = after_button.getChildNodes();

				while (pre_buttons.hasChildNodes())
					pre_buttons.removeChild(pre_buttons.getFirstChild());

			} else if (!pre_buttons.hasChildNodes()) {

				Node des = dom.createElement("description");
				Node icon = dom.createElement("icon");
				Node code = dom.createElement("code");
				pre_buttons.appendChild(des);
				pre_buttons.appendChild(code);
				pre_buttons.appendChild(icon);
				pre_nodelist = pre_buttons.getChildNodes();
				while (after_button.hasChildNodes())
					after_button.removeChild(after_button.getFirstChild());
			}
			for (int i = 0; i < pre_nodelist.getLength(); i++) {
				// }
				if (pre_nodelist.item(i).getNodeName().toString().equals(
						"description")) {
					pre_nodelist.item(i).setTextContent(
							after_map.get("description"));
				}
				if (pre_nodelist.item(i).getNodeName().equals("icon")) {
					pre_nodelist.item(i).setTextContent(after_map.get("icon"));
				}
				if (pre_nodelist.item(i).getNodeName().equals("code")) {
					pre_nodelist.item(i).setTextContent(after_map.get("code"));
				}
			}
			for (int i = 0; i < after_nodelist.getLength(); i++) {
				if (after_nodelist.item(i).getNodeName().equals("description")) {
					after_nodelist.item(i).setTextContent(
							pre_map.get("description"));
				}
				if (after_nodelist.item(i).getNodeName().equals("icon")) {
					after_nodelist.item(i).setTextContent(pre_map.get("icon"));
				}
				if (after_nodelist.item(i).getNodeName().equals("code")) {
					after_nodelist.item(i).setTextContent(pre_map.get("code"));
				}
			}
		}
	}

	public void modifyDescription(int num, String string) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		if (!buttons.item(num).hasChildNodes()) {
			// System.out.println("no childs");
			Node des = dom.createElement("description");
			Node icon = dom.createElement("icon");
			Node code = dom.createElement("code");
			des.setTextContent(string);
			buttons.item(num).appendChild(des);
			buttons.item(num).appendChild(code);
			buttons.item(num).appendChild(icon);
		} else {
			for (int j = 0; j < buttons.item(num).getChildNodes().getLength(); j++) {
				// System.out.println("has childs");
				// System.out.println(buttons.item(num).getChildNodes().item(j).getNodeName());

				if (buttons.item(num).getChildNodes().item(j).getNodeName()
						.equals("description")) {
					buttons.item(num).getChildNodes().item(j).setTextContent(
							string);
				}
			}
		}
		// System.out.println(buttons.item(num).getChildNodes().item(1)
		// .getTextContent());
	}

	public String getBackgroundPath() {
		// TODO Auto-generated method stub
		NodeList nodelist = rootElement.getElementsByTagName("background");
		if (nodelist.getLength() == 0)
			return null;
		else
			return nodelist.item(0).getTextContent();
	}

	public void modifyCode(int num, String code) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		NodeList nodes = buttons.item(num).getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			// System.out.println("searchcode");
			if (nodes.item(i).getNodeName().equals("code")) {
				// System.out.println(code);
				nodes.item(i).setTextContent(code);
				// System.out.println("found");

			}
		}
	}

	public void modifyButNums(int numButs) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		Node newbutton = dom.createElement("button");
		if (buttons.getLength() > numButs) {
			while (button.getElementsByTagName("button").getLength() != numButs) {
				button.removeChild(button.getLastChild());
			}
		} else if (buttons.getLength() < numButs) {
			while (button.getElementsByTagName("button").getLength() != numButs) {
				// System.out.println("append child");
				button.appendChild(newbutton);
				newbutton = dom.createElement("button");
			}
		}

	}

	public void modifyIcon(int pos, String valueOf) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");
		for (int i = 0; i < buttons.item(pos).getChildNodes().getLength(); i++) {
			System.out.println(buttons.item(pos).getChildNodes().item(i)
					.getNodeName());
			if (buttons.item(pos).getChildNodes().item(i).getNodeName().equals(
					"icon")) {
				buttons.item(pos).getChildNodes().item(i).setTextContent(
						valueOf);
			}
		}
	}

	public void modifyRow(int diff, int prePos) {
		// TODO Auto-generated method stub
		Element button = (Element) rootElement.getElementsByTagName("Buttons")
				.item(0);
		NodeList buttons = button.getElementsByTagName("button");

		if (diff > 0) {
			Node newbutton = dom.createElement("button");

			for (int i = 0; i < diff; i++) {
				if (buttons.item(prePos) == null)
					button.appendChild(newbutton);
				else
					button.insertBefore(newbutton, buttons.item(prePos));
				newbutton = dom.createElement("button");
			}

		} else {
			for (int i = 0; i < Math.abs(diff); i++) {
				button.removeChild(buttons.item(prePos + diff + i));
			}
		}

	}

}
