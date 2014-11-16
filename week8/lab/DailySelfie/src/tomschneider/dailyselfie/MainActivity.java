package tomschneider.dailyselfie;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends FragmentActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	
	int displayWidth, displayHeight;
	Bitmap imageBitmap;
	boolean mReturningWithResult = false;
	
	static Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			Fragment placeHolderFragment = new PlaceholderFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().add(R.id.container, placeHolderFragment).commit();
		}
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
		if (id == R.id.action_picture) {
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public static class PictureFragment extends Fragment {
		Bitmap bmp;
		
		public PictureFragment(Bitmap tempbmp) {
			bmp = tempbmp;
		}
		
		private class PicView extends View {
			public PicView(Context context) {
				super(context);
				// TODO Auto-generated constructor stub
			}

			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				// TODO - Resize bitmap to screen size
				
				canvas.drawBitmap(bmp, 0, 0, null);
			}
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = PicView();
			return rootView;
		}
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        imageBitmap = (Bitmap) extras.get("data");
	        mReturningWithResult = true;
	    }
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		
		if (mReturningWithResult) {
			Fragment picFragment = new PictureFragment(imageBitmap);
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        transaction.replace(R.id.container, picFragment).commit();
		}
	}
}
