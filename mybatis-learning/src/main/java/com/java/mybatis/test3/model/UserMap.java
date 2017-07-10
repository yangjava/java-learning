package com.java.mybatis.test3.model;


public class UserMap {
	
	    private Integer userId;
		private String userSex;
		private String address;
		
		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer userId) {
			this.userId = userId;
		}
		public String getUserSex() {
			return userSex;
		}
		public void setUserSex(String userSex) {
			this.userSex = userSex;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		@Override
		public String toString() {
			return "UserMap [userId=" + userId + ", userSex=" + userSex
					+ ", address=" + address + "]";
		}
		
		
		
}
