package com.java.mybatis.chain;
/*既然为设计模式,必然有其鲜明代码的主体框架,我们来分析下

首先定义一个抽象的处理角色Handler ,其次是具体实现类ConcreteHandler ,在ConcreteHandler 我们通过getNextHandler()来判断是否还有下一个责任链,如果有,则继续

传递下去,调用getNextHandler().doHandler()来实现。

分析了简单的责任链模式的代码框架之后,我们接下来给代码加点实际的功能,举个很简单的例子,就是报销流程,项目经理<部门经理<总经理

其中项目经理报销额度不能大于500,则部门经理的报销额度是不大于1000,超过1000则需要总经理审核*/
public class ChainTest {

	public static void main(String[] args) {
		/*ConcreteHandler handler1 = new ConcreteHandler();
        ConcreteHandler handler2 = new ConcreteHandler();
        handler1.setNextHandler(handler2);
        handler1.doHandler();*/
        
        ProjectHandler projectHandler =new ProjectHandler();
        DeptHandler deptHandler =new DeptHandler();
        GeneralHandler generalHandler =new GeneralHandler();
        projectHandler.setNextHandler(deptHandler);
        deptHandler.setNextHandler(generalHandler);
        projectHandler.doHandler("lwx", 450);
        projectHandler.doHandler("lwx", 600);
        projectHandler.doHandler("zy", 600);
        projectHandler.doHandler("zy", 1500);
        projectHandler.doHandler("lwxzy", 1500);
	}
}
