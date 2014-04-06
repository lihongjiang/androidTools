package com.bslee.MeSport.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopWindowTools {

	PopupWindow popupWindow;

	public void showPopUp(View v, Activity activity) {
		LinearLayout layout = new LinearLayout(activity);
		layout.setBackgroundColor(Color.GRAY);
		TextView tv = new TextView(activity);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tv.setText("I'm a pop -----------------------------!");
		tv.setTextColor(Color.WHITE);
		layout.addView(tv);

		popupWindow = new PopupWindow(layout, 120, 120);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);
		// 上边
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
		location[1] - popupWindow.getHeight());

		// 下方：
		// popupWindow.showAsDropDown(v);
		// popupWindow.showAsDropDown(v,200,0);
		// 左边
		// popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0]
		// - popupWindow.getWidth(), location[1]);
		// 右边
		// popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
		// location[0] + v.getWidth(), location[1]);
	}

	//
	// LayoutInflater mLayoutInflater = (LayoutInflater)
	// getSystemService(LAYOUT_INFLATER_SERVICE);
	// ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
	// R.layout.tips, null, true);
	// pw = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT, true);
	// // 设置点击返回键使其消失，且不影响背景，此时setOutsideTouchable函数即使设置为false
	// // 点击PopupWindow 外的屏幕，PopupWindow依然会消失；相反，如果不设置BackgroundDrawable
	// // 则点击返回键PopupWindow不会消失，同时，即时setOutsideTouchable设置为true
	// // 点击PopupWindow 外的屏幕，PopupWindow依然不会消失
	// pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	// pw.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？
	// pw.setAnimationStyle(R.style.PopupAnimation); // 设置动画
	//
	// // 计算x轴方向的偏移量，使得PopupWindow在Title的正下方显示，此处的单位是pixels
	// int xoffInPixels =
	// ScreenTools.getInstance(PopDemoActivity.this).getWidth() / 2 -
	// titleName.getWidth() / 2;
	// // 将pixels转为dip
	// int xoffInDip =
	// ScreenTools.getInstance(PopDemoActivity.this).px2dip(xoffInPixels);
	// pw.showAsDropDown(titleName, -xoffInDip, 0);
	// //pw.showAsDropDown(titleName);
	// pw.update();
	//
	// TextView tv = (TextView) menuView.findViewById(R.id.tips_ok);
	// tv.setOnClickListener(new View.OnClickListener() {
	//
	// public void onClick(View v) {
	// pw.dismiss();
	// }
	//
	// });
}
