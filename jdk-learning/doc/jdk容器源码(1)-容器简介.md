Collection
├List
│├LinkedList
│├ArrayList
│└Vector
│　└Stack
└Set
Map
├Hashtable
├HashMap
└WeakHashMap



Collection接口
　　Collection是最基本的集合接口，一个Collection代表一组Object，即Collection的元素（Elements）。Java SDK不提供直接继承自Collection的类，java SDK提供的类都是继承自Collection的“子接口”如List和Set。
　　所有实现Collection接口的类都必须提供两个标准的构造函数：无参数的构造函数用于创建一个空的Collection，有一个 Collection参数的构造函数用于创建一个新的Collection，这个新的Collection与传入的Collection有相同的元素。后一个构造函数允许用户复制一个Collection。
　　如何遍历Collection中的每一个元素？不论Collection的实际类型如何，它都支持一个iterator()的方法，该方法返回一个迭代子，使用该迭代子即可逐一访问Collection中每一个元素。典型的用法如下：
　　　　Iterator it = collection.iterator(); // 获得一个迭代子
　　　　while(it.hasNext()) {
　　　　　　Object obj = it.next(); // 得到下一个元素
　　　　}
　　由Collection接口派生的两个接口是List和Set。

让我们转到对框架实现的研究，具体的集合类遵循命名约定，并将基本数据结构和框架接口相结合。除了四个历史集合类外，Java 2 框架还引入了六个集合实现，如下表所示。关于历史集合类如何转换、比如说，如何修改Hashtable 并结合到框架中，请参阅历史集合类 。

接口	实现	历史集合类
Set	HashSet	
TreeSet	
List	ArrayList	Vector
LinkedList	Stack
Map	HashMap	Hashtable
TreeMap	Properties
这里没有 Collection 接口的实现。历史集合类，之所以这样命名是因为从 Java 类库 1.0 发行版就开始沿用至今了。

如果从历史集合类转换到新的框架类，主要差异之一在于所有的操作都和新类不同步。您可以往新类中添加同步的实现，但您不能把它从旧的类中除去。

Collection collection = new ArrayList();（这样写的好处在于，以后如果要理性不同的集合，可以省略很多麻烦。因为都是用Collection接口里的方法，）

 boolean	add(E o)
          确保此 collection 包含指定的元素（可选操作）。
 boolean	addAll(Collection<? extends E> c)
          将指定 collection 中的所有元素都添加到此 collection 中（可选操作）。
 void	clear()
          移除此 collection 中的所有元素（可选操作）。
 boolean	contains(Object o)
          如果此 collection 包含指定的元素，则返回 true。
 boolean	containsAll(Collection<?> c)
          如果此 collection 包含指定 collection 中的所有元素，则返回true。
 boolean	equals(Object o)
          比较此 collection 与指定对象是否相等。
 int	hashCode()
          返回此 collection 的哈希码值。
 boolean	isEmpty()
          如果此 collection 不包含元素，则返回 true。
 Iterator<E>	iterator()
          返回在此 collection 的元素上进行迭代的迭代器。
 boolean	remove(Object o)
          从此 collection 中移除指定元素的单个实例，如果存在的话（可选操作）。
 boolean	removeAll(Collection<?> c)
          移除此 collection 中那些也包含在指定 collection 中的所有元素（可选操作）。
 boolean	retainAll(Collection<?> c)
          仅保留此 collection 中那些也包含在指定 collection 的元素（可选操作）。
 int	size()
          返回此 collection 中的元素数。
 Object[]	toArray()
          返回包含此 collection 中所有元素的数组。
<T> T[]	toArray(T[] a)
          返回包含此 collection 中所有元素的数组；返回数组的运行时类型与指定数组的运行时类型相同。
*All方法参数的类型都为Collection ，大多数方法都是返回boolean类型值，Collection 接口用于表示任何对象或元素组。想要尽可能以常规方式处理一组元素时，就使用这一接口。（如，可以直接add（100），可以是普通数据类型）。

容器类对象在调用remove,contains等方法时需要比较对象是否相等地，这会涉及到对象类型的equals方法和hashcode方法。即，相等的对象应该有相等的hashcode.当然，如果是自定义的类型，需要重写这两个方法。

iterator接口

 boolean	hasNext()
          如果仍有元素可以迭代，则返回 true。
 E	next()
          返回迭代的下一个元素。
 void	remove()
          从迭代器指向的集合中移除迭代器返回的最后一个元素（可选操作）。
Set
Set接口同样是Collection接口的一个子接口，它表示数学意义上的集合概念。Set中不包含重复的元素，即Set中不存两个这样的元素e1和e2，使得e1.equals(e2)为true。由于Set接口提供的数据结构是数学意义上集合概念的抽象，因此它需要支持对象的添加、删除，而不需提供随机访问。故Set接口与Collection的接口相同，在此对里面的方法不作介绍。
 boolean	add(E o)
          如果 set 中尚未存在指定的元素，则添加此元素（可选操作）。
 boolean	addAll(Collection<? extends E> c)
          如果 set 中没有指定 collection 中的所有元素，则将其添加到此 set 中（可选操作）。
 void	clear()
          移除 set 中的所有元素（可选操作）。
 boolean	contains(Object o)
          如果 set 包含指定的元素，则返回 true。
 boolean	containsAll(Collection<?> c)
          如果此 set 包含指定 collection 的所有元素，则返回 true。
 boolean	equals(Object o)
          比较指定对象与此 set 的相等性。
 int	hashCode()
          返回 set 的哈希码值。
 boolean	isEmpty()
          如果 set 不包含元素，则返回 true。
 Iterator<E>	iterator()
          返回在此 set 中的元素上进行迭代的迭代器。
 boolean	remove(Object o)
          如果 set 中存在指定的元素，则将其移除（可选操作）。
 boolean	removeAll(Collection<?> c)
          移除 set 中那些包含在指定 collection 中的元素（可选操作）。
 boolean	retainAll(Collection<?> c)
          仅保留 set 中那些包含在指定 collection 中的元素（可选操作）。
 int	size()
          返回 set 中的元素数（其容量）。
 Object[]	toArray()
          返回一个包含 set 中所有元素的数组。
