package com.java.dom4j.testdom;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.PrintWriter;  
  
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.OutputKeys;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerConfigurationException;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  
  
import org.w3c.dom.Attr;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  
import org.xml.sax.SAXException;






public class TestDom {
	   /* 
     * 解析器读入整个文档，然后构建一个驻留内存的树结构， 
     *  
     * 然后代码就可以使用 DOM 接口来操作这个树结构。 
     *  
     * 优点：整个文档树在内存中，便于操作；支持删除、修改、重新排列等多种功能； 
     *  
     * 缺点：将整个文档调入内存（包括无用的节点），浪费时间和空间； 
     *  
     * 使用场合：一旦解析了文档还需多次访问这些数据；硬件资源充足（内存、CPU） 
     */  
  
    // 表示整个HTML或 XML文档。从概念上讲，它是文档树的根，并提供对文档数据的基本访问  
    private Document document;  
  
    /** 
     * 创建DOM树 
     *  
     * 要读入一个XML文档，首先要一个DocumentBuilder对象 
     */  
    public void init() {  
        // 获取 DocumentBuilderFactory 的新实例  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        // 使用当前配置的参数创建一个新的 DocumentBuilder 实例  
        DocumentBuilder builder = null;  
        try {  
            builder = factory.newDocumentBuilder();  
        } catch (ParserConfigurationException e) {  
            e.printStackTrace();  
        }  
        // 获取 DOM Document 对象的一个新实例来生成一个 DOM 树  
        this.document = builder.newDocument();  
    }  
  
