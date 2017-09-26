package com.thtfit.pos.debug;

import com.thtfit.pos.R.bool;

import android.util.Log;

public class DebugPrint
{
	private String TAG = null;

	private static final boolean DEBUG = true;
	private static boolean isDebug = false;

	public DebugPrint(boolean isDebug, String TAG)
	{
		this.isDebug = isDebug;
		this.TAG = TAG;
	}

	public void D(String msg)
	{
		if (isDebug && DEBUG)
		{
			Log.d(TAG, msg);
		}
	}

	public void E(String msg)
	{
		if (isDebug && DEBUG)
		{
			Log.e(TAG, msg);
		}
	}

	public void V(String msg)
	{
		if (isDebug && DEBUG)
		{
			Log.v(TAG, msg);
		}
	}

	public void W(String msg)
	{
		if (isDebug && DEBUG)
		{
			Log.w(TAG, msg);
		}
	}

	public void I(String msg)
	{
		if (isDebug && DEBUG)
		{
			Log.i(TAG, msg);
		}
	}

	public static void d(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.w(tag, msg);
		}
	}

	public static void i(String tag, String msg)
	{
		if (DEBUG)
		{
			Log.i(tag, msg);
		}
	}
}
