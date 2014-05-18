package com.erevacation.magiclist;

import java.util.HashMap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListView;

public class Animation {
	public static void animateNewState(String clicked, ListView lv, HashMap<String, Integer> mSavedState, Interpolator mInterpolator, int deapth) {
		try{
			for(int i=0; i < lv.getChildCount(); i++) {
				if(i == deapth){
					View v = lv.getChildAt(i);
					int top = v.getTop();
					int oldTop = 0;
					if(mSavedState.containsKey(clicked+"")){
						oldTop = mSavedState.get(clicked+"");
					}					
					int hDiff = top - oldTop;
					
					ObjectAnimator move = ObjectAnimator.ofFloat(v, "translationY", -hDiff);
					move.setDuration(0);
					ObjectAnimator moveBack = ObjectAnimator.ofFloat(v, "translationY", 0f);
					moveBack.setDuration(200);
					AnimatorSet animatorSet = new AnimatorSet();
					animatorSet.playSequentially(move,moveBack);
					animatorSet.start();
				}
				else if(i>deapth){
					View v = lv.getChildAt(i);
					ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha",0f);
					fadeIn.setDuration(0);
					ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha",1f);
					fadeOut.setDuration(300);
					AnimatorSet animatorSet = new AnimatorSet();
					animatorSet.playSequentially(fadeIn,fadeOut);
					animatorSet.start();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void animateNewStateTop(ListView lv, Interpolator mInterpolator) {
		try{
			for(int i=0; i < lv.getChildCount(); i++) {
				View v = lv.getChildAt(i);
				int top = v.getTop();
				int oldTop = 0;
				int hDiff = top - oldTop;
				
				ObjectAnimator move = ObjectAnimator.ofFloat(v, "translationY", -hDiff);
				move.setDuration(0);
				ObjectAnimator moveBack = ObjectAnimator.ofFloat(v, "translationY", 0f);
				moveBack.setDuration(200);
				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.playSequentially(move,moveBack);
				animatorSet.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
