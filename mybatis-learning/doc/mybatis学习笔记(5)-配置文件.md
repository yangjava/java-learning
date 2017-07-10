mybatis学习笔记(5)-配置文件


SqlMapConfig.xml中配置的内容和顺序如下

- properties（属性）
- settings（全局配置参数）
- **typeAliases（类型别名）**
- typeHandlers（类型处理器）
- *objectFactory（对象工厂）*
- *plugins（插件）*
- environments（环境集合属性对象）
  - environment（环境子属性对象）
     - transactionManager（事务管理）
     - dataSource（数据源）
- **mappers（映射器）**

## properties(属性)

通过properties添加配置

<properties >  
 <property name="jdbc.driver" value="com.mysql.jdbc.Driver"/>
 <property name="jdbc.url" value="jdbc:mysql://localhost:3306/mybatistest?characterEncoding=utf-8"/>
 <property name="jdbc.username" value="root"/>
 <property name="jdbc.password" value="root"/>
</properties>

<!-- 和spring整合后 environments配置将废除-->
<environments default="development">
    <environment id="development">
        <!-- 使用jdbc事务管理，事务控制由mybatis-->
        <transactionManager type="JDBC" />
        <!-- 数据库连接池,由mybatis管理-->
        <dataSource type="POOLED">
            <property name="driver" value="${jdbc.driver}" />
            <property name="url" value="${jdbc.url}" />
            <property name="username" value="${jdbc.username}" />
            <property name="password" value="${jdbc.password}" />
        </dataSource>
    </environment>
</environments>

将数据库连接参数单独配置在db.properties中，只需要在SqlMapConfig.xml中加载db.properties的属性值。在SqlMapConfig.xml中就不需要对数据库连接参数硬编码。

将数据库连接参数只配置在db.properties中。原因：方便对参数进行统一管理，其它xml可以引用该db.properties。

```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mybatistest?characterEncoding=utf-8
jdbc.username=root
jdbc.password=root
```

在sqlMapConfig.xml加载属性文件：


<properties resource="db.properties">
        <!--properties中还可以配置一些属性名和属性值  -->
        <!-- <property name="jdbc.driver" value=""/> -->
</properties>

<!-- 和spring整合后 environments配置将废除-->
<environments default="development">
    <environment id="development">
        <!-- 使用jdbc事务管理，事务控制由mybatis-->
        <transactionManager type="JDBC" />
        <!-- 数据库连接池,由mybatis管理-->
        <dataSource type="POOLED">
            <property name="driver" value="${jdbc.driver}" />
            <property name="url" value="${jdbc.url}" />
            <property name="username" value="${jdbc.username}" />
            <property name="password" value="${jdbc.password}" />
        </dataSource>
    </environment>
</environments>


注意： MyBatis 将按照下面的顺序(优先级)来加载属性：

- 在`properties`元素体内定义的属性首先被读取。 
- 然后会读取`properties`元素中resource或url加载的属性，它会覆盖已读取的同名属性。 
- 最后读取`parameterType`传递的属性，它会覆盖已读取的同名属性。

建议：

- 不要在`properties`元素体内添加任何属性值，只将属性值定义在properties文件中。
- 在properties文件中定义属性名要有一定的特殊性，如：jdbc.driver
--首选propeties文件,不要混用


## settings(全局参数配置)

mybatis框架在运行时可以调整一些运行参数,比如：开启二级缓存、开启延迟加载...

全局参数将会影响mybatis的运行行为。

