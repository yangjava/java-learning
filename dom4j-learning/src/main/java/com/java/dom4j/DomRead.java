package com.java.dom4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class DomRead {

            //读取并解析XML文档：
	// 从文件读取XML，输入文件名，返回XML文档
	/**
	 * 其中，reader的read方法是重载的，可以从InputStream, File, Url等多种不同的源来读取。得到的Document对象就带表了整个XML。
              根据本人自己的经验，读取的字符编码是按照XML文件头定义的编码来转换。如果遇到乱码问题，注意要把各处的编码名称保持一致即可。
	 * @param fileName
	 * @return
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	 public Document read(String fileName) throws MalformedURLException, DocumentException {
	       SAXReader reader = new SAXReader();
	       Document document = reader.read(new File(fileName));
	       return document;
	    }
	    /** dom解析*/
	    private Document getDocument() throws Exception {  
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder db = dbf.newDocumentBuilder();  
	        FileInputStream fis = new FileInputStream("xxxx"); //注：MailSendInfo.xml与当前Java文件在同一目录  
	        Document doc = (Document) db.parse(fis);  
	        return doc;  
	    } 
	    
	    
	 //	 取得Root节点
	 //	 读取后的第二步，就是得到Root节点。熟悉XML的人都知道，一切XML分析都是从Root元素开始的
	   public Element getRootElement(Document doc){
	       return doc.getRootElement();
	    }
	
	 /***************遍历XML树 *********/
	 /******** 1.枚举(Iterator)*******/
	  public void IteratorRootElement(Element root){
		   // 枚举所有子节点
		    for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
		       Element element = (Element) i.next();
		       // do something
		    }
		    // 枚举名称为foo的节点
		    for ( Iterator i = root.elementIterator("foo"); i.hasNext();) {
		       Element foo = (Element) i.next();
		       // do something
		    }
		    // 枚举属性
		    for ( Iterator i = root.attributeIterator(); i.hasNext(); ) {
		       Attribute attribute = (Attribute) i.next();
		       // do something
		    } 
		  
		  
	  }

	  /******** 2.递归*******/
	  //递归也可以采用Iterator作为枚举手段，但文档中提供了另外的做法
	  public void treeWalk(Document doc) {
	       treeWalk(getRootElement(doc));
	    }
	    public void treeWalk(Element element) {
	       for (int i = 0, size = element.nodeCount(); i < size; i++)     {
	           Node node = element.node(i);
	           if (node instanceof Element) {
	              treeWalk((Element) node);
	           } else { // do something....
	           }
	       }
	}
	  
	    /******** 3.Visitor模式*******/
//	    最令人兴奋的是DOM4J对Visitor的支持，这样可以大大缩减代码量，并且清楚易懂。了解设计模式的人都知道，Visitor是GOF设计模式之一。其主要原理就是两种类互相保有对方的引用，并且一种作为Visitor去访问许多Visitable。我们来看DOM4J中的Visitor模式(快速文档中没有提供)
//	    只需要自定一个类实现Visitor接口即可。
	  
	    public class MyVisitor extends VisitorSupport {
	           public void visit(Element element){
	               System.out.println(element.getName());
	           }
	           public void visit(Attribute attr){
	               System.out.println(attr.getName());
	           }
	        }
	 
//	        调用：  root.accept(new MyVisitor())
	  
	    /******** XPath支持******
	    XPath技术：主要是用于快速获取所需的节点对象
	    1）导入xPath支持jar包。  jaxen-1.1-beta-6.jar

	    2）使用xpath方法

	             List<Node>  selectNodes("xpath表达式");      查询多个节点对象

	               Node       selectSingleNode("xpath表达式");  查询一个节点对象

	                       3）xPath语法：

	                               /       绝对路径      表示从xml的根位置开始或子元素（一个层次结构）

	                                //      相对路径      表示不分任何层次结构的选择元素。

	                                *       通配符        表示匹配所有元素

	                                []      条件          表示选择什么条件下的元素

	                                @      属性         表示选择属性节点

	                                and     关系         表示条件的与关系（等价于&&）

	                                text()    文本         表示选择文本内容
	     */                           
	    //DOM4J对XPath有良好的支持，如访问一个节点，可直接用XPath选择。
	    public void bar(Document document) {
	        List list = document.selectNodes("//foo/bar");
	        Node node = document.selectSingleNode("//foo/bar/author");
	        String name = ((Node) node).valueOf("@name");
	     }
	 // 例如，如果你想查找XHTML文档中所有的超链接，下面的代码可以实现：
	    public void findLinks(Document document) throws DocumentException {
	        List list = document.selectNodes( "//a/@href" );
	        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
	            Attribute attribute = (Attribute) iter.next();
	            String url = attribute.getValue();
	        }
	     }
	    /**********字符串与XML的转换*********/
         
	    public void cast() throws MalformedURLException, DocumentException{
		    // XML转字符串
		      String textString = read("").asXML();
		  // 字符串转XML
		      Document document = DocumentHelper.parseText("");
	    }

	   
	          //用XSLT转换XML
	    public Document styleDocument(
	    	       Document document,
	    	       String stylesheet
	    	    ) throws Exception {
	    	    // load the transformer using JAXP
	    	    TransformerFactory factory = TransformerFactory.newInstance();
	    	    Transformer transformer = factory.newTransformer(
	    	       new StreamSource( stylesheet )
	    	    );
	    	    // now lets style the given document
	    	    DocumentSource source = new DocumentSource( document );
	    	    DocumentResult result = new DocumentResult();
	    	    transformer.transform( source, result );
	    	    // return the transformed document
	    	    Document transformedDoc = result.getDocument();
	    	    return transformedDoc;
	    	}
	    
	   // 一般创建XML是写文件前的工作，这就像StringBuffer一样容易。
	    public Document createDocument() {
	        Document document = DocumentHelper.createDocument();
	        Element root = document.addElement("root");
	        Element author1 =
	            root
	               .addElement("author")
	               .addAttribute("name", "James")
	               .addAttribute("location", "UK")
	               .addText("James Strachan");
	        Element author2 =
	            root
	               .addElement("author")
	               .addAttribute("name", "Bob")
	               .addAttribute("location", "US")
	               .addText("Bob McWhirter");
	        return document;
	     }
	    
//	    文件输出 一个简单的输出方法是将一个Document或任何的Node通过write方法输出

	    public void outXML(Document document) throws IOException{
		    FileWriter out = new FileWriter( "foo.xml");
		    document.write(out);	
	    }

	    //如果你想改变输出的格式，比如美化输出或缩减格式，可以用XMLWriter类
	    public void write(Document document) throws IOException {
	        // 指定文件
	        XMLWriter writer = new XMLWriter(
	            new FileWriter("output.xml")
	        );
	        writer.write( document );
	        writer.close();
	        // 美化格式
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        writer = new XMLWriter( System.out, format );
	        writer.write( document );
	        // 缩减格式
	        format = OutputFormat.createCompactFormat();
	        writer = new XMLWriter( System.out, format );
	        writer.write( document );
	     }
}
