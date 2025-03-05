package com.llm.ttsql.exception;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 22:19.
 * 2.
 **/
public class SqlGenerationException extends Exception {
    public SqlGenerationException(String message) {
        super(message);
    }

    public SqlGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}