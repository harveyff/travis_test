package manager;

import java.math.BigInteger;
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
import org.bitcoinj.core.ECKey;
import org.hibernate.Session;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.Chains;
import com.bytetrade.pro.bytemodule.chain.OpWrapper;
import com.bytetrade.pro.bytemodule.chain.Optional;
import com.bytetrade.pro.bytemodule.chain.Ripemd160;
import com.bytetrade.pro.bytemodule.chain.TimeSpec;
import com.bytetrade.pro.bytemodule.chain.Transaction;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.UINT32;
import com.bytetrade.pro.bytemodule.chain.UINT8;
import com.bytetrade.pro.bytemodule.chain.Util;
import com.bytetrade.pro.bytemodule.chain.operations.ProposeOperation;
import com.bytetrade.pro.bytemodule.chain.operations.Withdraw2Operation;
import com.bytetrade.pro.bytemodule.chain.operations.WithdrawOperation;

import google.authenticator.util.GoogleAuthenticatorUtils;
import hibernate.HibernateUtil;
import hibernate.hbm.Address;
import hibernate.hbm.Users;
import hibernate.hbm.WithdrawApply;
import utils.DesUtil;
import utils.HttpRequest;
import utils.Utils;

public class WithdrawManager {
	public static Logger log4j = LogManager.getLogger(UsersManager.class);
	public static Map<Integer, String> StatusMap = new HashMap<Integer, String>();
	public static String PixiuWithdrawUrl = "https://pixiu.bytetrade.io/api/v1?cmd=withdrawNotify";
	public static String PixiuWithdrawUrlBTC = "https://pixiu.bytetrade.io/api/v1?cmd=withdrawNotify_btc";

	/**
	 * BTC 提现
	 * 
	 * @param bttUserid
	 * @param bttPrivateId
	 * @param assetId
	 * @param amount
	 * @param pixiuAddress
	 * @param toExternalAddress
	 * @param chain_type
	 * @return
	 */
	public static JSONObject doWithdrawBtc(String bttUserid, String bttPrivateId, int assetId, BigInteger amount,
			 String toExternalAddress, Integer chain_type) {
		log4j.info(bttUserid + "," + bttPrivateId + "," + assetId + "," + amount + "," 
				+ toExternalAddress);
		
		JSONObject res = new JSONObject();
		Withdraw2Operation withdrawOperation = new Withdraw2Operation();
		withdrawOperation.fee = Bytetrade.getPackFee();
		withdrawOperation.from = new AccountID(bttUserid);
		withdrawOperation.toExternalAddress = toExternalAddress;
		withdrawOperation.assetType = new UINT32(new BigInteger("" + assetId));
		withdrawOperation.amount = new UINT128(amount);
		withdrawOperation.asset_fee = new Optional<UINT128>(new UINT128(new BigInteger("5000")));

		ArrayList<BaseOperation> operations = new ArrayList<BaseOperation>();
		operations.add(withdrawOperation);

		Transaction transaction = new Transaction(Util.hexToBytes(Chains.CHAIN_ID), operations);
		transaction.timestamp = TimeSpec.now();
		transaction.expiration = new Optional<>(new TimeSpec(transaction.timestamp.value + 10));
		// transaction.timestamp = new TimeSpec(0);
		// transaction.expiration = new Optional<>(new TimeSpec(10));
		transaction.validate_type = new UINT8(BigInteger.valueOf(0));
		transaction.dapp = new Optional<AccountID>(new AccountID(Bytetrade.getDapp()));
		transaction.proposal_transaction_id = new Optional<Ripemd160>(null);

		ECKey mPrivateKey = org.bitcoinj.core.ECKey.fromPrivate(Util.hexToBytes(bttPrivateId));
		transaction.signTransaction(mPrivateKey);

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("transaction", transaction.toJsonString());
		paramsMap.put("toExternalAddress", toExternalAddress);
		paramsMap.put("chain_type", chain_type + "");
		paramsMap.put("chainContractAddress",
				AssetManager.AssetIdMap.get(assetId + "").getString("chain_contract_address"));
		log4j.info(paramsMap.toString());
		String resStr = HttpRequest.sendPostUrl(PixiuWithdrawUrlBTC, paramsMap);
		log4j.info("res:" + resStr);
		res = JSONObject.parseObject(resStr);
		return res;
	}

