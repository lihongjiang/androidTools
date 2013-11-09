package com.bslee.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


public class FastJsonUtil {
	/**
	 * 得到一个TwoObject对象
	 * 
	 * @param <T>
	 * 
	 * @param jsonString    {name：{}，age：20}
	 * @param cls
	 * @return
	 */
	public static <T> T getTwoObject(String jsonString, Class<T> cls) {
		T t = null;
		t = JSON.parseObject(jsonString, cls);
		return t;
	}
	/**
	 * 得到一个Object对象
	 * 
	 * @param <T>
	 * 
	 * @param jsonString  {name：lhj，age：20}
	 * @param cls
	 * @return
	 */
	public static <T> T getObject(String jsonString, Class<T> cls) {
		T t = null;
		t = JSON.parseObject(jsonString, cls);
		return t;
	}

	/**
	 * 得到一个List集合对象
	 * 
	 * @param <T>
	 * 
	 * @param jsonString  [{name:lhj,age:20},{name:lhj，age:20},{name:lhj，age:20}]
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getListObject(String jsonString, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		list = JSON.parseArray(jsonString, cls);
		return list;
	}
	/**
	 * 得到一个Map集合对象
	 * 
	 * @param <T>
	 * 
	 * @param jsonString   {key1:{name:lhj,age:20},key2:{name:lhj，age:20},key3:{name:lhj，age:20}}
	 * @param cls
	 * @return
	 */
	public static <T> Map<String, T> getMapObject(String jsonString,
			Class<T> cls) {
		Map<String, T> map = new HashMap<String, T>();
		map = JSON.parseObject(jsonString, new TypeReference<Map<String, T>>(){});
		return map;
	}

}
