关于线程池的文章很多，有的充斥着理论和各种专业名词，很容易让人犯晕、搞混概念，降低理解线程池的效率。有的写得很详细很好，但忽略了一些地方。于是我索性抛开这些文章，直接深入源码，重新抽丝剥茧，不过难免犯错走进代码逻辑的死胡同，再翻看别人的好文章才得以走通。现在把总结写出来，尽量用源码（jdk 1.8）说话，用简单的语言说通概念和逻辑，作为别人文章的一种补充。如有错误或不足，欢迎指出。

线程池的好处

线程池其中一个好处是什么？

线程复用，减少系统开销。
众所周知，创建一个线程系统开销大。假设有以下代表任务的类

class MyTask implements Runnable{
    public void run(){
        //...
    };
}
如果要创建10个该任务并行执行，就要创建10个线程。

for(int i=0;i<10;i++){
    Thread t = new Thread(new MyTask());
    t.start();
}
如果用了线程池，只需这样。假设使用具有固定线程的线程池，且只有5个线程，执行10个任务，比上例系统开销少一半，平均一个线程执行2个任务，这就是线程复用。

ExecutorService es = Executors.newFixedThreadPool(5);
for(int i=0;i<10;i++){
    es.execute(new MyTask());
}
es.shutdown();
 

线程池的种类和概念

线程池的种类

通过java.util.concurrent.Executors创建不同类型的线程池：

Executors.newCachedThreadPool()
        创建无界线程池，动态地根据任务数和处理能力新增或回收线程。任务统统被放在阻塞队列里排队，每个线程都尝试从阻塞队列里取出任务，有任务就执行它。当队列没有任务时，线程阻塞60秒，60秒后依然没有任务，回收线程。当每个线程都繁忙，队列还有任务在等待，就再创建一个线程执行它。

Executors.newFixedThreadPool( int n)
        创建有界的线程池，线程池里只有n个线程，线程处理完一个任务就从阻塞队列里取出其他任务。当所有线程都在忙，其他任务在队列里等待。

Executors.newSingleThreadExecutor()
        创建单线程的线程池。

Executors.newScheduledThreadPool( int n)
        （因为这个线程池是另一个线程池类，所以不在本文讨论之列，详细可看其他文章。）

从Executors源码可以看到，前三个方法创建的都是一样的线程池类ThreadPoolExecutor，只是参数不一样，本文只说这个类。

看ThreadPoolExecutor的构造方法和其属性，稍后逐个说明：

public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {

    //...（省略部分参数检查源码）
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue; 
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory; 
    this.handler = handler; 
}
ThreadPoolExecutor的内部类Worker

private final class Worker extends AbstractQueuedSynchronizer implements Runnable{
    //...
}
首先Worker实现了Runnable接口。线程池中创建的线程，都是Worker线程，每个Worker线程通过直接调用任务实例的run方法，以实现对任务的执行。

另外Worker还继承了AbstractQueuedSynchronizer（后面简称AQS），使得Worker获得锁功能。为什么要锁功能？稍后再说，预告一下，就是shutdown方法和shutdownNow方法实现原理的区别。

PS：ReentranLock之所以能实现代码块的并发访问控制，就是因为继承了AQS而实现的（忘了是从JDK哪个版本开始，ReentranLock类抛开对其的依赖，而是通过内部类ReentrantLock.Sync来继承），而不是通过关键字synchronized等方式实现，可见其强大之处。以后有机会，我会去研究一下AQS，有结果就再写一篇文章。

ThreadPoolExecutor的重要属性

corePoolSize
        直译为核心线程池大小，使用FixedThreadPool时才用到，用于限制创建Worker线程的数量。例如Executors.newFixedThreadPool(5)，corePoolSize就是5，只能创建5个Worker线程。

maximumPoolSize
        直译为最大线程池大小，使用CachedThreadPool时才用到，默认值为Integer.MAX_VALUE，也用于限制创建Worker线程的数量，当然MAX_VALUE个线程只是极端情况，毕竟CachedThreadPool是按需创建或销毁线程。当使用FixedThreadPool时，此值等于corePoolSize。

keepAliveTime
        Worker线程等待任务的时间，使用CachedThreadPool时此值默认为60秒，表示当阻塞队列没有任务时，线程等待60秒再取任务，还是没有的话，Worker线程被回收。FixedThreadPool该值为0

workQueue
        它是一个无界LinkedBlockingQueue阻塞队列，任务在此排队等待被Worker获取和执行。队列为空时，Worker线程对队列的take/poll操作会阻塞进入等待状态。（我觉得命名为taskQueue会不会更好一点，毕竟它放的是任务。）

allowCoreThreadTimeOut
        决定Worker线程如何从workQueue取出任务的行为。默认为false，当workQueue没有任务，Worker线程阻塞等待，直到该队列有任务为止。设为true，当workQueue没有任务，Worker线程只阻塞keepAliveTime时间，还是没有任务就返回null。

RejectExecutionHandler
        拒绝策略，默认为AbortPolicy

ThreadFactory
        创建Worker线程的线程工厂类，默认为Executors.DefaultThreadFactory

workers
        Worker实例的集合，是个普通的HashSet。每次对workers的增删操作，有ReentranLock保证并发安全。

ctl
        这个参数设计很巧妙，既用于记录Worker的数量，也用于记录线程池状态（生命周期）。

ctl属性

这个属性十分重要，看源码：

private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
private static final int COUNT_BITS = Integer.SIZE - 3;
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
private static final int RUNNING    = -1 << COUNT_BITS;
private static final int SHUTDOWN   =  0 << COUNT_BITS;
private static final int STOP       =  1 << COUNT_BITS;
private static final int TIDYING    =  2 << COUNT_BITS;
private static final int TERMINATED =  3 << COUNT_BITS;
其中静态参数RUNNING、SHUTDOWN、STOP、TIDYING和TERMINATED表示线程池的各种状态，由类型为AtomInteger的ctl记录当前线程池状态，保证对线程池状态的修改都是原子操作。ctl这个属性设计十分巧妙，我觉得是属于大牛级别的技术，可以借鉴应用到其他项目中，下面开始解释。

