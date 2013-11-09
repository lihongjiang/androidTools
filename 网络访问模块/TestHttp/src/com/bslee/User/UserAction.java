package com.bslee.User;

import java.util.concurrent.ExecutorService;

import com.bslee.net.HttpUtils;
import com.bslee.net.RequestListener;

public class UserAction {
	// �����¼�¼�
	public void asyncGetVisitor(ExecutorService mPool,
			final UserLoginRequestParam param,
			final RequestListener<UserLoginResponseBean> listener) {
		mPool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				// �����������ݲ���װ��ʵ��
				UserLoginResponseBean bean = getUserLogin(param);
				listener.onComplete(bean);
			}
		});
	}

	private UserLoginResponseBean getUserLogin(UserLoginRequestParam param) {
		
		String response = null;
		// ��������
		response=HttpUtils.getJsonByGet(param.getParams().get("suburl"));
		return new UserLoginResponseBean(response);
	}
}
