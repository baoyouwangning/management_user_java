package management_user.account.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import management_user.account.dao.AccountUserDao;
import management_user.bean.Course;
import management_user.bean.User;

public class AccountUserServiceImplementation implements AccountUserService {
	
	private static List<User> serviceList = new ArrayList<User>();
	
	public AccountUserServiceImplementation() {
		//nothing now
	}
	
	@Override
	public int userLogin(User aUser) {
		
		//判断是否重复登录
		if( checkLogin(aUser.getUserName()) ) {
			return -3;  //用户已登录
		}
		
		User userFromDao = new User();
		userFromDao  = getUserFromDao(aUser.getUserName());
		
		//进行用户名存在检验
		if( userFromDao == null ) {
			return 5;  //用户不存在
		}
		
		//用户存在，进行用户名密码校验
		String encryptedPwd = new String(aUser.getUserPwd());
		if( !userFromDao.getUserPwd().equals(JM(encryptedPwd)) ) {
			return -4;  //密码错误
		}
		
		//加至在线用户列表(User中为Dao中上次等录的最新信息
		userFromDao.setUserPwd(userFromDao.KL(userFromDao.getUserPwd()));
		AccountUserServiceImplementation.serviceList.add(userFromDao);
		
		//将此用户本身loginRole标记设为登录状态
		userFromDao.setLoginRole(AccountUserServiceImplementation.serviceList);
		
		return 1;
	}
	
	@Override
	public int userSignUp(User aUser) {
		User userFromDao = new User();
		userFromDao  = getUserFromDao(aUser.getUserName());
		
		//进行用户名存在检验
		if( userFromDao != null ) {
			return -5;  //用户已存在
		}
		
		//还原MD5值，添加一个用户
		String secEncryptedPwd = new String(aUser.getUserPwd()); 
		aUser.setUserPwd(JM(secEncryptedPwd)); //此时user password 为明文密码的MD5值
		
		AccountUserDao aDao = new AccountUserDao();
		aDao.addUser(aUser);
		
		return 2;
	}
	
	@Override
	public int userCheck(User aUser) {
		User userFromDao = new User();
		userFromDao  = getUserFromDao(aUser.getUserName());
		
		//进行用户名存在检验
		if( userFromDao != null ) {
			return -5;  //用户已存在
		}
		return 5;
	}
	
	@Override
	public User getUserFromDao(String aUserName) {
		
		AccountUserDao aDao = new AccountUserDao();
		User findUser = new User();
		findUser = aDao.findUserByName(aUserName);
		
		if( findUser == null ) {
			return null;
		}
		return findUser;
	}
	
	@Override
	public String JM(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String k = new String(a);
		return k;
	}
	
	@Override
	public boolean checkLogin(String aUserName) {
		 for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
	            User userRef = i.next(); 
	            if( userRef.getUserName().equals(aUserName) ) {
	            	return true;
	            }
	        }   
		return false;
	}
		
	@Override
	public List<Course> getUserCourseList(String aUserName) {
		for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUserName) ) {
            	return userRef.getCourseList();
            }
        }  
		return null;
	}
	
	@Override
	public int refreshCourse(User aUser,Course aCourse) {
		
		//更新/添加一科成绩,此时该用户在serviceList中且loginRole已为登录状态
		for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUser.getUserName()) ) {
            	return userRef.setCourse(aCourse.getCourseName(), aCourse.getCourseScore());
            }
        }	
		
		return -88;  //未登录
	}
	
	@Override
	public List<Course> getCourseScore(String aUserName,String aCourseName) {

		//更新/添加一科成绩,此时该用户在serviceList中且loginRole已为登录状态
		for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUserName) ) {
            	//检测是否无该课程信息 (userRef.getScore(aCourseName) (== -10 未登录(一般不会存在) || (== -9 无该课程) || == 一个大于零的float数)) 
            	if( userRef.getScore(aCourseName) < 0 ) {   
            		return null;
            	}
            	List<Course> aCourseList = new ArrayList<Course>();
            	Course aCourse = new Course(aCourseName,userRef.getScore(aCourseName));
            	aCourseList.add(aCourse);
            	return aCourseList;
            }
        }	
		return null;  //用户未登录，不能获得课程信息
	}
	
	@Override
	public int deleteCourse(User aUser, String aCourseName) {
		
		//更新/添加一科成绩,此时该用户在serviceList中且loginRole已为登录状态
		for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUser.getUserName()) ) {
            	return userRef.deleteCourse(aCourseName);
            }
        }	
		
		return -88;  //用户未登录，违法修改成绩
	}
	
	@Override
	public int userLoginOut(User aUser) {
		//将Service层缓存的用户信息写入DAO层
		//取出Service层缓存数据
		for (Iterator<User> i = AccountUserServiceImplementation.serviceList.iterator(); i.hasNext();) {
            User userRef = i.next(); 
            if( userRef.getUserName().equals(aUser.getUserName()) ) {
            	
            	//将Service层缓存（更新后的）用户密码还原为MD5后（暂时无意义，留作扩展，以后可能有修改密码功能）
            	String encryptedPwd = new String(userRef.getUserPwd());
            	userRef.setUserPwd(JM(encryptedPwd));
            	
            	//同步当前用户信息到DAO
            	AccountUserDao aDao = new AccountUserDao();
        		if ( aDao.SyncUserInfo(userRef) ) {
        			AccountUserServiceImplementation.serviceList.remove(userRef);
        			return 50;  //安全退出
        		}
        		AccountUserServiceImplementation.serviceList.remove(userRef);
        		break;
            }
        }	
		
		return -50; //退出时遇到问题（用户已经退出或DAO层数据同步遇到错误） 
	}

}
