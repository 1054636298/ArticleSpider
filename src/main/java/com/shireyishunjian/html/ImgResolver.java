package com.shireyishunjian.html;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ImgResolver {
    public static List<String> getImgList(Element element){
        List<String> imgList=new ArrayList<>();
        Elements elements=element.select(".mbn.savephotop>img");
        for (Element img : elements) {
            String url=img.attr("file");
            imgList.add(url.startsWith("http")?url:"https://www.shireyishunjian.com/main/"+url);
        }
        return imgList;
    }
    public static List<String> getImgList(Elements elements){
        List<String> imgList=new ArrayList<>();
        for (Element element : elements) {
            imgList.addAll(getImgList(element));
        }
        return imgList;
    }
}
