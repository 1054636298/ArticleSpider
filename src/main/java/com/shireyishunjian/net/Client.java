package com.shireyishunjian.net;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Client {
    static final String User_Agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0";
    static final String URL="https://www.shireyishunjian.com/";
    OkHttpClient client;
    CookieJar cookieJar=new MemoryCookieJar();

    public Client(List<String> ipaddressList){

        CustomDns dns=new CustomDns();
        for (String address: ipaddressList){
            dns.addMapping("www.shireyishunjian.com",address);
        }

        client=new OkHttpClient().newBuilder()
                .dns(dns)
                .cookieJar(cookieJar)
                .connectTimeout(Duration.ofSeconds(5))
                .callTimeout(Duration.ofSeconds(6))
                .build();
    }

    public void register(String name,String password)throws IOException{
        String hash=getFormHash();
        String agree=getAgreeBbrul();
        MultipartBody.Builder form = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        form.addFormDataPart("regsubmit","yes");
        form.addFormDataPart("formhash",hash);
        form.addFormDataPart("referer","https://www.shireyishunjian.com/main/");
        form.addFormDataPart("shireuser",name);
        form.addFormDataPart("shirepass",password);
        form.addFormDataPart("shirepass2",password);
        form.addFormDataPart("shiremail",StringUtils.getRandomEmail());
        form.addFormDataPart("field6","不确定");
        form.addFormDataPart("gender","0");
        form.addFormDataPart("agreebbrule",agree);

        Request request=new Request.Builder()
                .method("POST",form.build())
                .header("User-Agent",User_Agent)
                .url("https://www.shireyishunjian.com/main/member.php?mod=register&inajax=1")
                .build();

        try (Response response=client.newCall(request).execute()){
            if (response.body()==null)throw new RuntimeException();

        }
    }

    public void register()throws IOException{
        register(StringUtils.getRandomName(),StringUtils.getRandomPassword());
    }

    public String getFormHash()throws IOException{
        Document document= Jsoup.parse(getBody("main/"));
        Element table=document.select(".y.pns").first();

        if (table == null)throw new RuntimeException();
        Element input = table.select("input[name='formhash']").first();
        if (input==null)throw new RuntimeException();
        return input.attr("value");
    }

    public String getAgreeBbrul()throws IOException{
        Document document= Jsoup.parse(getBody("main/member.php?mod=register"));
        Element span=document.select("#reginfo_a_btn").first();
        Element input=span.select("input[name='agreebbrule']").first();
        return input.attr("value");
    }

    public String getBody(String url)throws IOException{
        Request request=new Request.Builder()
                .get()
                .header("User-Agent",User_Agent)
                .url(URL+url)
                .build();

       try (Response response=client.newCall(request).execute()){
           if (response.code()!=200){
               throw new IOException("UnExpect status code");
           } else if (response.body()==null){
               throw new IOException("Empty body");
           }
           return response.body().string();
       }
    }

    public String getArticle(long tid,long page)throws IOException{
        return getBody(String.format("main/forum.php?mod=viewthread&tid=%d&page=%d",tid,page));
    }

    public String getArticleList(long fid,long page)throws IOException{
        return getBody(String.format("main/forum.php?mod=forumdisplay&fid=%d&page=%d",fid,page));
    }
}
