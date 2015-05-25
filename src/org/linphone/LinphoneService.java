/*
LinphoneService.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.linphone;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.linphone.LinphoneManager.NewOutgoingCallUiListener;
import org.linphone.LinphoneSimpleListener.LinphoneServiceListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Log;
import org.linphone.core.OnlineStatus;
import org.linphone.mediastream.Version;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.thtfit.pos.activity.MainActivity;
import com.thtfit.pos.R;

import org.linphone.BaseErrDef;
import java.util.ArrayList;
import android.os.Process;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

/**
 * 
 * Linphone service, reacting to Incoming calls, ...<br />
 * 
 * Roles include:
 * <ul>
 * <li>Initializing LinphoneManager</li>
 * <li>Starting C libLinphone through LinphoneManager</li>
 * <li>Reacting to LinphoneManager state changes</li>
 * <li>Delegating GUI state change actions to GUI listener</li>
 * 
 * 
 * @author Guillaume Beraudo
 * 
 */
public final class LinphoneService extends Service implements LinphoneServiceListener 
{
	private static final String TAG = "LinphoneService";
	private static final Object m_InstanceMutex = new Object();
	/*
	 * Listener needs to be implemented in the Service as it calls
	 * setLatestEventInfo and startActivity() which needs a context.
	 */

	private Handler mHandler = new Handler();
	private static LinphoneService instance = null;

	private WifiManager mWifiManager;
	private WifiLock mWifiLock;

	private static final ArrayList<LinphoneSrvEvtListener> m_lstLinphoneSrvEvtListener = 
		new ArrayList<LinphoneSrvEvtListener>();

	public static boolean isReady() {
		synchronized(m_InstanceMutex)
		{
			return (instance != null);
		}
	}

	/**
	 * @throws RuntimeException
	 *             service not instantiated
	 */
	public static LinphoneService instance() {
		if (isReady())
			return instance;

		throw new RuntimeException("LinphoneService not instantiated yet");
	}

	public static final int Add_LinphoneSrvEvt_Listener(LinphoneSrvEvtListener SrvEnvtListener)
	{
		int iOutRet = BaseErrDef.ERR_SUCCESS;
		boolean bRet = false;
		
		synchronized(m_lstLinphoneSrvEvtListener)
		{
			do
			{
				if(null == SrvEnvtListener)
				{
					iOutRet = BaseErrDef.ERR_INVAL_ARG;
					break;
				}
				if(null == m_lstLinphoneSrvEvtListener)
				{
					iOutRet = BaseErrDef.ERR_INVAL_STATE;
					break;
				}
				if(m_lstLinphoneSrvEvtListener.contains(SrvEnvtListener))
				{
					break;
				}
				bRet = m_lstLinphoneSrvEvtListener.add(SrvEnvtListener);
				if(false == bRet)
				{
					iOutRet = BaseErrDef.ERR_ADD_FAIL;
					break;
				}
			}while(false);
		}

		return iOutRet;
	}

	public static final int Remove_LinphoneSrvEvt_Listener(LinphoneSrvEvtListener SrvEnvtListener)
	{
		int iOutRet = BaseErrDef.ERR_SUCCESS;
		boolean bRet = false;
		
		synchronized(m_lstLinphoneSrvEvtListener)
		{
			do
			{
				if(null == SrvEnvtListener)
				{
					iOutRet = BaseErrDef.ERR_INVAL_ARG;
					break;
				}
				if(null == m_lstLinphoneSrvEvtListener)
				{
					iOutRet = BaseErrDef.ERR_INVAL_STATE;
					break;
				}
				if(false == m_lstLinphoneSrvEvtListener.contains(SrvEnvtListener))
				{
					break;
				}
				bRet = m_lstLinphoneSrvEvtListener.remove(SrvEnvtListener);
				if(false == bRet)
				{
					iOutRet = BaseErrDef.ERR_REMOVE_FAIL;
					break;
				}
			}while(false);
		}

		return iOutRet;
	}

