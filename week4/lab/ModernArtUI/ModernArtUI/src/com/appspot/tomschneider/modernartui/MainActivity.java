package com.appspot.tomschneider.modernartui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Color;

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
		Thread mDrawingThread;
		
		RectangleView rectangleView1 = new RectangleView(getApplicationContext());
		relativeLayout.addView(rectangleView1);
		Log.i(TAG, "Rectangle at " + rectangleView1.getPosition());
		RectangleView rectangleView2 = new RectangleView(getApplicationContext());
		relativeLayout.addView(rectangleView2);
		Log.i(TAG, "Rectangle at " + rectangleView2.getPosition());
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
	
	private class RectangleView extends View {
		
        private final Paint mPainter = new Paint();
        private Thread mDrawingThread;
        private final int[] size, position;
        private final Random r = new Random();
        private DisplayMetrics mDisplay;
        private int mDisplayWidth, mDisplayHeight;
        
        private int mColor;
		
		RectangleView(Context context) {
			super(context);
			
			mDisplay = new DisplayMetrics();
            MainActivity.this.getWindowManager().getDefaultDisplay()
                            .getMetrics(mDisplay);
            mDisplayWidth = mDisplay.widthPixels;
            mDisplayHeight = mDisplay.heightPixels;
			
			mPainter.setAntiAlias(true);
            
            size = new int[2];
            position = new int[2];
            
            setSizeAndPosition();
            setColor();
		}
		
		private void setSizeAndPosition() {
			//Check if there are alreadyS rectangles, then draw accordingly to occupy all screen space
			if(mDisplayWidth == 0 || mDisplayHeight == 0) {
				mDisplayWidth = relativeLayout.getWidth();
				mDisplayHeight = relativeLayout.getHeight();
			}
			Log.i(TAG, "Layout dimensions: [" + mDisplayWidth + ":" + mDisplayHeight + "]");
			int width = 0;
			int height = 0;
			
			if ( relativeLayout.getChildCount() > 0) {
				RectangleView currentRect;
				for (int i = 0; i < relativeLayout.getChildCount(); i++) {
					currentRect = (RectangleView) relativeLayout.getChildAt(i);
				}
				
				switch (relativeLayout.getChildCount()) {
					case 1:
						currentRect = (RectangleView) relativeLayout.getChildAt(0);
						position[0] = currentRect.getPosition()[0] + currentRect.getSize()[0] + 1;
						position[1] = 0;
						width = mDisplayWidth - position[0];
						while (height < mDisplayHeight * 1 / 4) {
							height = r.nextInt(mDisplayHeight * 2 / 3);
						}
						break;
				}
			} else {
				while (width < mDisplayWidth * 1 / 4) {
					width = r.nextInt(mDisplayWidth * 2 / 3);
				}
				while (height < mDisplayHeight * 1 / 4) {
					height = r.nextInt(mDisplayHeight * 2 / 3);
				}
				
				position[0] = 0;
				position[1] = 0;
			}
			size[0] = width;
			size[1] = height;
			
		}
		
		private void setColor() {
			int[] colorPanel = new int[6];
			
            colorPanel[0] = Color.MAGENTA;
            colorPanel[1] = Color.BLUE;
            colorPanel[2] = Color.GREEN;
            colorPanel[3] = Color.YELLOW;
            colorPanel[4] = Color.RED;
            colorPanel[5] = Color.WHITE;
            
            int rectNumber = relativeLayout.getChildCount();
        	boolean isWhite = false;
            if (rectNumber > 0) {
            	RectangleView rectView;
            	for (int i = 0; i < rectNumber; i++) {
            		 rectView = (RectangleView) relativeLayout.getChildAt(i);
            		 if (rectView.getColor() == Color.WHITE) {
            			 isWhite = true;
            		 }
            	}
            }
            
            if (isWhite) {
            	mColor = colorPanel[r.nextInt(4)];
            } else {
            	if (rectNumber == 4) {
            		mColor = colorPanel[5];
            	} else {
            		mColor = colorPanel[r.nextInt(5)];
            	}
            }
		}
		
		public int getColor() {
			return mColor;
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
			String colorString;
			switch(mColor) {
				case Color.MAGENTA:
					colorString = "MAGENTA";
					break;
				case Color.BLUE:
					colorString = "BLUE";
					break;
				case Color.GREEN:
					colorString = "GREEN";
					break;
				case Color.YELLOW:
					colorString = "YELLOW";
					break;
				case Color.RED:
					colorString = "RED";
					break;
				case Color.WHITE:
					colorString = "WHITE";
					break;
				default:
					colorString = "UNKNOWN";
			}
			
			//canvas.drawColor(Color.GRAY);
			Log.i(TAG, "Drawing " + colorString + " rectangle of size [" + size[0] + ":" + size[1] + "] on position [" + position[0] + ":" + position[1] + "]");
			mPainter.setStyle(Paint.Style.FILL_AND_STROKE);
			mPainter.setColor(mColor);
			
			RectF rect = new RectF(getPosition()[0], getPosition()[1], getSize()[0], getSize()[1]);
			canvas.drawRect(rect, mPainter);
			
			canvas.restore();
		}
	}
}
