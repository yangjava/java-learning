package com.java.dom4j.testdom;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestSAXParse extends DefaultHandler{
	
	/**
	 * 为解决DOM的问题，出现了SAX。SAX ，事件驱动。
	 * 当解析器发现元素开始、元素结束、文本、文档的开始或结束等时，发送事件，程序员编写响应这些事件的代码，保存数据。
	 * 优点：不用事先调入整个文档，占用资源少；SAX解析器代码比DOM解析器代码小，适于Applet，下载。
	 * 缺点：不是持久的；事件过后，若没保存数据，那么数据就丢了；无状态性；从事件中只能得到文本，但不知该文本属于哪个元素；
	 * 使用场合：Applet;只需XML文档的少量内容，很少回头访问；机器内存少。
	 */
	 private String tagValue; // 标签值  
	  
	    // 开始解析XML文件  
	    public void startDocument() throws SAXException {  
	        System.out.println("开始解析");  
	    }  
	  
	    // 结束解析XML文件  
	    public void endDocument() throws SAXException {  
	        System.out.println("结束解析");  
	    }  
	  
	    // 解析元素  
	    /** 
	     * 开始解析一个元素 
	     * @param qName 标签名 
	     * @param attributes 属性 
	     */  
	    @Override  
	    public void startElement(String uri, String localName, String qName, Attributes attributes)  
	            throws SAXException {  
	        System.out.println(qName + "开始");  
	        // 属性  
	        if (attributes != null && attributes.getLength() != 0) {  
	            System.out.println("属性：");  
	            for (int i = 0; i < attributes.getLength(); i++) {  
	                System.out.print(attributes.getQName(i) + "="); // 属性名  
	                System.out.print(attributes.getValue(i) + " "); // 属性值  
	            }  
	            System.out.println();  
	        }  
	    }  
	  
	    /** 
	     * 结束一个元素的解析 遇到结束标签时调用此方法 通常在此方法对标签取值并处理 
	     */  
	    @Override  
	    public void endElement(String uri, String localName, String qName) throws SAXException {  
	        System.out.println(qName + "标签值：" + tagValue);  
	        System.out.println(qName + "结束");  
	    }  
	  
	    // 所有xml文件中的字符都会放到ch[]中  
	    public void characters(char ch[], int start, int length) throws SAXException {  
	        tagValue = new String(ch, start, length).trim();  
	    }  
	  
	    public static void main(String[] args) {  
	        File file = new File("src/cn/main/example/demo.xml");  
	        SAXParserFactory saxParFac = SAXParserFactory.newInstance();  
	        try {  
	            SAXParser saxParser = saxParFac.newSAXParser();  
	            saxParser.parse(file, new TestSAXParse());  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	  
	    }  
	
}
