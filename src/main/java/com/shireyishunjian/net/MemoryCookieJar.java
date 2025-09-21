package com.shireyishunjian.net;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程安全的内存 CookieJar 实现
 * 1. 自动存储服务器返回的 Cookie
 * 2. 自动为请求附加合适的 Cookie
 * 3. 提供简单方法手动添加 Cookie
 * 4. 完全线程安全
 */
public class MemoryCookieJar implements CookieJar {
    // 使用 ConcurrentHashMap 保证线程安全
    private final Map<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    /**
     * 保存服务器返回的 Cookie
     */
    @Override
    public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        if (cookies.isEmpty()) {
            return;
        }

        String domain = url.host();

        // 使用 compute 方法保证原子性操作
        cookieStore.compute(domain, (key, existingCookies) -> {
            List<Cookie> domainCookies = (existingCookies != null) ?
                    new CopyOnWriteArrayList<>(existingCookies) :
                    new CopyOnWriteArrayList<>();

            // 更新或添加 Cookie
            for (Cookie newCookie : cookies) {
                // 移除已存在的同名 Cookie
                domainCookies.removeIf(existingCookie ->
                        existingCookie.name().equals(newCookie.name()));

                // 添加新 Cookie
                domainCookies.add(newCookie);
            }

            return domainCookies;
        });
    }

    /**
     * 为请求加载合适的 Cookie
     */
    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        List<Cookie> matchingCookies = new CopyOnWriteArrayList<>();

        // 遍历所有 Cookie，找到匹配的
        cookieStore.forEach((domain, cookies) -> {
            for (Cookie cookie : cookies) {
                if (cookie.matches(url)) {
                    matchingCookies.add(cookie);
                }
            }
        });

        return matchingCookies;
    }

    /**
     * 手动添加 Cookie（简化方法，只需域名、键、值）
     * @param domain Cookie 所属域名
     * @param name Cookie 名称
     * @param value Cookie 值
     */
    public void addCookie(String domain, String name, String value) {
        // 创建一个简单的 Cookie（永不过期，适用于所有路径）
        Cookie cookie = new Cookie.Builder()
                .domain(domain)
                .path("/")
                .name(name)
                .value(value)
                .build();

        // 使用 compute 方法保证原子性操作
        cookieStore.compute(domain, (key, existingCookies) -> {
            List<Cookie> domainCookies = (existingCookies != null) ?
                    new CopyOnWriteArrayList<>(existingCookies) :
                    new CopyOnWriteArrayList<>();

            // 移除已存在的同名 Cookie
            domainCookies.removeIf(existingCookie ->
                    existingCookie.name().equals(name));

            // 添加新 Cookie
            domainCookies.add(cookie);

            return domainCookies;
        });
    }

    /**
     * 获取指定域名的所有 Cookie
     * @param domain 域名
     * @return 该域名的 Cookie 列表
     */
    public List<Cookie> getCookiesForDomain(String domain) {
        List<Cookie> cookies = cookieStore.get(domain);
        return cookies != null ? new ArrayList<>(cookies) : new ArrayList<>();
    }

    /**
     * 清除所有 Cookie
     */
    public void clearAllCookies() {
        cookieStore.clear();
    }

    /**
     * 清除指定域名的 Cookie
     * @param domain 域名
     */
    public void clearCookiesForDomain(String domain) {
        cookieStore.remove(domain);
    }

    /**
     * 移除指定域名的特定 Cookie
     * @param domain 域名
     * @param name Cookie 名称
     */
    public void removeCookie(String domain, String name) {
        cookieStore.computeIfPresent(domain, (key, cookies) -> {
            cookies.removeIf(cookie -> cookie.name().equals(name));
            return cookies.isEmpty() ? null : cookies; // 如果列表为空，移除该域名的条目
        });
    }
}