package com.curry.vendingmachine.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class CurryTables {

	public static final String AUTHORITY = "com.curry.vendingmachine.provider";

	public static class GoodsTable implements BaseColumns {
		public static final String TABLE_NAME = "goods";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);

		public static final String ID = "id";
		public static final String CAPACITY = "capacity";
		public static final String STOCK_NUM = "stock_num";
		public static final String PRICE = "price";

		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + "(_id INTEGER PRIMARY KEY, " + ID + " TEXT, "
				+ CAPACITY + " TEXT, " 
				+ STOCK_NUM + " TEXT, " 
				+ PRICE + " TEXT );";
	}

	public static class SaleRecordTable implements BaseColumns {
		public static final String TABLE_NAME = "sale_record";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);

		public static final String ID = "id";
		public static final String PRICE = "price";
		public static final String TIME = "time";

		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + "(_id INTEGER PRIMARY KEY, " + ID + " TEXT, "
				+ PRICE + " TEXT, " 
				+ TIME + " TEXT );";
	}

	public static class EquipStatusTable implements BaseColumns {
		public static final String TABLE_NAME = "equip_status";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);

		public static final String SBID = "sbId";
		public static final String VERSION = "version";
		public static final String ZBQZS = "zbq_zs";
		public static final String YBQZS = "ybq_zs";
		public static final String STATUS = "status";
		public static final String ZBQSTATUS = "zbq_status";
		public static final String YBQSTATUS = "ybq_status";
		public static final String YSJSTATUS = "ysj_status";
		public static final String KZBSTATUS = "kzb_status";
		public static final String GPRSSTATUS = "gprs_status";
		public static final String NETWORK = "network";
		
		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + "(_id INTEGER PRIMARY KEY, " + SBID + " TEXT, "
				+ VERSION + " TEXT, "
				+ ZBQZS + " TEXT, "
				+ YBQZS + " TEXT, "
				+ STATUS + " TEXT, "
				+ ZBQSTATUS + " TEXT, "
				+ YBQSTATUS + " TEXT, "
				+ YSJSTATUS + " TEXT, "
				+ KZBSTATUS + " TEXT, "
				+ GPRSSTATUS + " TEXT, "
				+ NETWORK + " TEXT );";

	}
}
