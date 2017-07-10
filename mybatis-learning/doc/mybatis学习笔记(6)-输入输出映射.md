mybatis学习笔记(6)-输入输出映射

# 映射器
mapper.xml文件的配置是mybatis实体对象和查询接口封装SQL操作的配置文件，
其中还包括了查询、修改和删除等操作，并且包含对象的关联映射、数据集合的关联映射、
以及对象的缓存设置和缓存引用等操作，了解这些对手动设置和学习对象的关系很有帮助。
MyBatis的真正的力量是在映射语句。这是奇迹发生的地方。对于所有的力量，映射XML文件相对简单。

映射XML文件只有几个一流的元素（它们应该被定义的顺序）：

cache -配置给定命名空间的缓存。
cache-ref -缓存配置从另一个命名空间的参考。
resultMap -最复杂，最强大的元素，介绍了如何将你的对象从数据库结果集。
parameterMap的 -已过时！老派的参数映射。内联参数是优选的，这些元素在将来可能会被删除。这里没有记录。
sql -可以重用的SQL块，也可以被其他语句引用。
insert -一个映射INSERT语句。
update -映射的UPDATE语句。
delete -映射DELETE语句。
select -一个映射的SELECT语句。


## 选择（select）查询语句
select语句是您使用MyBatis最流行的元素之一。把数据库中有价值的数据筛选，
直到帮你把它找回来，所以许多应用程序查询远远超过他们修改数据。
对于每次插入，更新或者删除，有许多选择。查询是MyBatis的构建原则之一，
原因是这么多的重点和精力放在查询和结果映射，查询元素是非常简单的。

select的属性
属性

描述

ID

此命名空间中的唯一标识符，可以用来引用这条语句。

parameterType

类的完全限定名或别名将传入这条语句的参数。

parameterMap

这是一个废弃的方法引用外部parameterMap。使用内联参数映射和parameterMap属性。

resultType

从这个声明中，将返回的期望类型的类的完全限定名或别名。请注意，在集合的情况下，这应该是集合包含类型，不是类型的集合本身。使用resultType或 resultMap，而不是两个。

resultMap

的命名参照一个外部的resultMap。结果映射MyBatis的是最强大的功能，可以解决他们一个很好的了解，许多复杂映射的情形。使用resultMap或与resultType，而不是两个。

flushCache

设置为true，将导致每当这个语句被称作本地和第二级缓存被刷新。默认值：false。

useCache

设置为true，将导致这句话的结果是二级缓存缓存。默认： true。

timeout

这设定的秒数，将驱动程序等待数据库返回请求之前，抛出一个异常。默认unset（取决于驱动程序）。

fetchSize

这是一个驱动暗示试图将导致驱动程序返回的结果行的编号在此设置的大小等于批次。默认的设置（取决于驱动程序）。

statementType

任何一个STATEMENT，PREPARED或CALLABLE。这导致MyBatis的使用PreparedStatement或CallableStatement的分别。默认值：准备。

resultSetType

任何一个FORWARD_ONLY SCROLL_SENSITIVE的SCROLL_INSENSITIVE。默认的设置（取决于驱动程序）。

databaseId

有配置的databaseIdProvider，MyBatis将会加载与没有databaseId 属性相匹配的当前用的databaseID的所有陈述。如果情况，如果发现相同的语句与和的databaseID无后者将被丢弃。

resultOrdered

这是只适用于嵌套结果select语句：如果这是真的，它是假定嵌套结果载列或组合在一起，这样，当一个新的主要结果行返回到以前的结果行，没有提及会再发生。这允许嵌套结果来填补内存更友好。默认： false。

resultSets

这是只适用于多个结果集。它列出了将语句返回的结果集，并给出每一个名字。名称由逗号分隔。


## 输入映射
是在映射文件中通过parameterType指定输入参数的类型，
类型可以是简单类型、hashmap、pojo的包装类型。
假设现在有个比较复杂的查询需求：完成用户信息的综合查询，
需要传入查询条件很复杂（可能包括用户信息、其它信息，比如商品、订单的），
那么我们单纯的传入一个User就不行了，所以首先我们得根据查询条件，
自定义一个新的pojo，在这个pojo中包含所有的查询条件。

