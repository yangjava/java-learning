一、HashMap概述

　　HashMap基于哈希表的 Map 接口的实现。此实现提供所有可选的映射操作，并允许使用 null 值和 null 键。（除了不同步和允许使用 null 之外，HashMap 类与 Hashtable 大致相同。）此类不保证映射的顺序，特别是它不保证该顺序恒久不变。

　　值得注意的是HashMap不是线程安全的，如果想要线程安全的HashMap，可以通过Collections类的静态方法synchronizedMap获得线程安全的HashMap。

 Map map = Collections.synchronizedMap(new HashMap());

二、HashMap的数据结构

　　HashMap的底层主要是基于数组和链表来实现的，它之所以有相当快的查询速度主要是因为它是通过计算散列码来决定存储的位置。HashMap中主要是通过key的hashCode来计算hash值的，只要hashCode相同，计算出来的hash值就一样。如果存储的对象对多了，就有可能不同的对象所算出来的hash值是相同的，这就出现了所谓的hash冲突。学过数据结构的同学都知道，解决hash冲突的方法有很多，HashMap底层是通过链表来解决hash冲突的。



 

 图中，紫色部分即代表哈希表，也称为哈希数组，数组的每个元素都是一个单链表的头节点，链表是用来解决冲突的，如果不同的key映射到了数组的同一位置处，就将其放入单链表中。

我们看看HashMap中Entry类的代码：

        

 





 

 

HashMap其实就是一个Entry数组，Entry对象中包含了键和值，其中next也是一个Entry对象，它就是用来处理hash冲突的，形成一个链表。

 

三、HashMap源码分析

 

       1、关键属性

　　先看看HashMap类中的一些关键属性：

transient Entry[] table;//存储元素的实体数组
 transient int size;//存放元素的个数
 int threshold; //临界值   当实际大小超过临界值时，会进行扩容threshold = 加载因子*容量 
  final float loadFactor; //加载因子
 transient int modCount;//被修改的次数

 

其中loadFactor加载因子是表示Hsah表中元素的填满的程度.

若:加载因子越大,填满的元素越多,好处是,空间利用率高了,但:冲突的机会加大了.链表长度会越来越长,查找效率降低。

反之,加载因子越小,填满的元素越少,好处是:冲突的机会减小了,但:空间浪费多了.表中的数据将过于稀疏（很多空间还没用，就开始扩容了）

冲突的机会越大,则查找的成本越高.

因此,必须在 "冲突的机会"与"空间利用率"之间寻找一种平衡与折衷. 这种平衡与折衷本质上是数据结构中有名的"时-空"矛盾的平衡与折衷.

　　如果机器内存足够，并且想要提高查询速度的话可以将加载因子设置小一点；相反如果机器内存紧张，并且对查询速度没有什么要求的话可以将加载因子设置大一点。不过一般我们都不用去设置它，让它取默认值0.75就好了。

 

2、构造方法

下面看看HashMap的几个构造方法：

 

public HashMap(int initialCapacity, float loadFactor) {
          //确保数字合法
          if (initialCapacity < 0)
              throw new IllegalArgumentException("Illegal initial capacity: " +
                                                initialCapacity);
          if (initialCapacity > MAXIMUM_CAPACITY)
              initialCapacity = MAXIMUM_CAPACITY;
          if (loadFactor <= 0 || Float.isNaN(loadFactor))
              throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
         // Find a power of 2 >= initialCapacity
         int capacity = 1;   //初始容量
         while (capacity < initialCapacity)   //确保容量为2的n次幂，使capacity为大于initialCapacity的最小的2的n次幂
             capacity <<= 1; 
         this.loadFactor = loadFactor;
         threshold = (int)(capacity * loadFactor);
         table = new Entry[capacity];
        init();
    }
     public HashMap(int initialCapacity) {
         this(initialCapacity, DEFAULT_LOAD_FACTOR);
    } 
     public HashMap() {
         this.loadFactor = DEFAULT_LOAD_FACTOR;
         threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
         table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
     }

 

我们可以看到在构造HashMap的时候如果我们指定了加载因子和初始容量的话就调用第一个构造方法，否则的话就是用默认的。默认初始容量为16，默认加载因子为0.75。我们可以看到上面代码中13-15行，这段代码的作用是确保容量为2的n次幂，使capacity为大于initialCapacity的最小的2的n次幂，至于为什么要把容量设置为2的n次幂，我们等下再看。

 

重点分析下HashMap中用的最多的两个方法put和get

       3、存储数据

　　下面看看HashMap存储数据的过程是怎样的，首先看看HashMap的put方法：

public V put(K key, V value) {
     // 若“key为null”，则将该键值对添加到table[0]中。
         if (key == null)
            return putForNullKey(value);
     // 若“key不为null”，则计算该key的哈希值，然后将其添加到该哈希值对应的链表中。
         int hash = hash(key.hashCode());
     //搜索指定hash值在对应table中的索引
         int i = indexFor(hash, table.length);
     // 循环遍历Entry数组,若“该key”对应的键值对已经存在，则用新的value取代旧的value。然后退出！
         for (Entry<K,V> e = table[i]; e != null; e = e.next) {
             Object k;
              if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { //如果key相同则覆盖并返回旧值
                  V oldValue = e.value;
                 e.value = value;
                 e.recordAccess(this);
                 return oldValue;
              }
         }
     //修改次数+1
         modCount++;
     //将key-value添加到table[i]处
     addEntry(hash, key, value, i);
     return null;
}

 

上面程序中用到了一个重要的内部接口：Map.Entry，每个 Map.Entry 其实就是一个 key-value 对。从上面程序中可以看出：当系统决定存储 HashMap 中的 key-value 对时，完全没有考虑 Entry 中的 value，仅仅只是根据 key 来计算并决定每个 Entry 的存储位置。这也说明了前面的结论：我们完全可以把 Map 集合中的 value 当成 key 的附属，当系统决定了 key 的存储位置之后，value 随之保存在那里即可。

我们慢慢的来分析这个函数，第2和3行的作用就是处理key值为null的情况，我们看看putForNullKey(value)方法：

private V putForNullKey(V value) {
          for (Entry<K,V> e = table[0]; e != null; e = e.next) {
              if (e.key == null) {   //如果有key为null的对象存在，则覆盖掉
                  V oldValue = e.value;
                  e.value = value;
                  e.recordAccess(this);
                  return oldValue;
             }
         }
         modCount++;
         addEntry(0, null, value, 0); //如果键为null的话，则hash值为0
         return null;
     }

 

注意：如果key为null的话，hash值为0，对象存储在数组中索引为0的位置。即table[0]

我们再回去看看put方法中第4行，它是通过key的hashCode值计算hash码，下面是计算hash码的函数：

//计算hash值的方法 通过键的hashCode来计算
     static int hash(int h) {
         // This function ensures that hashCodes that differ only by
         // constant multiples at each bit position have a bounded
         // number of collisions (approximately 8 at default load factor).
         h ^= (h >>> 20) ^ (h >>> 12);
         return h ^ (h >>> 7) ^ (h >>> 4);
     }

 

得到hash码之后就会通过hash码去计算出应该存储在数组中的索引，计算索引的函数如下：

static int indexFor(int h, int length) { //根据hash值和数组长度算出索引值
         return h & (length-1);  //这里不能随便算取，用hash&(length-1)是有原因的，这样可以确保算出来的索引是在数组大小范围内，不会超出
     }

 

这个我们要重点说下，我们一般对哈希表的散列很自然地会想到用hash值对length取模（即除法散列法），Hashtable中也是这样实现的，这种方法基本能保证元素在哈希表中散列的比较均匀，但取模会用到除法运算，效率很低，HashMap中则通过h&(length-1)的方法来代替取模，同样实现了均匀的散列，但效率要高很多，这也是HashMap对Hashtable的一个改进。

 

    接下来，我们分析下为什么哈希表的容量一定要是2的整数次幂。首先，length为2的整数次幂的话，h&(length-1)就相当于对length取模，这样便保证了散列的均匀，同时也提升了效率；其次，length为2的整数次幂的话，为偶数，这样length-1为奇数，奇数的最后一位是1，这样便保证了h&(length-1)的最后一位可能为0，也可能为1（这取决于h的值），即与后的结果可能为偶数，也可能为奇数，这样便可以保证散列的均匀性，而如果length为奇数的话，很明显length-1为偶数，它的最后一位是0，这样h&(length-1)的最后一位肯定为0，即只能为偶数，这样任何hash值都只会被散列到数组的偶数下标位置上，这便浪费了近一半的空间，因此，length取2的整数次幂，是为了使不同hash值发生碰撞的概率较小，这样就能使元素在哈希表中均匀地散列。

 

　　这看上去很简单，其实比较有玄机的，我们举个例子来说明：

　　假设数组长度分别为15和16，优化后的hash码分别为8和9，那么&运算后的结果如下： 

h & (table.length-1)                     hash                             table.length-1
       8 & (15-1)：                                 0100                   &              1110                   =                0100
       9 & (15-1)：                                 0101                   &              1110                   =                0100
       -----------------------------------------------------------------------------------------------------------------------
       8 & (16-1)：                                 0100                   &              1111                   =                0100
       9 & (16-1)：                                 0101                   &              1111                   =                0101

 

从上面的例子中可以看出：当它们和15-1（1110）“与”的时候，产生了相同的结果，也就是说它们会定位到数组中的同一个位置上去，这就产生了碰撞，8和9会被放到数组中的同一个位置上形成链表，那么查询的时候就需要遍历这个链 表，得到8或者9，这样就降低了查询的效率。同时，我们也可以发现，当数组长度为15的时候，hash值会与15-1（1110）进行“与”，那么 最后一位永远是0，而0001，0011，0101，1001，1011，0111，1101这几个位置永远都不能存放元素了，空间浪费相当大，更糟的是这种情况中，数组可以使用的位置比数组长度小了很多，这意味着进一步增加了碰撞的几率，减慢了查询的效率！而当数组长度为16时，即为2的n次方时，2n-1得到的二进制数的每个位上的值都为1，这使得在低位上&时，得到的和原hash的低位相同，加之hash(int h)方法对key的hashCode的进一步优化，加入了高位计算，就使得只有相同的hash值的两个值才会被放到数组中的同一个位置上形成链表。

