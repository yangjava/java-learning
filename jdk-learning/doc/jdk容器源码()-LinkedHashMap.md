LinkedHashMap分析：

/**
 *  双向链表，用来维护存储的元素顺序，根据accessOrder来判断是存储
 * 访问顺序还是插入顺序。
 *  因为LinkedHashMap是继承了HashMap,LinkedHashMap其实维护了两组数     * 据，底层是HashMap中的Entry[] table
 */
private transient Entry<K,V> header;
/**
*   true表示按照访问顺序迭代，false时表示按照插入顺序
*   默认情况下都是false,只有一个构造方法中提供设置参数
*   LRU:Least Recently Used最近最少使用算法 
*/
private final boolean accessOrder;
/**
* 可以设置accessOrder的构造方法
*/
public LinkedHashMap(int initialCapacity, float loadFactor ,boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
/**
*它是在HashMap构造方法中最后被调用的，在HashMap中无意义，专门给继承类*覆盖的。这里初始化了LinkedHashMap的header。
*
*/
void init() {
    header = new Entry<>(-1, null, null, null);
    header.before = header.after = header;
}
/**
*是在resize方法中调用的，LinkedHashMap重写了这个方法，原HashMaph中的transfer遍历table中的每个元素Entry，然后对每个元素再进行遍历得到每一个Entry，重新hash到newTable中。
*而LinkedHashMap因为维护了一个header双向链表，只需循环header,提高了迭代效率。
*/
void transfer(HashMap.Entry[] newTable) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e = header.after; e != header; e = e.after) {
            int index = indexFor(e.hash, newCapacity);
            e.next = newTable[index];
            newTable[index] = e;
        }
    }
/**
*为了header的双向链表与底层HashMap中的table共同维护，LinkedHashMap覆盖了void addEntry(int hash, K key, V value, int bucketIndex) 和void  createEntry(int hash, K key, V value, int bucketIndex)，同时覆盖了类Entry中的void recordAccess(HashMap m)实现header的排序
*
*
*/
void addEntry(int hash, K key, V value, int bucketIndex) {
    createEntry(hash, key, value, bucketIndex);
    // Remove eldest entry if instructed, else grow capacity if appropriate
    Entry<K,V> eldest = header.after;
    if (removeEldestEntry(eldest)) {
        removeEntryForKey(eldest.key);
    } else {
        if (size >= threshold)
            resize(2 * table.length);
    }
}
/**
*方法默认返回false,提供该方法是为了可以修改为判断是否需要移除最后那个entry,实现比如缓存中将最老的元素移除。
*/
protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }

//同时维护了header
void createEntry(int hash, K key, V value, int bucketIndex) {
    HashMap.Entry<K,V> old = table[bucketIndex];
    Entry<K,V> e = new Entry<>(hash, key, value, old);
    table[bucketIndex] = e;
    e.addBefore(header);
    size++;
}
//插入到header前
private void addBefore(Entry<K,V> existingEntry) {
            after  = existingEntry;
            before = existingEntry.before;
            before.after = this;
            after.before = this;
        }
//当调用了get或set方法时，此方法被调用用来维护header中元素的顺序。
void recordAccess(HashMap<K,V> m) {
            LinkedHashMap<K,V> lm = (LinkedHashMap<K,V>)m;
            if (lm.accessOrder) {
                lm.modCount++;
                remove();
                addBefore(lm.header);
            }
        }
        
        
  
  
  
