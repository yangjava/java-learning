本文是针对Java 1.8的源代码进行解析的，可能会和其他版本有所出入。

一、继承和实现

继承：AbstractList

实现：List<E>, RandomAccess, Cloneable, Serializable接口

源代码

public class ArrayList<E> extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable{
}
二、全局变量

1. 默认容量

private static final int DEFAULT_CAPACITY = 10;
2. 空的对象数组

private static final Object[] EMPTY_ELEMENTDATA = {};
3.默认的空数组

// 无参构造函数创建的数组
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
4.存放数据的数组的缓存变量，不可序列化

transient Object[] elementData;
5.数组的大小

private int size;
三、构造方法

1.带有容量initialCapacity的构造方法

源码解释：

public ArrayList(int initialCapacity) {
     // 如果初始化时ArrayList大小大于0
    if (initialCapacity > 0) {
          // new一个该大小的object数组赋给elementData
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) { // 如果大小为0
          // 将空数组赋给elementData
        this.elementData = EMPTY_ELEMENTDATA;
    } else { // 小于0
          // 则抛出IllegalArgumentException异常
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    }
}
2.不带参数的构造方法

源码解释：

public ArrayList() {
     // 直接将空数组赋给elementData  
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}
3.带参数Collection的构造方法

源码解释：

参数c为一个Collection，Collection的实现类大概有以下几种常用类型：

List：元素可以重复的容器
Set: 元素不可重复的容器
Queue:结构是一个队列，先进先出
这个构造方法的意思是，将一个Collection实现类的对象转换为一个ArrayList，但是c容器装的内容

必须为ArrayList装的内容的子类。例如，将一个装了String内容的HashSet转换为装了String内容的

ArrayList，使得ArrayList的大小和值数组都是HashSet的大小和值数组。具体实现如下代码，首先调

用c(Collection的具体实现类)的toArray方法，具体大家可以看各个实现类的toArray方法，但是大

概意思都是将c容器转换为object类型的数组，因为它们的返回值都是object[]。之于下面的两个判断

是当得到的elementData的类名不是Object类名的时候或者是长度为0的时候才会执行。

public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        // replace with empty array.
        this.elementData = EMPTY_ELEMENTDATA;
    }
}  
四、方法

1.trimToSize()

说明：将ArrayList的容量设置为当前size的大小。首先需要明确一个概念，ArrayList的size就是ArrayList的元素个数，length是ArrayList申请的内容空间长度。ArrayList每次都会预申请多一点空间，以便添加元素的时候不需要每次都进行扩容操作，例如我们的元素个数是10个，它申请的内存空间必定会大于10，即length>size，而这个方法就是把ArrayList的内存空间设置为size，去除没有用到的null值空间。这也就是我们为什么每次在获取数据长度是都是调用list.size()而不是list.length()。

源码解释：首先modCount是从类 java.util.AbstractList 继承的字段，这个字段主要是为了防止在多线程操作的情况下，List发生结构性的变化，什么意思呢？就是防止一个线程正在迭代，另外一个线程进行对List进行remove操作，这样当我们迭代到最后一个元素时，很明显此时List的最后一个元素为空，那么这时modCount就会告诉迭代器，让其抛出异常 ConcurrentModificationException。

如果没有这一个变量，那么系统肯定会报异常ArrayIndexOutOfBoundsException，这样的异常显然不是应该出现的(这些运行时错误都是使用者的逻辑错误导致的，我们的JDK那么高端，不会出现使用错误，我们只抛出使用者造成的错误，而这个错误是设计者应该考虑的)，为了避免出现这样的异常，定义了检查。

