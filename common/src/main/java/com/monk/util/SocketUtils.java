package com.monk.util;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public class SocketUtils {

    public static String remoteAddress(Channel channel) {
        final SocketAddress socketAddress = channel.remoteAddress();
        if (null != socketAddress) {
            return socketAddress.toString().substring(1);
        }
        return "UNKNOWN HOST";
    }

    public static String remoteAddress(SocketAddress socketAddress) {
        if (null != socketAddress) {
            return socketAddress.toString().substring(1);
        }
        return "UNKNOWN HOST";
    }
}
