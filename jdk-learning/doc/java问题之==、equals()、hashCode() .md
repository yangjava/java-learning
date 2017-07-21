在java编程或者面试中经常会遇到 == 、equals()的比较。自己看了看源码，结合实际的编程总结一下。

1. == 

　　java中的==是比较两个对象在JVM中的地址。比较好理解。看下面的代码：

1 public class ComAddr{
2     public static void main(String[] args) throws Exception {
3         String s1 = "nihao";
4         String s2 = "nihao";
5         String s3 = new String("nihao");
6         System.out.println(s1 == s2);    //    true
7         System.out.println(s1 == s3);    //    false
8     }
9 }
　　上述代码中：

　　(1)s1 == s2为true，是因为s1和s2都是字符串字面值"nihao"的引用，指向同一块地址，所以相等。

　　(2)s1 == s3为false，是因为通过new产生的对象在堆中，s3是堆中变量的引用，而是s1是指向字符串字面值"nihao"的引用，地址不同所以不相等。

2.equals()

 　　equals是根类Obeject中的方法。源代码如下：

public boolean equals(Object obj) {
    return (this == obj);
}
　　可见默认的equals方法，直接调用==，比较对象地址。

 　 不同的子类，可以重写此方法，进行两个对象的equals的判断。

　　String类源码中重写的equals方法如下，

1     public boolean equals(Object anObject) {
 2         if (this == anObject) {
 3             return true;
 4         }
 5         if (anObject instanceof String) {
 6             String anotherString = (String) anObject;
 7             int n = value.length;
 8             if (n == anotherString.value.length) {
 9                 char v1[] = value;
10                 char v2[] = anotherString.value;
11                 int i = 0;
12                 while (n-- != 0) {
13                     if (v1[i] != v2[i])
14                             return false;
15                     i++;
16                 }
17                 return true;
18             }
19         }
20         return false;
21     }
　　从上面的代码中可以看到，

　　(1)String类中的equals首先比较地址，如果是同一个对象的引用，可知对象相等，返回true。

　　(2)若果不是同一个对象，equals方法挨个比较两个字符串对象内的字符，只有完全相等才返回true，否则返回false。

3.hashcode()

　　hashCode是根类Obeject中的方法。

　　默认情况下，Object中的hashCode() 返回对象的32位jvm内存地址。也就是说如果对象不重写该方法，则返回相应对象的32为JVM内存地址。

　　String类源码中重写的hashCode方法如下，

1 public int hashCode() {
 2     int h = hash;    //Default to 0 ### String类中的私有变量，
 3     if (h == 0 && value.length > 0) {    //private final char value[]; ### Sting类中保存的字符串内容的的数组
 4         char val[] = value;
 5 
 6         for (int i = 0; i < value.length; i++) {
 7             h = 31 * h + val[i];
 8         }
 9         hash = h;
10     }
11     return h;
12 }
 

　　String源码中使用private final char value[];保存字符串内容，因此String是不可变的。

　　看下面的例子，没有重写hashCode方法的类，直接返回32位对象在JVM中的地址；Long类重写了hashCode方法，返回计算出的hashCode数值：

1 public class ComHashcode{
 2     public static void main(String[] args) throws Exception {
 3         ComHashcode a = new ComHashcode();
 4         ComHashcode b = new ComHashcode();
 5         System.out.println(a.hashCode());    //870919696
 6         System.out.println(b.hashCode());    //298792720
 7         
 8         Long num1 = new Long(8);
 9         Long num2 = new Long(8);
10         System.out.println(num1.hashCode());    //8
11         System.out.println(num2.hashCode());    //8
12     }
13 }
总结：

(1)绑定。当equals方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明相等对象必须具有相等的哈希码。

(2)绑定原因。Hashtable实现一个哈希表，为了成功地在哈希表中存储和检索对象，用作键的对象必须实现 hashCode 方法和 equals 方法。同(1)，必须保证equals相等的对象，hashCode 也相等。因为哈希表通过hashCode检索对象。

(3)默认。

　　==默认比较对象在JVM中的地址。

　　hashCode 默认返回对象在JVM中的存储地址。

　　equal比较对象，默认也是比较对象在JVM中的地址，同==