定义包装类型pojo

　　定义一个UserQueryVo类，将要查询的条件包装进去。
这里为了简单起见，就不添加其他的查询条件了，
UserQueryVo中就包含一个User，假设复杂的查询条件在User中都已经包含了。

public class UserQueryVo {
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

配置UserMapper.xml映射文件

    <select id="findUserByUserQueryVoId" parameterType="com.java.mybatis.test3.model.UserQueryVo" resultType="com.java.mybatis.test3.model.User">
        SELECT * FROM  user  WHERE id=#{user.id} and username like '%${user.username}%'
    </select>

我们看到，输入的parameterType的值是我们自己定义的pojo，输出的是User，当然这里的User也可以换成另一个用户自定义的pojo，包含用户所需要的条件，都行，不仅仅局限为User。然后查询条件使用OGNL表达式，取出UserQueryVo中User的相应属性即可。 
　　然后别忘了在SqlMapperConfig.xml中配置好这个UserMapper.xml映射文件。

定义Mapper接口
public interface UserMapper {
    public List<User> findUserByUserQueryVoId(UserQueryVo userQueryVo);
}

测试
        ...
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserQueryVo userQueryVo=new UserQueryVo();
        User userQuery=new User();
        userQuery.setId(1);
        userQuery.setUsername("admin");
        userQueryVo.setUser(userQuery);
        List<User> user=userMapper.findUserByUserQueryVoId(userQueryVo);
        System.out.println(user);
		...
		
[com.java.mybatis.test3.dao.UserMapper.findUserByUserQueryVoId] - ==>  Preparing: SELECT * FROM user WHERE id=? and username like '%admin%' 
[com.java.mybatis.test3.dao.UserMapper.findUserByUserQueryVoId] - ==> Parameters: 1(Integer)
[com.java.mybatis.test3.dao.UserMapper.findUserByUserQueryVoId] - <==      Total: 1

使用Map传递参数
 
 UserMapper.xml
 
  <select id="findUserByIdAndName" parameterType="map" resultType="com.java.mybatis.test3.model.User">
        SELECT * FROM  user  WHERE id=#{id}  AND username LIKE concat ('%',#{username},'%')
    </select> 
	
 userMapper.java 接口
 
 public interface UserMapper {
    User findUserByIdAndName(Map<String,Object> map);
  }
测试类
    @Test
    public void findUserByIdAndNameTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id", 1);
        map.put("username", "admin");
        User user=userMapper.findUserByIdAndName(map);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
[com.java.mybatis.test3.dao.UserMapper.findUserByIdAndName] - ==>  Preparing: SELECT * FROM user WHERE id=? AND username LIKE concat ('%',?,'%') 
[com.java.mybatis.test3.dao.UserMapper.findUserByIdAndName] - ==> Parameters: 1(Integer), admin(String)
[com.java.mybatis.test3.dao.UserMapper.findUserByIdAndName] - <==      Total: 1
User [id=1, username=admin, password=admin, birthday=Fri Jan 13 00:00:00 CST 1989, sex=1, address=北京市]
	
问题
使用Map传递参数,会使代码的可读性变差,mybaits可以使用param参数

 UserMapper.xml
     parameterType 因为类型不一样 所以可以不填写
     <select id="findUserByIdAndNameParam"  resultType="com.java.mybatis.test3.model.User">
        SELECT * FROM  user  WHERE id=#{id}  AND username LIKE concat ('%',#{username},'%')
    </select> 
 userMapper.java 接口
 
