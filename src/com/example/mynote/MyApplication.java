package com.example.mynote;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyApplication extends Application {
	private static MyApplication mInstance = null;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		mInstance = MyApplication.this;
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(this);
		ImageLoader.getInstance().init(configuration);

	}

	public static MyApplication getInstance() {
		return mInstance;
	}

	public static Context getContext() {
		return context;
	}

}
