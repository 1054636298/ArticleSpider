package com.shireyishunjian.spider;

import java.util.Timer;
import java.util.TimerTask;

public class SyncTimer implements AutoCloseable{
    Timer timer = new Timer();
    Spider spider;

    public SyncTimer(Spider spider){
        this.spider=spider;
    }

    public void run(){
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                spider.sync();
            }
        };
        timer.schedule(task,1000,10000);
    }

    @Override
    public void close() throws Exception {
        timer.cancel();
    }
}
