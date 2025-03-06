package com.llm.ttsql;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.llm.ttsql.config.Config;
import com.llm.ttsql.entity.ColumnMeta;
import com.llm.ttsql.entity.LLMInfo;
import com.llm.ttsql.entity.PromptConfig;
import com.llm.ttsql.entity.TableMeta;
import com.llm.ttsql.exception.SqlGenerationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 基于大模型的文本到SQL生成器
 *
 * <p>本类提供以下核心功能：</p>
 * <ol>
 *   <li>根据自然语言问题生成SQL语句</li>
 *   <li>支持自定义提示词</li>
 *   <li>可扩展的SQL解析策略</li>
 *   <li>带重试机制的模型调用</li>
 * </ol>
 *
 * <p>典型用法：</p>
 * <pre>{@code
 * GenerateSQLWithLLM g=new GenerateSQLWithLLM(llm);
 * String sql = g.generateSQL(your_question, testGetListTableMeta());
 * }</pre>
 *
 * @author zan.kang
 * @version 1.2
 * @history 1. [2025/03/05] 初始版本
 */
public class GenerateSQLWithLLM {
    private final Map<String, TableMeta> tableCache = new ConcurrentHashMap<>();

    /**
     * 缓存表元数据
     * @param table 需要缓存的表结构元数据
     */
    public void cacheTableMeta(TableMeta table) {
        tableCache.put(table.getTableName(), table);
    }

    /**
     * 批量缓存表元数据
     * @param tables 需要缓存的表结构列表
     */
    public void cacheAllTableMeta(List<TableMeta> tables) {
        tables.forEach(table -> tableCache.put(table.getTableName(), table));
    }
    private LLMInfo llmInfo;
    private Config config;

    /**
     * 构造方法（使用默认配置）
     *
     * @param llmInfo 大模型连接配置，包含API密钥和模型信息
     */
    public GenerateSQLWithLLM(LLMInfo llmInfo,List<TableMeta> tables) {
        this(llmInfo,tables, new Config());
    }
    /**
     * 构造方法（使用默认配置）
     *
     * @param llmInfo 大模型连接配置，包含API密钥和模型信息
     */
    public GenerateSQLWithLLM(LLMInfo llmInfo) {
        this(llmInfo,null, new Config());
    }

    /**
     * 构造方法（自定义配置）
     *
     * @param llmInfo 大模型连接配置
     * @param config  生成器配置，包括超时、重试策略、生成的sql支持的数据库等
     * @param tables 初始化时预缓存的表结构列表（可选，可传null）
     * @throws NullPointerException 如果任一参数为null
     */
    public GenerateSQLWithLLM(LLMInfo llmInfo, List<TableMeta> tables,Config config) {
        cacheAllTableMeta(tables);
        this.llmInfo = Objects.requireNonNull(llmInfo);
        this.config = Objects.requireNonNull(config);
    }

    /**
     * 生成SQL语句（自动构建提示词）
     * <p>当需要动态指定表结构时使用此方法，会优先使用传入的表结构</p>
     * @param question 用户自然语言问题（非空）
     * @param tables   涉及的表结构元数据（至少包含一个表）
     * @return 生成的SQL语句（非空）
     * @throws SqlGenerationException 当生成过程出现错误时抛出
     */
    public String generateSQL(String question, List<TableMeta> tables) throws SqlGenerationException {
        return generateSQL(question, tables, null);

    }

