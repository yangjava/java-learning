package com.java.dom4j.testdom;

import java.io.File;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.util.Iterator;  
  
import org.dom4j.Attribute;  
import org.dom4j.Document;  
import org.dom4j.DocumentException;  
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;  
import org.dom4j.io.SAXReader;  
import org.dom4j.io.XMLWriter; 
/**
 * DOM4J 是一个非常非常优秀的Java XML API，具有性能优异、功能强大和极端易用使用的特点，同时它也是一个开放源代码的软件。
 * 如今你可以看到越来越多的 Java 软件都在使用 DOM4J 来读写 XML，特别值得一提的是连 Sun 的 JAXM 也在用 DOM4J。
 * 导入jar包：dom4j-1.6.1.jar*/

public class TestDom4j {
	 /** 
     * 利用dom4j进行xml文档的写入操作 
     */  
    public void createXml(File file) {  
  
        // XML 声明 <?xml version="1.0" encoding="UTF-8"?> 自动添加到 XML文档中  
  
        // 使用DocumentHelper类创建文档实例(生成 XML文档节点的 dom4j API工厂类)  
        Document document = DocumentHelper.createDocument();  
  
        // 使用addElement()方法创建根元素 employees(用于向 XML 文档中增加元素)  
        Element root = document.addElement("employees");  
  
        // 在根元素中使用 addComment()方法添加注释"An XML Note"  
        root.addComment("An XML Note");  
  
        // 在根元素中使用 addProcessingInstruction()方法增加一个处理指令  
        root.addProcessingInstruction("target", "text");  
  
        // 在根元素中使用 addElement()方法增加employee元素。  
        Element empElem = root.addElement("employee");  
  
        // 使用 addAttribute()方法向employee元素添加id和name属性  
        empElem.addAttribute("id", "0001");  
        empElem.addAttribute("name", "wanglp");  
  
        // 向employee元素中添加sex元素  
        Element sexElem = empElem.addElement("sex");  
        // 使用setText()方法设置sex元素的文本  
        sexElem.setText("m");  
  
        // 在employee元素中增加age元素 并设置该元素的文本。  
        Element ageElem = empElem.addElement("age");  
        ageElem.setText("25");  
  
        // 在根元素中使用 addElement()方法增加employee元素。  
        Element emp2Elem = root.addElement("employee");  
  
        // 使用 addAttribute()方法向employee元素添加id和name属性  
        emp2Elem.addAttribute("id", "0002");  
        emp2Elem.addAttribute("name", "fox");  
  
        // 向employee元素中添加sex元素  
        Element sex2Elem = emp2Elem.addElement("sex");  
        // 使用setText()方法设置sex元素的文本  
        sex2Elem.setText("f");  
  
        // 在employee元素中增加age元素 并设置该元素的文本。  
        Element age2Elem = emp2Elem.addElement("age");  
        age2Elem.setText("24");  
  
        // 可以使用 addDocType()方法添加文档类型说明。  
        // document.addDocType("employees", null, "file://E:/Dtds/dom4j.dtd");  
        // 这样就向 XML 文档中增加文档类型说明：  
        // <!DOCTYPE employees SYSTEM "file://E:/Dtds/dom4j.dtd">  
        // 如果文档要使用文档类型定义（DTD）文档验证则必须有 Doctype。  
  
        try {  
            XMLWriter output = new XMLWriter(new FileWriter(file));  
            output.write(document);  
            output.close();  
        } catch (IOException e) {  
            System.out.println(e.getMessage());  
        }  
    }  
  
    /** 
     * 利用dom4j进行xml文档的读取操作 
     */  
    public void parserXml(File file) {  
  
        Document document = null;  
  
        // 使用 SAXReader 解析 XML 文档 catalog.xml：  
        SAXReader saxReader = new SAXReader();  
  
        try {  
            document = saxReader.read(file);  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
        // 将字符串转为XML  
        // document = DocumentHelper.parseText(fileString);  
  
        // 获取根节点  
        Element root = document.getRootElement();  
        // 打印节点名称  
        System.out.println("<" + root.getName() + ">");  
  
        // 获取根节点下的子节点遍历  
        Iterator<?> iter = root.elementIterator("employee");  
        // 遍历employee节点  
        while (iter.hasNext()) {  
            // 获取当前子节点  
            Element empEle = (Element) iter.next();  
            System.out.println("<" + empEle.getName() + ">");  
  
            // 获取当前子节点的属性遍历  
            Iterator<?> attrList = empEle.attributeIterator();  
            while (attrList.hasNext()) {  
                Attribute attr = (Attribute) attrList.next();  
                System.out.println(attr.getName() + "=" + attr.getValue());  
            }  
  
            // 遍历employee节点下所有子节点  
            Iterator<?> eleIte = empEle.elementIterator();  
            while (eleIte.hasNext()) {  
                Element ele = (Element) eleIte.next();  
                System.out.println("<" + ele.getName() + ">" + ele.getTextTrim());  
            }  
  
            // 获取employee节点下的子节点sex值  
            // String sex = empEle.elementTextTrim("sex");  
            // System.out.println("sex:" + sex);  
  
        }  
        System.out.println("</" + root.getName() + ">");  
    }  
  
    public static void main(String[] args) {  
  
    	TestDom4j dom4j = new TestDom4j();  
        File file = new File("e:/dom4j.xml");  
        // dom4j.createXml(file);  
  
        dom4j.parserXml(file);  
  
    }  
}
