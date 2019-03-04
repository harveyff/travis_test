package hibernate.hbm;

/**
 * Users entity. @author MyEclipse Persistence Tools
 */
public class Users extends AbstractUsers implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Users() {
	}

	/** minimal constructor */
	public Users(int id) {
		super(id);
	}

	/** full constructor */
	public Users(int id, String email,String loginPwd,String capitalPwd,String status) {
		super(id, email,loginPwd,capitalPwd,status);
	}

}
