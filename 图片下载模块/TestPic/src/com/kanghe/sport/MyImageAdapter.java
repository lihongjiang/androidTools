package com.kanghe.sport;

import com.bslee.imageload.AsyncImageLoader;
import com.bslee.imageload.AsyncImageLoader.ImageCallback;

import android.app.Activity;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.ListView;

public class MyImageAdapter extends BaseAdapter {
	public String[] data = new String[] {
			"http://img6.paipaiimg.com/d3eb8175/item-0FAA86BD-760DD4A400000000040100000E68261A.0.300x300.jpg",
			"http://t01.pic.sogou.com/909ee041115b08cb.jpg",
			"http://t01.pic.sogou.com/15dd01050fdad689.jpg",
			"http://t03.pic.sogou.com/509e02a9a007029b.jpg",
			"http://t02.pic.sogou.com/af6778d33e33729d.jpg",
			"http://t04.pic.sogou.com/7c2907b363ab94ec.jpg",
			"http://t02.pic.sogou.com/01861ad50fdad689.jpg",
			"http://t03.pic.sogou.com/2b2fdba0aa01d6d7_i.jpg",
			"http://g.hiphotos.baidu.com/pic/w%3D230/sign=417dc8087e3e6709be0042fc0bc69fb8/9f510fb30f2442a73fc8c98ad043ad4bd01302de.jpg",
			"http://b.hiphotos.baidu.com/pic/w%3D230/sign=f87fb5166f061d957d46303b4bf50a5d/6a600c338744ebf84b32a811d8f9d72a6059a759.jpg",
			"http://e.hiphotos.baidu.com/pic/w%3D230/sign=79c528d6a686c9170803553af93d70c6/e7cd7b899e510fb3e32bbbb9d833c895d1430cea.jpg",
			"http://g.hiphotos.baidu.com/pic/w%3D230/sign=4b23fd6503087bf47dec50eac2d2575e/eaf81a4c510fd9f9819c9747242dd42a2834a411.jpg",
			"http://b.hiphotos.baidu.com/pic/w%3D230/sign=8e44c82271cf3bc7e800caefe100babd/43a7d933c895d1434406d00c72f082025aaf07a8.jpg",
			"http://g.hiphotos.baidu.com/pic/w%3D230/sign=f14ceae71b4c510faec4e51950592528/4a36a",
			"http://d.hiphotos.baidu.com/pic/w%3D230/sign=dae96d99c75c1038247ec9c18210931c/e61190ef76c6a7effda2c5a5fcfaaf51f2de66a8.jpg",
			"http://g.hiphotos.baidu.com/pic/w%3D230/sign=867d165f55e736d158138b0bab514ffc/cdbf6c81800a19d8824ddc8632fa828ba71e46e2.jpg",
			"http://h.hiphotos.baidu.com/pic/w%3D230/sign=3fe20cf0faf2b211e42e824dfa816511/a6efce1b9d16fdfa808bac6db58f8c5495ee7b49.jpg",
			"http://h.hiphotos.baidu.com/pic/w%3D230/sign=44cc74e70823dd542173a06be10bb3df/c2cec3fdfc0392455957e98c8694a4c27c1e2556.jpg",
			"http://f.hiphotos.baidu.com/pic/w%3D230/sign=238221d5d1160924dc25a518e406359b/728da9773912b31b252e8baa8718367adbb4e1f7.jpg",
			"http://c.hiphotos.baidu.com/pic/w%3D230/sign=e0ab8e63eac4b7453494b015fffd1e78/63d9f2d3572c11df4312b769622762d0f603c2bd.jpg",
			"http://a.hiphotos.baidu.com/pic/w%3D230/sign=ed628979d0c8a786be2a4d0d5708c9c7/34fae6cd7b899e51c7ce139743a7d933c9950df4.jpg" };
	Activity testPicActivity;
	ListView listview;
	AsyncImageLoader Loader;

	public MyImageAdapter(Activity testPicActivity, ListView listview) {
		this.testPicActivity = testPicActivity;
		this.listview = listview;
		Loader = new AsyncImageLoader(testPicActivity, "xiaohua");
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(testPicActivity).inflate(
					R.layout.list_item, null);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.listimage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageview.setTag(data[position]);
		Bitmap bit = Loader.getBitmap2(data[position]);
		holder.imageview.setImageResource(android.R.color.white);
		if (bit == null) {
			Loader.downloadImage(data[position], new ImageCallback() {
				@Override
				public void onImageLoaded(Bitmap bitmap, String imageUrl) {
					ImageView imageViewByTag = (ImageView) listview
							.findViewWithTag(imageUrl);
					if (imageViewByTag != null) {
						if (bitmap != null) {
							imageViewByTag.setImageBitmap(bitmap);
						}
					}
				}
			});
		} else {
			holder.imageview.setImageBitmap(bit);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageview;
	}
}