	private final static int NOTIF_ID = 1;
	private final static int INCALL_NOTIF_ID = 2;

	private Notification mNotif;
	private Notification mIncallNotif;
	private PendingIntent mNotifContentIntent;
	private String mNotificationTitle;

	private static final int IC_LEVEL_ORANGE = 0;
	/*
	 * private static final int IC_LEVEL_GREEN=1; private static final int
	 * IC_LEVEL_RED=2;
	 */
	private static final int IC_LEVEL_OFFLINE = 3;

	@Override
	public void onCreate() {
		super.onCreate();

		do
		{
			//Log.d(TAG, " onCreate("+this+"),Pid="+Process.myPid()+",Tid="+Thread.currentThread().getId());

			//only one instance can be created
			synchronized(m_InstanceMutex)
			{
				if(null != instance)
				{
					Log.e(TAG, "One instance already created, do not create more.");
					break;
				}
			}

			// In case restart after a crash. Main in LinphoneActivity
			LinphonePreferenceManager.getInstance(this);

			// Set default preferences
			PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

			mNotificationTitle = getString(R.string.app_name);

			// Dump some debugging information to the logs
			Log.i(START_LINPHONE_LOGS);
			dumpDeviceInformation();
			dumpInstalledLinphoneInformation();

			mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNM.cancel(INCALL_NOTIF_ID); // in case of crash the icon is not removed
			mNotif = new Notification(R.drawable.status_level, "",
					System.currentTimeMillis());
			mNotif.iconLevel = IC_LEVEL_ORANGE;
			mNotif.flags |= Notification.FLAG_ONGOING_EVENT;

			// shovn@20120731
			// Intent notifIntent = new Intent(this, LinphoneActivity.class);
			// mNotifContentIntent = PendingIntent.getActivity(this, 0, notifIntent,
			// 0);
			// mNotif.setLatestEventInfo(this, mNotificationTitle,"",
			// mNotifContentIntent);

			LinphoneManager.createAndStart(this, this);
			LinphoneManager.getLc().setPresenceInfo(0, null, OnlineStatus.Online);
			mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			mWifiLock = mWifiManager.createWifiLock(
					WifiManager.WIFI_MODE_FULL_HIGH_PERF, this.getPackageName()
							+ "-wifi-call-lock");
			mWifiLock.setReferenceCounted(false);
			instance = this; // instance is ready once linphone manager has been
								// created

			// Retrieve methods to publish notification and keep Android
			// from killing us and keep the audio quality high.
			if (Version.sdkStrictlyBelow(Version.API05_ECLAIR_20)) {
				try {
					mSetForeground = getClass().getMethod("setForeground",
							mSetFgSign);
				} catch (NoSuchMethodException e) {
					Log.e(e, "Couldn't find foreground method");
				}
			} else {
				try {
					mStartForeground = getClass().getMethod("startForeground",
							mStartFgSign);
					mStopForeground = getClass().getMethod("stopForeground",
							mStopFgSign);
				} catch (NoSuchMethodException e) {
					Log.e(e, "Couldn't find startGoreground or stopForeground");
				}
			}

			startForegroundCompat(NOTIF_ID, mNotif);
			Log.d(TAG, " onCreate("+this+"),Pid="+Process.myPid()+",Tid="+Thread.currentThread().getId()," OK");
		}while(false);
	}

	private enum IncallIconState {
		INCALL, PAUSE, VIDEO, IDLE
	}

	private IncallIconState mCurrentIncallIconState = IncallIconState.IDLE;

