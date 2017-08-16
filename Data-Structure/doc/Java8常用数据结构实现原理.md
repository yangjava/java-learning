ArrayList

extends AbstractList<E>
implements List<E>, RandomAccess, Cloneable, java.io.Serializable
(1)RandomAccess实现了快速访问,遍历ArrayList时 使用for()比使用iterato()速度更快（用时近似常量）

(2)

 transient Object[] elementData; // non-private to simplify nested class access
底层使用数组结构

(3)不支持并发操作,modCount(集合改变次数，继承自AbstractList用于iterator时校验并发操作)不保证并发操作时 一定起作用

(4)元素无序可重复

LinkedList

extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
(1)底层使用链表数据结构

private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
(2)元素无序可重复

(3)实现了Deque(双端队列)接口，对链表头尾进行操作

(4)不支持并发操作

HashMap<K,V>

 extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable
(1)数据结构设计 transient Node<K,V>[] table;

K,V都可以为null

(2)典型算法

/**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Implements Map.put and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //1.如果table未初始化，初始化
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        //2.如果散列到数组下标没有Node，创建新Node
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
        //3.如果散列到的数组下标已有Node
            Node<K,V> e; K k;
       //4.以下几个条件判断是寻找是否存在相等的key，如果找到则直接替换值。
        //第一个Node的Key相等
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
        //调用树遍历查找key
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                //for循环用于更新binCount修改次数，不限条件，一直查找到最后一个节点
                for (int binCount = 0; ; ++binCount) {    
                    //没有找到相等key，新建一个Node,追加到最后一个Node后面
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        //到临界值 Node由链表转换为树结构
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                   //找到相等key的Node，返回该Node
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
   // 如果找到key相等的Node,替换新值，返回老的值
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        //调用模板Hook方法 用于子类重写
        afterNodeInsertion(evict);
        return null;
    }

public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
/**
     * Implements Map.get and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            //bucket位置算法：tab[(n - 1) & hash]，其中table的长度必须是2的幂值，保证散列值均匀分布到table的                //每个下标，数组大小默认值16 ConcurrentHashMap锁分段分为16个
            (first =** tab[(n - 1) & hash]) **!= null) {
            if (first.hash == hash && // always check first node（验证第一个Node）
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                //树结构判断
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                //Node循环判断
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
(3)元素无序

(4)不支持并发操作

(5)当table每个下标多于8个Node时 使用红黑树代替链表结构（添加 删除元素时间复杂度是O(log(n)),而链表是O(n)）

LinkedHashMap

 extends HashMap<K,V>
    implements Map<K,V>
(1)默认new LinkedHashMap() 构造一个 insert-ordering的Map，iterator时按照insert顺序,可通过accessOrder构造参数定义按照access-ordering还是insert-ordering

(2)LinkedHashMap(int,float,boolean)构造方法用于创建一个迭代顺序为LRU的实例（从最少访问到最多访问顺序）

(3)非同步，需要用将该Map封装到对象中保证并发操作的正确性，或用Collections.synchronized()包裹, Map m = Collections.synchronizedMap(new LinkedHashMap(...));

(4)``` <i>the fail-fast behavior of iterators

should be used only to detect bugs.</i> ```
(5)如果Hash算法正确地散列（分布均匀），

add
remove
contains
的时间复杂度是O(1)

(6)``` if the map is structurally modified at any time after

the iterator is created, in any way except through the iterator's own
<tt>remove</tt> method, the iterator will throw a {@link
ConcurrentModificationException}. ``` =>遍历map 过程中，除非使用iterator本身的remove(),否则任何其他修改都会抛出ConcurrentModificationException。
(7) ``` <i>the fail-fast behavior of iterators

should be used only to detect bugs.</i> ``` fail-fast不能保证非同步的并发修改的正确行为，只能用来发现bug。
(8)LinkedListMap所有的Map操作都在HashMap父类中完成 ，本类只是重新调整Node元素的顺序

HashTable

(1) K,V都不可以为null

(2)遍历map 过程中，除非使用iterator本身的remove(),否则任何其他修改都会抛出ConcurrentModificationException。

(3)高并发场景下要保证线程安全，推荐使用ConcurrentHashMap<K,V>,而不是HashTable

(4) fail-fast不能保证非同步的并发修改的正确行为，只能用来发现bug。

(5)无参构造器 默认capacity是11，loadFactor是0.75f

 /**
     * Constructs a new, empty hashtable with a default initial capacity (11)
     * and load factor (0.75).
     */
    public Hashtable() {
        this(11, 0.75f);
    }
(6)HashTable中size()、isEmpty()、contains(),containsKey(Object key),get(Object key),remove(Object key),toString() 等方法都使用了Synchronized方法锁。

   /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key.equals(k))},
     * then this method returns {@code v}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     * @see     #put(Object, Object)
     */
    @SuppressWarnings("unchecked")
    public synchronized V get(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
       ** int index = (hash & 0x7FFFFFFF) % tab.length;**
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                return (V)e.value;
            }
        }
        return null;
    }
