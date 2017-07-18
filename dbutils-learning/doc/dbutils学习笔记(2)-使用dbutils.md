## 使用DBUtils
其实只是使用的话，只看两个类（DbUtils 和QueryRunner）和一个接口（ResultSethandler）就可以了。

1，DbUtils

DbUtils	

是一个为做一些诸如关闭连接、装载JDBC驱动程序之类的常规工作提供有用方法的类，
它里面所有的方法都是静态的。
这个类里的重要方法有：

close()：

    DbUtils类提供了三个重载的关闭方法。这些方法检查所提供的参数是不是NULL，
    如果不是的话，它们就关闭连接、声明和结果集（ResultSet）。
	
CloseQuietly:

这一方法不仅能在连接、声明或者结果集（ResultSet）为NULL情况下避免关闭，
还能隐藏一些在程序中抛出的SQLEeception。如果你不想捕捉这些异常的话，这对你是非常有用的。
在重载CloseQuietly方法时，特别有用的一个方法是closeQuietly
(Connection conn,Statement stmt,ResultSet rs)，
这是因为在大多数情况下，连接、声明和结果集（ResultSet）
是你要用的三样东西，而且在最后的块你必须关闭它们。
使用这一方法，你最后的块就可以只需要调用这一方法即可。

CommitAndCloseQuietly(Connection conn)：

这一方法用来提交连接，然后关闭连接，并且在关闭连接时不向上抛出在关闭时发生的一些SQL异常。
LoadDriver(String driveClassName):这一方法装载并注册JDBC驱动程序，如果成功就返回TRUE。
使用这种方法，你不需要去捕捉这个异常ClassNotFoundException。使用loadDrive方法，
编码就变得更容易理解，
你也就得到了一个很好的Boolean返回值，这个返回值会告诉你驱动类是不是已经加载成功了。

2,ResultSetHandler

这一接口执行处理一个jaca.sql.ResultSet，将数据转变并处理为任何一种形式，
这样有益于其应用而且使用起来更容易。
这一组件提供了ArrayHandler, 
ArrayListHandler, BeanHandler, BeanListHandler, MapHandler, MapListHandler,
 and ScalarHandler等执行程序。
ResultSetHandler接口提供了一个单独的方法：
Object handle (java.sql.ResultSet .rs)。
因此任何ResultSetHandler 的执行需要一个结果集（ResultSet）作为参数传入，
然后才能处理这个结果集，再返回一个对象。
因为返回类型是java.lang.Object，所以除了不能返回一个原始的Java类型之外，
其它的返回类型并没有什么限制。
如果你发现这七个执行程序中没有任何一个提供了你想要的服务，
你可以自己写执行程序并使用它。

3,QreryRunner

这个类使执行SQL查询简单化了，它与ResultSetHandler串联在一起有效地履行着一些平常的任务，
它能够大大减少你所要写的编码。
QueryRunner类提供了两个构造器：其中一个是一个空构造器，
另一个则拿一个 javax.sql.DataSource 来作为参数。
因此，在你不用为一个方法提供一个数据库连接来作为参数的情况下，
提供给构造器的数据源(DataSource) 被用来获得一个新的连接并将继续进行下去。
 
这一类中的重要方法包括以下这些：

query(Connection conn, String sql, Object[] params, ResultSetHandler rsh):

这一方法执行一个选择查询，在这个查询中，对象阵列的值被用来作为查询的置换参数。
这一方法内在地处理PreparedStatement 和ResultSet  的创建和关闭。

ResultSetHandler对把从 ResultSet得来的数据转变成一个更容易的或是应用程序特定的格式来使用。

query(String sql, Object[] params, ResultSetHandler rsh):

这几乎与第一种方法一样；唯一的不同在于它不将数据库连接提供给方法，
并且它是从提供给构造器的数据源(DataSource) 或使用的setDAtaSource 方法中重新获得的。

query(Connection conn, String sql, ResultSetHandler rsh):

这执行一个不要参数的选择查询。

update(Connection conn, String sql, Object[] params):

这一方法被用来执行一个插入、更新或删除操作。对象阵列为声明保存着置换参数。