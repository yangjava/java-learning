ArrayList和Vector，他们都是顺序表，使用数组实现，现在我们看一下LinkedList，LinkedList是一个链表，并且是一个双向链表，下面我们看一下链表的实现图解。

链表使用节点存储元素，单向链表节点保存元素，并且指向下一个节点。双向链表保存元素的同时，指向前一个节点和后一个节点，第一个元素指向的第一个元素为null，最后一个元素指向最后一个的一个节点为null.



下面我们可以看一下LinkedList是怎么实现双向链表的。LinkedList声明了三个实例变量 size用于保存元素的个数，而first和last分别表示第一个节点和最后一个节点。初始化的时候first节点和last节点都为null，size=0

    //用于保存元素的个数
    transient int size = 0;

    /**
     * 指向第一个元素，表示链表的第一个元素
     * (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * 指向最后一个元素，表示链表的最后一个元素
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;
现在我我们再看一下Node，Node保存节点信息，保存要添加的元素，同时指向上一个节点和下一个节点。

   private static class Node<E> {
       //存储元素
        E item;
        //指向下一个节点，最后一个元素，next为null
        Node<E> next;
        //指向上一个节点，第一个元素，prev为null
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
下面我们可以分析一下从添加一个元素时开始，查看LinkedList是怎么存储的，我们先看源码。我们以添加两个元素，进行实例图解。

//添加元素
 public boolean add(E e) {
        调用linkedLast(E e)添加元素
        linkLast(e);
        return true;
    }

//将元素添加为最后一个元素,在LinkedList最后面添加一个元素
void linkLast(E e) {
       //定义局部变量等于最后一个节点
        final Node<E> l = last;
       //创建新节点
       //Node(Node<E> prev, E element, Node<E> next) {
       //     this.item = element;
       ////     this.next = next;
            this.prev = prev;
       // }
       //并且让创建的节点指向最后一个节点
        final Node<E> newNode = new Node<>(l, e, null);
       //将最后一个节点更改为新创建的节点
        last = newNode;
        if (l == null)
            first = newNode;
        else
            //让原来的最后一个节点的next信息指向新节点。原来最后一个节点不再是最后一个节点。
            l.next = newNode;
        //元素个数加1
        size++;
        modCount++;
    }


然后我们看一下一个重要的方法。获取指定索引的节点。返回值非空。

 //为了提高查找效率，遍历一半节点，而不是遍历所有节点
Node<E> node(int index) {
       //如果索引小于size的二分之一，则从第一个元素开始查找节点，
        if (index < (size >> 1)) {
            Node<E> x = first;
           //从第一个元素开始，遍历到index-1个节点
            for (int i = 0; i < index; i++)
               //通过index-1的next指向，获取index处节点
                x = x.next;
            return x;
        } else {
           //否则正好相反，从最后一个节点查找节点，获取index+1索引处节点，然后获取其prev节点，即为索引处节点
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
获取到指定索引的节点了，我们就可以在指定索引处添加元素了。我们先看下源码，然后看图解

//在指定索引添加元素
public void add(int index, E element) {
        //检查索引
        checkPositionIndex(index);
        //如果指定索引与size相等，直接在末尾添加节点即可
        if (index == size)
            linkLast(element);
        else
            //否则找到索引处节点，然后在节点前添加新节点
            linkBefore(element, node(index));
    }
//检查索引
private void checkPositionIndex(int index) {
       //如果索引不在size范围内
       //调用isPositionIndex(int index)方法
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
//是正确的索引大于等0小于等于size
private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
//在指定节点前，添加元素
void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        //获取指定节点的上一个节点
        final Node<E> pred = succ.prev;
        //创建新节点，next节点指向succ prev节点指向pred
        final Node<E> newNode = new Node<>(pred, e, succ);
        //指定节点的prev指向新节点
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            //pre的next节点指向新节点
            pred.next = newNode;
        //大小增加
        size++;
        modCount++;
    }


在对LinkedList进行操作时，都是对节点进行操作，改变节点的指向。节点所有的操作几乎都是调用下面的方法，前面已经介绍了linkLast和linkBefor方法，下面还有几种方法，注释一下，只要掌握了节点的指向，就很好理解了。

//与linkLast相反，在第一个节点前面添加节点，
private void linkFirst(E e) {
        //第一个节点
        final Node<E> f = first;
        //创建新节点，新节点指向的前一个节点为null,后一个节点为原来第一个节点
        final Node<E> newNode = new Node<>(null, e, f);
        //让第一个节点为新节点
        first = newNode;
        if (f == null)
            last = newNode;
        else
            让原来第一个节点指向的前面的节点为新节点
            f.prev = newNode;
        //大小增加
        size++;
        modCount++;
    }

//与linkFirst相反，总是删除第一个节点，元素，在poll()方法和removeFirst()调用内被调用
//总的设计就是让第一个节点的指向为null,第一个节点存储的元素也为空
//让第一个节点，赋值为第一个节点，第二个节点指向的前面的节点为null
private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
//poll方法，删除第一个元素
 public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
//删除第一个元素
public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    
     //总是删除最后一个元素,与unlinkFirst相似
     
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

//删除最后一个元素
public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }
//删除最后一个元素
public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

//删除任意节点
E unlink(Node<E> x) {
        //获取节点的元素
        final E element = x.item;
        //获取节点的下一个节点
        final Node<E> next = x.next;
        //获取节点的上一个节点
        final Node<E> prev = x.prev;
        //如果prev为null，即要删除的节点为头结点
        if (prev == null) {
            //将下一个节点变为头结点
            first = next;
        } else {
            //否则让上一节点的下一节点指向next节点
            prev.next = next;
            //节点指向的上一个节点为null
            x.prev = null;
        }
        //如果是最后一个元素
        if (next == null) {
            //让最后一个节点为前一节点
            last = prev;
        } else {
           //否则，让下一节点指向的前一个节点为prev
            next.prev = prev;
           //指向的下一个节点为Null
            x.next = null;
        }
        //元素为null
        x.item = null;
        //改变大小
        size--;
        modCount++;
        return element;
    }
    
    
    
  
  
  
基本概念

在探究 LinkedList 之前首先要明白一个词：链表。

概念：链表是一种物理存储单元上非连续、非顺序的存储结构，数据元素的逻辑顺序是通过链表中的指针链接次序实现的。

结构：由一系列节点组成，节点包括值域和指针域两个部分，其中值域用来存储数据元素的值，指针域用来存储上（下）一个节点的地址。

特点：链表查找和修改相应的时间复杂度分别是 O(n)、O(1) 。

类型：链表有单链表和双链表之分，单链表只有一个指针域，指向该节点的下一个节点位置；双链表则有两个指针域，分别执向它的前节点、后节点。

原理

在这里主要探究的是 LinkedList 的内部构造，以及它常用的增删改查方法。

以下源码来自 JDK 1.7

1.节点元素

首先来看 LinkedList 中一个重要的静态内部类：

private static class Node<E> {
    E item; // 值域
    Node<E> next; // 后指针
    Node<E> prev; // 前指针

    Node(Node<E> prev, E element, Node<E> next) {
        this.prev = prev;
        this.item = element;
        this.next = next;
    }
}
Node，也称节点。通过代码可以发现构建一个节点需要三种元素：值域、前指针、后指针，缺一不可。其中：

值域：用来存放元素的值。
前后指针：指针可以指向别的节点，具体作用稍后再讲。
根据描述可以绘制 Node 的结构如下：

输入图片说明

2.内部结构

介绍完节点的概念，再来看看 LinkedList 的内部构造。

// 元素个数
transient int size = 0;

// 尾节点
transient Node<E> last;

// 头结点
transient Node<E> first;

// 构造函数
public LinkedList() { }
LinkedList 的三个成员变量，分别代表元素个数、尾节点、头节点，并且它们都无法被序列化。因此可以猜测其内部构造是一个链表，由多个节点链接而成。

观察它的构造函数，是一个默认构造函数。说明 LinkedList 默认创建出来后是一个空的集合，与 ArrayList 不同，它不需要指定容量，因为它的容量是可变的。

空的集合我们还看不出它具体的内部结构，因此需往集合中添加一个元素后，再作分析。

// 添加指定元素
public boolean add(E e) {
    linkLast(e);
    return true;
}


// 链接（添加）到末尾位置
void linkLast(E e) {

    final Node<E> l = last;

    // 关键-> 创建一个值域为 e 的节点，其前指针指向 l （即原来的尾节点）
    final Node<E> newNode = new Node<>(l, e, null);

    // 将新创建的节点设置为尾节点
    last = newNode;

    // 第一次往 LinkedList 中添加元素时 last 为空节点
    if (l == null){
        // 关键 ->将新创建的节点设置为首节点，说明只有一个节点时，该节点既是首节点又是尾节点
        first = newNode;
    }else{
        // 修改 l 的后指针
        l.next = newNode;
    }

    size++;
    modCount++;
}
根据代码，可以绘制 LinkedList 创建过程如下：

输入图片说明

3.添加操作

这里要分析的 LinkedList 常用的添加操作有：不指定位置（添加到链表末尾）、指定位置（添加到链表指定位置）。

前者在上面介绍内部结构时已经分析过原理，这里不在阐述。
后者与前者相比，多了查询指定位置元素的步骤。
下面来看看在指定位置添加元素的过程：

public void add(int index, E element) {
    // 校验位置是否合法
    checkPositionIndex(index);

    // 判断指定位置是否为链表末尾
    if (index == size){
        linkLast(element);
    }else{
        // 关键-> 先找到该位置的元素，再添加到该元素前面。
        linkBefore(element, node(index));
    }
}

// 校验位置是否合法
private void checkPositionIndex(int index) {
    if (!isPositionIndex(index)){
        // 抛出异常...
    }
}
private boolean isPositionIndex(int index) {
    return index >= 0 && index <= size;
}

// 找到指定位置节点
Node<E> node(int index) {
    // 判断指定位置在链表的前半部分还是后半部分
    if (index < (size >> 1)) {
        // 从头节点开始遍历
        Node<E> x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    } else {
        // 从尾节点开始遍历
        Node<E> x = last;
        for (int i = size - 1; i > index; i--) {
            x = x.prev;
        }
        return x;
    }
}

// 关键-> 这里 e 表示要添加的元素，succ 表示指定位置上的元素
void linkBefore(E e, Node<E> succ) {

    final Node<E> pred = succ.prev;    

    // ①构建新节点，节点值为 e，并指定前后节点分别为 succ 的前节点、succ
    final Node<E> newNode = new Node<>(pred, e, succ);

    // ②修改前后节点的指针
    succ.prev = newNode;
    if (pred == null){
        first = newNode;
    }else{
        pred.next = newNode;
    }
    size++;
    modCount++;
}
分析代码，在 LinkedList 的指定位置添加元素大概需要以下几个步骤：

检验指定位置是否合法
根据指定位置选择遍历方向，从头节点或从尾节点
找到为指定位置的节点
构建新节点并插入链表（关键）
整个过程最为关键的步骤在最后一步，这里绘制出整个插入过程如下（①② 对应 linkBefore 方法的注释）：

输入图片说明

4.修改操作

LinkedList 的修改操作与添加操作类似，这里不再探究，只贴出源码：

public E set(int index, E element) {
    checkElementIndex(index);
    Node<E> x = node(index);
    E oldVal = x.item;
    x.item = element;
    return oldVal;
}
整个过程如下：

检验指定位置是否合法
找到指定位置的节点
替换节点的值域（元素）
5.删除操作

这里要分析 LinkedList 常用的删除操作有三种，分别是：不指定元素的删除操作、删除指定位置的元素、删除指定元素。

不指定元素的删除操作，默认是移除链表的首节点。源码如下：
public E remove() {
    return removeFirst();
}

public E removeFirst() {
    final Node<E> f = first;
    if (f == null){
        throw new NoSuchElementException();
    }
    return unlinkFirst(f);
}

private E unlinkFirst(Node<E> f) {

    final E element = f.item;
    final Node<E> next = f.next;

    /// 将节点的值域置空，让 GC 回收
    f.item = null;

    // 将节点的后指针置空
    f.next = null; 

    // 设置新的头节点
    first = next;

    // next 为空， 表示只有一个节点
    if (next == null){
        // 关键-> 链表只有一个节点时，该节点（f）既是头节点，也是尾节点
        // 这里要删除 f，所以要把 last 置空
        last = null;
    }else{
        // 修改后节点的前指针
        next.prev = null;
    }

    size--;
    modCount++;
    return element;
}
移除指定元素
public E remove(int index) {
    checkElementIndex(index);

    // 关键 -> 从链表中移除
    return unlink(node(index));
}
移除指定位置的元素，与前者相比多了查找指定位置元素的步骤：
public boolean remove(Object o) {
    // 判断指定元素是否为空
    if (o == null) {
        // 遍历链表找到指定元素
        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item == null) {
                // 关键 -> 从链表中移除
                unlink(x);
                return true;
            }
        }
    } else {
        for (Node<E> x = first; x != null; x = x.next) {
            if (o.equals(x.item)) {
                unlink(x);
                return true;
            }
        }
    }
    return false;
}
在上面的代码中，第二、第三种删除操作的真正执行过程都发生下 unlink 方法，其作用是将指定元素从链表中移除。下面来看它的源码：

E unlink(Node<E> x) {

    final E element = x.item;
    final Node<E> next = x.next;
    final Node<E> prev = x.prev;

    // prev 为空，说明整个链表只有该节点一个节点
    if (prev == null) {
        first = next;
    } else {
        // ①与前节点断开链接
        prev.next = next;
        x.prev = null;
    }

    // next 为空，说明该节点是链接的尾节点
    if (next == null) {
        last = prev;
    } else {
        // ②与后节点断开链接
        next.prev = prev;
        x.next = null;
    }

    // ③将节点的值域置空，让 GC 回收
    x.item = null;

    size--;
    modCount++;
    return element;
}
不考虑前后节点为空的情况下， 可以绘制其操作过程如下：

Alt text

总的来讲，在 LinkedList 中删除操作的步骤如下：

校验指定位置是否合法，并找到要操作的元素
断开与前节点的链接，包括本节点的前指针、前节点的后指针
断开与后节点的链接，包括本节点的后指针、后节点的前指针
将节点的值域置空让 GC 生效
值得注意的是，若操作的元素为头节点或尾节点时，需要设置新的头/尾节点

6.遍历操作

一般的来说集合的遍历操作是通过迭代器（Iterator）来完成的，具体调用如下：

List<String> list = new LinkedList<String>();
Iterator<String> iter = list.iterator();
while(iter.hasNext()){
    System.out.println(iter.next());
}
首先来看 LinkedList 的继承关系，从上到下依次是：list -> AbstractList -> AbstractSequentialList-> LinkedList。如下图所示：

输入图片说明

再来看看 itertor 方法的调用过程：

// 在 List 中定义如下：
 Iterator<E> iterator();

// 在 AbstractSequentialList 中定义如下：
public Iterator<E> iterator() {
    // 该方法继承自 AbstractList 类
    return listIterator();
}

// 在 AbstractList 中定义如下:
public ListIterator<E> listIterator() {
    return listIterator(0);
}

// 在 LinkedList 中定义如下：
public ListIterator<E> listIterator(int index) {
    checkPositionIndex(index);
    return new ListItr(index);
}
最后来探究下遍历操作的实现过程：

// 该类是 LinkedList 的内部类
private class ListItr implements ListIterator<E> {
    private Node<E> lastReturned = null;
    private Node<E> next;
    private int nextIndex;
    private int expectedModCount = modCount;

    // 构造函数
    ListItr(int index) {
        // nextIndex 表示开始遍历操作的位置
        // next 表示该位置的上的节点
        next = (index == size) ? null : node(index);
        nextIndex = index;
    }

    public boolean hasNext() {
        return nextIndex < size;
    }

    public E next() {
        checkForComodification();
        if (!hasNext()){
            // 抛出异常...
        }
        lastReturned = next;
        next = next.next;
        nextIndex++;
        return lastReturned.item;
    }
    final void checkForComodification() {
        if (modCount != expectedModCount){
            // 抛出异常...
        }    
    }

    //省略其他代码...
}
总结

LinkedList 内部由非循环的双向链表 构成，而链表的特点是：便于修改，不便于查询。

LinkedList 与 ArrayList 不同的是，ArrayList 内容由数组（即线性表）构成， 数组的特点是：便于查询，不便于修改。

正是由于它们的内部构造不同导致它们有了不同的特性，因此要根据不同的场景选择不同的 List。

若是频繁修改的，则选择 LinkedList 的效率更好；若是频繁查询的，则选择 ArrayList 的执行效率更好。



一、节点分析

LinkedList内部是通过链表来实现的，那么就少不了节点，所以在源码中必然能找到这样一个节点。

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
节点中定义了三个成员变量：E item（节点的存储内容）、Node<E> next（记录下一个节点的指针）、Node<E> prev（记录后一个节点的指针），其构造方法我觉得很巧妙，该构造函数的三个参数中就包含了它的前一个节点，节点保存的内容，和它的后一个节点，只要通过这个构造函数new出的新节点就自动实现了节点间的链接，在后面的增删改查操作中我们会发现，通过这个构造方法我们可以省去很多Node<E> next和Node<E> prev指针指来指去的操作。

二、LinkedList的核心操作方法

在LinkedList中有可以看到这样两个成员变量Node<E> first和Node<E> last

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;
这个两个成员变量很关键，主要用来记录链表的头和尾，这样方便我们在CRUD操作的过程中来查找到相应位置的节点。通过分析源码可以知道LinkedList其实是用的是双向链表来实现的。
双向链表 在分析一个数据结构的时候，从相关add方法分析走能很好的理清数据结构的脉络。

linkFirst方法的分析
可以看到在addFirst的方法中其实是调用的linkFirst方法。

  /**
   * Inserts the specified element at the beginning of this list.
   *
   * @param e the element to add
   */
  public void addFirst(E e) {
      linkFirst(e);
  }
