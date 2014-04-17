package management_user.bean;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import management_user.account.view.AccountUserView;

public class User {

	private String userName = new String();
	private String userPwd = new String();
	private List<Course> courseList = new ArrayList<Course>();
	private int loginRole = 0;
	
	public User() {
		this(null, null);
	}
	
	public User(String aName, String aPwd) {
		this.setUserName(aName);
		this.setUserPwd(aPwd);
	}

	public void setUserName(String userName) {	
		this.userName = userName;
	}
	public String getUserName() {
		return this.userName;
	}
	
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserPwd() {
		return this.userPwd;
	}
	
	public boolean setLoginRole (List<User> aServiceList) {
		//在登录列表中根据用户名查找是否已登录,aServiceList 只能在Service得到
//		int j = 0;
		this.loginRole = 0;
		for (Iterator<User> i = aServiceList.iterator(); i.hasNext();) { 
            User userRef = i.next(); 
//            j++;
            if( userRef.getUserName().equals(this.userName) ) {
            	this.loginRole = 1;
            	
//            	//如果是管理员
//            	if( j == 1 ) {
//            		this.loginRole = 2;
//            	}
            	return true;
            }
        }   
		return false;
	}
	
	public int setCourse(String courseName,float courseScore) {
		
		//未登录
		if( 0 == loginRole) {
			return -88; //设置课程时未登录
		}
		
		//冲刷该课程已有成绩
		for (Iterator<Course> i = this.courseList.iterator(); i.hasNext();) { 
            Course courseRef = i.next(); 
            if( courseRef.getCourseName().equals(courseName) ) {
            	courseRef.setCourseScore(courseScore);
            	return 88; //更新课程成功
            }
		}
        
        //没有该成绩
		Course aTempCourse = new Course(courseName,courseScore); 
		this.courseList.add(aTempCourse);     
		return 89;  //添加课程成功
	}
	
	public float getScore(String courseName) {
		//未登录
		if( 0 == loginRole) {
			return -10;  //获取课程成绩 未登录错误
		}
		
		//找到该成绩
		for (Iterator<Course> i = this.courseList.iterator(); i.hasNext();) { 
            Course courseRef = i.next(); 
            if( courseRef.getCourseName().equals(courseName) ) {
            	return courseRef.getCourseScore();
            }
		}
		
		//未找到
		return -9; //未找到成绩错误码
	}
	
	public int deleteCourse(String aCourseName) {
		//未登录
		if( 0 == loginRole) {
			return -88;  //获取课程成绩 未登录错误
		}
		
		//找到改课程
		for (Iterator<Course> i = this.courseList.iterator(); i.hasNext();) { 
            Course courseRef = i.next(); 
            if( courseRef.getCourseName().equals(aCourseName) ) {
            	this.courseList.remove(courseRef);
            	return 60;
            }
		}
		
		return -60;  //删除失败
	}
	public List<Course> getCourseList() {
		//未登录
		if( 0 == loginRole || this.courseList.size() < 1) {
			return null;  //获取课程成绩未登录错误码
		}
		return this.courseList;
	}
	public boolean regexUserName() {
		//对用户名正则匹配
		/*
		 * 用户名要求：
		 * 			 1. 4 <= 长度 <=15
		 * 			 2. 由字母或数字组成
		 */
		String aUserName = new String(this.userName);
		
		// 检验用户名长度
		if (aUserName.length() < 6 || aUserName.length() > 15) {
			connectView("用户名长度不符合标准！4-15");
			return false;
		}

		// 检验用户名是否由字母或数字组成
		if (!(aUserName.matches("[a-zA-Z0-9]+"))) {
			connectView("用户名必须由字母、数字组成！");
			return false;
		}
		return true;
	}
	
	public boolean regexUserPwd () {
		//对用户密码正则匹配
		/*
		 * 用户密码要求:
		 * 			  1. 6 <= 长度 <= 16
		 * 			  2.必须由字母和数字组成
		 * 			  3.要求大小写字母都有，且必须有数字
		 * 
		 */
		String aPassword = new String(this.userPwd);
		
		if( !(aPassword.length() >= 6 && aPassword.length() <= 16) ) {
			connectView("密码长度不符标准!6-16");
			return false;
		}
		else if ( !(aPassword.matches("^\\w+$")) ) {
			connectView("密码必须由字母和数字组成!");
			return false;
		}	
		else {
			Pattern p1 = Pattern.compile("[a-z]+");
			Pattern p2 = Pattern.compile("[0-9]+");
			Pattern p3 = Pattern.compile("[A-Z]+");
			Matcher m = p1.matcher(aPassword);
			if (!m.find()) {
				connectView("密码中必须含有小写字母!");
				return false;
			}
			else {
				m.reset().usePattern(p2);
				if (!m.find()) {
					connectView("密码中必须含有数字!");
					return false;
				}
				else {
					m.reset().usePattern(p3);
					if (!m.find()) {
						connectView("密码中必须含有大写字母!");
						return false;
					}
				}
			}
		}
		return true;
	}

	//将userPwd更新为MD5加密后再可逆加密一次
	public boolean md5AndKlForPwd() {		
		this.setUserPwd(this.KL(this.MD5(this.getUserPwd())));
		return true;
	}
	
	/*
	 * md5加密 与 一个可逆加密，暂时用一下，还没很理解原理
	 * 加密算法来源:
	 * http://blog.csdn.net/binyao02123202/article/details/6247871
	 */	
	//MD5加密
	public String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			connectView(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	//可逆的加密算法
	public String KL(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;
	}
	
	//与V层通信
	public void connectView(String aStr) {
		AccountUserView aView = new AccountUserView();
		aView.displayInfo(aStr);
		aView = null;
	}
}
