package com.bslee.sqlite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BasicDAO extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "HelloOrmlite.db";
	private static final int DATABASE_VERSION = 1;

	public BasicDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Hello.class);
		} catch (SQLException e) {
			Log.e(BasicDAO.class.getName(), "创建数据库失败", e);
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		try {
			TableUtils.dropTable(connectionSource, Hello.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(BasicDAO.class.getName(), "更新数据库失败", e);
			e.printStackTrace();
		}
	}

}
