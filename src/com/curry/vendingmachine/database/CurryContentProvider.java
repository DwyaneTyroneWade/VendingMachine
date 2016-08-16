package com.curry.vendingmachine.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.curry.vendingmachine.database.CurryTables.EquipStatusTable;
import com.curry.vendingmachine.database.CurryTables.GoodsTable;
import com.curry.vendingmachine.database.CurryTables.SaleRecordTable;

public class CurryContentProvider extends ContentProvider {
	private static final int GOODS = 1000;
	private static final int SALERECORD = 1001;
	private static final int EQUIPSTATUS = 1002;

	private static final UriMatcher MATCHER;

	static {
		MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		MATCHER.addURI(CurryTables.AUTHORITY, GoodsTable.TABLE_NAME, GOODS);
		MATCHER.addURI(CurryTables.AUTHORITY, SaleRecordTable.TABLE_NAME,
				SALERECORD);
		MATCHER.addURI(CurryTables.AUTHORITY, EquipStatusTable.TABLE_NAME,
				EQUIPSTATUS);
	}

	private CurryDatabaseHelper mHelper;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mHelper = new CurryDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = mHelper.getReadableDatabase();
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		final int match = MATCHER.match(uri);
		String groupBy = null;
		String having = null;
		switch (match) {
		case GOODS:
			builder.setTables(GoodsTable.TABLE_NAME);
			break;
		case SALERECORD:
			builder.setTables(SaleRecordTable.TABLE_NAME);
			break;
		case EQUIPSTATUS:
			builder.setTables(EquipStatusTable.TABLE_NAME);
			break;
		default:
			throw new UnsupportedOperationException("unkown uri:"
					+ uri.toString());
		}
		String limit = uri.getQueryParameter("limit");
		Cursor c = null;
		if (!TextUtils.isEmpty(limit)) {
			c = builder.query(db, projection, selection, selectionArgs,
					groupBy, having, sortOrder, limit);
		} else {
			c = builder.query(db, projection, selection, selectionArgs,
					groupBy, having, sortOrder);
		}

		if (c != null) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		final int match = MATCHER.match(uri);
		long rowId = 0;
		switch (match) {
		case GOODS:
			rowId = mHelper.getWritableDatabase().insert(GoodsTable.TABLE_NAME,
					null, values);
			break;
		case SALERECORD:
			rowId = mHelper.getWritableDatabase().insert(
					SaleRecordTable.TABLE_NAME, null, values);
			break;
		case EQUIPSTATUS:
			rowId = mHelper.getWritableDatabase().insert(
					EquipStatusTable.TABLE_NAME, null, values);
			break;

		default:
			throw new UnsupportedOperationException("unkown uri:"
					+ uri.toString());
		}

		if (rowId > 0) {
			getContext().getContentResolver().notifyChange(uri, null, false);
			return ContentUris.withAppendedId(uri, rowId);
		}

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		final int match = MATCHER.match(uri);
		String table;
		switch (match) {
		case GOODS:
			table = GoodsTable.TABLE_NAME;
			break;
		case SALERECORD:
			table = SaleRecordTable.TABLE_NAME;
			break;
		case EQUIPSTATUS:
			table = EquipStatusTable.TABLE_NAME;
			break;
		default:
			throw new UnsupportedOperationException("unkown uri:"
					+ uri.toString());
		}

		final SQLiteDatabase db = mHelper.getWritableDatabase();
		final int count = db.delete(table, selection, selectionArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null, false);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = mHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		String table;
		switch (match) {
		case GOODS:
			table = GoodsTable.TABLE_NAME;
			break;
		case SALERECORD:
			table = SaleRecordTable.TABLE_NAME;
			break;
		case EQUIPSTATUS:
			table = EquipStatusTable.TABLE_NAME;
			break;
		default:
			throw new UnsupportedOperationException("unkown uri:"
					+ uri.toString());
		}

		int count = db.update(table, values, selection, selectionArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null, false);
		}
		return count;
	}

}
