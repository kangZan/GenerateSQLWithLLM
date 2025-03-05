package com.llm.ttsql.extractor.impl;

import cn.hutool.json.JSONObject;
import com.llm.ttsql.exception.SqlGenerationException;
import com.llm.ttsql.extractor.SqlExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 22:18.
 * 2.
 **/
public class DefaultSqlExtractor implements SqlExtractor {
    private static final Pattern[] CODE_BLOCK_PATTERNS = {
            Pattern.compile("```json\\s*(.*?)\\s*```", Pattern.DOTALL),
            Pattern.compile("```sql\\s*(.*?)\\s*```", Pattern.DOTALL)
    };

    @Override
    public String extract(String input) throws SqlGenerationException {
        try {
            String jsonContent = null;
            for (Pattern codeBlockPattern : CODE_BLOCK_PATTERNS) {
                Matcher matcher = codeBlockPattern.matcher(input);
                if (matcher.find()) {
                    // 1. 匹配到后提取JSON代码块
                    jsonContent = matcher.group(1);
                    break;
                }
            }
            if (jsonContent == null) {
                throw new SqlGenerationException("未找到JSON代码块");
            }
            // 在解析前添加格式校验
            if (!jsonContent.trim().startsWith("{")) {
                throw new SqlGenerationException("非标准JSON格式");
            }
            // 2. 解析JSON
            JSONObject root = new JSONObject(jsonContent);
            String sql = root.getStr("sql");
            // 添加简单的SQL注入检测
            if (sql.toUpperCase().matches(".*\\b(DROP|DELETE|INSERT|UPDATE|TRUNCATE)\\b.*")) {
                throw new SqlGenerationException("检测到危险操作");
            }
            return sql;
        } catch (Exception e) {
            throw new SqlGenerationException("SQL提取失败: " + e.getMessage());
        }
    }
}
