package com.thtfit.pos.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;

public final class RootCmd {
    private static final String TAG = "RootCmd";
    private static final String ROOT_KEY = "root_1nbgetaqz37#@159";
    private static boolean mHaveRoot = false;

    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test");
            if (ret != -1) {
                Log.i(TAG, "have root!");
                mHaveRoot = true;
            } else {
                Log.i(TAG, "not root!");
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    public static String execRootCmd() {
    	String cmd = "/system/bin/logwrapper mount -o remount,rw /system;mount";
        String result = "";
        DataOutputStream dos = null;
//        DataInputStream dis = new DataInputStream(in);
        BufferedReader br = null;

        try {
            Process p = Runtime.getRuntime().exec("su "+ROOT_KEY);//must use ROOT_KEY to get root user on LimePC system
            dos = new DataOutputStream(p.getOutputStream());
//            dis = new DataInputStream(p.getInputStream());
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
/*
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();*/

            String line = null;
            while ((line = br.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            result = e.toString();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.toString();
                }
            }
            if (br != null) {
                try {
                	br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.toString();
                }
            }
        }
        return result;
    }

    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su "+ROOT_KEY);////must use ROOT_KEY to get root user on LimePC system
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
