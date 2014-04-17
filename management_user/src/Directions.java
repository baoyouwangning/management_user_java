import java.util.List;

import management_user.bean.User;


public class Directions {

	/*
	                 这里做个设计思路的小描述
	    0.假设AccountUserView是个V层，其中的System.out.print() 是用户可以直接看到的东西
	      而AccountUserController、AccountUserServiceImplementation、AccountUserDao则是C层和M层，
	      其中的System.out.print(); 则是写log信息，用户不能直接看到。
	    1.当一个用户进行注册时，在从V层向C层和M层传递用户信息时，由于需经过公网传输，防止被截获破解用户信息，
	      在V层进行的处理有：
	      				 1>暂只将用户密码，进行了MD5加密和一个简单的可逆加密。
	      				 2>对用户名和密码进行了正则匹配校验，保证用户名和密码符合大众习惯
	      				 3>正则匹配过程在客户端（V层）进行，而不再服务端进行的好处不提了
	      此时，V层给C层传来一个相对安全且符合标准的User对象。C层将注册申请传给Service,
	      Service先想数据传入Dao判断用户是否已被注册，若没有则再将用户添加到Dao。
	      Dao中存储的用户名密码是明文密码MD5以后的值。（感觉不太合理，但如果Dao被恶意盗取，起码也不能一下看出用户密码）
	      Service返回反馈码给C，C调用V相应函数显示提示信息。
	    2.当一个用户进行登录时，V层不对用户名和密码做正则匹配（防止对恶意登录泄漏部分规则信息），只对密码进行加密传给C，C给Service，
	      Service去Dao找用户，存在则将Dao中整个用户信息放入Service层缓冲用户列表中，
	      并给用户可以增删改查成绩权限，实现方案:
	                                      1>给用户类一个私有属性作是否登录标记
	                                      2>登录标记设定函数分别在登录和登出时在Service层更改
	                                      3>登录标记设定函数原型为
	                                      public boolean setLoginRole (List<User> aServiceList);
	                                      4>aServiceList 是由Service层传入缓存用户列表
	                                      5>当user检测到自己在登录列表中时则将自己设为登录状态，否则为非登录状态
	                                      6>当用户为登录状态时则就有了修改成绩的权限
		3.在用户登录状态时，对课程信息的增删改查操作的均为Service层的缓存用户
		4.当一个用户退出时，则将Service层该用户登录状态更改的缓存信息写入Dao后用户安全退出！
	 */
}
