package com.tomschneider.dailyselfie;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class SelfieActivity extends Activity {
	private static final String TAG = "Daily-Selfie";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_selfie);
		
		Intent intent = getIntent();
		
		File currFile = new File(intent.getStringExtra(MainActivity.EXTRA_BMP_FILEPATH));
		if (currFile.exists()) {
			ImageView imageView = (ImageView) findViewById(R.id.selfie_full_pic);
			Bitmap selfiePic = BitmapFactory.decodeFile(currFile.getAbsolutePath());
			
			Log.i(TAG, "file " + currFile.getAbsolutePath() + " with path" + intent.getStringExtra(MainActivity.EXTRA_BMP_FILEPATH) + " exists");
			imageView.setImageBitmap(selfiePic);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selfie, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_back) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
