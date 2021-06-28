package com.dell.lockedme.model;

import java.io.Serializable;

public class Users  implements Serializable{

	public String fullname;
	private String username;
	private String password;
	
	public Users() {}

	public Users(String fullname, String username, String password) {
		this.fullname = fullname;
		this.username = username;
		this.password = password;
	}

	public String getFullName() {
		return password;
	}

	public void setFullName(String fullname) {
		this.fullname = fullname;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	@Override
	public String toString() {
		return "Users [fullname=" + fullname + ", username=" + username + ", password=" + password + "]";
	}

	
	
	
}
