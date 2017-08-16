本篇简要介绍，方法(函数)体内的参数检查，遇到不合法的输入参数时，抛出带关键错误提示信息的异常。

[java] view plain copy
/** 
     * Initializes a new instance of this class. 
     * 
     * @param channel 
     *            The channel upon whose file this lock is held 
     * 
     * @param position 
     *            The position within the file at which the locked region 
     *            starts; must be non-negative 
     * 
     * @param size 
     *            The size of the locked region; must be non-negative, and the 
     *            sum <tt>position</tt> + <tt>size</tt> must be 
     *            non-negative 
     * 
     * @param shared 
     *            <tt>true</tt> if this lock is shared, <tt>false</tt> if it is 
     *            exclusive 
     * 
     * @throws IllegalArgumentException 
     *             If the preconditions on the parameters do not hold 
     * 
     * @since 1.7 
     */  
    protected FileLock(AsynchronousFileChannel channel, long position,  
            long size, boolean shared) {  
        if (position < 0)  
            throw new IllegalArgumentException("Negative position");  
        if (size < 0)  
            throw new IllegalArgumentException("Negative size");  
        if (position + size < 0)  
            throw new IllegalArgumentException("Negative position + size");  
        this.channel = channel;  
        this.position = position;  
        this.size = size;  
        this.shared = shared;  
    }  

当输入参数position<0的时候，抛出IllegalArgumentException(postion为负数)。

当输入参数size<0的时候，抛出IllegalArgumentException(size为负数)。

当输入参数position和size的和<0的时候，抛出IllegalArgumentException(position+size为负数)。

这样，当输入参数不合法的时候，程序就不会再顺序执行下去。

通过抛出异常的方式，避免了程序进一步不合理的执行。

从异常的类型IllegalArgumentException来看，一看就知道是参数不合法造成的。

从异常的错误提示信息来看，“postion为负数”、“size为负数”、“position+size为负数”，准确地告诉了我们哪个参数不合法。

简而言之，异常的构造非常准确和友好，值得学习。