package manager;

import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hibernate.HibernateUtil;
import hibernate.hbm.Kyc;
import hibernate.hbm.Users;
import utils.Utils;

public class KycManager {
	public static Logger log4j = LogManager.getLogger(KycManager.class);
	/**
	 * KYC状态
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject kycStatus(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		JSONObject res = new JSONObject();
		Kyc kyc = getKycByEmail(email);
		if (kyc == null) {
			res.put("code", 0);
			res.put("status", "0");
			res.put("message", "");
		} else {
			res.put("code", 0);
			res.put("status", kyc.getStatus());
			res.put("message", kyc.getMessage());
		}
		return res;
	}

	/**
	 * KYC申请
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject kycSend(JSONObject args) {
		log4j.info("kycSend,args:"+args.toJSONString());
		String email = Utils.getString(args, "email", "");
		JSONObject res = new JSONObject();
		Kyc kyc = getKycByEmail(email);
		if (kyc == null || kyc.getStatus().equals("2")) {
			String name = Utils.getString(args, "name", "");// 姓名
			String idno = Utils.getString(args, "idno", "");// 身份证号
			String idPhoneFront = Utils.getString(args, "idPhoneFront", ""); // 身份证正面
			String idPhoneReverse = Utils.getString(args, "idPhoneReverse", ""); // 身份证反面
			if (kyc == null) {
				Users user = UsersManager.getUserByEmail(email);
				kyc = new Kyc();
				kyc.setBttUserId(user.getBttUserid());
				kyc.setId(Utils.getUUID());
				kyc.setCreateAt((int) (new Date().getTime() / 1000));
			}
			kyc.setUpdateAt((int) (new Date().getTime() / 1000));
			kyc.setUserId(email);
			kyc.setName(name);
			System.out.println("name:"+name);
			kyc.setIdno(idno);
			kyc.setIdPhoneFront(idPhoneFront);
			kyc.setIdPhoneReverse(idPhoneReverse);
			kyc.setStatus("1");
			kyc.setMessage("");
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.saveOrUpdate(kyc);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);
		} else {
			res.put("code", 100010);
		}
		return res;
	}

	/**
	 * 待审核的KYC列表
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject kycList(JSONObject args) {
		String status = Utils.getString(args, "status", "1");
		int offset = Utils.getInt(args, "offset", 0);
		int size = Utils.getInt(args, "size", 50);
		JSONObject res = new JSONObject();
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Kyc> crq = crb.createQuery(Kyc.class);
			Root<Kyc> root = crq.from(Kyc.class);
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
			int count= s.createQuery(crq).getResultList().size();
			crq.orderBy(crb.desc(root.get("createAt")));
			List<Kyc> list = s.createQuery(crq).setFirstResult(offset).setMaxResults(size).getResultList();
			JSONArray result = new JSONArray();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); ++i) {
					result.add(tranlatKycToJSON(list.get(i)));
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

	/**
	 * 审核
	 * 
	 * @param args
	 * @return
	 */
	public static JSONObject kycApprove(JSONObject args) {
		JSONObject res = new JSONObject();
		String status = Utils.getString(args, "status", "");// 2:失败，3:通过
		String id = Utils.getString(args, "id", "");
		String message = Utils.getString(args, "message", "");// 不通过原因
		Kyc kyc = getKycById(id);
		if (kyc == null || !kyc.getStatus().equals("1")) {
			res.put("code", 100016);
			return res;
		}
		if (status.equals("2")) {
			kyc.setStatus(status);
			kyc.setUpdateAt((int) (new Date().getTime() / 1000));
			kyc.setMessage(message);
		} else if (status.equals("3")) {
			kyc.setStatus(status);
			kyc.setUpdateAt((int) (new Date().getTime() / 1000));
			Users user = UsersManager.getUserByEmail(kyc.getUserId());
			user.setKycStatus("1");
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.update(user);
			s.getTransaction().commit();
			s.close();
		}
		res.put("code", 0);
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.update(kyc);
		s.getTransaction().commit();
		s.close();
		return res;
	}

	public static Kyc getKycByEmail(String email) {
		Kyc kyc = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Kyc> crq = crb.createQuery(Kyc.class);
			Root<Kyc> root = crq.from(Kyc.class);
			crq.select(root);
			crq.where(crb.equal(root.get("userId"), email));
			List<Kyc> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				kyc = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return kyc;
	}

	public static Kyc getKycById(String id) {
		Kyc kyc = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Kyc> crq = crb.createQuery(Kyc.class);
			Root<Kyc> root = crq.from(Kyc.class);
			crq.select(root);
			crq.where(crb.equal(root.get("id"), id));
			List<Kyc> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				kyc = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		return kyc;
	}

	public static JSONObject tranlatKycToJSON(Kyc kyc) {
		JSONObject ob = new JSONObject();
		if (kyc != null) {
			ob.put("id", kyc.getId());
			ob.put("name", kyc.getName());
			ob.put("userId", kyc.getUserId());
			ob.put("bttUserId", kyc.getBttUserId());
			ob.put("idno", kyc.getIdno());
			ob.put("idPhoneFront", kyc.getIdPhoneFront());
			ob.put("idPhoneReverse", kyc.getIdPhoneReverse());
			ob.put("message", kyc.getMessage());
			ob.put("status", kyc.getStatus());
			ob.put("createAt", kyc.getCreateAt());
			ob.put("updateAt", kyc.getUpdateAt());
		}
		return ob;
	}
}
