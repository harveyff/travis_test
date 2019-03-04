package hibernate.hbm;

/**
 * Transaction entity. @author MyEclipse Persistence Tools
 */

public class WithdrawApply implements java.io.Serializable {

	// Fields

	private String id;
	private String userId;
	private String bttUserId;
	private Integer asset;
	private String address;
	private double amount;
	private String status;
	private String message;
	private Long createAt;
	private Long updateAt;
	private String content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getAsset() {
		return asset;
	}
	public void setAsset(Integer asset) {
		this.asset = asset;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}
	public Long getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getBttUserId() {
		return bttUserId;
	}
	public void setBttUserId(String bttUserId) {
		this.bttUserId = bttUserId;
	}
}