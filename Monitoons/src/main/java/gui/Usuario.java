package gui;

import conexao.Conexao;
import conexao.ConexaoSQLServer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Usuario {
    private Integer idUsuarioMySQL;
    private Integer idUsuarioSQLServer;
    private String nome;
    private String email;
    private String senha;
    private Integer idComputadorMySQL;
    private Integer idComputadorSQLServer;
    private String plano;

    public Usuario(String email, String senha){
        this.email = email;
        this.senha = senha;
    }

    public String logar() {
        ConexaoSQLServer conexaoSQLServer = new ConexaoSQLServer();
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplateMySQL = conexao.getConexaoDoBanco();
        JdbcTemplate jdbcTemplateMsSQL = conexaoSQLServer.getConexaoDoBanco();
        InetAddress inetAddress = null;

        try {
            // Obtém o endereço IP da máquina local
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Verificar se a senha está correta
        String senhaCerta = jdbcTemplateMsSQL.queryForObject("SELECT senha FROM usuario WHERE email = ?", String.class, email);

        if (senha.equals(senhaCerta)) {
            // Obter informações do usuário
            idUsuarioMySQL = jdbcTemplateMySQL.queryForObject("SELECT idUsuario FROM usuario WHERE email = ?", Integer.class, email);
            idUsuarioSQLServer = jdbcTemplateMsSQL.queryForObject("SELECT idUsuario FROM usuario WHERE email = ?", Integer.class, email);
            nome = jdbcTemplateMsSQL.queryForObject("SELECT nomeUsuario FROM usuario WHERE email = ?", String.class, email);
            plano = jdbcTemplateMsSQL.queryForObject("SELECT plano FROM usuario WHERE email = ?", String.class, email);

            Boolean existeComputador = jdbcTemplateMsSQL.queryForObject("SELECT COUNT(*) FROM computador WHERE fkUsuario = ?", Integer.class, idUsuarioSQLServer) > 0;
            if (!existeComputador) {
                // Cadastrar computador no banco de dados
                jdbcTemplateMsSQL.update("INSERT INTO computador (fkUsuario, nome) VALUES (?, ?)", idUsuarioSQLServer, inetAddress.getHostName());
                idComputadorSQLServer = jdbcTemplateMsSQL.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuarioSQLServer);

                jdbcTemplateMySQL.update("INSERT INTO computador (fkUsuario, nome) VALUES (?, ?)", idUsuarioMySQL, inetAddress.getHostName());
                idComputadorMySQL = jdbcTemplateMsSQL.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuarioMySQL);

            } else {
                // Obter ID do computador
                idComputadorSQLServer = jdbcTemplateMsSQL.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuarioSQLServer);

                idComputadorMySQL = jdbcTemplateMsSQL.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuarioMySQL);
            }

            return plano;
        } else {
            return null;
        }
    }

    public Integer getIdUsuarioMySQL() {
        return idUsuarioMySQL;
    }

    public void setIdUsuarioMySQL(Integer idUsuarioMySQL) {
        this.idUsuarioMySQL = idUsuarioMySQL;
    }

    public Integer getIdUsuarioSQLServer() {
        return idUsuarioSQLServer;
    }

    public void setIdUsuarioSQLServer(Integer idUsuarioSQLServer) {
        this.idUsuarioSQLServer = idUsuarioSQLServer;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getIdComputadorMySQL() {
        return idComputadorMySQL;
    }

    public void setIdComputadorMySQL(Integer idComputadorMySQL) {
        this.idComputadorMySQL = idComputadorMySQL;
    }

    public Integer getIdComputadorSQLServer() {
        return idComputadorSQLServer;
    }

    public void setIdComputadorSQLServer(Integer idComputadorSQLServer) {
        this.idComputadorSQLServer = idComputadorSQLServer;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }
}
