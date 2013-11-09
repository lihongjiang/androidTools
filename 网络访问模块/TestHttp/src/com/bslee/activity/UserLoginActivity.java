package com.bslee.activity;

import com.bslee.User.UserLoginRequestParam;
import com.bslee.User.UserLoginResponseBean;
import com.bslee.http.R;
import com.bslee.net.AsyncTaskManager;
import com.bslee.net.NetDataManager;
import com.bslee.net.RequestListener;
import com.bslee.net.ResponseBean;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class UserLoginActivity extends Activity {
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				textview.setText(NetDataManager.userresult.size()+""+NetDataManager.userresult.get(0).Title);
			}
		};
	};
	private TextView textview = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textview = (TextView) findViewById(R.id.text);
		AsyncTaskManager.getInstance().getVisitor(
				new UserLoginRequestParam("name", "age"), lister);

	}

	RequestListener<UserLoginResponseBean> lister = new RequestListener<UserLoginResponseBean>() {
		@Override
		public void onStart() {

		}

		@Override
		public void onComplete(UserLoginResponseBean bean) {
			// ���ش���Ľ��ʵ��+���ݹ�����
			// ȡ��bean��������Ժ�ִ������ķ���
			// ������Ϣ��handler����
			bean.resolve();
			Message msg = handler.obtainMessage();
	
				msg.what = 1;
				handler.sendMessage(msg);
	
			// �����������������,��������ر��ػ�������
		}
	};
}