package com.bslee.sqlite;

import java.sql.SQLException;
import java.util.List;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestSqlliteActivity extends Activity {

	HelloDao hellodao = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv = (TextView) this.findViewById(R.id.output);
		hellodao = new HelloDao(this);
		try {
			hellodao.add();
			tv.setText(tv.getText() + "\n" + "添加数据完成");
			// 查询添加的数据
			List<Hello> hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
			// 删除数据第一条数据
			hellodao.delete(hellos.get(0));
			tv.setText(tv.getText() + "\n" + "删除数据完成");
			// 重新查询数据
			hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
			// 修改数据
			Hello h1 = hellos.get(0);
			h1.setWord("这是修改过的数据");
			tv.setText(tv.getText() + "\n" + "修改数据完成");
			hellodao.update(h1);
			// 重新查询数据
			hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
