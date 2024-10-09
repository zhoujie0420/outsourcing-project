package com.lhqjlb.project.util;

import okhttp3.OkHttpClient;

public class OkHttpClientUtil {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }
}
