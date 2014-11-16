package tomschneider.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends FragmentActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;

	private static final String TAG = "Daily-Selfie";
	int mDisplayWidth, mDisplayHeight;
	Bitmap imageBitmap;
	boolean mReturningWithResult = false;
	String mCurrentPhotoPath;
	
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
			View rootView = new PicView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			rootView.setLayoutParams(params);
			return rootView;
		}
	}
	
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	        	Log.i(TAG, ex.toString());
	        	Toast.makeText(mContext, "Couldn't create photo file. Check your external memory. " + ex.toString(), Toast.LENGTH_LONG);
	            finish();
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            Log.i(TAG, "Uri passed to MediaStore.EXTRA_OUTPUT: " + Uri.fromFile(photoFile).toString());
	            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	        }
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
	

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "dailyselfie_" + timeStamp;
		File storageDir;
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			Log.i(TAG, "Writing to external storage public pictures directory");
			storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		} else {
			Log.i(TAG, "Writing to internal files directory");
			storageDir = mContext.getFilesDir();
		}
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);
		image.getParentFile().mkdirs();
		// Save a file: path for use with ACTION_VIEW intents
		//mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		mCurrentPhotoPath = image.getAbsolutePath();
		Log.i(TAG, "Picture file path: " + mCurrentPhotoPath);
		return image;
	}
}
