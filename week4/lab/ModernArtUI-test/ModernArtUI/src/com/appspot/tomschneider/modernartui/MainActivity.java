package com.appspot.tomschneider.modernartui;

import java.util.Random;

import android.annotation.SuppressLint;
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

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	RelativeLayout relativeLayout;
	
	private static final String TAG = "Modern-Art-UI";
	private static final int HEADS = 0;
	private static final int TAILS = 1;
	
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
		//setContentView(R.layout.activity_main);
		
		relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
		setContentView(relativeLayout);
		
		//mDisplayWidth = relativeLayout.getWidth();
		//mDisplayHeight = relativeLayout.getHeight();
		
		SurfaceView view = new SurfaceView(this);
        //setContentView(view);
        view.getHolder().addCallback(this);
		relativeLayout.addView(view);
	}
	
	@SuppressLint("WrongCall")
	private void tryDrawing(SurfaceHolder holder) {
		
		Log.i(TAG, "Trying to draw...");

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.e(TAG, "Cannot draw onto the canvas as it's null");
        } else {
    		
    		RectangleView rectangleView1 = new RectangleView(getApplicationContext());
    		rectangleView1.onDraw(canvas);
    		Log.i(TAG, "Rectangle at " + rectangleView1.getPosition());
    		RectangleView rectangleView2 = new RectangleView(getApplicationContext());
    		rectangleView2.onDraw(canvas);
    		Log.i(TAG, "Rectangle at " + rectangleView2.getPosition());
    		RectangleView rectangleView3 = new RectangleView(getApplicationContext());
    		rectangleView3.onDraw(canvas);
    		Log.i(TAG, "Rectangle at " + rectangleView3.getPosition());
    		
            holder.unlockCanvasAndPost(canvas);
        }
	}
	
	@Override
    public void surfaceCreated(SurfaceHolder holder) {
        tryDrawing(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) { 
        tryDrawing(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

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
		private SurfaceHolder holder;
		
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
			Log.i(TAG, "Layout dimensions: [" + mDisplayWidth + ":" + mDisplayHeight + "]");
			int width = 0;
			int height = 0;
			if ( relativeLayout.getChildCount() > 0) {
				RectangleView currentRect;
								
				switch (relativeLayout.getChildCount()) {
					case 1:
						currentRect = (RectangleView) relativeLayout.getChildAt(0);
						position[0] = currentRect.getPosition()[0] + currentRect.getSize()[0];
						position[1] = 0;
						width = mDisplayWidth - currentRect.getSize()[0];
						while (height < mDisplayHeight * 1 / 4) {
							height = r.nextInt(mDisplayHeight * 2 / 3);
						}
						break;
					case 2:
						RectangleView rect1 = (RectangleView) relativeLayout.getChildAt(0);
						RectangleView rect2 = (RectangleView) relativeLayout.getChildAt(1);
						position[0] = 0;
						position[1] = rect1.getPosition()[1] + rect1.getSize()[1];
						if (rect1.getSize()[1] > rect2.getSize()[1] + mDisplayHeight / 5) {
							int coinToss = r.nextInt(1);
							if(coinToss == HEADS) {
								width = mDisplayWidth;
							} else {
								width = rect1.getSize()[0];
							}
						} else {
							width = rect1.getSize()[0];
						}
						while ((height < mDisplayHeight / 6 || mDisplayHeight - height < mDisplayHeight / 6) && height != mDisplayHeight - rect1.getSize()[1]) {
							height = r.nextInt(mDisplayHeight - rect1.getSize()[1]);
						}
						
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
