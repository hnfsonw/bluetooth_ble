package com.ronda.tech.upgrade.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池创建类
 * Created by SNOW on 2019/7/15.
 */

public class ExecutorService {

    public static ThreadPoolExecutor createExecutorService(int threadNumber){
        return new ThreadPoolExecutor(
                threadNumber,
                threadNumber,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
