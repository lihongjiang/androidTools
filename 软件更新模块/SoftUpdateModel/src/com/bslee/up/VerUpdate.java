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
	 * 当前的版本号
	 */
	private int vercode;

	/**
	 * 服务器端的版本号
	 */
	private int newVerCode = 0;

	/**
	 * 新的版本名称
	 */
	private String newVerName = "";
	/**
	 * 新版本更新内容
	 */
	private String updateContent = "";
	/**
	 * 下载进度条
	 */
	public ProgressDialog pBar;

	/**
	 * 获取要比较的服务器端版本信息文件 一般为 ver.json
	 */
	private String updateVersion = "ver.json";

	/**
	 * 要获取的服务器端版本的文件夹路径
	 */
	private String updateServer = "";

	/**
	 * 存储在本地的文件名
	 */
	private String saveApkName = "";

	/**
	 * 存储在服务器端的程序名称
	 */
	private String serverApkName = "";

	Context c;

	private Handler handler = new Handler();

	/**
	 * 升级类构造函数
	 * 
	 * @param _c
	 *            要调用的页面
	 * @param _updateServer
	 *            服务器端版本的文件夹网络地址最后以\结束
	 * @param _updateVersion
	 *            比较的服务器端版本信息文件 一般为 ver.json
	 * @param _saveApkName
	 *            存储在本地的文件名
	 * @param _serverApkName
	 *            要下载的服务器端的文件名
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
	 * 执行升级函数，首先判断本地和服务器端版本号，然后提示是否升级
	 * 
	 * @param alert
	 *            当不需要升级时，是否弹出提示
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
		// 是否升级

	}

	private void notNewVersionShow() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				int verCode = getVerCode();
				String verName = getVerName();
				StringBuffer sb = new StringBuffer();
				sb.append("当前版本:");
				sb.append(verName);
				// sb.append(" Code:");
				// sb.append(verCode);
				sb.append(",\n已是最新版,无需更新!");
				Dialog dialog = new AlertDialog.Builder(c).setTitle("温馨提示")
						.setMessage(sb.toString())
						// 设置内容
						.setPositiveButton("确定",
						// 设置确定按钮
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();// 创建
				// 显示对话框
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

	/*-----------------------------检测更新---------------------------------------------------*/
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
				sb.append("版本号:");
				sb.append(newVerName);
				sb.append("\n功能描述:\n");
				sb.append(updateContent);
				Dialog dialog = new AlertDialog.Builder(c)
						.setTitle("版本信息")
						.setMessage(sb.toString())
						// 设置内容
						.setPositiveButton("忽略",
						// 设置确定按钮
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setNegativeButton("立即更新",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int whichButton) {
										pBar = new ProgressDialog(c);
										pBar.setTitle("正在下载");
										pBar.setMessage("请稍候...");
										pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										downFile(updateServer + serverApkName);
									}
								}).create();// 创建
				// 显示对话框
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

