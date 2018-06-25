package com.moodimodo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.moodimodo.receivers.MoodResultReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils
{
	public static int convertDpToPixel(float dp, Resources res)
	{
		DisplayMetrics metrics = res.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public static int convertPixelsToDp(float px, Resources res)
	{
		DisplayMetrics metrics = res.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return (int) dp;
	}

	public static float convertSpToPixels(float px, Resources res)
	{
		float scaledDensity = res.getDisplayMetrics().scaledDensity;
		return px * scaledDensity;
	}

	public static void expandView(final View v, Animation.AnimationListener listener)
	{
		v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				v.getLayoutParams().height = interpolatedTime == 1
						? ViewGroup.LayoutParams.WRAP_CONTENT
						: (int) (targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density) * 2);
		if (listener != null)
		{
			a.setAnimationListener(listener);
		}
		v.startAnimation(a);
		v.setVisibility(View.VISIBLE);
	}

	public static void collapseView(final View v, Animation.AnimationListener listener)
	{
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				if (interpolatedTime == 1)
				{
					v.setVisibility(View.GONE);
				}
				else
				{
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 2);
		if (listener != null)
		{
			a.setAnimationListener(listener);
		}
		v.startAnimation(a);
	}

	public static int roundToNearestInteger(double d)
	{
		double dAbs = Math.abs(d);
		int i = (int) dAbs;
		double result = dAbs - (double) i;
		if (result < 0.5)
		{
			return d < 0 ? -i : i;
		}
		else
		{
			return d < 0 ? -(i + 1) : i + 1;
		}
	}

	public static boolean checkRoot() {
		String[] paths = { "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
				"/system/bin/failsafe/su", "/data/local/su" };
		for (String path : paths) {
			if (new File(path).exists()) return true;
		}
		return false;
	}

	public static void shareGraph(final Context ctx, final View view, final Runnable callback){
		final ProgressDialog progressDialog = new ProgressDialog(ctx);
		progressDialog.setMessage(ctx.getString(R.string.share_graph_saving_graph));
		progressDialog.show();

		final Intent sharingIntent = new Intent();
		sharingIntent.setAction(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault());
		String currentTimeString = simpleDateFormat.format(new Date());
		File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File pictureDir = new File(picturesDir.getPath() + "/Quantimodo");
		pictureDir.mkdirs();
		final File target = new File(pictureDir, currentTimeString + "_graph.png");

		final Handler handler = new Handler();
		Runnable run = new Runnable() {
			@Override
			public void run() {
				Bitmap image = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
				Canvas c = new Canvas(image);
				view.draw(c);

				try {
					target.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(target);
					image.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						callback.run();
					}
				});

				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + target.getAbsolutePath()));
				ctx.startActivity(Intent.createChooser(sharingIntent,ctx.getString(R.string.share_chart_select)));
			}
		};

		Thread thread = new Thread(run);
		thread.start();
	}

	public static double convertToOneToHundred(double value){
		return  ((value - 1) / 4) * 100;
	}

	public static void changeAlpha(final Handler handler,final View container){
		for (int i = 0; i < 255; i += 5)
		{
			final int alpha = i;

			handler.post(new Runnable()
			{
				public void run()
				{
					container.getBackground().setAlpha(alpha);
				}
			});
			try
			{
				Thread.sleep(10);
			}
			catch (Exception e)
			{
				break;
			}
		}
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				container.getBackground().setAlpha(255);
			}
		});
	}

	public static Intent createIntent(Context context,String variableName,double value){
		Intent intent = new Intent(context,MoodResultReceiver.class);
		intent.setAction(MoodResultReceiver.INTENT_ACTION_SUBMIT);
		Bundle measurement = new Bundle();
		measurement.putDouble(variableName,value);
		intent.putExtra(MoodResultReceiver.EXTRA_MEASUREMENTS,measurement);
		return intent;
	}
}
