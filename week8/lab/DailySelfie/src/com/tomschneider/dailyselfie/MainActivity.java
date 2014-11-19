package com.tomschneider.dailyselfie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private static final String TAG = "Daily-Selfie";
	private static final String FILENAME = "selfielist.txt";
	private ArrayList<String> selfieList = new ArrayList<String>();
	
	private SelfieViewAdapter mAdapter;
	private static Context mContext;
	private ArrayList<Uri> selfieUriList;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	private static int count = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView selfieListView = getListView();
		
		mContext = getApplicationContext();
		
		selfieUriList = readListFromFile();
		Log.i(TAG, "Retrieving uri list from file: " + selfieUriList.toString());
		if (selfieUriList.size() > 0) {
			
		}
		
		mAdapter = new SelfieViewAdapter(mContext);
		setListAdapter(mAdapter);
		
		//setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			writeListToFile(selfieUriList);
			Log.i(TAG, "Writing selfie uri list to file");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Couldn't write selfie uri list to file: " + e.toString());
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
		if (id == R.id.action_takepic) {
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	
	Uri mCurrentPhotoPath;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	        	if (data != null) {
	        		Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
	        	} else {
	        		Toast.makeText(this, "Image saved to:\n" + mCurrentPhotoPath.toString(), Toast.LENGTH_LONG).show();
	        	}
	            //Bitmap imageBitmap = new Bitmap();
	        	try {
	        		Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
	        		SelfieRecord selfie = new SelfieRecord(imageBitmap, mCurrentPhotoPath.toString());
	        		mAdapter.add(selfie);
	        		selfieUriList.add(mCurrentPhotoPath);
	        		setListAdapter(mAdapter);
	        		Log.i(TAG, "Adding new Selfie " + selfie.getName() + " to adapter");
	        	} catch (IOException ex) {
	        		Log.i(TAG, "Couldn't built bitmap from uri " + mCurrentPhotoPath + ": " + ex.toString());
	        	}
		        
		        Log.i(TAG, "Selfies in adapter: " + mAdapter.getCount());
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }

	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Video saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        } else {
	            // Video capture failed, advise user
	        }
	    }
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		try {
			return Uri.fromFile(getOutputMediaFile(type));
		} catch(IOException ex) {
			return null;
		}
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) throws IOException{
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
		File env;
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			env = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		} else {
			env = mContext.getFilesDir();
		}

	    File mediaStorageDir = new File(env, "DailySelfieApp");
		//File mediaStorageDir = new File("/sdcard");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.i(TAG, "failed to create directory");
	            return null;
	        }
	    }
	    
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Selfie"+ timeStamp + ".jpg");
	    	String filename = "/sdcard/selfie" + count + ".jpg";
	    	mediaFile = new File(filename);
	        //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "selfie" + count + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Selfie_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	    Log.i(TAG, "mediaFile " + mediaFile.getAbsolutePath().toString());
	    count += 1;
	    return mediaFile;

	}


	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        // Continue only if the File was successfully created
        	Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        	
	        if (fileUri != null) {
	        	Log.i(TAG, "Taking picture " + fileUri.toString());
	        	
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	            mCurrentPhotoPath = fileUri;
	            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	        }
	    }
	}
	
	private void writeListToFile(ArrayList<Uri> list) throws FileNotFoundException {

        FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);

        PrintWriter pw = new PrintWriter(fos);

        for (int i = 0; i < list.size(); i++) {
        	pw.println(list.get(i));
        }
        pw.close();

	}

	private ArrayList<Uri> readListFromFile() {
    	ArrayList<Uri> uriList = new ArrayList<Uri>();

        try {
        	FileInputStream fis = openFileInput(FILENAME);
        	BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        	String line = "";
			while (null != (line = br.readLine())) {
				uriList.add(Uri.parse(line));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Couldn't retrieve list from file: " + e.toString());
		}
        
        return uriList;
	}
}
