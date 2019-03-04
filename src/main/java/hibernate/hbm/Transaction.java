package hibernate.hbm;

/**
 * Transaction entity. @author MyEclipse Persistence Tools
 */

public class Transaction implements java.io.Serializable {

	// Fields

	private Integer id;
	private String userid;
	private String address;
	private Integer asset;
	private Integer chainType;
	private String contract;
	private String userTransactionId;
	private String depositAmount;
	private String feeTransactionId;
	private Integer feeAsset;
	private String feeAmount;
	private String approveTransactionId;
	private String payTransactionId;
	private String payAmount;
	private String txId;
	private Integer status;
	private Long createAt;
	private Long updateAt;
	private Boolean isdelete;
	private Boolean depositOrWithdraw;
	private String fromAddress;
	private String toAddress;
	private Integer pixiuId;
	// Constructors

	/** default constructor */
	public Transaction() {
	}

	/** minimal constructor */
	public Transaction(String userid, String address, Integer asset, Integer chainType, String contract,
			String userTransactionId, String depositAmount, String feeTransactionId, Integer feeAsset, String feeAmount,
			String approveTransactionId, String payTransactionId, String payAmount, Boolean isdelete) {
		this.userid = userid;
		this.address = address;
		this.asset = asset;
		this.chainType = chainType;
		this.contract = contract;
		this.userTransactionId = userTransactionId;
		this.depositAmount = depositAmount;
		this.feeTransactionId = feeTransactionId;
		this.feeAsset = feeAsset;
		this.feeAmount = feeAmount;
		this.approveTransactionId = approveTransactionId;
		this.payTransactionId = payTransactionId;
		this.payAmount = payAmount;
		this.isdelete = isdelete;
	}

	/** full constructor */
	public Transaction(String userid, String address, Integer asset, Integer chainType, String contract,
			String userTransactionId, String depositAmount, String feeTransactionId, Integer feeAsset, String feeAmount,
			String approveTransactionId, String payTransactionId, String payAmount, Integer status, Long createAt,
			Long updateAt, Boolean isdelete) {
		this.userid = userid;
		this.address = address;
		this.asset = asset;
		this.chainType = chainType;
		this.contract = contract;
		this.userTransactionId = userTransactionId;
		this.depositAmount = depositAmount;
		this.feeTransactionId = feeTransactionId;
		this.feeAsset = feeAsset;
		this.feeAmount = feeAmount;
		this.approveTransactionId = approveTransactionId;
		this.payTransactionId = payTransactionId;
		this.payAmount = payAmount;
		this.status = status;
		this.createAt = createAt;
		this.updateAt = updateAt;
		this.isdelete = isdelete;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAsset() {
		return this.asset;
	}

	public void setAsset(Integer asset) {
		this.asset = asset;
	}

	public Integer getChainType() {
		return this.chainType;
	}

	public void setChainType(Integer chainType) {
		this.chainType = chainType;
	}

	public String getContract() {
		return this.contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getUserTransactionId() {
		return this.userTransactionId;
	}

	public void setUserTransactionId(String userTransactionId) {
		this.userTransactionId = userTransactionId;
	}

	public String getDepositAmount() {
		return this.depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getFeeTransactionId() {
		return this.feeTransactionId;
	}

	public void setFeeTransactionId(String feeTransactionId) {
		this.feeTransactionId = feeTransactionId;
	}

	public Integer getFeeAsset() {
		return this.feeAsset;
	}

	public void setFeeAsset(Integer feeAsset) {
		this.feeAsset = feeAsset;
	}

	public String getFeeAmount() {
		return this.feeAmount;
	}

	public void setFeeAmount(String feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getApproveTransactionId() {
		return this.approveTransactionId;
	}

	public void setApproveTransactionId(String approveTransactionId) {
		this.approveTransactionId = approveTransactionId;
	}

	public String getPayTransactionId() {
		return this.payTransactionId;
	}

	public void setPayTransactionId(String payTransactionId) {
		this.payTransactionId = payTransactionId;
	}

	public String getPayAmount() {
		return this.payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getCreateAt() {
		return this.createAt;
	}

	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}

	public Long getUpdateAt() {
		return this.updateAt;
	}

	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}

	public Boolean getIsdelete() {
		return this.isdelete;
	}

	public void setIsdelete(Boolean isdelete) {
		this.isdelete = isdelete;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public Boolean getDepositOrWithdraw() {
		return depositOrWithdraw;
	}

	public void setDepositOrWithdraw(Boolean depositOrWithdraw) {
		this.depositOrWithdraw = depositOrWithdraw;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public Integer getPixiuId() {
		return pixiuId;
	}

	public void setPixiuId(Integer pixiuId) {
		this.pixiuId = pixiuId;
	}

}