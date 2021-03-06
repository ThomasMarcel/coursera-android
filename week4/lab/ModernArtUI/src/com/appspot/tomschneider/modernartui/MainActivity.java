package com.appspot.tomschneider.modernartui;

import java.math.BigDecimal;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
	private float alpha;
	private final Handler mHandler = new Handler();
	
	/*final Runnable mUpdateAlpha = new Runnable() {
		public void run() {
			updateAlphaInUI();
		}
	};*/

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
	          int progress = 0;
	           
	          @Override
	          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
	              progress = progresValue;
	              //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
	              //changeAlpha(mSurface, progress);
	          }
	         
	          @Override
	          public void onStartTrackingTouch(SeekBar seekBar) {
	              //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
	          }
	         
	          @Override
	          public void onStopTrackingTouch(SeekBar seekBar) {
	              //textView.setText("Covered: " + progress + "/" + seekBar.getMax());
	              Toast.makeText(getApplicationContext(), progress + "%", Toast.LENGTH_SHORT).show();
	              //changeAlpha(mSurface, progress);
	              mSurface.setAlphaPercent(progress);
	          }
	       });
	}
	
	public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd.floatValue();
    }
	
	public void changeAlpha(final RectangleSurfaceView view, final int progress) {
		//Thread alphaThread = new Thread(new Runnable() {
			//public void run() {
				alpha = 1 - ((float) progress / 100);
				alpha = round(alpha, 1);
				//Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Alpha: " + alpha);
				//view.getBackground().setAlpha(alpha);
				view.animate().alpha(alpha).setDuration(30000);
				//view.animate().alpha(1f).setDuration(3000);
				Log.i(TAG, "Progress: " + progress + ".Setting alpha: " + alpha);
			//}
		//});
		//alphaThread.start();
	}
	
	/*private void updateAlphaInUI() {
		view.setAlpha(alpha);
		view.postInvalidate();
	}*/

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
		switch (id) {
			case  R.id.action_moma:
				Log.i(TAG, "MOMA selected");
				//Toast.makeText(getApplicationContext(), "Visit MOMA", Toast.LENGTH_LONG);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.moma.org"));
				startActivity(browserIntent);
				break;
			case R.id.action_notnow:
				//Toast.makeText(getApplicationContext(), "Not Now", Toast.LENGTH_LONG);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class RectangleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		private final Paint mPainter = new Paint();
		private SurfaceHolder mSurfaceHolder; 
		private Thread rectangleThread;
		private DisplayMetrics mDisplay;
		private int mDisplayWidth, mDisplayHeight;
		private int alpha;
		
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
			setWillNotDraw(false);
			// TODO Auto-generated method stub
			//this.mSurfaceHolder = holder;
		}
		
		@Override
		public void onDraw(Canvas canvas) {
			rectangleThread = new Thread( new Runnable() {
				public void run() {
						Canvas canvas = null;
						canvas = mSurfaceHolder.lockCanvas();
						if (null != canvas) {
							drawRectangles(canvas);
							canvas.saveLayerAlpha(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						}
				}
			});
			rectangleThread.start();
			
		}
		
		private int getAlphaPercent() {
			return alpha;
		}
		
		private void setAlphaPercent(int percent) {
			//rectangleThread.interrupt();
			alpha = (int) ((float) percent / 100 * 255);
			invalidate();
			postInvalidate();
			Log.i(TAG, "Setting alpha to " + alpha);
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
            Log.i(TAG, "Drawing rectangles with alpha " + alpha);
            //canvas.save();
			canvas.drawColor(Color.DKGRAY);
			mPainter.setStyle(Paint.Style.FILL_AND_STROKE);
            mPainter.setColor(getResources().getColor(android.R.color.white));
            RectF rect = new RectF(0, 0, mDisplayWidth / 2, mDisplayHeight / 3);
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
            canvas.saveLayerAlpha(new RectF(0, 0, mDisplayWidth, mDisplayHeight), alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
            //canvas.restore();
		}

	}
}
