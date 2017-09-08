package com.java.utils.collection;

public class TestCollection {

	public static  String toStringFromByte(byte[] b){
		return b.toString();
	}
	
	public static void main(String[] args) {
		String s="Hello World";
		byte[] bytes = s.getBytes();
		System.out.println(toStringFromByte(bytes));;
	}
	
}
