package com.email.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetWork {

	/**
	 * 判断网络连接类型类型
	 * 
	 * return -1 没有网络 0 wifi 1 手机网络
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetWorkType(Context context) {

		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {

			if (info.getType() == ConnectivityManager.TYPE_WIFI) {

				return 0;
			} else {
				return 1;
			}
		}

		return -1;
	}

	/**
	 * 判断手机网络类型
	 * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信的3G为EVDO
	 * @param context
	 * @return
	 */
	public static int getMobileNotWorkType(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (tm.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:

			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:            //网络类型为CDMA

			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:            //网络类型为EDGE

			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:           // 网络类型为EVDO0

			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:          //网络类型为EVDOA

			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:

			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:

			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:             //网络类型为GPRS

			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:            //网络类型为HSDPA

			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:             //网络类型为HSPA

			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:

			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:            //网络类型为HSUPA

			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:

			break;
		case TelephonyManager.NETWORK_TYPE_LTE:

			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:              //网络类型为UMTS

			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:

			break;
		default:
			break;
		}
		return -1;
	}
	
	/**
	 * 获取手机IP
	 */
	public static String getPhoneIp(){
		try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
                    //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {  
                        return inetAddress.getHostAddress().toString();  
                    }  
                }  
            }  
        } catch (Exception e) {  
        }  
        return ""; 
	}
	
	/**
	 * PING  www.xxx.com
	 * @return
	 */
	public static final boolean PING(){
		String result = null;
		
		String ip = "www.baidu.com";
		try {
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
			// 读取ping的内容，可不加。
			
			InputStream input = p.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			StringBuffer stringBuffer = new StringBuffer();

			String content = "";

			while ((content = in.readLine()) != null) {

			stringBuffer.append(content);

			}

			Log.i("TTT", "result content : " + stringBuffer.toString());
			// PING的状态

			int status = p.waitFor();

			if (status == 0) {

			result = "successful~";

			return true;

			} else {

				result = "failed~ cannot reach the IP address";

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "failed~ InterruptedException";
		}//ping3次 
		finally {

		Log.i("TTT", "result = " + result);

		}
		
		return false;
	}
}
