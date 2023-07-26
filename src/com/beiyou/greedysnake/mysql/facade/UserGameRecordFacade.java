package com.beiyou.greedysnake.mysql.facade;

import java.sql.Timestamp;

import com.ibatis.db.dao.DaoException;
import com.pwl.framework.db.ibatis.IbatisHelper;
import com.pwl.framework.exception.BaseException;
import com.pwl.framework.facade.BaseFacade;
import com.beiyou.greedysnake.mysql.ibatis.dao.UserGameRecordDao;
import com.beiyou.greedysnake.mysql.util.DAONameLink;

public class UserGameRecordFacade extends BaseFacade{
	private UserGameRecordDao userGameRecordDao;
	
	public UserGameRecordFacade(){
		try{
			userGameRecordDao = (UserGameRecordDao) ((IbatisHelper) helper).getDao(DAONameLink.USERGAMERECORD_DAO);
		}catch(Exception ex){
			throw new BaseException(ex);
		}
		
	}
	
	public void beginTime(int uid, Timestamp timebegin) throws BaseException{
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			userGameRecordDao.beginTime(uid, timebegin);
			// 提交任务
			helper.commit();
		} catch (DaoException ex)
		{
			helper.rollback();
			throw new BaseException(ex);
		}catch(Exception ex){
			helper.rollback();
			throw new BaseException(ex);
		}
	}
	
	public void updateTime(int uid, Timestamp gametime, Timestamp timeend) throws BaseException{
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			userGameRecordDao.updateTime(uid, gametime, timeend);
			// 提交任务
			helper.commit();
		} catch (DaoException ex)
		{
			helper.rollback();
			throw new BaseException(ex);
		}catch(Exception ex){
			helper.rollback();
			throw new BaseException(ex);
		}
	}

	public void updateRank(int uid, int rank, Timestamp gametime){
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			userGameRecordDao.updateRank(uid, rank, gametime);
			// 提交任务
			helper.commit();
		} catch (DaoException ex)
		{
			helper.rollback();
			throw new BaseException(ex);
		}catch(Exception ex){
			helper.rollback();
			throw new BaseException(ex);
		}
	}


}
