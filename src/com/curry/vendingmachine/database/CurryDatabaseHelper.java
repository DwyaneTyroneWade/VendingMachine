package com.curry.vendingmachine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.curry.vendingmachine.database.CurryTables.EquipStatusTable;
import com.curry.vendingmachine.database.CurryTables.GoodsTable;
import com.curry.vendingmachine.database.CurryTables.SaleRecordTable;

public class CurryDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "curry.db";
	private static final int DB_VERSION = 1;

	public CurryDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(GoodsTable.SQL_CREATE);
		db.execSQL(SaleRecordTable.SQL_CREATE);
		db.execSQL(EquipStatusTable.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
