package com.bslee.imageload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import com.bslee.util.AbMd5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ImageFileCache {
	// 图片缓存文件夹，默认为LHJCache
	private String CACHDIR = "LHJCache";
	// 图片后缀名
	private static final String WHOLESALE_CONV = ".temp";
	private static final int MB = 1024 * 1024;
	// 缓存大小
	private static final int CACHE_SIZE = 10;
	// 手机剩余空间
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

	public ImageFileCache() {
		// 清除文件夹下的缓存
		removeCache(getDirectory());
	}

	// 设置新缓存文件夹
	public void setCACHDIR(String cACHDIR) {
		CACHDIR = cACHDIR;
	}

	/**
	 * 从文件夹中得到图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getImage(final String url) {

		final String path = getDirectory() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {

			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileTime(path);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 保存下载的图片
	 * 
	 * @param bm
	 * @param url
	 */
	public void saveBitmap(Bitmap bm, String url) {
		if (bm == null) {
			return;

		}
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {

			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getDirectory();
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException");
		}
	}

	/**
	 * 缓存内容超过10M时候,清楚一部分内容
	 * 
	 * @param dirPath
	 * @return
	 */
	private boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return true;
		}
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return false;
		}
		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(WHOLESALE_CONV)) {
				dirSize += files[i].length();
			}
		}
		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			for (int i = 0; i < removeFactor; i++) {
				if (files[i].getName().contains(WHOLESALE_CONV)) {
					files[i].delete();
				}
			}
		}
		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}
		return true;
	}

	/**
	 * 更新文件时间
	 * 
	 * @param path
	 */
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/**
	 * SD卡剩余空间
	 * 
	 * @return
	 */
	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/**
	 * MD5加密url
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToFileName(String url) {
		return AbMd5.MD5(url) + WHOLESALE_CONV;
	}

	@SuppressWarnings("unused")
	private String convertUrlToFileName2(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + WHOLESALE_CONV;
	}

	/**
	 * 得到文件目录
	 * 
	 * @return
	 */
	private String getDirectory() {
		String dir = getSDPath() + "/" + CACHDIR;
		return dir;
	}

	/**
	 * 得到SD卡路径
	 * 
	 * @return
	 */
	private String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	/**
	 * 检查文件是否最新
	 */
	private class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}