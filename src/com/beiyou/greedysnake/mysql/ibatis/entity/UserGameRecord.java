package com.beiyou.greedysnake.mysql.ibatis.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserGameRecord implements Serializable{
	private int id;
	private int uid;
	private int gamerank;
	private Timestamp timebegin;
	private Timestamp timeend;
	
	public UserGameRecord() {
		super();
	}
	
	public UserGameRecord(int uid){
		super();
		this.uid = uid;
	}
	
	public UserGameRecord(int uid, int gamerank){
		super();
		this.uid = uid;
		this.gamerank = gamerank;
	}
	
	public int getId(){
		return id;
	}
	
	public int getUid(){
		return uid;
	}
	
	public Integer getGamerank(){
		return gamerank;
	}
	
	public Timestamp getTimebegin(){
		return timebegin;
	}
	
	public Timestamp getTimeend(){
		return timeend;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setUid(int uid){
		this.uid = uid;
	}
	
	public void setGamerank(int gamerank){
		this.gamerank = gamerank;
	}
	
	public void setTimebegin(Timestamp timebegin){
		this.timebegin = timebegin;
	}
	
	public void setTimeend(Timestamp timeend){
		this.timeend = timeend;
	}
}

