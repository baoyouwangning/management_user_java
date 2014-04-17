package management_user.account.controller;

import java.util.ArrayList;
import java.util.List;

import management_user.account.service.AccountUserServiceImplementation;
import management_user.account.view.AccountUserView;
import management_user.bean.Course;
import management_user.bean.User;

public class AccountUserController {
	public AccountUserController() {
		//nothing now
	}
		
	public void OperController(int aChoose,User aUser) {
		
		AccountUserServiceImplementation aService = new AccountUserServiceImplementation();
		
		int feedBackCode = 0; //M层返回反馈码
		
		switch(aChoose) {
		case 1: {
			/* 登录 */
			
			feedBackCode = aService.userLogin(aUser);
			break;
		}
		case 2: {
			/* 注册 */
			
			feedBackCode = aService.userSignUp(aUser);			
			break;
		}
		case 3: {
			/* 检查 */
			
			feedBackCode = aService.userCheck(aUser);
			break;
		}
		default: {
			System.exit(1);
		}
		}
		
		//把M层反馈码，传向V层
		AccountUserView aView = new AccountUserView();
		aView.displayFeedBack(feedBackCode, aUser.getUserName());
		Course aCourse = new Course();
		
		//当 feedBackCode == 1 (当前用户登录成功) 列出用户课程操作
		while (true) {
			if( feedBackCode == 1) {
				int courseChoose = aView.displayCourseMenu(aUser.getUserName());
				int courseFeedBackCode = 0;
				String aCourseName = new String();
				List<Course> feedBackCourseList = new ArrayList<Course>();
				
				switch(courseChoose) {
				case 1: {
					/* 显示所有课程 */
					feedBackCourseList = aService.getUserCourseList(aUser.getUserName());
					aView.displayCourseList(feedBackCourseList);
					break;
				}
				case 2: {
					/* 增加更新课程 */
					aCourse = aView.receiveCourse();
					courseFeedBackCode = aService.refreshCourse(aUser,aCourse); 
					aView.displayCourseFeedBack(courseFeedBackCode,aUser.getUserName());
					break;
				}
				case 3: {
					/* 查询课程 */
					aCourseName = aView.receiveCourseName();
					feedBackCourseList = aService.getCourseScore(aUser.getUserName(),aCourseName);
					aView.displayCourseList(feedBackCourseList);
					break;
				}
				case 4: {
					/* 删除课程 */
					aCourseName = aView.receiveCourseName();
					courseFeedBackCode = aService.deleteCourse(aUser,aCourseName);
					aView.displayCourseFeedBack(courseFeedBackCode,aUser.getUserName());
					break;
				}
				case 5: {
					/* 退出登录 */
					feedBackCode = aService.userLoginOut(aUser);
					aView.displayFeedBack(feedBackCode, aUser.getUserName());
					break;
				}
				default: {
					System.exit(1);
				}
				}  //switch course
			}  //if( feedBackCode == 1)
			else {
				break;
			}
		}//while(true)
		
	} //swith user
}