　　 所以说，当数组长度为2的n次幂的时候，不同的key算得得index相同的几率较小，那么数据在数组上分布就比较均匀，也就是说碰撞的几率小，相对的，查询的时候就不用遍历某个位置上的链表，这样查询效率也就较高了。

　　　

       根据上面 put 方法的源代码可以看出，当程序试图将一个key-value对放入HashMap中时，程序首先根据该 key 的 hashCode() 返回值决定该 Entry 的存储位置：如果两个 Entry 的 key 的 hashCode() 返回值相同，那它们的存储位置相同。如果这两个 Entry 的 key 通过 equals 比较返回 true，新添加 Entry 的 value 将覆盖集合中原有 Entry 的 value，但key不会覆盖。如果这两个 Entry 的 key 通过 equals 比较返回 false，新添加的 Entry 将与集合中原有 Entry 形成 Entry 链，而且新添加的 Entry 位于 Entry 链的头部——具体说明继续看 addEntry() 方法的说明。

void addEntry(int hash, K key, V value, int bucketIndex) {
         Entry<K,V> e = table[bucketIndex]; //如果要加入的位置有值，将该位置原先的值设置为新entry的next,也就是新entry链表的下一个节点
         table[bucketIndex] = new Entry<>(hash, key, value, e);
         if (size++ >= threshold) //如果大于临界值就扩容
             resize(2 * table.length); //以2的倍数扩容
     }

 

参数bucketIndex就是indexFor函数计算出来的索引值，第2行代码是取得数组中索引为bucketIndex的Entry对象，第3行就是用hash、key、value构建一个新的Entry对象放到索引为bucketIndex的位置，并且将该位置原先的对象设置为新对象的next构成链表。

　　第4行和第5行就是判断put后size是否达到了临界值threshold，如果达到了临界值就要进行扩容，HashMap扩容是扩为原来的两倍。

 

4、调整大小

resize()方法如下：

 重新调整HashMap的大小，newCapacity是调整后的单位

 

void resize(int newCapacity) {
          Entry[] oldTable = table;
          int oldCapacity = oldTable.length;
          if (oldCapacity == MAXIMUM_CAPACITY) {
              threshold = Integer.MAX_VALUE;
              return;
         }
          Entry[] newTable = new Entry[newCapacity];
         transfer(newTable);//用来将原先table的元素全部移到newTable里面
         table = newTable;  //再将newTable赋值给table
         threshold = (int)(newCapacity * loadFactor);//重新计算临界值
     }

 

新建了一个HashMap的底层数组，上面代码中第10行为调用transfer方法，将HashMap的全部元素添加到新的HashMap中,并重新计算元素在新的数组中的索引位置

  

当HashMap中的元素越来越多的时候，hash冲突的几率也就越来越高，因为数组的长度是固定的。所以为了提高查询的效率，就要对HashMap的数组进行扩容，数组扩容这个操作也会出现在ArrayList中，这是一个常用的操作，而在HashMap数组扩容之后，最消耗性能的点就出现了：原数组中的数据必须重新计算其在新数组中的位置，并放进去，这就是resize。

 

   那么HashMap什么时候进行扩容呢？当HashMap中的元素个数超过数组大小*loadFactor时，就会进行数组扩容，loadFactor的默认值为0.75，这是一个折中的取值。也就是说，默认情况下，数组大小为16，那么当HashMap中元素个数超过16*0.75=12的时候，就把数组的大小扩展为 2*16=32，即扩大一倍，然后重新计算每个元素在数组中的位置，扩容是需要进行数组复制的，复制数组是非常消耗性能的操作，所以如果我们已经预知HashMap中元素的个数，那么预设元素的个数能够有效的提高HashMap的性能。

 

 5、数据读取

public V get(Object key) {  
    if (key == null)  
        return getForNullKey();  
    int hash = hash(key.hashCode());  
    for (Entry<K,V> e = table[indexFor(hash, table.length)];  
        e != null;  
        e = e.next) {  
        Object k;  
        if (e.hash == hash && ((k = e.key) == key || key.equals(k)))  
            return e.value;  
    }  
    return null;  
} 

 

有了上面存储时的hash算法作为基础，理解起来这段代码就很容易了。从上面的源代码中可以看出：从HashMap中get元素时，首先计算key的hashCode，找到数组中对应位置的某一元素，然后通过key的equals方法在对应位置的链表中找到需要的元素。

 

6、HashMap的性能参数：

 

   HashMap 包含如下几个构造器：

   HashMap()：构建一个初始容量为 16，负载因子为 0.75 的 HashMap。

   HashMap(int initialCapacity)：构建一个初始容量为 initialCapacity，负载因子为 0.75 的 HashMap。

   HashMap(int initialCapacity, float loadFactor)：以指定初始容量、指定的负载因子创建一个 HashMap。

   HashMap的基础构造器HashMap(int initialCapacity, float loadFactor)带有两个参数，它们是初始容量initialCapacity和加载因子loadFactor。

   initialCapacity：HashMap的最大容量，即为底层数组的长度。

   loadFactor：负载因子loadFactor定义为：散列表的实际元素数目(n)/ 散列表的容量(m)。

   负载因子衡量的是一个散列表的空间的使用程度，负载因子越大表示散列表的装填程度越高，反之愈小。对于使用链表法的散列表来说，查找一个元素的平均时间是O(1+a)，因此如果负载因子越大，对空间的利用更充分，然而后果是查找效率的降低；如果负载因子太小，那么散列表的数据将过于稀疏，对空间造成严重浪费。

   HashMap的实现中，通过threshold字段来判断HashMap的最大容量：

 

 
threshold = (int)(capacity * loadFactor);  

 
   结合负载因子的定义公式可知，threshold就是在此loadFactor和capacity对应下允许的最大元素数目，超过这个数目就重新resize，以降低实际的负载因子。默认的的负载因子0.75是对空间和时间效率的一个平衡选择。当容量超出此最大容量时， resize后的HashMap容量是容量的两倍
   
   
   
   containsValue(Object value):

public boolean containsValue(Object value) {
    if (value == null)
        return containsNullValue();

    Entry[] tab = table;
    for (int i = 0; i < tab.length ; i++)
        for (Entry e = tab[i] ; e != null ; e = e.next)
            if (value.equals(e.value))
                return true;
    return false;
}
可以看出，这里对table做了一次线性遍历才能够获取出value，复杂度为O(n)。

再看一下Map遍历用到的方法entrySet()

public Set<Map.Entry<K,V>> entrySet() {
    return entrySet0();
}

private Set<Map.Entry<K,V>> entrySet0() {
    Set<Map.Entry<K,V>> es = entrySet;
    return es != null ? es : (entrySet = new EntrySet());
}
entrySet调用了entrySet0,entrySet0返回了EntrySet对象，有点重复的样子！

看来主要的内容在EntrySet中，EntrySet是一个内部类。

private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
    public Iterator<Map.Entry<K,V>> iterator() {
        return newEntryIterator();
    }
    public boolean contains(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<K,V> e = (Map.Entry<K,V>) o;
        Entry<K,V> candidate = getEntry(e.getKey());
        return candidate != null && candidate.equals(e);
    }
    public boolean remove(Object o) {
        return removeMapping(o) != null;
    }
    public int size() {
        return size;
    }
    public void clear() {
        HashMap.this.clear();
    }
}
并没有什么属性！其实它是一个代理类，并且因为它是HashMap的内部类，所以可以直接调用HashMap的方法、属性。这个set并没有add方法。EntrySet的迭代器，是通过newEntryIterator返回。

Iterator<Map.Entry<K,V>> newEntryIterator()   {
    return new EntryIterator();
}
继续往下看：

private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
    public Map.Entry<K,V> next() {
        return nextEntry();
    }
}
EntryIterator是继承了HashIterator类。

private abstract class HashIterator<E> implements Iterator<E>
HashIterator是HashMap的内部抽象类，实现了Iterator接口。

其构造函数

HashIterator() {
    expectedModCount = modCount;
    if (size > 0) { // advance to first entry
        Entry[] t = table;
        while (index < t.length && (next = t[index++]) == null)
            ;
    }
}
遍历table数组，找到第一个不为空的槽。

hasNext方法

public final boolean hasNext() {
    return next != null;
}
如果next不为空，则存在下一个元素。

public void remove() {
    if (current == null)
        throw new IllegalStateException();
    if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
    Object k = current.key;
    current = null;
    HashMap.this.removeEntryForKey(k);
    expectedModCount = modCount;
}
remove方法是调用了HashMap的removeEntryForKey方法。没看到next方法，这是因为HashMap想复用HashIteraotr这个类，我们看到HashMap有三个迭代器:

private final class ValueIterator extends HashIterator<V> {
    public V next() {
        return nextEntry().value;
    }
}

private final class KeyIterator extends HashIterator<K> {
    public K next() {
        return nextEntry().getKey();
    }
}

private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
    public Map.Entry<K,V> next() {
        return nextEntry();
    }
}
ValueIterator是给values()用的迭代器，KeyIterator是给KeySet用的迭代器，EntryIterator是提供给EntrySet使用的迭代器。

这里有可以看出一个非常常见的设计：接口实现规范，抽象类实现大部分工作，具体类实现差异化内容！

看下HashIterator的nextEntry方法

final Entry<K,V> nextEntry() {
    if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
    Entry<K,V> e = next;
    if (e == null)
        throw new NoSuchElementException();

    if ((next = e.next) == null) {
        Entry[] t = table;
        while (index < t.length && (next = t[index++]) == null)
            ;
    }
    current = e;
    return e;
}
对table一个槽一个槽的链表遍历。

在看下keySet()方法

public Set<K> keySet() {
    Set<K> ks = keySet;
    return (ks != null ? ks : (keySet = new KeySet()));
}

