package com.bslee.User;

import java.util.concurrent.ExecutorService;

import com.bslee.net.HttpUtils;
import com.bslee.net.RequestListener;

public class UserAction {
	// 处理登录事件
	public void asyncGetVisitor(ExecutorService mPool,
			final UserLoginRequestParam param,
			final RequestListener<UserLoginResponseBean> listener) {
		mPool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				// 下载网络数据并封装成实体
				UserLoginResponseBean bean = getUserLogin(param);
				listener.onComplete(bean);
			}
		});
	}

	private UserLoginResponseBean getUserLogin(UserLoginRequestParam param) {
		
		String response = null;
		// 下载数据
		response=HttpUtils.getJsonByGet(param.getParams().get("suburl"));
		return new UserLoginResponseBean(response);
	}
}
