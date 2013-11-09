package com.bslee.sqlite;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class HelloDao {
	public Dao<Hello, Integer> dao = null;
	public BasicDAO helper = null;
	public Context context = null;

	public HelloDao(Context context) {
		this.context = context;
		helper = OpenHelperManager.getHelper(context, BasicDAO.class);
		try {
			dao = helper.getDao(Hello.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ��������
	public void add() throws SQLException {
		for (int i = 0; i < 2; i++) {
			Hello hello = new Hello("Hello" + i);
			dao.create(hello);
		}
	}

	// ɾ������
	public void delete(Hello hello) throws SQLException {
		dao.delete(hello);
	}

	// ��������
	public void update(Hello hello) throws SQLException {
		dao.update(hello);
	}

	// ��������
	public List<Hello> findall() throws SQLException {
		return  dao.queryForAll();
	}
}