 public interface UserMapper {
    User findUserByIdAndNameParam(@Param("id") int id,@Param("username") String name);
  }

  
     @Test
    public void findUserByIdAndNameParamTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=userMapper.findUserByIdAndNameParam(1, "admin");
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
 
 
insert,update和delete属性
插入，更新和删除属性

属性

描述

ID

此命名空间中的唯一标识符，可以用来引用这条语句。

parameterType

类的完全限定名或别名将传入这条语句的参数。

parameterMap

这是一个废弃的方法引用外部parameterMap。使用内联参数的映射和parameterType属性。

flushCache

设置为true，将导致第二级和本地缓存刷新每当这个语句被称作。默认：true的插入，更新和删除语句。

timeout

这设置最大秒数将驱动程序等待数据库返回请求之前，抛出一个异常。默认的unset（取决于驱动程序）。

statementType

任何一个STATEMENT，PREPARED或CALLABLE。这导致MyBatis的使用PreparedStatement或CallableStatement的分别。默认值：准备。

useGeneratedKeys

（插入）这就告诉MyBatis使用JDBC getGeneratedKeys方法来检索数据库（如自动递增字段在关系型数据库，如MySQL或SQL Server）内部产生的密钥。默认值：false

keyProperty

（插入）标识的属性到MyBatis将会设置键返回值getGeneratedKeys，或由selectKey元素的子元素的insert语句。默认：未设置。

keyColumn

（插入）设置生成的键的表中的列名。只需要在特定的数据库（如PostgreSQL）键列时，是不是第一个表中的列。

databaseId

有配置的databaseIdProvider，MyBatis将会加载与没有databaseId 属性相匹配的当前用的databaseID的所有陈述。如果情况，如果发现相同的语句与和的databaseID无后者将被丢弃。

## 主键回填

<insert id="insertUser" parameterType="com.java.mybatis.test4.model.User" 
useGeneratedKeys="true"  keyProperty="id">
 
 <selectKey keyProperty="id" order="AFTER" resultType="java.lang.Integer">
          SELECT LAST_INSERT_ID()
        </selectKey>
		
		
selectKey元素属性

属性

描述

keyProperty

selectKey元素语句的结果应设置目标属性。

resultType

不同的结果。的MyBatis通常可以算出来，但它不会伤害它添加到务必。MyBatis允许任何简单类型，被用来作为关键，包括字符串。

order

这可以设置BEFORE或AFTER。如果设置BEFORE，那么它会首先选择主键，设置keyProperty，然后执行插入语句。如果设置后，执行插入语句，然后selectKey语句-这是很常见的数据库，如Oracle，可能已经嵌入序列调用INSERT语句。

statementType

同上，MyBatis的支持声STATEMENT，PREPARED和CALLABLE语句类型分别映射到声明的PreparedStatement和CallableStatement的。 



字符串替换

默认情况下，使用＃{}语法会导致MyBatis使生成的PreparedStatement性能和安全设定值，对PreparedStatement的参数（例如？）。
虽然这是更安全，速度更快，而且几乎总是首选，
有时你只是想直接注入到SQL语句中的字符串未修改。
例如，对于ORDER BY，你可能会使用这样的事情：
ORDER BY ${columnName}
这里的MyBatis不会修改或转义字符串。注：这不是安全地接受用户的输入，并提供一份声明中这样未修改。这将导致潜在的SQL注入攻击，
因此，你应该允许用户输入这些字段中，或总是执行自己的逃逸和检查。

## 输出映射

输出映射中同样有很多中对象类型，这里也主要总结一下输出pojo对象。
mybatis中的与输出映射有关的resultType就不再叙述了，
这里主要总结一下另一个resultMap的使用方法。 
我们知道，通过resultType输出映射的时候，查询出来的列名和pojo中对应的属性名要一致才可以做正确的映射，
如果不一致就会映射错误。但是如果不一致呢？该如何解决这个问题呢？这就要使用resultMap来映射了。 
假设现在映射文件中有个sql语句：SELECT id id_,username username_ FROM USER WHERE id=#{id}，
从这个sql语句中可以看出，查询出了id和username两列，但是都起了别名了，
也就是说，如果我们现在用resultType去映射到User中的话，肯定会出问题，
所以我们现在要定义一个resultMap来做查询结果列与User的属性之间的一个映射。

输出映射有两种方式

- `resultType`
- `resultMap`


## resultType

- 使用`resultType`进行输出映射，只有查询出来的列名和pojo中的属性名一致，该列才可以映射成功。
- 如果查询出来的列名和pojo中的属性名全部不一致，没有创建pojo对象。
- 只要查询出来的列名和pojo中的属性有一个一致，就会创建pojo对象。


### 输出简单类型


- mapper.xml

