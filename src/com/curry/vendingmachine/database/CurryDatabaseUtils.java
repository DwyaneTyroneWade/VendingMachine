package com.curry.vendingmachine.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.bean.Goods;
import com.curry.vendingmachine.bean.SaleRecord;
import com.curry.vendingmachine.database.CurryTables.EquipStatusTable;
import com.curry.vendingmachine.database.CurryTables.GoodsTable;
import com.curry.vendingmachine.database.CurryTables.SaleRecordTable;
import com.curry.vendingmachine.utils.C;

public class CurryDatabaseUtils {
	/** GoodsTable**start ***/
	public static Cursor queryGoods(Goods goods) {
		return C.get()
				.getContentResolver()
				.query(GoodsTable.CONTENT_URI, null, GoodsTable.ID + " =? ",
						new String[] { goods.getId() }, null);
	}

	public static Cursor queryGoods() {
		return C.get().getContentResolver()
				.query(GoodsTable.CONTENT_URI, null, null, null, null);
	}

	public static void updateGoods(Goods goods) {
		ContentValues values = appendValues(goods);
		C.get()
				.getContentResolver()
				.update(GoodsTable.CONTENT_URI, values, GoodsTable.ID + "=?",
						new String[] { goods.getId() });
	}

	public static void insertGoods(Goods goods) {
		ContentValues values = appendValues(goods);
		C.get().getContentResolver().insert(GoodsTable.CONTENT_URI, values);
	}

	public static void deleteGoods(Goods goods) {
		C.get()
				.getContentResolver()
				.delete(GoodsTable.CONTENT_URI, GoodsTable.ID + "=?",
						new String[] { goods.getId() });
	}

	public static void deleteGoods() {
		C.get().getContentResolver().delete(GoodsTable.CONTENT_URI, null, null);
	}

	private static ContentValues appendValues(Goods goods) {
		ContentValues values = new ContentValues();
		values.put(GoodsTable.ID, goods.getId());
		values.put(GoodsTable.CAPACITY, goods.getCapacity());
		values.put(GoodsTable.STOCK_NUM, goods.getStockNum());
		values.put(GoodsTable.PRICE, goods.getPrice());

		return values;
	}

	public static ArrayList<Goods> getGoods(Cursor c) {
		ArrayList<Goods> goodsArr = new ArrayList<Goods>();

		if (c == null || c.getCount() <= 0) {
			return goodsArr;
		}

		c.moveToFirst();
		while (!c.isAfterLast()) {
			Goods goods = new Goods();
			goods.setId(c.getString(c.getColumnIndex(GoodsTable.ID)));
			goods.setCapacity(c.getString(c.getColumnIndex(GoodsTable.CAPACITY)));
			goods.setStockNum(c.getString(c
					.getColumnIndex(GoodsTable.STOCK_NUM)));
			goods.setPrice(c.getString(c.getColumnIndex(GoodsTable.PRICE)));

			goodsArr.add(goods);
			c.moveToNext();
		}

		return goodsArr;
	}

	/** GoodsTable**end ***/

	/** SaleRecordTable**start ***/
	public static Cursor querySaleRecord(SaleRecord saleRecord) {
		return C.get()
				.getContentResolver()
				.query(SaleRecordTable.CONTENT_URI, null,
						SaleRecordTable.ID + " =? ",
						new String[] { saleRecord.getId() }, null);
	}

	public static Cursor querySaleRecord() {
		return C.get().getContentResolver()
				.query(SaleRecordTable.CONTENT_URI, null, null, null, null);
	}

	public static void updateSaleRecord(SaleRecord saleRecord) {
		ContentValues values = appendValues(saleRecord);
		C.get()
				.getContentResolver()
				.update(SaleRecordTable.CONTENT_URI, values,
						SaleRecordTable.ID + "=?",
						new String[] { saleRecord.getId() });
	}

	public static void insertSaleRecord(SaleRecord saleRecord) {
		ContentValues values = appendValues(saleRecord);
		C.get().getContentResolver()
				.insert(SaleRecordTable.CONTENT_URI, values);
	}

	public static void deleteSaleRecord(SaleRecord saleRecord) {
		C.get()
				.getContentResolver()
				.delete(SaleRecordTable.CONTENT_URI, SaleRecordTable.ID + "=?",
						new String[] { saleRecord.getId() });
	}

	public static void deleteSaleRecord() {
		C.get().getContentResolver()
				.delete(SaleRecordTable.CONTENT_URI, null, null);
	}

	private static ContentValues appendValues(SaleRecord saleRecord) {
		ContentValues values = new ContentValues();
		values.put(SaleRecordTable.ID, saleRecord.getId());
		values.put(SaleRecordTable.PRICE, saleRecord.getPrice());
		values.put(SaleRecordTable.TIME, saleRecord.getTime());

		return values;
	}

