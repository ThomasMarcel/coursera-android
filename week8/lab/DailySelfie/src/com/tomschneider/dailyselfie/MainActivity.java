package com.tomschneider.dailyselfie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

//import com.tomschneider.dailyselfie.SelfieNotification;;

public class MainActivity extends ListActivity {

	private static final String TAG = "Daily-Selfie";
	private static final String DIRNAME = "dailyselfie";
	public static final String EXTRA_BMP_FILEPATH = "bitmap";
	private ArrayList<String> selfieList = new ArrayList<String>();
	
	private static SelfieViewAdapter mAdapter;
	private static Context mContext;
	private ArrayList<Uri> selfieUriList;
	private ArrayList<SelfieRecord> selfieRecordList;
	private ArrayList<Integer> selfieToRemove;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	private static final int TWO_MINUTES = 2 * 60 * 1000;
	int alarmType = AlarmManager.ELAPSED_REALTIME;
	
	private static File mediaStorageDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		
		mAdapter = new SelfieViewAdapter(mContext);
		
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			mediaStorageDir = Environment.getExternalStorageDirectory();
		} else {
			mediaStorageDir = mContext.getFilesDir();
		}
		
		//mediaStorageDir = mContext.getFilesDir();
		
		if (!mediaStorageDir.exists()) {
			mediaStorageDir.mkdirs();
		}
		
		//selfieUriList = readListFromFile();
		selfieRecordList = generateSelfieFileList();
		selfieToRemove = new ArrayList<Integer>();
		
		Log.i(TAG, "Retrieving uri list from file: " + selfieRecordList.toString() + " of size " + selfieRecordList.size());
		if (selfieRecordList.size() > 0) {
			for (int i = 0; i < selfieRecordList.size(); i++) {
				mAdapter.add(new SelfieRecord(mContext, selfieRecordList.get(i)));
			}
		}
		/*if (selfieToRemove.size() > 0) {
			for (int i = selfieToRemove.size() - 1; i >= 0; i--) {
				selfieUriList.remove(i);
			}
		}*/
		
		setListAdapter(mAdapter);
		cancelNotification();
		//setContentView(R.layout.activity_main);
		//doBindService();
		setLastPicTime(selfieRecordList);
		if (isPictureOld(getLastPicTime())) {
			Log.i(TAG, "Pic is old (" + new Date(getLastPicTime()).toString() + "), setting the notification for ASAP");
			sendNotification(0);
		} else {
			Log.i(TAG, "Pic is young (" + new Date(getLastPicTime()).toString() + "), setting the notification for the following day: " + new Date(followingDay(getLastPicTime())).toString());
			sendNotification(followingDay(getLastPicTime()));
		}
	}
	
	public void sendNotification(long millis) {
		Log.i(TAG, "MainActivity.sendNotification");
        //Log.i(TAG, "ServiceConnection mBoundService: " + mBoundService.toString());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		int minutes = prefs.getInt("interval", 2); 
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); 
		Intent i = new Intent(mContext, NotificationService.class); 
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0); am.cancel(pi); 
		
		Log.i(TAG, "Intent: " + i.toString() + ", PendingIntent: " + pi.toString() + ", minutes: " + minutes);
		// by my own convention, minutes <= 0 means notifications are disabled 
		if (millis == 0) { 
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pi); 
		} else {
			am.setInexactRepeating(AlarmManager.RTC, millis, minutes*60*1000, pi);
		}
	}
	
	public void cancelNotification() {
		Log.i(TAG, "Cancelling alarms");
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); 
		Intent i = new Intent(mContext, NotificationService.class); 
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0); am.cancel(pi);
		am.cancel(pi);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		setLastPicTime(selfieRecordList);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		//doUnbindService();
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
	        		//Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
	        		Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
	        		SelfieRecord selfie = new SelfieRecord(mCurrentPhotoPath, mCurrentPhotoPath.getLastPathSegment());
	        		mAdapter.add(selfie);
	        		//selfieUriList.add(mCurrentPhotoPath);
	        		selfieRecordList.add(selfie);
	        		setLastPicTime(selfieRecordList);
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
		
		//env = mContext.getDir(DIRNAME, Context.MODE_PRIVATE);
		//env = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

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
	    int count = mAdapter.nextInt();
	    if (type == MEDIA_TYPE_IMAGE){
	        //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Selfie"+ timeStamp + ".jpg");
	    	//String filename = mediaStorageDir.getAbsolutePath() + File.separator + "selfie" + count + ".jpg";
	    	String filename = "selfie" + count + ".jpg";
	    	//String filename = "/sdcard/selfie" + count + ".jpg";
	    	mediaFile = new File(mediaStorageDir, filename);
	    	//mediaFile = new File(filename);
	        //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "selfie" + count + ".jpg");
	    	if (! mediaFile.exists()) {
	    		mediaFile.createNewFile();
	    	}
	    	mediaFile.setWritable(true, false);
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Selfie_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	    Log.i(TAG, "MainActivity.getOutputMediaFile mediaFile " + mediaFile.getAbsolutePath().toString());
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
	
	private ArrayList<SelfieRecord> generateSelfieFileList() {
		//File dir = mContext.getDir(DIRNAME, Context.MODE_PRIVATE);
		File dir = mediaStorageDir;
		if (! dir.isDirectory()) {
			Log.i(TAG, "generateSelfieFileList: " + dir.toString() + " is not a valid directory");
		}
		final Pattern p = Pattern.compile("selfie[0-9]*\\.jpg");
		File[] fileList = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return p.matcher(pathname.getName()).matches();
			}
			
		});
		
		ArrayList<SelfieRecord> selfieList = new ArrayList<SelfieRecord>();
		if (fileList.length > 0) {
			for(int i = 0; i < fileList.length; i++) {
				//Uri fileUri = Uri.fromFile(new File(dir.getAbsolutePath() + fileList[i]));
				Uri fileUri = Uri.fromFile(fileList[i]);
				Log.i(TAG, "generateSelfieFileList: uri for file " + fileList[i].toString() + ": " + fileUri.toString());
				SelfieRecord selfie = new SelfieRecord(fileUri, fileUri.getLastPathSegment());
				selfie.setDate(fileList[i].lastModified());
				selfieList.add(selfie);
			}
		}
		
		return selfieList;
	}
	
	public static long getLastPicTime() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext); 
		long lastPicTime = prefs.getLong("lastpictime", 0);
		
		return lastPicTime;
	}
	
	private void setLastPicTime(ArrayList<SelfieRecord> list) {
		long millis = 0;
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getDate().getTime() > millis) {
					millis = list.get(i).getDate().getTime();
				}
			}
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext); 
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("lastpictime", millis);
		editor.commit();
	}
	
	public static boolean isPictureOld (long oldPicMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, 1);
		if (oldPicMillis + 24 * 60 * 60 * 1000 > cal.getTimeInMillis()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static long followingDay (long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(millis));
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, 1);
		Log.i(TAG, "Timezone: " + TimeZone.getDefault().toString());
		return cal.getTimeInMillis();
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
	    SelfieRecord selfieRecord = (SelfieRecord) mAdapter.getItem(position);
	    //Toast.makeText(this, "Clicked row " + position + ". " + selfieRecord.getBmp().toString(), Toast.LENGTH_SHORT).show();
	    
	    Intent selfieIntent = new Intent(mContext, SelfieActivity.class);
	    selfieIntent.putExtra(EXTRA_BMP_FILEPATH, selfieRecord.getBmp().getPath());
	    startActivity(selfieIntent);
	}
}