<T> T[]	toArray(T[] a)
          返回一个包含 set 中所有元素的数组；返回数组的运行时类型是指定数组的类型。
按照定义，Set 接口继承 Collection 接口，而且它不允许集合中存在重复项。所有原始方法都是现成的，没有引入新方法。具体的Set 实现类依赖添加的对象的 equals()方法来检查等同性。

HashSet 类和 TreeSet 类

“集合框架”支持 Set 接口两种普通的实现：HashSet 和TreeSet。在更多情况下，您会使用 HashSet 存储重复自由的集合。考虑到效率，添加到 HashSet 的对象需要采用恰当分配散列码的方式来实现hashCode() 方法。虽然大多数系统类覆盖了 Object 中缺省的hashCode()实现，但创建您自己的要添加到 HashSet 的类时，别忘了覆盖 hashCode()。当您要从集合中以有序的方式抽取元素时，TreeSet 实现会有用处。为了能顺利进行，添加到TreeSet 的元素必须是可排序的。 “集合框架”添加对 Comparable 元素的支持，在排序的“可比较的接口”部分中会详细介绍。我们暂且假定一棵树知道如何保持java.lang 包装程序器类元素的有序状态。一般说来，先把元素添加到 HashSet，再把集合转换为TreeSet 来进行有序遍历会更快。

为优化 HashSet 空间的使用，您可以调优初始容量和负载因子。TreeSet 不包含调优选项，因为树总是平衡的，保证了插入、删除、查询的性能为log(n)。

HashSet 和 TreeSet 都实现 Cloneable 接口。

集的使用示例

为演示具体 Set 类的使用，下面的程序创建了一个 HashSet，并往里添加了一组名字，其中有个名字添加了两次。接着，程序把集中名字的列表打印出来，演示了重复的名字没有出现。接着，程序把集作为TreeSet 来处理，并显示有序的列表。

import java.util.*;

public class SetExample {
  public static void main(String args[]) {
    Set set = new HashSet();
    set.add("Bernadine");
    set.add("Elizabeth");
    set.add("Gene");
    set.add("Elizabeth");
    set.add("Clara");
    System.out.println(set);
    Set sortedSet = new TreeSet(set);
    System.out.println(sortedSet);
  }
}
运行程序产生了以下输出。请注意重复的条目只出现了一次，列表的第二次输出已按字母顺序排序。

[Gene, Clara, Bernadine, Elizabeth]
[Bernadine, Clara, Elizabeth, Gene]
List 接口

List 接口继承了 Collection 接口以定义一个允许重复项的有序集合。该接口不但能够对列表的一部分进行处理，还添加了面向位置的操作。

有序的 collection（也称为序列）。此接口的用户可以对列表中每个元素的插入位置进行精确地控制。用户可以根据元素的整数索引（在列表中的位置）访问元素，并搜索列表中的元素。 

与 set 不同，列表通常允许重复的元素。更正式地说，列表通常允许满足 e1.equals(e2) 的元素对 e1 和 e2，并且如果列表本身允许 null 元素的话，通常它们允许多个 null 元素。难免有人希望通过在用户尝试插入重复元素时抛出运行时异常的方法来禁止重复的列表，但我们希望这种用法越少越好。 

List 接口在 iterator、add、remove、equals 和 hashCode 方法的协定上加了一些其他约定，超过了 Collection 接口中指定的约定。为方便起见，这里也包括了其他继承方法的声明。 

List 接口提供了 4 种对列表元素进行定位（索引）访问方法。列表（像 Java 数组一样）是基于 0 的。注意，这些操作可能在和某些实现（例如 LinkedList 类）的索引值成比例的时间内执行。因此，如果调用方不知道实现，那么在列表元素上迭代通常优于用索引遍历列表。 

List 接口提供了特殊的迭代器，称为 ListIterator，除了允许 Iterator 接口提供的正常操作外，该迭代器还允许元素插入和替换，以及双向访问。还提供了一个方法来获取从列表中指定位置开始的列表迭代器。 

List 接口提供了两种搜索指定对象的方法。从性能的观点来看，应该小心使用这些方法。在很多实现中，它们将执行高开销的线性搜索。 

List 接口提供了两种在列表的任意位置高效插入和移除多个元素的方法。 

注意：尽管列表允许把自身作为元素包含在内，但建议要特别小心：在这样的列表上，equals 和 hashCode 方法不再是定义良好的。 

