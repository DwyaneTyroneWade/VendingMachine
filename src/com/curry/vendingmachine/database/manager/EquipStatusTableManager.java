package com.curry.vendingmachine.database.manager;

import java.util.ArrayList;

import android.database.Cursor;

import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.database.CurryDatabaseUtils;

public class EquipStatusTableManager {

	public ArrayList<EquipStatus> get() {
		// query
		Cursor cursor = CurryDatabaseUtils.queryEquipStatus();
		if (cursor != null && cursor.getCount() > 0) {
			// get
			ArrayList<EquipStatus> EquipStatusDB = CurryDatabaseUtils
					.getEquipStatus(cursor);
			cursor.close();
			return EquipStatusDB;
		}
		return null;
	}

	private void save(ArrayList<EquipStatus> EquipStatusArr) {
		if (EquipStatusArr != null && EquipStatusArr.size() > 0) {
			for (int i = 0; i < EquipStatusArr.size(); i++) {
				EquipStatus EquipStatusItem = EquipStatusArr.get(i);
				if (EquipStatusItem != null) {
					Cursor c = CurryDatabaseUtils
							.queryEquipStatus(EquipStatusItem);
					if (c != null) {
						if (c.getCount() > 0) {
							// update
							CurryDatabaseUtils
									.updateEquipStatus(EquipStatusItem);
						} else {
							// insert
							CurryDatabaseUtils
									.insertEquipStatus(EquipStatusItem);
						}
						c.close();
					} else {
						// insert
						CurryDatabaseUtils.insertEquipStatus(EquipStatusItem);
					}
				}
			}
		}
	}

	public void saveAfterDeleteAll(ArrayList<EquipStatus> EquipStatusArr) {
		CurryDatabaseUtils.deleteEquipStatus();
		save(EquipStatusArr);
	}

	// delete同样存在id 为空的问题 解决 save之后返回的id补全
	public void delete(EquipStatus EquipStatus) {
		CurryDatabaseUtils.deleteEquipStatus(EquipStatus);
	}

	public void clear() {
		CurryDatabaseUtils.deleteEquipStatus();
	}

	public boolean isEquipStatus() {
		boolean isEquipStatus = false;
		ArrayList<EquipStatus> EquipStatusArrDB = get();
		if (EquipStatusArrDB != null && EquipStatusArrDB.size() > 0) {
			isEquipStatus = true;
		}
		return isEquipStatus;
	}
}