private final class KeySet extends AbstractSet<K> {
    public Iterator<K> iterator() {
        return newKeyIterator();
    }
    public int size() {
        return size;
    }
    public boolean contains(Object o) {
        return containsKey(o);
    }
    public boolean remove(Object o) {
        return HashMap.this.removeEntryForKey(o) != null;
    }
    public void clear() {
        HashMap.this.clear();
    }
}
额，和EntrySet基本一样。继续看Values()

public Collection<V> values() {
    Collection<V> vs = values;
    return (vs != null ? vs : (values = new Values()));
}

private final class Values extends AbstractCollection<V> {
    public Iterator<V> iterator() {
        return newValueIterator();
    }
    public int size() {
        return size;
    }
    public boolean contains(Object o) {
        return containsValue(o);
    }
    public void clear() {
        HashMap.this.clear();
    }
}
还是差不多。

从分析Iterator的实现中可以看到，iterator是要遍历整个table的，所以不要将capacity的值设置得太高，也不要把loadfactor的值设置得太低。看HashMap的这句注释：

Iteration over collection views requires time proportional to the "capacity" of the HashMap instance (the number of buckets) plus its size (the number of key-value mappings). Thus, it's very important not to set the initial capacity too high (or the load factor too low) if iteration performance is important.

HashMap的分析到这里也差不多了，对于HashMap,还是对其hash方法不太明白。




这一章，我们对HashMap进行学习。
我们先对HashMap有个整体认识，然后再学习它的源码，最后再通过实例来学会使用HashMap。内容包括：
第1部分 HashMap介绍
第2部分 HashMap数据结构
第3部分 HashMap源码解析(基于JDK1.6.0_45)
    第3.1部分 HashMap的“拉链法”相关内容
    第3.2部分 HashMap的构造函数
    第3.3部分 HashMap的主要对外接口
    第3.4部分 HashMap实现的Cloneable接口
    第3.5部分 HashMap实现的Serializable接口
第4部分 HashMap遍历方式
第5部分 HashMap示例

第1部分 HashMap介绍

HashMap简介

HashMap 是一个散列表，它存储的内容是键值对(key-value)映射。
HashMap 继承于AbstractMap，实现了Map、Cloneable、java.io.Serializable接口。
HashMap 的实现不是同步的，这意味着它不是线程安全的。它的key、value都可以为null。此外，HashMap中的映射不是有序的。

HashMap 的实例有两个参数影响其性能：“初始容量” 和 “加载因子”。容量 是哈希表中桶的数量，初始容量 只是哈希表在创建时的容量。加载因子 是哈希表在其容量自动增加之前可以达到多满的一种尺度。当哈希表中的条目数超出了加载因子与当前容量的乘积时，则要对该哈希表进行 rehash 操作（即重建内部数据结构），从而哈希表将具有大约两倍的桶数。
通常，默认加载因子是 0.75, 这是在时间和空间成本上寻求一种折衷。加载因子过高虽然减少了空间开销，但同时也增加了查询成本（在大多数 HashMap 类的操作中，包括 get 和 put 操作，都反映了这一点）。在设置初始容量时应该考虑到映射中所需的条目数及其加载因子，以便最大限度地减少 rehash 操作次数。如果初始容量大于最大条目数除以加载因子，则不会发生 rehash 操作。

 

HashMap的构造函数

HashMap共有4个构造函数,如下：

复制代码
// 默认构造函数。
HashMap() // 指定“容量大小”的构造函数
HashMap(int capacity) // 指定“容量大小”和“加载因子”的构造函数
HashMap(int capacity, float loadFactor) // 包含“子Map”的构造函数
HashMap(Map<? extends K, ? extends V> map)
复制代码
 

HashMap的API

复制代码
void clear() Object clone() boolean containsKey(Object key) boolean containsValue(Object value) Set<Entry<K, V>> entrySet() V get(Object key) boolean isEmpty() Set<K> keySet() V put(K key, V value) void                 putAll(Map<? extends K, ? extends V> map) V remove(Object key) int size() Collection<V>        values()
复制代码
 

第2部分 HashMap数据结构

HashMap的继承关系

复制代码
java.lang.Object ↳ java.util.AbstractMap<K, V> ↳ java.util.HashMap<K, V>

public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable { }
复制代码
 

HashMap与Map关系如下图：



从图中可以看出： 
(01) HashMap继承于AbstractMap类，实现了Map接口。Map是"key-value键值对"接口，AbstractMap实现了"键值对"的通用函数接口。 
(02) HashMap是通过"拉链法"实现的哈希表。它包括几个重要的成员变量：table, size, threshold, loadFactor, modCount。
　　table是一个Entry[]数组类型，而Entry实际上就是一个单向链表。哈希表的"key-value键值对"都是存储在Entry数组中的。 
　　size是HashMap的大小，它是HashMap保存的键值对的数量。 
　　threshold是HashMap的阈值，用于判断是否需要调整HashMap的容量。threshold的值="容量*加载因子"，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
　　loadFactor就是加载因子。 
　　modCount是用来实现fail-fast机制的。

 

第3部分 HashMap源码解析(基于JDK1.6.0_45)

为了更了解HashMap的原理，下面对HashMap源码代码作出分析。
在阅读源码时，建议参考后面的说明来建立对HashMap的整体认识，这样更容易理解HashMap。


复制代码
 1 package java.util;  2 import java.io.*;  3 
 4 public class HashMap<K,V>
 5     extends AbstractMap<K,V>
 6     implements Map<K,V>, Cloneable, Serializable  7 {  8 
 9     // 默认的初始容量是16，必须是2的幂。
 10     static final int DEFAULT_INITIAL_CAPACITY = 16;  11 
 12     // 最大容量（必须是2的幂且小于2的30次方，传入容量过大将被这个值替换）
 13     static final int MAXIMUM_CAPACITY = 1 << 30;  14 
 15     // 默认加载因子
 16     static final float DEFAULT_LOAD_FACTOR = 0.75f;  17 
 18     // 存储数据的Entry数组，长度是2的幂。  19     // HashMap是采用拉链法实现的，每一个Entry本质上是一个单向链表
 20     transient Entry[] table;  21 
 22     // HashMap的大小，它是HashMap保存的键值对的数量
 23     transient int size;  24 
 25     // HashMap的阈值，用于判断是否需要调整HashMap的容量（threshold = 容量*加载因子）
 26     int threshold;  27 
 28     // 加载因子实际大小
 29     final float loadFactor;  30 
 31     // HashMap被改变的次数
 32     transient volatile int modCount;  33 
 34     // 指定“容量大小”和“加载因子”的构造函数
 35     public HashMap(int initialCapacity, float loadFactor) {  36         if (initialCapacity < 0)  37             throw new IllegalArgumentException("Illegal initial capacity: " +
 38  initialCapacity);  39         // HashMap的最大容量只能是MAXIMUM_CAPACITY
 40         if (initialCapacity > MAXIMUM_CAPACITY)  41             initialCapacity = MAXIMUM_CAPACITY;  42         if (loadFactor <= 0 || Float.isNaN(loadFactor))  43             throw new IllegalArgumentException("Illegal load factor: " +
 44  loadFactor);  45 
 46         // 找出“大于initialCapacity”的最小的2的幂
 47         int capacity = 1;  48         while (capacity < initialCapacity)  49             capacity <<= 1;  50 
 51         // 设置“加载因子”
 52         this.loadFactor = loadFactor;  53         // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
 54         threshold = (int)(capacity * loadFactor);  55         // 创建Entry数组，用来保存数据
 56         table = new Entry[capacity];  57  init();  58  }  59 
 60 
 61     // 指定“容量大小”的构造函数
 62     public HashMap(int initialCapacity) {  63         this(initialCapacity, DEFAULT_LOAD_FACTOR);  64  }  65 
 66     // 默认构造函数。
 67     public HashMap() {  68         // 设置“加载因子”
 69         this.loadFactor = DEFAULT_LOAD_FACTOR;  70         // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
 71         threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);  72         // 创建Entry数组，用来保存数据
 73         table = new Entry[DEFAULT_INITIAL_CAPACITY];  74  init();  75  }  76 
 77     // 包含“子Map”的构造函数
 78     public HashMap(Map<? extends K, ? extends V> m) {  79         this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,  80  DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);  81         // 将m中的全部元素逐个添加到HashMap中
 82  putAllForCreate(m);  83  }  84 
 85     static int hash(int h) {  86         h ^= (h >>> 20) ^ (h >>> 12);  87         return h ^ (h >>> 7) ^ (h >>> 4);  88  }  89 
 90     // 返回索引值  91     // h & (length-1)保证返回值的小于length
 92     static int indexFor(int h, int length) {  93         return h & (length-1);  94  }  95 
 96     public int size() {  97         return size;  98  }  99 
