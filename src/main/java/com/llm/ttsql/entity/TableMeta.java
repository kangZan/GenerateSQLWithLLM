package com.llm.ttsql.entity;

import java.util.List;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 20:05.
 * 2.
 **/
public class TableMeta {
    private String tableName;
    private String description;
    private List<ColumnMeta> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMeta> columns) {
        this.columns = columns;
    } public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
