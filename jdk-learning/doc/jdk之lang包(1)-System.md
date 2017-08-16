System类也是是一个不可继续类，用的final修饰符。

public final class System {...}
System类有三个流，分别是in，out，err三个流，代表着输入流，输出流，错误流。看一下它们的定义。

public final static InputStream in = null;
public final static PrintStream out = null;
public final static PrintStream err = null;
然后这三个流是怎么加载进去的呢？请看：

    public static void setIn(InputStream in) {
        checkIO();
        setIn0(in);
    }
    public static void setOut(PrintStream out) {
        checkIO();
        setOut0(out);
    }
    public static void setErr(PrintStream err) {
        checkIO();
        setErr0(err);
    }
然后我们再接着放下走看看这checkIO和setXXX是什么神奇的东西。

    private static void checkIO() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setIO"));
        }
    }
    private static native void setIn0(InputStream in);
    private static native void setOut0(PrintStream out);
    private static native void setErr0(PrintStream err);
checkIO方法中出现了一个新东西：SecurityManager，SecurityManager类中提供了很多检查权限的方法，例如checkPermission方法会根据安全策略文件描述的权限对操作进行判断是否有操作权限，而checkRead方法则用于判断对文件访问权限。一旦发现没有权限都会抛出安全异常。简单的介绍可以看这里http://www.bubuko.com/infodetail-306759.html。

设置流的SetXX方法都是用native修饰，说明这些方法都是本地方法实现，毕竟这涉及了比较底层的流，所以需要C/C++的支持。在实践中，这些流方法一般用来重定向。

下面来看一下大家都很喜欢的方法：currentTimeMillis，在实践中，大家是不是都很喜欢用它来模拟随机数？因为它比Random方便，Random还要new出来，而currentTimeMillis因为是个static修饰的类方法，直接就好。它表示的是从1970.01.01到现在的毫秒数。可以看出它也是一个本地方法调用。

public static native long currentTimeMillis();
与它一样的还有一个微秒方法：

public static native long nanoTime();
下面我们来看一个在数组操作中经常用到的方法，前面讲到的String，StringBuilder，StringBuffer底层的数据操作很多都是用的这个方法。

public static native void arraycopy(Object src,  int  srcPos, Object dest, int destPos,int length);
参数解释：

src：源端对象，就是要拷贝数据的那个对象。
srcPos：开始位置，就是拷贝数据从哪个位置开始。
dest：目标端对象，就是要把数据拷贝到的那个对象。
destPos：开始位置，就是把数据拷贝到目标对象的哪个位置。
length：长度，就是要从源端拷贝数据的长度。
接下来我们看一下一对比较有用的全局方法。

   public static String setProperty(String key, String value) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key,
                SecurityConstants.PROPERTY_WRITE_ACTION));
        }

        return (String) props.setProperty(key, value);
    }
    public static String getProperty(String key, String def) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key, def);
    }
这对方法看名字就知道是用来设置属性的，它们是全局的类方法，只要使用System.setProperty把一个属性设值，那么在程序的任何一个地方都可以利用System.getProperty方法把这个值拿出来。

如果你想清理掉某个property也是可以的，可以用下面的这个方法：

    public static String clearProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "write"));
        }
        return (String) props.remove(key);
    }
在程序中如果想获取某个环境变量，java也提供了对应的方法。

    public static String getenv(String name) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv."+name));
        }
        return ProcessEnvironment.getenv(name);
    }
如果想获取全部的环境变量也是可以的，请看这个方法。可以看到需要用一个Map对象来接收。

    public static java.util.Map<String,String> getenv() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv.*"));
        }
        return ProcessEnvironment.getenv();
    }
有同学看到System中还有一个gc方法，gc就是垃圾收集，很多人以为只要调用了这个方法，JVM就是帮我们把不用的对象全部清除。其实不然，这个方法的作用主要是提醒虚拟机：程序员希望进行一次垃圾回收。但是它不能保证垃圾回收一定会进行，而且具体什么时候进行是取决于具体的虚拟机的，不同的虚拟机有不同的对策。

    public static void gc() {
        Runtime.getRuntime().gc();
    }
接下来，讲两个有意思的方法。假设我们需要调用本地方法（所谓的本地方法就是别人写好的库函数，例如windows下的dll，linux下的so），怎么加载呢？那就得用下面这两个方法了。

    public static void load(String filename) {
        Runtime.getRuntime().load0(Reflection.getCallerClass(), filename);
    }
    public static void loadLibrary(String libname) {
        Runtime.getRuntime().loadLibrary0(Reflection.getCallerClass(), libname);
    }
这两者的区别：

load 参数为库文件的绝对路径，可以是任意路径。
loadLibrary 参数为库文件名，不包含库文件的扩展名。