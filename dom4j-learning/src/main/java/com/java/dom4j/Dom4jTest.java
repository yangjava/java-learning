package com.java.dom4j;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

public class Dom4jTest {
	
    /**
	 说明：
	 Document document = DocumentHelper.createDocument();
	 通过这句定义一个XML文档对象。
	  
	 Element booksElement = document.addElement("books");
	 通过这句定义一个XML元素，这里添加的是根节点。
	 Element有几个重要的方法：
	 l         addComment：添加注释
	 l         addAttribute：添加属性
	 l         addElement：添加子元素
	  
	 最后通过XMLWriter生成物理文件，默认生成的XML文件排版格式比较乱，可以通过OutputFormat类的createCompactFormat()方法或createPrettyPrint()方法格式化输出，默认采用createCompactFormat()方法，显示比较紧凑，这点将在后面详细谈到。
	  
	 生成后的holen.xml文件内容如下：
	 <?xml version="1.0" encoding="UTF-8"?>
				<books><!--books的注释-->
					<book show="yes"><title>book1</title></book>
					<book show="yes"><title>book2</title></book>
					<book show="no"><title>book3</title></book>
					<owner>me</owner>
				</books>
	 
	 
	 */
	 public int createXMLFile(String filename){
	       /** 返回操作结果, 0表失败, 1表成功 */
	       int returnValue = 0;
	       /** 建立document对象 */
	       Document document = DocumentHelper.createDocument();
	       /** 建立XML文档的根books */
	       Element booksElement = document.addElement("books");
	       /** 加入一行注释 */
	       booksElement.addComment("books的注释");
	       /** 加入第一个book节点 */
	       Element bookElement = booksElement.addElement("book");
	       /** 加入show属性内容 */
	       bookElement.addAttribute("show","yes");
	       /** 加入title节点 */
	       Element titleElement = bookElement.addElement("title");
	       /** 为title设置内容 */
	       titleElement.setText("book1");
	      
	       /** 类似的完成后两个book */
	       bookElement = booksElement.addElement("book");
	       bookElement.addAttribute("show","yes");
	       titleElement = bookElement.addElement("title");
	       titleElement.setText("book2");
	       bookElement = booksElement.addElement("book");
	       bookElement.addAttribute("show","no");
	       titleElement = bookElement.addElement("title");
	       titleElement.setText("book3");
	      
	       /** 加入owner节点 */
	       Element ownerElement = booksElement.addElement("owner");
	       ownerElement.setText("me");
	      
	       try{
	           /** 将document中的内容写入文件中 */
	           XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)));
	           writer.write(document);
	           writer.close();
	           /** 执行成功,需返回1 */
	           returnValue = 1;
	       }catch(Exception ex){
	           ex.printStackTrace();
	       }
	             
	       return returnValue;
	    }

	 
	 
	 /**
	     * 修改XML文件中内容,并另存为一个新文件
	     * 重点掌握dom4j中如何添加节点,修改节点,删除节点
	     * @param filename 修改对象文件
	     * @param newfilename 修改后另存为该文件
	     * @return 返回操作结果, 0表失败, 1表成功
	     * 
	     * 说明：
			List list = document.selectNodes("/books/book/@show" );
			list = document.selectNodes("/books/book");
			上述代码通过xpath查找到相应内容。
			通过setValue()、setText()修改节点内容。
			通过remove()删除节点或属性。
	     */
	    public int ModiXMLFile(String filename,String newfilename){
	       int returnValue = 0;
	       try{
	           SAXReader saxReader = new SAXReader();
	           Document document = saxReader.read(new File(filename));
	           /** 修改内容之一: 如果book节点中show属性的内容为yes,则修改成no */
	           /** 先用xpath查找对象 */
	           List list = document.selectNodes("/books/book/@show" );
	           Iterator iter = list.iterator();
	           while(iter.hasNext()){
	              Attribute attribute = (Attribute)iter.next();
	              if(attribute.getValue().equals("yes")){
	                  attribute.setValue("no");
	              }  
	           }
	          
	           /**
	            * 修改内容之二: 把owner项内容改为Tshinghua
	            * 并在owner节点中加入date节点,date节点的内容为2004-09-11,还为date节点添加一个属性type
	            */
	           list = document.selectNodes("/books/owner" );
	           iter = list.iterator();
	           if(iter.hasNext()){
	              Element ownerElement = (Element)iter.next();
	              ownerElement.setText("Tshinghua");
	              Element dateElement = ownerElement.addElement("date");
	              dateElement.setText("2004-09-11");
	              dateElement.addAttribute("type","Gregorian calendar");
	           }
	          
	           /** 修改内容之三: 若title内容为Dom4j Tutorials,则删除该节点 */
	           list = document.selectNodes("/books/book");
	           iter = list.iterator();
	           while(iter.hasNext()){
	              Element bookElement = (Element)iter.next();
	              Iterator iterator = bookElement.elementIterator("title");
	              while(iterator.hasNext()){
	                  Element titleElement=(Element)iterator.next();
	                  if(titleElement.getText().equals("Dom4j Tutorials")){
	                     bookElement.remove(titleElement);
	                  }
	              }
	           }         
	          
	           try{
	              /** 将document中的内容写入文件中 */
	              XMLWriter writer = new XMLWriter(new FileWriter(new File(newfilename)));
	              writer.write(document);
	              writer.close();
	              /** 执行成功,需返回1 */
	              returnValue = 1;
	           }catch(Exception ex){
	              ex.printStackTrace();
	           }
	          
	       }catch(Exception ex){
	           ex.printStackTrace();
	       }
	       return returnValue;
	    }
	 
	 
	    /**默认的输出方式为紧凑方式，默认编码为UTF-8，
	     * 但对于我们的应用而言，一般都要用到中文，并且希望显示时按自动缩进的方式的显示，这就需用到OutputFormat类。
	     * 格式化XML文档,并解决中文问题
	     * 说明：
			OutputFormat format = OutputFormat.createPrettyPrint();
			这句指定了格式化的方式为缩进式，则非紧凑式。
			format.setEncoding("GBK");
			指定编码为GBK。
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format);
			这与前面两个方法相比，多加了一个OutputFormat对象，用于指定显示和编码方式。
	     * @param filename
	     * @return
	     */
	    public int formatXMLFile(String filename){
	       int returnValue = 0;
	       try{
	           SAXReader saxReader = new SAXReader();
	           Document document = saxReader.read(new File(filename));
	           XMLWriter writer = null;
	           /** 格式化输出,类型IE浏览一样 */
	           OutputFormat format = OutputFormat.createPrettyPrint();
	           /** 指定XML编码 */
	           format.setEncoding("GBK");
	           writer= new XMLWriter(new FileWriter(new File(filename)),format);
	           writer.write(document);
	           writer.close();     
	           /** 执行成功,需返回1 */
	           returnValue = 1;    
	       }catch(Exception ex){
	           ex.printStackTrace();
	       }
	       return returnValue;
	    }
	    
	    
	 @Test
	 public void testCreateXMLFile(){
		 
		 Dom4jTest test=new Dom4jTest();
		 test.createXMLFile("E:/work/books.xml");
		 /**
		  * <?xml version="1.0" encoding="UTF-8"?>
				<books><!--books的注释-->
					<book show="yes"><title>book1</title></book>
					<book show="yes"><title>book2</title></book>
					<book show="no"><title>book3</title></book>
					<owner>me</owner>
				</books>
		  */
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
