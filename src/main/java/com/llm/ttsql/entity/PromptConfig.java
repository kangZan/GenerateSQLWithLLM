package com.llm.ttsql.entity;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 21:59.
 * 2.
 **/
public class PromptConfig {

    // 构造大模型提示
    static String sysTemDefaultPrompt = "你是一个专业的 %s DBA工程师，请根据以下数据库结构及用户问题需求生成SQL查询。" +
            "以下是整个数据库表结构：" +
            "%s" +
            "\n用户问题：%s" +
            "\n要求：" +
            "\n1. 使用支持的SQL语法" +
            "\n2. 只生成SELECT语句" +
            "\n3. 包含必要的字段说明" +
            "\n4. 结合问题，只选取需要查询的表生成查询sql" +
            "\n5. 结合问题，只生成一条sql" +
            "\n6. 返回格式：{ \"sql\": \"生成的SQL\" }";

    public static String getSysTemDefaultPrompt() {
        return sysTemDefaultPrompt;
    }

    public static void setSysTemDefaultPrompt(String prompt) {
        sysTemDefaultPrompt = prompt;
    }
}
