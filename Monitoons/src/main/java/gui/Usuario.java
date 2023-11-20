package gui;

import conexao.Conexao;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Usuario {
    private Integer idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Integer idComputador;
    private String plano;

    public Usuario(String email, String senha){
        this.email = email;
        this.senha = senha;
    }

    public String logar() {
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();
        InetAddress inetAddress = null;

        try {
            // Obtém o endereço IP da máquina local
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Verificar se a senha está correta
        String senhaCerta = jdbcTemplate.queryForObject("SELECT senha FROM usuario WHERE email = ?", String.class, email);

        if (senha.equals(senhaCerta)) {
            // Obter informações do usuário
            idUsuario = jdbcTemplate.queryForObject("SELECT idUsuario FROM usuario WHERE email = ?", Integer.class, email);
            nome = jdbcTemplate.queryForObject("SELECT nomeUsuario FROM usuario WHERE email = ?", String.class, email);
            plano = jdbcTemplate.queryForObject("SELECT plano FROM usuario WHERE email = ?", String.class, email);

            Boolean existeComputador = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario) > 0;
            if (!existeComputador) {
                // Cadastrar computador no banco de dados
                jdbcTemplate.update("INSERT INTO computador (fkUsuario, nome) VALUES (?, ?)", idUsuario, inetAddress.getHostName());
                idComputador = jdbcTemplate.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario);
            } else {
                // Obter ID do computador
                idComputador = jdbcTemplate.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario);
            }

            return plano;
        } else {
            return null;
        }
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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

    public Integer getIdComputador() {
        return idComputador;
    }

    public void setIdComputador(Integer idComputador) {
        this.idComputador = idComputador;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }
}
