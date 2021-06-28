package com.dell.lockedme.model;

import java.io.Serializable;

public class UserCreds  implements Serializable{
	private String siteName;
	private String username;
	private String password;
	
	public UserCreds() {}

	public UserCreds(String siteName, String username, String password) {
		this.siteName = siteName;
		this.username = username;
		this.password = password;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
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
		return "UserCreds [siteName=" + siteName + ", username=" + username+ ", password=" + password + "]";
	}
	
	
}
