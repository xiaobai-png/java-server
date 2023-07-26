package com.beiyou.greedysnake.mysql.facade;

import com.beiyou.greedysnake.mysql.ibatis.dao.UserGameScoreDao;
import com.beiyou.greedysnake.mysql.ibatis.entity.UserGameScore;
import com.beiyou.greedysnake.mysql.util.DAONameLink;
import com.ibatis.db.dao.DaoException;
import com.pwl.framework.db.ibatis.IbatisHelper;
import com.pwl.framework.exception.BaseException;
import com.pwl.framework.facade.BaseFacade;

public class UserGameScoreFacade extends BaseFacade{
	private UserGameScoreDao userGameScoreDao;
	
	public UserGameScoreFacade(){
		try{
			userGameScoreDao = (UserGameScoreDao) ((IbatisHelper) helper).getDao(DAONameLink.USERGAMESCORE_DAO);
		}catch(Exception ex){
			throw new BaseException(ex);
		}
	
	}
	
	public void insertUserGrade(int name, String grade) throws BaseException
	{	
	
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			userGameScoreDao.insertUserGrade(name, grade);
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
	
	public UserGameScore getUserGrade(int uid) throws BaseException
	{
		UserGameScore grade = null;
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			grade = userGameScoreDao.getUserGrade(uid);
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
		return grade;
	}
	
	
	public void updateUserGrade(int uid, String grade) throws BaseException{
		try
		{	
			// 开始业务
			helper.beginTransaction();
			// 执行业务
			userGameScoreDao.updateUserGrade(uid, grade);
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
