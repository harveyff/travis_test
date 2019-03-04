package manager;

import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import gt3.src.d1.VerifyLoginServlet;
import hibernate.HibernateUtil;
import hibernate.hbm.Address;
import hibernate.hbm.Users;
import utils.HttpRequest;
import utils.Utils;

public class AssetManager {
	public static Logger log4j = LogManager.getLogger(AssetManager.class);
	public static Map<String, JSONObject> AssetIdMap = new HashMap<String, JSONObject>();
	public static Map<String, JSONObject> AssetSymbolMap = new HashMap<String, JSONObject>();
	public static Map<String, JSONObject> AssetAddressMapNew=new HashMap<String, JSONObject>();

	public static void init() {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("cmd", "listAccounts");
		paramsMap.put("channel", "all");
		log4j.info("initAsset begin...");
		JSONObject ress = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
		JSONArray balances = ress.getJSONArray("balances");
		log4j.info("asset init:" + balances.size());
		for (int i = 0; i < balances.size(); ++i) {
			JSONObject obj = (JSONObject) balances.get(i);
			AssetIdMap.put(obj.getInteger("id") + "", obj);
			AssetSymbolMap.put(obj.getString("asset"), obj);
			if(Utils.isStringEmpty(obj.getString("chain_contract_address"))){
				obj.put("chain_contract_address", "");
			}
			AssetAddressMapNew.put(obj.getString("chain_contract_address").toLowerCase()+"_"+obj.getString("chain_type"), obj);
		}
	}
	
	public static JSONArray allAddressList() {
		JSONArray result = new JSONArray();
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("cmd", "listAccounts");
		paramsMap.put("channel", "newton");
		paramsMap.put("userid", "");
		JSONObject ress = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
		JSONArray balances = ress.getJSONArray("balances");
		for (int i = 0; i < balances.size(); ++i) {
			JSONObject obj = (JSONObject) balances.get(i);
				JSONObject obj1 = new JSONObject();
				obj1.put("id", obj.getInteger("id")+"");
				obj1.put("asset", obj.getString("asset"));
				result.add(obj1);
		}
		return result;
	}
	public static JSONObject addressList(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		JSONObject res = new JSONObject();
		JSONArray result = new JSONArray();
		Users user = UsersManager.getUserByEmail(email);
		if (user == null) {
			log4j.info("addressList时user空null，email：" + email);
		} else {
			String userid = user.getBttUserid();
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("cmd", "listAccounts");
			paramsMap.put("channel", "newton");
			paramsMap.put("userid", userid);
			JSONObject ress = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
			JSONArray balances = ress.getJSONArray("balances");

			Map<String, String> depositAddressMap = depositAddressMap(email);
			if(balances!=null){
				for (int i = 0; i < balances.size(); ++i) {
					JSONObject obj = (JSONObject) balances.get(i);
					JSONObject asset=AssetManager.AssetIdMap.get(obj.getString("id"));
					if(asset==null){
						AssetManager.init();
						break;
					}
				}
				for (int i = 0; i < balances.size(); ++i) {
					JSONObject obj = (JSONObject) balances.get(i);
					JSONObject asset=AssetManager.AssetIdMap.get(obj.getString("id"));
						int chain_type=asset.getInteger("chain_type");
						if(chain_type==1){
							obj.put("depositAddress", depositAddressMap.get("ETH"));
						}else if(chain_type==2){
							obj.put("depositAddress", depositAddressMap.get("BTC"));
						} else if(chain_type==3){
							if(!Utils.isStringEmpty(depositAddressMap.get("CMT"))){
								obj.put("depositAddress", depositAddressMap.get("CMT"));
							}else{
								obj.put("depositAddress", depositAddressMap.get("ETH"));
							}
						} else {
							obj.put("depositAddress", "");
						}
						result.add(obj);
				}
			}
		}
		res.put("code", 0);
		res.put("balances", result);
		return res;
	}

	public static Map<String, String> depositAddressMap(String email) {
		Map<String, String> res = new HashMap<String, String>();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Address> crq = crb.createQuery(Address.class);
			Root<Address> root = crq.from(Address.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("userId"), email));
			predicates.add(crb.equal(root.get("type"), "2"));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			crq.orderBy(crb.asc(root.get("showIndex")));
			List<Address> list = s.createQuery(crq).setFirstResult(0).setMaxResults(50).getResultList();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					res.put(list.get(i).getAddressName(), list.get(i).getAddress());
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
		return res;
	}

	// public static JSONObject addressList(JSONObject args) {
	// String email = Utils.getString(args, "email", "");
	// JSONObject res=new JSONObject();
	// Session s = null;
	// JSONArray result = new JSONArray();
	// try {
	// s = HibernateUtil.getSessionFactory().openSession();
	// CriteriaBuilder crb = s.getCriteriaBuilder();
	// CriteriaQuery<Address> crq = crb.createQuery(Address.class);
	// Root<Address> root = crq.from(Address.class);
	// crq.select(root);
	// List<Predicate> predicates = new ArrayList<Predicate>();
	// predicates.add(crb.equal(root.get("userId"), email));
	// crq.where(predicates.toArray(new Predicate[predicates.size()]));
	// crq.orderBy(crb.asc(root.get("showIndex")));
	// List<Address> list =
	// s.createQuery(crq).setFirstResult(0).setMaxResults(50).getResultList();
	// if (list.size() > 0) {
	// for (int i = 0; i < list.size(); ++i) {
	// result.add(tranlatAddressToJSON(list.get(i)));
	// }
	// }
	// res.put("result", result);
	// res.put("code", 0);
	// } catch (Exception e) {
	// res.put("code", 100000);
	// e.printStackTrace();
	//
	// } finally {
	// if (s != null) {
	// s.close();
	// s = null;
	// }
	// }
	//
	// res.put("code", 0);
	// res.put("result", result);
	// return res;
	// }
	// public static JSONObject tranlatAddressToJSON(Address address) {
	// JSONObject ob = new JSONObject();
	// if (address != null) {
	// ob.put("id", address.getId());
	// ob.put("address", address.getAddress());
	// ob.put("asset", address.getAddressName());
	// ob.put("assetId", address.getAssetId());
	// ob.put("balance", address.getBalance());
	// ob.put("available", address.getAvailable());
	// ob.put("frozen", address.getFrozen());
	// ob.put("canWithdraw", true);
	// ob.put("canDeposit", true);
	// }
	// return ob;
	// }
}
