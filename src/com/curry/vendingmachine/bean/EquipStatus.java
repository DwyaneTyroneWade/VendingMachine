package com.curry.vendingmachine.bean;

import java.io.Serializable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EquipStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String sbId; // 设备Id
	public String version; // 版本号
	public String zbq_zs; // 纸币张数
	public String ybq_zs; // 硬币数目
	public String status;// 状态0正常 1故障 2故障
	public String zbq_status;// 纸币器状态
	public String ybq_status;// 硬币器状态
	public String ysj_status;// 压缩机状态
	public String kzb_status;// 扩展板状态
	public String gprs_status;// gprs状态
	public String network;// gprs信号
	public List<HuoDaoInfo> array;

	public String jsonArray() {
		String json = "";
		if (array != null && array.size() > 0) {
			JSONArray jsonArr = new JSONArray();
			for (int i = 0; i < array.size(); i++) {
				JSONObject object = new JSONObject();
				try {
					object.put("huodao", array.get(i).huodao);
					object.put("status", array.get(i).status);
					object.put("stock", array.get(i).stock);
					object.put("price", array.get(i).price);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				jsonArr.put(object);
			}
			if (jsonArr.length() > 0) {
				json = jsonArr.toString();
			}
		}
		return json;
	}
}