某些列表实现对列表可能包含的元素有限制。例如，某些实现禁止 null 元素，而某些实现则对元素的类型有限制。试图添加不合格的元素会抛出未经检查的异常，通常是 NullPointerException 或 ClassCastException。试图查询不合格的元素是否存在可能会抛出异常，也可能简单地返回 false；某些实现会采用前一种行为，而某些则采用后者。概括地说，试图对不合格元素执行操作时，如果完成该操作后不会导致在列表中插入不合格的元素，则该操作可能抛出一个异常，也可能成功，这取决于实现的选择。此接口的规范中将这样的异常标记为“可选”。


面向位置的操作包括插入某个元素或 Collection 的功能，还包括获取、除去或更改元素的功能。在 List 中搜索元素可以从列表的头部或尾部开始，如果找到元素，还将报告元素所在的位置。

void add(int index, Object element) 
boolean addAll(int index, Collection collection) 
Object get(int index) 
int indexOf(Object element) 
int lastIndexOf(Object element) 
Object remove(int index) 
Object set(int index, Object element) 
List 接口不但以位置友好的方式遍历整个列表，还能处理集合的子集：

ListIterator listIterator() 
ListIterator listIterator(int startIndex) 
List subList(int fromIndex, int toIndex) 
处理 subList() 时，位于 fromIndex 的元素在子列表中，而位于 toIndex 的元素则不是，提醒这一点很重要。以下 for-loop 测试案例大致反映了这一点：

for (int i=fromIndex; i<toIndex; i++) {
  // process element at position i
}
此外，我们还应该提醒的是 ― 对子列表的更改（如 add()、remove() 和 set() 调用）对底层 List 也有影响。

 boolean	add(E o) 
          向列表的尾部追加指定的元素（可选操作）。
 void	add(int index, E element) 
          在列表的指定位置插入指定元素（可选操作）。
 boolean	addAll(Collection<?
 extends E> c) 
          追加指定
 collection 中的所有元素到此列表的结尾，顺序是指定 collection 的迭代器返回这些元素的顺序（可选操作）。
 boolean	addAll(int index, Collection<?
 extends E> c) 
          将指定
 collection 中的所有元素都插入到列表中的指定位置（可选操作）。
 void	clear() 
          从列表中移除所有元素（可选操作）。
 boolean	contains(Object o) 
          如果列表包含指定的元素，则返回 true。
 boolean	containsAll(Collection<?> c) 
          如果列表包含指定
 collection 的所有元素，则返回 true。
 boolean	equals(Object o) 
          比较指定的对象与列表是否相等。
 E	get(int index) 
          返回列表中指定位置的元素。
 int	hashCode() 
          返回列表的哈希码值。
 int	indexOf(Object o) 
          返回列表中首次出现指定元素的索引，如果列表不包含此元素，则返回
 -1。
 boolean	isEmpty() 
          如果列表不包含元素，则返回 true。
 Iterator<E>	iterator() 
          返回以正确顺序在列表的元素上进行迭代的迭代器。
 int	lastIndexOf(Object o) 
          返回列表中最后出现指定元素的索引，如果列表不包含此元素，则返回
 -1。
 ListIterator<E>	listIterator() 
          返回列表中元素的列表迭代器（以正确的顺序）。
 ListIterator<E>	listIterator(int index) 
          返回列表中元素的列表迭代器（以正确的顺序），从列表的指定位置开始。
 E	remove(int index) 
          移除列表中指定位置的元素（可选操作）。
 boolean	remove(Object o) 
          移除列表中出现的首个指定元素（可选操作）。
 boolean	removeAll(Collection<?> c) 
          从列表中移除指定
 collection 中包含的所有元素（可选操作）。
 boolean	retainAll(Collection<?> c) 
          仅在列表中保留指定
 collection 中所包含的元素（可选操作）。
 E	set(int index, E element) 
          用指定元素替换列表中指定位置的元素（可选操作）。
 int	size() 
          返回列表中的元素数。
 List<E>	subList(int fromIndex,
 int toIndex) 
          返回列表中指定的 fromIndex（包括 ）和 toIndex（不包括）之间的部分视图。
 Object[]	toArray() 
          返回以正确顺序包含列表中的所有元素的数组。
<T> T[]
toArray(T[] a) 
          返回以正确顺序包含列表中所有元素的数组；返回数组的运行时类型是指定数组的运行时类型。


其中set方法返回的是被替换的内容。

 

Linked 改快读慢

Array 读快改慢

Hash 两都之间

Collection是集合接口
    |————Set子接口:无序，不允许重复。
    |————List子接口:有序，可以有重复元素。

    区别：Collections是集合类

    Set和List对比：
    Set：检索元素效率低下，删除和插入效率高，插入和删除不会引起元素位置改变。
    List：和数组类似，List可以动态增长，查找元素效率高，插入删除元素效率低，因为会引起其他元素位置改变。

    Set和List具体子类：
    Set
     |————HashSet：以哈希表的形式存放元素，插入删除速度很快。

    List
     |————ArrayList：动态数组
     |————LinkedList：链表、队列、堆栈。

    Array和java.util.Vector
    Vector是一种老的动态数组，是线程同步的，效率很低，一般不赞成使用。

 

Map 接口

Map 接口不是 Collection 接口的继承。而是从自己的用于维护键-值关联的接口层次结构入手。按定义，该接口描述了从不重复的键到值的映射。

我们可以把这个接口方法分成三组操作：改变、查询和提供可选视图。

