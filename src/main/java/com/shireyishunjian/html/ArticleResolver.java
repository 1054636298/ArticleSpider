package com.shireyishunjian.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleResolver {
    public static Element resolveAll(String content){
        Document document= Jsoup.parse(content);
        Element postList= document.select("#postlist").first();
        if (postList==null)throw new RuntimeException("#postlist not found");

        postList.select("#fj").remove();
        postList.select(".ad").remove();
        postList.select(".mtw.mbm.hm.cl").remove();
        postList.select(".pob.cl").remove();
        postList.select(".xl.xl2.o.cl").remove();
        postList.select(".cl").removeAttr("style");

        return postList;
    }
    public static boolean hasNext(String content){
        Document document=Jsoup.parse(content);
        return !document.select(".pgbtn").isEmpty();
    }

    public static Elements resolveReplies(String content){
        Document document= Jsoup.parse(content);
        Elements postList= document.select("#postlist");
        if (postList.isEmpty())throw new RuntimeException("#postlist not found");

        postList.select("#fj").remove();
        postList.select(".ad").remove();
        postList.select(".mtw.mbm.hm.cl").remove();
        postList.select(".pob.cl").remove();
        postList.select(".xl.xl2.o.cl").remove();
        postList.select(".cl").removeAttr("style");

        return postList.select("[id^='post_']");
    }

}
