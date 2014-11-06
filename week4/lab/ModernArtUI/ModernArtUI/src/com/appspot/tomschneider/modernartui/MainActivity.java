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

public class MainActivity extends Activity {

	RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
	
	private int mDisplayWidth, mDisplayHeight;
	
	private static final String TAG = "Modern-Art-UI";
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			// Get the size of the display so this View knows where borders are
			mDisplayWidth = relativeLayout.getWidth();
			mDisplayHeight = relativeLayout.getHeight();

		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
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
		
		
		public RectangleView(Context context) {
			super(context);
			
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
			
			int width = r.nextInt(mDisplayWidth * 2 / 3);
			int height = r.nextInt(mDisplayHeight * 2 / 3);
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
		
		@Override
		public void onDraw(Canvas canvas) {
			canvas.save();
			
			canvas.drawColor(Color.MAGENTA);
			canvas.drawRect(100.0f, 100.0f, 100.0f, 100.0f, mPainter);
			
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
							draw(canvas);
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
				}
			});
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
