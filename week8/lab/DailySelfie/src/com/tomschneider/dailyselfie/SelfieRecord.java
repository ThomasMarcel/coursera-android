package com.tomschneider.dailyselfie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class SelfieRecord {
	private Bitmap mBmp;
	private String mName;
	private Date mDate;
	
	private static final String TAG = "Daily-Selfie";
	
	public SelfieRecord(Bitmap bmp, String name) {
		this.mBmp = bmp;
		this.mName = name;
		this.mDate = new Date();
	}
	
	public SelfieRecord() {
		this.mDate = new Date();
		this.mName = "selfie";
	}
	
	public SelfieRecord(Context mContext, Uri selfieUri) throws FileNotFoundException {
		try {
			this.mBmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selfieUri);
			this.mDate = new Date();
			if (selfieUri.getLastPathSegment() != null) {
				this.mName = selfieUri.getLastPathSegment();
			} else {
				this.mName = "selfie";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Problem creating selfierecord from context and uri: " + e.toString());
			throw new FileNotFoundException("uri: " + selfieUri);
		}
	}
	
	public void setBmp(Bitmap bmp) {
		this.mBmp = bmp;
	}
	
	public Bitmap getBmp() {
		return this.mBmp;
	}
	
	public void setDate(Date date) {
		this.mDate = date;
	}
	
	public Date getDate() {
		return this.mDate;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getName() {
		return this.mName;
	}
	
	@Override
	public String toString() {
		return mName + " on " + mDate.toString();
	}
}
