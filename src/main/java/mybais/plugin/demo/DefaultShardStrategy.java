package mybais.plugin.demo;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.ParameterMap;
import org.omg.Dynamic.Parameter;

import java.util.List;

/**
 * Created by niejinping on 2017/5/10.
 */
public class DefaultShardStrategy implements ShardStrategy {

    public String getTableName(String tableName, Object param) {

        StringBuilder sb = new StringBuilder();
        sb.append(tableName);

        if(param instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap param2 = (MapperMethod.ParamMap)param;
            if (param2.containsKey("id")) {
                Long id = (Long) param2.get("id");

                if (id != null) {
                    sb.append(id % 10);
                }
            }
        }

        return sb.toString();
    }

}