ctl有两个作用：

记录线程池状态
记录Worker数量，包括活动的和空闲的（或叫阻塞）。
线程池状态有：

RUNNING            线程池可用，可以通过execute方法添加新任务
SHUTDOWN        线程池已关闭，无法通过execute方法添加新任务，但已经排队的任务或正在执行的任务不受影响
STOP                    线程池已停止，既无法通过execute方法添加新任务，在排队或正在执行的任务也要立即中断
TIDYING               线程池已经没有任务存在了，Worker数量也为0，可以终结线程池
TERMIATED          线程池已终结
奇怪，一个Integer，取值范围是-2^31 到 2^31-1，仅且只能有一个数值，如何既记录线程池状态，又记录Worker数量呢？二进制的厉害之处就在这里了，看下表列出了各状态的值：

属性	十进制	二进制	十六进制
CAPACITY	536870911	0001 1111 1111 1111 1111 1111 1111 1111	0x1FFFFFFF
RUNNING	-536870912	1110 0000 0000 0000 0000 0000 0000 0000	0xE0000000
SHUTDOWN	0	0000 0000 0000 0000 0000 0000 0000 0000	0x00000000
STOP	536870912	0010 0000 0000 0000 0000 0000 0000 0000	0x20000000
TIDYING	1073741824	0100 0000 0000 0000 0000 0000 0000 0000	0x40000000
TERMINATED	1610612736	0110 0000 0000 0000 0000 0000 0000 0000	0x60000000
线程池利用ctl的二进制数据的前3位记录状态，后29位记录Worker数量。

例如在RUNNING状态，有1个Worker时，ctl就是：

1110 0000 0000 0000 0000 0000 0000 0001，十进制即 -536870911。前3位111是RUNNING，后29位就是1的二进制。

RUNNING状态，有2个Worker时，ctl就是：

1110 0000 0000 0000 0000 0000 0000 0010，十进制即 -536870910，后29位是2的二进制

RUNNING状态，Worker最多时，ctl就是：

1111 1111 1111 1111 1111 1111 1111 1111，十进制即 -1，后29位是536870911的二进制

要如何从ctl中取得状态值和Worker数呢，通过“按位与”操作即可，看源码：

private static int runStateOf(int ctl){    //根据ctl的值取得状态
    return ctl & ~CAPACITY; 
}
private static int workerCountOf(int ctl){ //根据ctl的值取得Worker数
    return ctl & CAPACITY; 
}
又如何在ctl中设置状态值和Worker数呢？通过“按位或”操作即可，看源码：

private static int ctlOf(int rs, int wc) {    // rs是状态，wc是Worker数
    return rs | wc;
}
所以，ctl的初始值表示线程池状态是RUNNING，Worker数为0。

关于“按位或”在现实中的应用，可以参考这篇别人的文章《按位或在多选中的应用》

线程池状态的变化看下图，可以看到是单向的，参照上面的表格，线程池状态值只能变大：

线程池状态的变化

 

线程池工作流程及原理

之前已经提到，不管是Executors.newFixedThreadPool、Executors.newCacherThreadPool还是Executors.newSingleThreadExecutor，创建的对象都是ThreadPoolExecutor，只是构造方法的参数不同。现在以FixedThreadPool为例，分析其源码。

假设该线程池只有2个线程，放入3个任务（假设这3个任务都比较耗时），然后shutdown线程池：

ExecutorService es = Executors.newFixedThreadPool(2);
es.execute(new MyTask("Task_1"));
es.execute(new MyTask("Task_2"));
es.execute(new MyTask("Task_3"));
es.shutdown();
该线程池工作基本流程：

1.  创建线程数为2的有界线程池，初始状态为RUNNING。

2.  线程池处于RUNNING状态时，可以通过execute方法往线程池添加任务。

3.  假设每个任务都比较耗时，前2个任务分别给2个Worker线程执行，其余任务在workQueue里排队。

4.  关闭线程池，无法再通过execute方法添加新任务

        a)  假设调用shutdown方法关闭线程池，虽然不能添加新任务，但正在执行的任务和排队的任务不受影响，此时线程池状态为SHUTDOWN。

        b)  假设调用shutdownNow方法关闭线程池，不但无法添加新任务，而且正在执行的任务和排队的任务都被终止，此时线程池状态为STOP。

5.  当一个Worker线程完成一个任务，就立即从workQueue里取出其他任务。当workQueue已经没有任务，此Worker线程处于空闲状态（阻塞），直到workQueue再有任务时才继续执行。

6.  当线程池处于SHUTDOWN状态时，workQueue没有任务，其中一个刚执行完最后一个任务的Worker线程先调用tryTerminate()尝试终结其他空闲的Worker线程，再终结自己。

7.  当所有Worker线程已经终结，线程池状态设为TIDYING，线程池跟着终结，状态最后变为TERMINATED。

execute方法流程图

这个流程图忽略了部分不那么重要的细节，只展示重要的工作流程和逻辑。稍后的源码分析也分开Main线程和Worker线程来分析，我觉得这样比较好理解多线程下的流程。

使用FixedThreadPool时execute方法的流程图

Main线程，execute方法

线程池就通过这个方法添加任务Task

public void execute(Runnable task){
    int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(task, true))
            return;
        c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(task)){
	    //...
    }
}
exeucute方法很简单，先通过ctl取得Worker数（包括繁忙和空闲的），小于corePoolSize就调用addWorker方法创建Worker实例和Worker线程，执行传入的任务Task。大于corePoolSize就将任务Task放入workQueue排队。如果线程池状态不是RUNNING了，就拒绝任务Task。

Main线程，addWorker方法

addWorker用于创建Worker实例，并负责启动Worker线程。分两部分，前一部分通过两个for循环来检测是否足够条件执行addWorker。

