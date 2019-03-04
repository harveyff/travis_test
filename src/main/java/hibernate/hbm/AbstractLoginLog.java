package hibernate.hbm;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractLoginLog implements java.io.Serializable {

	// Fields

	private int id;
	private String email;
	private String ip;
	private String loginPwd;
	private int time;
	private String status;
	// Constructors

	/** default constructor */
	public AbstractLoginLog() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/** full constructor */
	public AbstractLoginLog(int id, String email,String loginPwd,String ip,int time,String status) {
		this.id = id;
		this.email = email;
		this.loginPwd = loginPwd;
		this.ip=ip;
		this.time=time;
		this.status=status;
	}
	
}