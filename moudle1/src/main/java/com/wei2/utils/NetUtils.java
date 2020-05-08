package com.wei2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {
    public static boolean networkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (context != null) {
            if(Build.VERSION.SDK_INT<23){
                NetworkInfo mWiFiNetworkInfo = cm.getActiveNetworkInfo();
                if (mWiFiNetworkInfo != null) {
                    if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//WIFI
                        return true;
                    } else if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//移动数据
                        return true;
                    }
                }
            }else{
                Network network =cm.getActiveNetwork();
                if(network!=null){
                    NetworkCapabilities nc=cm.getNetworkCapabilities(network);
                    if(nc!=null){
                        if(nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){//WIFI
                            return true;
                        }else if(nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){//移动数据
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    public  static boolean isReachable(String url) throws IOException {
        Pattern p = Pattern.compile("/.*:");
        Matcher m = p.matcher(url);
        String ipAddress = null;
        if (m.find()) {
            ipAddress = m.group().substring(2,m.group().length()-1);
        }
        System.out.println(ipAddress);
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        return inetAddress.isReachable(1000);
    }


}
