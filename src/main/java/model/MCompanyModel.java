package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import apps.appsProxy;
import database.db;
import esayhelper.DBHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;
import nlogger.nlogger;

public class MCompanyModel {
	private static DBHelper comp;
	private static formHelper form;
	private JSONObject _obj = new JSONObject();

	static {
		comp = new DBHelper(appsProxy.configValue().get("db").toString(), "ManageComp");
		form = comp.getChecker();
	}

	public MCompanyModel() {
		form.putRule("companyName", formdef.notNull);
		form.putRule("companyEmail", formdef.email);
		form.putRule("companyMob", formdef.mobile);
	}

	private db bind() {
		return comp.bind(String.valueOf(appsProxy.appid()));
	}

	/**
	 * \ 新增操作，带权限验证
	 * 
	 * @param username
	 * @param object
	 * @return
	 */
	public String addComp(JSONObject object) {
		String info = "";
		if (object == null) {
			return resultMessage(0, "新增数据失败");
		}
		if (!form.checkRuleEx(object)) {
			return resultMessage(1); // 必填字段没有填
		}
		if (object.containsKey("companyEmail")) {
			if (!form.checkRule(object)) {
				return resultMessage(2); // 邮箱格式不正确
			}
		}
		if (object.containsKey("companyMob")) {
			if (!form.checkRule(object)) {
				return resultMessage(3); // 手机号格式不正确
			}
		}
		info = bind().data(object).insertOnce().toString();
		if (("").equals(info)) {
			return resultMessage(0, "新增数据失败");
		}
		JSONObject obj = FindcomByID(info);
		return resultMessage(obj);
	}

	public String updateComp(String mid, JSONObject object) {
		JSONObject obj = bind().eq("_id", new ObjectId(mid)).data(object).update();
		return obj != null ? resultMessage(0, "修改成功") : resultMessage(99);
	}

	public String deleteComp(String mid) {
		JSONObject object = bind().eq("_id", new ObjectId(mid)).delete();
		return object != null ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String deleteCompe(String[] mids) {
		bind().or();
		for (int i = 0; i < mids.length; i++) {
			bind().eq("_id", new ObjectId(mids[i]));
		}
		return bind().deleteAll() == mids.length ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String find(JSONObject Info) {
		JSONArray array = null;
		try {
			if (Info != null) {
				array = new JSONArray();
				for (Object object2 : Info.keySet()) {
					if ("_id".equals(object2.toString())) {
						bind().eq("_id", new ObjectId(Info.get("_id").toString()));
					}
					bind().eq(object2.toString(), Info.get(object2.toString()));
				}
				array = bind().limit(20).select();
			}
		} catch (Exception e) {
			array = null;
		}
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		if (idx > 0 && pageSize > 0) {
			try {
				object = new JSONObject();
				JSONArray array = bind().page(idx, pageSize);
				object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
				object.put("currentPage", idx);
				object.put("pageSize", pageSize);
				object.put("data", array);
			} catch (Exception e) {
				object = null;
			}
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize, JSONObject Info) {
		JSONObject object = null;
		if (Info != null) {
			if (idx > 0 && pageSize > 0) {
				object = new JSONObject();
				try {
					for (Object object2 : Info.keySet()) {
						if ("_id".equals(object2.toString())) {
							bind().eq("_id", new ObjectId(Info.get("_id").toString()));
						}
						bind().eq(object2.toString(), Info.get(object2.toString()));
					}
					JSONArray array = bind().dirty().page(idx, pageSize);
					object = new JSONObject();
					object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
					object.put("currentPage", idx);
					object.put("pageSize", pageSize);
					object.put("data", array);
				} catch (Exception e) {
					nlogger.logout(e);
					object = null;
				}
			}
		}
		return resultMessage(object);
	}

	public JSONObject FindcomByID(String mid) {
		JSONObject object = bind().eq("_id", new ObjectId(mid)).find();
		return object != null ? object : null;
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
		if (object == null) {
			return null;
		}
		if (map.entrySet() != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	public String resultMessage(int num) {
		return resultMessage(num, "");
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		if (object == null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		if (array == null) {
			array = new JSONArray();
		}
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
