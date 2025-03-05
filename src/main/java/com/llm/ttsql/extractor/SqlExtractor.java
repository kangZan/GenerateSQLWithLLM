package com.llm.ttsql.extractor;

import com.llm.ttsql.exception.SqlGenerationException;

/**
 * 策略模式sql解析接口
 */
public interface SqlExtractor {
    String extract(String input) throws SqlGenerationException;
}