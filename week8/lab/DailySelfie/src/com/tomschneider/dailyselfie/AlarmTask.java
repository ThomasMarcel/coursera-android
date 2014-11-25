package com.tomschneider.dailyselfie;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
 
import com.tomschneider.dailyselfie.NotificationService;
 
/**
 * Set an alarm for the date passed into the constructor
 * When the alarm is raised it will start the NotifyService
 *
 * This uses the android build in alarm manager *NOTE* if the phone is turned off this alarm will be cancelled
 *
 * This will run on it's own thread.
 *
 * @author paul.blundell
 */
public class AlarmTask implements Runnable{
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager am;
    // Your context to retrieve the alarm manager from
    private final Context context;
    private int delay = 0;
    
    private static final String TAG = "Daily-Selfie";
 
    public AlarmTask(Context context, Calendar date) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
    }
    
    public AlarmTask(Context context, int millisecs) {
    	this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        Date newDate = new Date();
        cal.setTime(newDate);
        this.date = cal;
        this.delay = millisecs;
    }

    @Override
    public void run() {
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
    	Log.i(TAG, "AlarmTask.run");
    	Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NotificationService.INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Log.i(TAG, "intent: " + intent.toURI() + ", pendingintent: " + pendingIntent.toString() + ", delay: " + delay);
        
        this.date.add(Calendar.SECOND, 10);
        
        //delay = 0;
        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        if (this.delay == 0) {
        	Log.i(TAG, "Setting simple alarm");
        	//am.cancel(pendingIntent);
        	am.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);
        } else {
        	Log.i(TAG, "Setting repeating alarm with delay " + this.delay);
        	// SystemClock.elapsedRealtime()
        	//am.cancel(pendingIntent);
        	//am.setRepeating(AlarmManager.RTC_WAKEUP, this.date.getTimeInMillis(), this.delay, pendingIntent);
        	am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
    }
}