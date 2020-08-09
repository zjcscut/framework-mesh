package club.throwable.cm.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/5 11:51
 */
@FunctionalInterface
public interface PreparedStatementProcessor {

    void process(PreparedStatement ps) throws SQLException;
}
