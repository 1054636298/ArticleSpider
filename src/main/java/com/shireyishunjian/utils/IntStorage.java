package com.shireyishunjian.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntStorage {
    private final static int DEFAULT_CAPACITY=10;
    private final Object lock=new Object();
    int[] storage;
    int position;

    public IntStorage(int capacity) {
        storage=new int[capacity];
        position=-1;
    }

    public IntStorage(){
        this(DEFAULT_CAPACITY);
    }

    public int getData()throws InterruptedException{
        synchronized (lock) {
            while (position<0)lock.wait();//我不清楚为什么用if有概率出现走到下面时position==-1

            int data=storage[position];
            position--;

            lock.notify();
            return data;
        }
    }

    public void pushData(int data)throws InterruptedException{
        synchronized (lock){
            while (position==storage.length-1)lock.wait();
            position++;
            storage[position]=data;
            lock.notify();
        }
    }

    public int size(){
        return position+1;
    }

    public byte[] toBytes(){
        synchronized (lock) {
            try (var stream = new ByteArrayOutputStream();
                 var out = new DataOutputStream(stream)) {

                for (int i = position; i >= 0; i--) {
                    out.writeInt(storage[i]);
                }
                return stream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