100     public boolean isEmpty() { 101         return size == 0; 102  } 103 
104     // 获取key对应的value
105     public V get(Object key) { 106         if (key == null) 107             return getForNullKey(); 108         // 获取key的hash值
109         int hash = hash(key.hashCode()); 110         // 在“该hash值对应的链表”上查找“键值等于key”的元素
111         for (Entry<K,V> e = table[indexFor(hash, table.length)]; 112              e != null; 113              e = e.next) { 114  Object k; 115             if (e.hash == hash && ((k = e.key) == key || key.equals(k))) 116                 return e.value; 117  } 118         return null; 119  } 120 
121     // 获取“key为null”的元素的值 122     // HashMap将“key为null”的元素存储在table[0]位置！
123     private V getForNullKey() { 124         for (Entry<K,V> e = table[0]; e != null; e = e.next) { 125             if (e.key == null) 126                 return e.value; 127  } 128         return null; 129  } 130 
131     // HashMap是否包含key
132     public boolean containsKey(Object key) { 133         return getEntry(key) != null; 134  } 135 
136     // 返回“键为key”的键值对
137     final Entry<K,V> getEntry(Object key) { 138         // 获取哈希值 139         // HashMap将“key为null”的元素存储在table[0]位置，“key不为null”的则调用hash()计算哈希值
140         int hash = (key == null) ? 0 : hash(key.hashCode()); 141         // 在“该hash值对应的链表”上查找“键值等于key”的元素
142         for (Entry<K,V> e = table[indexFor(hash, table.length)]; 143              e != null; 144              e = e.next) { 145  Object k; 146             if (e.hash == hash &&
147                 ((k = e.key) == key || (key != null && key.equals(k)))) 148                 return e; 149  } 150         return null; 151  } 152 
153     // 将“key-value”添加到HashMap中
154     public V put(K key, V value) { 155         // 若“key为null”，则将该键值对添加到table[0]中。
156         if (key == null) 157             return putForNullKey(value); 158         // 若“key不为null”，则计算该key的哈希值，然后将其添加到该哈希值对应的链表中。
159         int hash = hash(key.hashCode()); 160         int i = indexFor(hash, table.length); 161         for (Entry<K,V> e = table[i]; e != null; e = e.next) { 162  Object k; 163             // 若“该key”对应的键值对已经存在，则用新的value取代旧的value。然后退出！
164             if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { 165                 V oldValue = e.value; 166                 e.value = value; 167                 e.recordAccess(this); 168                 return oldValue; 169  } 170  } 171 
172         // 若“该key”对应的键值对不存在，则将“key-value”添加到table中
173         modCount++; 174  addEntry(hash, key, value, i); 175         return null; 176  } 177 
178     // putForNullKey()的作用是将“key为null”键值对添加到table[0]位置
179     private V putForNullKey(V value) { 180         for (Entry<K,V> e = table[0]; e != null; e = e.next) { 181             if (e.key == null) { 182                 V oldValue = e.value; 183                 e.value = value; 184                 e.recordAccess(this); 185                 return oldValue; 186  } 187  } 188         // 这里的完全不会被执行到!
189         modCount++; 190         addEntry(0, null, value, 0); 191         return null; 192  } 193 
194     // 创建HashMap对应的“添加方法”， 195     // 它和put()不同。putForCreate()是内部方法，它被构造函数等调用，用来创建HashMap 196     // 而put()是对外提供的往HashMap中添加元素的方法。
197     private void putForCreate(K key, V value) { 198         int hash = (key == null) ? 0 : hash(key.hashCode()); 199         int i = indexFor(hash, table.length); 200 
201         // 若该HashMap表中存在“键值等于key”的元素，则替换该元素的value值
202         for (Entry<K,V> e = table[i]; e != null; e = e.next) { 203  Object k; 204             if (e.hash == hash &&
205                 ((k = e.key) == key || (key != null && key.equals(k)))) { 206                 e.value = value; 207                 return; 208  } 209  } 210 
211         // 若该HashMap表中不存在“键值等于key”的元素，则将该key-value添加到HashMap中
212  createEntry(hash, key, value, i); 213  } 214 
215     // 将“m”中的全部元素都添加到HashMap中。 216     // 该方法被内部的构造HashMap的方法所调用。
217     private void putAllForCreate(Map<? extends K, ? extends V> m) { 218         // 利用迭代器将元素逐个添加到HashMap中
219         for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) { 220             Map.Entry<? extends K, ? extends V> e = i.next(); 221  putForCreate(e.getKey(), e.getValue()); 222  } 223  } 224 
225     // 重新调整HashMap的大小，newCapacity是调整后的单位
226     void resize(int newCapacity) { 227         Entry[] oldTable = table; 228         int oldCapacity = oldTable.length; 229         if (oldCapacity == MAXIMUM_CAPACITY) { 230             threshold = Integer.MAX_VALUE; 231             return; 232  } 233 
234         // 新建一个HashMap，将“旧HashMap”的全部元素添加到“新HashMap”中， 235         // 然后，将“新HashMap”赋值给“旧HashMap”。
236         Entry[] newTable = new Entry[newCapacity]; 237  transfer(newTable); 238         table = newTable; 239         threshold = (int)(newCapacity * loadFactor); 240  } 241 
242     // 将HashMap中的全部元素都添加到newTable中
243     void transfer(Entry[] newTable) { 244         Entry[] src = table; 245         int newCapacity = newTable.length; 246         for (int j = 0; j < src.length; j++) { 247             Entry<K,V> e = src[j]; 248             if (e != null) { 249                 src[j] = null; 250                 do { 251                     Entry<K,V> next = e.next; 252                     int i = indexFor(e.hash, newCapacity); 253                     e.next = newTable[i]; 254                     newTable[i] = e; 255                     e = next; 256                 } while (e != null); 257  } 258  } 259  } 260 
261     // 将"m"的全部元素都添加到HashMap中
262     public void putAll(Map<? extends K, ? extends V> m) { 263         // 有效性判断
264         int numKeysToBeAdded = m.size(); 265         if (numKeysToBeAdded == 0) 266             return; 267 
268         // 计算容量是否足够， 269         // 若“当前实际容量 < 需要的容量”，则将容量x2。
270         if (numKeysToBeAdded > threshold) { 271             int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1); 272             if (targetCapacity > MAXIMUM_CAPACITY) 273                 targetCapacity = MAXIMUM_CAPACITY; 274             int newCapacity = table.length; 275             while (newCapacity < targetCapacity) 276                 newCapacity <<= 1; 277             if (newCapacity > table.length) 278  resize(newCapacity); 279  } 280 
281         // 通过迭代器，将“m”中的元素逐个添加到HashMap中。
282         for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) { 283             Map.Entry<? extends K, ? extends V> e = i.next(); 284  put(e.getKey(), e.getValue()); 285  } 286  } 287 
288     // 删除“键为key”元素
289     public V remove(Object key) { 290         Entry<K,V> e = removeEntryForKey(key); 291         return (e == null ? null : e.value); 292  } 293 
294     // 删除“键为key”的元素
295     final Entry<K,V> removeEntryForKey(Object key) { 296         // 获取哈希值。若key为null，则哈希值为0；否则调用hash()进行计算
297         int hash = (key == null) ? 0 : hash(key.hashCode()); 298         int i = indexFor(hash, table.length); 299         Entry<K,V> prev = table[i]; 300         Entry<K,V> e = prev; 301 
302         // 删除链表中“键为key”的元素 303         // 本质是“删除单向链表中的节点”
304         while (e != null) { 305             Entry<K,V> next = e.next; 306  Object k; 307             if (e.hash == hash &&
308                 ((k = e.key) == key || (key != null && key.equals(k)))) { 309                 modCount++; 310                 size--; 311                 if (prev == e) 312                     table[i] = next; 313                 else
314                     prev.next = next; 315                 e.recordRemoval(this); 316                 return e; 317  } 318             prev = e; 319             e = next; 320  } 321 
322         return e; 323  } 324 
325     // 删除“键值对”
326     final Entry<K,V> removeMapping(Object o) { 327         if (!(o instanceof Map.Entry)) 328             return null; 329 
330         Map.Entry<K,V> entry = (Map.Entry<K,V>) o; 331         Object key = entry.getKey(); 332         int hash = (key == null) ? 0 : hash(key.hashCode()); 333         int i = indexFor(hash, table.length); 334         Entry<K,V> prev = table[i]; 335         Entry<K,V> e = prev; 336 
337         // 删除链表中的“键值对e” 338         // 本质是“删除单向链表中的节点”
339         while (e != null) { 340             Entry<K,V> next = e.next; 341             if (e.hash == hash && e.equals(entry)) { 342                 modCount++; 343                 size--; 344                 if (prev == e) 345                     table[i] = next; 346                 else
347                     prev.next = next; 348                 e.recordRemoval(this); 349                 return e; 350  } 351             prev = e; 352             e = next; 353  } 354 
355         return e; 356  } 357 
358     // 清空HashMap，将所有的元素设为null
359     public void clear() { 360         modCount++; 361         Entry[] tab = table; 362         for (int i = 0; i < tab.length; i++) 363             tab[i] = null; 364         size = 0; 365  } 366 
367     // 是否包含“值为value”的元素
368     public boolean containsValue(Object value) { 369     // 若“value为null”，则调用containsNullValue()查找
370     if (value == null) 371             return containsNullValue(); 372 
373     // 若“value不为null”，则查找HashMap中是否有值为value的节点。
374     Entry[] tab = table; 375         for (int i = 0; i < tab.length ; i++) 376             for (Entry e = tab[i] ; e != null ; e = e.next) 377                 if (value.equals(e.value)) 378                     return true; 379     return false; 380  } 381 
382     // 是否包含null值
383     private boolean containsNullValue() { 384     Entry[] tab = table; 385         for (int i = 0; i < tab.length ; i++) 386             for (Entry e = tab[i] ; e != null ; e = e.next) 387                 if (e.value == null) 388                     return true; 389     return false; 390  } 391 
392     // 克隆一个HashMap，并返回Object对象
393     public Object clone() { 394         HashMap<K,V> result = null; 395         try { 396             result = (HashMap<K,V>)super.clone(); 397         } catch (CloneNotSupportedException e) { 398             // assert false;
399  } 400         result.table = new Entry[table.length]; 401         result.entrySet = null; 402         result.modCount = 0; 403         result.size = 0; 404  result.init(); 405         // 调用putAllForCreate()将全部元素添加到HashMap中
406         result.putAllForCreate(this); 407 
408         return result; 409  } 410 
411     // Entry是单向链表。 412     // 它是 “HashMap链式存储法”对应的链表。 413     // 它实现了Map.Entry 接口，即实现getKey(), getValue(), setValue(V value), equals(Object o), hashCode()这些函数
414     static class Entry<K,V> implements Map.Entry<K,V> { 415         final K key; 416  V value; 417         // 指向下一个节点
418         Entry<K,V> next; 419         final int hash; 420 
421         // 构造函数。 422         // 输入参数包括"哈希值(h)", "键(k)", "值(v)", "下一节点(n)"
423         Entry(int h, K k, V v, Entry<K,V> n) { 424             value = v; 425             next = n; 426             key = k; 427             hash = h; 428  } 429 
430         public final K getKey() { 431             return key; 432  } 433 
434         public final V getValue() { 435             return value; 436  } 437 
438         public final V setValue(V newValue) { 439             V oldValue = value; 440             value = newValue; 441             return oldValue; 442  } 443 
444         // 判断两个Entry是否相等 445         // 若两个Entry的“key”和“value”都相等，则返回true。 446         // 否则，返回false
447         public final boolean equals(Object o) { 448             if (!(o instanceof Map.Entry)) 449                 return false; 450             Map.Entry e = (Map.Entry)o; 451             Object k1 = getKey(); 452             Object k2 = e.getKey(); 453             if (k1 == k2 || (k1 != null && k1.equals(k2))) { 454                 Object v1 = getValue(); 455                 Object v2 = e.getValue(); 456                 if (v1 == v2 || (v1 != null && v1.equals(v2))) 457                     return true; 458  } 459             return false; 460  } 461 
462         // 实现hashCode()
463         public final int hashCode() { 464             return (key==null   ? 0 : key.hashCode()) ^
465                    (value==null ? 0 : value.hashCode()); 466  } 467 
468         public final String toString() { 469             return getKey() + "=" + getValue(); 470  } 471 
472         // 当向HashMap中添加元素时，绘调用recordAccess()。 473         // 这里不做任何处理
474         void recordAccess(HashMap<K,V> m) { 475  } 476 
477         // 当从HashMap中删除元素时，绘调用recordRemoval()。 478         // 这里不做任何处理
479         void recordRemoval(HashMap<K,V> m) { 480  } 481  } 482 
483     // 新增Entry。将“key-value”插入指定位置，bucketIndex是位置索引。
484     void addEntry(int hash, K key, V value, int bucketIndex) { 485         // 保存“bucketIndex”位置的值到“e”中
486         Entry<K,V> e = table[bucketIndex]; 487         // 设置“bucketIndex”位置的元素为“新Entry”， 488         // 设置“e”为“新Entry的下一个节点”
489         table[bucketIndex] = new Entry<K,V>(hash, key, value, e); 490         // 若HashMap的实际大小 不小于 “阈值”，则调整HashMap的大小
491         if (size++ >= threshold) 492             resize(2 * table.length); 493  } 494 
495     // 创建Entry。将“key-value”插入指定位置，bucketIndex是位置索引。 496     // 它和addEntry的区别是： 497     // (01) addEntry()一般用在 新增Entry可能导致“HashMap的实际容量”超过“阈值”的情况下。 498     // 例如，我们新建一个HashMap，然后不断通过put()向HashMap中添加元素； 499     // put()是通过addEntry()新增Entry的。 500     // 在这种情况下，我们不知道何时“HashMap的实际容量”会超过“阈值”； 501     // 因此，需要调用addEntry() 502     // (02) createEntry() 一般用在 新增Entry不会导致“HashMap的实际容量”超过“阈值”的情况下。 503     // 例如，我们调用HashMap“带有Map”的构造函数，它绘将Map的全部元素添加到HashMap中； 504     // 但在添加之前，我们已经计算好“HashMap的容量和阈值”。也就是，可以确定“即使将Map中 505     // 的全部元素添加到HashMap中，都不会超过HashMap的阈值”。 506     // 此时，调用createEntry()即可。
507     void createEntry(int hash, K key, V value, int bucketIndex) { 508         // 保存“bucketIndex”位置的值到“e”中
509         Entry<K,V> e = table[bucketIndex]; 510         // 设置“bucketIndex”位置的元素为“新Entry”， 511         // 设置“e”为“新Entry的下一个节点”
512         table[bucketIndex] = new Entry<K,V>(hash, key, value, e); 513         size++; 514  } 515 
516     // HashIterator是HashMap迭代器的抽象出来的父类，实现了公共了函数。 517     // 它包含“key迭代器(KeyIterator)”、“Value迭代器(ValueIterator)”和“Entry迭代器(EntryIterator)”3个子类。
518     private abstract class HashIterator<E> implements Iterator<E> { 519         // 下一个元素
520         Entry<K,V> next; 521         // expectedModCount用于实现fast-fail机制。
522         int expectedModCount; 523         // 当前索引
524         int index; 525         // 当前元素
526         Entry<K,V> current; 527 
528  HashIterator() { 529             expectedModCount = modCount; 530             if (size > 0) { // advance to first entry
531                 Entry[] t = table; 532                 // 将next指向table中第一个不为null的元素。 533                 // 这里利用了index的初始值为0，从0开始依次向后遍历，直到找到不为null的元素就退出循环。
534                 while (index < t.length && (next = t[index++]) == null) 535  ; 536  } 537  } 538 
539         public final boolean hasNext() { 540             return next != null; 541  } 542 
543         // 获取下一个元素
544         final Entry<K,V> nextEntry() { 545             if (modCount != expectedModCount) 546                 throw new ConcurrentModificationException(); 547             Entry<K,V> e = next; 548             if (e == null) 549                 throw new NoSuchElementException(); 550 
551             // 注意！！！ 552             // 一个Entry就是一个单向链表 553             // 若该Entry的下一个节点不为空，就将next指向下一个节点; 554             // 否则，将next指向下一个链表(也是下一个Entry)的不为null的节点。
555             if ((next = e.next) == null) { 556                 Entry[] t = table; 557                 while (index < t.length && (next = t[index++]) == null) 558  ; 559  } 560             current = e; 561             return e; 562  } 563 
564         // 删除当前元素
565         public void remove() { 566             if (current == null) 567                 throw new IllegalStateException(); 568             if (modCount != expectedModCount) 569                 throw new ConcurrentModificationException(); 570             Object k = current.key; 571             current = null; 572             HashMap.this.removeEntryForKey(k); 573             expectedModCount = modCount; 574  } 575 
576  } 577 
578     // value的迭代器
579     private final class ValueIterator extends HashIterator<V> { 580         public V next() { 581             return nextEntry().value; 582  } 583  } 584 
585     // key的迭代器
586     private final class KeyIterator extends HashIterator<K> { 587         public K next() { 588             return nextEntry().getKey(); 589  } 590  } 591 
592     // Entry的迭代器
593     private final class EntryIterator extends HashIterator<Map.Entry<K,V>> { 594         public Map.Entry<K,V> next() { 595             return nextEntry(); 596  } 597  } 598 
599     // 返回一个“key迭代器”
600     Iterator<K> newKeyIterator() { 601         return new KeyIterator(); 602  } 603     // 返回一个“value迭代器”
604     Iterator<V> newValueIterator() { 605         return new ValueIterator(); 606  } 607     // 返回一个“entry迭代器”
608     Iterator<Map.Entry<K,V>> newEntryIterator() { 609         return new EntryIterator(); 610  } 611 
612     // HashMap的Entry对应的集合
613     private transient Set<Map.Entry<K,V>> entrySet = null; 614 
615     // 返回“key的集合”，实际上返回一个“KeySet对象”
616     public Set<K> keySet() { 617         Set<K> ks = keySet; 618         return (ks != null ? ks : (keySet = new KeySet())); 619  } 620 
621     // Key对应的集合 622     // KeySet继承于AbstractSet，说明该集合中没有重复的Key。
623     private final class KeySet extends AbstractSet<K> { 624         public Iterator<K> iterator() { 625             return newKeyIterator(); 626  } 627         public int size() { 628             return size; 629  } 630         public boolean contains(Object o) { 631             return containsKey(o); 632  } 633         public boolean remove(Object o) { 634             return HashMap.this.removeEntryForKey(o) != null; 635  } 636         public void clear() { 637             HashMap.this.clear(); 638  } 639  } 640 
641     // 返回“value集合”，实际上返回的是一个Values对象
642     public Collection<V> values() { 643         Collection<V> vs = values; 644         return (vs != null ? vs : (values = new Values())); 645  } 646 
647     // “value集合” 648     // Values继承于AbstractCollection，不同于“KeySet继承于AbstractSet”， 649     // Values中的元素能够重复。因为不同的key可以指向相同的value。
650     private final class Values extends AbstractCollection<V> { 651         public Iterator<V> iterator() { 652             return newValueIterator(); 653  } 654         public int size() { 655             return size; 656  } 657         public boolean contains(Object o) { 658             return containsValue(o); 659  } 660         public void clear() { 661             HashMap.this.clear(); 662  } 663  } 664 
665     // 返回“HashMap的Entry集合”
666     public Set<Map.Entry<K,V>> entrySet() { 667         return entrySet0(); 668  } 669 
670     // 返回“HashMap的Entry集合”，它实际是返回一个EntrySet对象
671     private Set<Map.Entry<K,V>> entrySet0() { 672         Set<Map.Entry<K,V>> es = entrySet; 673         return es != null ? es : (entrySet = new EntrySet()); 674  } 675 
676     // EntrySet对应的集合 677     // EntrySet继承于AbstractSet，说明该集合中没有重复的EntrySet。
678     private final class EntrySet extends AbstractSet<Map.Entry<K,V>> { 679         public Iterator<Map.Entry<K,V>> iterator() { 680             return newEntryIterator(); 681  } 682         public boolean contains(Object o) { 683             if (!(o instanceof Map.Entry)) 684                 return false; 685             Map.Entry<K,V> e = (Map.Entry<K,V>) o; 686             Entry<K,V> candidate = getEntry(e.getKey()); 687             return candidate != null && candidate.equals(e); 688  } 689         public boolean remove(Object o) { 690             return removeMapping(o) != null; 691  } 692         public int size() { 693             return size; 694  } 695         public void clear() { 696             HashMap.this.clear(); 697  } 698  } 699 
700     // java.io.Serializable的写入函数 701     // 将HashMap的“总的容量，实际容量，所有的Entry”都写入到输出流中
702     private void writeObject(java.io.ObjectOutputStream s) 703         throws IOException 704  { 705         Iterator<Map.Entry<K,V>> i =
706             (size > 0) ? entrySet0().iterator() : null; 707 
708         // Write out the threshold, loadfactor, and any hidden stuff
709  s.defaultWriteObject(); 710 
711         // Write out number of buckets
712  s.writeInt(table.length); 713 
714         // Write out size (number of Mappings)
715  s.writeInt(size); 716 
717         // Write out keys and values (alternating)
718         if (i != null) { 719             while (i.hasNext()) { 720             Map.Entry<K,V> e = i.next(); 721  s.writeObject(e.getKey()); 722  s.writeObject(e.getValue()); 723  } 724  } 725  } 726 
727 
728     private static final long serialVersionUID = 362498820763181265L; 729 
730     // java.io.Serializable的读取函数：根据写入方式读出 731     // 将HashMap的“总的容量，实际容量，所有的Entry”依次读出
732     private void readObject(java.io.ObjectInputStream s) 733          throws IOException, ClassNotFoundException 734  { 735         // Read in the threshold, loadfactor, and any hidden stuff
736  s.defaultReadObject(); 737 
738         // Read in number of buckets and allocate the bucket array;
739         int numBuckets = s.readInt(); 740         table = new Entry[numBuckets]; 741 
742         init();  // Give subclass a chance to do its thing. 743 
744         // Read in size (number of Mappings)
745         int size = s.readInt(); 746 
747         // Read the keys and values, and put the mappings in the HashMap
748         for (int i=0; i<size; i++) { 749             K key = (K) s.readObject(); 750             V value = (V) s.readObject(); 751  putForCreate(key, value); 752  } 753  } 754 
755     // 返回“HashMap总的容量”
756     int   capacity()     { return table.length; } 757     // 返回“HashMap的加载因子”
758     float loadFactor()   { return loadFactor; } 759 }
复制代码
说明:

