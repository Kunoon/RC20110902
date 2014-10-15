package com.koobest.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigParse extends DefaultHandler{
	private String neededTagName,currentTagName;
	private String neededContent = null;
	
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		
		String inner = new String(ch,start,length);
		
		if(currentTagName.equals(neededTagName) && inner.trim().length() != 0){
			neededContent = new String(ch,start,length);
			System.out.println("content:" + neededContent);
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		super.error(e);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		currentTagName = localName;
	}
	
}