private boolean addWorker(Runnable firstTask, boolean core){
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //  当初我就在这里犯了低级错误，搞错了逻辑，导致之后对流程的理解进入了死循环。当满足这些条件时，返回fasle，拒绝加入任务Task，条件有：
        //  1.	状态为STOP、DIDYING或TERMINATE
        //  2.	SHUTDOWN时firstTask为null
        //  3.	SHUTDOWN时workQueue为空
        if (rs >= SHUTDOWN && ! (rs == SHUTDOWN && firstTask == null &&! workQueue.isEmpty()))
            return false;

        for (;;) {
        
            //  这里只判断Worker数是否超过线程池容许的线程数量，超过肯定拒绝加入任务
            int wc = workerCountOf(c);
            if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
                return false;

            //  如果以上拒绝条件都没有，就给ctl的Worker数量+1，并跳出循环，进入addWorker的下一部分
            if (compareAndIncrementWorkerCount(c))
                break retry;
            c = ctl.get();
            if (runStateOf(c) != rs)
                continue retry;
        }
    }
    //...
}
addWorker方法的第二部分，忽略了try代码和判断代码

Worker w = new Worker(firstTask);
final Thread t = w.thread;
final ReentrantLock mainLock = this.mainLock;
mainLock.lock();
if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)){
    workers.add(w);		//workers只是个HashSet，由mainLock保证并发安全
}
mainLock.unlock();
t.start();				//启动Worker线程
创建Worker实例，Worker类继承了AQS实现了Runnable接口，其构造方法也很简单

private final class Worker extends AbstractQueuedSynchronizer implements Runnable{
    Worker(Runnable firstTask) {
        setState(-1);                //设置AQS的state状态，0表示未锁定，1表示锁定
        this.firstTask = firstTask;                        //Worker维护了任务Task
        this.thread = getThreadFactory().newThread(this);  //创建Worker线程
    }
    //...
}
初始化Worker时为什么state为-1而不是0，后面会解释。

addWorker方法成功，返回true，并结束execute方法。

当Main线程执行完三次execute方法添加了三个任务Task后（假设每个任务都比较耗时），线程池状态如下。此时还有2个Worker线程，正在执行任务Task_1和Task_2，Task_3在排队。

3个execute方法执行完的线程池状态

Main线程，shutdown方法

public void shutdown() {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);  //设置ctl的状态为SHUTDOWN
        interruptIdleWorkers();     //中断空闲的Worker线程，稍后再详细分析
        onShutdown();               //触发shutdown事件，默认啥都没做
    } finally {
        mainLock.unlock();
    }
    tryTerminate();                 //尝试终结线程池
}
由于目前仍有Worker线程在执行任务，在此条件下shutdown方法也仅仅是将线程池状态设为SHUTDOWN，其他行为稍后分析。

此时，Main线程的步骤已经走完，开始看另外两个Worker线程到底在干嘛。

Worker线程，runWorker方法

先看看runWorker的简单流程图，依然忽略了一些不重要的细节

Worker线程的runWorker方法流程图

众所周知，当Worker线程调用start方法启动线程，实则调用Worker实例的run方法，看源码，它直接调用ThreadPoolExecutor的runWorker方法

public void run() {
    runWorker(this);		//this是Worker实例
}
省略了部分无关紧要的runWorker代码

final void runWorker(Worker w){
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock();                 // a)
    boolean completedAbruptly = true;

    //从Worker里取出任务Task，如果为null就通过getTask方法从workQueue里取，都是null就结束，让当前Worker进入销毁阶段
    while (task != null || (task = getTask()) != null){
        w.lock();              // b)

        //当线程池处于STOP状态，线程中断自己
        if ((runStateAtLeast(ctl.get(), STOP) ||
                (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
            wt.interrupt();

        task.run();           //直接调用任务Task的run方法，执行任务本身
        task = null;          //重置task引用，目的是执行完一个任务，再执行workQueue里的另一个任务
        w.unlock();           // c)
        completedAbruptly = false;
    }
    //当连getTask也返回null表示没有任何任务，可以销毁当前Worker实例
    processWorkerExit(w, completedAbruptly);
}
上面的代码注释不难理解，然而a)、b)和c)步骤是什么鬼呢？有什么用呢？这涉及到Worker线程的中断机制：

a)      通过unlock方法给Worker实例解锁（AQS的state设为1），此时Worker线程还没开始访问任务Task，表示Worker线程可以被其他线程中断

b)      在while代码块里，表示任务Task已经准备就绪，通过lock方法给Worker实例加锁，表示Worker线程开始执行任务，不能被其他Worker线程中断，只能在线程池处于STOP状态时自己中断自己（回顾之前STOP的描述），或被Main线程强制中断。

c)      任务Task已经顺利完成，解锁Worker实例（AQS的state设为0）。

也就是说，当Worker线程在执行任务时，Worker处于lock状态。当完成任务或任务没有就绪，Worker处于unlock状态。

为什么其他Worker线程要取得别的Worker的锁时才能中断它的线程。Main线程则不要取得锁就可以中断别的Worker线程，个中实现原理在后面介绍interruptIdleWorkers方法中会说到。

Worker线程，getTask方法

假设此时任务Task_2已经完成，Woker#2实例就访问workQueue，发现有任务Task_3，就取出并执行。

Worker线程的getTask方法的线程池状态

如何从workQueue取出任务Task_3的呢，看getTask方法源码。

