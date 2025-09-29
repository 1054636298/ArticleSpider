package com.shireyishunjian.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class PostListResolver {
    public static List<Integer> resolvePostList(String content){
        Document document= Jsoup.parse(content);
        List<Integer> postList=new ArrayList<>();
        Elements elements = document.select("[id^='normalthread_']");
        for (Element post:elements){
            Element a=post.select(".s.xst").first();
            if (a==null)throw new RuntimeException("link not found");
            String url=a.attr("href");
            String[] str=url.split("&");
            for (String s:str){
                if (s.startsWith("tid=")){
                    postList.add(Integer.valueOf(s.substring(4)));
                }
            }
        }
        return postList;
    }

    public static boolean hasNext(String content){
        Document document=Jsoup.parse(content);
        Element element=document.select(".bm.bw0.pgs.cl").first();
        if (element==null)return false;

        Elements elements=element.select("strong");
        if (elements.isEmpty())return false;

        String current=elements.text();
        String total=element.select("label span").text()
                .replace('页',' ')
                .replace('/',' ')
                .trim();

        return !total.equals(current);
    }
}
