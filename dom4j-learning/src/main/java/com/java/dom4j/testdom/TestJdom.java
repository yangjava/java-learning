package com.java.dom4j.testdom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class TestJdom {
	 /**
	  * 要实现的功能简单，如解析、创建等，但在底层，JDOM还是使用SAX（最常用）、DOM、Xanan文档。
      * 导入jar包：jdom.jar
	  */
	 Document document = new Document();  
	  
	    /** 
	     * 利用JDom进行xml文档的写入操作 
	     */  
	    public void createXml(File file) {  
	  
	        // 1.创建元素 及 设置为根元素  
	        Element employees = new Element("employees");  
	        document.setContent(employees);  
	  
	        // 2.创建注释 及 设置到根元素上  
	        Comment commet = new Comment("this is my comment");  
	        employees.addContent(commet);  
	  
	        // 3.创建元素  
	        Element element1 = new Element("employee");  
	  
	        // 3.1 设置元素的属性名及属性值  
	        element1.setAttribute(new Attribute("id", "0001"));  
	  
	        // 3.2 创建元素的属性名及属性值  
	        Attribute nameAttr = new Attribute("name", "wanglp");  
	  
	        // 3.3 设置元素名及文本  
	        Element sexEle = new Element("sex");  
	        sexEle.setText("m");  
	        // 设置到上层元素上  
	        element1.addContent(sexEle);  
	  
	        // 设置元素  
	        Element ageEle = new Element("age");  
	        ageEle.setText("22");  
	        element1.addContent(ageEle);  
	  
	        // 设置为根元素的子元素  
	        employees.addContent(element1);  
	        // 将元素属性设置到元素上  
	        element1.setAttribute(nameAttr);  
	  
	        // 3.创建元素  
	        Element element2 = new Element("employee");  
	  
	        // 3.1 设置元素的属性名及属性值  
	        element2.setAttribute(new Attribute("id", "0002"));  
	  
	        // 3.2 创建元素的属性名及属性值  
	        Attribute name2Attr = new Attribute("name", "fox");  
	  
	        // 3.3 设置元素名及文本  
	        Element sex2Ele = new Element("sex");  
	        sex2Ele.setText("f");  
	        // 设置到上层元素上  
	        element2.addContent(sex2Ele);  
	  
	        // 设置元素  
	        Element age2Ele = new Element("age");  
	        age2Ele.setText("21");  
	        element2.addContent(age2Ele);  
	  
	        // 设置为根元素的子元素  
	        employees.addContent(element2);  
	        // 将元素属性设置到元素上  
	        element2.setAttribute(name2Attr);  
	  
	        Element element3 = new Element("employee");  
	        element3.setText("title");  
	        element3.addContent(new Element("name").addContent(new Element("hello")));  
	        employees.addContent(element3);  
	  
	        // 设置xml文档输出的格式  
	        Format format = Format.getPrettyFormat();  
	        XMLOutputter out = new XMLOutputter(format);  
	        // 将得到的xml文档输出到文件流中  
	        try {  
	            out.output(document, new FileOutputStream(file));  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    /** 
	     * 利用JDom进行xml文档的读取操作 
	     */  
	    public void parserXml(File file) {  
	        // 建立解析器  
	        SAXBuilder builder = new SAXBuilder();  
	        try {  
	            // 将解析器与文档关联  
	            document = builder.build(file);  
	        } catch (JDOMException e1) {  
	            e1.printStackTrace();  
	        } catch (IOException e1) {  
	            e1.printStackTrace();  
	        }  
	        // 读取根元素  
	        Element root = document.getRootElement();  
	        // 输出根元素的名字  
	        System.out.println("<" + root.getName() + ">");  
	  
	        // 读取元素集合  
	        List<?> employeeList = root.getChildren("employee");  
	        for (int i = 0; i < employeeList.size(); i++) {  
	            Element ele = (Element) employeeList.get(i);  
	            // 得到元素的名字  
	            System.out.println("<" + ele.getName() + ">");  
	  
	            // 读取元素的属性集合  
	            List<?> empAttrList = ele.getAttributes();  
	            for (int j = 0; j < empAttrList.size(); j++) {  
	                Attribute attrs = (Attribute) empAttrList.get(j);  
	                // 将属性的名字和值 并 输出  
	                String name = attrs.getName();  
	                String value = (String) attrs.getValue();  
	                System.out.println(name + "=" + value);  
	            }  
	            try {  
	                Element sex = ele.getChild("sex");  
	                System.out.println("<sex>" + sex.getText());  
	                Element age = ele.getChild("age");  
	                System.out.println("<age>" + age.getText());  
	            } catch (NullPointerException e) {  
	                System.out.println(ele.getTextTrim());  
	                Element name = ele.getChild("name");  
	                System.out.println("<name>" + name.getName());  
	                  
	            }  
	            System.out.println("</employee>");  
	        }  
	        System.out.println("</employees>");  
	    }  
	  
	    /** 
	     * 测试 
	     */  
	    public static void main(String[] args) {  
	  
	    	TestJdom jdom = new TestJdom();  
	        File file = new File("E://jdom.xml");  
	        jdom.createXml(file);  
	        jdom.parserXml(file);  
	    }  
}
