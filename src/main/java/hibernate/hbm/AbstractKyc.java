package hibernate.hbm;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractKyc implements java.io.Serializable {

	private String id;

	private String userId;
	private String bttUserId;
	private String name;

	private String idno;

	private String idPhoneFront;

	private String idPhoneReverse;

	private String status;

	private String message;

	private Integer createAt;

	private Integer updateAt;

	public String getId() {
		return this.id;
	};

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	};

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return this.name;
	};

	public void setName(String name) {
		this.name = name;
	}

	public String getIdno() {
		return this.idno;
	};

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getIdPhoneFront() {
		return this.idPhoneFront;
	};

	public void setIdPhoneFront(String idPhoneFront) {
		this.idPhoneFront = idPhoneFront;
	}

	public String getIdPhoneReverse() {
		return this.idPhoneReverse;
	};

	public void setIdPhoneReverse(String idPhoneReverse) {
		this.idPhoneReverse = idPhoneReverse;
	}

	public String getStatus() {
		return this.status;
	};

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return this.message;
	};

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCreateAt() {
		return this.createAt;
	};

	public void setCreateAt(Integer createAt) {
		this.createAt = createAt;
	}

	public Integer getUpdateAt() {
		return this.updateAt;
	};

	public void setUpdateAt(Integer updateAt) {
		this.updateAt = updateAt;
	}

	public String getBttUserId() {
		return bttUserId;
	}

	public void setBttUserId(String bttUserId) {
		this.bttUserId = bttUserId;
	}

	
	
}