在详细介绍HashMap的代码之前，我们需要了解：HashMap就是一个散列表，它是通过“拉链法”解决哈希冲突的。
还需要再补充说明的一点是影响HashMap性能的有两个参数：初始容量(initialCapacity) 和加载因子(loadFactor)。容量 是哈希表中桶的数量，初始容量只是哈希表在创建时的容量。加载因子 是哈希表在其容量自动增加之前可以达到多满的一种尺度。当哈希表中的条目数超出了加载因子与当前容量的乘积时，则要对该哈希表进行 rehash 操作（即重建内部数据结构），从而哈希表将具有大约两倍的桶数。


第3.1部分 HashMap的“拉链法”相关内容

3.1.1 HashMap数据存储数组

transient Entry[] table;
HashMap中的key-value都是存储在Entry数组中的。

3.1.2 数据节点Entry的数据结构


复制代码
 1 static class Entry<K,V> implements Map.Entry<K,V> {  2     final K key;  3  V value;  4     // 指向下一个节点
 5     Entry<K,V> next;  6     final int hash;  7 
 8     // 构造函数。  9     // 输入参数包括"哈希值(h)", "键(k)", "值(v)", "下一节点(n)"
10     Entry(int h, K k, V v, Entry<K,V> n) { 11         value = v; 12         next = n; 13         key = k; 14         hash = h; 15  } 16 
17     public final K getKey() { 18         return key; 19  } 20 
21     public final V getValue() { 22         return value; 23  } 24 
25     public final V setValue(V newValue) { 26         V oldValue = value; 27         value = newValue; 28         return oldValue; 29  } 30 
31     // 判断两个Entry是否相等 32     // 若两个Entry的“key”和“value”都相等，则返回true。 33     // 否则，返回false
34     public final boolean equals(Object o) { 35         if (!(o instanceof Map.Entry)) 36             return false; 37         Map.Entry e = (Map.Entry)o; 38         Object k1 = getKey(); 39         Object k2 = e.getKey(); 40         if (k1 == k2 || (k1 != null && k1.equals(k2))) { 41             Object v1 = getValue(); 42             Object v2 = e.getValue(); 43             if (v1 == v2 || (v1 != null && v1.equals(v2))) 44                 return true; 45  } 46         return false; 47  } 48 
49     // 实现hashCode()
50     public final int hashCode() { 51         return (key==null   ? 0 : key.hashCode()) ^
52                (value==null ? 0 : value.hashCode()); 53  } 54 
55     public final String toString() { 56         return getKey() + "=" + getValue(); 57  } 58 
59     // 当向HashMap中添加元素时，绘调用recordAccess()。 60     // 这里不做任何处理
61     void recordAccess(HashMap<K,V> m) { 62  } 63 
64     // 当从HashMap中删除元素时，绘调用recordRemoval()。 65     // 这里不做任何处理
66     void recordRemoval(HashMap<K,V> m) { 67  } 68 }
复制代码
从中，我们可以看出 Entry 实际上就是一个单向链表。这也是为什么我们说HashMap是通过拉链法解决哈希冲突的。
Entry 实现了Map.Entry 接口，即实现getKey(), getValue(), setValue(V value), equals(Object o), hashCode()这些函数。这些都是基本的读取/修改key、value值的函数。

 

