package com.appspot.tomschneider.modernartui;

import android.app.Activity;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
		
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
		
		
		public RectangleView(Context context, int width, int height) {
			super(context);
			
			mPainter.setAntiAlias(true);

            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
		}
		
		public void draw(Canvas canvas) {
			canvas.drawColor(Color.MAGENTA);
			canvas.drawRect(, mPainter);
		}
	
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		
		}
	
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}
	}
}