private Runnable getTask(){
    for (;;){
        int c = ctl.get();
        int rs = runStateOf(c);

        //  当满足这些条件时，终止getTask方法，返回null，并将ctl的Worker数量减一。表示当前Worker已经没有任务可以执行，进入销毁步骤
        //  1.	线程池是STOP、TIDYING或TERMINATE时
        //  2.	SHUTDOWN时，workQueue为空
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }
        int wc = workerCountOf(c);
		
        //使用FixedThreadPool且allowCoreThreadPool为fasle，此为false
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
		
        //如果Worker数超过线程池极限，当然不能再增加活动的Worker。或者poll超时，当前Worker自然也要终结，返回null进入销毁步骤
        if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
                continue;
        }

        //从阻塞队列取出任务Task，如果allowCoreThreadPool为true就用poll方法，否则take方法
        Runnable r = timed ? 
                       workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                       workQueue.take();
       if (r != null)
           return r;
       timedOut = true;		//记录poll超时
    }
}
阻塞队列的poll方法和take方法有什么区别呢？

poll方法从阻塞队列中取出对象，如果队列为空，则当前线程阻塞keepAliveTime时间再尝试取出，还是没有就返回null，记录超时状态，在重新进入for循环时才试图终结Worker。
take方法是，如果队列为空，当前线程则一直阻塞，直到队列有对象为止，返回该对象。
所以当使用FixedThreadPool，都是take方法，Worker线程有可能在这里出现阻塞。使用CachedThreadPool且allowCoreThreadTimeOut为true，则是poll方法。

现在假设这么一个情况，Worker#2线程在getTask方法第一段代码workQueue.isEmpty()时，Task_3还是在的。但中途线程调度器切换到Worker#1线程，并处理完之前的任务，并workQueue.take()取出了Task_3任务执行。线程调度器切换回Worker#2线程，当来到workQueue.take()时，阻塞队列已经没有对象，Worker#2线程阻塞，进入空闲状态。

Worker线程getTask方法可能的线程池状态

Worker#1线程，runWorker方法

现在Worker#2线程处于阻塞状态，只剩Worker#1线程在运行了。

while (task != null || (task = getTask()) != null){
    w.lock();	
    task.run();			//执行任务Task_3
    task = null; 
    w.unlock();
}
processWorkerExit(w, completedAbruptly);
当执行完任务Task_3之后，再次调用getTask方法，workQueue为空，返回null，结束while循环，调用processWorkerExit方法，Worker#1开始销毁步骤。

要清楚一点，此时Worker#2实例是没有锁的，没有锁的，没有锁的。因为lock方法在getTask方法之后，而Worker#2在getTask方法中处于阻塞状态，所以Worker#2实例是处于unlock状态，这十分重要，后面会解释。

Worker#1线程，processWorkerExit方法

当Worker没有任务可以处理，进入自销毁阶段。

省略了部分不重要的代码段

private void processWorkerExit(Worker w, boolean completedAbruptly){
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        completedTaskCount += w.completedTasks;    //线程池总的执行任务数
        workers.remove(w);                         //从workers删除一个Worker实例
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
	
    //...
    addWorker(null, false);
}
最后的addWorker(null)是什么意思呢，就是在RUNNING或SHUTDOWN且workQueue非空时，再创建一个Worker处理workQueue里余下的任务。通常CacherThreadPool才有这一步，意味着所有Worker都繁忙，刚好在processWorkerExit方法执行中又有新任务加入，线程池动态新创建一个Worker进行处理。

如果是FixedThreadPool，addWorker(null)返回false，不做任何事。

Worker#1线程，tryTerminate方法

尝试终结线程池，将状态设为TIDYING，最后设为TERMINATED

简单流程图如下

tryTerminate方法流程图

终止tryTerminate方法的条件有：

线程池状态是RUNNING时
线程池状态是SHUTDOWN时workerQueue非空
线程池状态已经是TIDYING或TERMINATE时
ctl的Worker数不为0
ctl的Worker数不是0，意味着还有Worker线程在执行任务，或有Worker线程空闲（阻塞），就调用interruptIdleWorkers(ONLY_ONE)中断一个空闲（阻塞）的Worker线程，并结束。

int c = ctl.get();
if (isRunning(c) || runStateAtLeast(c, TIDYING) || 
        (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
    return;
if (workerCountOf(c) != 0) {
    interruptIdleWorkers(ONLY_ONE);		// 中断一个空闲（阻塞）的Worker线程
    return;
}
worker数为0，表示已经没有任何任务，也没有任何Worker，可以将线程池状态设为TIDYING，然后再设为TERMINATED。

if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
    try {
        terminated();						//空方法
    } finally {
        ctl.set(ctlOf(TERMINATED, 0));
        termination.signalAll();			//唤醒其他线程
    }
    return;
}
 

Worker#1线程，interruptIdleWorkers方法

负责中断一个空闲的Worker线程。分析这个方法之前，先看看shutdown方法和shutdownNow方法有什么不一样。

shutdown方法的代码片段：

checkShutdownAccess();
advanceRunState(SHUTDOWN);
interruptIdleWorkers();    //看这里
onShutdown();
shutdownNow方法的代码片段：

List<Runnable> tasks;
checkShutdownAccess();
advanceRunState(STOP);
interruptWorkers();    //看这里
tasks = drainQueue();
区别就是：

shutdown方法调用interruptIdleWorkers方法
shutdownNow方法调用interruptWorkers方法
再看看interruptIdleWorkers方法和interruptWorkers方法有什么不一样

interruptIdleWorkers方法的代码片段：

for (Worker w : workers) {
    Thread t = w.thread;
    if (!t.isInterrupted() && w.tryLock()) {		//尝试取得Worker的锁
        try {
            t.interrupt();
        } catch (SecurityException ignore) {
        } finally {
            w.unlock();
        }
    }
    if (onlyOne)
        break;
}
interruptWorkers方法的代码片段：

for (Worker w : workers)
    w.interruptIfStarted();
可以看到，interruptIdleWorkers方法在尝试中断其他线程时，会先对Worker#2调用tryLock方法尝试取得Worker#2的锁。我还没有研究AQS的源码，但我已经猜测到，当Worker#1线程尝试取得Worker#2的锁时，如果无法取得（代表Worker#2正在执行任务），则Worker#1线程在此阻塞。如果取得，Worker#1线程就中断Worker#2线程，那么Worker#2线程抛出InterruptedException。

interruptWorkers方法就不一样，它不会尝试取得Worker锁，它不管Worker是否有锁，立即中断该线程。看interruptIfStarted方法的源码：

Thread t;
//AQS的state为0表示未锁定，1表示锁定。Worker实例初始化时state为-1，表示我还没开始工作做贡献实现价值呢，你不要中断我的。
if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) 
    t.interrupt();
