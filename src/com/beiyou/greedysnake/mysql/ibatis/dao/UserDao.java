package com.beiyou.greedysnake.mysql.ibatis.dao;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameScore;
import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameRecord;
import com.beiyou.greedysnake.mysql.ibatis.entity.User;
import com.ibatis.db.dao.DaoException;
import com.pwl.framework.db.ibatis.dao.BaseDao;

public class UserDao extends BaseDao{

	public UserDao(){
		super();
	}
	
	public User getUserById(int id) throws DaoException{
		return (User)this.executeQueryForObject("getUserById", id);
	}
	
	public User getUserByName(String name)throws DaoException{
		return (User)this.executeQueryForObject("getUserByName", name);
	}
	
	public User getUserByNickName(String name)throws DaoException{
		return (User)this.executeQueryForObject("getUserByNickName", name);
	}
	
	public void setUserByName(String name, String pass)throws DaoException{
		 Map<String, String> param = new HashMap<String, String>();
		 param.put("name", name);
		 param.put("pass", pass);
		 System.out.println(name);
		 System.out.println(param.get("pass"));
		 this.executeUpdate("setUserByName", param);
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
	
	public void beginTime(int uid, Timestamp timebegin) throws DaoException{
		UserGameRecord temp = new UserGameRecord(uid);
		temp.setTimebegin(timebegin);
		this.executeUpdate("beginTimer", temp);
	}

	public void updateTime(int uid, Timestamp gametime, Timestamp timeend) throws DaoException{
		UserGameRecord temp = new UserGameRecord(uid);
		temp.setTimeend(timeend);
		temp.setTimebegin(gametime);
		this.executeUpdate("updateTime", temp);
	}
	
	public void updateRank(int uid, int rank, Timestamp gametime) throws DaoException{
		UserGameRecord temp = new UserGameRecord(uid);
		temp.setGamerank(rank);
		temp.setTimebegin(gametime);
		this.executeUpdate("updateRank", temp);
	}

	public void updateAllTime(Timestamp gametime, Timestamp timeend) throws DaoException {
		// TODO Auto-generated method stub
		UserGameRecord temp = new UserGameRecord();
		temp.setTimebegin(timeend);
		temp.setTimebegin(gametime);
		this.executeUpdate("updateAllTime", temp);
	}
}