1. LinkedHashMap 概述
LinkedHashMap 是 Map 接口的哈希表和链接列表实现，具有可预知的迭代顺序。此实
现提供所有可选的映射操作，并允许使用 null 值和 null 键。此类不保证映射的顺序，特别
是它不保证该顺序恒久不变。
LinkedHashMap 实现与 HashMap 的不同之处在于，后者维护着一个运行于所有条目
的双重链接列表。此链接列表定义了迭代顺序，该迭代顺序可以是插入顺序或者是访问顺序。
注意，此实现不是同步的。如果多个线程同时访问链接的哈希映射，而其中至少一个线
程从结构上修改了该映射，则它必须保持外部同步。
2. LinkedHashMap 的实现
对于 LinkedHashMap 而言，它继承与 HashMap、底层使用哈希表与双向链表来保存
所有元素。其基本操作与父类 HashMap 相似，它通过重写父类相关的方法，来实现自己的
链接列表特性。下面我们来分析 LinkedHashMap 的源代码：
1) Entry 元素：
LinkedHashMap 采用的 hash 算法和 HashMap 相同，但是它重新定义了数组中保存的
元素 Entry，该 Entry 除了保存当前对象的引用外，还保存了其上一个元素 before 和下一个
元素 after 的引用，从而在哈希表的基础上又构成了双向链接列表。看源代码：
Java 代码

/** 
   * 双向链表的表头元素。 
   */  
  private transient Entry<K,V> header;  
  /** 
   * LinkedHashMap 的 Entry 元素。 
   * 继承 HashMap 的 Entry 元素，又保存了其上一个元素 before 和下一个元素 after 的引用。  
   */  
  private static class Entry<K,V> extends HashMap.Entry<K,V> {  
      Entry<K,V> before, after;  
      ……  
}
2)  初始化：
通过源代码可以看出，在 LinkedHashMap 的构造方法中，实际调用了父类 HashMap
的相关构造方法来构造一个底层存放的 table 数组。如：
Java 代码

public LinkedHashMap(int initialCapacity, float loadFactor) {  
      super(initialCapacity, loadFactor);  
      accessOrder = false;  
  }
HashMap 中的相关构造方法：
Java 代码

public HashMap(int initialCapacity, float loadFactor) {  
      if (initialCapacity < 0)  
          throw new IllegalArgumentException("Illegal initial capacity: " +   initialCapacity);  
      if (initialCapacity > MAXIMUM_CAPACITY)  
          initialCapacity = MAXIMUM_CAPACITY;  
      if (loadFactor <= 0 || Float.isNaN(loadFactor))  
         throw new IllegalArgumentException("Illegal load factor: " +  loadFactor);  
      // Find a power of 2 >= initialCapacity  
      int capacity = 1;  
      while (capacity < initialCapacity)  
          capacity <<= 1;  
      this.loadFactor = loadFactor;  
      threshold = (int)(capacity * loadFactor);  
      table = new Entry[capacity];  
      init();  
  }
我们已经知道 LinkedHashMap 的 Entry 元素继承 HashMap 的 Entry，提供了双向链表的功能。在上述 HashMap 的构造器中，最后会调用 init()方法，进行相关的初始化，这个方法在HashMap 的实现中并无意义，只是提供给子类实现相关的初始化调用。

LinkedHashMap 重写了 init()方法，在调用父类的构造方法完成构造后，进一步实现了对其元素 Entry 的初始化操作。
Java 代码

void init() {  

      header = new Entry<K,V>(-1, null, null, null);  

      header.before = header.after = header;  

  }
3)  存储：
LinkedHashMap 并未重写父类 HashMap 的 put 方法，而是重写了父类 HashMap 的put 方法调用的子方法 void addEntry(int hash, K key, V value, int bucketIndex)  和 void createEntry(int hash, K key, V value, int bucketIndex)，提供了自己特有的双向链接列表的实现。
Java 代码

void addEntry(int hash, K key, V value, int bucketIndex) {  

      // 调用 create 方法，将新元素以双向链表的的形式加入到映射中。  

      createEntry(hash, key, value, bucketIndex);  

      // 删除最近最少使用元素的策略定义  

      Entry<K,V> eldest = header.after;  

      if (removeEldestEntry(eldest)) {  

          removeEntryForKey(eldest.key);  

      } else {  

          if (size >= threshold)  

              resize(2 * table.length);  

      }  

  }