纵观ThreadPoolExecutor所有代码，只有shutdownNow方法调用interruptWorkers方法。

所以，这就是Worker加锁时（正在执行任务）不能被其他Worker线程中断的原理。而当Main线程强制调用shutdownNow方法时，Worker无论是否有锁，也能被Main线程中断。

至此，Worker#1线程的interruptIdleWorkers方法中断了Worker#2线程，Worker#2线程抛出InterruptedException，（回顾getTask方法的代码）。

Worker#1线程也已完结

完结

最后贴出Executors.newCachedThreadPool()的基本流程，依然省略了不那么重要的步骤。

使用CachedThreadPool时execute方法的流程图




前面的文章已经详细分析了Executor框架及其家族的各个成员，为介绍本文做了铺垫，因此分析ThreadPoolExecutor核心实现原理可谓千呼万唤使出来啊，直奔主题吧！

首先从ThreadPoolExecutor的构造方法开始。

ThreadPoolExecutor的构造方法

    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
    }
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), handler);
    }
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
构造器中各个参数的含义：

corePoolSize：核心池的大小，在创建了线程池后，默认情况下，线程池中并没有任何线程，而是等待有任务到来才创建线程去执行任务，当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中。只有当工作队列满了的情况下才会创建超出这个数量的线程。如果某个线程的空闲时间超过了活动时间，那么将标记为可回收，并且只有当线程池的当前大小超过corePoolSize时该线程才会被终止。用户可调用prestartAllCoreThreads()或者prestartCoreThread()方法预先创建线程，即在没有任务到来之前就创建corePoolSize个线程或者一个线程。

maximumPoolSize：线程池最大线程数，这个参数也是一个非常重要的参数，它表示在线程池中最多能创建多少个线程；当大于了这个值就会将Thread由一个丢弃处理机制来处理。

keepAliveTime：表示线程没有任务执行时最多保持多久时间会终止。默认情况下，只有当线程池中的线程数大于corePoolSize时，keepAliveTime才会起作用，直到线程池中的线程数不大于corePoolSize，即当线程池中的线程数大于corePoolSize时，如果一个线程空闲的时间达到keepAliveTime，则会终止，直到线程池中的线程数不超过corePoolSize。但是如果调用了allowCoreThreadTimeOut(boolean)方法，在线程池中的线程数不大于corePoolSize时，keepAliveTime参数也会起作用，直到线程池中的线程数为0；

Unit：参数keepAliveTime的时间单位，有7种取值，在TimeUnit类中有7种静态属性。

workQueue：一个阻塞队列，用来存储等待执行的任务，当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中。

threadFactory：线程工厂，主要用来创建线程；

handler：表示当拒绝处理任务时的策略，也就是参数maximumPoolSize达到后丢弃处理的方法。有以下四种取值：

ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。 
ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。 
ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务
用户也可以实现接口RejectedExecutionHandler定制自己的策略。

下面将深入剖析线程池的实现原理：

线程池状态

JDK1.7中使用原子变量ctl来控制线程池的状态，其中ctl包装了以下两个field：

workerCount：表示有效的线程数
runState：表示线程池的状态，是否运行，关闭等

由于workerCount和runState被保存在一个int中，因此workerCount限制为（2 ^ 29）-1（约5亿）线程。其使用shift / mask常数来计算workerCount和runState的值。源码如下：

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }
workerCount是已经启动但没有停止的worker线程数量。

runState用于控制线程池的生命周期状态，主要包含以下几个值：

RUNNING	接收新任务，并且处理任务队列中的任务，当创建线程池后，初始时，线程池处于RUNNING状态
SHUTDOWN	不接收新任务，但是处理任务队列的任务
STOP	不接收新任务，不处理任务队列，同时中断所有进行中的任务
TIDYING	所有任务已经被终止，工作线程数量为 0，到达该状态会执行terminated()
TERMINATED	terminated()已经完成
各状态的转换关系：

RUNNING -> SHUTDOWN：shutdown()被调用

(RUNNING or SHUTDOWN) -> STOP：shutdownNow()被调用

SHUTDOWN -> TIDYING：队列和池均为空

STOP -> TIDYING：池为空

TIDYING -> TERMINATED：钩子方法terminated()已经完成。



当线程池状态为TERMINATED时，调用awaitTermination()的线程将从等待中返回。

Worker

考虑到将Worker实现分析加入本文将导致文章太长，不宜阅读，关于Worker的核心实现以及ThreadPoolExecutor的核心方法runWorker、getTask和processWorkerExit的功能分析和源码解读请参见https://my.oschina.net/7001/blog/889770。

addWorker

addWorker用于添加并启动工作线程，先看其流程图：



