package com.beiyou.greedysnake.mysql.ibatis.entity;

import java.io.Serializable;

public class UserGameScore  implements Serializable{
	private int id;
	private int uid;
	private String grade;
	
	public UserGameScore() {
		super();
	}
	
	public UserGameScore(int uid){
		super();
		this.uid = uid;
	}
	
	public UserGameScore(int uid, String grade){
		super();
		this.uid = uid;
		this.grade = grade;
	}
	
	public int getId(){
		return id;
	}
	
	public int getUid(){
		return uid;
	}
	
	public String getGrade(){
		return grade;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setUid(int uid){
		this.uid = uid;
	}
	
	public void setGrade(String grade){
		this.grade = grade;
	}
}