改变操作允许您从映射中添加和除去键-值对。键和值都可以为 null。但是，您不能把Map 作为一个键或值添加给自身。

Object put(Object key, Object value)返回值是被替换的值。
Object remove(Object key)
void putAll(Map mapping)
void clear()
查询操作允许您检查映射内容：

Object get(Object key)
boolean containsKey(Object key)
boolean containsValue(Object value)
int size()
boolean isEmpty()
最后一组方法允许您把键或值的组作为集合来处理。

public Set keySet()
public Collection values()
public Set entrySet()
因为映射中键的集合必须是唯一的，您用 Set 支持。因为映射中值的集合可能不唯一，您用Collection 支持。最后一个方法返回一个实现 Map.Entry 接口的元素 Set。

Map.Entry 接口

Map 的 entrySet() 方法返回一个实现Map.Entry 接口的对象集合。集合中每个对象都是底层 Map 中一个特定的键-值对。

通过这个集合迭代，您可以获得每一条目的键或值并对值进行更改。但是，如果底层 Map 在Map.Entry 接口的setValue() 方法外部被修改，此条目集就会变得无效，并导致迭代器行为未定义。

HashMap 类和 TreeMap 类

“集合框架”提供两种常规的 Map 实现：HashMap 和TreeMap。和所有的具体实现一样，使用哪种实现取决于您的特定需要。在Map 中插入、删除和定位元素，HashMap 是最好的选择。但如果您要按顺序遍历键，那么TreeMap 会更好。根据集合大小，先把元素添加到 HashMap，再把这种映射转换成一个用于有序键遍历的 TreeMap 可能更快。使用HashMap 要求添加的键类明确定义了 hashCode() 实现。有了TreeMap 实现，添加到映射的元素一定是可排序的。我们将在排序中详细介绍。

为了优化 HashMap 空间的使用，您可以调优初始容量和负载因子。这个TreeMap 没有调优选项，因为该树总处于平衡状态。

HashMap 和 TreeMap 都实现Cloneable 接口。

Hashtable 类和 Properties 类是Map 接口的历史实现。我们将在Dictionary 类、Hashtable 类和 Properties 类中讨论。

映射的使用示例

以下程序演示了具体 Map 类的使用。该程序对自命令行传递的词进行频率计数。HashMap 起初用于数据存储。后来，映射被转换为TreeMap 以显示有序的键列列表。

import java.util.*;

public class MapExample {
  public static void main(String args[]) {
    Map map = new HashMap();
    Integer ONE = new Integer(1);
    for (int i=0, n=args.length; i<n; i++) {
      String key = args[i];
      Integer frequency = (Integer)map.get(key);
      if (frequency == null) {
        frequency = ONE;
      } else {
        int value = frequency.intValue();
        frequency = new Integer(value + 1);
      }
      map.put(key, frequency);
    }
    System.out.println(map);
    Map sortedMap = new TreeMap(map);
    System.out.println(sortedMap);
  }
}
用 Bill of Rights 的第三篇文章的文本运行程序产生下列输出，请注意有序输出看起来多么有用！

无序输出：

{prescribed=1, a=1, time=2, any=1, no=1, shall=1, nor=1, peace=1, owner=1, soldier=1, to=1, the=2, law=1, but=1, manner=1, without=1, house=1, in=4, by=1, consent=1, war=1, quartered=1, be=2, of=3}

有序输出：

{a=1, any=1, be=2, but=1, by=1, consent=1, house=1, in=4, law=1, manner=1, no=1, nor=1, of=3, owner=1, peace=1, prescribed=1, quartered=1, shall=1, soldier=1, the=2, time=2, to=1, war=1, without=1}

 

 

　Java集合框架是最常被问到的Java面试问题，要理解Java技术强大特性就有必要掌握集合框架。这里有一些实用问题，常在核心Java面试中问到。

　　1、什么是Java集合API

　　Java集合框架API是用来表示和操作集合的统一框架，它包含接口、实现类、以及帮助程序员完成一些编程的算法。简言之，API在上层完成以下几件事：

　　● 编程更加省力，提高城程序速度和代码质量

　　● 非关联的API提高互操作性

　　● 节省学习使用新API成本

　　● 节省设计新API的时间

　　● 鼓励、促进软件重用

　　具体来说，有6个集合接口，最基本的是Collection接口，由三个接口Set、List、SortedSet继承，另外两个接口是Map、SortedMap，这两个接口不继承Collection，表示映射而不是真正的集合。



　　2、什么是Iterator

　　一些集合类提供了内容遍历的功能，通过java.util.Iterator接口。这些接口允许遍历对象的集合。依次操作每个元素对象。当使用 Iterators时，在获得Iterator的时候包含一个集合快照。通常在遍历一个Iterator的时候不建议修改集合本省。

　　3、Iterator与ListIterator有什么区别？

　　Iterator：只能正向遍历集合，适用于获取移除元素。ListIerator：继承Iterator，可以双向列表的遍历，同样支持元素的修改。

　　4、什么是HaspMap和Map？

　　Map是接口，Java 集合框架中一部分，用于存储键值对，HashMap是用哈希算法实现Map的类。

　　5、HashMap与HashTable有什么区别？对比Hashtable VS HashMap