	private synchronized void setIncallIcon(IncallIconState state) {
		if (state == mCurrentIncallIconState)
			return;
		mCurrentIncallIconState = state;
		if (mIncallNotif == null)
			mIncallNotif = new Notification();

		int notificationTextId = 0;
		switch (state) {
		case IDLE:
			mNM.cancel(INCALL_NOTIF_ID);
			return;
		case INCALL:
			mIncallNotif.icon = R.drawable.conf_unhook;
			notificationTextId = R.string.incall_notif_active;
			break;
		case PAUSE:
			mIncallNotif.icon = R.drawable.conf_status_paused;
			notificationTextId = R.string.incall_notif_paused;
			break;
		case VIDEO:
			mIncallNotif.icon = R.drawable.conf_video;
			notificationTextId = R.string.incall_notif_video;
			break;
		default:
			throw new IllegalArgumentException("Unknown state " + state);
		}

		mIncallNotif.iconLevel = 0;
		mIncallNotif.when = System.currentTimeMillis();
		mIncallNotif.flags &= Notification.FLAG_ONGOING_EVENT;
		mIncallNotif.setLatestEventInfo(this, mNotificationTitle,
				getString(notificationTextId), mNotifContentIntent);
		notifyWrapper(INCALL_NOTIF_ID, mIncallNotif);
	}

	public void refreshIncallIcon(LinphoneCall currentCall) {
		LinphoneCore lc = LinphoneManager.getLc();
		if (currentCall != null) {
			if (currentCall.getCurrentParamsCopy().getVideoEnabled()
					&& currentCall.cameraEnabled()) {
				// checking first current params is mandatory
				setIncallIcon(IncallIconState.VIDEO);
			} else {
				setIncallIcon(IncallIconState.INCALL);
			}
		} else if (lc.getCallsNb() == 0) {
			setIncallIcon(IncallIconState.IDLE);
		} else if (lc.isInConference()) {
			setIncallIcon(IncallIconState.INCALL);
		} else {
			setIncallIcon(IncallIconState.PAUSE);
		}
	}

	private static final Class<?>[] mSetFgSign = new Class[] { boolean.class };
	private static final Class<?>[] mStartFgSign = new Class[] { int.class,
			Notification.class };
	private static final Class<?>[] mStopFgSign = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	/*private Class<? extends Activity> incomingReceivedActivity = LinphoneActivity.class;*/

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w(e, "Unable to invoke method");
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w(e, "Unable to invoke method");
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		if (mSetForeground != null) {
			mSetForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
			// continue
		}

		notifyWrapper(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		if (mSetForeground != null) {
			mSetForegroundArgs[0] = Boolean.FALSE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
		}
	}

	public static final String START_LINPHONE_LOGS = " ==== Phone information dump ====";

	private void dumpDeviceInformation() {
		StringBuilder sb = new StringBuilder();
		sb.append("DEVICE=").append(Build.DEVICE).append("\n");
		sb.append("MODEL=").append(Build.MODEL).append("\n");
		// MANUFACTURER doesn't exist in android 1.5.
		// sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
		sb.append("SDK=").append(Build.VERSION.SDK);
		Log.i(sb.toString());
	}