	/**
	 * 
	 * @param bttUserid
	 * @param bttPrivateId
	 * @param assetId
	 * @param amount
	 * @param pixiuAddress
	 * @param toExternalAddress
	 * @return
	 */
	public static JSONObject doWithdraw(String bttUserid, String bttPrivateId, int assetId, BigInteger amount,
			String pixiuAddress, String toExternalAddress, Integer chain_type) {
		log4j.info(bttUserid + "," + bttPrivateId + "," + assetId + "," + amount + "," + pixiuAddress + ","
				+ toExternalAddress);
		JSONObject res = new JSONObject();
		WithdrawOperation withdrawOperation = new WithdrawOperation();
		withdrawOperation.fee = Bytetrade.getPackFee();
		withdrawOperation.from = new AccountID(bttUserid);
		withdrawOperation.toExternalAddress = pixiuAddress;
		withdrawOperation.assetType = new UINT32(new BigInteger("" + assetId));
		withdrawOperation.amount = new UINT128(amount);

		ProposeOperation proposeOperation = new ProposeOperation();
		proposeOperation.fee = Bytetrade.getPackFee();
		proposeOperation.proposaler = new AccountID(bttUserid);
		proposeOperation.proposed_ops.add(new OpWrapper(withdrawOperation));
		proposeOperation.expiration_time = new TimeSpec(0);

		ArrayList<BaseOperation> operations = new ArrayList<BaseOperation>();
		operations.add(proposeOperation);

		Transaction transaction = new Transaction(Util.hexToBytes(Chains.CHAIN_ID), operations);
		transaction.timestamp = TimeSpec.now();
		transaction.expiration = new Optional<>(new TimeSpec(transaction.timestamp.value + 10));
		// transaction.timestamp = new TimeSpec(0);
		// transaction.expiration = new Optional<>(new TimeSpec(10));
		transaction.validate_type = new UINT8(BigInteger.valueOf(0));
		transaction.dapp = new Optional<AccountID>(new AccountID(Bytetrade.getDapp()));
		transaction.proposal_transaction_id = new Optional<Ripemd160>(null);

		ECKey mPrivateKey = org.bitcoinj.core.ECKey.fromPrivate(Util.hexToBytes(bttPrivateId));
		transaction.signTransaction(mPrivateKey);

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("transaction", transaction.toJsonString());
		paramsMap.put("toExternalAddress", toExternalAddress);
		paramsMap.put("chain_type", chain_type + "");
		paramsMap.put("chainContractAddress",
				AssetManager.AssetIdMap.get(assetId + "").getString("chain_contract_address"));
		String resStr = HttpRequest.sendPostUrl(PixiuWithdrawUrl, paramsMap);
		log4j.info("res:" + resStr);
		res = JSONObject.parseObject(resStr);
		return res;
	}

	public static JSONObject putTransaction(JSONObject args) {
		log4j.info(new Date() + ",putTransaction:" + args.toJSONString());
		// 请求签名
		String email = Utils.getString(args, "email", "");
		String method = Utils.getString(args, "method", "");
		String tr_object = Utils.getString(args, "trObject", "");

		Users user = UsersManager.getUserByEmail(email);
		JSONObject res = new JSONObject();
		if (user == null) {
			res.put("code", 100010);
		} else {
			// JSONObject signRes = sign(user.getBttUserid(),
			// user.getBttPrivateId(), method, tr_object);

			ECKey mPrivateKey = org.bitcoinj.core.ECKey
					.fromPrivate(Util.hexToBytes(DesUtil.decrypt(user.getBttPrivateId())));
			Transaction t = new Transaction(new org.json.JSONObject(tr_object));
			t.signTransaction(mPrivateKey);
			org.json.JSONObject o = t.toJsonObject();
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("cmd", "putTransaction");
			paramsMap.put("method", method);
			paramsMap.put("trObject", o.toString());
			String resultStr = HttpRequest.sendPost("0", paramsMap);
			res = JSONObject.parseObject(resultStr);
		}
		return res;
	}
	public static double getUserBalance(String userid, int assetId) {
		double balance = 0.0;
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("cmd", "listAccounts");
		paramsMap.put("userid", userid);
		paramsMap.put("asset", assetId+"");
		paramsMap.put("channel", "all");
		JSONObject ress = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
		JSONArray balances = ress.getJSONArray("balances");
		if (balances.size() > 0) {
			balance = Double.parseDouble(((JSONObject) balances.get(0)).getString("available"));
		}
		return balance;
	}

