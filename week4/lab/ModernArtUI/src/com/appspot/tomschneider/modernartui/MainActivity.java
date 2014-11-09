package com.appspot.tomschneider.modernartui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = "ModernArtUI";
	
	private SeekBar seekBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		
		//seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar = new SeekBar(getApplicationContext());
		layout.addView(seekBar);
		
		final RectangleSurfaceView mSurface = new RectangleSurfaceView(getApplicationContext());
		layout.addView(mSurface);
		
		seekBar.setVisibility(SeekBar.VISIBLE);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	          float progress = 0;
	           
	          @Override
	          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
	              progress = progresValue;
	              //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
	              mSurface.setAlpha(progress / 100);
	              Log.i(TAG, "Setting alpha: " + (progress / 100));
	              
	          }
	         
	          @Override
	          public void onStartTrackingTouch(SeekBar seekBar) {
	              //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
	          }
	         
	          @Override
	          public void onStopTrackingTouch(SeekBar seekBar) {
	              //textView.setText("Covered: " + progress + "/" + seekBar.getMax());
	              Toast.makeText(getApplicationContext(), "Stopped tracking seekbar, progress: " + progress, Toast.LENGTH_SHORT).show();
	          }
	       });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class RectangleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private final Paint mPainter = new Paint();
		private SurfaceHolder mSurfaceHolder; 
		private Thread rectangleThread;
		private DisplayMetrics mDisplay;
		private int mDisplayWidth, mDisplayHeight;
		
		public RectangleSurfaceView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			
			mDisplay = new DisplayMetrics();
	        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(mDisplay);
	        mDisplayWidth = mDisplay.widthPixels;
	        mDisplayHeight = mDisplay.heightPixels;
	        
			mSurfaceHolder = getHolder();
			mSurfaceHolder.addCallback(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			//this.mSurfaceHolder = holder;
			
			rectangleThread = new Thread( new Runnable() {
				public void run() {
						Canvas canvas = null;
						canvas = mSurfaceHolder.lockCanvas();
						if (null != canvas) {
							drawRectangles(canvas);
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						}
				}
			});
			rectangleThread.start();
			
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			mDisplay = new DisplayMetrics();
	        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(mDisplay);
	        mDisplayWidth = mDisplay.widthPixels;
	        mDisplayHeight = mDisplay.heightPixels;
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			rectangleThread.interrupt();
		}
		
		public void drawRectangles(Canvas canvas) {
			canvas.drawColor(Color.DKGRAY);
			mPainter.setStyle(Paint.Style.FILL_AND_STROKE);
            mPainter.setColor(getResources().getColor(android.R.color.white));
            RectF rect = new RectF(0, 0, mDisplayWidth / 2, mDisplayHeight / 3);
            Log.i(TAG, "Drawing rectangle 1");
            canvas.drawRect(rect, mPainter);
            mPainter.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            rect = new RectF(0, mDisplayHeight / 3, mDisplayWidth / 2, mDisplayHeight * 2 / 3);
            canvas.drawRect(rect, mPainter);
            mPainter.setColor(getResources().getColor(android.R.color.holo_orange_dark));
            rect = new RectF(0, mDisplayHeight * 2 / 3, mDisplayWidth / 2, mDisplayHeight);
            canvas.drawRect(rect, mPainter);
            
            mPainter.setColor(getResources().getColor(android.R.color.holo_green_dark));
            rect = new RectF(mDisplayWidth / 2, 0, mDisplayWidth, mDisplayHeight / 3);
            canvas.drawRect(rect, mPainter);
            mPainter.setColor(getResources().getColor(android.R.color.holo_red_dark));
            rect = new RectF(mDisplayWidth / 2, mDisplayHeight / 3, mDisplayWidth, mDisplayHeight * 2 / 3);
            canvas.drawRect(rect, mPainter);
            mPainter.setColor(getResources().getColor(android.R.color.holo_purple));
            rect = new RectF(mDisplayWidth / 2, mDisplayHeight * 2 / 3, mDisplayWidth, mDisplayHeight);
            canvas.drawRect(rect, mPainter);
		}

	}
}
