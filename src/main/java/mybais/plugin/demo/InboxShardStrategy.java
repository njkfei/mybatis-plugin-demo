package mybais.plugin.demo;

import org.apache.ibatis.binding.MapperMethod;

/**
 * Created by niejinping on 2017/5/10.
 */
public class InboxShardStrategy implements ShardStrategy {

    public String getTableName(String tableName, Object param) {

        StringBuilder sb = new StringBuilder();
        sb.append(tableName);
        Long uid = null;
        if(param instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) param;
            if (paramMap.containsKey("uid")) {
                uid = (Long) paramMap.get("uid");


            }
        }else if(param instanceof T){
            uid = ((T) param).getUid();
        }

        if (uid != null) {
            if (uid < 10) {
                sb.append((uid % 100));
            } else {
                String uidone = uid + "";
                Long mod2 = Long.parseLong(uidone.substring(0, uidone.length() - 1)) % 100;

                sb.append(mod2);
            }
        }

        return sb.toString();
    }

}
