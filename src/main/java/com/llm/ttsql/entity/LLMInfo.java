package com.llm.ttsql.entity;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 21:59.
 * 2.
 **/
public class LLMInfo {
    String apiKey;
    String model;
    /*
     * 聊天端点
     */
    static String chatEndpoint;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKeys) {
        apiKey = apiKeys;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String models) {
        model = models;
    }

    public String getChatEndpoint() {
        return chatEndpoint;
    }

    public void setChatEndpoint(String chatEndpoints) {
        chatEndpoint = chatEndpoints;
    }
}
