package org.risa.android.util;

import com.aqt.qin.R;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Helper Class that handles Android Animations.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class FX {

	/**
	 * Slides the view down expanding its view
	 * 
	 * @param ctx Context to find the animation.
	 * @param v View to expand a slide down
	 */
	public static void expandDown(Context ctx, View v) {
		if (v == null)
			throw new NullPointerException("FX.slideDown() Null view");

		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
		assert a != null: "Unable to find animation R.anim.slide_down";

		a.reset();
		v.clearAnimation();
		v.startAnimation(a);
	}

	/**
	 * Slides the view up collapsing the view
	 * 
	 * @param ctx
	 * @param v
	 */
	public static void collapseUp(Context ctx, View v) {
		if (v == null)
			throw new NullPointerException("FX.slideDown() Null view");

		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
		assert a != null: "Unable to find animation R.anim.slide_down";

		a.reset();
		v.clearAnimation();
		v.startAnimation(a);
	}

}
