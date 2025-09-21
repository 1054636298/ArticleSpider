package com.shireyishunjian.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shireyishunjian.config.Config;
import com.shireyishunjian.net.Client;

import java.io.File;
import java.util.concurrent.Executors;

class Main{
    public static void main(String[] args) throws Throwable{
        Config config=new ObjectMapper().readValue(new File("config.json"),Config.class);
        Client client=new Client(config.getResolve());
        client.register();

        try (Spider spider=new Spider(client,config, Spider.FailPolicy.SLIP_ON_FAIL);
        var executor= Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory())){
            Thread.ofPlatform().name("link-loader").start(spider::load);
            //spider.sync();
            for (int i = 0; i<Integer.parseInt(config.getMax_thread()); i++){
                int finalI = i;
                executor.submit(()->{
                    Thread.currentThread().setName("spider-"+ finalI);
                    spider.getTask().run();
                });
            }
        }
    }
}