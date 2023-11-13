package org.example.gui.conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    private JdbcTemplate conexaoDoBanco;

    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.17.0.2:3306/monitoons");
        dataSource.setUsername("animoons");
        dataSource.setPassword("animoons");
        conexaoDoBanco = new JdbcTemplate(dataSource);

    }

    public JdbcTemplate getConexaoDoBanco() {
        return conexaoDoBanco;
    }
}

