package manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hibernate.HibernateUtil;
import hibernate.hbm.Transaction;
import hibernate.hbm.Users;
import utils.Utils;

public class DepositManager {
	public static Logger log4j = LogManager.getLogger(DepositManager.class);
	public static Map<Integer, String> StatusMap=new HashMap<Integer, String>();
	public static void init(){
		StatusMap.put(1, "DEPOSIT_FAILED");
		StatusMap.put(2, "FEE_SEND_FAILED");
		StatusMap.put(3, "FEE_EXECUED");
		StatusMap.put(4, "FEE_FAILED");
		StatusMap.put(5, "PAY_EXECUED");
		StatusMap.put(6, "PAY_SEND_FAILED");
		StatusMap.put(7, "PAY_FAILED");
		StatusMap.put(8, "PAY_SUCCED");
		StatusMap.put(9, "FAILED");
		StatusMap.put(10, "SUCCED");
		StatusMap.put(11, "EXECUTED");
		StatusMap.put(12, "FAILED");
		StatusMap.put(13, "SUCCED");
		StatusMap.put(998, "审核中");
		StatusMap.put(999, "审核失败");
	}
	public static JSONObject list(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		Users user=UsersManager.getUserByEmail(email);
		int offset = Utils.getInt(args, "offset", 0);
		int size =  Utils.getInt(args, "size", 50);
		JSONObject res = new JSONObject();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Transaction> crq = crb.createQuery(Transaction.class);
			Root<Transaction> root = crq.from(Transaction.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userid"), user.getBttUserid()));
			predicates.add(crb.equal(root.get("depositOrWithdraw"), true));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			crq.orderBy(crb.desc(root.get("createAt")));
			List<Transaction> list = s.createQuery(crq).setFirstResult(offset).setMaxResults(size).getResultList();
			JSONArray result = new JSONArray();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranTransToJSON(list.get(i)));
				}
			}
			res.put("result", result);
			res.put("code", 0);
		} catch (Exception e) {
			res.put("code", 100000);
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		
		return res;
	}
	public static JSONObject depositNotify(JSONObject args) {
		JSONObject res = new JSONObject();
		log4j.info("args:" + args.toJSONString());
		JSONObject deposit = JSON.parseObject(args.getString("deposit"));
		Transaction trans = getTransByUserPixiuId(deposit.getInteger("id"));
		boolean isSave = false;
		if (trans == null) {
			isSave = true;
			trans = new Transaction();
			trans.setCreateAt(deposit.getLong("createAt"));
			trans.setIsdelete(deposit.getBoolean("isdelete"));
			trans.setDepositOrWithdraw(deposit.getBoolean("deposit_or_withdraw"));
			trans.setFromAddress(deposit.getString("from_address"));
			trans.setToAddress(deposit.getString("to_address"));
			trans.setAsset(deposit.getInteger("asset"));
			trans.setPixiuId(deposit.getInteger("id"));
		}
		if(!Utils.isStringEmpty(deposit.getString("address"))){
			trans.setAddress(deposit.getString("address"));
		}
		if(deposit.getInteger("chain_type")!=null){
			trans.setChainType(deposit.getInteger("chain_type"));
		}
		if(!Utils.isStringEmpty(deposit.getString("contract"))){
			trans.setContract(deposit.getString("contract"));
		}
		if(!Utils.isStringEmpty(deposit.getString("userid"))){
			trans.setUserid(deposit.getString("userid"));
		}
		if(!Utils.isStringEmpty(deposit.getString("fee_amount"))){
			trans.setFeeAmount(deposit.getString("fee_amount"));
		}
		if(!Utils.isStringEmpty(deposit.getString("deposit_amount"))){
			trans.setDepositAmount(deposit.getString("deposit_amount"));
		}
		if(!Utils.isStringEmpty(deposit.getString("pay_amount"))){
			trans.setPayAmount(deposit.getString("pay_amount"));
		}
		if(!Utils.isStringEmpty(deposit.getString("pay_transaction_id"))){
			trans.setPayTransactionId(deposit.getString("pay_transaction_id"));
		}
		if(!Utils.isStringEmpty(deposit.getString("approve_transaction_id"))){
			trans.setApproveTransactionId(deposit.getString("approve_transaction_id"));
		}
		if(!Utils.isStringEmpty(deposit.getString("fee_transaction_id"))){
			trans.setFeeTransactionId(deposit.getString("fee_transaction_id"));
		}
		if(!Utils.isStringEmpty(deposit.getString("user_transaction_id"))){
			trans.setUserTransactionId(deposit.getString("user_transaction_id"));
		}
		if(!Utils.isStringEmpty(deposit.getString("btt_transaction_id"))){
			trans.setTxId(deposit.getString("btt_transaction_id"));
		}
		trans.setStatus(deposit.getInteger("status"));
		trans.setUpdateAt(deposit.getLong("updateAt"));
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		if (isSave) {
			s.save(trans);
		} else {
			s.update(trans);
		}
		s.getTransaction().commit();
		s.close();
		return res;
	}

	public static Transaction getTransByUserPixiuId(int pixiuId) {
		Transaction trans = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Transaction> crq = crb.createQuery(Transaction.class);
			Root<Transaction> root = crq.from(Transaction.class);
			crq.select(root);
			crq.where(crb.equal(root.get("pixiuId"), pixiuId));
			List<Transaction> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				trans = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}

		return trans;
	}
	
	
	public static JSONObject tranTransToJSON(Transaction trans) {
		JSONObject ob = new JSONObject();
		if (trans != null) {
			
			ob.put("address", trans.getAddress());
			if(Utils.isStringEmpty(trans.getContract())){
				trans.setContract("");
			}
			JSONObject asset=AssetManager.AssetAddressMapNew.get(trans.getContract().toLowerCase()+"_"+trans.getChainType());
			if(asset!=null){
				ob.put("currency", asset.getString("asset"));
			}
			if(trans.getDepositAmount()!=null&&asset!=null){
				ob.put("amount", Utils.parseAmout(trans.getDepositAmount(),asset.getInteger("id")));
			}else{
				ob.put("amount",0);
			}
			ob.put("doneTime", "");
			if(trans.getCreateAt()!=null){
				ob.put("createTime", (int)(trans.getCreateAt()/1000));
			}
			if(trans.getFeeAmount()!=null&&asset!=null){
				ob.put("free", Utils.parseAmout(trans.getFeeAmount(),asset.getInteger("id")));
			}else{
				ob.put("free", 0);
			}
			if(trans.getUpdateAt()!=null){
				ob.put("updateTime", (int)(trans.getUpdateAt()/1000));
			}
			ob.put("feeTransactionId", trans.getFeeTransactionId());
			ob.put("approveTransactionId", trans.getApproveTransactionId());
			ob.put("payTransactionId", trans.getPayTransactionId());
			ob.put("id", trans.getUserTransactionId());
			ob.put("seq", trans.getId());
			ob.put("state", StatusMap.get(trans.getStatus()));
			JSONObject detail = new JSONObject();
			detail.put("cmd", "");
			detail.put("method", "deposit");
			detail.put("status", StatusMap.get(trans.getStatus()));
			detail.put("statusVal", trans.getStatus());
			if(Utils.isStringEmpty(trans.getTxId())){
				trans.setTxId("");
			}
			detail.put("tid", trans.getTxId());
			ob.put("detail", detail);
		}
		return ob;
	}
}
