package com.thtfit.pos.fragment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCall.State;
import org.linphone.mediastream.Log;
import org.linphone.ui.AddressText;
import org.linphone.ui.CallButton;
import org.linphone.ui.EraseButton;


import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.thtfit.pos.R;


public class CallFragment extends Fragment implements OnClickListener{

    private ImageButton btnNum0 = null;
	private ImageButton btnNum1 = null;
	private ImageButton btnNum2 = null;
	private ImageButton btnNum3 = null;
	private ImageButton btnNum4 = null;
	private ImageButton btnNum5 = null;
	private ImageButton btnNum6 = null;
	private ImageButton btnNum7 = null;
	private ImageButton btnNum8 = null;
	private ImageButton btnNum9 = null;
	private ImageButton btnNum10 = null;
	private ImageButton btnNum11 = null;
    private CallButton  btnCallOn = null;
    private ImageButton btnCallOff = null;
	private ImageButton imageVolume;
	private AddressText  edit_call_num=null;
	private EraseButton erase_call_num=null;
	private TextView local_ipaddr=null;
	
	private View mView;
	private LinphoneCall mCall;
	private static CallFragment instance = null;
	
	private static String Local_IP_addr=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_call, container,
				false);
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		super.onActivityCreated(savedInstanceState);
	}
	
	public void initView(){
        btnNum0 = (ImageButton) mView.findViewById(R.id.image_num_0);
        btnNum1 = (ImageButton) mView.findViewById(R.id.image_num_1);
		btnNum2 = (ImageButton) mView.findViewById(R.id.image_num_2);
		btnNum3 = (ImageButton) mView.findViewById(R.id.image_num_3);
		btnNum4 = (ImageButton) mView.findViewById(R.id.image_num_4);
		btnNum5 = (ImageButton) mView.findViewById(R.id.image_num_5);
		btnNum6 = (ImageButton) mView.findViewById(R.id.image_num_6);
		btnNum7 = (ImageButton) mView.findViewById(R.id.image_num_7);
		btnNum8 = (ImageButton) mView.findViewById(R.id.image_num_8);
        btnNum9 = (ImageButton) mView.findViewById(R.id.image_num_9);
        btnNum10 = (ImageButton) mView.findViewById(R.id.image_num_asterisk);
        btnNum11 = (ImageButton) mView.findViewById(R.id.image_num_octothorpe);
        btnCallOn = (CallButton) mView.findViewById(R.id.image_num_callon);
		btnCallOff = (ImageButton) mView.findViewById(R.id.image_num_calloff);
		imageVolume = (ImageButton) mView.findViewById(R.id.image_num_volume);
		edit_call_num = (AddressText) mView.findViewById(R.id.edit_call_num);
		edit_call_num.setText("");
		erase_call_num = (EraseButton) mView.findViewById(R.id.erase_call_num);
		
		btnCallOn.setAddressWidget(edit_call_num);
		erase_call_num.setAddressWidget(edit_call_num);
		local_ipaddr = (TextView)mView.findViewById(R.id.local_ipaddr);
		
		
		if(Local_IP_addr==null)
		{
			local_ipaddr.setText("No NetWork");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Local_IP_addr = GetLocalIpAddress();
					setipaddr(Local_IP_addr);
				}
			}).start();
		}else{
			setipaddr(Local_IP_addr);
		}
		
		imageVolume.setOnClickListener(this);

		btnNum0.setOnClickListener(this);
		btnNum1.setOnClickListener(this);
		btnNum2.setOnClickListener(this);
		btnNum3.setOnClickListener(this);
		btnNum4.setOnClickListener(this);
		btnNum5.setOnClickListener(this);
		btnNum6.setOnClickListener(this);
		btnNum7.setOnClickListener(this);
		btnNum8.setOnClickListener(this);
		btnNum9.setOnClickListener(this);
		btnNum10.setOnClickListener(this);
		btnNum11.setOnClickListener(this);
//		btnCallOn.setOnClickListener(this);
		btnCallOff.setOnClickListener(this);
//		erase_call_num.setOnClickListener(this);
		
	}
    
	protected void setipaddr(String addr) {
		addr = LinphoneUtils.IpNameToSipAddr(addr);
		local_ipaddr.setText(addr);
	}


	
   
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.image_num_volume:
				AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
				break;
			case R.id.image_num_0:
				appendnum("0");
				break;
			case R.id.image_num_1:
				appendnum("1");
				break;
			case R.id.image_num_2:
				appendnum("2");
				break;
			case R.id.image_num_3:
				appendnum("3");
				break;
			case R.id.image_num_4:
				appendnum("4");
				break;
			case R.id.image_num_5:
				appendnum("5");
				break;
			case R.id.image_num_6:
				appendnum("6");
				break;
			case R.id.image_num_7:
				appendnum("7");
				break;
			case R.id.image_num_8:
				appendnum("8");
				break;
			case R.id.image_num_9:
				appendnum("9");
				break;
