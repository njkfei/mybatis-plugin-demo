package mybais.plugin.demo;

/**
 * Created by niejinping on 2017/5/9.
 */

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Connection;
import java.util.Properties;

import static org.apache.ibatis.reflection.SystemMetaObject.DEFAULT_OBJECT_FACTORY;
import static org.apache.ibatis.reflection.SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })
@Log4j2(topic = "application")
public class InboxInterceptor implements Interceptor {
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler,DEFAULT_OBJECT_FACTORY ,DEFAULT_OBJECT_WRAPPER_FACTORY,new DefaultReflectorFactory());
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");//获得sql
        log.error("hello mybatis plugin");
        log.debug(new Gson().toJson(metaStatementHandler.getGetterNames()));

        String id = (String) metaStatementHandler.getValue("delegate.mappedStatement.id"); // 获取接口
        log.debug("生成分页SQL : "+originalSql);
        log.debug("mapper:{}",id);

        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        log.debug(new Gson().toJson(boundSql.getSql()));

        return invocation.proceed();
    }
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub
    }
}