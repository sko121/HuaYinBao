package com.thtfit.pos.debug;

import android.util.Log;

public class DebugPrint
{
	private String TAG = null;
	
	private static final boolean DEBUG = true;
	
	public DebugPrint(boolean isDebug, String TAG)
	{
		//DEBUG = isDebug;
		this.TAG = TAG;
	}

	public void D(String msg)
	{
		if (DEBUG)
		{
			Log.d(TAG, msg);
		}
	}

	public void E(String msg)
	{
		if (DEBUG)
		{
			Log.e(TAG, msg);
		}
	}

	public void V(String msg)
	{
		if (DEBUG)
		{
			Log.v(TAG, msg);
		}
	}

	public void W(String msg)
	{
		if (DEBUG)
		{
			Log.w(TAG, msg);
		}
	}
	public void I(String msg)
	{
		if (DEBUG)
		{
			Log.i(TAG, msg);
		}
	}
	
	
	
	
	
	public static void d(String tag,String msg)
	{
		if (DEBUG)
		{
			Log.d(tag, msg);
		}
	}
	
	public static void e(String tag,String msg)
	{
		if (DEBUG)
		{
			Log.e(tag, msg);
		}
	}
	
	public static void v(String tag,String msg)
	{
		if (DEBUG)
		{
			Log.v(tag, msg);
		}
	}
	
	public static void w(String tag,String msg)
	{
		if (DEBUG)
		{
			Log.w(tag, msg);
		}
	}
	
	public static void i(String tag,String msg)
	{
		if (DEBUG)
		{
			Log.i(tag, msg);
		}
	}
}
