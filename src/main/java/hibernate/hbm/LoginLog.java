package hibernate.hbm;

/**
 * Users entity. @author MyEclipse Persistence Tools
 */
public class LoginLog extends AbstractLoginLog implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public LoginLog() {
	}

	/** full constructor */
	public LoginLog(int id, String email,String loginPwd,String ip,int time,String status) {
		super(id, email,loginPwd,ip,time,status);
	}

}
