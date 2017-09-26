package com.thtfit.pos.activity;

import com.imagpay.Settings;
import com.imagpay.ttl.TTLHandler;

import android.app.Application;

public class ThtfitPosApplication extends Application{
	private TTLHandler _handler;
	private Settings _setting;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public TTLHandler get_handler() {
		return _handler;
	}

	public void set_handler(TTLHandler _handler) {
		this._handler = _handler;
	}

	public Settings get_setting() {
		return _setting;
	}

	public void set_setting(Settings _setting) {
		this._setting = _setting;
	}

}