<!-- settings是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。 -->
	<settings>
		<!-- 该配置影响的所有映射器中配置的缓存的全局开关。默认值true -->
	  <setting name="cacheEnabled" value="true"/>
	  <!--延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置fetchType属性来覆盖该项的开关状态。默认值false  -->
	  <setting name="lazyLoadingEnabled" value="true"/>
	  	<!-- 是否允许单一语句返回多结果集（需要兼容驱动）。 默认值true -->
	  <setting name="multipleResultSetsEnabled" value="true"/>
	  <!-- 使用列标签代替列名。不同的驱动在这方面会有不同的表现， 具体可参考相关驱动文档或通过测试这两种不同的模式来观察所用驱动的结果。默认值true -->
	  <setting name="useColumnLabel" value="true"/>
	  <!-- 允许 JDBC 支持自动生成主键，需要驱动兼容。 如果设置为 true 则这个设置强制使用自动生成主键，尽管一些驱动不能兼容但仍可正常工作（比如 Derby）。 默认值false  -->
	  <setting name="useGeneratedKeys" value="false"/>
	 <!--  指定 MyBatis 应如何自动映射列到字段或属性。 NONE 表示取消自动映射；PARTIAL 只会自动映射没有定义嵌套结果集映射的结果集。 FULL 会自动映射任意复杂的结果集（无论是否嵌套）。 --> 
	 <!-- 默认值PARTIAL -->
	  <setting name="autoMappingBehavior" value="PARTIAL"/>
	  
	  <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
	 <!--  配置默认的执行器。SIMPLE 就是普通的执行器；REUSE 执行器会重用预处理语句（prepared statements）； BATCH 执行器将重用语句并执行批量更新。默认SIMPLE  -->
	  <setting name="defaultExecutorType" value="SIMPLE"/>
	  <!-- 设置超时时间，它决定驱动等待数据库响应的秒数。 -->
	  <setting name="defaultStatementTimeout" value="25"/>
	  
	  <setting name="defaultFetchSize" value="100"/>
	  <!-- 允许在嵌套语句中使用分页（RowBounds）默认值False -->
	  <setting name="safeRowBoundsEnabled" value="false"/>
	  <!-- 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射。  默认false -->
	  <setting name="mapUnderscoreToCamelCase" value="false"/>
	  <!-- MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。
	  		 默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。
	   		若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不会共享数据。  -->
	  <setting name="localCacheScope" value="SESSION"/>
	  <!-- 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。 某些驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。  -->
	  <setting name="jdbcTypeForNull" value="OTHER"/>
	<!--   指定哪个对象的方法触发一次延迟加载。  -->
	  <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
	</settings>
	
	
	
## typeAliases(类型别名)

在mapper.xml中，定义很多的statement，statement需要`parameterType`指定输入参数的类型、需要`resultType`指定输出结果的映射类型。

如果在指定类型时输入类型全路径，不方便进行开发，可以针对`parameterType`或`resultType`指定的类型定义一些别名，在mapper.xml中通过别名定义，方便开发。


- mybatis默认支持别名

|  别名  |  映射的类型  |
|:---    |  :----     |
|_byte   | 	byte | 
|_long 	 |  long | 
|_short  |	short| 
|_int| 	int| 
|_integer| 	int| 
|_double| 	double|
|_float| 	float |
|_boolean| 	boolean|
|string |	String |
|byte |	Byte |
|long |	Long| 
|short |	Short |
|int |	Integer |
|integer |	Integer |
|double |	Double |
|float |	Float |
|boolean| 	Boolean |
|date |	Date |
|decimal |	BigDecimal |
|bigdecimal| BigDecimal| 


- 自定义别名
  -	单个别名定义 
  -	批量定义别名（常用）	
 在mybatis源码中 org.apache.ibatis.type.TypeAliasRegistry 包含别名注册的类
public class TypeAliasRegistry {

  private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<String, Class<?>>();

