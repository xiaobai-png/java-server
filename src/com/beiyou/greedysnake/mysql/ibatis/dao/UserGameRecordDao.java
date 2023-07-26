package com.beiyou.greedysnake.mysql.ibatis.dao;

import java.sql.Timestamp;

import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameRecord;
import com.ibatis.db.dao.DaoException;
import com.pwl.framework.db.ibatis.dao.BaseDao;

public class UserGameRecordDao extends BaseDao{
	public UserGameRecordDao(){
		super();
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

}