源码解析如下：

    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            /** 这里返回false有以下可能：
              * 1 线程池状态大于SHUTDOWN
              * 2 线程池状态为SHUTDOWN，但firstTask不为空，也就是说线程池已经SHUTDOWN，拒绝添加新任务
              * 3 线程池状态为SHUTDOWN且firstTask为空，但workQueue为空，即无任务需要执行
              */
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN &&
                   firstTask == null &&
                   ! workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                /** 返回false有以下可能：
                  * 1 工作线程数量超过最大容量
                  * 2 core为true，工作线程数量超过边界corePoolSize
                  * 3 core为false,工作线程数量超过边界maximumPoolSize
                  */
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;//直接跳出最外层循环
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)//线程池状态发生改变则从最外层循环重新开始
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        Worker w = new Worker(firstTask);
        Thread t = w.thread;

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // 持有锁之后需要重新检查线程池状态，防止ThreadFactory返回失败或线程池在加锁之前被关闭
            int c = ctl.get();
            int rs = runStateOf(c);
             /** 返回false有以下可能：
               * 1 t为null,说明ThreadFactory创建线程失败，可能发生OutOfMemoryError
               * 2 线程池状态大于SHUTDOWN
               * 3 线程池状态为SHUTDOWN，但firstTask不为空
               */
            if (t == null ||
                (rs >= SHUTDOWN &&
                 ! (rs == SHUTDOWN &&
                    firstTask == null))) {
                decrementWorkerCount();
                tryTerminate();
                return false;
            }

            workers.add(w);

            int s = workers.size();
            if (s > largestPoolSize)
                largestPoolSize = s;
        } finally {
            mainLock.unlock();
        }

        t.start();
        // 在线程池变为stop期间，线程可能已经被添加到workers，但还未被启动（该现象不太可能发生，这可能
        // 导致罕见的丢失中断，因为Thread.interrupt不能保证对非启动状态的线程有效
        if (runStateOf(ctl.get()) == STOP && ! t.isInterrupted())
            t.interrupt();

        return true;
    }
addWorker首先会检查当前线程池的状态和给定的边界是否可以创建一个新的worker，在此期间会对workers的数量进行适当调整；如果满足条件，将创建一个新的worker并启动，以参数中的firstTask作为worker的第一个任务。

任务的执行

ThreadPoolExecutor类中，最核心的任务提交方法是execute()方法，使用submit()提交任务最终调用的也是execute()方法，先看看流程图：



源码如下：

    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * 分3步处理:
         *
         * 1. 当前工作线程数 < corePoolSize，直接创建新的工作线程执行任务（调用addWorker）
         *
         * 2. 当前工作线程数 >=corePoolSize,线程池状态为RUNNING，且任务加入工作队列成功，
         * 再次检查线程池当前状态是否处于RUNNING，如果不是，从队列移除任务，移除成功则拒绝任务
         * 如果为RUNNING，判断当前工作线程数量是否为 0，如果为 0，就增加一个工作线程
         *
         * 3. 线程池状态不是RUNNING或任务入队失败，尝试开启普通线程执行任务，失败就拒绝该任务
         */
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }
从上面的分析可以总结出线程池运行任务的四个阶段：

poolSize < corePoolSize 且队列为空，此时会新建线程来处理提交的任务
poolSize == corePoolSize，提交的任务进入工作队列，工作线程从队列中获取任务执行，此时队列不为空且未满。
poolSize == corePoolSize，并且工作队列已满，此时也会新建线程来处理提交的任务，但是poolSize < maxPoolSize
poolSize == maxPoolSize，并且队列已满，此时会触发拒绝策略。
当再次检查线程池当前状态不是RUNNING时，不仅从任务队列移除任务，同时会尝试终止线程池。

    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }
尝试终止线程池

tryTerminate在很多地方都有调用，那么这个方法作用是什么呢？

场景分析：当调用线程池的shutDown()方法后，会调用interruptIdleWorkers尝试中断工作线程，而工作线程只有在getTask()期间才会有被中断的机会。假设interruptIdleWorkers成功设置的多个线程的中断状态，若此时任务队列非空，由于线程池状态为SHUTDOWN，getTask()将会从任务队列成功获取到任务；在runWorker执行任务时，线程池状态为SHUTDOWN（小于STOP），那么当前工作线程的中断状态将会被清除。当中断状态被清除后，从工作队列取任务将不会响应中断，直到工作队列为空，此时之前被成功设置中断状态的工作线程都可能会阻塞在workQueue.take()，由于SHUTDOWN状态的线程池不会接收新任务，工作线程将一直阻塞下去，永不会退出。怎么办呢？tryTerminate这时将派上用场，Doug Lea大神巧妙的在所有可能导致线程池产终止的地方安插了tryTerminated()尝试线程池终止的逻辑，由tryTerminated来终止空闲的线程，直到无空闲线程，然后终止线程池。

下面看看tryTerminated的具体实现：

    final void tryTerminate() {
        for (;;) {
            int c = ctl.get();
            //由之前的状态转换可知，RUNNING不能直接跳到TERMINATED，因此返回
            //状态已经为TERMINATED，无需再调用terminated()，返回
            //状态为SHUTDOWN且队列不空，队列中的任务仍需要处理，不能调用terminated()，返回
            if (isRunning(c) ||
                runStateAtLeast(c, TIDYING) ||
                (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
                return;
            if (workerCountOf(c) != 0) { // 符合终止条件
                interruptIdleWorkers(ONLY_ONE);//一次仅中断一个线程
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));//将状态设为TERMINATED，且设置workerCount为0
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
            // else retry on failed CAS
        }
    }
    //中断空闲线程
    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne)
                    break;
            }
        } finally {
            mainLock.unlock();
        }
    }
由源码可知，以下情况将线程池变为TERMINATED终止状态：

1 线程池状态为SHUTDOWN，并且线程池中工作线程数量为0，工作队列为空

2 线程池状态为STOP，并且线程池中工作线程数量为0

关闭线程池

可使用shutdown()和shutdownNow()关闭线程池，但是效果和实现方法各不相同；同时也可调用awaitTermination(long timeout, TimeUnit unit)等待线程池终止。理解关闭线程池逻辑可能需要参照文章https://my.oschina.net/7001/blog/889770中介绍的runWorker和getTask()逻辑。

shutdown()

使用shutdown关闭线程池时，之前提交的任务都会被执行完成，但是拒绝接收新任务。shutdown不会等待之前提交的任务执行结束，该情况下可以使用awaitTermination()。源码如下：

    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();//权限校验
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();//中断所有空闲线程
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }
    //更新线程池状态为SHUTDOWN，使用自旋保证完成
    private void advanceRunState(int targetState) {
        for (;;) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) ||
                ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
                break;
        }
    }
本文不止一次提到过空闲线程，那么线程池中什么才是空闲线程？