  public TypeAliasRegistry() {
    registerAlias("string", String.class);

    registerAlias("byte", Byte.class);
    registerAlias("long", Long.class);
    registerAlias("short", Short.class);
    registerAlias("int", Integer.class);
    registerAlias("integer", Integer.class);
    registerAlias("double", Double.class);
    registerAlias("float", Float.class);
    registerAlias("boolean", Boolean.class);	
	
	针对单个类起别名
<typeAliases>
	<typeAlias type="com.java.mybatis.test1.model.User" alias="user" />
</typeAliases>	
	针对包下所有类起别名
<typeAliases>
	<package name="com.java.mybatis.test1.model"/>
</typeAliases>

使用注解起别名
@Alias("user")
public class User {
}

## typeHandlers(类型处理器)

mybatis中通过typeHandlers完成jdbc类型和java类型的转换。例如：

```xml
<select id="findUserById" parameterType="int" resultType="user">
		select * from user where id = #{id}
</select>
源码见 org.apache.ibatis.type.TypeHandlerRegistry

 public TypeHandlerRegistry() {
    register(Boolean.class, new BooleanTypeHandler());
    register(boolean.class, new BooleanTypeHandler());
    register(JdbcType.BOOLEAN, new BooleanTypeHandler());
    register(JdbcType.BIT, new BooleanTypeHandler());

    register(Byte.class, new ByteTypeHandler());
    register(byte.class, new ByteTypeHandler());
    register(JdbcType.TINYINT, new ByteTypeHandler());

    register(Short.class, new ShortTypeHandler());
    register(short.class, new ShortTypeHandler());
    register(JdbcType.SMALLINT, new ShortTypeHandler());

    register(Integer.class, new IntegerTypeHandler());
    register(int.class, new IntegerTypeHandler());
    register(JdbcType.INTEGER, new IntegerTypeHandler());

    register(Long.class, new LongTypeHandler());
    register(long.class, new LongTypeHandler());
	
	
	
|类型处理器	|Java类型|	JDBC类型|
|:---|:---|:----|
|BooleanTypeHandler | Boolean，boolean |任何兼容的布尔值|
|ByteTypeHandler |	Byte，byte |	任何兼容的数字或字节类型|
|ShortTypeHandler |	Short，short |	任何兼容的数字或短整型|
|IntegerTypeHandler| 	Integer，int |	任何兼容的数字和整型|
|LongTypeHandler |	Long，long 	|任何兼容的数字或长整型|
|FloatTypeHandler |	Float，float |	任何兼容的数字或单精度浮点型|
|DoubleTypeHandler |	Double，double |	任何兼容的数字或双精度浮点型|
|BigDecimalTypeHandler |	BigDecimal |	任何兼容的数字或十进制小数类型|
|StringTypeHandler |	String |	CHAR和VARCHAR类型|
|ClobTypeHandler |	String |	CLOB和LONGVARCHAR类型|
|NStringTypeHandler| 	String |	NVARCHAR和NCHAR类型|
|NClobTypeHandler 	|String |	NCLOB类型
|ByteArrayTypeHandler| 	byte[] |	任何兼容的字节流类型|
|BlobTypeHandler |	byte[] |	BLOB和LONGVARBINARY类型|
|DateTypeHandler |	Date（java.util）|	TIMESTAMP类型|
|DateOnlyTypeHandler |	Date（java.util）|	DATE类型|
|TimeOnlyTypeHandler |	Date（java.util）|	TIME类型|
|SqlTimestampTypeHandler |	Timestamp（java.sql）|	TIMESTAMP类型|
|SqlDateTypeHandler |	Date（java.sql）|	DATE类型|
|SqlTimeTypeHandler |	Time（java.sql）|	TIME类型|
|ObjectTypeHandler| 	任意|	其他或未指定类型|
|EnumTypeHandler |	Enumeration类型|	VARCHAR-任何兼容的字符串类型，作为代码存储（而不是索引）|

注意:
1.数值类型的精度,数据库中的类型int  double decimal与java类型的精度不一样
2.时间只到日期选用DateOnlyTypeHandler,精度到秒选用SqlTimestampTypeHandler

用户自定义TypeHandler 

  sqlMapConfig.xml
  
	<typeHandlers>
		<typeHandler 
			javaType="string" jdbcType="VARCHAR" handler="com.java.mybatis.test1.MyStringTypeHandler"/>
	</typeHandlers> 


userMapper.xml
  <resultMap id="BaseResultMap" type="com.java.mybatis.test1.model.User" >
    <id column="id" property="id" jdbcType="INTEGER" />
    <result column="username" property="username" jdbcType="VARCHAR" />
    <result column="password" property="password" typeHandler="com.java.mybatis.test1.MyStringTypeHandler" />
    <result column="birthday" property="birthday" jdbcType="DATE" />
    <result column="sex" property="sex" jdbcType="CHAR" />
    <result column="address" property="address" jdbcType="VARCHAR" />
  </resultMap>
  
  java 类
@MappedJdbcTypes(value={JdbcType.VARCHAR})
@MappedTypes(value={String.class})
public class MyStringTypeHandler extends BaseTypeHandler<String>{

关于枚举类型

sqlMapConfig.xml

<typeHandler 
			javaType="com.java.mybatis.test1.model.SexEnum" 
			handler="org.apache.ibatis.type.EnumOrdinalTypeHandler"/>

userMapper.xml			
<result column="birthday" property="birthday" jdbcType="DATE" />
    <result column="sex" property="sex"  typeHandler="org.apache.ibatis.type.EnumOrdinalTypeHandler" />
	
枚举类

public enum SexEnum {
	
	MALE(1,"男"),FMALE(0,"女");
	
	private int id;
	private String name;
	private SexEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static SexEnum getSex(int id){
		if("0".equals(id)){
			return FMALE;
		}else if("1".equals(id)){
			return MALE;
		}
		return null;
	}

}

 POJO类
public class User {
    private Integer id;

    private String username;

    private String password;

    private Date birthday;

    private SexEnum sex;
	
	...
	}
	
##	environments
	
 	<environments default="development">
		<environment id="development">
			<!-- 使用jdbc事务管理，事务控制由mybatis -->
			<transactionManager type="JDBC" />
			<!-- 数据库连接池,由mybatis管理 -->
			<dataSource type="POOLED">
				<property name="driver" value="com.mysql.jdbc.Driver" />
				<property name="url"
					value="jdbc:mysql://localhost:3306/mybatistest?useSSL=false" />
				<property name="username" value="root" />
				<property name="password" value="root" />
			</dataSource>
		</environment>
	</environments>
	
默认的环境ID（比如：default:"development"）development : 开发模式    work : 工作模式
每个 environment 元素定义的环境 ID（比如:id=”development”）。
事务管理器的配置（比如:type=”JDBC”）
数据源的配置（比如:type=”POOLED”）。
默认的环境和环境 ID 是一目了然的。随你怎么命名，只要保证默认环境要匹配其中一个环境ID

事务管理器（transactionManager）

事务管理器有两种：type="[ JDBC | MANAGED ]":

JDBC:这个配置就是直接使用了JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务范围。

MANAGED ：这个配置从来都不提交和回滚一个连接，而是让容器来管理事务的整个生命周期（比如JEE应用服务的上下文）。
默认情况下他会关闭连接，然而一些容器并不希望这样，因此需要将closeConnection属性设置为false来阻止它默认的关闭行为。

<transactionManager type="MANAGED">  
  <property name="closeConnection" value="false"/>  
</transactionManager>  
如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器， 
因为 Spring 模块会使用自带的管理器来覆盖前面的配置。

dataSource--数据源

dataSource元素使用标准的JDBC数据源接口来配置JDBC连接对象的资源。

三种内建的数据源类型：type=[ UNPOOLED | POOLED | JNDI ]
UNPOOLED - 这个数据源的实现只是每次请求时打开和关闭连接。虽然一点慢，他对在及时可用连接方面没有性能要求的简单应用程序是一个很好的选择，不同的数据库在这方面表现也是不一样的，所以对某些数据库来说使用连接池并不重要，这个配置也是理想的。UNPOOLED类型的数据源仅仅需要配置一下5种属性：

driver：JDBC驱动的java类的完全限定名
url：数据库的JDBC URL地址
userName： 登录数据库的用户名
password ： 登录数据库的密码
dedaultTransactionIsolationLevel– 默认的连接事务隔离级别。
作为可选项，你也可以传递属性给数据库驱动，要这样做，属性的前缀为“driver.”,例如：driver.encoding=UTF8

这将通过DriverManager.getConnection(url,driverProperties) 方法传递值为UTF8的encoding 属性给数据库驱动。

POOLED - 这种数据源的实现利用“池”的概念将JDBC连接对象组织起来，避免了创建先的连接实例时所必须的初始化和认证时间。这是一种使得并发WEb应用快速响应请求的流行的处理方式。

除了上述提到UNPOOLED下的属性外，还有以下属性来配置POOLED的数据源：

poolMaximumActiveConnections-在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10
poolMaximumIdleConnections - 任意时间可能存在的空闲连接数。
poolMaximumCheckoutTime - 再被强制返回之前，池中连接被检出时间，默认值2W毫秒  即20s
poolTimeToWait - 这是一个底层设置，如果获取连接花费的相当长的时间，它会给连接池打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：2W毫秒即 20 s
PoolPingQuery - 发送到数据库的侦测查询，用来检验连接是否处在正常工作秩序中并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息
PoolPingConnectionsNotUsedFor -配置 poolPingQuery 的使用频度。这可以被设置成匹配具体的数据库连接超时时间，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。
JDNI- 这个数据源的实现是为了能在如EJB或应用服务器这类容器中使用，容器可以集中在外部配置数据源，然后放置一个JDNI上下文的引用。这种数据源只需要两个属性：

initial_context - 这个属性用来InitailContext中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么 data_source 属性将会直接从 InitialContext 中寻找
data_source - 这是引用数据源实例位置的上下文的路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找
和其他数据源配置类似，可以通过添加前缀“env.”直接把属性传递给初始上下文。比如：

  env.encoding=UTF8

这就是在初始上下文（InitialContext）实例化时往它的构造方法传递值为UTF8 的 encoding 属性。	
	

## mappers(映射配置)

- 通过resource加载单个映射文件

```xml
<!--通过resource方法一次加载一个映射文件 -->
<mapper resource="mapper/UserMapper.xml"/>
```

- 通过mapper接口加载单个mapper

```xml
 <!-- 通过mapper接口加载单个 映射文件
        遵循一些规范：需要将mapper接口类名和mapper.xml映射文件名称保持一致，且在一个目录中
        上边规范的前提是：使用的是mapper代理方法
         -->
<mapper class="com.iot.mybatis.mapper.UserMapper"/> 
```

目录示例

```
com.iot.mybatis.mapper------------------package包
           |----UserMapper.java
           |----UserMapper.xml
              
```

- 批量加载mapper(推荐使用)

```xml
<!-- 批量加载mapper
		指定mapper接口的包名，mybatis自动扫描包下边所有mapper接口进行加载
		遵循一些规范：需要将mapper接口类名和mapper.xml映射文件名称保持一致，且在一个目录 中
		上边规范的前提是：使用的是mapper代理方法
		 -->
<package name="com.iot.mybatis.mapper"/>	

mappers映射配置：

1）通过resource加载单个映射文件

2）通过mapper接口加载单个mapper

3）批量加载mapper(推荐使用)