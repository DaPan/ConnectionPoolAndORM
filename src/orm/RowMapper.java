package orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 这个类是为了查询每一行记录定义的一个映射规则
 * 这个队长每个查询不一样  具体策略
 */
public interface RowMapper {

    //方法定义策略 查询完毕的结果如何组装成domain对象
    public Object mapperRow(ResultSet rs) throws SQLException;
}
