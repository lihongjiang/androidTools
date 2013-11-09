package com.kanghe.sport;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class TestPicActivity extends Activity {
	ListView myimagelist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myimagelist = (ListView) findViewById(R.id.myimagelist);
		myimagelist.setAdapter(new MyImageAdapter(this, myimagelist));
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}