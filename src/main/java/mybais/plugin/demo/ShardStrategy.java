package mybais.plugin.demo;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;

/**
 * Created by niejinping on 2017/5/10.
 */
public interface ShardStrategy {
    /**
     * 获取表名
     * @param param
     * @return
     */
    String getTableName(String tableName ,Object param);
}
