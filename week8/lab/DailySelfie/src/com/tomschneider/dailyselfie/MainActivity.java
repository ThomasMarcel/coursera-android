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
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tomschneider.dailyselfie.SelfieNotification;;

public class MainActivity extends ListActivity {

	private static final String TAG = "Daily-Selfie";
	private static final String FILENAME = "selfielist.txt";
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
	private ScheduleService mBoundService;
	private boolean mIsBound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		
		mAdapter = new SelfieViewAdapter(mContext);
		
		//selfieUriList = readListFromFile();
		selfieRecordList = readListFromFile();
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
		
		//setContentView(R.layout.activity_main);
		doBindService();
		setAlarmForNotification(TWO_MINUTES);
		doUnbindService();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			writeListToFile(selfieRecordList);
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
	        		//Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
	        		Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentPhotoPath);
	        		SelfieRecord selfie = new SelfieRecord(mCurrentPhotoPath, mCurrentPhotoPath.getLastPathSegment());
	        		mAdapter.add(selfie);
	        		//selfieUriList.add(mCurrentPhotoPath);
	        		selfieRecordList.add(selfie);
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
	    int count = mAdapter.nextInt();
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
	
	//private void writeListToFile(ArrayList<Uri> list) throws FileNotFoundException {
	private void writeListToFile(ArrayList<SelfieRecord> list) throws FileNotFoundException {


        FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);

        //PrintWriter pw = new PrintWriter(fos);
        ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(fos);

	        for (int i = 0; i < list.size(); i++) {
	        	//pw.println(list.get(i));
	        	os.writeObject(list.get(i));
	        }
	        //pw.close();
	        os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Couldn't write object to file: " + e.toString());
			throw new FileNotFoundException(e.toString());
		}

	}

	//private ArrayList<Uri> readListFromFile() {
	private ArrayList<SelfieRecord> readListFromFile() {
    	//ArrayList<Uri> uriList = new ArrayList<Uri>();
		ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();

        try {
        	FileInputStream fis = openFileInput(FILENAME);
        	//BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        	ObjectInputStream is = new ObjectInputStream(fis);

        	//String line = "";
			//while (null != (line = br.readLine())) {
        	SelfieRecord selfieRecord;
        	while (null != (selfieRecord = (SelfieRecord) is.readObject())) {
				//uriList.add(Uri.parse(line));
        		list.add(selfieRecord);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG,"readListFromFile: Couldn't retrieve list from file: " + e.toString());
			list = generateSelfieFileList();
			return list;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "readListFromFile: Couldn't read object from file, class not found: " + e.toString());
		}
        
        //return uriList;
        return list;
	}
	
	private ArrayList<SelfieRecord> generateSelfieFileList() {
		File dir = new File("/sdcard/");
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
		
		if (fileList.length > 0) {
			ArrayList<SelfieRecord> selfieList = new ArrayList<SelfieRecord>();
			for(int i = 0; i < fileList.length; i++) {
				//Uri fileUri = Uri.fromFile(new File(dir.getAbsolutePath() + fileList[i]));
				Uri fileUri = Uri.fromFile(fileList[i]);
				Log.i(TAG, "generateSelfieFileList: uri for file " + fileList[i].toString() + ": " + fileUri.toString());
				SelfieRecord selfie = new SelfieRecord(fileUri, fileUri.getLastPathSegment());
				selfie.setDate(fileList[i].lastModified());
				selfieList.add(selfie);
			}
			try {
				writeListToFile(selfieList);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "generateSelfieFileList: file not found. dir: " + dir.toString() + ". pattern: " + p.toString() + ". selfielist: " + selfieList.toString());
				e.printStackTrace();
			}
			
			return selfieList;
		} else {
			return null;
		}
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
	    SelfieRecord selfieRecord = (SelfieRecord) mAdapter.getItem(position);
	    //Toast.makeText(this, "Clicked row " + position + ". " + selfieRecord.getBmp().toString(), Toast.LENGTH_SHORT).show();
	    
	    Intent selfieIntent = new Intent(mContext, SelfieActivity.class);
	    selfieIntent.putExtra(EXTRA_BMP_FILEPATH, selfieRecord.getBmp().getPath());
	    startActivity(selfieIntent);
	}
	
	public void doBindService() {
        // Establish a connection with our service
        mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
     
    /**
     * When you attempt to connect to the service, this connection will be called with the result.
     * If we have successfully connected we instantiate our service object so that we can call methods on it.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with our service has been established,
            // giving us the service object we can use to interact with our service.
            mBoundService = ((ScheduleService.ServiceBinder) service).getService();
        }
 
        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };
    
    /**
     * Tell our service to set an alarm for the given date
     * @param c a date to set the notification for
     */
    public void setAlarmForNotification(Calendar c){
        mBoundService.setAlarm(c);
    }
    
    public void setAlarmForNotification(int millisecs){
        mBoundService.setAlarm(millisecs);
    }
     
    /**
     * When you have finished with the service call this method to stop it
     * releasing your connection and resources
     */
    public void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
