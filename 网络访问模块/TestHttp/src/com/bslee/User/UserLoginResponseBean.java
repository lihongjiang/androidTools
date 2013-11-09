package com.bslee.User;

import java.util.ArrayList;
import java.util.List;

import com.bslee.net.FastJsonUtil;
import com.bslee.net.NetDataManager;
import com.bslee.net.ResponseBean;

public class UserLoginResponseBean extends ResponseBean {
	public boolean isOver = false;// ��ҳ������������
	private String response = null;
	public boolean success = false;

	public UserLoginResponseBean(String response) {
		super(response);
		this.response = response;
	}

	/**
	 * һ���Խ���ʵ������,���ڹ��캯����
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
	 * ׷������,��Ҫ�ڼ������д���
	 * 
	 * @param isadd
	 */
	public void resolve(boolean isAdd) {

		if (!isAdd) {
			// ��������,������
			NetDataManager.userresult = FastJsonUtil.getListObject(response,
					UserResult.class);
		} else {
			// �������ݳ��Ȳ���10��
			List<UserResult> result = new ArrayList<UserResult>();
			if (result.size() < 10) {
				isOver = true;
			}
			NetDataManager.userresult.addAll(result);
		}
	}
}
