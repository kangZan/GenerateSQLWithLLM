package com.llm.ttsql.entity;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 20:05.
 * 2.
 **/
public class ColumnMeta {

    private String name;
    private String type;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