	public static JSONObject withdrawCancel(JSONObject args) {
		JSONObject res = new JSONObject();
		String id = Utils.getString(args, "id", "");
		String email = Utils.getString(args, "email", "");
		if (Utils.isStringEmpty(id)) {
			res.put("code", 100001);
			return res;
		}
		WithdrawApply withdrawApply = getWithdrawApplyById(id);
		if (withdrawApply == null) {
			res.put("code", 100011);
			return res;
		}
		if (!withdrawApply.getUserId().equals(email)) {
			res.put("code", 100003);
			return res;
		}
		if (!withdrawApply.getStatus().equals("1")) {
			res.put("code", 100016);
			return res;
		}
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.delete(withdrawApply);
		s.getTransaction().commit();
		s.close();
		res.put("code", 0);
		return res;
	}

	public static JSONObject withdrawApply(JSONObject args) {
		log4j.info("withdrawApply,args:" + args.toJSONString());
		JSONObject res = new JSONObject();
		String email = Utils.getString(args, "email", "");
		String tokenAddress = Utils.getString(args, "tokenAddress", "");
		String capitalPwd = Utils.getString(args, "capitalPwd", "");
		String googleAuthCode = Utils.getString(args, "googleAuthCode", "");
		int assetId = Utils.getInt(args, "assetId", -1);
		double amount = Utils.getDouble(args, "amount");
		if (assetId == -1 || amount <= 0 || Utils.isStringEmpty(tokenAddress) || Utils.isStringEmpty(capitalPwd)
				|| Utils.isStringEmpty(googleAuthCode)) {
			res.put("code", 100001);
			return res;
		}
		Users user = UsersManager.getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		if (!user.getCapitalPwd().equals(DesUtil.MD5(capitalPwd))) {
			res.put("code", 200300);
			return res;
		}
		boolean success = GoogleAuthenticatorUtils.verify(user.getGoogleAuthSecret(), googleAuthCode);
		if (success != true) {
			res.put("code", 100014);
			return res;
		}
		double balance = getUserBalance(user.getBttUserid(), assetId);
		if (amount > balance) {
			log4j.info("balance not enough："+balance);
			res.put("code", 100015);
			return res;
		}
		// 查询冻结中的额度
		double frozenBalance = getFrozenBalance(user.getEmail(), assetId);
		if (amount > (balance - frozenBalance)) {
			log4j.info("balance not enough："+(balance - frozenBalance));
			res.put("code", 100015);
			return res;
		}
		// 写入提现请求
		WithdrawApply withdrawApply = new WithdrawApply();
		withdrawApply.setAsset(assetId);
		withdrawApply.setId(Utils.getUUID());
		withdrawApply.setStatus("1");
		withdrawApply.setAmount(amount);
		withdrawApply.setUserId(email);
		withdrawApply.setBttUserId(user.getBttUserid());
		withdrawApply.setAddress(tokenAddress);
		withdrawApply.setCreateAt(new Date().getTime() / 1000);
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.save(withdrawApply);
		s.getTransaction().commit();
		s.close();
		res.put("code", 0);
		return res;
	}