　　两者都是用key-value方式获取数据。Hashtable是原始集合类之一（也称作遗留类）。HashMap作为新集合框架的一部分在Java2的1.2版本中加入。它们之间有一下区别：

　　● HashMap和Hashtable大致是等同的，除了非同步和空值（HashMap允许null值作为key和value，而Hashtable不可以）。

　　● HashMap没法保证映射的顺序一直不变，但是作为HashMap的子类LinkedHashMap，如果想要预知的顺序迭代（默认按照插入顺序），你可以很轻易的置换为HashMap，如果使用Hashtable就没那么容易了。

　　● HashMap不是同步的，而Hashtable是同步的。

　　● 迭代HashMap采用快速失败机制，而Hashtable不是，所以这是设计的考虑点。

　　6、在Hashtable上下文中同步是什么意思？

　　同步意味着在一个时间点只能有一个线程可以修改哈希表，任何线程在执行hashtable的更新操作前需要获取对象锁，其他线程等待锁的释放。

　　7、什么叫做快速失败特性

　　从高级别层次来说快速失败是一个系统或软件对于其故障做出的响应。一个快速失败系统设计用来即时报告可能会导致失败的任何故障情况，它通常用来停止正常的操作而不是尝试继续做可能有缺陷的工作。当有问题发生时，快速失败系统即时可见地发错错误告警。在Java中，快速失败与iterators有关。如果一个iterator在集合对象上创建了，其它线程欲“结构化”的修改该集合对象，并发修改异常 （ConcurrentModificationException） 抛出。

　　8、怎样使Hashmap同步？

　　HashMap可以通过Map m = Collections.synchronizedMap（hashMap）来达到同步的效果。

　　9、什么时候使用Hashtable，什么时候使用HashMap

　　基本的不同点是Hashtable同步HashMap不是的，所以无论什么时候有多个线程访问相同实例的可能时，就应该使用Hashtable，反之使用HashMap。非线程安全的数据结构能带来更好的性能。

　　如果在将来有一种可能—你需要按顺序获得键值对的方案时，HashMap是一个很好的选择，因为有HashMap的一个子类 LinkedHashMap。所以如果你想可预测的按顺序迭代（默认按插入的顺序），你可以很方便用LinkedHashMap替换HashMap。反观要是使用的Hashtable就没那么简单了。同时如果有多个线程访问HashMap，Collections.synchronizedMap（）可以代替，总的来说HashMap更灵活。

　　10、为什么Vector类认为是废弃的或者是非官方地不推荐使用？或者说为什么我们应该一直使用ArrayList而不是Vector

　　你应该使用ArrayList而不是Vector是因为默认情况下你是非同步访问的，Vector同步了每个方法，你几乎从不要那样做，通常有想要同步的是整个操作序列。同步单个的操作也不安全（如果你迭代一个Vector，你还是要加锁，以避免其它线程在同一时刻改变集合）.而且效率更慢。当然同样有锁的开销即使你不需要，这是个很糟糕的方法在默认情况下同步访问。你可以一直使用Collections.sychronizedList来装饰一个集合。

　　事实上Vector结合了“可变数组”的集合和同步每个操作的实现。这是另外一个设计上的缺陷。Vector还有些遗留的方法在枚举和元素获取的方法，这些方法不同于List接口，如果这些方法在代码中程序员更趋向于想用它。尽管枚举速度更快，但是他们不能检查如果集合在迭代的时候修改了，这样将导致问题。尽管以上诸多原因，Oracle也从没宣称过要废弃Vector。


集合包：Collection（存放单个对象），map（key-value）

collection：List（支持放入重复对象），set（不能有重复对象）

List接口常见的类：ArrayList，LinkedList，vector，stack

set接口常见类：treeset，hashset

ma接口常见类：treemap，hashmap



java.util.Collection [I]
+--java.util.List [I]
   +--java.util.ArrayList [C]
   +--java.util.LinkedList [C]
   +--java.util.Vector [C]
      +--java.util.Stack [C]
+--java.util.Set [I]
   +--java.util.HashSet [C]
   +--java.util.SortedSet [I]
      +--java.util.TreeSet [C]


java.util.Map [I]
+--java.util.SortedMap [I]
   +--java.util.TreeMap [C]
+--java.util.Hashtable [C]
+--java.util.HashMap [C]
+--java.util.LinkedHashMap [C]
+--java.util.WeakHashMap [C]


ArrayList

采用数组方式存储对象，无容量的限制

插入对象：已有元素添加1，如果超过数组容量

(01) ArrayList 实际上是通过一个数组去保存数据的。当我们构造ArrayList时；若使用默认构造函数，则ArrayList的默认容量大小是10。

(02) 当ArrayList容量不足以容纳全部元素时，ArrayList会重新设置容量：新的容量=“(原始容量x3)/2 + 1”。

删除元素不减小数组的容量（减小可以调用trimToSize）

非线程安全

http://git.oschina.net/memristor/javalab/blob/master/javalab/src/collection/list/ArrayListExample.java

LinkedeList

双向链表机制，集合每个元素都知道其前一个元素与后一个元素的位置。

插入元素，切换相应元素的前后指针引用，查找需要遍历链表，删除先遍历链表找到删除元素然后删除元素

非线程安全

http://git.oschina.net/memristor/javalab/blob/master/javalab/src/collection/list/LinkedListExample.java



Vector

基于数组实现，默认创建10个元素数组

