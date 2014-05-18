package com.erevacation.magiclist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ReordableListViewFragment extends ListFragment {
	final ArrayList<String> mDataSet = new ArrayList<String>();
	HashMap<String, Integer> colors = new HashMap<String, Integer>();
	@SuppressLint("UseSparseArrays")
	HashMap<String, Integer> mSavedState = new HashMap<String, Integer>();
	Interpolator mInterpolator = new DecelerateInterpolator();
	ItemAdapter mAdapter;
	int current_deapth = 0;
	int[] colors_int = new int[]{Color.rgb(233, 88, 87), Color.rgb(138, 80, 118), Color.rgb(58, 119, 150), Color.rgb(97, 177, 185), Color.rgb(113, 185, 145), Color.rgb(115, 220, 178), Color.rgb(109, 119, 131)};
	int last_color = 0;

	private void saveState() {
		try{
			mSavedState.clear();
			ListView lv = getListView();
			int first = lv.getFirstVisiblePosition();
			int last = lv.getLastVisiblePosition();
			for(int i=0; i<mDataSet.size(); i++) {
				if( i >= first && i <= last) {
					View v = lv.getChildAt(i-first);
					int top = v.getTop();
					int dataIdx = i;
					String dataId = mDataSet.get(dataIdx);
					mSavedState.put(dataId, top);
				} else if( i < first ) {
					int top = lv.getTop() - lv.getHeight()/2;
					String dataId = mDataSet.get(i);
					mSavedState.put(dataId, top);
				} else if( i > last ) {
					int top = lv.getBottom() + lv.getHeight()/2;
					String dataId = mDataSet.get(i);
					mSavedState.put(dataId, top);
				}
			}
			for(int i=0; i < lv.getChildCount(); i++) {
				View v = lv.getChildAt(i);
				int top = v.getTop();
				int dataIdx = first + i;
				String dataId = mDataSet.get(dataIdx);
				mSavedState.put(dataId, top);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateDataSet() {
		try{
			JSONObject jObject  = new JSONObject(Resources.json);
			JSONArray jArray = jObject.getJSONArray("all");
			mDataSet.clear();
			for (int i=0; i < jArray.length(); i++)
			{
				try {
					JSONObject oneObject = jArray.getJSONObject(i);
					// Pulling items from the array
					String oneObjectsItem = oneObject.getString("title");
					mDataSet.add(oneObjectsItem);
					colors.put(oneObjectsItem, colors_int[i]);
				} catch (JSONException e) {
					// Oops
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fillWithSubCategory(String[] json_path){
		try{
			JSONObject jObject  = new JSONObject(Resources.json);
			JSONArray jArray = jObject.getJSONArray("all");
			for(int j = 0; j< json_path.length; j++){
				mDataSet.add(json_path[j]);
				for (int i=0; i < jArray.length(); i++)
				{
					try {
						JSONObject oneObject = jArray.getJSONObject(i);
						// Pulling items from the array
						String oneObjectsItem = oneObject.getString("title");
						if(!oneObjectsItem.equals(json_path[j])){
							continue;
						}
						else if(oneObjectsItem.equals(json_path[j]) && j == json_path.length-1){
							// last iteration
							JSONArray jArr = oneObject.getJSONArray("children");
							for (int z=0; z < jArr.length(); z++){
								JSONObject oneObj = jArr.getJSONObject(z);
								String oneObjItm = oneObj.getString("title");
								mDataSet.add(oneObjItm);
							}
							return;
						}
						else if(oneObjectsItem.equals(json_path[j])){
							jArray = oneObject.getJSONArray("children");
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void reorderDataSet(String old) {
		try{
			ArrayList<String> temp = new ArrayList<String>(mDataSet);
			if(old.equals("All product")){
				current_deapth = 0;
				generateDataSet();
			}
			else if(current_deapth == 1){
				mDataSet.clear();
				mDataSet.add("All product");
				fillWithSubCategory(new String[]{old});
			}
			else{
				mDataSet.clear();
				mDataSet.add("All product");
				String[] json_path = new String[current_deapth];
				int z = 0;
				for(int i=current_deapth;i>1;i--){
					json_path[z] = temp.get(current_deapth-(i-1));
					z++;
				}
				json_path[z] = old;
				fillWithSubCategory(json_path);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v1, int position, long id) {
		if(position > current_deapth){
			current_deapth++;
		}
		else if(current_deapth == 0){
			current_deapth = 1;
		}
		else if(position == current_deapth){
			return;
		}
		else if(position < current_deapth){
			current_deapth = position;
		}
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View v = inflater.inflate(R.layout.reordable_list_view, null);
		final String pos = (String)l.getItemAtPosition(position);
		saveState();
		reorderDataSet(pos);
		mAdapter.notifyDataSetChanged();
		
		
		// there is some small blink in animation 
		v.postDelayed(new Runnable() {

			@Override
			public void run() {
				if(pos.equals("All product")){
					Animation.animateNewStateTop(getListView(), mInterpolator);
				}
				else{
					Animation.animateNewState(pos, getListView(), mSavedState, mInterpolator, current_deapth);
				}
			}
			}, 0);
		
		/*
		can be run in this thread, but ListView are not still refresh (animation look on) 
		if(pos.equals("All product")){
			Animation.animateNewStateTop(getListView(), mInterpolator);
		}
		else{
			Animation.animateNewState(pos, getListView(), mSavedState, mInterpolator, current_deapth);
		}
		*/

		super.onListItemClick(l, v, position, id);
	}
	

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		generateDataSet();

		final View v = inflater.inflate(R.layout.reordable_list_view, null);
		final ListView lv = (ListView) v.findViewById(android.R.id.list);
		if( mAdapter == null ) mAdapter = new ItemAdapter();
		lv.setAdapter( mAdapter );
		return v;
	}


	private class ItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataSet.size();
		}

		@Override
		public Object getItem(int position) {
			String o = mDataSet.get(position);
			return o;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if( v == null ) {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				v = inflater.inflate(R.layout.reordable_list_item, null);
			}
			TextView tv = (TextView) v.findViewById(R.id.text);
			tv.setText(mDataSet.get(position));
			tv.setTextColor(Color.WHITE);

			String category = mDataSet.get(position);
			if(colors.containsKey(category) && current_deapth == 0){
				tv.setBackgroundColor(colors.get(category));
				last_color = colors.get(category);
				tv.setTextColor(Color.WHITE);
			}
			else if(colors.containsKey(category) && current_deapth == 1){
				tv.setBackgroundColor(colors.get(category));
				last_color = colors.get(mDataSet.get(position));
			}
			else if(current_deapth>1 && current_deapth == position){
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(last_color);
			}
			else{
				tv.setTextColor(last_color);
				tv.setBackgroundColor(Color.BLACK);
			}

			if(current_deapth>position){
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.BLACK);
			}
			return v;
		}
	}
}
