package com.shireyishunjian.net;
import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class CustomDns implements Dns {
    private final Map<String, Set<InetAddress>> hostToIpMap=new HashMap<>();

    /**
     * 添加域名到IP的映射
     * @param hostname 域名
     * @param ipAddress IP地址
     */
    public void addMapping(String hostname, String... ipAddress) {

        //判断域名是否已存在
        Set<InetAddress> addressSet=hostToIpMap.get(hostname);
        addressSet= addressSet==null? new HashSet<>():addressSet;

        //添加映射
        for (String address:ipAddress) {
            try {
                addressSet.add(InetAddress.getByName(address));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        hostToIpMap.put(hostname, addressSet);

    }



    @NotNull
    @Override
    public List<InetAddress> lookup(@NotNull String hostname) throws UnknownHostException {
        // 检查是否有自定义映射
        if (hostToIpMap.containsKey(hostname)) {
            return hostToIpMap.get(hostname).stream().toList();
        }

        // 对于其他域名，使用系统默认的DNS解析
        return Dns.SYSTEM.lookup(hostname);
    }
}