第3.2部分 HashMap的构造函数

HashMap共包括4个构造函数


复制代码
 1 // 默认构造函数。
 2 public HashMap() {  3     // 设置“加载因子”
 4     this.loadFactor = DEFAULT_LOAD_FACTOR;  5     // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
 6     threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);  7     // 创建Entry数组，用来保存数据
 8     table = new Entry[DEFAULT_INITIAL_CAPACITY];  9  init(); 10 } 11 
12 // 指定“容量大小”和“加载因子”的构造函数
13 public HashMap(int initialCapacity, float loadFactor) { 14     if (initialCapacity < 0) 15         throw new IllegalArgumentException("Illegal initial capacity: " +
16  initialCapacity); 17     // HashMap的最大容量只能是MAXIMUM_CAPACITY
18     if (initialCapacity > MAXIMUM_CAPACITY) 19         initialCapacity = MAXIMUM_CAPACITY; 20     if (loadFactor <= 0 || Float.isNaN(loadFactor)) 21         throw new IllegalArgumentException("Illegal load factor: " +
22  loadFactor); 23 
24     // Find a power of 2 >= initialCapacity
25     int capacity = 1; 26     while (capacity < initialCapacity) 27         capacity <<= 1; 28 
29     // 设置“加载因子”
30     this.loadFactor = loadFactor; 31     // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
32     threshold = (int)(capacity * loadFactor); 33     // 创建Entry数组，用来保存数据
34     table = new Entry[capacity]; 35  init(); 36 } 37 
38 // 指定“容量大小”的构造函数
39 public HashMap(int initialCapacity) { 40     this(initialCapacity, DEFAULT_LOAD_FACTOR); 41 } 42 
43 // 包含“子Map”的构造函数
44 public HashMap(Map<? extends K, ? extends V> m) { 45     this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, 46  DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR); 47     // 将m中的全部元素逐个添加到HashMap中
48  putAllForCreate(m); 49 }
复制代码
 

第3.3部分 HashMap的主要对外接口

3.3.1 clear()

clear() 的作用是清空HashMap。它是通过将所有的元素设为null来实现的。


复制代码
1 public void clear() { 2     modCount++; 3     Entry[] tab = table; 4     for (int i = 0; i < tab.length; i++) 5         tab[i] = null; 6     size = 0; 7 }
复制代码
 

3.3.2 containsKey()

containsKey() 的作用是判断HashMap是否包含key。

public boolean containsKey(Object key) { return getEntry(key) != null; }
containsKey() 首先通过getEntry(key)获取key对应的Entry，然后判断该Entry是否为null。
getEntry()的源码如下：


复制代码
 1 final Entry<K,V> getEntry(Object key) {  2     // 获取哈希值  3     // HashMap将“key为null”的元素存储在table[0]位置，“key不为null”的则调用hash()计算哈希值
 4     int hash = (key == null) ? 0 : hash(key.hashCode());  5     // 在“该hash值对应的链表”上查找“键值等于key”的元素
 6     for (Entry<K,V> e = table[indexFor(hash, table.length)];  7          e != null;  8          e = e.next) {  9  Object k; 10         if (e.hash == hash &&
11             ((k = e.key) == key || (key != null && key.equals(k)))) 12             return e; 13  } 14     return null; 15 }
复制代码
getEntry() 的作用就是返回“键为key”的键值对，它的实现源码中已经进行了说明。
这里需要强调的是：HashMap将“key为null”的元素都放在table的位置0处，即table[0]中；“key不为null”的放在table的其余位置！


3.3.3 containsValue()

containsValue() 的作用是判断HashMap是否包含“值为value”的元素。


复制代码
 1 public boolean containsValue(Object value) {  2     // 若“value为null”，则调用containsNullValue()查找
 3     if (value == null)  4         return containsNullValue();  5 
 6     // 若“value不为null”，则查找HashMap中是否有值为value的节点。
 7     Entry[] tab = table;  8     for (int i = 0; i < tab.length ; i++)  9         for (Entry e = tab[i] ; e != null ; e = e.next) 10             if (value.equals(e.value)) 11                 return true; 12     return false; 13 }
复制代码
从中，我们可以看出containsNullValue()分为两步进行处理：第一，若“value为null”，则调用containsNullValue()。第二，若“value不为null”，则查找HashMap中是否有值为value的节点。

containsNullValue() 的作用判断HashMap中是否包含“值为null”的元素。


复制代码
1 private boolean containsNullValue() { 2     Entry[] tab = table; 3     for (int i = 0; i < tab.length ; i++) 4         for (Entry e = tab[i] ; e != null ; e = e.next) 5             if (e.value == null) 6                 return true; 7     return false; 8 }
复制代码
 

3.3.4 entrySet()、values()、keySet()

它们3个的原理类似，这里以entrySet()为例来说明。
entrySet()的作用是返回“HashMap中所有Entry的集合”，它是一个集合。实现代码如下：


复制代码
 1 // 返回“HashMap的Entry集合”
 2 public Set<Map.Entry<K,V>> entrySet() {  3     return entrySet0();  4 }  5 
 6 // 返回“HashMap的Entry集合”，它实际是返回一个EntrySet对象
 7 private Set<Map.Entry<K,V>> entrySet0() {  8     Set<Map.Entry<K,V>> es = entrySet;  9     return es != null ? es : (entrySet = new EntrySet()); 10 } 11 
