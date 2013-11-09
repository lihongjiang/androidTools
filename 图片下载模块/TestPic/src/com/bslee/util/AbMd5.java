/*
 * 
 */
package com.bslee.util;

import java.security.MessageDigest;

public class AbMd5 {
	
	/**
	 * ������MD5����.
	 * @param str Ҫ���ܵ��ַ���
	 * @return String ���ܵ��ַ���
	 */
	public final static String MD5(String str) {
		char hexDigits[] = { // �������ֽ�ת���� 16 ���Ʊ�ʾ���ַ�
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			byte[] strTemp = str.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte tmp[] = mdTemp.digest(); // MD5 �ļ�������һ�� 128 λ�ĳ�������
			// ���ֽڱ�ʾ���� 16 ���ֽ�
			char strs[] = new char[16 * 2]; // ÿ���ֽ��� 16 ���Ʊ�ʾ�Ļ���ʹ�������ַ���
			// ���Ա�ʾ�� 16 ������Ҫ 32 ���ַ�
			int k = 0; // ��ʾת������ж�Ӧ���ַ�λ��
			for (int i = 0; i < 16; i++) { // �ӵ�һ���ֽڿ�ʼ���� MD5 ��ÿһ���ֽ�
				// ת���� 16 �����ַ���ת��
				byte byte0 = tmp[i]; // ȡ�� i ���ֽ�
				strs[k++] = hexDigits[byte0 >>> 4 & 0xf]; // ȡ�ֽ��и� 4 λ������ת��,
				// >>> Ϊ�߼����ƣ�������λһ������
				strs[k++] = hexDigits[byte0 & 0xf]; // ȡ�ֽ��е� 4 λ������ת��
			}
			return new String(strs).toUpperCase(); // ����Ľ��ת��Ϊ�ַ���
		} catch (Exception e) {
			return null;
		}
	}

	
	public static void main(String[] args) {
		System.out.println(AbMd5.MD5("2011123456").toLowerCase());
	}

}
