package com.erevacation.magiclist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_fragment);
		
		if( savedInstanceState == null ) {
			FragmentManager fm = getSupportFragmentManager();
			Fragment f = new ReordableListViewFragment();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, f);
			ft.commit();
		}
	}	
}
