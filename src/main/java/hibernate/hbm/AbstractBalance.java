package hibernate.hbm;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractBalance implements java.io.Serializable {

	private String id;

	private String userId;

	private double change;

	private String type;

	private String assetType;

	private double balance;

	private String addressId;

	private String address;

	private String addressName;

	private String cmd;

	private String status;

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

	public double getChange() {
		return this.change;
	};

	public void setChange(double change) {
		this.change = change;
	}

	public String getType() {
		return this.type;
	};

	public void setType(String type) {
		this.type = type;
	}

	public String getAssetType() {
		return this.assetType;
	};

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public double getBalance() {
		return this.balance;
	};

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAddressId() {
		return this.addressId;
	};

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getAddress() {
		return this.address;
	};

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressName() {
		return this.addressName;
	};

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getCmd() {
		return this.cmd;
	};

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getStatus() {
		return this.status;
	};

	public void setStatus(String status) {
		this.status = status;
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

}