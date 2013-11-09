package com.bslee.wall;

import java.util.ArrayList;

import java.util.List;

import android.app.Activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bslee.imageload.AsyncImageLoader;
import com.bslee.imageload.AsyncImageLoader.ImageCallback;
import com.bslee.wall.WaterFlipView.OnScrollListener;

public class TestWallfallActivity extends Activity {

	private WaterFlipView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;

	private List<Pic> image_filenames;

	private int itemWidth;

	private int column_count = 2;// 显示列数
	private int page_count = 40;// 每次加载15张图片

	private int current_page = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Loader = new AsyncImageLoader(this, "xiaohua");
		display = this.getWindowManager().getDefaultDisplay();
		itemWidth = display.getWidth() / column_count;// 根据屏幕大小计算每列大小

		InitLayout();

	}

	AsyncImageLoader Loader;

	private void InitLayout() {
		waterfall_scroll = (WaterFlipView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
				// 滚动中
				Log.d("LazyScroll", "Scroll");
			}

			@Override
			public void onBottom() {
				Log.d("LazyScroll", "bottom=============");
				++current_page;
				String url = "http://sc.chinaz.com/tupian/huacaotupian_"
						+ current_page + ".html";
				Log.d("LazyScroll", "bottom=============" + url);
				addData(url);
				// 滚动到最低端
			}
		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		waterfall_items = new ArrayList<LinearLayout>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					itemWidth, LayoutParams.FILL_PARENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}
		addData("http://sc.chinaz.com/tupian/huacaotupian_2.html");
	}

	public void addData(String url) {
		new AsyncTask<String, Void, List<Pic>>() {

			@Override
			protected List<Pic> doInBackground(String... params) {
				return NetUtil.getListPic(params[0]);
			}

			@Override
			protected void onPostExecute(List<Pic> result) {
				image_filenames = result;
				// 第一次加载
				AddItemToContainer(current_page, page_count);
				super.onPostExecute(result);
			}
		}.execute(url);
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int j = 0;
		int imagecount = image_filenames.size();
		for (int i = 0; i < imagecount; i++) {
			j = i % column_count;
			AddImage(image_filenames.get(i).url, j);
		}

	}

	private void AddImage(String filename, int columnIndex) {
		
		LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.waterfallitem, null);
		waterfall_items.get(columnIndex).addView(view);
		ImageView imageView = (ImageView) view.findViewById(R.id.waterfall_image);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText(filename);

		imageView.setTag(filename);
		Bitmap bit = Loader.getBitmap2(filename);
		imageView.setImageResource(android.R.color.white);
		if (bit == null) {
			Loader.downloadImage(filename, new ImageCallback() {
				@Override
				public void onImageLoaded(Bitmap bitmap, String imageUrl) {
					ImageView imageViewByTag = (ImageView) waterfall_container
							.findViewWithTag(imageUrl);
					if (imageViewByTag != null) {
						if (bitmap != null) {
							imageViewByTag.setImageBitmap(bitmap);
						}
					}
				}
			});
		} else {
			imageView.setImageBitmap(bit);
		}

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TestWallfallActivity.this,
						"您点击了" + v.getTag() + "个Item", Toast.LENGTH_SHORT)
						.show();
			}
		});

	}
}