add方法加了synchronize，线程安全，扩大数组的方法与ArrayList也不同。如果capcacityIncrement大于1，则扩充为现有数组size+capacityIncrement，如果capcacityIncrement小于0，扩充为现有大小的两倍，容量控制策略更加可控

indexof，remove，get都加了synchronized关键字

线程安全

Stack

实现基于Vector，后进先出，push，pop，peek



hashset

set中不允许元素重复，采用基于hashmap实现

hashset构造函数要先创建一个hashmap对象，add方法调用hashmap的put（object，object）完成操作，将需要增加的元素作为map的key，value则传入一个之前已经创建的对象

其他都是调用hashmap的接口

非线程安全

TreeSet

treeset与hashset的区别是treeset提供对排序的支持，基于TreeMap实现

非线程安全

hashMap

将loadFactor设为0.75， threshold设置为12，创建一个大小为16的Entry对象数组



table是一个Entry[]数组类型，而Entry实际上就是一个单向链表。哈希表的"key-value键值对"都是存储在Entry数组中的。 

size是HashMap的大小，它是HashMap保存的键值对的数量。 

threshold是HashMap的阈值，用于判断是否需要调整HashMap的容量。threshold的值="容量*加载因子"，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。

loadFactor就是加载因子。 

HashMap的构造函数

http://www.cnblogs.com/skywang12345/p/3310835.html

// 默认构造函数。
public HashMap() {
    // 设置“加载因子”
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
    threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    // 创建Entry数组，用来保存数据
    table = new Entry[DEFAULT_INITIAL_CAPACITY];
    init();
}

// 指定“容量大小”和“加载因子”的构造函数
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                           initialCapacity);
    // HashMap的最大容量只能是MAXIMUM_CAPACITY
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                                           loadFactor);

    // Find a power of 2 >= initialCapacity
    int capacity = 1;
    while (capacity < initialCapacity)
        capacity <<= 1;

    // 设置“加载因子”
    this.loadFactor = loadFactor;
    // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
    threshold = (int)(capacity * loadFactor);
    // 创建Entry数组，用来保存数据
    table = new Entry[capacity];
    init();
}

// 指定“容量大小”的构造函数
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

// 包含“子Map”的构造函数
public HashMap(Map<? extends K, ? extends V> m) {
    this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                  DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
    // 将m中的全部元素逐个添加到HashMap中
    putAllForCreate(m);
}
数组存储的是一个链表，当有冲突的时候采用链表的方式

非线程安全的

TreeMap

支持排序的map实现，基于红黑树实现，非线程安全



(01) TreeMap实现继承于AbstractMap，并且实现了NavigableMap接口。

(02) TreeMap的本质是R-B Tree(红黑树)，它包含几个重要的成员变量： root, size, comparator。

　　root 是红黑数的根节点。它是Entry类型，Entry是红黑数的节点，它包含了红黑数的6个基本组成成分：key(键)、value(值)、left(左孩子)、right(右孩子)、parent(父节点)、color(颜色)。Entry节点根据key进行排序，Entry节点包含的内容为value。 

　　红黑数排序时，根据Entry中的key进行排序；Entry中的key比较大小是根据比较器comparator来进行判断的。

　　size是红黑数中节点的个数。

WeakHashMap

http://www.cnblogs.com/skywang12345/p/3311092.html

 和HashMap一样，WeakHashMap 也是一个散列表，它存储的内容也是键值对(key-value)映射，而且键和值都可以是null。不过WeakHashMap的键是“弱键”。在 WeakHashMap 中，当某个键不再正常使用时，会被从WeakHashMap中被自动移除。更精确地说，对于一个给定的键，其映射的存在并不阻止垃圾回收器对该键的丢弃，这就使该键成为可终止的，被终止，然后被回收。某个键被终止时，它对应的键值对也就从映射中有效地移除了。

这个“弱键”的原理呢？大致上就是，通过WeakReference和ReferenceQueue实现的。 WeakHashMap的key是“弱键”，即是WeakReference类型的；ReferenceQueue是一个队列，它会保存被GC回收的“弱键”。实现步骤是：

    (01) 新建WeakHashMap，将“键值对”添加到WeakHashMap中。

           实际上，WeakHashMap是通过数组table保存Entry(键值对)；每一个Entry实际上是一个单向链表，即Entry是键值对链表。

   (02) 当某“弱键”不再被其它对象引用，并被GC回收时。在GC回收该“弱键”时，这个“弱键”也同时会被添加到ReferenceQueue(queue)队列中。

   (03) 当下一次我们需要操作WeakHashMap时，会先同步table和queue。table中保存了全部的键值对，而queue中保存被GC回收的键值对；同步它们，就是删除table中被GC回收的键值对。

   这就是“弱键”如何被自动从WeakHashMap中删除的步骤了。



(01) WeakHashMap继承于AbstractMap，并且实现了Map接口。

(02) WeakHashMap是哈希表，但是它的键是"弱键"。WeakHashMap中保护几个重要的成员变量：table, size, threshold, loadFactor, modCount, queue。

　　table是一个Entry[]数组类型，而Entry实际上就是一个单向链表。哈希表的"key-value键值对"都是存储在Entry数组中的。 

　　size是Hashtable的大小，它是Hashtable保存的键值对的数量。 

　　threshold是Hashtable的阈值，用于判断是否需要调整Hashtable的容量。threshold的值="容量*加载因子"。