    /** 
     * xml文档的写入操作 
     *  
     * @param file 
     */  
    public void createXml(File file) {  
  
        // 创建DOM树  
        this.init();  
  
        // 创建XML根节点employees  
        Element root = this.document.createElement("employees");  
        // Adds the node newChild to the end of the list of children of this  
        // node.  
        // If the newChild is already in the tree, it is first removed.  
        this.document.appendChild(root);  
  
        // 1.创建根节点的子节点employee  
        Element employee = this.document.createElement("employee");  
  
        // 向根节点添加属性节点  
        Attr id = this.document.createAttribute("id");  
        id.setNodeValue("0001");  
        // 把属性节点对象，追加到达employee节点；  
        employee.setAttributeNode(id);  
  
        // 声明employee的子节点name  
        Element name = this.document.createElement("name");  
        // 向XML文件name节点追加数据  
        name.appendChild(this.document.createTextNode("wanglp"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(name);  
  
        // 声明employee的子节点sex  
        Element sex = this.document.createElement("sex");  
        // 向XML文件sex节点追加数据  
        sex.appendChild(this.document.createTextNode("m"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(sex);  
  
        // 声明employee的子节点age  
        Element age = this.document.createElement("age");  
        // 向XML文件age节点追加数据  
        age.appendChild(this.document.createTextNode("25"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(age);  
  
        // employee节点定义完成，追加到root  
        root.appendChild(employee);  
  
        // 2.创建根节点的子节点employee  
        employee = this.document.createElement("employee");  
  
        // 向根节点添加属性节点  
        id = this.document.createAttribute("id");  
        id.setNodeValue("0002");  
        // 把属性节点对象，追加到达employee节点；  
        employee.setAttributeNode(id);  
  
        // 声明employee的子节点name  
        name = this.document.createElement("name");  
        // 向XML文件name节点追加数据  
        name.appendChild(this.document.createTextNode("huli"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(name);  
  
        // 声明employee的子节点sex  
        sex = this.document.createElement("sex");  
        // 向XML文件sex节点追加数据  
        sex.appendChild(this.document.createTextNode("f"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(sex);  
  
        // 声明employee的子节点age  
        age = this.document.createElement("age");  
        // 向XML文件age节点追加数据  
        age.appendChild(this.document.createTextNode("12"));  
        // 把子节点的属性追加到employee子节点中元素中  
        employee.appendChild(age);  
  
        // employee节点定义完成，追加到root  
        root.appendChild(employee);  
  
        // 获取 TransformerFactory 的新实例。  
        TransformerFactory tf = TransformerFactory.newInstance();  
        // 创建执行从 Source 到 Result 的复制的新 Transformer。能够将源树转换为结果树  
        Transformer transformer = null;  
        try {  
            transformer = tf.newTransformer();  
        } catch (TransformerConfigurationException e) {  
            e.printStackTrace();  
        }  
  
        // 设置转换中实际的输出属性  
        // 指定首选的字符编码  
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
        // indent="yes"|"no".指定了当输出结果树时，Transformer是否可以添加额外的空白  
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");  
        // 声明文件流  
        PrintWriter pw = null;  
        try {  
            pw = new PrintWriter(new FileOutputStream(file));  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            System.out.println("文件没有找到!");  
        }  
        // 充当转换结果的持有者，可以为 XML、纯文本、HTML 或某些其他格式的标记  
        StreamResult result = new StreamResult(pw);  
        // DOMSource implements Source  
        DOMSource source = new DOMSource(document);  
  
        try {  
            // 将 XML Source 转换为 Result  
            transformer.transform(source, result);  
        } catch (TransformerException e) {  
            e.printStackTrace();  
            System.out.println("生成XML文件失败!");  
        }  
        System.out.println("生成XML文件成功!");  
    }  
  
    /** 
     * xml文档的读取操作 
     *  
     * @param file 
     */  
    public void parserXml(File file) {  
        // 获取 DocumentBuilderFactory 的新实例  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        // 使用当前配置的参数创建一个新的 DocumentBuilder 实例  
        DocumentBuilder builder;  
        try {  
            builder = factory.newDocumentBuilder();  
            // 将给定 URI的内容解析为一个 XML文档，并且返回一个新的 DOM Document 对象  
            document = builder.parse(file);  
        } catch (ParserConfigurationException e) {  
            e.printStackTrace();  
        } catch (SAXException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // 获得文档根元素对对象;  
        Element root = document.getDocumentElement();  
        // 获得文档根元素下一级子元素所有元素;  
        NodeList nodeList = root.getChildNodes();  
  
        System.out.print("<employees>");  
        System.out.println(root.getNodeName());  
  
        if (null != root) {  
            for (int i = 0; i < nodeList.getLength(); i++) {  
                Node child = nodeList.item(i);  
  
                // 输出child的属性;  
                System.out.print("<test>");  
                System.out.println(child);  
  
                if (child.getNodeType() == Node.ELEMENT_NODE) {  
                    System.out.print("<id>");  
                    System.out.println(child.getAttributes().getNamedItem("id").getNodeValue());  
                }  
                for (Node node = child.getFirstChild(); node != null; node = node.getNextSibling()) {  
                    if (node.getNodeType() == Node.ELEMENT_NODE) {  
                        if ("name".equals(node.getNodeName())) {  
                            System.out.print("<name>");  
                            System.out.println(node.getFirstChild().getNodeValue());  
                        }  
                    }  
                    if (node.getNodeType() == Node.ELEMENT_NODE) {  
                        if ("sex".equals(node.getNodeName())) {  
                            System.out.print("<sex>");  
                            System.out.println(node.getFirstChild().getNodeValue());  
                        }  
                    }  
                    if (node.getNodeType() == Node.ELEMENT_NODE) {  
                        if ("age".equals(node.getNodeName())) {  
                            System.out.print("<age>");  
                            System.out.println(node.getFirstChild().getNodeValue());  
                        }  
                    }  
                    if (node.getNodeType() == Node.ELEMENT_NODE) {  
                        if ("email".equals(node.getNodeName())) {  
                            System.out.print("<email>");  
                            System.out.println(node.getFirstChild().getNodeValue());  
                        }  
                    }  
                }  
            }  
        }  
        System.out.println("解析完毕");  
    }  
  
    /** 
     * 测试 
     */  
    public static void main(String[] args) {  
  
        // 为什么有类似于这样东西[#text:]  
        // 原因是XML文件元素之间的空白字符也是一个元素，<employees></employees>包含的空白  
    	TestDom dom = new TestDom();  
        File file = new File("E://dom.xml");  
          
        dom.createXml(file);  
        dom.parserXml(file);  
    }  
}