	public static ArrayList<SaleRecord> getSaleRecord(Cursor c) {
		ArrayList<SaleRecord> saleRecordArr = new ArrayList<SaleRecord>();

		if (c == null || c.getCount() <= 0) {
			return saleRecordArr;
		}
		c.moveToFirst();
		while (!c.isAfterLast()) {
			SaleRecord saleRecord = new SaleRecord();
			saleRecord.setId(c.getString(c.getColumnIndex(SaleRecordTable.ID)));
			saleRecord.setPrice(c.getString(c
					.getColumnIndex(SaleRecordTable.PRICE)));
			saleRecord.setTime(c.getString(c
					.getColumnIndex(SaleRecordTable.TIME)));
			saleRecordArr.add(saleRecord);
			c.moveToNext();
		}

		return saleRecordArr;
	}

	/** SaleRecordTable**end ***/

	/** EquipStatusTable**start ***/
	public static Cursor queryEquipStatus(EquipStatus EquipStatus) {
		return C.get()
				.getContentResolver()
				.query(EquipStatusTable.CONTENT_URI, null,
						EquipStatusTable.SBID + " =? ",
						new String[] { EquipStatus.sbId }, null);
	}

	public static Cursor queryEquipStatus() {
		return C.get().getContentResolver()
				.query(EquipStatusTable.CONTENT_URI, null, null, null, null);
	}

	public static void updateEquipStatus(EquipStatus EquipStatus) {
		ContentValues values = appendValues(EquipStatus);
		C.get()
				.getContentResolver()
				.update(EquipStatusTable.CONTENT_URI, values,
						EquipStatusTable.SBID + "=?",
						new String[] { EquipStatus.sbId });
	}

	public static void insertEquipStatus(EquipStatus EquipStatus) {
		ContentValues values = appendValues(EquipStatus);
		C.get().getContentResolver()
				.insert(EquipStatusTable.CONTENT_URI, values);
	}

	public static void deleteEquipStatus(EquipStatus EquipStatus) {
		C.get()
				.getContentResolver()
				.delete(EquipStatusTable.CONTENT_URI,
						EquipStatusTable.SBID + "=?",
						new String[] { EquipStatus.sbId });
	}

	public static void deleteEquipStatus() {
		C.get().getContentResolver()
				.delete(EquipStatusTable.CONTENT_URI, null, null);
	}

	private static ContentValues appendValues(EquipStatus EquipStatus) {
		ContentValues values = new ContentValues();
		values.put(EquipStatusTable.SBID, EquipStatus.sbId);
		values.put(EquipStatusTable.VERSION, EquipStatus.version);
		values.put(EquipStatusTable.ZBQZS, EquipStatus.zbq_zs);
		values.put(EquipStatusTable.YBQZS, EquipStatus.ybq_zs);
		values.put(EquipStatusTable.STATUS, EquipStatus.status);
		values.put(EquipStatusTable.ZBQSTATUS, EquipStatus.zbq_status);
		values.put(EquipStatusTable.YBQSTATUS, EquipStatus.ybq_status);
		values.put(EquipStatusTable.YSJSTATUS, EquipStatus.ysj_status);
		values.put(EquipStatusTable.KZBSTATUS, EquipStatus.kzb_status);
		values.put(EquipStatusTable.GPRSSTATUS, EquipStatus.gprs_status);
		values.put(EquipStatusTable.NETWORK, EquipStatus.network);
		return values;
	}

	public static ArrayList<EquipStatus> getEquipStatus(Cursor c) {
		ArrayList<EquipStatus> EquipStatusArr = new ArrayList<EquipStatus>();

		if (c == null || c.getCount() <= 0) {
			return EquipStatusArr;
		}
		c.moveToFirst();
		while (!c.isAfterLast()) {
			EquipStatus EquipStatus = new EquipStatus();
			EquipStatus.sbId = c.getString(c
					.getColumnIndex(EquipStatusTable.SBID));
			EquipStatus.version = c.getString(c
					.getColumnIndex(EquipStatusTable.VERSION));
			EquipStatus.zbq_zs = c.getString(c
					.getColumnIndex(EquipStatusTable.ZBQZS));
			EquipStatus.ybq_zs = c.getString(c
					.getColumnIndex(EquipStatusTable.YBQZS));
			EquipStatus.status = c.getString(c
					.getColumnIndex(EquipStatusTable.STATUS));
			EquipStatus.zbq_status = c.getString(c
					.getColumnIndex(EquipStatusTable.ZBQSTATUS));
			EquipStatus.ybq_status = c.getString(c
					.getColumnIndex(EquipStatusTable.YBQSTATUS));
			EquipStatus.ysj_status = c.getString(c
					.getColumnIndex(EquipStatusTable.YSJSTATUS));
			EquipStatus.kzb_status = c.getString(c
					.getColumnIndex(EquipStatusTable.KZBSTATUS));
			EquipStatus.gprs_status = c.getString(c
					.getColumnIndex(EquipStatusTable.GPRSSTATUS));
			EquipStatus.network = c.getString(c
					.getColumnIndex(EquipStatusTable.NETWORK));

			EquipStatusArr.add(EquipStatus);
			c.moveToNext();
		}

		return EquipStatusArr;
	}

	/** EquipStatusTable**end ***/

}
