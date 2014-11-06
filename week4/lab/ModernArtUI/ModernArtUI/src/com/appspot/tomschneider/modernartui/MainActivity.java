package com.appspot.tomschneider.modernartui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class MainActivity extends Activity {

	RelativeLayout relativeLayout;
	
	private static final String TAG = "Modern-Art-UI";
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "MainActivity.onCreate()");
		setContentView(R.layout.activity_main);
		
		relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
		
		//mDisplayWidth = relativeLayout.getWidth();
		//mDisplayHeight = relativeLayout.getHeight();
		
		RectangleView rectangleView1 = new RectangleView(getApplicationContext());
		relativeLayout.addView(rectangleView1);
		Log.i(TAG, "Rectangle at " + rectangleView1.getPosition());
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
	
	private class RectangleView extends SurfaceView implements SurfaceHolder.Callback {
		
		private final SurfaceHolder mSurfaceHolder;
        private final Paint mPainter = new Paint();
        private Thread mDrawingThread;
        private final int[] size, position;
        private final Random r = new Random();
        private DisplayMetrics mDisplay;
        private int mDisplayWidth, mDisplayHeight;
		
		
		public RectangleView(Context context) {
			super(context);
			
			mDisplay = new DisplayMetrics();
            MainActivity.this.getWindowManager().getDefaultDisplay()
                            .getMetrics(mDisplay);
            mDisplayWidth = mDisplay.widthPixels;
            mDisplayHeight = mDisplay.heightPixels;
			
			mPainter.setAntiAlias(true);

            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
            
            size = new int[2];
            position = new int[2];
            
            setSizeAndPosition();
		}
		
		private void setSizeAndPosition() {
			//Check if there are already rectangles, then draw accordingly to occupy all screen space
			for (int i = 0; i < relativeLayout.getChildCount(); i++) {
				if(i != 0) {
					//CHeck if the second rectangle is longer that the first
				}
			}
			
			if(mDisplayWidth == 0 || mDisplayHeight == 0) {
				mDisplayWidth = relativeLayout.getWidth();
				mDisplayHeight = relativeLayout.getHeight();
			}
			Log.i(TAG, "Layout dimensions: [" + mDisplayWidth + ":" + mDisplayHeight + "]");
			int width = 0;
			while (width < mDisplayWidth * 1 / 3) {
				width = r.nextInt(mDisplayWidth * 2 / 3);
			}
			int height = 0;
			while (height < mDisplayHeight * 1 / 4) {
				height = r.nextInt(mDisplayHeight * 2 / 3);
			}
			size[0] = width;
			size[1] = height;
			
			int x = 250;
			int y = 250;
			position[0] = x;
			position[1] = y;
			
		}
		
		public int[] getPosition() {
			return position;
		}
		
		public int[] getSize() {
			return size;
		}
		
		private void drawRectangle(Canvas canvas) {
			canvas.save();
			
			canvas.drawColor(Color.GRAY);
			Log.i(TAG, "Drawing rectangle of size " + getSize() + " on position " + getPosition());
			RectF rect = new RectF(0, 0, getSize()[0], getSize()[1]);
			canvas.drawRect(rect, mPainter);
			
			canvas.restore();
		}
		
		public void start() {
			
		}
	
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mDrawingThread = new Thread(new Runnable() {
				public void run() {
					Canvas canvas = null;
					while(!Thread.currentThread().isInterrupted()) {
						canvas = mSurfaceHolder.lockCanvas();
						if(null != canvas) {
							drawRectangle(canvas);
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
				}
			});
			mDrawingThread.start();
		}
	
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mDrawingThread.interrupt();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}
	}
}
