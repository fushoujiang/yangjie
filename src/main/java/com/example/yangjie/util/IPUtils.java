package com.example.yangjie.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP装换的工具类
 * @author wenqi.wu
 */
public final class IPUtils {
    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);
    private final static int Num = 32;
    private final static Pattern p= Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");

    private IPUtils(){}

    /**
     * 判断点分十进制IP地址是否正确
     * @param str   IP地址
     * @return  boolean
     */
    public static boolean isIP(String str){
        Matcher m =p.matcher(str);
        return m.matches();
    }

    /**
     * 将127.0.0.1 形式的IP地址转换成10进制整数，这里没有进行任何错误处理
     * @param strIP 目标IP
     * @return  long
     */
    public static long ipToLong(String strIP) {
        long[] ip = new long[4];
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        long value = (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        logger.info("将IP地址:[{}]转换为整数:[{}]",strIP,value);
        return value; // ip1*256*256*256+ip2*256*256+ip3*256+ip4
    }

    /**
     * 将10进制整数形式转换成127.0.0.1形式的IP地址
     * @param longIP    将整数ip转换为IP地址
     * @return  String
     */
    public static String longToIP(long longIP) {
        StringBuilder sb = new StringBuilder("");
        sb.append(String.valueOf(longIP >>> 24));// 直接右移24位
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16)); // 将高8位置0，然后右移16位
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));
        return sb.toString();
    }
}