(引用自：郭无心，详情可以看他在知乎的回答：https://www.zhihu.com/question/24086463/answer/64717159)。

public void trimToSize() {
    modCount++;
     // 如果size小于length
    if (size < elementData.length) {
         // 重新将elementData设置大小为size
        elementData = (size == 0)
          ? EMPTY_ELEMENTDATA
          : Arrays.copyOf(elementData, size);
    }
}  
2.size()

说明：返回ArrayList的大小

源码解释：直接返回size

public int size() {
    return size;
}
3.isEmpty()

说明：返回是否为空

源码解释： 直接返回判断size==0

public boolean isEmpty() {
    return size == 0;
}
4.indexOf(Object o)

说明：对象o在ArrayList中的下标位置，如果存在返回位置i，不存在返回-1

源码解释：遍历ArrayList的大小，比较o和容器内的元素，若相等，则返回位置i，若遍历完都不相等，返回-1

public int indexOf(Object o) {
    if (o == null) {
        for (int i = 0; i < size; i++)
            if (elementData[i]==null)
                return i;
    } else {
        for (int i = 0; i < size; i++)
            if (o.equals(elementData[i]))
                return i;
    }
    return -1;
}  
5.contains(Object o)

说明：是否包含对象o

源码解释：调用indexOf()方法得到下标，存在则下标>=0，不存在为-1，即只要比较下标和0的大小即可。

public boolean contains(Object o) {
    return indexOf(o) >= 0;
}
6.lastIndexOf(Object o)

说明：返回容器内出现o的最后一个位置

源码解释：从后向前遍历，得到第一个出现对象o的位置，不存在则返回-1

public int lastIndexOf(Object o) {
    if (o == null) {
        for (int i = size-1; i >= 0; i--)
            if (elementData[i]==null)
                return i;
    } else {
        for (int i = size-1; i >= 0; i--)
            if (o.equals(elementData[i]))
                return i;
    }
    return -1;
}  
7.clone()

说明：返回此 ArrayList 实例的浅表副本。

源码解释：

public Object clone() {
    try {
         // 调用父类(翻看源码可见是Object类)的clone方法得到一个ArrayList副本
        ArrayList<?> v = (ArrayList<?>) super.clone();
         // 调用Arrays类的copyOf，将ArrayList的elementData数组赋值给副本的elementData数组
        v.elementData = Arrays.copyOf(elementData, size);
        v.modCount = 0;
         // 返回副本v
        return v;
    } catch (CloneNotSupportedException e) {
        throw new InternalError(e);
    }
 }  
8.toArray()

说明：ArrayList 实例转换为。

源码解释：直接调用Arrays类的copyOf。

public Object[] toArray() {
    return Arrays.copyOf(elementData, size);
}  
9.toArray(T[] a)

说明：将ArrayList里面的元素赋值到一个数组中去

源码解释：如果a的长度小于ArrayList的长度，直接调用Arrays类的copyOf，返回一个比a数组长度要大的新数组，里面元素就是ArrayList里面的元素；如果a的长度比ArrayList的长度大，那么就调用System.arraycopy，将ArrayList的elementData数组赋值到a数组，然后把a数组的size位置赋值为空。 public <T> T[] toArray(T[] a) { if (a.length < size) // Make a new array of a's runtime type, but my contents: return (T[]) Arrays.copyOf(elementData, size, a.getClass()); System.arraycopy(elementData, 0, a, 0, size); if (a.length > size) a[size] = null; return a; }

10.rangeCheck(int index)

说明：测试index是否越界

源码解释：

private void rangeCheck(int index) {
     // 如果下标超过ArrayList的数组长度
    if (index >= size)
         // 抛出IndexOutOfBoundsException异常
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}  
11.get(int index)

说明：获取index位置的元素

源码解释：先检查是否越界，然后返回ArrayList的elementData数组index位置的元素。

public E get(int index) {
     // 检查是否越界
    rangeCheck(index);
     // 返回ArrayList的elementData数组index位置的元素
    return elementData(index);
}  
12.set(int index, E element)

说明：设置index位置的元素值了element，返回该位置的之前的值

源码解释：

public E set(int index, E element) {
     // 检查是否越界  
    rangeCheck(index);
     // 调用elementData(index)获取到当前位置的值
    E oldValue = elementData(index);
     // 将element赋值到ArrayList的elementData数组的第index位置
    elementData[index] = element;
    return oldValue;
}  
13.ensureCapacityInternal(int minCapacity)

说明：得到最小扩容量

源码解释：

private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
         // 获取默认的容量和传入参数的较大值
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}
14.ensureExplicitCapacity(int minCapacity)

说明：判断是否需要扩容

源码解释：

private void ensureExplicitCapacity(int minCapacity) {
    modCount++;
    // 如果最小需要空间比elementData的内存空间要大，则需要扩容
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
} 
15.grow()方法

说明：帮助ArrayList动态扩容的核心方法

源码解释：

// MAX_VALUE为231-1，MAX_ARRAY_SIZE 就是获取Java中int的最大限制，以防止越界  
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

private void grow(int minCapacity) {
    // 获取到ArrayList中elementData数组的内存空间长度
    int oldCapacity = elementData.length;
    // 扩容至原来的1.5倍
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    // 再判断一下新数组的容量够不够，够了就直接使用这个长度创建新数组， 
    // 不够就将数组长度设置为需要的长度
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    // 判断有没超过最大限制
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // 调用Arrays.copyOf方法将elementData数组指向新的内存空间时newCapacity的连续空间
    // 并将elementData的数据复制到新的内存空间
    elementData = Arrays.copyOf(elementData, newCapacity);
}  
16.add(E e)

说明：添加元素e

源码解释：

public boolean add(E e) {
     // 扩容
    ensureCapacityInternal(size + 1);  
    // 将e赋值给elementData的size+1的位置。
    elementData[size++] = e;
    return true;
}  
17.add(int index, E element)

说明：在ArrayList的index位置，添加元素element

源码解释:

public void add(int index, E element) {
    // 判断index是否越界  
    rangeCheckForAdd(index);
     // 扩容
    ensureCapacityInternal(size + 1);  
     // 将elementData从index位置开始，复制到elementData的index+1开始的连续空间
    System.arraycopy(elementData, index, elementData, index + 1,
                     size - index);
     // 在elementData的index位置赋值element
    elementData[index] = element;
     // ArrayList的大小加一  
    size++;
}  
18.remove(int index)

说明：在ArrayList的移除index位置的元素

源码解释：

public E remove(int index) {
     // 判断是否越界  
    rangeCheck(index);
    modCount++;
     // 读取旧值  
    E oldValue = elementData(index);
     // 获取index位置开始到最后一个位置的个数
    int numMoved = size - index - 1;
    if (numMoved > 0)
         // 将elementData数组index+1位置开始拷贝到elementData从index开始的空间
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
     // 使size-1 ，设置elementData的size位置为空，让GC来清理内存空间
    elementData[--size] = null; // clear to let GC do its work
    return oldValue;
}  
19.remove(Object o)

说明：在ArrayList的移除对象为O的元素，跟indexOf方法思想基本一致

源码解释：

public boolean remove(Object o) {
    if (o == null) {
        for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
    } else {
        for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
    }
    return false;
}  
20.clear()

说明：设置全部元素为null值，并设置size为0。

源码解释：可见clear操作并不是从空间内删除，只是设置为null值，等待垃圾回收机制来回收而已，把size设置为0，以便我们不会浏览到null值的内存空间。

public void clear() {
    modCount++;
    // clear to let GC do its work
    for (int i = 0; i < size; i++)
        elementData[i] = null;
    size = 0;
}  
21.addAll(Collection<? extends E> c)

说明：将Collection c的全部元素添加到ArrayList中

源码解释：

public boolean addAll(Collection<? extends E> c) {
     // 将c转换为数组a
    Object[] a = c.toArray();
     // 获取a占的内存空间长度赋值给numNew
    int numNew = a.length;
     // 扩容至size + numNew
    ensureCapacityInternal(size + numNew);  // Increments modCount
     // 将a的第0位开始拷贝至elementData的size位开始，拷贝长度为numNew
    System.arraycopy(a, 0, elementData, size, numNew);
     // 将size增加numNew  
    size += numNew;
     // 如果c为空，返回false，c不为空，返回true
    return numNew != 0;
}  
22.addAll(int index, Collection<? extends E> c)

说明：从第index位开始，将c全部拷贝到ArrayList

源码解释：

public boolean addAll(int index, Collection<? extends E> c) {
     // 判断index大于size或者是小于0,如果是，则抛出IndexOutOfBoundsException异常
    rangeCheckForAdd(index);
     // 将c转换为数组a
    Object[] a = c.toArray();
    int numNew = a.length;
     // 扩容至size + numNew
    ensureCapacityInternal(size + numNew);  // Increments modCount
      // 获取需要添加的个数
    int numMoved = size - index;
    if (numMoved > 0)
        System.arraycopy(elementData, index, elementData, index + numNew,
                         numMoved);
    System.arraycopy(a, 0, elementData, index, numNew);
    size += numNew;
    return numNew != 0;
}  
24.batchRemove(Collection<?> c, boolean complement)

说明：根据complement值，将ArrayList中包含c中元素的元素删除或者保留

源码解释：

private boolean batchRemove(Collection<?> c, boolean complement) {
    final Object[] elementData = this.elementData;
     // 定义一个w，一个r，两个同时右移   
    int r = 0, w = 0;
    boolean modified = false;
    try {
         // r先右移
        for (; r < size; r++)
              // 如果c中不包含elementData[r]这个元素
            if (c.contains(elementData[r]) == complement)
                  // 则直接将r位置的元素赋值给w位置的元素，w自增
                elementData[w++] = elementData[r];
    } finally {
        // 防止抛出异常导致上面r的右移过程没完成
        if (r != size) {
              // 将r未右移完成的位置的元素赋值给w右边位置的元素
            System.arraycopy(elementData, r,
                             elementData, w,
                             size - r);
              // 修改w值增加size-r
            w += size - r;
        }
        if (w != size) {
            // 如果有被覆盖掉的元素，则将w后面的元素都赋值为null
            for (int i = w; i < size; i++)
                elementData[i] = null;
            modCount += size - w;
              // 修改size为w
            size = w;
            modified = true;
        }
    }
    return modified;
}  
25.removeAll(Collection<?> c)

说明：ArrayList移除c中的所有元素

源码解释：

public boolean removeAll(Collection<?> c) {
     // 如果c为空，则抛出空指针异常
    Objects.requireNonNull(c);
     // 调用batchRemove移除c中的元素
    return batchRemove(c, false);
}  
26.retainAll(Collection<?> c)

说明：和removeAll相反，仅保留c中所有的元素

源码解释：

public boolean retainAll(Collection<?> c) {
    Objects.requireNonNull(c);
     // 调用batchRemove保留c中的元素
    return batchRemove(c, true);
}  
27.iterator()

说明：返回一个Iterator对象，Itr为ArrayList的一个内部类，其实现了Iterator<E>接口

public Iterator<E> iterator() {
    return new Itr();
}  
28.listIterator()

说明：返回一个ListIterator对象，ListItr为ArrayList的一个内部类，其实现了ListIterator<E> 接口

源码解释：

public ListIterator<E> listIterator() {
    return new ListItr(0);
}  
29.listIterator(int index)

说明：返回一个从index开始的ListIterator对象

源码解释：

public ListIterator<E> listIterator(int index) {
    if (index < 0 || index > size)
        throw new IndexOutOfBoundsException("Index: "+index);
    return new ListItr(index);
}  
30.subList(int fromIndex, int toIndex)

说明：根据两个参数，获取到一个子序列

源码解释：

public List<E> subList(int fromIndex, int toIndex) {
     // 检查异常
    subListRangeCheck(fromIndex, toIndex, size);
     // 调用SubList类的构造方法
    return new SubList(this, 0, fromIndex, toIndex);
}
五、内部类

(1)private class Itr implements Iterator<E>
(2)private class ListItr extends Itr implements ListIterator<E>
(3)private class SubList extends AbstractList<E> implements RandomAccess
(4)static final class ArrayListSpliterator<E> implements Spliterator<E>
ArrayList有四个内部类，

其中的Itr是实现了Iterator接口，同时重写了里面的hasNext()，next()，remove()等方法；

其中的ListItr继承Itr，实现了ListIterator接口，同时重写了hasPrevious()，nextIndex()， previousIndex()，previous()，set(E e)，add(E e)等方法，所以这也可以看出了Iterator和ListIterator的区别，就是ListIterator在Iterator的基础上增加了添加对象，修改对象，逆向遍历等方法，这些是Iterator不能实现的。具体可以参考http://blog.csdn.net/a597926661/article/details/7679765。

其中的SubList继承AbstractList，实现了RandmAccess接口，类内部实现了对子序列的增删改查等方法，但它同时也充分利用了内部类的优点，就是共享ArrayList的全局变量，例如检查器变量modCount，数组elementData等，所以SubList进行的增删改查操作都是对ArrayList的数组进行的，并没有创建新的数组。




在日常开发中，ArrayList是使用最为频繁的容器之一，其访问性能较高，插入新元素的速度较LinkedList低，但如果需要放入容器中的元素不多，且设置得当,使用ArrayList就会获得较高的性能。

    ArrayList　的底层是用数组来实现，因此每当数组填充满元素之后就必须申请新的空间，并将原来的元素复制到新的数组中去,因此如果需要频繁插入大量元素或者删除元素时，此时就会带来比较大的性能开销，这是其缺点之一，但使用数组来实现的话就使得访问的性能较快,这是其优点。从来就没有一种最优的数据结构，增删改查快又省空间。我们所能做的就是选取最为合适的容器(数据结构)，来实现我们的目的。

      底层采用数组实现,默认初始化大小为10,因此如果可以预见所需要的数据量最好指定初始化大小，避免重新申请空间，赋值所带来的性能开支。

private int size;
private static final int DEFAULT_CAPACITY = 10;
transient Object[] elementData;
      elementData 被 transient修饰不再是持久化的一部分，该变量内容在序列化后无法再被访问。

      每次调用add方法之时都会检查当前数组是否还有空间，如空间已满则申请新的空间并将原来的数组复制过去。       

   public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // 检查空间是否已满，如果已满则申请新的空间
        elementData[size++] = e;
        return true;
    }
        如空间已满，调用grow方法:申请新的空间，并将原来的元素复制过去。       

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
       可以看出为了获得更高的性能，调用非Java代码（Ｃ）实现的数组复制方法　Arrays.copyOf ，尽管如此，其插入性能还是低于LinkedList,但相对来说性能已经有了很大的提升。

          ArrayList 删除性能较于LinkedList来说还是很低，其实现方法是　检查是否为数组最后一位元素，如果是则，直接将其设置为null,不是则采用数组复制实现,仍然是调用 Arrays.copyOf

