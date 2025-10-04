package com.shireyishunjian.config;

import java.util.List;

@SuppressWarnings("unused")
public class Config {
    boolean download_img=false;
    String img_dir="./img";
    int max_page;
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

    public int getMax_page() {
        return max_page;
    }

    public boolean isDownload_img() {
        return download_img;
    }

    public void setDownload_img(boolean download_img) {
        this.download_img = download_img;
    }

    public String getImg_dir() {
        return img_dir;
    }

    public void setImg_dir(String img_dir) {
        this.img_dir = img_dir;
    }

    public void setMax_page(int max_page) {
        this.max_page = max_page;
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
