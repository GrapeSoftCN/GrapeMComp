package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class MCompanyModel {
	private static DBHelper comp;
	private static formHelper form;
	private JSONObject _obj = new JSONObject();

	static {
		comp = new DBHelper("mongodb", "ManageComp");
		form = comp.getChecker();
	}

	public MCompanyModel() {
		form.putRule("companyName", formdef.notNull);
	}

	/**
	 * \ 新增操作，带权限验证
	 * 
	 * @param username
	 * @param object
	 * @return
	 */
	public String addComp(HashMap<String, Object> map, JSONObject object) {
		if (!form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		if (object.containsKey("companyEmail")) {
			form.putRule("companyEmail", formdef.email);
			if (!form.checkRule(object)) {
				return resultMessage(2, ""); // 邮箱格式不正确
			}
		}
		if (object.containsKey("companyMob")) {
			form.putRule("companyMob", formdef.mobile);
			if (!form.checkRule(object)) {
				return resultMessage(3, ""); // 手机号格式不正确
			}
		}
		object = AddMap(map, object);
		String info = comp.data(object).insertOnce().toString();
		return FindcomByID(info).toString();
	}

	public int updateComp(String mid, JSONObject object) {
		return comp.eq("_id", new ObjectId(mid)).data(object).update() != null
				? 0 : 99;
	}

	public int deleteComp(String mid) {
		return comp.eq("_id", new ObjectId(mid)).delete() != null ? 0 : 99;
	}

	public int deleteCompe(String[] mids) {
		comp = (DBHelper) comp.or();
		for (int i = 0; i < mids.length; i++) {
			comp.eq("_id", new ObjectId(mids[i]));
		}
		return comp.deleteAll() == mids.length ? 0 : 99;
	}

	public JSONArray find(JSONObject Info) {
		for (Object object2 : Info.keySet()) {
			comp.eq(object2.toString(), Info.get(object2.toString()));
		}
		return comp.limit(20).select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = comp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) comp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			comp.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		JSONArray array = comp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) comp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	public JSONObject FindcomByID(String mid) {
		return comp.eq("_id", new ObjectId(mid)).find();
	}

	public String getID() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}

	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map, JSONObject object) {
		if (map.entrySet() != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
						.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}

	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填项没有填";
			break;
		case 2:
			msg = "邮箱格式不正确";
			break;
		case 3:
			msg = "手机号格式不正确";
			break;
		case 4:
			msg = "没有创建数据权限，请联系管理员进行权限调整";
			break;
		case 5:
			msg = "没有修改数据权限，请联系管理员进行权限调整";
			break;
		case 6:
			msg = "没有删除数据权限，请联系管理员进行权限调整";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