ConcurrentHashMap



(1)与java.util.Hashtable中方法一一对应

(2)

K,V都不可以为null

(3)However, iterators are designed to be used by only one thread at a time. Bear in mind that the results of aggregate status methods including size, isEmpty, and containsValue are typically useful only when a map is not undergoing concurrent updates in other threads. Otherwise the results of these methods reflect transient states that may be adequate for monitoring or estimation purposes, but not for program control.

ConcurrentHashMap的迭代器设计用于单个线程使用。size(),isEmpty(),containsValue()仅在没有其他线程并发地更新时，才有效(准确)。否则这些方法适用于监控目的，而不是程序控制。

(4)put()方法实现

 /**
     * Maps the specified key to the specified value in this table.
     * Neither the key nor the value can be null.
     *
     * <p>The value can be retrieved by calling the {@code get} method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key or value is null
     */
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;

**//此处使用内置锁锁住table中按照散列算法 得到的Node  如果没设置capacity大小 默认是table16个元素其中一个**

                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
TreeMap

(1)使用红黑树实现,并实现了SortedMap

(2)节点在树中的分布默认根据key自然排序，否则根据传入的排序算法分布

(3)containsKey, get , put ,remove 都是log(n)的时间复杂度

(4)非同步Map，转换方式 SortedMap m = Collections.synchronizedSortedMap(new TreeMap(...));

(5)遍历map 过程中，除非使用iterator本身的remove(),否则任何其他修改都会抛出ConcurrentModificationException。

fail-fast不能保证非同步的并发修改的正确行为**，只能用来发现bug。

(6)put方法实现 使用红黑树实现的Entry


/**
     * Node in the Tree.  Doubles as a means to pass key-value pairs back to
     * user (see Map.Entry).
     */

    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
//左节点
        Entry<K,V> left;
//右节点
        Entry<K,V> right;
//父节点
        Entry<K,V> parent;
//颜色
        boolean color = BLACK;

        /**
         * Make a new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null) {
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
    //构造函数传入比较算法
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
    //如果未自定义比较算法 使用key的比较算法
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
HashSet

(1)底层基于HashMap实现 元素是无序的 不重复的 且可以为null

(2)假定均匀散列 add remove size contains方法的时间复杂度是 常量 性能和底层数组大小有关 所以考虑性能的话,底层 HashMap的table的初始capacity不能太大

(3)非同步实现，需要手动实现同步 Set s = Collections.synchronizedSet(new HashSet(...));

(4)遍历set 过程中，除非使用iterator本身的remove(),否则任何其他修改都会抛出ConcurrentModificationException。

fail-fast不能保证非同步的并发修改的正确行为，只能用来发现bug。

(5)底层实现 add方法如果HashMap的k不存在 返回V ==null 如果已存在 则返回之前的V值 ！=null

   // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

  /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element <tt>e</tt> to this set if
     * this set contains no element <tt>e2</tt> such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>.
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }

    /**
     * Removes the specified element from this set if it is present.
     * More formally, removes an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
     * if this set contains such an element.  Returns <tt>true</tt> if
     * this set contained the element (or equivalently, if this set
     * changed as a result of the call).  (This set will not contain the
     * element once the call returns.)
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }
TreeSet

(1)实现了SortedSet<E> 节点在树中的分布默认根据key自然排序，否则根据传入的排序算法分布

(2)底层基于TreeMap实现 containsKey, get , put ,remove 时间复杂度是log(n)

(3)非同步实现 可以使用 SortedSet s = Collections.synchronizedSortedSet(new TreeSet(...));同步

(4)遍历set 过程中，除非使用iterator本身的remove(),否则任何其他修改都会抛出ConcurrentModificationException。

fail-fast不能保证非同步的并发修改的正确行为，只能用来发现bug。

(5)默认无参构造器实现

/**
     * Constructs a new, empty tree set, sorted according to the
     * natural ordering of its elements.  All elements inserted into
     * the set must implement the {@link Comparable} interface.
     * Furthermore, all such elements must be <i>mutually
     * comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.  If the user attempts to add an element
     * to the set that violates this constraint (for example, the user
     * attempts to add a string element to a set whose elements are
     * integers), the {@code add} call will throw a
     * {@code ClassCastException}.
     */
    public TreeSet() {
        this(new TreeMap<E,Object>());
    }
 /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * the set contains no element {@code e2} such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param e element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     */
    public boolean add(E e) {
        return m.put(e, PRESENT)==null;
    }

    /**
     * Removes the specified element from this set if it is present.
     * More formally, removes an element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
     * if this set contains such an element.  Returns {@code true} if
     * this set contained the element (or equivalently, if this set
     * changed as a result of the call).  (This set will not contain the
     * element once the call returns.)
     *
     * @param o object to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     */
    public boolean remove(Object o) {
        return m.remove(o)==PRESENT;
    }
以上分析可知,所有的Set实现底层都基于Map