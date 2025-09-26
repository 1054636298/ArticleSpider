package com.shireyishunjian.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shireyishunjian.config.Config;
import com.shireyishunjian.net.Client;
import com.shireyishunjian.spider.Spider;
import com.shireyishunjian.spider.SyncTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class Main{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Throwable{
        Config config=new ObjectMapper().readValue(new File("config.json"),Config.class);
        logger.info("Starting with fid:{},thread:{},output:'{}',syncFile:'{}'",
                config.getFid(),config.getMax_thread(),config.getOutput(),config.getSync_file());

        Client client=new Client(config.getResolve());
        client.register();
        client.upgrade();

        boolean loadFromFile=false;
        BlockingQueue<Long> queue=new LinkedBlockingQueue<>();
        File dat=new File(config.getSync_file());
        if (dat.exists()&&dat.length()>0){
            try (FileInputStream fis=new FileInputStream(dat);
                 DataInputStream dis=new DataInputStream(fis)){
                while(true){
                    queue.add(dis.readLong());
                    loadFromFile=true;
                }
            }catch (EOFException ignored){
                logger.info("Loaded {} links from file {}",queue.size(),dat);
            }catch (IOException e){
                logger.error("Failed loading links from file {}",dat,e);
                queue.clear();
            }
        }

        try (Spider spider=new Spider(client,config, Spider.FailPolicy.SLIP_ON_FAIL,queue);
             SyncTimer syncTimer=new SyncTimer(spider);
             var executor= Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory())){
            if (!loadFromFile) {
                Thread.ofPlatform().name("link-loader").start(spider::load);
            }
            syncTimer.run();
            for (int i = 0; i<Integer.parseInt(config.getMax_thread()); i++){
                int finalI = i;
                executor.submit(()->{
                    Thread.currentThread().setName("spider-"+ finalI);
                    logger.trace("Thread {} started",finalI);
                    spider.getTask().run();
                });
            }
        }
        System.exit(0);
    }
}