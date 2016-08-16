package com.curry.vendingmachine.database.manager;

import java.util.ArrayList;

import android.database.Cursor;

import com.curry.vendingmachine.bean.Goods;
import com.curry.vendingmachine.database.CurryDatabaseUtils;

public class GoodsTableManager {

	public ArrayList<Goods> get() {
		// query
		Cursor cursor = CurryDatabaseUtils.queryGoods();
		if (cursor != null && cursor.getCount() > 0) {
			// get
			ArrayList<Goods> goodsDB = CurryDatabaseUtils.getGoods(cursor);
			cursor.close();
			return goodsDB;
		}
		return null;
	}

	private void save(ArrayList<Goods> goodsArr) {
		if (goodsArr != null && goodsArr.size() > 0) {
			for (int i = 0; i < goodsArr.size(); i++) {
				Goods goodsItem = goodsArr.get(i);
				if (goodsItem != null) {
					Cursor c = CurryDatabaseUtils.queryGoods(goodsItem);
					if (c != null) {
						if (c.getCount() > 0) {
							// update
							CurryDatabaseUtils.updateGoods(goodsItem);
						} else {
							// insert
							CurryDatabaseUtils.insertGoods(goodsItem);
						}
						c.close();
					} else {
						// insert
						CurryDatabaseUtils.insertGoods(goodsItem);
					}
				}
			}
		}
	}

	public void saveAfterDeleteAll(ArrayList<Goods> goodsArr) {
		CurryDatabaseUtils.deleteGoods();
		save(goodsArr);
	}

	// delete同样存在id 为空的问题 解决 save之后返回的id补全
	public void delete(Goods goods) {
		CurryDatabaseUtils.deleteGoods(goods);
	}

	public void clear() {
		CurryDatabaseUtils.deleteGoods();
	}

	public boolean isGoods() {
		boolean isGoods = false;
		ArrayList<Goods> goodsArrDB = get();
		if (goodsArrDB != null && goodsArrDB.size() > 0) {
			isGoods = true;
		}
		return isGoods;
	}
}
