package com.java.spring.ioc;


public class HelloWorldAutowire {

	private User user;

	
	
	public HelloWorldAutowire() {
		super();
	}

	public HelloWorldAutowire(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
