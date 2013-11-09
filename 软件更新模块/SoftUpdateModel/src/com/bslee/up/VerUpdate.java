package com.bslee.up;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class VerUpdate {
	/**
	 * ��ǰ�İ汾��
	 */
	private int vercode;

	/**
	 * �������˵İ汾��
	 */
	private int newVerCode = 0;

	/**
	 * �µİ汾����
	 */
	private String newVerName = "";
	/**
	 * �°汾��������
	 */
	private String updateContent = "";
	/**
	 * ���ؽ�����
	 */
	public ProgressDialog pBar;

	/**
	 * ��ȡҪ�Ƚϵķ������˰汾��Ϣ�ļ� һ��Ϊ ver.json
	 */
	private String updateVersion = "ver.json";

	/**
	 * Ҫ��ȡ�ķ������˰汾���ļ���·��
	 */
	private String updateServer = "";

	/**
	 * �洢�ڱ��ص��ļ���
	 */
	private String saveApkName = "";

	/**
	 * �洢�ڷ������˵ĳ�������
	 */
	private String serverApkName = "";

	Context c;

	private Handler handler = new Handler();

	/**
	 * �����๹�캯��
	 * 
	 * @param _c
	 *            Ҫ���õ�ҳ��
	 * @param _updateServer
	 *            �������˰汾���ļ��������ַ�����\����
	 * @param _updateVersion
	 *            �Ƚϵķ������˰汾��Ϣ�ļ� һ��Ϊ ver.json
	 * @param _saveApkName
	 *            �洢�ڱ��ص��ļ���
	 * @param _serverApkName
	 *            Ҫ���صķ������˵��ļ���
	 */
	public VerUpdate(Context _c, String _updateServer, String _updateVersion,
			String _saveApkName, String _serverApkName) {
		this.c = _c;
		vercode = getVerCode();

		updateServer = _updateServer;
		updateVersion = _updateVersion;
		saveApkName = _saveApkName;
		serverApkName = _serverApkName;
	}

	/**
	 * ִ�����������������жϱ��غͷ������˰汾�ţ�Ȼ����ʾ�Ƿ�����
	 * 
	 * @param alert
	 *            ������Ҫ����ʱ���Ƿ񵯳���ʾ
	 */
	public void Update(boolean alert) {
		final boolean bAlert = alert;
		new Thread() {
			@Override
			public void run() {
				if (getServerVerCode()) {
					if (newVerCode > vercode) {
						doNewVersionUpdate();
					} else {
						if (bAlert) {
							notNewVersionShow();
						}
					}
				}
			}
		}.start();
		// �Ƿ�����

	}

	private void notNewVersionShow() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				int verCode = getVerCode();
				String verName = getVerName();
				StringBuffer sb = new StringBuffer();
				sb.append("��ǰ�汾:");
				sb.append(verName);
				// sb.append(" Code:");
				// sb.append(verCode);
				sb.append(",\n�������°�,�������!");
				Dialog dialog = new AlertDialog.Builder(c).setTitle("��ܰ��ʾ")
						.setMessage(sb.toString())
						// ��������
						.setPositiveButton("ȷ��",
						// ����ȷ����ť
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();// ����
				// ��ʾ�Ի���
				dialog.show();
			}
		});

	}

	public int getVerCode() {
		int verCode = -1;
		try {
			String packageName = c.getPackageName();
			verCode = c.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

	public String getVerName() {
		String verName = "";
		try {
			String packageName = c.getPackageName();
			verName = c.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return verName;
	}

	/*-----------------------------������---------------------------------------------------*/
	private boolean getServerVerCode() {

		try {
			String verjson = NetworkTool.getContent(updateServer
					+ updateVersion);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					newVerName = obj.getString("verName");
					updateContent = obj.getString("updateContent");
				} catch (Exception e) {
					newVerCode = -1;
					newVerName = "";
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void doNewVersionUpdate() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				int verCode = getVerCode();
				String verName = getVerName();
				StringBuffer sb = new StringBuffer();
				sb.append("�汾��:");
				sb.append(newVerName);
				sb.append("\n��������:\n");
				sb.append(updateContent);
				Dialog dialog = new AlertDialog.Builder(c)
						.setTitle("�汾��Ϣ")
						.setMessage(sb.toString())
						// ��������
						.setPositiveButton("����",
						// ����ȷ����ť
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setNegativeButton("��������",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int whichButton) {
										pBar = new ProgressDialog(c);
										pBar.setTitle("��������");
										pBar.setMessage("���Ժ�...");
										pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										downFile(updateServer + serverApkName);
									}
								}).create();// ����
				// ��ʾ�Ի���
				dialog.show();
			}
		});

	}

	public String aa = "";

	void downFile(final String url) {
		aa = url;
		pBar.show();
		new Thread() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File dir = Environment.getExternalStorageDirectory();
						File file = new File(dir, saveApkName);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {

							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	void down() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				pBar.cancel();
				update();
			}
		});
	}

	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), saveApkName)),
				"application/vnd.android.package-archive");
		c.startActivity(intent);
	}
}

