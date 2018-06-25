package com.moodimodo.dialogs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.moodimodo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoodShareDialog
{
	private Context context;

	private View vwOverTime;
	private View vwDistribution;

	private ImageView imOverTimePreview;
	private ImageView imDistributionPreview;

	private Bitmap overTimeScreencap;
	private Bitmap distributionScreencap;

	public MoodShareDialog(Context context, View vwOverTime, View vwDistribution)
	{
		this.context = context;
		this.vwOverTime = vwOverTime;
		this.vwDistribution = vwDistribution;
	}

	public void show()
	{
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_sharemood, null);

		imOverTimePreview = (ImageView) view.findViewById(R.id.imOverTimePreview);
		imDistributionPreview = (ImageView) view.findViewById(R.id.imDistributionPreview);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.action_share);
		builder.setView(view);

		builder.setPositiveButton(R.string.action_share, new DialogInterface.OnClickListener()
		{
			@Override public void onClick(DialogInterface dialogInterface, int i)
			{
				doShare(true, true);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override public void onClick(DialogInterface dialogInterface, int i)
			{
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}


	private void doShare(boolean shareOverTime, boolean shareDistribution)
	{
		final Intent sharingIntent = new Intent();
		if(shareOverTime && shareDistribution)
		{
			sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		}
		else
		{
			sharingIntent.setAction(Intent.ACTION_SEND);
		}
		sharingIntent.setType("image/png");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
		simpleDateFormat = new SimpleDateFormat(simpleDateFormat.toLocalizedPattern());
		String currentTimeString = simpleDateFormat.format(new Date());

		File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File moodiModoPicturesDir = new File(picturesDir.getPath() + "/MoodiModo");
		moodiModoPicturesDir.mkdirs();

		final File overTimeImageFile = new File(moodiModoPicturesDir, currentTimeString + "_overtime.png");
		final File distributionImageFile = new File(moodiModoPicturesDir, currentTimeString + "_distribution.png");

		try
		{
			overTimeImageFile.createNewFile();
			distributionImageFile.createNewFile();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Saving charts");
		progressDialog.show();

		final Handler handler = new Handler();
		Runnable run = new Runnable()
		{
			@Override public void run()
			{
				try
				{
					FileOutputStream fileOutputStream = new FileOutputStream(overTimeImageFile);
					overTimeScreencap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				try
				{
					FileOutputStream fileOutputStream = new FileOutputStream(distributionImageFile);
					distributionScreencap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}

				handler.post(new Runnable()
				{
					@Override public void run()
					{
						progressDialog.dismiss();
					}
				});

				ArrayList<Uri> imageUris = new ArrayList<Uri>();
				imageUris.add(Uri.parse("file://" + overTimeImageFile.getAbsolutePath()));
				imageUris.add(Uri.parse("file://" + distributionImageFile.getAbsolutePath()));
				sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
				context.startActivity(Intent.createChooser(sharingIntent, "Share images to.."));
			}
		};
		Thread thread = new Thread(run);
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		thread.start();
	}
}
