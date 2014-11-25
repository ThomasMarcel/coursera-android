package com.tomschneider.dailyselfie;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tomschneider.dailyselfie.MainActivity;

public class BootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "Daily-Selfie";
	
	public void onReceive(Context context, Intent intent) { 
		
		Log.i(TAG, "BootReceiver.onReceive");
		
		// in our case intent will always be BOOT_COMPLETED, so we can just set 
		// the alarm 
		// Note that a BroadcastReceiver is *NOT* a Context. Thus, we can't use 
		// "this" whenever we need to pass a reference to the current context. 
		// Thankfully, Android will supply a valid Context as the first parameter 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		int minutes = prefs.getInt("interval", 2); 
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		Intent i = new Intent(context, NotificationService.class); 
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0); 
		am.cancel(pi); 

		if (MainActivity.isPictureOld(MainActivity.getLastPicTime())) {
			Log.i(TAG, "BootReceiver old pic, setting alarm ASAP");
			// by my own convention, minutes <= 0 means notifications are disabled 
			if (minutes > 0) { 
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pi); 
			}
		} else {
			Log.i(TAG, "BootReceiver young pic, setting alarm to " + new Date(MainActivity.followingDay(MainActivity.getLastPicTime())).toString());
			am.setInexactRepeating(AlarmManager.RTC, MainActivity.followingDay(MainActivity.getLastPicTime()), minutes*60*1000, pi);
		}
	}
}
