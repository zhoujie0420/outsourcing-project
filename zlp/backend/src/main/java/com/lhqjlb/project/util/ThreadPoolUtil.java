package com.lhqjlb.project.util;

import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolUtil {

    // 无界
    public static final ThreadPoolExecutor EXECUTOR = ExecutorBuilder.create()
            .setCorePoolSize(4)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .build();

}
