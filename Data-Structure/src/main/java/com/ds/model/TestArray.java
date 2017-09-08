package com.ds.model;

import org.junit.Test;

public class TestArray {
	@Test
	public void testArray1(){
		Person[] p1=new Person[5];
		Student[] s1=new Student[5];
		s1[0]=new Student();
//		p1[0]=new Student();
		
	}
	
    @Test
	public void testExtends(){
		Person p1=new Person();
		Student s1=new Student();
		p1=s1;
    	s1=(Student) p1;
    	
    	Student a=(Student) new Person();
	}
	
    
    
    
}
