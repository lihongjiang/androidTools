package com.bslee.User;

import java.util.HashMap;
import java.util.Map;

import com.bslee.net.RequestParam;

public class UserLoginRequestParam extends RequestParam {
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String USERLOGIN_URL = "http://m.bitauto.com/appapi/News/List.ashx/";
	private Map<String, String> mMap = null;

	public UserLoginRequestParam(String name, String age) {
		mMap = new HashMap<String, String>();
		mMap.put(NAME, name);
		mMap.put(PASSWORD, age);
		mMap.put("suburl", USERLOGIN_URL);
	}

	@Override
	public Map<String, String> getParams() {

		return mMap;
	}

}
