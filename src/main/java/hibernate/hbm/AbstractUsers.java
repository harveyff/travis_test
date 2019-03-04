package hibernate.hbm;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractUsers implements java.io.Serializable {

	// Fields

	private int id;
	private String email;
	private String loginPwd;
	private String capitalPwd;
	private String bttUserid;
	private String bttPrivateId;
	private Long createAt;
	private Long updateAt;
	private String status;
	private String mobile;
	private String googleAuthSecret;
	private String googleAuthUrl;
	private String kycStatus;
	private String bankBindStatus;
	private String googleAuthStatus;
	// Constructors

	/** default constructor */
	public AbstractUsers() {
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

	/** minimal constructor */
	public AbstractUsers(int id) {
		this.id = id;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getCapitalPwd() {
		return capitalPwd;
	}

	public void setCapitalPwd(String capitalPwd) {
		this.capitalPwd = capitalPwd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBttUserid() {
		return bttUserid;
	}

	public void setBttUserid(String bttUserid) {
		this.bttUserid = bttUserid;
	}

	public Long getCreateAt() {
		return createAt;
	}

	public Long getUpdateAt() {
		return updateAt;
	}

	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}

	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}

	public String getBttPrivateId() {
		return bttPrivateId;
	}

	public void setBttPrivateId(String bttPrivateId) {
		this.bttPrivateId = bttPrivateId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGoogleAuthSecret() {
		return googleAuthSecret;
	}

	public void setGoogleAuthSecret(String googleAuthSecret) {
		this.googleAuthSecret = googleAuthSecret;
	}

	public String getGoogleAuthUrl() {
		return googleAuthUrl;
	}

	public void setGoogleAuthUrl(String googleAuthUrl) {
		this.googleAuthUrl = googleAuthUrl;
	}

	/** full constructor */
	public AbstractUsers(int id, String email, String loginPwd, String capitalPwd, String status) {
		this.id = id;
		this.email = email;
		this.loginPwd = loginPwd;
		this.capitalPwd = capitalPwd;
		this.status = status;
	}

	public String getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}

	public String getBankBindStatus() {
		return bankBindStatus;
	}

	public void setBankBindStatus(String bankBindStatus) {
		this.bankBindStatus = bankBindStatus;
	}

	public String getGoogleAuthStatus() {
		return googleAuthStatus;
	}

	public void setGoogleAuthStatus(String googleAuthStatus) {
		this.googleAuthStatus = googleAuthStatus;
	}

}