	/**
	 * 审核提现
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject withdrawApprove(JSONObject args) {
		JSONObject res = new JSONObject();
		String status = Utils.getString(args, "status", "");// 2:待人工审核，3:不通过
		String id = Utils.getString(args, "id", "");
		String message = Utils.getString(args, "message", "");// 不通过原因
		WithdrawApply withdrawApply = getWithdrawApplyById(id);
		if (withdrawApply == null || !withdrawApply.getStatus().equals("1")) {
			res.put("code", 100010);
			return res;
		}
		withdrawApply.setUpdateAt(new Date().getTime() / 1000);
		withdrawApply.setStatus(status);
		if (status.equals("3")) {
			withdrawApply.setMessage(message);
		}
		
		if (status.equals("2")) {
			// 通知提现
			int chain_type = AssetManager.AssetIdMap.get(withdrawApply.getAsset() + "").getInteger("chain_type");
			Users user = UsersManager.getUserByEmail(withdrawApply.getUserId());
			Address address = depositAddress(withdrawApply.getUserId(), withdrawApply.getAsset());
			
			if (chain_type == 2) {
				res = doWithdrawBtc(user.getBttUserid(), DesUtil.decrypt(user.getBttPrivateId()),
						withdrawApply.getAsset(),
						Utils.parseBigIntegerAmout(withdrawApply.getAmount() + "", withdrawApply.getAsset()),
						withdrawApply.getAddress(), chain_type);
			} else {
				res = doWithdraw(user.getBttUserid(), DesUtil.decrypt(user.getBttPrivateId()), withdrawApply.getAsset(),
						Utils.parseBigIntegerAmout(withdrawApply.getAmount() + "", withdrawApply.getAsset()),
						address.getAddress(), withdrawApply.getAddress(), chain_type);
			}
		}else{
			res.put("code", 0);
		}
		if(res.get("code").toString().equals("0")){
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.update(withdrawApply);
			s.getTransaction().commit();
			s.close();
		}
		return res;
	}

	public static JSONObject bankCardBind(JSONObject args) {
		JSONObject res = new JSONObject();
		String type = Utils.getString(args, "type", "");// 类型，1:绑定,0:解绑
		if (!type.equals("0") && !type.equals("1")) {
			res.put("code", 100001);
			return res;
		}
		String email = Utils.getString(args, "email", "");
		String capitalPwd = Utils.getString(args, "capitalPwd", "");
		String code = Utils.getString(args, "code", "");
		Users user = UsersManager.getUserByEmail(email);
		if (!user.getCapitalPwd().equals(DesUtil.MD5(capitalPwd))) {
			res.put("code", 200300);
			return res;
		}
		boolean success = GoogleAuthenticatorUtils.verify(user.getGoogleAuthSecret(), code);
		if (success != true) {
			res.put("code", 100014);
			return res;
		}
		Address address = getAddressByType(email, "1", null);
		if (type.equals("1")) {
			if (address != null) {
				res.put("code", 100010);
				return res;
			}
			String name = Utils.getString(args, "name", "");
			String bankNo = Utils.getString(args, "bankNo", "");
			String bank = Utils.getString(args, "bank", "");
			if (Utils.isStringEmpty(name) || Utils.isStringEmpty(bankNo) || Utils.isStringEmpty(bank)) {
				res.put("code", 100001);
				return res;
			}
			address = new Address();
			address.setType("1");
			address.setStatus("1");
			address.setUserId(email);
			address.setCreateAt(new Date().getTime() / 1000);
			address.setUpdateAt(new Date().getTime() / 1000);
			address.setBankNo(bankNo);
			address.setBankName(name);
			address.setAddressName("Bank");
			address.setAddress("Bank");
			address.setAssetId("0");
			address.setBank(bank);
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.save(address);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);

			user.setBankBindStatus("1");
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.update(user);
			s.getTransaction().commit();
			s.close();

		} else if (type.equals("0")) {
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.delete(address);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);
		}
		return res;
	}

	public static JSONObject tokenAddressBind(JSONObject args) {
		JSONObject res = new JSONObject();
		String type = Utils.getString(args, "type", "");// 类型，1:绑定,0:解绑
		if (!type.equals("0") && !type.equals("1")) {
			res.put("code", 100001);
			return res;
		}
		String email = Utils.getString(args, "email", "");
		String assetId = Utils.getString(args, "assetId", "");
		String capitalPwd = Utils.getString(args, "capitalPwd", "");
		String code = Utils.getString(args, "code", "");
		if (Utils.isStringEmpty(assetId)) {
			res.put("code", 100001);
			return res;
		}
		Users user = UsersManager.getUserByEmail(email);
		if (!user.getCapitalPwd().equals(DesUtil.MD5(capitalPwd))) {
			res.put("code", 200300);
			return res;
		}
		boolean success = GoogleAuthenticatorUtils.verify(user.getGoogleAuthSecret(), code);
		if (success != true) {
			res.put("code", 100014);
			return res;
		}
		Address address = getAddressByType(email, "3", assetId);
		if (type.equals("1")) {
			if (address != null) {
				res.put("code", 100010);
				return res;
			}
			JSONArray allAsset = AssetManager.allAddressList();
			HashMap<String, String> addressMap = new HashMap<String, String>();
			for (int i = 0; i < allAsset.size(); ++i) {
				JSONObject asset = (JSONObject) allAsset.get(i);
				addressMap.put(asset.getInteger("id") + "", asset.getString("asset"));
			}
			String tokenAddress = Utils.getString(args, "tokenAddress", "");
			if (Utils.isStringEmpty(tokenAddress)) {
				res.put("code", 100001);
				return res;
			}
			address = new Address();
			address.setType("3");
			address.setStatus("1");
			address.setUserId(email);
			address.setCreateAt(new Date().getTime() / 1000);
			address.setUpdateAt(new Date().getTime() / 1000);
			address.setAddress(tokenAddress);
			address.setAssetId(assetId);
			address.setAddressName(addressMap.get(assetId));
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.save(address);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);
		} else if (type.equals("0")) {
			if (address == null) {
				res.put("code", 100011);
				return res;
			}
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.delete(address);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);
		}
		return res;
	}

	/**
	 * 提现地址管理
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject listWithdrawsAddress(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		String type = Utils.getString(args, "type", "3");
		if (type.equals("2")) {
			type = "3";
		}
		JSONObject res = new JSONObject();
		res.put("code", 0);
		JSONArray result = new JSONArray();
		Session s = null;
		HashMap<String, String> bindAddressMap = new HashMap<String, String>();
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Address> crq = crb.createQuery(Address.class);
			Root<Address> root = crq.from(Address.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			predicates.add(crb.equal(root.get("type"), type));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			List<Address> list = s.createQuery(crq).getResultList();

			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					JSONObject obj = tranlatAddressToJSON(list.get(i), type);
					bindAddressMap.put(obj.getString("assetId"), obj.getString("assetId"));
					result.add(obj);
				}
			}
			if (!type.equals("1")) {
				JSONArray allAsset = AssetManager.allAddressList();
				JSONArray newAllAsset = new JSONArray();
				for (int i = 0; i < allAsset.size(); ++i) {
					if (!bindAddressMap.containsKey(((JSONObject) allAsset.get(i)).getString("id"))) {
						newAllAsset.add(allAsset.get(i));
					}
				}
				res.put("canBindAssets", newAllAsset);
			}
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
		res.put("result", result);
		return res;
	}

	public static JSONObject listDepositsAddress(JSONObject args) {
		String type = Utils.getString(args, "type", "");// 类型，1:银行卡,2:数字货币
		String email = Utils.getString(args, "email", "");
		JSONObject res = new JSONObject();
		res.put("code", 0);
		JSONArray result = new JSONArray();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Address> crq = crb.createQuery(Address.class);
			Root<Address> root = crq.from(Address.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			if (type.equals("1")) {
				// 银行卡
				predicates.add(crb.equal(root.get("type"), "1"));
			} else if (type.equals("1")) {
				// 数字货币
				predicates.add(crb.equal(root.get("type"), "2"));
			}
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			List<Address> list = s.createQuery(crq).getResultList();

			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranlatAddressToJSON(list.get(i), type));
				}
			}
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
		res.put("result", result);
		return res;
	}

	public static JSONObject tranlatAddressToJSON(Address address, String type) {
		JSONObject ob = new JSONObject();
		if (address != null) {
			if (type.equals("1")) {
				ob.put("bank", address.getBank());
				ob.put("bankNo", address.getBankNo());
				ob.put("bankName", address.getBankName());
			} else if (type.equals("2")) {
				ob.put("address", address.getAddress());
				ob.put("addressName", address.getAddressName());
				ob.put("assetId", address.getAssetId());
			} else if (type.equals("3")) {
				if (!Utils.isStringEmpty(address.getBank())) {
					ob.put("bank", address.getBank());
				}
				if (!Utils.isStringEmpty(address.getBankNo())) {
					ob.put("bankNo", address.getBankNo());
				}
				if (!Utils.isStringEmpty(address.getBankName())) {
					ob.put("bankName", address.getBankName());
				}
				ob.put("address", address.getAddress());
				ob.put("addressName", address.getAddressName());
				ob.put("assetId", address.getAssetId());
			}
			ob.put("id", address.getId());
			ob.put("status", address.getStatus());
			ob.put("createAt", address.getCreateAt());
			ob.put("updateAt", address.getUpdateAt());
		}
		return ob;
	}

	public static Address getAddressByType(String email, String type, String assetId) {
		Address address = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Address> crq = crb.createQuery(Address.class);
			Root<Address> root = crq.from(Address.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			predicates.add(crb.equal(root.get("type"), type));
			if (!Utils.isStringEmpty(assetId)) {
				predicates.add(crb.equal(root.get("assetId"), assetId));
			}
			crq.where(predicates.toArray(new Predicate[predicates.size()]));

			List<Address> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				address = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}

		return address;
	}

	public static WithdrawApply getWithdrawApplyById(String id) {
		WithdrawApply withdrawApply = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<WithdrawApply> crq = crb.createQuery(WithdrawApply.class);
			Root<WithdrawApply> root = crq.from(WithdrawApply.class);
			crq.select(root);
			crq.where(crb.equal(root.get("id"), id));
			List<WithdrawApply> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				withdrawApply = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return withdrawApply;
	}

	public static Double getFrozenBalance(String email, int assetId) {
		Double balance = 0.0;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<WithdrawApply> crq = crb.createQuery(WithdrawApply.class);
			Root<WithdrawApply> root = crq.from(WithdrawApply.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			predicates.add(crb.equal(root.get("asset"), assetId));
			predicates.add(crb.equal(root.get("status"), "1"));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			List<WithdrawApply> list = s.createQuery(crq).setFirstResult(0).setMaxResults(1).getResultList();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					balance += list.get(i).getAmount();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return balance;
	}

	public static Address depositAddress(String email, int assetId) {
		Address address = new Address();
		Session s = null;
		try {
			log4j.info("email:" + email + ",assetId:" + assetId);
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Address> crq = crb.createQuery(Address.class);
			Root<Address> root = crq.from(Address.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			int chain_type = AssetManager.AssetIdMap.get(assetId + "").getInteger("chain_type");
			int asset_id = 0;
			if (chain_type == 1) {
				asset_id = AssetManager.AssetSymbolMap.get("ETH").getInteger("id");
			} else if (chain_type == 2) {
				asset_id = AssetManager.AssetSymbolMap.get("BTC").getInteger("id");
			} else if (chain_type == 3) {
				asset_id = AssetManager.AssetSymbolMap.get("ETH").getInteger("id");
			}
			predicates.add(crb.equal(root.get("assetId"), asset_id));
			predicates.add(crb.equal(root.get("type"), "2"));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			List<Address> list = s.createQuery(crq).setFirstResult(0).setMaxResults(1).getResultList();
			if (list.size() > 0) {
				address = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return address;
	}

	public static JSONObject tranTransToJSON(hibernate.hbm.Transaction trans) {
		JSONObject ob = new JSONObject();
		if (trans != null) {
			ob.put("address", trans.getAddress());
			if(trans.getContract()==null){
				trans.setContract("");
			}
			JSONObject asset=AssetManager.AssetAddressMapNew.get(trans.getContract().toLowerCase()+"_"+trans.getChainType());
			if(asset!=null){
				ob.put("currency", asset.getString("asset"));
			}
			if (trans.getDepositAmount() != null) {
				ob.put("amount", Utils.parseAmout(trans.getDepositAmount(),asset.getInteger("id")));
			} else {
				ob.put("amount", 0);
			}
			ob.put("doneTime", "");
			if (trans.getCreateAt() != null) {
				ob.put("createTime", (int) (trans.getCreateAt() / 1000));
			}

			if (trans.getFeeAmount() != null) {
				ob.put("free", Utils.parseAmout(trans.getFeeAmount(),asset.getInteger("id")));
			} else {
				ob.put("free", 0);
			}
			ob.put("id", trans.getUserTransactionId());
			ob.put("seq", trans.getId());
			ob.put("state", StatusMap.get(trans.getStatus()));
			if (trans.getUpdateAt() != null) {
				ob.put("updateTime", (int) (trans.getUpdateAt() / 1000));
			}
			ob.put("feeTransactionId", trans.getFeeTransactionId());
			ob.put("approveTransactionId", trans.getApproveTransactionId());
			ob.put("payTransactionId", trans.getPayTransactionId());
			JSONObject detail = new JSONObject();
			detail.put("cmd", "");
			detail.put("method", "deposit");
			detail.put("status", StatusMap.get(trans.getStatus()));
			detail.put("statusVal", trans.getStatus());
			if (Utils.isStringEmpty(trans.getTxId())) {
				trans.setTxId("");
			}
			ob.put("fromAddress", trans.getFromAddress());
			ob.put("toAddress", trans.getToAddress());
			detail.put("tid", trans.getTxId());
			ob.put("detail", detail);
		}
		return ob;
	}

	public static JSONObject tranWithdrawApplyToJSON(WithdrawApply withdrawApply) {
		JSONObject ob = new JSONObject();
		if (withdrawApply != null) {

			ob.put("address", withdrawApply.getAddress());
			ob.put("currency", AssetManager.AssetIdMap.get(withdrawApply.getAsset() + "").getString("asset"));
			ob.put("amount", withdrawApply.getAmount());
			ob.put("doneTime", "");
			ob.put("createTime", withdrawApply.getCreateAt());
			ob.put("free", 0);
			ob.put("id", withdrawApply.getId());
			ob.put("seq", 0);
			ob.put("state", StatusMap.get(999));
			JSONObject detail = new JSONObject();
			detail.put("cmd", "");
			detail.put("method", "deposit");
			if (withdrawApply.getStatus().equals("1")) {
				detail.put("status", "待审核");
				detail.put("statusVal", "998");
			} else if (withdrawApply.getStatus().equals("3")) {
				detail.put("status", "已拒绝");
				detail.put("statusVal", "999");
			} else {
				detail.put("status", "");
			}

			ob.put("fromAddress", "");
			ob.put("toAddress", "");
			detail.put("tid", "");
			ob.put("detail", detail);
		}
		return ob;
	}
	
	public static JSONArray withdrawApplyList(String userid) {
		JSONArray result = new JSONArray();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<WithdrawApply> crq = crb.createQuery(WithdrawApply.class);
			Root<WithdrawApply> root = crq.from(WithdrawApply.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), userid));
			predicates.add(crb.equal(root.get("status"), "1"));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			crq.orderBy(crb.desc(root.get("createAt")));
			List<WithdrawApply> list = s.createQuery(crq).setFirstResult(0).setMaxResults(20).getResultList();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranWithdrawApplyToJSON(list.get(i)));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return result;
	}

	public static JSONObject list(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		Users user = UsersManager.getUserByEmail(email);
		int offset = Utils.getInt(args, "offset", 0);
		int size = Utils.getInt(args, "size", 50);
		JSONObject res = new JSONObject();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<hibernate.hbm.Transaction> crq = crb.createQuery(hibernate.hbm.Transaction.class);
			Root<hibernate.hbm.Transaction> root = crq.from(hibernate.hbm.Transaction.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userid"), user.getBttUserid()));
			predicates.add(crb.equal(root.get("depositOrWithdraw"), false));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			crq.orderBy(crb.desc(root.get("createAt")));
			List<hibernate.hbm.Transaction> list = s.createQuery(crq).setFirstResult(offset).setMaxResults(size)
					.getResultList();
			JSONArray result = new JSONArray();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranTransToJSON(list.get(i)));
				}
			}
			JSONArray withdrawApplyList = withdrawApplyList(email);
			if (withdrawApplyList.size() > 0) {
				for (int i = 0; i < withdrawApplyList.size(); ++i) {
					result.add(withdrawApplyList.get(i));
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

	public static JSONObject tranAdminWithdrawApplyToJSON(WithdrawApply withdrawApply) {
		JSONObject ob = new JSONObject();
		if (withdrawApply != null) {

			ob.put("address", withdrawApply.getAddress());
			ob.put("currency", AssetManager.AssetIdMap.get(withdrawApply.getAsset() + "").getString("asset"));
			ob.put("assetId", withdrawApply.getAsset());
			ob.put("amount", withdrawApply.getAmount());
			ob.put("createTime", withdrawApply.getCreateAt());
			ob.put("id", withdrawApply.getId());
			ob.put("status", withdrawApply.getStatus());
			ob.put("message", withdrawApply.getMessage());
			ob.put("bttUserId", withdrawApply.getBttUserId());
			ob.put("email", withdrawApply.getUserId());
		}
		return ob;
	}

	public static JSONObject withdrawApplyAdminList(JSONObject args) {
		int offset = Utils.getInt(args, "offset", 0);
		int size = Utils.getInt(args, "size", 50);
		String status = Utils.getString(args, "status", "1");
		JSONObject res = new JSONObject();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<WithdrawApply> crq = crb.createQuery(WithdrawApply.class);
			Root<WithdrawApply> root = crq.from(WithdrawApply.class);
			crq.select(root);
			if (!Utils.isStringEmpty(status)) {
				if (status.indexOf(",") > -1) {
					Predicate p1 = crb.equal(root.get("status"), status.split(",")[0]);
					Predicate p2 = crb.equal(root.get("status"), status.split(",")[1]);
					if (status.split(",").length == 3) {
						Predicate p3 = crb.equal(root.get("status"), status.split(",")[2]);
						crq.where(crb.or(p1, p2, p3));
					} else {
						crq.where(crb.or(p1, p2));
					}
				} else {
					Predicate p1 = crb.equal(root.get("status"), status);
					crq.where(p1);
				}

			}
			int count = s.createQuery(crq).getResultList().size();
			crq.orderBy(crb.desc(root.get("createAt")));
			List<WithdrawApply> list = s.createQuery(crq).setFirstResult(offset).setMaxResults(size).getResultList();
			JSONArray result = new JSONArray();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranAdminWithdrawApplyToJSON(list.get(i)));
				}
			}
			res.put("result", result);
			res.put("count", count);
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
	
	public static void init() {
		if (Utils.isDug) {
			PixiuWithdrawUrl = "https://newton.bytetrade.io/pixiu/api/v1?cmd=withdrawNotify";
			PixiuWithdrawUrlBTC = "https://newton.bytetrade.io/pixiu/api/v1?cmd=withdrawNotify_btc";
		}
		StatusMap.put(1, "DEPOSIT_FAILED");
		StatusMap.put(2, "FEE_SEND_FAILED");
		StatusMap.put(3, "FEE_EXECUED");
		StatusMap.put(4, "FEE_FAILED");
		StatusMap.put(5, "PAY_EXECUED");
		StatusMap.put(6, "PAY_SEND_FAILED");
		StatusMap.put(7, "PAY_FAILED");
		StatusMap.put(8, "PAY_SUCCED");
	}

}
