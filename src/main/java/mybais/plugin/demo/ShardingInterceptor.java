package mybais.plugin.demo;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mybais.plugin.demo.Utils.ShardUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.logging.log4j.util.Strings;

import java.sql.Connection;
import java.util.*;

/**
 * Created by niejinping on 2017/5/9.
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })
@Log4j2(topic = "application")
public class ShardingInterceptor implements Interceptor{
    private List<String> tableNames = new ArrayList<String>();
    private Map<String , ShardStrategy> STRATEGY_CONTEXT = new HashMap<String, ShardStrategy>();
    private Map<String , ShardStrategy> TABLE_ROUTER = new HashMap<String, ShardStrategy>();
    private final static String SQL_PARAM_NAME = "delegate.parameterHandler.parameterObject";
    private final static String MAPPED_STATEMENT_NAME = "delegate.parameterHandler.mappedStatement";
    private final static String BOUNDSQL_SQL_NAME = "delegate.boundSql.sql";
    private final static String STRATEGY_SUFFIX = "Strategy";
    private final static ShardStrategy DEFAULTSTRATEGY = new DefaultShardStrategy();


    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        log.error("hello mybatis ShardingInterceptor plugin");

        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");//获得sql
        log.debug(originalSql);


        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        log.debug(new Gson().toJson(boundSql.getSql()));

        String tableName = ShardUtil.getTableNameFromSql(boundSql.getSql());

        log.debug("tableName : {}",tableName);

        Object object = metaStatementHandler.getValue(SQL_PARAM_NAME);

        String newSql = decorateSql(tableName,boundSql.getSql(),object);

        if(newSql.equals(boundSql.getSql()) == false){
            metaStatementHandler.setValue(BOUNDSQL_SQL_NAME,newSql);
            log.debug("newSQL : {}",newSql);
        }

        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    /**
     *
     * @param tableName
     * @param executeSql
     * @param paramMap 可能是传入的对象，也可能是注解上面的东西，需要区别对待哦
     * @return
     */
    private String decorateSql(String tableName,String executeSql, Object paramMap) {

        if(TABLE_ROUTER.containsKey(tableName)){
            String newTaleName = TABLE_ROUTER.get(tableName).getTableName(tableName , paramMap);
            //String newTaleName2 = new StringBuilder().append(" ").append(newTaleName).append(" ").toString();
            String regex = new StringBuilder().append("\\b").append(tableName).append("\\b").toString();
            executeSql = executeSql.replaceFirst(regex,newTaleName);
            return executeSql;
        }

        return executeSql;
    }


    public void setProperties(Properties properties) {
        setShardStrategy(properties);
        setTableNames(properties);
    }

    private void setTableNames(Properties properties){
        String tableName = properties.getProperty("tableNames");
        if(Strings.isBlank(tableName)){
            log.error("tableNames is empty");
            return;
        }

        String[] tempTableRouterStrList = tableName.trim().split(",");

        for(String tempTableRouterStr : tempTableRouterStrList){
            String[] single =tempTableRouterStr.trim().split("\\|");

            tableNames.add(single[0]);
            if(single.length == 1){
                TABLE_ROUTER.put(single[0], DEFAULTSTRATEGY);
            }else {
                TABLE_ROUTER.put(single[0], STRATEGY_CONTEXT.get(single[1]));
            }
        }
    }

    private void setShardStrategy(Properties properties){
        try {
            for(Map.Entry entry : properties.entrySet()){
                String strategyClassNameKey = entry.getKey().toString();
                if(strategyClassNameKey.indexOf(STRATEGY_SUFFIX)!=-1){
                    String strategyClassName = entry.getValue().toString();
                    Class strategyClass = Class.forName(strategyClassName);

                    Object o = strategyClass.newInstance();
                    if(o instanceof ShardStrategy){
                        STRATEGY_CONTEXT.put(strategyClassNameKey , (ShardStrategy)o);
                    }else {
                        throw new IllegalStateException(
                                "strategyClass must implement interface ShardStrategy<P>"
                        );
                    }
                }
            }
        }catch (Exception e){
            log.error("生成ShardStrategy策略失败", e);
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        String executeSql = "select * from t where 1 = 1 limit 1 ";
        String tableName = "t";
        String newTaleName = " t10 ";
        String regex = new StringBuilder().append("\\s+").append(tableName).append("\\s+").toString();
        executeSql = executeSql.replaceFirst(regex,newTaleName);
        log.debug(executeSql);

        regex = new StringBuilder().append("\\b").append(tableName).append("\\b").toString();
        executeSql = executeSql.replaceFirst(regex,newTaleName);
        log.debug(executeSql);

    }
}
