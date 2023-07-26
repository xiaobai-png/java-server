package com.beiyou.greedysnake.mysql.ibatis.dao;

import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameScore;
import com.ibatis.db.dao.DaoException;
import com.pwl.framework.db.ibatis.dao.BaseDao;

public class UserGameScoreDao extends BaseDao{
	public UserGameScoreDao(){
		super();
	}
	
	public void insertUserGrade(int name, String grade)throws DaoException{
		 UserGameScore temp = new UserGameScore(name, grade);
		 this.executeUpdate("insertUserGrade", temp);
	}
	
	public UserGameScore getUserGrade(int uid)throws DaoException{
		return (UserGameScore)this.executeQueryForObject("getUserGrade", uid);
	}
	
	public void updateUserGrade(int uid, String grade)throws DaoException{
		 UserGameScore temp = new UserGameScore(uid, grade);
		 this.executeUpdate("updateUserGrade", temp);
	}
	
}
