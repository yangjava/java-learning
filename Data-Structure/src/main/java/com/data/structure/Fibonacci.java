package com.data.structure;
/*
 * 使用java实现斐波那契数列
 * 这道兔子题的实质就是斐波那契数列： 
 * 有一对兔子，从出生后第3个月起每个月都生一对兔子，小兔子长到第三个月后每个月又生一对兔子，
 * 假如兔子都不死，问每个月的兔子总数为多少?，
 * 现在从递归、递推两个角度出发解决这个问题,当然还有其它的方法，
 * 同一道题用各种不同的思路去思考解决，也是对知识综合运用的锻炼吧。
 * 关于斐波那契数列在百度百科上的定义如下： 
 * 斐波那契数列，又称黄金分割数列，指的是这样一个数列：0、1、1、2、3、5、8、13、21、34、……
 * 在数学上，斐波纳契数列以如下被以递归的方法定义：F（0）=0，F（1）=1，F（n）=F(n-1)+F(n-2)（n≥2，n∈N*）
 * 百度百科关于斐波那契数列的来源请参见兔子问题根据其定义我们可以很方便的构建出该数列的数据结构实现。
 */
public class Fibonacci {
	
	//使用递归方式实现斐波那契数列
	public static int fibonacci1(int n){  
        if(n <= 2){  
            return 1;  
        }else{  
            return fibonacci1(n-1) + fibonacci1(n-2);  
        }  
    }  
      
    // 递推实现方式  
    public static int fibonacci2(int n){  
        if(n <= 2){  
            return 1;  
        }  
        int n1 = 1, n2 = 1, sn = 0;  
        for(int i = 0; i < n - 2; i ++){  
            sn = n1 + n2;  
            n1 = n2;  
            n2 = sn;  
        }  
        return sn;  
    }  
    
    /*****************************************************/
    
    /**
     * 返回斐波那契数第n个值,n从0开始
     * 实现方式，基于递归实现
     * 递归是最简单的实现方式，但递归有很多的问题，在n的值非常大时，会占用很多的内存空间，
     * 既然该数列定义F（n）=F(n-1)+F(n-2)（n≥2，n∈N*），那么我们可以从头到尾进行计算，先计算前面的值，然后逐步算出第n个值。
     * @param n
     * @return
     */
    public static int getFib(int n){
        if(n < 0){
            return -1;
        }else if(n == 0){
            return 0;
        }else if(n == 1 || n ==2){
            return 1;
        }else{
            return getFib(n - 1) + getFib(n - 2);
        }
    }
    
    /**
     * 返回斐波那契数第n个值,n从0开始
     * 实现方式，基于变量实现
     * 从上面的实现中我们定义了3个变量a、b、c其中c=a+b，然后逐步进行计算从而得到下标为n的值。 
     * 既然我们可以定义变量进行存储，那么同样我们还可以定义一个数组，该数组的每一个元素即一个斐波那契数列的值，这样我们不仅能得到第n个值，还能获取整个斐波那契数列。
     * @param n
     * @return
     */
    public static int getFib2(int n){
        if(n < 0){
            return -1;
        }else if(n == 0){
            return 0;
        }else if (n == 1 || n == 2){
            return 1;
        }else{
            int c = 0, a = 1, b = 1;
            for(int i = 3; i <= n; i++){
                c = a + b;
                a = b;
                b = c;
            }
            return c;
        }
    }
    
    
    /**
     * 返回斐波那契数第n个值,n从0开始
     * 实现方式，基于数组实现
     * @param n
     * @return
     */
    public static int getFib3(int n){
        if(n < 0){
            return -1;
        }else if(n == 0){
            return 0;
        }else if (n == 1 || n == 2){
            return 1;
        }else{
            int[] fibAry = new int[n + 1];
            fibAry[0] = 0;
            fibAry[1] = fibAry[2] = 1;
            for(int i = 3; i <= n; i++){
                fibAry[i] = fibAry[i - 1] + fibAry[i - 2];
            }
            return fibAry[n];
        }
    }
}
