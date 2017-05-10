package mybais.plugin.demo.Utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Created by niejinping on 2017/5/10.
 */
@Log4j2(topic = "application")
public class ShardUtil {
    public static String getTableNameFromSql(String boundSql){
        String sql = boundSql.trim().toLowerCase();
        String[] sqls = sql.split("\\s+");
        String tableName="";
        String operatorFlag = sqls[0];

        if(operatorFlag.equals("select")){
            for(int i = 0; i < sqls.length;i++){
                if(sqls[i].equals("from")){
                    tableName = sqls[i+1];
                    break;
                }
            }
        }else if(operatorFlag.equals("update")){
            tableName = sqls[1];
        }else if(operatorFlag.equals("insert")){
            tableName = sqls[2].replaceAll("\\(.*","");
        }else if(operatorFlag.equals("replace")){
            tableName = sqls[2].replaceAll("\\(.*","");
        }else if(operatorFlag.equals("delete")){
            tableName = sqls[2];
        }else if(operatorFlag.equals("truncate")){
            tableName = sqls[2];
        }else {
           log.error("ERROR boundSql: {}",boundSql);
        }

        if(StringUtils.isNotBlank(tableName)){
            tableName = tableName.replace("`","").trim();
        }

        return tableName;
    }


    public static void main(String[] args){
        String boundSql = "SELECT * FROM `activity_prize` WHERE 1";

        log.debug(getTableNameFromSql(boundSql));
        boundSql = "SELECT `id`, `prize_name`, `prize_lists`, `probability`, `num`, `gid`, `create_at`, `update_at` FROM `activity_prize` WHERE 1";

        log.debug(getTableNameFromSql(boundSql));

        boundSql = "INSERT INTO `activity_prize(`id`, `prize_name`, `prize_lists`, `probability`, `num`, `gid`, `create_at`, `update_at`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6],[value-7],[value-8])";

        log.debug(getTableNameFromSql(boundSql));

        boundSql = "UPDATE `activity_prize` SET `id`=[value-1],`prize_name`=[value-2],`prize_lists`=[value-3],`probability`=[value-4],`num`=[value-5],`gid`=[value-6],`create_at`=[value-7],`update_at`=[value-8] WHERE 1";

        log.debug(getTableNameFromSql(boundSql));

        boundSql = "DELETE FROM product_csi WHERE 1";

        log.debug(getTableNameFromSql(boundSql));

        boundSql = "truncate table product_csi";

        log.debug(getTableNameFromSql(boundSql));
    }

}
