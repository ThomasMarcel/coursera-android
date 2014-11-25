package com.tomschneider.dailyselfie;

import java.util.Calendar;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tomschneider.dailyselfie.AlarmTask;

public class ScheduleService extends IntentService {
	
	private static final String TAG = "Daily-Selfie";
 
    public ScheduleService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		Log.i(TAG, "ScheduleService constructor");
	}
    
    public ScheduleService() {
    	super("SceduleService");
    }

	// This is the object that receives interactions from clients. See
    private final IBinder mBinder = new ServiceBinder();
 
    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        ScheduleService getService() {
            return ScheduleService.this;
        }
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
        
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public void setAlarm(Calendar c) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        new AlarmTask(this, c).run();
    }
    
    public void setAlarm(int millisecs) {
    	new AlarmTask(this, millisecs).run();
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "ScheduleService onHandleIntent");
	}
}