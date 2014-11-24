package com.tomschneider.dailyselfie;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {
	
	private static final String TAG = "Daily-Selfie";
	 
    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }
 
    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification 
    public static final String INTENT_NOTIFY = "com.tomschneider.dailyselfie.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager mNM;
 
    @Override
    public void onCreate() {
    	//super.onCreate();
        Log.i(TAG, "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
         
        // If this service was started by out AlarmTask intent then we want to show our notification
        Log.i(TAG, "Showing Notification: " + intent.getBooleanExtra(INTENT_NOTIFY, false));
        if(intent.getBooleanExtra(INTENT_NOTIFY, true))
        	Log.i(TAG, "Showing notification");
            showNotification();
         
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
 
    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();
 
    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
    	Log.i(TAG, "NotificationService.showNotification");
        // This is the 'title' of the notification
        CharSequence title = "Alarm!!";
        // This is the icon to use on the notification
        int icon = R.drawable.ic_action_camera;
        // This is the scrolling text of the notification
        CharSequence text = "Your notification time is upon us.";      
        // What time to show on the notification
        long time = System.currentTimeMillis();
         
        //Notification notification = new Notification(icon, text, time);
 
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
 
        // Set the info for the views that show in the notification panel.
        //notification.setLatestEventInfo(this, title, text, contentIntent);
 
        // Clear the notification when it is pressed
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        Notification.Builder notificationBuilder = new Notification.Builder(this)
        	.setSmallIcon(icon)
        	.setContentTitle(title)
        	.setContentText(text)
        	.setContentIntent(contentIntent);
         
        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notificationBuilder.build());
         
        // Stop the service when we are finished
        stopSelf();
    }
}