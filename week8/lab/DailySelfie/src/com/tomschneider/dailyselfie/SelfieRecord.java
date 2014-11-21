package com.tomschneider.dailyselfie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class SelfieRecord implements Serializable {
	private String mBmp;
	private String mName;
	private long mDate;
	
	private static final String TAG = "Daily-Selfie";
	
	public SelfieRecord(Uri bmp, String name) {
		this.mBmp = bmp.getPath();
		this.mName = name;
		this.mDate = new Date().getTime();
	}
	
	public SelfieRecord() {
		Date date = new Date();
		this.mDate = new Date().getTime();
		this.mName = "selfie";
	}
	
	public SelfieRecord(Context mContext, Uri selfieUri) throws FileNotFoundException {
		//this.mBmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selfieUri);
		this.mBmp = selfieUri.getPath();
		this.mDate = new Date().getTime();
		if (selfieUri.getLastPathSegment() != null) {
			this.mName = selfieUri.getLastPathSegment();
		} else {
			this.mName = "selfie";
		}
	}
	
	public SelfieRecord(Context mContext, SelfieRecord selfieRecord) {
		this.mBmp = selfieRecord.getBmp().getPath();
		this.mDate = selfieRecord.getDate().getTime();
		this.mName = selfieRecord.getName();
	}
	
	public void setBmp(Uri bmp) {
		this.mBmp = bmp.getPath();
	}
	
	public Uri getBmp() {
		return Uri.parse(this.mBmp);
	}
	
	public void setDate(Date date) {
		this.mDate = date.getTime();
	}
	
	public void setDate(long secs) {
		this.mDate = new Date(secs).getTime();
	}
	
	public Date getDate() {
		return new Date(this.mDate);
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getName() {
		return this.mName;
	}
	
	@Override
	public String toString() {
		Date date = new Date(this.mDate);
		return mName + " on " + date.toString();
	}
}