接下来看看linkFirst方法是如何实现节点间操作的：

  /**
   * Links e as first element.
   */
  private void linkFirst(E e) {
      final Node<E> f = first;
      final Node<E> newNode = new Node<>(null, e, f);
      first = newNode;
      if (f == null)
          last = newNode;
      else
          f.prev = newNode;
      size++;
      modCount++;
  }
linkFirst顾名思义，就是将节点链接到第一个。该方法首先是拿到链表的first(第一个）节点，然后通过那个巧妙的节点构造函数构造出一个新节点，然后将记录链表头的first指向这个新的节点，如果之前那个记录链表头的first节点等于null，说明当前链表中还没有一个节点（空链表）,所以就将记录链表尾的last节点也指向这个新节点；如果之前那个记录链表头的first节点不为null，那么就将之前的第一个节点的prev指针指向新节点，在节点的构造函数中就完成了新节点的next指针指向之前的第一个节点，所以这样就形成了节点间的双向记录。

linkLast方法的分析
可以看到在addLast的方法中其实是调用的linkLast方法。

  /**
   * Appends the specified element to the end of this list.
   *
   * <p>This method is equivalent to {@link #add}.
   *
   * @param e the element to add
   */
  public void addLast(E e) {
      linkLast(e);
  }
再来看看linkLast方法是如何实现的：

  /**
   * Links e as last element.
   */
  void linkLast(E e) {
      final Node<E> l = last;
      final Node<E> newNode = new Node<>(l, e, null);
      last = newNode;
      if (l == null)
          first = newNode;
      else
          l.next = newNode;
      size++;
      modCount++;
  }
这个方法是不是和linkFirst方法很像，它首先是拿到记录链表的last节点，然后又通过那个巧妙的构造方法构造一个新的节点，最后同样是判断之前记录链表的last节点为不为null，如果为null说明链表依然是空的，所以就将记录链表头的first指向该新节点，如果不为null说明链表之前已经有节点了，此时只需要将之前的那个尾节点的next指针指向当前新节点即可，同样是构造方法帮助我们完成了新节点的prev指针指向前一个节点。所以我觉得那个节点的构造函数很巧妙。

linkBefore方法的分析
这个方法是比较重要也比较难理解的方法，先来看看这个函数的代码：

/**
   * Inserts element e before non-null Node succ.
   */
  void linkBefore(E e, Node<E> succ) {
      // assert succ != null;
      final Node<E> pred = succ.prev;
      final Node<E> newNode = new Node<>(pred, e, succ);
      succ.prev = newNode;
      if (pred == null)
          first = newNode;
      else
          pred.next = newNode;
      size++;
      modCount++;
  }
虽然代码和简洁，但却比较难理解，这个方法的两个参数分别表示：插入新节点的元素、需要在哪个节点前插入的节点。结合下面的这张图来分析： linkBefore
比如我现在想在Node3前面插入一个节点，那么当前的succ = Node3，所以这句代码final Node<E> pred = succ.prev;执行后pred = Node2，再通过那个巧妙的节点构造函数就将新节点链接上去了，如图： 新节点插入
这时候再通这句代码succ.prev = newNode;就Node3的prev指针指向了插入的新节点。后面的判读pred为不为null是为了知道是不是再第一个节点前插入新节点，如果是在第一个节点前插入新节点，那么就将记录链表头的first指针指向新节点，否则就pred的next指针指向插入的新节点，这样就完成了 新节点的插入操作。如图： 新节点插入

unlinkFirst方法的分析

  /**
   * Unlinks non-null first node f.
   */
  private E unlinkFirst(Node<E> f) {
      // assert f == first && f != null;
      final E element = f.item;
      final Node<E> next = f.next;
      f.item = null;
      f.next = null; // help GC
      first = next;
      if (next == null)
          last = null;
      else
          next.prev = null;
      size--;
      modCount++;
      return element;
  }
该方法是移除第一个节点，首先是通过传入的first指针拿到第一个节点的内容，然后拿到它的下一个节点，再将第一个节点的内容和指向下个节点的next指针置空，方便GC回收。下一步便是将记录头节点的first指向final Node<E> next = f.next;拿到的这个节点，如果这个的节点为空，那么last = null（说明链表在移除第一个节点前只有一个节点），否则就将拿到的这个节点中的prev指针置空，表示这个节点就是第一个节点。

unlinkLast方法的分析

  /**
   * Unlinks non-null last node l.
   */
  private E unlinkLast(Node<E> l) {
      // assert l == last && l != null;
      final E element = l.item;
      final Node<E> prev = l.prev;
      l.item = null;
      l.prev = null; // help GC
      last = prev;
      if (prev == null)
          first = null;
      else
          prev.next = null;
      size--;
      modCount++;
      return element;
  }
这个方法和unlinkFirst的实现基本差不多，此方法的作用是移除链表中的最后一个节点。只要清楚了unlinkFirst这个方法，那么unlinkLast也就清楚了。

unlink方法的分析

  /**
   * Unlinks non-null node x.
   */
  E unlink(Node<E> x) {
      // assert x != null;
      final E element = x.item;
      final Node<E> next = x.next;
      final Node<E> prev = x.prev;

      if (prev == null) {
          first = next;
      } else {
          prev.next = next;
          x.prev = null;
      }

      if (next == null) {
          last = prev;
      } else {
          next.prev = prev;
          x.next = null;
      }

      x.item = null;
      size--;
      modCount++;
      return element;
  }
此方法是移除链表中指定的节点，在移除这个节点前肯定需要拿到这个节点prev指针和next指针所记录的节点，并需要判断prev指针和next是否为空，prev指针为空表示这个节点就是第一个节点，next指针为空表示这个节点就是最后一个节点。关键代码便是通过判断将拿到的prev节点的next指针指向拿到的next节点，以及将拿到的next节点的prev指针指向拿到的prev节点。

三、LinkedList中的经典算法

在LinkedList中有一个根据索引查找相应节点的方法，此方法的源码如下：

    /**
     * Returns the (non-null) Node at the specified element index.
     */
    Node<E> node(int index) {
        // assert isElementIndex(index);

        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
在这个方法中可以看到用到了折半查找的算法，当传入一个索引后会判断index < (size >> 1)，如果index小于size的一半，则从前往后找节点；否则就从后往前找节点。

通过对LinkedList的分析后，对数据结构中的链表有了新的认识，在LinkedList中用的链表是双向链表，其实通过双向循环链表也可以来实现，如果是通过双向循环链表可以不需要last这个记录链表尾的变量了，只需要一个first变量记录链表的头，也可以实现从前往后和从后往前的查找等操作。    
    