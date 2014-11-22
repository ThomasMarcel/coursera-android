package com.tomschneider.dailyselfie;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
 
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
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NotificationService.INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
         
        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        if (this.delay == 0) {
        	am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
        } else {
        	am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000, this.delay, pendingIntent);
        }
    }
}