Java 代码 

  void createEntry(int hash, K key, V value, int bucketIndex) {  

      HashMap.Entry<K,V> old = table[bucketIndex];  

      Entry<K,V> e = new Entry<K,V>(hash, key, value, old);  

      table[bucketIndex] = e;  

      // 调用元素的 addBrefore 方法，将元素加入到哈希、双向链接列表。  

      e.addBefore(header);  

      size++;  

  }
Java 代码 

  private void addBefore(Entry<K,V> existingEntry) {  
      after  = existingEntry;  
      before = existingEntry.before;  
      before.after = this;  
      after.before = this;  
  }
4)  读取：
LinkedHashMap 重写了父类 HashMap 的 get 方法，实际在调用父类 getEntry()方法取
得查找的元素后，再判断当排序模式 accessOrder 为 true 时，记录访问顺序，将最新访问
的元素添加到双向链表的表头，并从原来的位置删除。由于的链表的增加、删除操作是常量
级的，故并不会带来性能的损失。
Java 代码 

 public V get(Object key) {  

     // 调用父类 HashMap 的 getEntry()方法，取得要查找的元素。  

     Entry<K,V> e = (Entry<K,V>)getEntry(key);  

     if (e == null)  

          return null;  

      // 记录访问顺序。  

      e.recordAccess(this);  

      return e.value;  

  }
Java 代码 

void recordAccess(HashMap<K,V> m) {  

     LinkedHashMap<K,V> lm = (LinkedHashMap<K,V>)m;  

      // 如果定义了 LinkedHashMap 的迭代顺序为访问顺序，  

      // 则删除以前位置上的元素，并将最新访问的元素添加到链表表头。  

      if (lm.accessOrder) {  

          lm.modCount++;  

          remove();  

          addBefore(lm.header);  

      }  

  }
5)  排序模式：
LinkedHashMap 定义了排序模式 accessOrder，该属性为 boolean 型变量，对于访问
顺序，为 true；对于插入顺序，则为 false。
Java 代码 

private final boolean accessOrder;
一般情况下，不必指定排序模式，其迭代顺序即为默认为插入顺序。看 LinkedHashMap的构造方法，如：
Java 代码 

public LinkedHashMap(int initialCapacity, float loadFactor) {  

     super(initialCapacity, loadFactor);  

      accessOrder = false;  

  }
这些构造方法都会默认指定排序模式为插入顺序。如果你想构造一个 LinkedHashMap，并打算按从近期访问最少到近期访问最多的顺序（即访问顺序）来保存元素，那么请使用下面的构造方法构造 LinkedHashMap：
Java 代码 

 public LinkedHashMap(int initialCapacity,   float loadFactor,  boolean accessOrder) {  

     super(initialCapacity, loadFactor);  

      this.accessOrder = accessOrder;  

 }
该哈希映射的迭代顺序就是最后访问其条目的顺序，这种映射很适合构建 LRU 缓存。LinkedHashMap 提供了 removeEldestEntry(Map.Entry<K,V> eldest)方法，在将新条目插入到映射后， put 和  putAll 将调用此方法。该方法可以提供在每次添加新条目时移除最旧条目的实现程序，默认返回 false，这样，此映射的行为将类似于正常映射，即永远不能移除最旧的元素。
Java 代码 

  protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {  

      return false;  

 }
此方法通常不以任何方式修改映射，相反允许映射在其返回值的指引下进行自我修改。如果用此映射构建 LRU 缓存，则非常方便，它允许映射通过删除旧条目来减少内存损耗。
例如：重写此方法，维持此映射只保存 100 个条目的稳定状态，在每次添加新条目时删除最旧的条目。
Java 代码 

 private static final int MAX_ENTRIES = 100;  

 protected boolean removeEldestEntry(Map.Entry eldest) {  

     return size() > MAX_ENTRIES;  

}

        
        