//			case R.id.image_num_callon:
//				callTo();
//				break;
			case R.id.image_num_calloff:
				callEnd();
				break; 
			case R.id.image_num_asterisk:
				appendnum("*");
				break;
			case R.id.image_num_octothorpe:
				appendnum("#");
				break;
//			case R.id.erase_call_num:
//				deleteOnenum();
//				break;
			default:
				break;
		}
		
	}
	private void callEnd()
	{
		hangUp();
	}

	private void hangUp() {
		LinphoneCore lc = LinphoneManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();
		
		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
		}
	}

	private void appendnum(String text)
	{
		StringBuilder callNumSb=new StringBuilder();
		callNumSb.append(edit_call_num.getText());
		callNumSb.append(text);
		edit_call_num.setText(callNumSb.toString());
	}
	private String mCachedLocalIpAddr="";
	private String GetLocalIpAddress() {
		String strCurActiveNetIpAddr = "";

		do
		{
			if(0 < mCachedLocalIpAddr.length())
			{
				strCurActiveNetIpAddr = mCachedLocalIpAddr;
				break;
			}
			//
			try {
				//try to get cur active net link
				do
				{
//					if(null == mContext)
//					{
//						break;
//					}
//					ConnectivityManagerEx connMgrEx = new ConnectivityManagerEx();
//					if(null == connMgrEx)
//					{
//						break;
//					}
//					ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//					if(null == connMgr)
//					{
//						break;
//					}
//					LinkProperties curLinkProp = connMgrEx.getActiveLinkProperties(connMgr);
//					if(null == curLinkProp)
//					{
//						break;
//					}
//					Collection<InetAddress> linkInetAddrs = curLinkProp.getAddresses();
//					if(null == linkInetAddrs)
//					{
//						break;
//					}
//					Iterator <InetAddress> itInetAddr = linkInetAddrs.iterator();
//					while(itInetAddr.hasNext())
//					{
//						InetAddress inetAddr = itInetAddr.next();
//						if(null == inetAddr)
//						{
//							continue;
//						}
//						if(false == inetAddr.isLoopbackAddress())
//						{
//							strCurActiveNetIpAddr = inetAddr.getHostAddress();
//							mCachedLocalIpAddr = strCurActiveNetIpAddr;
//							break;
//						}
//					}
					if(0 < strCurActiveNetIpAddr.length())
					{
						break;
					}
				}while(false);
				if(0 < strCurActiveNetIpAddr.length())
				{
					break;
				}
				//Ethernet
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					if(null == intf)
					{
						continue;
					}
					final String strNetIfName = intf.getName();
					if(null == strNetIfName)
					{
						continue;
					}
					if(false == strNetIfName.startsWith("eth"))
					{
						continue;
					}
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (4 == inetAddress.getAddress().length && false == inetAddress.isLoopbackAddress()) {
							String ip = inetAddress.getHostAddress();
							if(null != ip && 0 < ip.length())
							{
								strCurActiveNetIpAddr = ip;
								mCachedLocalIpAddr = strCurActiveNetIpAddr;
								break;
							}
						}
					}
					if(0 < strCurActiveNetIpAddr.length())
					{
						break;
					}
				}
				if(0 < strCurActiveNetIpAddr.length())
				{
					break;
				}
				//WIFI
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					if(null == intf)
					{
						continue;
					}
					final String strNetIfName = intf.getName();
					if(null == strNetIfName)
					{
						continue;
					}
					if(false == strNetIfName.startsWith("wlan"))
					{
						continue;
					}
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (4 == inetAddress.getAddress().length && false == inetAddress.isLoopbackAddress()) {
							String ip = inetAddress.getHostAddress();
							if(null != ip && 0 < ip.length())
							{
								strCurActiveNetIpAddr = ip;
								mCachedLocalIpAddr = strCurActiveNetIpAddr;
								break;
							}
						}
					}
					if(0 < strCurActiveNetIpAddr.length())
					{
						break;
					}
				}
				if(0 < strCurActiveNetIpAddr.length())
				{
					break;
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			}
		}while(false);

		return strCurActiveNetIpAddr;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		instance = this;

		// Only one call ringing at a time is allowed
		if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
			List<LinphoneCall> calls = LinphoneUtils.getLinphoneCalls(LinphoneManager.getLc());
			for (LinphoneCall call : calls) {
				if (State.IncomingReceived == call.getState()) {
					mCall = call;
					break;
				}
			}
		}
		if (mCall == null) {
			Log.e("Couldn't find incoming call");
	
		}else{
			LinphoneAddress address = mCall.getRemoteAddress();
			edit_call_num.setText(LinphoneUtils.SipAddrToIpName(address.asStringUriOnly()));
		}
	}
	
} 
