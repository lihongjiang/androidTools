package com.bslee.User;

import java.util.ArrayList;
import java.util.List;

import com.bslee.net.FastJsonUtil;
import com.bslee.net.NetDataManager;
import com.bslee.net.ResponseBean;

public class UserLoginResponseBean extends ResponseBean {
	public boolean isOver = false;// 分页加载数据完标记
	private String response = null;
	public boolean success = false;

	public UserLoginResponseBean(String response) {
		super(response);
		this.response = response;
	}

	/**
	 * 一次性解析实体数据,放在构造函数中
	 * 
	 * @param response
	 */
	public void resolve() {
		NetDataManager.userresult = FastJsonUtil.getListObject(response,
				UserResult.class);
		if (NetDataManager.userresult != null
				&& NetDataManager.userresult.size() > 0) {
			success = true;
		}
	}

	/**
	 * 追加数据,需要在监听器中处理
	 * 
	 * @param isadd
	 */
	public void resolve(boolean isAdd) {

		if (!isAdd) {
			// 不是增加,新数组
			NetDataManager.userresult = FastJsonUtil.getListObject(response,
					UserResult.class);
		} else {
			// 解析数据长度不是10个
			List<UserResult> result = new ArrayList<UserResult>();
			if (result.size() < 10) {
				isOver = true;
			}
			NetDataManager.userresult.addAll(result);
		}
	}
}
