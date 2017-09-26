package com.thtfit.pos.emvswipe;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TLVParser {
	
	public static List<TLV> parse(String tlv) {
		try {
			return getTLVList(hexToByteArray(tlv));
		} catch(Exception e) {
			return null;
		}
	}
	
	private static List<TLV> getTLVList(byte[] data) {
		int index = 0;
		
		ArrayList<TLV> tlvList = new ArrayList<TLV>();
		
		try {
			while(index < data.length) {
				
				byte[] tag;
				byte[] length;
				byte[] value;
				
				boolean isNested;
				if((data[index] & (byte)0x20) == (byte)(0x20)) {
					isNested = true;
				} else {
					isNested = false;
				}
				
				if((data[index] & (byte)0x1F) == (byte)(0x1F)) {
					int lastByte = index + 1;
					while((data[lastByte] & (byte)0x80) == (byte)0x80) {
						++lastByte;
					}
					tag = new byte[lastByte - index + 1];
					System.arraycopy(data, index, tag, 0, tag.length);
					index += tag.length;
				} else {
					tag = new byte[1];
					tag[0] = data[index];
					++index;
					
					if(tag[0] == 0x00) {
						break;
					}
				}
				
				if((data[index] & (byte)0x80) == (byte)(0x80)) {
					int n = (data[index] & (byte)0x7F) + 1;
					length = new byte[n];
					System.arraycopy(data, index, length, 0, length.length);
					index += length.length;
				} else {
					length = new byte[1];
					length[0] = data[index];
					++index;
				}
				
				int n = getLengthInt(length);
				value = new byte[n];
				System.arraycopy(data, index, value, 0, value.length);
				index += value.length;
				
				TLV tlv = new TLV();
				tlv.tag = toHexString(tag);
				tlv.length = toHexString(length);
				tlv.value = toHexString(value);
				tlv.isNested = isNested;
				
				if(isNested) {
					tlv.tlvList = getTLVList(value);
				}
				
				tlvList.add(tlv);
			}
		} catch (Exception e) {
		}
		
		return tlvList;
	}
	
	private static int getLengthInt(byte[] data) {
		if((data[0] & (byte)0x80) == (byte)(0x80)) {
			int n = data[0] & (byte)0x7F;
			int length = 0;
			for(int i = 1; i < n + 1; ++i) {
				length <<= 8; 
				length |= (data[i] & 0xFF);
			}
			return length;
		} else {
			return data[0] & 0xFF;
		}
	}
	
	protected static byte[] hexToByteArray(String s) {
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}
	
	protected static String toHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
	
	protected static TLV searchTLV(List<TLV> tlvList, String targetTag) {
		for(int i = 0; i < tlvList.size(); ++i) {
			TLV tlv = tlvList.get(i);
			if(tlv.tag.equalsIgnoreCase(targetTag)) {
				return tlv;
			} else if(tlv.isNested) {
				TLV searchChild = searchTLV(tlv.tlvList, targetTag);
				if(searchChild != null) {
					return searchChild;
				}
			}
		}
		return null;
	}
}
