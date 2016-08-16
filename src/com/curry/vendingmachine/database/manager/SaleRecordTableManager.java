package com.curry.vendingmachine.database.manager;

import java.util.ArrayList;

import android.database.Cursor;

import com.curry.vendingmachine.bean.SaleRecord;
import com.curry.vendingmachine.database.CurryDatabaseUtils;

public class SaleRecordTableManager {

	public ArrayList<SaleRecord> get() {
		// query
		Cursor cursor = CurryDatabaseUtils.querySaleRecord();
		if (cursor != null && cursor.getCount() > 0) {
			// get
			ArrayList<SaleRecord> saleRecordDB = CurryDatabaseUtils
					.getSaleRecord(cursor);
			cursor.close();
			return saleRecordDB;
		}
		return null;
	}

	private void save(ArrayList<SaleRecord> saleRecordArr) {
		if (saleRecordArr != null && saleRecordArr.size() > 0) {
			for (int i = 0; i < saleRecordArr.size(); i++) {
				SaleRecord saleRecordItem = saleRecordArr.get(i);
				save(saleRecordItem);
			}
		}
	}

	private void save(SaleRecord saleRecordItem) {
		if (saleRecordItem != null) {
			Cursor c = CurryDatabaseUtils.querySaleRecord(saleRecordItem);
			if (c != null) {
				if (c.getCount() > 0) {
					// update
					CurryDatabaseUtils.updateSaleRecord(saleRecordItem);
				} else {
					// insert
					CurryDatabaseUtils.insertSaleRecord(saleRecordItem);
				}
				c.close();
			} else {
				// insert
				CurryDatabaseUtils.insertSaleRecord(saleRecordItem);
			}
		}
	}

	private void insert(SaleRecord saleRecordItem) {
		if (saleRecordItem != null) {
			CurryDatabaseUtils.insertSaleRecord(saleRecordItem);
		}
	}

	public void saveAfterDeleteAll(ArrayList<SaleRecord> saleRecordArr) {
		CurryDatabaseUtils.deleteSaleRecord();
		save(saleRecordArr);
	}

	public void saveSingle(SaleRecord saleRecord) {
		insert(saleRecord);
	}

	// delete同样存在id 为空的问题 解决 save之后返回的id补全
	public void delete(SaleRecord saleRecord) {
		CurryDatabaseUtils.deleteSaleRecord(saleRecord);
	}

	public void clear() {
		CurryDatabaseUtils.deleteSaleRecord();
	}

	public boolean isSaleRecord() {
		boolean isSaleRecord = false;
		ArrayList<SaleRecord> saleRecordArrDB = get();
		if (saleRecordArrDB != null && saleRecordArrDB.size() > 0) {
			isSaleRecord = true;
		}
		return isSaleRecord;
	}
}
