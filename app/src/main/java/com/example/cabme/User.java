package com.example.cabme;

public class User {
	private String firstName;
	private String lastName;
	private String email;
	private String userName;
	private String phone;
	private String password;

	User (String firstName,String lastName, String email, String userName, String phone, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.userName = userName;
		this.phone = phone;
		this.password = password;
	}

	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getEmail() {
		return this.email;
	}
	public String getUserName() {
		return this.userName;
	}
	public String getPhone() {
		return this.phone;
	}
	public String getPassword() {
		return this.password;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