    /**
     * 生成SQL语句（使用缓存元数据）
     *
     *  <p>注意：使用前需确保已通过以下任一方式缓存表结构：</p>
     *  <ul>
     *    <li>构造方法预缓存</li>
     *    <li>调用cacheTableMeta/cacheAllTableMeta方法</li>
     *  </ul>
     *
     * @param question 用户自然语言问题（需非空字符串）
     * @return 生成的SQL语句
     * @throws SqlGenerationException 当生成失败时抛出
     *  @throws IllegalArgumentException 当question为空或缓存为空时抛出
     */
    public String generateSQL(String question) throws SqlGenerationException {
        List<TableMeta> cachedTables = new ArrayList<>(tableCache.values());
        if (cachedTables.isEmpty()) {
            throw new IllegalArgumentException("请先通过cacheTableMeta方法缓存表结构");
        }
        return generateSQL(question, cachedTables);
    }
    /**
     * 生成SQL语句（使用缓存元数据）
     * <p>适用于需要同时使用缓存表结构和自定义提示词的场景</p>
     * @param prompt   自定义提示词模板（将覆盖默认模板）
     * @param question 用户自然语言问题（非空）
     * @return 生成的SQL语句
     * @throws SqlGenerationException 当缓存为空或生成失败时抛出
     */
    public String generateSQL(String question, String prompt) throws SqlGenerationException {
        List<TableMeta> cachedTables = new ArrayList<>(tableCache.values());
        if (cachedTables.isEmpty()) {
            throw new IllegalArgumentException("请先通过cacheTableMeta方法缓存表结构");
        }
        return generateSQL(question, cachedTables,prompt);
    }
    /**
     * 生成SQL语句（自定义提示词）
     *
     * @param question 用户自然语言问题
     * @param tables   涉及的表结构元数据
     * @param prompt   自定义提示词（可选，null表示使用默认模板）
     * @return 生成的SQL语句
     * @throws SqlGenerationException   生成失败时抛出，包含详细错误信息
     * @throws IllegalArgumentException 当参数不合法时抛出
     */
    public String generateSQL(String question, List<TableMeta> tables, String prompt) throws SqlGenerationException {
        // 参数校验
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("问题内容不能为空");
        }
        if (tables == null || tables.isEmpty()) {
            throw new IllegalArgumentException("至少需要提供一个表结构");
        }

        try {
            String finalPrompt = (prompt != null) ? prompt : buildPrompt(question, tables);
            String sql = chatGetSqlWithRetry(finalPrompt);
            return sql;
        } catch (HttpException | ConvertException e) {
            throw new SqlGenerationException("LLM服务调用失败", e);
        }
    }

    /**
     * 构建默认提示词模板
     *
     * @param question 用户问题
     * @param tables   表结构列表
     * @return 符合大模型要求的完整提示词
     */
    protected String buildPrompt(String question, List<TableMeta> tables) {
        // 构建表结构描述
        StringBuilder schemaDesc = new StringBuilder();
        for (TableMeta table : tables) {
            schemaDesc.append("[表名: ").append(table.getTableName()).append("");
            schemaDesc.append(",表描述: ").append(table.getDescription()).append("\n");
            for (ColumnMeta col : table.getColumns()) {
                schemaDesc.append("- 列名: ").append(col.getName())
                        .append(", 类型: ").append(col.getType())
                        .append(", 描述: ").append(col.getDescription()).append("\n");
            }
            schemaDesc.append("\n]");
        }
        return String.format(PromptConfig.getSysTemDefaultPrompt(), config.getDataBaseName(),schemaDesc, question);
    }

    /**
     * 带重试机制的模型调用
     *
     * @param prompt 完整的提示词内容
     * @return 解析后的SQL语句
     * @throws SqlGenerationException 当超过最大重试次数时抛出
     */
    private String chatGetSqlWithRetry(String prompt) throws SqlGenerationException {
        int retries = 0;
        Exception lastException = null;

        while (retries <= config.getMaxRetries()) {
            try {
                return chat(prompt);
            } catch (Exception e) {
                lastException = e;
                retries++;
            }
        }
        throw new SqlGenerationException(
                String.format("请求失败，已重试%d次", config.getMaxRetries()),
                lastException
        );
    }

    /**
     * 执行大模型调用并解析结果
     *
     * @param txt 完整的提示词内容
     * @return 原始模型响应内容
     * @throws SqlGenerationException 当通信失败或响应格式错误时抛出
     */
    private String chat(String txt) throws SqlGenerationException {
        System.out.println(txt);
        // 构造请求参数
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("model", llmInfo.getModel());
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Map paramMap2 = new HashMap<String, String>();
        paramMap2.put("role", "user");
        paramMap2.put("content", txt);
        dataList.add(paramMap2);
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            // 发送HTTP请求
            String body = HttpRequest.post(llmInfo.getChatEndpoint())
                    .header("Authorization", llmInfo.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(new JSONObject(paramMap).toString())
                    .execute()
                    .body();
            // 解析响应
            JSONObject jsonObject = JSONUtil.parseObj(body);
            System.out.println(jsonObject);
            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new SqlGenerationException("无效的模型响应：choices字段缺失或为空");
            }
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
            if (message == null) {
                throw new SqlGenerationException("无效的模型响应：message字段缺失");
            }
        } catch (HttpException e) {
            throw new SqlGenerationException("大模型http接口连接出错");
        } catch (ConvertException e) {
            throw new SqlGenerationException("调用结果转换出错");
        }
        return config.getSqlExtractor().extract(message.getStr("content"));
    }


}
