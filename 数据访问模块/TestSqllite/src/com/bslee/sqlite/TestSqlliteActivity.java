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
			tv.setText(tv.getText() + "\n" + "����������");
			// ��ѯ��ӵ�����
			List<Hello> hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
			// ɾ�����ݵ�һ������
			hellodao.delete(hellos.get(0));
			tv.setText(tv.getText() + "\n" + "ɾ���������");
			// ���²�ѯ����
			hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
			// �޸�����
			Hello h1 = hellos.get(0);
			h1.setWord("�����޸Ĺ�������");
			tv.setText(tv.getText() + "\n" + "�޸��������");
			hellodao.update(h1);
			// ���²�ѯ����
			hellos = hellodao.findall();
			for (Hello h : hellos) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