12 // EntrySet对应的集合 13 // EntrySet继承于AbstractSet，说明该集合中没有重复的EntrySet。
14 private final class EntrySet extends AbstractSet<Map.Entry<K,V>> { 15     public Iterator<Map.Entry<K,V>> iterator() { 16         return newEntryIterator(); 17  } 18     public boolean contains(Object o) { 19         if (!(o instanceof Map.Entry)) 20             return false; 21         Map.Entry<K,V> e = (Map.Entry<K,V>) o; 22         Entry<K,V> candidate = getEntry(e.getKey()); 23         return candidate != null && candidate.equals(e); 24  } 25     public boolean remove(Object o) { 26         return removeMapping(o) != null; 27  } 28     public int size() { 29         return size; 30  } 31     public void clear() { 32         HashMap.this.clear(); 33  } 34 }
复制代码
 

HashMap是通过拉链法实现的散列表。表现在HashMap包括许多的Entry，而每一个Entry本质上又是一个单向链表。那么HashMap遍历key-value键值对的时候，是如何逐个去遍历的呢？


下面我们就看看HashMap是如何通过entrySet()遍历的。
entrySet()实际上是通过newEntryIterator()实现的。 下面我们看看它的代码：


复制代码
 1 // 返回一个“entry迭代器”
 2 Iterator<Map.Entry<K,V>> newEntryIterator() {  3     return new EntryIterator();  4 }  5 
 6 // Entry的迭代器
 7 private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {  8     public Map.Entry<K,V> next() {  9         return nextEntry(); 10  } 11 } 12 
13 // HashIterator是HashMap迭代器的抽象出来的父类，实现了公共了函数。 14 // 它包含“key迭代器(KeyIterator)”、“Value迭代器(ValueIterator)”和“Entry迭代器(EntryIterator)”3个子类。
15 private abstract class HashIterator<E> implements Iterator<E> { 16     // 下一个元素
17     Entry<K,V> next; 18     // expectedModCount用于实现fast-fail机制。
19     int expectedModCount; 20     // 当前索引
21     int index; 22     // 当前元素
23     Entry<K,V> current; 24 
25  HashIterator() { 26         expectedModCount = modCount; 27         if (size > 0) { // advance to first entry
28             Entry[] t = table; 29             // 将next指向table中第一个不为null的元素。 30             // 这里利用了index的初始值为0，从0开始依次向后遍历，直到找到不为null的元素就退出循环。
31             while (index < t.length && (next = t[index++]) == null) 32  ; 33  } 34  } 35 
36     public final boolean hasNext() { 37         return next != null; 38  } 39 
40     // 获取下一个元素
41     final Entry<K,V> nextEntry() { 42         if (modCount != expectedModCount) 43             throw new ConcurrentModificationException(); 44         Entry<K,V> e = next; 45         if (e == null) 46             throw new NoSuchElementException(); 47 
48         // 注意！！！ 49         // 一个Entry就是一个单向链表 50         // 若该Entry的下一个节点不为空，就将next指向下一个节点; 51         // 否则，将next指向下一个链表(也是下一个Entry)的不为null的节点。
52         if ((next = e.next) == null) { 53             Entry[] t = table; 54             while (index < t.length && (next = t[index++]) == null) 55  ; 56  } 57         current = e; 58         return e; 59  } 60 
61     // 删除当前元素
62     public void remove() { 63         if (current == null) 64             throw new IllegalStateException(); 65         if (modCount != expectedModCount) 66             throw new ConcurrentModificationException(); 67         Object k = current.key; 68         current = null; 69         HashMap.this.removeEntryForKey(k); 70         expectedModCount = modCount; 71  } 72 
73 }
复制代码
当我们通过entrySet()获取到的Iterator的next()方法去遍历HashMap时，实际上调用的是 nextEntry() 。而nextEntry()的实现方式，先遍历Entry(根据Entry在table中的序号，从小到大的遍历)；然后对每个Entry(即每个单向链表)，逐个遍历。


3.3.5 get()

get() 的作用是获取key对应的value，它的实现代码如下：


复制代码
 1 public V get(Object key) {  2     if (key == null)  3         return getForNullKey();  4     // 获取key的hash值
 5     int hash = hash(key.hashCode());  6     // 在“该hash值对应的链表”上查找“键值等于key”的元素
 7     for (Entry<K,V> e = table[indexFor(hash, table.length)];  8          e != null;  9          e = e.next) { 10  Object k; 11         if (e.hash == hash && ((k = e.key) == key || key.equals(k))) 12             return e.value; 13  } 14     return null; 15 }
复制代码
 

3.3.6 put()

put() 的作用是对外提供接口，让HashMap对象可以通过put()将“key-value”添加到HashMap中。


复制代码
 1 public V put(K key, V value) {  2     // 若“key为null”，则将该键值对添加到table[0]中。
 3     if (key == null)  4         return putForNullKey(value);  5     // 若“key不为null”，则计算该key的哈希值，然后将其添加到该哈希值对应的链表中。
 6     int hash = hash(key.hashCode());  7     int i = indexFor(hash, table.length);  8     for (Entry<K,V> e = table[i]; e != null; e = e.next) {  9  Object k; 10         // 若“该key”对应的键值对已经存在，则用新的value取代旧的value。然后退出！
11         if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { 12             V oldValue = e.value; 13             e.value = value; 14             e.recordAccess(this); 15             return oldValue; 16  } 17  } 18 
19     // 若“该key”对应的键值对不存在，则将“key-value”添加到table中
20     modCount++; 21  addEntry(hash, key, value, i); 22     return null; 23 }
复制代码
若要添加到HashMap中的键值对对应的key已经存在HashMap中，则找到该键值对；然后新的value取代旧的value，并退出！
若要添加到HashMap中的键值对对应的key不在HashMap中，则将其添加到该哈希值对应的链表中，并调用addEntry()。
下面看看addEntry()的代码：


复制代码
 1 void addEntry(int hash, K key, V value, int bucketIndex) {  2     // 保存“bucketIndex”位置的值到“e”中
 3     Entry<K,V> e = table[bucketIndex];  4     // 设置“bucketIndex”位置的元素为“新Entry”，  5     // 设置“e”为“新Entry的下一个节点”
 6     table[bucketIndex] = new Entry<K,V>(hash, key, value, e);  7     // 若HashMap的实际大小 不小于 “阈值”，则调整HashMap的大小
 8     if (size++ >= threshold)  9         resize(2 * table.length); 10 }
复制代码
addEntry() 的作用是新增Entry。将“key-value”插入指定位置，bucketIndex是位置索引。

说到addEntry()，就不得不说另一个函数createEntry()。createEntry()的代码如下：


复制代码
1 void createEntry(int hash, K key, V value, int bucketIndex) { 2     // 保存“bucketIndex”位置的值到“e”中
3     Entry<K,V> e = table[bucketIndex]; 4     // 设置“bucketIndex”位置的元素为“新Entry”， 5     // 设置“e”为“新Entry的下一个节点”
6     table[bucketIndex] = new Entry<K,V>(hash, key, value, e); 7     size++; 8 }
复制代码
它们的作用都是将key、value添加到HashMap中。而且，比较addEntry()和createEntry()的代码，我们发现addEntry()多了两句：

if (size++ >= threshold) resize(2 * table.length);
那它们的区别到底是什么呢？
阅读代码，我们可以发现，它们的使用情景不同。
(01) addEntry()一般用在 新增Entry可能导致“HashMap的实际容量”超过“阈值”的情况下。
       例如，我们新建一个HashMap，然后不断通过put()向HashMap中添加元素；put()是通过addEntry()新增Entry的。
       在这种情况下，我们不知道何时“HashMap的实际容量”会超过“阈值”；
       因此，需要调用addEntry()
(02) createEntry() 一般用在 新增Entry不会导致“HashMap的实际容量”超过“阈值”的情况下。
        例如，我们调用HashMap“带有Map”的构造函数，它绘将Map的全部元素添加到HashMap中；
       但在添加之前，我们已经计算好“HashMap的容量和阈值”。也就是，可以确定“即使将Map中的全部元素添加到HashMap中，都不会超过HashMap的阈值”。
       此时，调用createEntry()即可。

 

3.3.7 putAll()

putAll() 的作用是将"m"的全部元素都添加到HashMap中，它的代码如下：


复制代码
 1 public void putAll(Map<? extends K, ? extends V> m) {  2     // 有效性判断
 3     int numKeysToBeAdded = m.size();  4     if (numKeysToBeAdded == 0)  5         return;  6 
 7     // 计算容量是否足够，  8     // 若“当前实际容量 < 需要的容量”，则将容量x2。
 9     if (numKeysToBeAdded > threshold) { 10         int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1); 11         if (targetCapacity > MAXIMUM_CAPACITY) 12             targetCapacity = MAXIMUM_CAPACITY; 13         int newCapacity = table.length; 14         while (newCapacity < targetCapacity) 15             newCapacity <<= 1; 16         if (newCapacity > table.length) 17  resize(newCapacity); 18  } 19 
20     // 通过迭代器，将“m”中的元素逐个添加到HashMap中。
21     for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) { 22         Map.Entry<? extends K, ? extends V> e = i.next(); 23  put(e.getKey(), e.getValue()); 24  } 25 }
复制代码
 

3.3.8 remove()

remove() 的作用是删除“键为key”元素


复制代码
 1 public V remove(Object key) {  2     Entry<K,V> e = removeEntryForKey(key);  3     return (e == null ? null : e.value);  4 }  5 
 6 
 7 // 删除“键为key”的元素
 8 final Entry<K,V> removeEntryForKey(Object key) {  9     // 获取哈希值。若key为null，则哈希值为0；否则调用hash()进行计算
10     int hash = (key == null) ? 0 : hash(key.hashCode()); 11     int i = indexFor(hash, table.length); 12     Entry<K,V> prev = table[i]; 13     Entry<K,V> e = prev; 14 
15     // 删除链表中“键为key”的元素 16     // 本质是“删除单向链表中的节点”
17     while (e != null) { 18         Entry<K,V> next = e.next; 19  Object k; 20         if (e.hash == hash &&
21             ((k = e.key) == key || (key != null && key.equals(k)))) { 22             modCount++; 23             size--; 24             if (prev == e) 25                 table[i] = next; 26             else
27                 prev.next = next; 28             e.recordRemoval(this); 29             return e; 30  } 31         prev = e; 32         e = next; 33  } 34 
35     return e; 36 }
复制代码
 

