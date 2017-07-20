## jdk源码包结构:
	Jre库包含的jar文件(jdk1.6)：resources.jar  rt.jar  jsse.jar  
	jce.jar  charsets.jar  dnsns.jar  localedata.jar等共10个jar文件，
	其中resource.jar为资源包（图片、properties文件）；rt.jar为运行时包，
	
## rt下面的包
   java.*  javax.*  org.*  sun*
   
   
   java.*  javax.*  org.*
   
   1、作为J2SE的API公开接口的一部分，与操作系统无关可以在所有Java平台上运行；
   2、不同的jdk版本会保持兼容不会轻易变化，提供API文档及源码(src.zip)。
   
   sun.*包：
	1、不是API公开接口的一部分，调用sun包的程序并不能确保工作在所有Java平台上，不同的操作系统中的实现可能不相同。
	2、不同的jdk版本sun包中的类也可能不定期的变化，因此sun.*包中的类没有提供API文档及源码。
	
	
	注意：平台无关性是Java语言最大的优势之一，从技术上讲，并不能防止你的程序调用sun.*包中的类。在JDK版本的变迁当中，这些类可能会被删除或转移到其它包路径下，而且它的接口（包括名称、标签等）也很有可能发生变化，在这种情况下，即便你希望程序仅仅运行在SUN的实现平台下，你仍将承受新的版本给你的系统带来破坏的风险。总之，编写依赖于sun.*包的Java程序是不安全的，他们将变得无法移植、破坏了程序的平台无法性、也无法被JDK各版本所兼容。

	sun.*包中的类如何查看源码？
	1、如果只是查看单个类源码，推荐一个网站进行搜索查看，可以很方便的查看API及源码，网站地址：http://www.docjar.com/
	2、如果希望在Eclipse中关联源码，查看源码包下载地址
	
	
## 在Eclipse查看jdk源代码及dt.jar、tools.jar、rt.jar的作用
    1.点 “window”-> "Preferences" -> "Java" -> "Installed JRES" 

	2.此时"Installed JRES"右边是列表窗格，列出了系统中的 JRE 环境，选择你的JRE，然后点边上的 "Edit..."， 会出现一个窗口(Edit JRE) 

	3.选中rt.jar文件的这一项：“c:/program files/java/jre_1.5.0_06/lib/rt.jar” 
	点 左边的“+” 号展开它， 

	4.展开后，可以看到“Source Attachment:(none)”，点这一项，点右边的按钮“Source Attachment...”, 选择你的JDK目录下的 “src.zip”文件 

	5.一路点"ok",结束。

## JDK中rt.jar、tools.jar和dt.jar作用
    dt.jar和tools.jar位于：{Java_Home}/lib/下，而rt.jar位于：{Java_Home}/jre/lib/下,其中： 
	rt.jar是JAVA基础类库，也就是你在java doc里面看到的所有的类的class文件 
	dt.jar是关于运行环境的类库 
	
	tools.jar是工具类库,编译和运行需要的都是toos.jar里面的类分别是sun.tools.java.*; sun.tols.javac.*; 
          在Classpath设置这几个变量，是为了方便在程序中 import；Web系统都用到tool.jar。 
          
	1. rt.jar 
    	rt.jar 默认就在Root Classloader的加载路径里面的，而在Claspath配置该变量是不需要的；同时jre/lib目录下的 
    	其他jar:jce.jar、jsse.jar、charsets.jar、resources.jar都在Root Classloader中 

	2. tools.jar 
   	 	tools.jar 是系统用来编译一个类的时候用到的，即执行javac的时候用到 
 		javac XXX.java 

    	实际上就是运行 
    	java -Calsspath=%JAVA_HOME%\lib\tools.jar xx.xxx.Main XXX.java 
    	javac就是对上面命令的封装 所以tools.jar 也不用加到classpath里面 
	3. dt.jar 
     	dt.jar是关于运行环境的类库,主要是swing的包   在用到swing时最好加上。
		dt.jar是关于运行环境的类库,主要是swing的包   
		tools.jar是关于一些工具的类库   
		rt.jar包含了jdk的基础类库，也就是你在java   doc里面看到的所有的类的class文件	
	
	
	
	
	
	
	