package com.java.mybatis.test1.model;

public enum SexEnum {
	
	MALE(1,"男"),FMALE(0,"女");
	
	private int id;
	private String name;
	private SexEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static SexEnum getSex(int id){
		if("0".equals(id)){
			return FMALE;
		}else if("1".equals(id)){
			return MALE;
		}
		return null;
	}

}
