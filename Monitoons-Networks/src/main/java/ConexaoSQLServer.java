import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

public class ConexaoSQLServer {
    private JdbcTemplate jdbcTemplate;
    public ConexaoSQLServer() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://3.209.58.184:1433;databaseName=monitoons;encrypt=true;trustServerCertificate=true;");
        dataSource.setUsername("animoons");
        dataSource.setPassword("animoons");

        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public JdbcTemplate getConexaoDoBanco() {
        return this.jdbcTemplate;
    }

    public int inserirERetornarIdGerado(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
}
