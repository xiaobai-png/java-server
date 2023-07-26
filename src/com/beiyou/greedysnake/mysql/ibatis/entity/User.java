package com.beiyou.greedysnake.mysql.ibatis.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable{
	
	private int id;
	private String username;
	private String password;
	private int grade;
	
	public User(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public int getUserGrade() {
		return grade;
	}
	
	public void setUserGrade(int grade) {
		this.grade = grade;
	}

	
}
