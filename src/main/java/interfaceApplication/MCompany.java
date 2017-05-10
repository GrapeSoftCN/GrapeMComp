package interfaceApplication;

import java.util.HashMap;

import esayhelper.JSONHelper;
import model.MCompanyModel;

public class MCompany {
	private MCompanyModel model = new MCompanyModel();
	private HashMap<String, Object> map = new HashMap<>();
//	private String userid;

	public MCompany() {
//		userid = execRequest.getChannelValue("Userid").toString();

		map.put("companyDesp", "");
		map.put("companyURL", "");
		map.put("companyTele", "");
		map.put("companyMob", 0);
		map.put("companyEmail", "0");
		map.put("companyQQ", 0);
		map.put("companyPerson", 0);
		map.put("type", 0);
		map.put("wbid", 0);
		map.put("rPlv", 1000); // 读取 权限值
		map.put("uPlv", 2000); // 修改 权限值
		map.put("dPlv", 3000); // 删除 权限值
	}

	public String AddComp(String compInfo) {
//		String tip = execRequest
//				._run("GrapeAuth/Auth/InsertPLV/s:" + userid, null).toString();
//		if (!"0".equals(tip)) {
//			return model.resultMessage(4, "");
//		}
		return model.resultMessage(JSONHelper.string2json(
				model.addComp(map, JSONHelper.string2json(compInfo))));
	}

	public String updateComp(String cid, String comInfo) {
//		String uPLV = model.FindcomByID(cid).get("uplv").toString();
//		String tip = execRequest
//				._run("GrapeAuth/Auth/UpdatePLV/s:" + uPLV + "/s:" + userid,
//						null)
//				.toString();
//		if (!"0".equals(tip)) {
//			return model.resultMessage(4, "没有编辑权限");
//		}
		return model.resultMessage(
				model.updateComp(cid, JSONHelper.string2json(comInfo)),
				"维护单位信息修改成功");
	}

	public String DeleteComp(String cid) {
//		String uPLV = model.FindcomByID(cid).get("uplv").toString();
//		String tip = execRequest
//				._run("GrapeAuth/Auth/UpdatePLV/s:" + uPLV + "/s:" + userid,
//						null)
//				.toString();
//		if (!"0".equals(tip)) {
//			return model.resultMessage(4, "没有编辑权限");
//		}
		return model.resultMessage(model.deleteComp(cid), "维护单位信息删除成功");
	}

	public String SearchComp(String Info) {
		return model.resultMessage(model.find(JSONHelper.string2json(Info)));
	}

	// 分页
	public String PageMComp(int idx, int pageSize) {
		return model.resultMessage(model.page(idx, pageSize));
	}

	// 条件分页
	public String PageByMComp(int idx, int pageSize, String msgInfo) {
		return model.resultMessage(
				model.page(idx, pageSize, JSONHelper.string2json(msgInfo)));
	}

}