	private void dumpInstalledLinphoneInformation() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException nnfe) {
		}

		if (info != null) {
			Log.i("Linphone version is ", info.versionCode);
		} else {
			Log.i("Linphone version is unknown");
		}
	}

	private synchronized void sendNotification(int level, int textId) {
		mNotif.iconLevel = level;
		mNotif.when = System.currentTimeMillis();
		String text = getString(textId);
		if (text.contains("%s") && LinphoneManager.getLc() != null) {
			// Test for null lc is to avoid a NPE when Android mess up badly
			// with the String resources.
			LinphoneProxyConfig lpc = LinphoneManager.getLc()
					.getDefaultProxyConfig();
			String id = lpc != null ? lpc.getIdentity() : "";
			text = String.format(text, id);
		}

		mNotif.setLatestEventInfo(this, mNotificationTitle, text,
				mNotifContentIntent);
		notifyWrapper(NOTIF_ID, mNotif);
	}

	/**
	 * Wrap notifier to avoid setting the linphone icons while the service is
	 * stopping. When the (rare) bug is triggered, the linphone icon is present
	 * despite the service is not running. To trigger it one could stop linphone
	 * as soon as it is started. Transport configured with TLS.
	 */
	private synchronized void notifyWrapper(int id, Notification notification) {
		synchronized(m_InstanceMutex)
		{
			if (instance != null) {
				mNM.notify(id, notification);
			} else {
				Log.i("Service not ready, discarding notification");
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public synchronized void onDestroy() {
		synchronized(m_InstanceMutex)
		{
			Log.d(TAG, " onDestroy("+this+"),Pid="+Process.myPid()+",Tid="+Thread.currentThread().getId());

			LinphoneCore linphoneCore = LinphoneManager.getLc();
			if(null != linphoneCore)
			{
				linphoneCore.terminateAllCalls();
				LinphoneManager.getLc().setPresenceInfo(0, null, OnlineStatus.Offline);
				LinphoneManager.destroy();
			}

			// Make sure our notification is gone.
			stopForegroundCompat(NOTIF_ID);
			mNM.cancel(INCALL_NOTIF_ID);
			mWifiLock.release();
			//
			instance = null;
			//
			super.onDestroy();
		}
	}

	private static final LinphoneGuiListener guiListener() {
		/*return DialerActivity.instance(); //shovn@20120821*/
		return null;
	}

	private static final LinphoneOnCallStateChangedListener incallListener() {
//		return IncallActivity.instance(); //shovn@20120821
		return null;
	}

	public void onDisplayStatus(final String message) {
		mHandler.post(new Runnable() {
			public void run() {
				if (guiListener() != null)
					guiListener().onDisplayStatus(message);
			}
		});
	}

	public void onGlobalStateChanged(final GlobalState state,
			final String message) {
		if (state == GlobalState.GlobalOn) {
			sendNotification(IC_LEVEL_OFFLINE, R.string.notification_started);

			// Slightly delay the propagation of the state change.
			// This is to let the linphonecore finish to be created
			// in the java part.
			mHandler.postDelayed(new Runnable() {
				public void run() {
					if (guiListener() != null)
						guiListener().onGlobalStateChangedToOn(message);
				}
			}, 50);
		}
	}

	public void onRegistrationStateChanged(final RegistrationState state, final String message) {
		if (instance == null) {
			Log.i("Service not ready, discarding registration state change to ",
					state.toString());
			return;
		}
		if (state == RegistrationState.RegistrationOk
				&& LinphoneManager.getLc().getDefaultProxyConfig()
						.isRegistered()) {
			sendNotification(IC_LEVEL_ORANGE, R.string.notification_registered);
		}

		if (state == RegistrationState.RegistrationFailed
				|| state == RegistrationState.RegistrationCleared) {
			sendNotification(IC_LEVEL_OFFLINE,
					R.string.notification_register_failure);
		}

		synchronized(m_lstLinphoneSrvEvtListener)
		{
			for(LinphoneSrvEvtListener OneLinphoneSrvEvtListener: m_lstLinphoneSrvEvtListener)
			{
				OneLinphoneSrvEvtListener.onRegistrationStateChanged(state, message);
			}
		}
	}
	public void onConnectivityChanged(final NetworkInfo networkInfo, final ConnectivityManager connMgr)
	{
		synchronized(m_lstLinphoneSrvEvtListener)
		{
			for(LinphoneSrvEvtListener OneLinphoneSrvEvtListener: m_lstLinphoneSrvEvtListener)
			{
				OneLinphoneSrvEvtListener.onConnectivityChanged(networkInfo, connMgr);
			}
		}
	}

	protected void onIncomingReceived() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean("onIncomingReceived", true);
		intent.putExtra("LinphoneService", bundle);
		startActivity(intent);
	}

	public void onCallStateChanged(final LinphoneCall call, final State state,
			final String message) {
		//Log.d(TAG, ",call="+call+",state="+state+",msg="+message);
		synchronized(m_InstanceMutex)
		{
			if (instance == null) {
				return;
			}
		}
		if (state == LinphoneCall.State.IncomingReceived) {
			onIncomingReceived();
		}

		if (state == State.CallUpdatedByRemote) {
			// If the correspondent proposes video while audio call
			boolean remoteVideo = call.getRemoteParams().getVideoEnabled();
			boolean localVideo = call.getCurrentParamsCopy().getVideoEnabled();
			boolean autoAcceptCameraPolicy = LinphoneManager.getInstance()
					.isAutoAcceptCamera();
			if (remoteVideo && !localVideo && !autoAcceptCameraPolicy) {
				try {
					LinphoneManager.getLc().deferCallUpdate(call);

					if (incallListener() != null)
						incallListener().onCallStateChanged(call, state,
								message);
				} catch (LinphoneCoreException e) {
					e.printStackTrace();
				}
			}
		}

		if (state == State.StreamsRunning) {
			// Workaround bug current call seems to be updated after state
			// changed to streams running
			refreshIncallIcon(call);
			mWifiLock.acquire();
		} else {
			refreshIncallIcon(LinphoneManager.getLc().getCurrentCall());
		}
		if ((state == State.CallEnd || state == State.Error)
				&& LinphoneManager.getLc().getCallsNb() < 1) {
			mWifiLock.release();
		}
		mHandler.post(new Runnable() {
			public void run() {
				if (guiListener() != null)
					guiListener().onCallStateChanged(call, state, message);
			}
		});

		synchronized(m_lstLinphoneSrvEvtListener)
		{
			for(LinphoneSrvEvtListener OneLinphoneSrvEvtListener: m_lstLinphoneSrvEvtListener)
			{
				OneLinphoneSrvEvtListener.onCallStateChanged(call, state, message);
				
			}
		}
	}

	public String getToneUri(final LinphoneCall call,final State state,final String message) {
	
		  synchronized(m_lstLinphoneSrvEvtListener)
			{
				for(LinphoneSrvEvtListener OneLinphoneSrvEvtListener: m_lstLinphoneSrvEvtListener)
				{
					
				return	OneLinphoneSrvEvtListener.getToneUri(call,state,message);
					
				}
			}
		  return null;
		}



	public interface LinphoneGuiListener extends NewOutgoingCallUiListener {
		void onDisplayStatus(String message);

		void onGlobalStateChangedToOn(String message);

		void onCallStateChanged(LinphoneCall call, State state, String message);
		String getStringUri(LinphoneCall call, State state,String message);
	}

	public void onRingerPlayerCreated(MediaPlayer mRingerPlayer) {
		final Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		try {
			mRingerPlayer.setDataSource(getApplicationContext(), ringtoneUri);
		} catch (IOException e) {
			try {
				final Uri localRingtoneUrl = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.oldphone_mono);
				mRingerPlayer.setDataSource(getApplicationContext(), localRingtoneUrl);
			} catch (IOException ioEx) {
				Log.e(TAG, "cannot set ringtone");
			}
		}
	}

	public void tryingNewOutgoingCallButAlreadyInCall() {
		mHandler.post(new Runnable() {
			public void run() {
				if (guiListener() != null)
					guiListener().onAlreadyInCall();
			}
		});
	}

	public void tryingNewOutgoingCallButCannotGetCallParameters() {
		mHandler.post(new Runnable() {
			public void run() {
				if (guiListener() != null)
					guiListener().onCannotGetCallParameters();
			}
		});
	}

	public void tryingNewOutgoingCallButWrongDestinationAddress() {
		mHandler.post(new Runnable() {
			public void run() {
				if (guiListener() != null)
					guiListener().onWrongDestinationAddress();
			}
		});
	}

	public void onCallEncryptionChanged(final LinphoneCall call,
			final boolean encrypted, final String authenticationToken) {
		// IncallActivity registers itself to this event and handle it.
	}
}
