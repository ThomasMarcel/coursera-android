package com.tomschneider.dailyselfie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfieViewAdapter extends BaseAdapter {
	
	private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
	private static LayoutInflater mInflater = null;
	private Context mContext;
	private static final String TAG = "Daily-Selfie";
	
	public SelfieViewAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
		  TextView nameView;
		  TextView dateView;
		  ImageView imageView;
		}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View newView = convertView;
		ViewHolder holder = null;
		
		SelfieRecord curr = list.get(position);
		if (null == convertView) {
			holder = new ViewHolder();
			newView = mInflater.inflate(R.layout.selfie_view, null);
			holder.imageView = (ImageView) newView.findViewById(R.id.selfie_pic);
			holder.nameView = (TextView) newView.findViewById(R.id.selfie_name);
			holder.dateView = (TextView) newView.findViewById(R.id.selfie_date);
			newView.setTag(holder);
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		
		try {
			holder.imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), curr.getBmp()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "ioexception while setting holder's imageview: " + e.toString());
			e.printStackTrace();
		}
		holder.nameView.setText(curr.getName());
		holder.dateView.setText(curr.getDate().toString());
		
		Log.i(TAG, "New view dimentsions: [" + newView.getWidth() + ":" + newView.getHeight() + "]");
		return newView;
	}
	
	public void add(SelfieRecord listItem) {
		list.add(listItem);
	}
	
	public ArrayList<SelfieRecord> getList(){
        return list;
	}

	public void removeAllViews(){
        list.clear();
        this.notifyDataSetChanged();
	}

}
