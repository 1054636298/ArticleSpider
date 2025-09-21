package com.shireyishunjian.config;

import java.util.List;

@SuppressWarnings("unused")
public class Config {
    int fid;
    List<String> resolve;
    String sync_file;
    String max_thread;
    String output;

    public int getFid() {
        return fid;
    }

    public List<String> getResolve() {
        return resolve;
    }

    public String getSync_file() {
        return sync_file;
    }

    public String getMax_thread() {
        return max_thread;
    }

    public String getOutput() {
        return output;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public void setResolve(List<String> resolve) {
        this.resolve = resolve;
    }

    public void setSync_file(String sync_file) {
        this.sync_file = sync_file;
    }

    public void setMax_thread(String max_thread) {
        this.max_thread = max_thread;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
