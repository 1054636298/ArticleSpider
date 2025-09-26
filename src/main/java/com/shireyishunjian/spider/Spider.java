package com.shireyishunjian.spider;

import com.shireyishunjian.config.Config;
import com.shireyishunjian.html.ArticleResolver;
import com.shireyishunjian.html.HTMLUtils;
import com.shireyishunjian.html.PostListResolver;
import com.shireyishunjian.net.Client;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.LongAdder;

public class Spider implements AutoCloseable{
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    Client client;
    Config config;
    BlockingQueue<Long> queue;
    OutputStream output;
    boolean closed=false;
    boolean loadedAll =false;
    File outDir;
    FailPolicy failPolicy;
    LongAdder fail=new LongAdder();

    public enum FailPolicy{
        EXIT_ON_FAIL,
        SLIP_ON_FAIL,
        RETRY_ON_FAIL,
        FAIL3_EXIT,
        RETRY_ON_FAIL_FAIL3_EXIT
    }


    public Spider(Client client, Config config, FailPolicy policy,BlockingQueue<Long> queue){
        this.client = client;
        this.config = config;
        this.failPolicy=policy;
        this.queue=queue;

        outDir= new File(config.getOutput());
        outDir.mkdirs();
    }

    public void sync(){
        try (var output=new FileOutputStream(config.getSync_file());
        DataOutputStream dataOut=new DataOutputStream(output)){
            synchronized (this){
                if (!queue.isEmpty()){
                    notifyAll();
                }
            }
            for (Long num:queue){
                dataOut.writeLong(num);
            }
            logger.info("Sync complete with {} links",queue.size());
        } catch (IOException e) {
            logger.error("Failed sync",e);
        }
    }

    @Override
    public void close() throws Exception {
        closed=true;
        sync();
        if (output!=null)output.close();
        queue.clear();
    }

    public void load(){
        long total=0;
        try {
            long i=1;
            String page=client.getArticleList(config.getFid(),i);
            List<Long> list=PostListResolver.resolvePostList(page);
            total+=list.size();
            queue.addAll(list);
            logger.info("Loaded page {}",i);
            while (PostListResolver.hasNext(page)){
                i++;
                page=client.getArticleList(config.getFid(),i);
                list=PostListResolver.resolvePostList(page);
                total+=list.size();
                queue.addAll(list);
                synchronized (this) {
                    notifyAll();
                }
                logger.info("Loaded page {}",i);
            }
            logger.info("Successfully load {} link in {} pages",total,i);
        } catch (IOException e) {
            logger.error("Failed to load pages",e);
        } finally {
            loadedAll=true;
        }

    }

    @SuppressWarnings("BusyWait")
    public Runnable getTask(){
        return ()->{
            while (!closed){
                Long temp=queue.poll();

                synchronized (this) {
                    if (temp == null) {
                        try {
                            wait();
                            if (loadedAll)break;
                            continue;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                long tid=temp;

                try {
                    handleArticle(tid);
                    logger.debug("Successfully get article {}",tid);
                } catch (Exception e) {
                    logger.error("Failed getting article {}", tid, e);
                    try {
                        switch (failPolicy){
                            case EXIT_ON_FAIL -> close();
                            case SLIP_ON_FAIL -> Thread.sleep(500);
                            case RETRY_ON_FAIL ->{
                                Thread.sleep(500);
                                handleArticle(tid);
                            }
                            case FAIL3_EXIT -> {
                                fail.add(1);
                                if (fail.intValue()>3)
                                    close();
                            }
                            case RETRY_ON_FAIL_FAIL3_EXIT -> {
                                fail.add(1);
                                handleArticle(tid);
                                if (fail.intValue()>3)
                                    close();
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("Failed retrying on article {}",tid,ex);
                    }
                }
            }
            logger.info("Task finished");
        };
    }
    public void handleArticle(long tid)throws IOException{
        logger.trace("Handle article {}",tid);
        Document document= Jsoup.parse("<html><head></head><body></body></html>");
        int i=1;

        String page =client.getArticle(tid,i);
        Element main= ArticleResolver.resolveAll(page);

        while (ArticleResolver.hasNext(page)){
            i++;
            page=client.getArticle(tid,i);
            main.appendChildren(ArticleResolver.resolveReplies(page));
        }

        HTMLUtils.addHead(document);
        document.body().appendChild(main);

        try {
            writeFile(tid,document);
        } catch (IOException e) {
            logger.error("Failed writing disk",e);
        }
    }

    private void writeFile(long tid,Document doc) throws IOException{
        String name=String.format("%09d",tid);
        String[] names=splitString(name,3);
        File dir=new File(outDir,names[0]+"/"+names[1]);
        dir.mkdirs();

        File file=new File(dir,names[2]+".html");
        Files.writeString(file.toPath(),doc.html(),StandardCharsets.UTF_8);
    }

    public static String[] splitString(String str, int partLength) {
        List<String> result = new ArrayList<>();
        int currSize = str.length();        //循环遍历
        for (int i = 0; i < currSize; i += partLength) {// 截取字符串，确保不超出原字符串的长度
            String part = str.substring(i, Math.min(currSize, i + partLength));//将分割的字符串添加到result中
            result.add(part);
        }
        //返回分割后的字符串集合
        return result.toArray(new String[0]);
    }
}
