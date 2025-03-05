package com.llm.ttsql.config;

import com.llm.ttsql.extractor.SqlExtractor;
import com.llm.ttsql.extractor.impl.DefaultSqlExtractor;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 22:17.
 * 2.
 **/
public class Config {
    private int timeout = 5000;
    //最大重试次数
    private int maxRetries = 1;
    private String dataBaseName = "MySql";
    private SqlExtractor sqlExtractor = new DefaultSqlExtractor();

    // Builder模式配置
    public Config timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Config maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public Config sqlExtractor(SqlExtractor extractor) {
        this.sqlExtractor = extractor;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public SqlExtractor getSqlExtractor() {
        return sqlExtractor;
    }

    public String getDataBaseName() {
        return dataBaseName ;
    }
}
