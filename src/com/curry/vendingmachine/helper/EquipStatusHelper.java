package com.curry.vendingmachine.helper;

import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.PreferenceHelper;

public class EquipStatusHelper {
	public static EquipStatus getEquipStatus() {
		Object obj = PreferenceHelper
				.getSerializeObj(Constants.PREFERENCE_KEY_EQUIP_STATUS);

		EquipStatus es = null;
		if (obj != null && obj instanceof EquipStatus) {
			es = (EquipStatus) obj;
		}

		return es;
	}

	public static void saveEquipStatus(EquipStatus es) {
		PreferenceHelper.setSerializeObj(Constants.PREFERENCE_KEY_EQUIP_STATUS,
				es);
	}
}
