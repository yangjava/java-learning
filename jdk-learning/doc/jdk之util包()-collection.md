##  java.util.Collection<E>源码

package java.util;


public interface Collection<E> extends Iterable<E> {
    // Query Operations

    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Iterator<E> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);

    // Modification Operations

    boolean add(E e);

    boolean remove(Object o);


    // Bulk Operations

    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    void clear();


    // Comparison and hashing

    boolean equals(Object o);

    int hashCode();
}


(1)从定义中可以看出Collection接口中定义了:查询方法、修改方法、批量操作方法、哈希和比较方法。

(2)Collection是一个根接口，表示一组对象，这些对象也称为collection的元素。

(3)Collection的实现类中有这样的特征: 有些collection是允许重复元素的;
有些collection是不允许重复元素的;有些collection是有序的;有些collection是无序的。

(4)JDK不提供对Collection接口的直接实现，而且提供了两个更为具体的接口去实现（如:java.util.List和java.util.Set)

----------------------------------------------------------------------------------

下面来看看java.util.Collection<E>中具体有哪些方法：

从下面的表格中可以看出java.util.Collection<E>接口中一共有15个方法，其中查询操作6个;修改操作2个;批量操作5个;比较和哈希操作2个。

查询操作	int size()	返回此 collection 中的元素数。
如果此 collection 包含的元素大于 Integer.MAX_VALUE，则返回 Integer.MAX_VALUE。

boolean isEmpty()	如果此 collection 不包含元素，则返回 true,否则返回false

boolean contains(Object o)	如果此 collection 包含指定的元素，则返回 true。
更确切地讲，当且仅当此 collection 至少包含一个满足 (o==null ? e==null : o.equals(e)) 的元素 e 时，返回 true。

Iterator<E> iterator()	返回在此 collection 的元素上进行迭代的迭代器。
关于元素返回的顺序没有任何保证（除非此 collection 是某个能提供保证顺序的类实例）。

Object[] toArray()	返回包含此 collection 中所有元素的数组。
如果 collection 对其迭代器返回的元素顺序做出了某些保证，那么此方法必须以相同的顺序返回这些元素。

<T> T[] toArray(T[] a)	返回包含此 collection 中所有元素的数组；返回数组的运行时类型与指定数组的运行时类型相同。
如果指定的数组能容纳该 collection，则返回包含此 collection 元素的数组。
否则，将分配一个具有指定数组的运行时类型和此 collection 大小的新数组。

修改操作	boolean add(E e)	将元素e添加到此集合中，如果添加成功则返回true,添加失败则返回false

boolean remove(Object o)	从此 collection 中移除指定元素o的单个实例,如果操作成功则返回true,操作失败则返回false

批量操作	boolean containsAll(Collection<?> c )	如果此 collection 包含指定 collection 中的所有元素，则返回 true。

addAll(Collection<? extends E> c)	将指定 collection 中的所有元素都添加到此 collection 中.

boolean removeAll(Collection<?> c)	移除此 collection 中那些也包含在指定 collection 中的所有元素。

boolean retainAll(Collection<?> c)	仅保留此 collection 中那些也包含在指定 collection 的元素。

void  clear()	移除此 collection 中的所有元素。

比较和哈希操作	boolean equals(Object o)	比较此 collection 与指定对象是否相等

int hashCode()	返回此 collection 的哈希码值
