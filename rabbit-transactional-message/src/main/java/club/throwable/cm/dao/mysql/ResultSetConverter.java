package club.throwable.cm.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/5 11:35
 */
public interface ResultSetConverter<T> {

    T convert(ResultSet resultSet) throws SQLException;
}
