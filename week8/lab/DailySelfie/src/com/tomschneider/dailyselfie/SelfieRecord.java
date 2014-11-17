package com.tomschneider.dailyselfie;

import java.util.Date;

import android.graphics.Bitmap;

public class SelfieRecord {
	private Bitmap mBmp;
	private String mName;
	private Date mDate;
	
	public SelfieRecord(Bitmap bmp, String name) {
		this.mBmp = bmp;
		this.mName = name;
		this.mDate = new Date();
	}
	
	public SelfieRecord() {
		this.mDate = new Date();
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