　　loadFactor就是加载因子。 

　　modCount是用来实现fail-fast机制的

　　queue保存的是“已被GC清除”的“弱引用的键”。

    “弱键”是一个“弱引用(WeakReference)”，在Java中，WeakReference和ReferenceQueue 是联合使用的。在WeakHashMap中亦是如此：如果弱引用所引用的对象被垃圾回收，Java虚拟机就会把这个弱引用加入到与之关联的引用队列中。 接着，WeakHashMap会根据“引用队列”，来删除“WeakHashMap中已被GC回收的‘弱键’对应的键值对”。

http://git.oschina.net/memristor/javalab/blob/master/javalab/src/collection/Test/WeakHashMapTest.java

HashTable

和HashMap一样，Hashtable 也是一个散列表，它存储的内容是键值对(key-value)映射。

Hashtable 继承于Dictionary，实现了Map、Cloneable、java.io.Serializable接口。

Hashtable 的函数都是同步的，这意味着它是线程安全的。它的key、value都不可以为null。此外，Hashtable中的映射不是有序的。

hashmap与hashtable

执行效率与线程安全：A.HashMap是非线程安全的，是Hashtable的轻量级实现，效率较高；B.Hashtable是线程安全的，效率较低

put方法对key和value的要求不同：A.HashMap允许Entry的key或value为null  B.Hashtable不允许Entry的key或value为null，否则出现NullPointerException

有无contains方法    A.HashMap没有contains方法    B.Hashtable有contains方法



对集合操作的工具类

    Java提供了java.util.Collections，以及java.util.Arrays类简化对集合的操作

    java.util.Collections主要提供一些static方法用来操作或创建Collection，Map等集合。

    java.util.Arrays主要提供static方法对数组进行操作。。

fail-fast机制

fail-fast 机制是java集合(Collection)中的一种错误机制。当多个线程对同一个集合的内容进行操作时，就可能会产生fail-fast事件。

package collection.Test;

import java.util.*;  
import java.util.concurrent.*;  
 
/*  
 * @desc java集合中Fast-Fail的测试程序。  
 *  
 *   fast-fail事件产生的条件：当多个线程对Collection进行操作时，若其中某一个线程通过iterator去遍历集合时，该集合的内容被其他线程所改变；则会抛出ConcurrentModificationException异常。  
 *   fast-fail解决办法：通过util.concurrent集合包下的相应类去处理，则不会产生fast-fail事件。  
 *  
 *   本例中，分别测试ArrayList和CopyOnWriteArrayList这两种情况。ArrayList会产生fast-fail事件，而CopyOnWriteArrayList不会产生fast-fail事件。  
 *   (01) 使用ArrayList时，会产生fast-fail事件，抛出ConcurrentModificationException异常；定义如下：  
 *            private static List<String> list = new ArrayList<String>();  
 *   (02) 使用时CopyOnWriteArrayList，不会产生fast-fail事件；定义如下：  
 *            private static List<String> list = new CopyOnWriteArrayList<String>();  
 *  
 * @author skywang  
 */ 
public class FastFailTest {  
 
    private static List<String> list = new ArrayList<String>();  
    //private static List<String> list = new CopyOnWriteArrayList<String>();  
    public static void main(String[] args) {  
      
        // 同时启动两个线程对list进行操作！  
        new ThreadOne().start();  
        new ThreadTwo().start();  
    }  
 
    private static void printAll() {  
        System.out.println("");  
 
        String value = null;  
        Iterator iter = list.iterator();  
        while(iter.hasNext()) {  
            value = (String)iter.next();  
            System.out.print(value+", ");  
        }  
    }  
 
    /**  
     * 向list中依次添加0,1,2,3,4,5，每添加一个数之后，就通过printAll()遍历整个list  
     */ 
    private static class ThreadOne extends Thread {  
        public void run() {  
            int i = 0;  
            while (i<6) {  
                list.add(String.valueOf(i));  
                printAll();  
                i++;  
            }  
        }  
    }  
 
    /**  
     * 向list中依次添加10,11,12,13,14,15，每添加一个数之后，就通过printAll()遍历整个list  
     */ 
    private static class ThreadTwo extends Thread {  
        public void run() {  
            int i = 10;  
            while (i<16) {  
                list.add(String.valueOf(i));  
                printAll();  
                i++;  
            }  
        }  
    }  
 
}
0, 

0, 10, 

0, 10, 11, 

0, 10, 11, 12, 

0, 10, 11, 12, 13, 

0, 10, 11, 12, 13, 14, Exception in thread "Thread-0" 

0, 10, 11, 12, 13, 14, 15, java.util.ConcurrentModificationException

 at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:859)

 at java.util.ArrayList$Itr.next(ArrayList.java:831)

 at collection.Test.FastFailTest.printAll(FastFailTest.java:37)

 at collection.Test.FastFailTest.access$1(FastFailTest.java:31)

 at collection.Test.FastFailTest$ThreadOne.run(FastFailTest.java:50)

结果说明：

(01) FastFailTest中通过 new ThreadOne().start() 和 new ThreadTwo().start() 同时启动两个线程去操作list。

    ThreadOne线程：向list中依次添加0,1,2,3,4,5。每添加一个数之后，就通过printAll()遍历整个list。

    ThreadTwo线程：向list中依次添加10,11,12,13,14,15。每添加一个数之后，就通过printAll()遍历整个list。