 <!-- 用户信息综合查询总数
        parameterType：指定输入类型和findUserList一样
        resultType：输出结果类型
    -->
   <select id="findUserCount" parameterType="com.java.mybatis.test3.model.UserQueryVo" resultType="int">
        SELECT count(*) FROM  user  WHERE id=#{user.id} and username like '%${user.username}%'
    </select>

- mapper.java

public interface UserMapper {  
    public int findUserCount(UserQueryVo userQueryVo);
}

    @Test
    public void findUserCountTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserQueryVo userQueryVo=new UserQueryVo();
        User userQuery=new User();
        userQuery.setId(1);
        userQuery.setUsername("admin");
        userQueryVo.setUser(userQuery);
        int userCount=userMapper.findUserCount(userQueryVo);
        System.out.println(userCount);
        // 释放资源
        sqlSession.close();

    }



- 小结

查询出来的结果集只有一行且一列，可以使用简单类型进行输出映射。


###	输出pojo对象和pojo列表

**不管是输出的pojo单个对象还是一个列表（list中包括pojo），在mapper.xml中`resultType`指定的类型是一样的。**

在mapper.java指定的方法返回值类型不一样：

- 输出单个pojo对象，方法返回值是单个对象类型

```java
//根据id查询用户信息
public User findUserById(int id) throws Exception;
```

- 输出pojo对象list，方法返回值是List<Pojo>

```java
//根据用户名列查询用户列表
public List<User> findUserByName(String name) throws Exception;
```


**生成的动态代理对象中是根据mapper方法的返回值类型确定是调用`selectOne`(返回单个对象调用)还是`selectList` （返回集合对象调用 ）.**



## resultMap

mybatis中使用resultMap完成高级输出结果映射。(一对多，多对多)


###	resultMap使用方法 

如果查询出来的列名和pojo的属性名不一致，通过定义一个resultMap对列名和pojo属性名之间作一个映射关系。

1.定义resultMap

2.使用resultMap作为statement的输出映射类型

- 定义reusltMap

userMapper.xml
	 <resultMap type="com.java.mybatis.test3.model.UserMap" id="userResultMap">
	 	<!-- id表示查询结果集中唯一标识 
	 	column：查询出来的列名
	 	property：type指定的pojo类型中的属性名
	 	最终resultMap对column和property作一个映射关系 （对应关系）
	 	-->
	 	<id column="id" property="userId"/>
	 	<!-- 
	 	result：对普通名映射定义
	 	column：查询出来的列名
	 	property：type指定的pojo类型中的属性名
	 	最终resultMap对column和property作一个映射关系 （对应关系）
	 	 -->
	 	<result column="sex" property="userSex"/>
	 </resultMap>

- 使用resultMap作为statement的输出映射类型

<!-- 使用resultMap进行输出映射
        resultMap：指定定义的resultMap的id，如果这个resultMap在其它的mapper文件，前边需要加namespace
        -->
   <select id="findUserMap" parameterType="int" resultMap="userResultMap">
        SELECT id AS userId,sex AS userSex FROM  user  WHERE id=#{value}
    </select>



- mapper.java

//根据id查询用户信息，使用resultMap输出
public interface UserMapper {   
    UserMap findUserMap(Integer id);
}

- 测试代码

    @Test
    public void findUserMapTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserMap user=userMapper.findUserMap(1);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
	
[com.java.mybatis.test3.dao.UserMapper.findUserMap] - ==>  Preparing: SELECT id AS userId,sex AS userSex FROM user WHERE id=? 
[com.java.mybatis.test3.dao.UserMapper.findUserMap] - ==> Parameters: 1(Integer)
[com.java.mybatis.test3.dao.UserMapper.findUserMap] - <==      Total: 1
UserMap [userId=1, userSex=1, address=null]

### 小结 

使用resultType进行输出映射，只有查询出来的列名和pojo中的属性名一致，该列才可以映射成功。

如果查询出来的列名和pojo的属性名不一致，通过定义一个resultMap对列名和pojo属性名之间作一个映射关系。

问题 
1. mybaits 自动映射 :如果POJO的属性名和column和列名相同

2.驼峰映射: sqlMapConfig 中autoMappingBehavior