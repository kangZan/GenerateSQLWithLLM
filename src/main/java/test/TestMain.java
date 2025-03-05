package test;

import com.llm.ttsql.GenerateSQLWithLLM;
import com.llm.ttsql.entity.ColumnMeta;
import com.llm.ttsql.entity.LLMInfo;
import com.llm.ttsql.entity.TableMeta;
import com.llm.ttsql.exception.SqlGenerationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Text2SQLForLLM
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2025/3/5 22:06.
 * 2.
 **/
public class TestMain {
    public static void main(String[] args) {
        LLMInfo llm = new LLMInfo();
        llm.setApiKey("Bearer 你自己的key");
        llm.setModel("qwen2.5:latest");
//        "deepseek-r1:1.5b";
        llm.setChatEndpoint("http://localhost:11434/v1/chat/completions");
        GenerateSQLWithLLM g=new GenerateSQLWithLLM(llm);
        try{
            String sql = g.generateSQL("本月系统登录人数和登录人次分别有多少？", testGetListTableMeta());
            System.out.println("--->" + sql);
        }catch (SqlGenerationException e){
            e.printStackTrace();
        }
    }

    public static List<TableMeta> testGetListTableMeta() {
        List<TableMeta> result = new ArrayList<TableMeta>();


        List<ColumnMeta> usertTablecolumnMetaList = new ArrayList<ColumnMeta>();
        TableMeta usertTable = new TableMeta();

        usertTable.setDescription("用户信息表");
        usertTable.setTableName("sys_user");
        ColumnMeta usertTablecolumnMetaListcolumnMeta4 = new ColumnMeta();
        usertTablecolumnMetaListcolumnMeta4.setDescription("用户类型(1:员工、2：领导、3：外委员工)");
        usertTablecolumnMetaListcolumnMeta4.setName("user_type");
        usertTablecolumnMetaListcolumnMeta4.setType("int");
        usertTablecolumnMetaList.add(usertTablecolumnMetaListcolumnMeta4);
        ColumnMeta usertTablecolumnMeta3 = new ColumnMeta();
        usertTablecolumnMeta3.setDescription("创建时间");
        usertTablecolumnMeta3.setName("create_time");
        usertTablecolumnMeta3.setType("datetime");
        usertTablecolumnMetaList.add(usertTablecolumnMeta3);
        ColumnMeta usertTablecolumnMeta2 = new ColumnMeta();
        usertTablecolumnMeta2.setDescription("用户名称");
        usertTablecolumnMeta2.setName("user_name");
        usertTablecolumnMeta2.setType("varchar");
        usertTablecolumnMetaList.add(usertTablecolumnMeta2);
        ColumnMeta usertTablecolumnMeta = new ColumnMeta();
        usertTablecolumnMeta.setDescription("主键id");
        usertTablecolumnMeta.setName("id");
        usertTablecolumnMeta.setType("varchar");
        usertTablecolumnMetaList.add(usertTablecolumnMeta);
        usertTable.setColumns(usertTablecolumnMetaList);
        result.add(usertTable);

        List<ColumnMeta> columnMetaList = new ArrayList<ColumnMeta>();
        TableMeta loginLog = new TableMeta();

        loginLog.setDescription("用户操作日志表");
        loginLog.setTableName("sys_log");
        ColumnMeta columnMeta4 = new ColumnMeta();
        columnMeta4.setDescription("操作类型(1:系统查看、2：系统操作、3：登录操作、4：退出操作)");
        columnMeta4.setName("op_type");
        columnMeta4.setType("int");
        columnMetaList.add(columnMeta4);
        ColumnMeta columnMeta3 = new ColumnMeta();
        columnMeta3.setDescription("操作时间");
        columnMeta3.setName("op_time");
        columnMeta3.setType("datetime");
        columnMetaList.add(columnMeta3);
        ColumnMeta columnMeta2 = new ColumnMeta();
        columnMeta2.setDescription("登录人id");
        columnMeta2.setName("user_id");
        columnMeta2.setType("varchar");
        columnMetaList.add(columnMeta2);
        ColumnMeta columnMeta = new ColumnMeta();
        columnMeta.setDescription("主键id");
        columnMeta.setName("id");
        columnMeta.setType("varchar");
        columnMetaList.add(columnMeta);
        loginLog.setColumns(columnMetaList);
        result.add(loginLog);

        return result;
    }
}