(02) 当某一个线程遍历list的过程中，list的内容被另外一个线程所改变了；就会抛出ConcurrentModificationException异常，产生fail-fast事件。

fail-fast机制，是一种错误检测机制。它只能被用来检测错误，因为JDK并不保证fail-fast机制一定会发生。若在多线程环境下使用fail-fast机制的集合，建议使用“java.util.concurrent包下的类”去取代“java.util包下的类”。所以，本例中只需要将ArrayList替换成java.util.concurrent包下对应的类即可。

(01) 和ArrayList继承于AbstractList不同，CopyOnWriteArrayList没有继承于AbstractList，它仅仅只是实现了List接口。

(02) ArrayList的iterator()函数返回的Iterator是在AbstractList中实现的；而CopyOnWriteArrayList是自己实现Iterator。

(03) ArrayList的Iterator实现类中调用next()时，会“调用checkForComodification()比较‘expectedModCount’和‘modCount’的大小”；但是，CopyOnWriteArrayList的Iterator实现类中，没有所谓的checkForComodification()，更不会抛出ConcurrentModificationException异常！ 


Iterator和Enumeration

package java.util;public interface Enumeration<E> {    
    boolean hasMoreElements();
    E nextElement();
}
public interface Iterator<E> {    
    boolean hasNext();
    E next();    
    void remove();
}
(01) 函数接口不同

        Enumeration只有2个函数接口。通过Enumeration，我们只能读取集合的数据，而不能对数据进行修改。

        Iterator只有3个函数接口。Iterator除了能读取集合的数据之外，也能数据进行删除操作。



(02) Iterator支持fail-fast机制，而Enumeration不支持。

Enumeration 是JDK 1.0添加的接口。使用到它的函数包括Vector、Hashtable等类，这些类都是JDK 1.0中加入的，Enumeration存在的目的就是为它们提供遍历接口。Enumeration本身并没有支持同步，而在Vector、Hashtable实现Enumeration时，添加了同步。而Iterator 是JDK 1.2才添加的接口，它也是为了HashMap、ArrayList等集合提供遍历接口。Iterator是支持fail-fast机制的：当多个线程对同一个集合的内容进行操作时，就可能会产生fail-fast事件，因此Enumeration 比 Iterator 的遍历速度更快


Collection接口是集合类的根接口，派生出两个子接口List和Set

Set不能包含重复元素。

List是一个有序集合，可以包含重复元素，提供按照索引访问的方式。

Map是Java.util包中的另一个接口，他与Collection接口没有关系，但是都属于集合类的一部分，Map不能包含重复key，value可以重复

所有集合类，都实现了Iterator接口，用于遍历集合主要有如下三个方法：

1.hasNext(); 2.next();3.remove();

List、Set、Map是这个集合体系中最主要的三个接口。 
List和Set继承自Collection接口。 Map也属于集合系统，但和Collection接口不同。

Set不允许元素重复。HashSet和TreeSet是两个主要的实现类。
Set 只能通过游标来取值，并且值是不能重复的。

List有序且允许元素重复。ArrayList、LinkedList和Vector是三个主要的实现类。 
ArrayList 是线程不安全的， Vector 是线程安全的，这两个类底层都是由数组实现的。
LinkedList 是线程不安全的，底层是由链表实现的 

Map 是键值对集合。其中key列就是一个集合，key不能重复，但是value可以重复。 
HashMap、TreeMap和Hashtable是Map的三个主要的实现类。 
HashTable 是线程安全的，不能存储 null 值 HashMap 不是线程安全的，可以存储 null 值
ArrayList 相较于LinkedList，查询效率高，ArrayList插入数据后，所有的索引的向后推一位，所以当插入删除错做多的时候不适合，LinkedList查询效率低，但是插入删除效率较高。Vector相较于前两个线程安全

HashSet与TreeSet，都是用hascode对元素进行查找，一般使用HashSet，如要有序的用TreeSet，都是线程不安全，需要自己手动保证同步，LinkedHashSet,普通插入错做比HashSet慢，遍历快

HashMap与HashTable ，HashTable是线程安全的，LinkedHashMap插入跟取出的顺序

ConcurrentHashMap所使用的锁分段技术，首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。

Map的遍历

第一种：

Map map = new HashMap();
map.put("key1","lisi1");
map.put("key2","lisi2");
map.put("key3","lisi3");
map.put("key4","lisi4");  
//先获取map集合的所有键的set集合，keyset（）
Iterator it = map.keySet().iterator();
 //获取迭代器
while(it.hasNext()){
Object key = it.next();
System.out.println(map.get(key));
}
第二种：

Map map = new HashMap();
map.put("key1","lisi1");
map.put("key2","lisi2");
map.put("key3","lisi3");
map.put("key4","lisi4");
//将map集合中的映射关系取出，存入到set集合
Iterator it = map.entrySet().iterator();
while(it.hasNext()){
Entry e =(Entry) it.next();
System.out.println("键"+e.getKey () + "的值为" + e.getValue());
}
推荐第二种， HashMap.keySet() 等于是对map进行了两次遍历，先iterator，再把key放在一个Set集合中，通过get()方法获取value。

entrySet()只遍历了一次，把key跟value都取出来 Set<Map.Entry<K,V>> entrySet()



