/*
CallButton.java
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
package org.linphone.ui;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneUtils;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.thtfit.pos.R;

/**
 * @author Guillaume Beraudo
 */
public class CallButton extends ImageView implements OnClickListener, AddressAware {

	private AddressText mAddress;
	public void setAddressWidget(AddressText a) { mAddress = a; }

	public void setExternalClickListener(OnClickListener e) { setOnClickListener(e); }
	public void resetClickListener() { setOnClickListener(this); }

	public CallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public void onClick(View v) {
		try {
			if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {

				if (mAddress.getText().length() > 0) { 
					CallPhoneNumber(mAddress);
				} else {
					if (getContext().getResources().getBoolean(R.bool.call_last_log_if_adress_is_empty)) {
						LinphoneCallLog[] logs = LinphoneManager.getLc().getCallLogs();
						LinphoneCallLog log = null;
						for (LinphoneCallLog l : logs) {
							if (l.getDirection() == CallDirection.Outgoing) {
								log = l;
								break;
							}
						}
						if (log == null) {
							return;
						}
						
						LinphoneProxyConfig lpc = LinphoneManager.getLc().getDefaultProxyConfig();
						if (lpc != null && log.getTo().getDomain().equals(lpc.getDomain())) {
							mAddress.setText(log.getTo().getUserName());
						} else {
							
							mAddress.setText(LinphoneUtils.SipAddrToIpName(log.getTo().asStringUriOnly()));
						}
						mAddress.setSelection(mAddress.getText().toString().length());
						mAddress.setDisplayedName(log.getTo().getDisplayName());
					}
				}
			}
		} catch (LinphoneCoreException e) {
			LinphoneManager.getInstance().terminateCall();
			onWrongDestinationAddress();
		}
	}
	


	private static String displayName="test";
	private void CallPhoneNumber(AddressText mAddress) {
		// TODO Auto-generated method stub
		String number = mAddress.getText().toString().toLowerCase();
		if(!number.startsWith("sip:")&&(number.length()==12))
		{
			//sip:test@192.168.70.67
			String myphone = String.format("sip:%s@%d.%d.%d.%d",
					number,
					Integer.parseInt(number.substring(0, 3)),
					Integer.parseInt(number.substring(3, 6)),
					Integer.parseInt(number.substring(6, 9)),
					Integer.parseInt(number.substring(9, 12)));
			LinphoneManager.getInstance().newOutgoingCall(myphone,null);
		}
		else
		{
			LinphoneManager.getInstance().newOutgoingCall(mAddress);
		}
		
		
	}

	protected void onWrongDestinationAddress() {
		Toast.makeText(getContext()
				,String.format(getResources().getString(R.string.warning_wrong_destination_address),mAddress.getText().toString())
				,Toast.LENGTH_LONG).show();
	}
}