空闲worker：正在从workQueue阻塞队列中获取任务的worker；

运行中worker：正在使用runWorker执行任务的worker。

阻塞在getTask()获取任务的worker在被中断后，会抛出InterruptedException，不再阻塞获取任务。继续进入自旋操作，此时线程池已经是shutdown状态，且workQueue.isEmpty()，getTask()返回null，进入worker退出逻辑。

shutdownNow()

shutdownNow表示立即关闭线程池，它会尝试停止所有活动的正在执行的任务，并停止处理任务队列中的任务，该方法将返回正在等待被执行的任务列表。shutdownNow尽力尝试停止运行中的任务，没有任何保证。取消任务是通过Thread.interrupt()发出中断信号来实现的。由runWorker源码可知，已经进入加锁区的任务并不会响应中断，因此只有工作线程执行完当前任务，进入getTask()才会感知线程池状态为STOP，开始处理退出逻辑。

shutdownNow对所有线程立即发出中断信号是为了阻止从任务队列取任务，让这些线程尽快进入退出逻辑；而那些正在执行runWorker加锁区中代码的线程，将在执行完当前任务后立即检测到线程池的状态，进入退出逻辑。源码如下：

    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);//将线程池状态修改为STOP
            interruptWorkers();//中断所有工作线程
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }
    /**
      * 使用drainTo方法一次性将工作队列中的任务加入taskList ，并从工作队列移除；
      * 如果队列是DelayQueue或任何其他类型的队列，poll或drainTo可能无法删除某些元素，则会逐个删除它们
      */
    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        List<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }
下面看看interruptWorkers的具体实现：

   private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                try {
                    w.thread.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        } finally {
            mainLock.unlock();
        }
    }
比较interruptIdleWorkers源码可知，interruptWorkers不需要等待持有Worker上的锁才中断线程，调用interruptWorkers会立即中断所有工作线程，interruptIdleWorkers则需要首先持有Worker上的锁才能进行中断。interruptWorkers目前只用在shutdownNow中。

awaitTermination

awaitTermination()会循环线程池是否terminated或是否已经超过超时时间，每次判断不满足条件则使用Condition对象termination阻塞指定时间。termination.awaitNanos() 是通过 LockSupport.parkNanos(this, nanosTimeout)实现的阻塞等待。调用shutdown之后，在以下情况发生之前，awaitTermination()都会被阻塞：

1 所有任务正常完成，线程池正常变为TERMINATED

2 任务仍未完成，到达超时时间

3 当前线程被中断

阻塞等待过程中发生以下具体情况会解除阻塞：

1 任务正常完成，线程池正常变为TERMINATED，此时会调用 termination.signalAll()唤醒所有阻塞等待的线程

2 到达超时时间，nanos <= 0条件满足，返回false

3 当前线程被中断，termination.awaitNanos()将从阻塞中唤醒，并向上抛出InterruptException异常。

源码如下：

   public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException {
        long nanos = unit.toNanos(timeout);//将超时时限分片
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (runStateAtLeast(ctl.get(), TERMINATED))//状态>=TERMINATED
                    return true;
                if (nanos <= 0)//达到超时时间
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }
常用的几种线程池

通常情况下，我们使用Executors的静态工厂方法来创建线程池，下面看创建几种常用线程池的方法：

    /** newFixedThreadPool将创建一个固定长度的线程池，每当提交一个任务就创建一个线程，
      * 直到达到线程池的最大数量，这时线程池的规模不再变化
      * （如果某个线程由于发生未预期的Exception而终止，线程池将补充一个新线程）
      */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
    /** newCachedThreadPool将创建一个可缓存的线程池，如果线程池的当前规模超过了处理需求，
      * 那么将回收空闲的线程，当需求增加时，则可以创建另外一个线程，线程池规模不存在任何限制
      */
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
    /** newSingleThreadExecutor是一个单线程的Executor，只创建一个线程来执行任务，
      * 如果线程异常结束，会创建新的线程来替代。
      * newSingleThreadExecutor能确保依照任务在队列中的顺序来串行执行（如：FIFO，LIFO，优先级）
      */
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
    /** 
      * newScheduledThreadPool创建一个固定长度的线程池，而且以延时或定时的方式来执行任务
      */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }


Worker类既实现了Runnable，又继承了AbstractQueuedSynchronizer，所以Worker本身是一个可执行的任务，又可以实现锁的功能。Worker主要用于管理线程的中断状态和一些指标，如已完成的任务数量；Worker通过继承AbstractQueuedSynchronizer来简化任务执行时获取锁与释放锁的操作。对Worker加锁可防止中断在运行任务中的工作线程，中断仅用于唤醒在等待从workQueue中获取任务的线程。

如何防止被中断？

worker实现了一个简单的不可重入互斥锁，工作线程执行任务时，首先会进行加锁，如果主线程想要中断当前工作线程，需要先获取锁，否则无法中断。当工作线程执行完任务则会释放锁，并调用getTask从workQueue获取任务继续执行。由此可知，只有在等待从workQueue中获取任务（调用getTask期间)时才能中断。工作线程接收到中断信息，并不会立即就会停止，而是会检查workQueue是否为空，不为空则还是会继续获取任务执行，只有队列为空才会被停止。因此中断是为了停止空闲线程，也就是那些从任务队列获取任务被阻塞（任务队列为空）的线程。后续会详细分析整个过程。

为什么Worker被设计为不可重入？

这就需要知道那些操作可能会发生中断工作线程的操作。目前主要有以下几个：

setCorePoolSize()；

setMaximumPoolSize()；

setKeppAliveTime()；

allowCoreThreadTimeOut()；

shutdown()；

tryTerminate()；

如果锁可以重入，调用诸如setCorePoolSize等线程池控制方法时可以再次获取锁，那么可能会导致调用线程池控制方法期间中断正在运行的工作线程。jdk不希望在调用像setCorePoolSize这样的池控制方法时重新获取锁。