第3.4部分 HashMap实现的Cloneable接口

HashMap实现了Cloneable接口，即实现了clone()方法。
clone()方法的作用很简单，就是克隆一个HashMap对象并返回。


复制代码
 1 // 克隆一个HashMap，并返回Object对象
 2 public Object clone() {  3     HashMap<K,V> result = null;  4     try {  5         result = (HashMap<K,V>)super.clone();  6     } catch (CloneNotSupportedException e) {  7         // assert false;
 8  }  9     result.table = new Entry[table.length]; 10     result.entrySet = null; 11     result.modCount = 0; 12     result.size = 0; 13  result.init(); 14     // 调用putAllForCreate()将全部元素添加到HashMap中
15     result.putAllForCreate(this); 16 
17     return result; 18 }
复制代码
 

第3.5部分 HashMap实现的Serializable接口

HashMap实现java.io.Serializable，分别实现了串行读取、写入功能。
串行写入函数是writeObject()，它的作用是将HashMap的“总的容量，实际容量，所有的Entry”都写入到输出流中。
而串行读取函数是readObject()，它的作用是将HashMap的“总的容量，实际容量，所有的Entry”依次读出


复制代码
 1 // java.io.Serializable的写入函数  2 // 将HashMap的“总的容量，实际容量，所有的Entry”都写入到输出流中
 3 private void writeObject(java.io.ObjectOutputStream s)  4     throws IOException  5 {  6     Iterator<Map.Entry<K,V>> i =
 7         (size > 0) ? entrySet0().iterator() : null;  8 
 9     // Write out the threshold, loadfactor, and any hidden stuff
10  s.defaultWriteObject(); 11 
12     // Write out number of buckets
13  s.writeInt(table.length); 14 
15     // Write out size (number of Mappings)
16  s.writeInt(size); 17 
18     // Write out keys and values (alternating)
19     if (i != null) { 20         while (i.hasNext()) { 21         Map.Entry<K,V> e = i.next(); 22  s.writeObject(e.getKey()); 23  s.writeObject(e.getValue()); 24  } 25  } 26 } 27 
28 // java.io.Serializable的读取函数：根据写入方式读出 29 // 将HashMap的“总的容量，实际容量，所有的Entry”依次读出
30 private void readObject(java.io.ObjectInputStream s) 31      throws IOException, ClassNotFoundException 32 { 33     // Read in the threshold, loadfactor, and any hidden stuff
34  s.defaultReadObject(); 35 
36     // Read in number of buckets and allocate the bucket array;
37     int numBuckets = s.readInt(); 38     table = new Entry[numBuckets]; 39 
40     init();  // Give subclass a chance to do its thing. 41 
42     // Read in size (number of Mappings)
43     int size = s.readInt(); 44 
45     // Read the keys and values, and put the mappings in the HashMap
46     for (int i=0; i<size; i++) { 47         K key = (K) s.readObject(); 48         V value = (V) s.readObject(); 49  putForCreate(key, value); 50  } 51 }
复制代码
 

第4部分 HashMap遍历方式

4.1 遍历HashMap的键值对

第一步：根据entrySet()获取HashMap的“键值对”的Set集合。
第二步：通过Iterator迭代器遍历“第一步”得到的集合。

复制代码
// 假设map是HashMap对象 // map中的key是String类型，value是Integer类型
Integer integ = null; Iterator iter = map.entrySet().iterator(); while(iter.hasNext()) { Map.Entry entry = (Map.Entry)iter.next(); // 获取key
    key = (String)entry.getKey(); // 获取value
    integ = (Integer)entry.getValue(); }
复制代码
4.2 遍历HashMap的键

第一步：根据keySet()获取HashMap的“键”的Set集合。
第二步：通过Iterator迭代器遍历“第一步”得到的集合。

复制代码
// 假设map是HashMap对象 // map中的key是String类型，value是Integer类型
String key = null; Integer integ = null; Iterator iter = map.keySet().iterator(); while (iter.hasNext()) { // 获取key
    key = (String)iter.next(); // 根据key，获取value
    integ = (Integer)map.get(key); }
复制代码
4.3 遍历HashMap的值

第一步：根据value()获取HashMap的“值”的集合。
第二步：通过Iterator迭代器遍历“第一步”得到的集合。

复制代码
// 假设map是HashMap对象 // map中的key是String类型，value是Integer类型
Integer value = null; Collection c = map.values(); Iterator iter= c.iterator(); while (iter.hasNext()) { value = (Integer)iter.next(); }
复制代码
 

遍历测试程序如下：


复制代码
 1 import java.util.Map;  2 import java.util.Random;  3 import java.util.Iterator;  4 import java.util.HashMap;  5 import java.util.HashSet;  6 import java.util.Map.Entry;  7 import java.util.Collection;  8 
 9 /*
 10  * @desc 遍历HashMap的测试程序。  11  * (01) 通过entrySet()去遍历key、value，参考实现函数：  12  * iteratorHashMapByEntryset()  13  * (02) 通过keySet()去遍历key、value，参考实现函数：  14  * iteratorHashMapByKeyset()  15  * (03) 通过values()去遍历value，参考实现函数：  16  * iteratorHashMapJustValues()  17  *  18  * @author skywang  19  */
 20 public class HashMapIteratorTest {  21 
 22     public static void main(String[] args) {  23         int val = 0;  24         String key = null;  25         Integer value = null;  26         Random r = new Random();  27         HashMap map = new HashMap();  28 
 29         for (int i=0; i<12; i++) {  30             // 随机获取一个[0,100)之间的数字
 31             val = r.nextInt(100);  32             
 33             key = String.valueOf(val);  34             value = r.nextInt(5);  35             // 添加到HashMap中
 36  map.put(key, value);  37             System.out.println(" key:"+key+" value:"+value);  38  }  39         // 通过entrySet()遍历HashMap的key-value
 40  iteratorHashMapByEntryset(map) ;  41         
 42         // 通过keySet()遍历HashMap的key-value
 43  iteratorHashMapByKeyset(map) ;  44         
 45         // 单单遍历HashMap的value
 46  iteratorHashMapJustValues(map);  47  }  48     
 49     /*
 50  * 通过entry set遍历HashMap  51  * 效率高!  52      */
 53     private static void iteratorHashMapByEntryset(HashMap map) {  54         if (map == null)  55             return ;  56 
 57         System.out.println("\niterator HashMap By entryset");  58         String key = null;  59         Integer integ = null;  60         Iterator iter = map.entrySet().iterator();  61         while(iter.hasNext()) {  62             Map.Entry entry = (Map.Entry)iter.next();  63             
 64             key = (String)entry.getKey();  65             integ = (Integer)entry.getValue();  66             System.out.println(key+" -- "+integ.intValue());  67  }  68  }  69 
 70     /*
 71  * 通过keyset来遍历HashMap  72  * 效率低!  73      */
 74     private static void iteratorHashMapByKeyset(HashMap map) {  75         if (map == null)  76             return ;  77 
 78         System.out.println("\niterator HashMap By keyset");  79         String key = null;  80         Integer integ = null;  81         Iterator iter = map.keySet().iterator();  82         while (iter.hasNext()) {  83             key = (String)iter.next();  84             integ = (Integer)map.get(key);  85             System.out.println(key+" -- "+integ.intValue());  86  }  87  }  88     
 89 
 90     /*
 91  * 遍历HashMap的values  92      */
 93     private static void iteratorHashMapJustValues(HashMap map) {  94         if (map == null)  95             return ;  96         
 97         Collection c = map.values();  98         Iterator iter= c.iterator();  99         while (iter.hasNext()) { 100  System.out.println(iter.next()); 101  } 102  } 103 }
复制代码
 

第5部分 HashMap示例

下面通过一个实例学习如何使用HashMap


复制代码
 1 import java.util.Map;  2 import java.util.Random;  3 import java.util.Iterator;  4 import java.util.HashMap;  5 import java.util.HashSet;  6 import java.util.Map.Entry;  7 import java.util.Collection;  8 
 9 /*
10  * @desc HashMap测试程序 11  * 12  * @author skywang 13  */
14 public class HashMapTest { 15 
16     public static void main(String[] args) { 17  testHashMapAPIs(); 18  } 19     
20     private static void testHashMapAPIs() { 21         // 初始化随机种子
22         Random r = new Random(); 23         // 新建HashMap
24         HashMap map = new HashMap(); 25         // 添加操作
26         map.put("one", r.nextInt(10)); 27         map.put("two", r.nextInt(10)); 28         map.put("three", r.nextInt(10)); 29 
30         // 打印出map
31         System.out.println("map:"+map ); 32 
33         // 通过Iterator遍历key-value
34         Iterator iter = map.entrySet().iterator(); 35         while(iter.hasNext()) { 36             Map.Entry entry = (Map.Entry)iter.next(); 37             System.out.println("next : "+ entry.getKey() +" - "+entry.getValue()); 38  } 39 
40         // HashMap的键值对个数 
41         System.out.println("size:"+map.size()); 42 
43         // containsKey(Object key) :是否包含键key
44         System.out.println("contains key two : "+map.containsKey("two")); 45         System.out.println("contains key five : "+map.containsKey("five")); 46 
47         // containsValue(Object value) :是否包含值value
48         System.out.println("contains value 0 : "+map.containsValue(new Integer(0))); 49 
50         // remove(Object key) ： 删除键key对应的键值对
51         map.remove("three"); 52 
53         System.out.println("map:"+map ); 54 
55         // clear() ： 清空HashMap
56  map.clear(); 57 
58         // isEmpty() : HashMap是否为空
59         System.out.println((map.isEmpty()?"map is empty":"map is not empty") ); 60  } 61 }
复制代码
 (某一次)运行结果： 

复制代码
map:{two=7, one=9, three=6} next : two - 7 next : one - 9 next : three - 6 size:3 contains key two : true contains key five : false contains value 0 : false map:{two=7, one=9} map is empty
复制代码



