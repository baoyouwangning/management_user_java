package management_user.account.service;

import java.util.List;

import management_user.bean.Course;
import management_user.bean.User;

public interface AccountUserService {
	
	//登录
	public int userLogin(User aUser);
	
	//注册
	public int userSignUp(User aUser);

	//检查
	public int userCheck(User aUser);
	
	//从dao中找出C层传过来的用户信息
	public User getUserFromDao(String aUserName);
	
	//解密为MD5后字串
	public String JM(String inStr);
	
	//检查用户是否已登录
	public boolean checkLogin(String aUserName);
	
	//根据user name 从Service层的在线用户列表中得到该用户课程列表的引用
	public List<Course> getUserCourseList(String aUserName);
	
	//更新/添加新课程
	public int refreshCourse(User aUser,Course aCourse);
	
	//获取一个课程信息
	public List<Course> getCourseScore(String aUserName,String aCourseName);
	
	//删除一个课程
	public int deleteCourse(User aUser,String aCourseName);
	
	//用户登出，并将Server层的修改的Course信息，更新至DAO
	public int userLoginOut(User aUser);
}