Worker源码如下：

    private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
    {
        /**
         * 该类实际绝不会被序列化，提供serialVersionUID主要为了屏蔽javac warning
         */
        private static final long serialVersionUID = 6138294804551838833L;

        /** 运行在Worker对象中的线程 */
        final Thread thread;
        /** 要运行的初始任务，可能为null */
        Runnable firstTask;
        /** 每个线程的任务计数器，使用volatile保证可见性 */
        volatile long completedTasks;

        /**
         * 使用指定的初始任务和ThreadFactory中的线程对象创建一个Worker
         */
        Worker(Runnable firstTask) {
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /** 将主运行循环委托给外部的runWorker  */
        public void run() {
            runWorker(this);
        }

        // Lock methods
        //
        // The value 0 represents the unlocked state.
        // The value 1 represents the locked state.

        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }
    }
核心函数 runWorker

 runWorker流程图：



runWorker会不断从工作队列表中取任务并执行；同时runWorker也会管理线程的中断状态，源码如下：

   final void runWorker(Worker w) {
        Runnable task = w.firstTask;
        w.firstTask = null;
        boolean completedAbruptly = true;//是否“突然完成”，非正常完成
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                clearInterruptsForTaskRun();
                try {
                    beforeExecute(w.thread, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }
主要步骤：

1 从初始任务开始执行，如果firstTask 为null，只要线程池在运行，调用getTask从队列中取任务来执行。如果getTask返回null，则worker可能由于线程池状态调整或参数动态调整导致退出。若外部代码中抛出异常导致worker退出，completedAbruptly将为true，则在processWorkerExit将创建新的worker替代。

2 执行任务前，对worker加锁，已防止在任务运行时，线程池中其他操作中断当前worker。调用clearInterruptsForTaskRun管理线程中断状态，首先看看源码：

   private void clearInterruptsForTaskRun() {
        if (runStateLessThan(ctl.get(), STOP) &&
            Thread.interrupted() &&
            runStateAtLeast(ctl.get(), STOP))
            Thread.currentThread().interrupt();
    }
这个方法调用非常重要，当线程池状态小于STOP，调用Thread.interrupted()，如果getTask期间设置了worker的中断状态，则返回true，同时Thread.interrupted()将清除中断状态，即再次调用将返回false；再次检查线程池状态，如果状态大于或等于STOP，则需要调用Thread.currentThread().interrupt()恢复线程的中断状态。因此，该方法有两个作用：

<一>：当线程池仍然在运行时，若其他操作中断了worker，则该操作将清除中断状态

<二>：清除中断状态后，再次检查线程池状态，如果状态大于或等于STOP，此时需要恢复线程的中断状态，这样在下次调用getTask将返回null，worker将正常退出。

3 每个任务执行前，调用beforeExecute，beforeExecute可能抛出异常，该情况下抛出的异常会导致任务未执行worker就死亡，没有使用catch处理，会向上抛跳出循环，且completedAbruptly==true。

4 beforeExecute正常完成则开始运行任务，并收集其抛出的任何异常以发送到afterExecute，这里将分别处理分别处理RuntimeException，Error和任意Throwables，由于不能在Runnable.run内重新抛出Throwables，因此将Throwable包装为Error（到线程的UncaughtExceptionHandler中处理）向上抛。任何向上抛的异常都将导致线程死亡，completedAbruptly仍然为true。

5 任务执行完成后，调用afterExecute，该方法同样可能抛出异常，并导致线程死亡。

获取任务

runWorker运行期间，将不断调用getTask()从任务队列中取任务来执行。

getTask方法流程图如下：



源码如下：

private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out?
        /**
         * 外层循环
         * 用于检查线程池状态和工作队列是否为空
         */
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // 调用了shutdownNow()或调用了shutdown()且workQueue为空，返回true
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            boolean timed;      // Are workers subject to culling?
            /**
              * 内层循环
              * 用于检测工作线程数量和获取task的超时状态
              */
            for (;;) {
                int wc = workerCountOf(c);
                timed = allowCoreThreadTimeOut || wc > corePoolSize;

                if (wc <= maximumPoolSize && ! (timedOut && timed))
                    break;
                if (compareAndDecrementWorkerCount(c))
                    return null;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }

            try {
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }
任务队列为空时，getTask()会根据当前线程池的配置执行阻塞或定时等待任务，当发生以下条件时，将返回null：

1 工作线程的数量超过maximumPoolSize

2 线程池已经停止

3 线程池调用了shutdown且任务队列为空

4 工作线程等待一个任务超时，且allowCoreThreadTimeOut || workerCount > corePoolSize返回true。

工作线程退出

runWorker中，当getTask返回null或抛出异常，将进入processWorkerExit处理工作线程的退出。

processWorkerExit方法流程图如下：



下面看看源码：

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
         /**
           * 如果是突然终止，工作线程数减1
           * 如果不是突然终止，在getTask()中已经减1
           */
        if (completedAbruptly)
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();//锁定线程池
        try {
            completedTaskCount += w.completedTasks;//汇总完成的任务数量
            workers.remove(w);//移除工作线程
        } finally {
            mainLock.unlock();
        }

        tryTerminate();//尝试终止线程池

        int c = ctl.get();
        //状态是running、shutdown，即tryTerminate()没有成功终止线程池
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                //任务队列中仍然有任务未执行，需至少保证有一个工作线程
                if (min == 0 && ! workQueue.isEmpty())
                    min = 1;
                /**
                  * allowCoreThreadTimeOut为false则需要保证线程池中至少有corePoolSize数量的工作线程
                  */
                if (workerCountOf(c) >= min)
                    return; 
            }
            //添加一个没有firstTask的工作线程
            addWorker(null, false);
        }
    }
processWorkerExit只会在工作线程中被调用，主要用于清理和记录一个即将死亡的线程，该方法可能会终止线程池。这里不再详细tryTerminate和addWorker的实现，关于tryTerminate和addWorker的分析参见

