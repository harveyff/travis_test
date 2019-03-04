package hibernate.hbm;

/**
 * AbstractUsers entity provides the base persistence definition of the Users
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractAddress implements java.io.Serializable {

	private int id;

	private String userId;

	private String addressName;

	private String address;

	private String assetId;

	private double balance;

	private double available;

	private double frozen;

	private String type;

	private String bank;

	private String bankNo;

	private String bankName;

	private String status;

	private String remark;

	private Long createAt;

	private Long updateAt;
	
	private Integer showIndex;

	public int getId() {
		return this.id;
	};

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	};

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAddressName() {
		return this.addressName;
	};

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddress() {
		return this.address;
	};

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAssetId() {
		return this.assetId;
	};

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public double getBalance() {
		return this.balance;
	};

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getAvailable() {
		return this.available;
	};

	public void setAvailable(double available) {
		this.available = available;
	}

	public double getFrozen() {
		return this.frozen;
	};

	public void setFrozen(double frozen) {
		this.frozen = frozen;
	}

	public String getType() {
		return this.type;
	};

	public void setType(String type) {
		this.type = type;
	}

	public String getBank() {
		return this.bank;
	};

	public void setBank(String bank) {
		this.bank = bank;
	}

	

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getBankName() {
		return this.bankName;
	};

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStatus() {
		return this.status;
	};

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return this.remark;
	};

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getCreateAt() {
		return this.createAt;
	};

	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}

	public Long getUpdateAt() {
		return this.updateAt;
	};

	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}

	public Integer getShowIndex() {
		return showIndex;
	}

	public void setShowIndex(Integer showIndex) {
		this.showIndex = showIndex;
	}

}