public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }
           而对于ArrayList来说,访问的性能是最高，毕竟是数组（∩＿∩)

  public E get(int index) {
        rangeCheck(index);//检查下标是否越界

        return elementData(index);
    }
           ArrayList的内部类：遍历器，实现了标准遍历器接口

           每次调用iterator方法都返回一个新的Itr类实例

 public Iterator<E> iterator() {
        return new Itr();
    }
private class Itr implements Iterator<E> {
        int cursor;       // 游标
        int lastRet = -1; // 最后一次返回元素所在的位置，如果为－１则说明没有
        int expectedModCount = modCount;

        public boolean hasNext() {//通过与数组的size比较来检查是否有下一个元素,经常与while配合使用
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;//游标向前移动
            //先进行范围检查，再返回元素
            //并将lastRet设置为当前所在位置
            return (E) elementData[lastRet = i];
        }
        
        //删除元素
        public void remove() {
            if (lastRet < 0)
                //判断游标是否已经开始移动
                throw new IllegalStateException();
            checkForComodification();

            try {
                //调用ArrayList的remove方法
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                //删除元素会将lastRet重置为-1
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
            单从代码就可以看出遍历ArrayList最高效的方式就是通过for循环,直接调用get方法访问每一个元素,使用遍历器的话，还是得多付出一点性能的开支。

           在ArrayList中还有另外一个遍历器内部类实现了ListIterator接口,在此贴上其代码:

  private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
            从来没有哪一种容器(数据结构)既能速度快(增删改查速度都快)又省空间，要么用时间换空间，要么用空间换时间，都是各方面妥协与平衡的结果。对于ArrayList不需要频繁删除或插入的情况下，是最优解；当然如果预见可能所使用的空间大小，最好在一开始就设置初始空间大小。
            
 
 
 
 
 
 
 /**
 * 概述：
 * 	List接口可调整大小的数组实现。实现所有可选的List操作，并允许所有元素，包括null，元素可重复。
 * 	除了列表接口外，该类提供了一种方法来操作该数组的大小来存储该列表中的数组的大小。
 * 
 * 时间复杂度：
 * 	方法size、isEmpty、get、set、iterator和listIterator的调用是常数时间的。
 * 	添加删除的时间复杂度为O(N)。其他所有操作也都是线性时间复杂度。
 *
 * 容量：
 * 	每个ArrayList都有容量，容量大小至少为List元素的长度，默认初始化为10。
 *  容量可以自动增长。
 *  如果提前知道数组元素较多，可以在添加元素前通过调用ensureCapacity()方法提前增加容量以减小后期容量自动增长的开销。
 *  也可以通过带初始容量的构造器初始化这个容量。
 *
 * 线程不安全：
 *	ArrayList不是线程安全的。
 *	如果需要应用到多线程中，需要在外部做同步
 *
 * modCount：
 * 	定义在AbstractList中：rotected transient int modCount = 0;
 * 	已从结构上修改此列表的次数。从结构上修改是指更改列表的大小，或者打乱列表，从而使正在进行的迭代产生错误的结果。
 *	此字段由iterator和listiterator方法返回的迭代器和列表迭代器实现使用。
 *	如果意外更改了此字段中的值，则迭代器（或列表迭代器）将抛出concurrentmodificationexception来响应next、remove、previous、set或add操作。
 *	在迭代期间面临并发修改时，它提供了快速失败 行为，而不是非确定性行为。
 *	子类是否使用此字段是可选的。
 *	如果子类希望提供快速失败迭代器（和列表迭代器），则它只需在其 add(int,e)和remove(int)方法（以及它所重写的、导致列表结构上修改的任何其他方法）中增加此字段。
 *	对add(int, e)或remove(int)的单个调用向此字段添加的数量不得超过 1，否则迭代器（和列表迭代器）将抛出虚假的 concurrentmodificationexceptions。
 *	如果某个实现不希望提供快速失败迭代器，则可以忽略此字段。
 *
 * transient：
 * 	默认情况下,对象的所有成员变量都将被持久化.在某些情况下,如果你想避免持久化对象的一些成员变量,你可以使用transient关键字来标记他们,transient也是java中的保留字(JDK 1.8)
 */
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;
    //默认初始容量
    private static final int DEFAULT_CAPACITY = 10;
    //用于空实例共享空数组实例。
    private static final Object[] EMPTY_ELEMENTDATA = {};
    //默认的空数组
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    //对的，存放元素的数组，包访问权限
    transient Object[] elementData;
    //大小，创建对象时Java会将int初始化为0
    private int size;
    //用指定的数设置初始化容量的构造函数，负数会抛出异常
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+initialCapacity);
        }
    }
    //默认构造函数，使用控数组初始化
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    //以集合的迭代器返回顺序，构造一个含有集合中元素的列表
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toarray可能（错误地）不返回对象[]（见JAVA BUG编号6260652）
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // 使用空数组
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    //因为容量常常会大于实际元素的数量。内存紧张时，可以调用该方法删除预留的位置，调整容量为元素实际数量。
    //如果确定不会再有元素添加进来时也可以调用该方法来节约空间
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }
    //使用指定参数设置数组容量
    public void ensureCapacity(int minCapacity) {
    	//如果数组为空，容量预取0，否则去默认值(10)
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)? 0: DEFAULT_CAPACITY;
        //若参数大于预设的容量，在使用该参数进一步设置数组容量
        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }
    //用于添加元素时，确保数组容量
    private void ensureCapacityInternal(int minCapacity) {
    	//使用默认值和参数中较大者作为容量预设值
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    //如果参数大于数组容量，就增加数组容量
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    //数组的最大容量，可能会导致内存溢出(VM内存限制)
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    //增加容量，以确保它可以至少持有由参数指定的元素的数目
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //预设容量增加一半
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //取与参数中的较大值
        if (newCapacity - minCapacity < 0)//即newCapacity<minCapacity
            newCapacity = minCapacity;
        //若预设值大于默认的最大值检查是否溢出
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    //检查是否溢出，若没有溢出，返回最大整数值(java中的int为4字节，所以最大为0x7fffffff)或默认最大值
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) //溢出
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
    //返回数组大小
    public int size() {
        return size;
    }
    //是否为空
    public boolean isEmpty() {
        return size == 0;
    }
    //是否包含一个数 返回bool
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    //返回一个值在数组首次出现的位置，会根据是否为null使用不同方式判断。不存在就返回-1。时间复杂度为O(N)
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    //返回一个值在数组最后一次出现的位置，不存在就返回-1。时间复杂度为O(N)
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    //返回副本，元素本身没有被复制，复制过程数组发生改变会抛出异常
    public Object clone() {
        try {
            ArrayList<?> v = (ArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    //转换为Object数组，使用Arrays.copyOf()方法
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }
    //返回一个数组，使用运行时确定类型，该数组包含在这个列表中的所有元素（从第一到最后一个元素）
    //返回的数组容量由参数和本数组中较大值确定
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }
    //返回指定位置的值，因为是数组，所以速度特别快
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }
    //返回指定位置的值，但是会检查这个位置数否超出数组长度
    public E get(int index) {
        rangeCheck(index);
        return elementData(index);
    }
    //设置指定位置为一个新值，并返回之前的值，会检查这个位置是否超出数组长度
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
    //添加一个值，首先会确保容量
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size++] = e;
        return true;
    }
    //指定位置添加一个值，会检查添加的位置和容量
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        ensureCapacityInternal(size + 1);
        //public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
        //src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组； destPos:目的数组放置的起始位置； length:复制的长度
        System.arraycopy(elementData, index, elementData, index + 1,size - index);
        elementData[index] = element;
        size++;
    }
    //删除指定位置的值，会检查添加的位置，返回之前的值
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0) System.arraycopy(elementData, index+1, elementData, index,numMoved);
        elementData[--size] = null; //便于垃圾回收期回收
        return oldValue;
    }
    //删除指定元素首次出现的位置
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    //快速删除指定位置的值，之所以叫快速，应该是不需要检查和返回值，因为只内部使用
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
    //清空数组，把每一个值设为null,方便垃圾回收(不同于reset，数组默认大小有改变的话不会重置)
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++) elementData[i] = null;
        size = 0;
    }
    //添加一个集合的元素到末端，若要添加的集合为空返回false
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew); 
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }
    //功能同上，从指定位置开始添加
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        Object[] a = c.toArray();	//要添加的数组
        int numNew = a.length;		//要添加的数组长度
        ensureCapacityInternal(size + numNew);	//确保容量
        int numMoved = size - index;//不会移动的长度(前段部分)
        if (numMoved > 0)			//有不需要移动的，就通过自身复制，把数组后部分需要移动的移动到正确位置
            System.arraycopy(elementData, index, elementData, index + numNew,numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);	//新的数组添加到改变后的原数组中间
        size += numNew;
        return numNew != 0;
    }
    //删除指定范围元素。参数为开始删的位置和结束位置
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;	//后段保留的长度
        System.arraycopy(elementData, toIndex, elementData, fromIndex,numMoved);
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }
    //检查数否超出数组长度 用于添加元素时
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //检查是否溢出
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //抛出的异常的详情
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }
    //删除指定集合的元素
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);//检查参数是否为null
        return batchRemove(c, false);
    }
    //仅保留指定集合的元素
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }
    /**
     * 源码解读 BY http://anxpp.com/
     * @param complement true时从数组保留指定集合中元素的值，为false时从数组删除指定集合中元素的值。
     * @return 数组中重复的元素都会被删除(而不是仅删除一次或几次)，有任何删除操作都会返回true
     */
    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
        	//遍历数组，并检查这个集合是否包含对应的值，移动要保留的值到数组前面，w最后值为要保留的元素的数量
        	//简单点：若保留，就将相同元素移动到前段；若删除，就将不同元素移动到前段
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        }finally {//确保异常抛出前的部分可以完成期望的操作，而未被遍历的部分会被接到后面
        	//r!=size表示可能出错了：c.contains(elementData[r])抛出异常
            if (r != size) {
                System.arraycopy(elementData, r,elementData, w,size - r);
                w += size - r;
            }
            //如果w==size：表示全部元素都保留了，所以也就没有删除操作发生，所以会返回false；反之，返回true，并更改数组
            //而w!=size的时候，即使try块抛出异常，也能正确处理异常抛出前的操作，因为w始终为要保留的前段部分的长度，数组也不会因此乱序
            if (w != size) {
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;//改变的次数
                size = w;	//新的大小为保留的元素的个数
                modified = true;
            }
        }
        return modified;
    }
    //保存数组实例的状态到一个流（即它序列化）。写入过程数组被更改会抛出异常
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
        int expectedModCount = modCount;
        s.defaultWriteObject();	//执行默认的反序列化/序列化过程。将当前类的非静态和非瞬态字段写入此流
        // 写入大小
        s.writeInt(size);
        // 按顺序写入所有元素
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    //上面是写，这个就是读了。
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        elementData = EMPTY_ELEMENTDATA;
        // 执行默认的序列化/反序列化过程
        s.defaultReadObject();
        // 读入数组长度
        s.readInt();
        if (size > 0) {
            ensureCapacityInternal(size);
            Object[] a = elementData;
            //读入所有元素
            for (int i=0; i<size; i++) {
                a[i] = s.readObject();
            }
        }
    }
    //返回ListIterator，开始位置为指定参数
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index);
        return new ListItr(index);
    }
    //返回ListIterator，开始位置为0
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }
    //返回普通迭代器
    public Iterator<E> iterator() {
        return new Itr();
    }
    //通用的迭代器实现
    private class Itr implements Iterator<E> {
        int cursor;       //游标，下一个元素的索引，默认初始化为0
        int lastRet = -1; //上次访问的元素的位置
        int expectedModCount = modCount;//迭代过程不运行修改数组，否则就抛出异常
        //是否还有下一个
        public boolean hasNext() {
            return cursor != size;
        }
        //下一个元素
        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();//检查数组是否被修改
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;	//向后移动游标
            return (E) elementData[lastRet = i];	//设置访问的位置并返回这个值
        }
        //删除元素
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();//检查数组是否被修改
            try {
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }
        //检查数组是否被修改
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
    //ListIterator迭代器实现
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }
        public boolean hasPrevious() {
            return cursor != 0;
        }
        public int nextIndex() {
            return cursor;
        }
        public int previousIndex() {
            return cursor - 1;
        }
        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();
            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        public void add(E e) {
            checkForComodification();
            try {
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
    //返回指定范围的子数组
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }
    //安全检查
    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }
    //子数组
    private class SubList extends AbstractList<E> implements RandomAccess {
        private final AbstractList<E> parent;
        private final int parentOffset;
        private final int offset;
        int size;
        SubList(AbstractList<E> parent,int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayList.this.modCount;
        }
        public E set(int index, E e) {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayList.this.elementData(offset + index);
            ArrayList.this.elementData[offset + index] = e;
            return oldValue;
        }
        public E get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ArrayList.this.elementData(offset + index);
        }
        public int size() {
            checkForComodification();
            return this.size;
        }
        public void add(int index, E e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }
        public E remove(int index) {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }
        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex,parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.size -= toIndex - fromIndex;
        }
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }
        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;
            checkForComodification();
            parent.addAll(parentOffset + index, c);
            this.modCount = parent.modCount;
            this.size += cSize;
            return true;
        }
        public Iterator<E> iterator() {
            return listIterator();
        }
        public ListIterator<E> listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;
            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = ArrayList.this.modCount;
                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }
                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }
                public boolean hasPrevious() {
                    return cursor != 0;
                }
                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }
                @SuppressWarnings("unchecked")
                public void forEachRemaining(Consumer<? super E> consumer) {
                    Objects.requireNonNull(consumer);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i >= size) {
                        return;
                    }
                    final Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write traffic
                    lastRet = cursor = i;
                    checkForComodification();
                }
                public int nextIndex() {
                    return cursor;
                }
                public int previousIndex() {
                    return cursor - 1;
                }
                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();
                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();
                    try {
                        ArrayList.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                public void add(E e) {
                    checkForComodification();
                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                final void checkForComodification() {
                    if (expectedModCount != ArrayList.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }
        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }
        private void checkForComodification() {
            if (ArrayList.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }
        public Spliterator<E> spliterator() {
            checkForComodification();
            return new ArrayListSpliterator<E>(ArrayList.this, offset,offset + this.size, this.modCount);
        }
    }
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final E[] elementData = (E[]) this.elementData;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
     * Overriding implementations should document the reporting of additional
     * characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator<>(this, 0, -1, 0);
    }
    /** Index-based split-by-two, lazily initialized Spliterator */
    static final class ArrayListSpliterator<E> implements Spliterator<E> {
        /*
         * If ArrayLists were immutable, or structurally immutable (no
         * adds, removes, etc), we could implement their spliterators
         * with Arrays.spliterator. Instead we detect as much
         * interference during traversal as practical without
         * sacrificing much performance. We rely primarily on
         * modCounts. These are not guaranteed to detect concurrency
         * violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to
         * be worthwhile in practice. To carry this out, we (1) lazily
         * initialize fence and expectedModCount until the latest
         * point that we need to commit to the state we are checking
         * against; thus improving precision.  (This doesn't apply to
         * SubLists, that create spliterators with current non-lazy
         * values).  (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach
         * (the most performance-sensitive method). When using forEach
         * (as opposed to iterators), we can normally only detect
         * interference after actions, not before. Further
         * CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small
         * elementData array given its size(), that could only have
         * occurred due to interference.  This allows the inner loop
         * of forEach to run without any further checks, and
         * simplifies lambda-resolution. While this does entail a
         * number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation
         * occur anywhere other than inside forEach itself.  The other
         * less-often-used methods cannot take advantage of most of
         * these streamlinings.
         */
        private final ArrayList<E> list;
        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set
        /** Create new spliterator covering the given  range */
        ArrayListSpliterator(ArrayList<E> list, int origin, int fence,
                             int expectedModCount) {
            this.list = list; // OK if null unless traversed
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }
        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            ArrayList<E> lst;
            if ((hi = fence) < 0) {
                if ((lst = list) == null)
                    hi = fence = 0;
                else {
                    expectedModCount = lst.modCount;
                    hi = fence = lst.size;
                }
            }
            return hi;
        }
        public ArrayListSpliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
                new ArrayListSpliterator<E>(list, lo, index = mid,
                                            expectedModCount);
        }
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked") E e = (E)list.elementData[i];
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            ArrayList<E> lst; Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((lst = list) != null && (a = lst.elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = lst.modCount;
                    hi = lst.size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked") E e = (E) a[i];
                        action.accept(e);
                    }
                    if (lst.modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }
        public long estimateSize() {
            return (long) (getFence() - index);
        }
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        int removeCount = 0;
        final BitSet removeSet = new BitSet(size);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            if (filter.test(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // shift surviving elements left over the spaces left by removed elements
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            for (int k=newSize; k < size; k++) {
                elementData[k] = null;  // Let gc do its work
            }
            this.size = newSize;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }
        return anyToRemove;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            elementData[i] = operator.apply((E) elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }
}           





