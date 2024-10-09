package com.lhqjlb.project.BaiDuDemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Chat {
    private final String ACCESS_TOKEN_URI = "https://aip.baidubce.com/oauth/2.0/token";
    private final String CHAT_URI = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/yi_34b_chat";

    private String apiKey = "Iyf6OvddxUR6v0rjQQVyUjYj";
    private String secretKey = "vOX70Z6Qv8Ns3MaKliaCnGmnkfY0GNEr";
    private String accessToken = "";

    private OkHttpClient client;

    //请求参数
    private RequestMessage requestBody = new RequestMessage();
    //响应超时时间
    private int responseTimeOut = 5000;

    public Chat() {
        this.client = new OkHttpClient.Builder().readTimeout(responseTimeOut, TimeUnit.SECONDS).build();
    }

    public Chat(int responseTimeOut) {
        this.client = new OkHttpClient.Builder().readTimeout(responseTimeOut, TimeUnit.SECONDS).build();
    }

    public boolean getAccessToken() {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        //创建一个请求
        Request request = new Request.Builder()
                .url(ACCESS_TOKEN_URI + "?client_id=" + apiKey + "&client_secret=" + secretKey + "&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            //使用浏览器对象发起请求
            Response response = client.newCall(request).execute();
            //只能执行一次response.body().string()。下次再执行会抛出流关闭异常，因此需要一个对象存储返回结果
            String responseMessage = response.body().string();
            System.out.println("获取accessToken成功");
            JSONObject jsonObject = JSON.parseObject(responseMessage);
            accessToken = (String) jsonObject.get("access_token");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取问题参数，返回答案
     *
     * @param question
     */
    public String getAnswer(String question) {
        //通过参数获取一个Message
        Message message = new Message("user", question);
        //将新的问题添加到消息上下文
        requestBody.addMessage(message);
        String jsonStr = JSON.toJSONString(requestBody);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        Request request = new Request.Builder()
                .url(CHAT_URI + "?access_token=" + accessToken)
                .addHeader("Content-Type", "application/json")
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseJsonStr = response.body().string();
            ResponseMessage responseMessage = JSON.parseObject(responseJsonStr, ResponseMessage.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseJsonStr);
            String res = jsonNode.get("result").asText();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public void getRequestBody() {
//        System.out.println(JSON.toJSONString(requestBody));
    }
}