package com.thtfit.pos.bbpos;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	/**
     * Convert Byte Array to Hex String
     * @param data
     * @return
     */
    private static String Hex2String(byte[] data)
    {
		String result = "";
		for (int i=0; i<data.length; i++)
		{
			int tmp = (data[i] >> 4);
			result += Integer.toString((tmp & 0x0F), 16);
			tmp = (data[i] & 0x0F);
			result += Integer.toString((tmp & 0x0F), 16);
		}
	
		return result;
    }
    
    /**
     * Convert Hex String to byte array
     * @param data
     * @return
     */
	private static byte[] String2Hex(String data)
	{
		byte[] result;
		
		result = new byte[data.length()/2];
		for (int i=0; i<data.length(); i+=2)
			result[i/2] = (byte)(Integer.parseInt(data.substring(i, i+2), 16));
		
		return result;
	}
	

	/**
     * encrypt data in ECB mode
     * @param data
     * @param key
     * @return
     */
    public static String encrypt(String data, String key)
    {
    	byte[] bData, bKey, bOutput;
    	String result;
    	
    	bData = String2Hex(data);
    	bKey = String2Hex(key);
    	bOutput = encrypt(bData, bKey);
    	result = Hex2String(bOutput);
    	
    	return result;
    }
    
    /**
	 * encrypt data in ECB mode
	 * @param data
	 * @param key
	 * @return
	 */
    public static byte[] encrypt(byte[] data, byte[] key)
    {
    	SecretKey sk = new SecretKeySpec(key, "AES");
    	try {
    		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
    		cipher.init(Cipher.ENCRYPT_MODE, sk);
			byte[] enc = cipher.doFinal(data);
			return enc;
        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        } catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {
		} 
    	
    	return null;
    }

    /**
     * encrypt data in CBC mode
     * @param data
     * @param key
     * @return
     */
    protected static String encrypt_CBC(String data, String key)
    {
    	byte[] bData, bKey, bOutput;
    	String result;
    	
    	bData = String2Hex(data);
    	bKey = String2Hex(key);
    	bOutput = encrypt_CBC(bData, bKey);
    	result = Hex2String(bOutput);
    	
    	return result;
    }
	
	/**
     * encrypt data in CBC mode
     * @param data
     * @param key
     * @return
     */
    protected static byte[] encrypt_CBC(byte[] data, byte[] key)
    {
    	SecretKey sk = new SecretKeySpec(key, "AES");
    	try {
    		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
    		cipher.init(Cipher.ENCRYPT_MODE, sk);
    		byte[] enc = new byte[data.length];
    		byte[] dataTemp1 = new byte[16];
    		byte[] dataTemp2 = new byte[16];
    		for (int i=0; i<data.length; i+=16)
    		{
    			for (int j=0; j<16; j++)
    				dataTemp1[j] = (byte)(data[i+j] ^ dataTemp2[j]);
    			dataTemp2 = cipher.doFinal(dataTemp1);
    			for (int j=0; j<16; j++)
    				enc[i+j] = dataTemp2[j];
    		}
			return enc;
    	} catch (Exception e) {
    		e.printStackTrace();
//        } catch (javax.crypto.NoSuchPaddingException e) {
//        } catch (java.security.NoSuchAlgorithmException e) {
//        } catch (java.security.InvalidKeyException e) {
//        } catch (javax.crypto.BadPaddingException e) {
//		} catch (IllegalBlockSizeException e) {
		} 
    	
    	return null;
    }
}