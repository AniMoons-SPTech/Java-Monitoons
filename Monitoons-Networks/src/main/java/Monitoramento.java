import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import org.springframework.jdbc.core.JdbcTemplate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Monitoramento {
    public static void main(String[] args) throws InterruptedException {
        // Desabilitar consultas para informações específicas do processador
        System.setProperty("oshi.util.platform.windows.PerfCounterWildcardQuery", "false");

        // Inicialização dos objetos necessários
        Scanner scanner = new Scanner(System.in);
        Looca looca = new Looca();
        Conexao conexao = new Conexao();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();
        InetAddress inetAddress = null;

        try {
            // Obtém o endereço IP da máquina local
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Integer idUsuario;
        String nomeUsuario;
        Integer idComputador = null;

        // Solicitar credenciais do usuário
        System.out.println("Insira seu email: ");
        String email = scanner.nextLine();

        System.out.println("Insira sua senha: ");
        String senha = scanner.nextLine();

        // Verificar se a senha está correta
        String senhaCerta = jdbcTemplate.queryForObject("SELECT senha FROM usuario WHERE email = ?", String.class, email);

        if (senha.equals(senhaCerta)) {
            // Obter informações do usuário
            idUsuario = jdbcTemplate.queryForObject("SELECT idUsuario FROM usuario WHERE email = ?", Integer.class, email);
            nomeUsuario = jdbcTemplate.queryForObject("SELECT nomeUsuario FROM usuario WHERE email = ?", String.class, email);

            Boolean existeComputador = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario) > 0;
            if (!existeComputador) {
                // Cadastrar computador no banco de dados
                jdbcTemplate.update("INSERT INTO computador (fkUsuario, nome) VALUES (?, ?)", idUsuario, inetAddress.getHostName());
                idComputador = jdbcTemplate.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario);
            } else {
                // Obter ID do computador
                idComputador = jdbcTemplate.queryForObject("SELECT idComputador FROM computador WHERE fkUsuario = ?", Integer.class, idUsuario);
            }

            System.out.println("Bem vindo, " + nomeUsuario + "!");
        } else {
            System.out.println("Senha incorreta!");
        }

        // Obter componentes cadastrados no banco de dados
        List<Componente> componentesCadastrados = jdbcTemplate.query("SELECT * FROM componente", (rs, rowNum) -> {
            Integer idComponente = rs.getInt("idComponente");
            String tipo = rs.getString("tipo");
            String nome = rs.getString("nome");
            List<Especificacao> especificacoes = jdbcTemplate.query("SELECT * FROM especificacoesComponente WHERE fkComponente = ?", (rs2, rowNum2) -> {
                Integer idEspecificacaoComp = rs2.getInt("idEspecificacaoComp");
                String tipoEspecificacao = rs2.getString("tipoEspecificacao");
                String valor = rs2.getString("valor");

                return new Especificacao(idEspecificacaoComp, tipoEspecificacao, valor);
            }, idComponente);

            return new Componente(idComponente, tipo, nome, especificacoes);
        });

        // Obter informações do sistema
        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
        List<Disco> discos = grupoDeDiscos.getDiscos();
        Memoria memoria = looca.getMemoria();
        Processador processador = looca.getProcessador();

        // Calcular e formatar informações da memória
        Long memoriaTotal = memoria.getTotal() / (1024 * 1024 * 1024);
        String memoriaNome = "Memoria de " + memoriaTotal.toString() + " GB";

        // Calcular e formatar informações do processador
        Long processadorFrequencia = processador.getFrequencia() / (1000 * 1000 * 1000);
        String processadorNome = processador.getNome();
        Integer processadorNucleosFisicos = processador.getNumeroCpusFisicas();
        Integer processadorNucleosLogicos = processador.getNumeroCpusLogicas();

        // Exibir componentes cadastrados
        System.out.println("Componentes cadastrados: " + componentesCadastrados);

        // Verificar se a memória está cadastrada no banco de dados
        Boolean existeMemoria = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'Memoria' AND nome = ?", Integer.class, memoriaNome) > 0;

        if (!existeMemoria) {
            // Cadastrar memória no banco de dados
            jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "Memoria", memoriaNome);
            Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, memoriaNome);

            // Relacionar memória ao computador
            jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            componentesCadastrados.add(new Componente(idComponente, "Memória", memoriaNome, List.of(
                    new Especificacao(idComponente, "Memória Total", memoriaTotal.toString())
            )));

            // Inserir especificações da memória
            for (Componente componenteCadastrado : componentesCadastrados) {
                for (Especificacao especificacao : componenteCadastrado.getEspecificacoes()) {
                    if (especificacao.getFkComponente().equals(idComponente)) {
                        jdbcTemplate.update("INSERT INTO especificacoesComponente (fkComponente, tipoEspecificacao, valor) VALUES (?, ?, ?)", idComponente, especificacao.getTipo(), especificacao.getValor());
                    }
                }
            }
        } else {
            // Verificar se a relação entre computador e memória existe
            boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, memoriaNome)) > 0;
            if (!compHasCompExiste) {
                // Se não existir, criar a relação
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, memoriaNome);
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            }
        }

        // Verificar se o processador está cadastrado no banco de dados
        Boolean existeProcessador = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'Processador' AND nome = ?", Integer.class, processadorNome) > 0;

        if (!existeProcessador) {
            // Cadastrar processador no banco de dados
            jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "Processador", processadorNome);
            Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, processadorNome);

            // Relacionar processador ao computador
            jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            componentesCadastrados.add(new Componente(idComponente, "Processador", processadorNome, List.of(
                    new Especificacao(idComponente, "Frequência", processadorFrequencia.toString()),
                    new Especificacao(idComponente, "Núcleos Físicos", processadorNucleosFisicos.toString()),
                    new Especificacao(idComponente, "Núcleos Lógicos", processadorNucleosLogicos.toString())
            )));

            // Inserir especificações do processador
            for (Componente componenteCadastrado : componentesCadastrados) {
                for (Especificacao especificacao : componenteCadastrado.getEspecificacoes()) {
                    if (especificacao.getFkComponente().equals(idComponente)) {
                        jdbcTemplate.update("INSERT INTO especificacoesComponente (fkComponente, tipoEspecificacao, valor) VALUES (?, ?, ?)", idComponente, especificacao.getTipo(), especificacao.getValor());
                    }
                }
            }
        } else {
            // Verificar se a relação entre computador e processador existe
            boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, processadorNome)) > 0;
            if (!compHasCompExiste) {
                // Se não existir, criar a relação
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, processadorNome);
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            }
        }

        // Iterar sobre os discos e verificar se estão cadastrados no banco de dados
        for (Disco disco : discos) {
            Long discoTamanho = disco.getTamanho() / (1024 * 1024 * 1024);
            String discoModelo = disco.getModelo();

            // Verificar se o disco está cadastrado no banco de dados
            Boolean existeDisco = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'Disco' AND nome = ?", Integer.class, discoModelo) > 0;

            if (!existeDisco) {
                // Cadastrar disco no banco de dados
                jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "Disco", discoModelo);
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, discoModelo);

                // Relacionar disco ao computador
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                componentesCadastrados.add(new Componente(idComponente, "Disco", discoModelo, List.of(
                        new Especificacao(idComponente, "Tamanho", discoTamanho.toString())
                )));

                // Inserir especificações do disco
                for (Componente componenteCadastrado : componentesCadastrados) {
                    for (Especificacao especificacao : componenteCadastrado.getEspecificacoes()) {
                        if (especificacao.getFkComponente().equals(idComponente)) {
                            jdbcTemplate.update("INSERT INTO especificacoesComponente (fkComponente, tipoEspecificacao, valor) VALUES (?, ?, ?)", idComponente, especificacao.getTipo(), especificacao.getValor());
                        }
                    }
                }
            } else {
                // Verificar se a relação entre computador e disco existe
                boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, discoModelo)) > 0;
                if (!compHasCompExiste) {
                    // Se não existir, criar a relação
                    Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, discoModelo);
                    jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                }
            }
        }

        while (true) {
            // Lista para armazenar os registros a serem inseridos no banco de dados
            List<Registro> registros = new ArrayList<>();

            // Iterar sobre os discos para obter informações de leitura e escrita
            for (Disco disco : discos) {
                Long velocidadeDeLeitura = disco.getBytesDeLeitura() / 1024;
                Long velocidadeDeEscrita = disco.getBytesDeEscritas() / 1024;

                // Obter IDs relacionados ao disco no banco de dados
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, disco.getModelo());
                Integer idCompHasComp = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponente);

                // Adicionar registros de velocidade de leitura e escrita à lista
                registros.add(new Registro(idCompHasComp, "Velocidade de Leitura", velocidadeDeLeitura.doubleValue()));
                registros.add(new Registro(idCompHasComp, "Velocidade de Escrita", velocidadeDeEscrita.doubleValue()));
            }

            // Obter IDs relacionados à memória no banco de dados
            Integer idComponenteMemoria = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, memoriaNome);
            Integer idCompHasCompMemoria = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponenteMemoria);

            // Calcular e adicionar registros de memória disponível e em uso à lista
            Long memoriaDisponivel = memoria.getDisponivel() / (1024 * 1024 * 1024);
            Long memoriaEmUso = memoria.getEmUso() / (1024 * 1024 * 1024);
            registros.add(new Registro(idCompHasCompMemoria, "Memória Disponível", memoriaDisponivel.doubleValue()));
            registros.add(new Registro(idCompHasCompMemoria, "Memória em Uso", memoriaEmUso.doubleValue()));

            // Obter IDs relacionados ao processador no banco de dados
            Integer idComponenteProcessador = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, processadorNome);
            Integer idCompHasCompProcessador = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponenteProcessador);

            // Obter e adicionar registros de uso da CPU e temperatura à lista
            Double usoCpu = processador.getUso();
            registros.add(new Registro(idCompHasCompProcessador, "Uso da CPU", usoCpu));

            // Iterar sobre os registros e inserir no banco de dados
            for (Registro registro : registros) {
                jdbcTemplate.update("INSERT INTO registro (fkCompHasComp, tipo, dadoValor, dataHora) VALUES (?, ?, ?, NOW())", registro.getFkCompHasComp(), registro.getTipo(), registro.getValor());
            }

                // Limpar a lista de registros e aguardar por 5 segundos antes da próxima iteração
                registros.clear();
                Thread.sleep(50);

        }
    }
}
