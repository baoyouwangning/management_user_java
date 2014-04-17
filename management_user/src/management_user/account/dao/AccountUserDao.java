package management_user.account.dao;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import management_user.bean.Course;
import management_user.bean.User;


public class AccountUserDao {
	private static List<User> userList = new ArrayList<User>();
	
	public boolean addUser(User aUser) {
		AccountUserDao.userList.add(aUser);
		return true;
	}
	
	//查找用户,并返回
	public User findUserByName(String aUserName) {
        for (Iterator<User> i = AccountUserDao.userList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUserName) ) {
            	return userRef;
            }
        }   
		return null;
	}
	
	public AccountUserDao() {
		//nothing now
		
		User aTest = new User("wangning","67b3173ea91a6e0704e0d68a73d445ce");
		userList.add(aTest);
	}
	
	public boolean SyncUserInfo(User aUser) {
		 //Dao层用户列表
		 for (Iterator<User> i = AccountUserDao.userList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUser.getUserName()) ) {
            	
            	//对用户密码和课程信息进行更新
            	userRef.setUserPwd(aUser.getUserPwd());
            	
            	//遍历Service层传过来的缓存CourseList
            	try {
	            	for (Iterator<Course> j = aUser.getCourseList().iterator(); j.hasNext();) { 
	                    Course courseRefFromServ = j.next(); 
	                    //更新DAO层用户课程
	                    userRef.setCourse(courseRefFromServ.getCourseName(), courseRefFromServ.getCourseScore());
	        		}
            	} catch(NullPointerException e) {
            		//课程列表为空
            		return true;
            	}
            	return true;
            }
		 }   
		return false;
	}

}
