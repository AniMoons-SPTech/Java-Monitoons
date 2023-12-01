import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import org.springframework.jdbc.core.JdbcTemplate;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonitoramentoSQLServer {
    public void comecarMonitoramentoSQLServer(Usuario usuario) throws InterruptedException, IOException{

        // Inicialização dos objetos necessários

        Looca looca = new Looca();
        ConexaoSQLServer conexao = new ConexaoSQLServer();
        JdbcTemplate jdbcTemplate = conexao.getConexaoDoBanco();
        InetAddress inetAddress = null;
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();


        try {
            // Obtém o endereço IP da máquina local
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Integer idUsuario = usuario.getIdUsuarioMySQL();
        String nomeUsuario = usuario.getNome();
        Integer idComputador = usuario.getIdComputadorSQLServer();

        // Obter componentes cadastrados no banco de dados
        List<Componente> componentesCadastrados = jdbcTemplate.query("SELECT * FROM componente", (rs, indice) -> {
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
        Map<Disco, Volume> discoVolumeMap = Utilitarios.relacionarDiscosComVolumes();
        Memoria memoria = looca.getMemoria();
        Processador processador = looca.getProcessador();
        List<GraphicsCard> gpus = hardware.getGraphicsCards();
        List<RedeInterface> redes = looca.getRede().getGrupoDeInterfaces().getInterfaces();



        // Calcular e formatar informações da memória
        Long memoriaTotal = memoria.getTotal();
        String memoriaNome = "Memoria de " + Utilitarios.formatBytes(memoriaTotal);

        // Calcular e formatar informações do processador
        Long processadorFrequencia = processador.getFrequencia();
        String processadorNome = processador.getNome();
        Integer processadorNucleosFisicos = processador.getNumeroCpusFisicas();
        Integer processadorNucleosLogicos = processador.getNumeroCpusLogicas();

        // Verificar se a memória está cadastrada no banco de dados
        Boolean existeMemoria = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'RAM' AND nome = ?", Integer.class, memoriaNome) > 0;

        if (!existeMemoria) {
            // Cadastrar memória no banco de dados
            jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "RAM", memoriaNome);
            Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'RAM'", Integer.class, memoriaNome);

            // Relacionar memória ao computador
            jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            componentesCadastrados.add(new Componente(idComponente, "RAM", memoriaNome, List.of(
                    new Especificacao(idComponente, "Memória Total", Utilitarios.formatBytes(memoriaTotal))
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
            Boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'RAM'", Integer.class, memoriaNome)) > 0;
            if (!compHasCompExiste) {
                // Se não existir, criar a relação
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'RAM'", Integer.class, memoriaNome);
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            }
        }

        // Verificar se o processador está cadastrado no banco de dados
        Boolean existeProcessador = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'CPU' AND nome = ?", Integer.class, processadorNome) > 0;

        if (!existeProcessador) {
            // Cadastrar processador no banco de dados
            jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "CPU", processadorNome);
            Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'CPU'", Integer.class, processadorNome);

            // Relacionar processador ao computador
            jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            componentesCadastrados.add(new Componente(idComponente, "CPU", processadorNome, List.of(
                    new Especificacao(idComponente, "Frequência", Utilitarios.formatFrequency(processadorFrequencia)),
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
            Boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'CPU'", Integer.class, processadorNome)) > 0;
            if (!compHasCompExiste) {
                // Se não existir, criar a relação
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'CPU'", Integer.class, processadorNome);
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
            }
        }

        // Iterar sobre os discos e verificar se estão cadastrados no banco de dados
        looca.getGrupoDeDiscos().getVolumes().get(0).getTotal();
        for (Map.Entry<Disco, Volume> entrada : discoVolumeMap.entrySet()) {
            Long discoTamanho = entrada.getKey().getTamanho();
            String discoModelo = entrada.getKey().getModelo().replace(" (Unidades de disco padrão)", "");

            // Verificar se o disco está cadastrado no banco de dados
            Boolean existeDisco = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'DISCO' AND nome = ?", Integer.class, discoModelo) > 0;

            if (!existeDisco) {
                // Cadastrar disco no banco de dados
                jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "DISCO", discoModelo);
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'DISCO'", Integer.class, discoModelo);

                // Relacionar disco ao computador
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                componentesCadastrados.add(new Componente(idComponente, "DISCO", discoModelo, List.of(
                        new Especificacao(idComponente, "Tamanho", Utilitarios.formatBytes(discoTamanho)))
                ));

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
                boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'DISCO'", Integer.class, discoModelo)) > 0;
                if (!compHasCompExiste) {
                    // Se não existir, criar a relação
                    Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, discoModelo);
                    jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                }
            }
        }

        // Iterar sobre as placas de vídeo e verificar se estão cadastradas no banco de dados
        try {

            Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=utilization.gpu,memory.used,memory.free --format=csv,noheader,nounits");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            for (GraphicsCard gpu : gpus) {
                String gpuNome = gpu.getName();
                String gpuFabricante = gpu.getVendor();
                String gpuVersao = gpu.getVersionInfo();
                Long gpuVRAM = gpu.getVRam();
                Long gpuMemoria = null;
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] memoryInfo = line.trim().split(",");
                    Double gpuMemoriaUso = Double.parseDouble(memoryInfo[1].trim());
                    Double gpuMemoriaDisponivel = Double.parseDouble(memoryInfo[2].trim());
                    if (gpuMemoriaDisponivel != null && gpuMemoriaUso != null) {
                        gpuMemoria = (long) (gpuMemoriaUso + gpuMemoriaDisponivel);
                    } else {
                        gpuMemoria = 0L;
                    }
                }

                // Verificar se a placa de vídeo está cadastrada no banco de dados
                Boolean existePlacaDeVideo = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'GPU' AND nome = ?", Integer.class, gpuNome) > 0;

                if (!existePlacaDeVideo) {
                    // Cadastrar placa de vídeo no banco de dados
                    jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "GPU", gpuNome);
                    Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'GPU'", Integer.class, gpuNome);

                    // Relacionar placa de vídeo ao computador
                    jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                    componentesCadastrados.add(new Componente(idComponente, "GPU", gpuNome, List.of(
                            new Especificacao(idComponente, "Fabricante", gpuFabricante),
                            new Especificacao(idComponente, "Versão", gpuVersao),
                            new Especificacao(idComponente, "VRAM", Utilitarios.formatBytes(gpuVRAM)),
                            new Especificacao(idComponente, "Memória", Utilitarios.formatBytes(gpuMemoria))
                    )));

                    // Inserir especificações da placa de vídeo
                    for (Componente componenteCadastrado : componentesCadastrados) {
                        for (Especificacao especificacao : componenteCadastrado.getEspecificacoes()) {
                            if (especificacao.getFkComponente().equals(idComponente)) {
                                jdbcTemplate.update("INSERT INTO especificacoesComponente (fkComponente, tipoEspecificacao, valor) VALUES (?, ?, ?)", idComponente, especificacao.getTipo(), especificacao.getValor());
                            }
                        }
                    }
                } else {
                    // Verificar se a relação entre computador e placa de vídeo existe
                    Boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'GPU'", Integer.class, gpuNome)) > 0;
                    if (!compHasCompExiste) {
                        // Se não existir, criar a relação
                        Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, gpuNome);
                        jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Não foi possível obter informações da placa de vídeo");
        }

        // Iterar sobre as interfaces de rede e verificar se estão cadastradas no banco de dados
        for (RedeInterface rede : redes) {
            String redeNome = rede.getNomeExibicao();
            String redeMac = rede.getEnderecoMac();
            List<String> redeIpv4 = rede.getEnderecoIpv4();
            List<String> redeIpv6 = rede.getEnderecoIpv6();

            // Verificar se a interface de rede está cadastrada no banco de dados
            Boolean existeInterfaceDeRede = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM componente WHERE tipo = 'REDE' AND nome = ?", Integer.class, redeNome) > 0;

            if (!existeInterfaceDeRede) {
                // Cadastrar interface de rede no banco de dados
                jdbcTemplate.update("INSERT INTO componente (tipo, nome) VALUES (?, ?)", "REDE", redeNome);
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'REDE'", Integer.class, redeNome);

                List<Especificacao> especificacoes = new ArrayList<>();

                especificacoes.add(new Especificacao(idComponente, "MAC", redeMac));
                if (!redeIpv4.isEmpty()) {
                    especificacoes.add(new Especificacao(idComponente, "IPv4", redeIpv4.get(0)));
                }
                if (!redeIpv6.isEmpty()) {
                    for (String ipv6 : redeIpv6) {
                        especificacoes.add(new Especificacao(idComponente, Utilitarios.checkIPv6Type(ipv6), ipv6));
                    }
                }


                // Relacionar interface de rede ao computador
                jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                componentesCadastrados.add(new Componente(idComponente, "REDE", redeNome, especificacoes));

                // Inserir especificações da interface de rede
                for (Componente componenteCadastrado : componentesCadastrados) {
                    for (Especificacao especificacao : componenteCadastrado.getEspecificacoes()) {
                        if (especificacao.getFkComponente().equals(idComponente)) {
                            jdbcTemplate.update("INSERT INTO especificacoesComponente (fkComponente, tipoEspecificacao, valor) VALUES (?, ?, ?)", idComponente, especificacao.getTipo(), especificacao.getValor());
                        }
                    }
                }
            } else {
                // Verificar se a relação entre computador e interface de rede existe
                Boolean compHasCompExiste = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ?", Integer.class, idComputador, jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'REDE'", Integer.class, redeNome)) > 0;
                if (!compHasCompExiste) {
                    // Se não existir, criar a relação
                    Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, redeNome);
                    jdbcTemplate.update("INSERT INTO computadorhascomponente (fkComputador, fkComponente) VALUES (?, ?)", idComputador, idComponente);
                }
            }
        }

            // Lista para armazenar os registros a serem inseridos no banco de dados
            List<Registro> registros = new ArrayList<>();



            // Iterar sobre as interfaces de rede para obter informações de uso
            for (RedeInterface rede : redes) {
                String redeNome = rede.getNomeExibicao();
                Long redeBytesRecebidos = rede.getBytesRecebidos();
                Long redeBytesEnviados = rede.getBytesRecebidos();
                Long tempoAntes = System.currentTimeMillis();


                // Obter IDs relacionados à interface de rede no banco de dados
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'REDE'", Integer.class, redeNome);
                Integer idCompHasComp = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponente);

                // Aguardar um segundo antes de calcular a taxa de transferência
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Long bytesRecebidosAgora = rede.getBytesRecebidos();
                Long bytesEnviadosAgora = rede.getBytesEnviados();
                Long tempoAgora = System.currentTimeMillis();

                Long bytesRecebidos = bytesRecebidosAgora - redeBytesRecebidos;
                Long bytesEnviados = bytesEnviadosAgora - redeBytesEnviados;
                Long tempoDecorrido = tempoAgora - tempoAntes;

                Long taxaRecebida = bytesRecebidos / tempoDecorrido * 1000; // taxa em bytes por segundo
                Long taxaEnviada = bytesEnviados / tempoDecorrido * 1000; // taxa em bytes por segundo

                registros.add(new Registro(idCompHasComp, "Taxa de Transferência Recebida", Utilitarios.formatBytesToDouble(taxaRecebida), Utilitarios.formatBytesPerSecond(taxaRecebida), Utilitarios.getUnidadeBytesPerSecond(taxaRecebida)));
                registros.add(new Registro(idCompHasComp, "Taxa de Transferência Enviada", Utilitarios.formatBytesToDouble(taxaEnviada), Utilitarios.formatBytesPerSecond(taxaEnviada), Utilitarios.getUnidadeBytesPerSecond(taxaEnviada)));
            }

            // Iterar sobre os discos para obter informações de leitura e escrita
            for (Map.Entry<Disco, Volume> entrada : discoVolumeMap.entrySet()) {
                Long velocidadeDeLeitura = entrada.getKey().getBytesDeLeitura();
                Long velocidadeDeEscrita = entrada.getKey().getBytesDeEscritas();
                Long espacoDisponivel = entrada.getValue().getDisponivel();
                Long espacoEmUso = entrada.getValue().getTotal() - entrada.getValue().getDisponivel();


                // Obter IDs relacionados ao disco no banco de dados
                Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, entrada.getKey().getModelo().replace(" (Unidades de disco padrão)", ""));
                Integer idCompHasComp = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponente);

                // Adicionar registros de velocidade de leitura e escrita à lista
                registros.add(new Registro(idCompHasComp, "Velocidade de Leitura", Utilitarios.formatBytesToDouble(velocidadeDeLeitura / (1024 * 1204)), Utilitarios.formatBytesPerSecond(velocidadeDeLeitura / (1024 * 1204)), Utilitarios.getUnidadeBytesPerSecond(velocidadeDeLeitura / (1024 * 1204))));
                registros.add(new Registro(idCompHasComp, "Velocidade de Escrita", Utilitarios.formatBytesToDouble(velocidadeDeEscrita / (1024 * 1204)), Utilitarios.formatBytesPerSecond(velocidadeDeEscrita / (1024 * 1204)), Utilitarios.getUnidadeBytesPerSecond(velocidadeDeEscrita / (1024 * 1204))));
                registros.add(new Registro(idCompHasComp, "Espaço Disponível", Utilitarios.formatBytesToDouble(espacoDisponivel), Utilitarios.formatBytes(espacoDisponivel), Utilitarios.getUnidadeBytes(espacoDisponivel)));
                registros.add(new Registro(idCompHasComp, "Espaço em Uso", Utilitarios.formatBytesToDouble(espacoEmUso), Utilitarios.formatBytes(espacoEmUso), Utilitarios.getUnidadeBytes(espacoEmUso)));


                // Obter índices dos registros armazenamento
                Integer indexEspacoDisp = 0;

                for (Registro registro : registros) {
                    if (registro.getTipo().equals("Espaço Disponível")) {
                        indexEspacoDisp = registros.indexOf(registro);
                    }
                }

                Double porcentagemEspacoDisp = Utilitarios.calcPercent(Utilitarios.formatBytesToDouble(espacoDisponivel), Utilitarios.formatBytesToDouble(entrada.getKey().getTamanho()));


                // Adicionar alertas de armazenamento
                if (porcentagemEspacoDisp < 10) {
                    registros.get(indexEspacoDisp).addAlerta(new Alerta("CRITICO", "DISCO"));
                } else if (porcentagemEspacoDisp < 20) {
                    registros.get(indexEspacoDisp).addAlerta(new Alerta("INTERMEDIARIO", "DISCO"));
                } else if (porcentagemEspacoDisp < 30) {
                    registros.get(indexEspacoDisp).addAlerta(new Alerta("MODERADO", "DISCO"));
                }

            }

            //Iterar sobre as placas de vídeo para obter informações de uso da GPU
            try {
                Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=utilization.gpu,memory.used,memory.free --format=csv,noheader,nounits");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                for (GraphicsCard gpu : gpus) {

                    String gpuNome = gpu.getName();
                    String line;
                    Long gpuMemUso = null;
                    Long gpuMemDisp = null;
                    Double videoPorcetUso = null;

                    while ((line = reader.readLine()) != null) {
                        String[] memoryInfo = line.trim().split(",");
                        videoPorcetUso = Double.parseDouble(memoryInfo[0].trim());
                        gpuMemUso = Long.parseLong(memoryInfo[1].trim());
                        gpuMemDisp = Long.parseLong(memoryInfo[2].trim());
                    }

                    // Obter IDs relacionados à placa de vídeo no banco de dados
                    Integer idComponente = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ?", Integer.class, gpuNome);
                    Integer idCompHasComp = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponente);

                    // Adicionar registro de uso da GPU à lista

                    if (videoPorcetUso != null && gpuMemUso != null && gpuMemDisp != null) {
                        registros.add(new Registro(idCompHasComp, "Uso da GPU", Utilitarios.formatPercentagetoDouble(videoPorcetUso), videoPorcetUso.toString(), "%"));
                        registros.add(new Registro(idCompHasComp, "Memória de Vídeo em Uso", Utilitarios.formatBytesToDouble(gpuMemUso), Utilitarios.formatBytes(gpuMemUso), Utilitarios.getUnidadeBytes(gpuMemUso)));
                        registros.add(new Registro(idCompHasComp, "Memória de Vídeo Disponível", Utilitarios.formatBytesToDouble(gpuMemDisp), Utilitarios.formatBytes(gpuMemDisp), Utilitarios.getUnidadeBytes(gpuMemDisp)));
                    }
                    // Obter índices dos registros de uso da GPU e de memória de vídeo disponível
                    Integer indexVideoUso = 0;
                    Integer indexVideoMemDisp = 0;

                    for (Registro registro : registros) {
                        if (registro.getTipo().equals("Uso da GPU")) {
                            indexVideoUso = registros.indexOf(registro);
                        }
                        if (registro.getTipo().equals("Memória de Vídeo Disponível")) {
                            indexVideoMemDisp = registros.indexOf(registro);
                        }
                    }

                    // Adicionar alertas de uso da GPU à lista
                    if (videoPorcetUso != null) {
                        if (videoPorcetUso > 90) {
                            registros.get(indexVideoUso).addAlerta(new Alerta("CRITICO", "GPU"));
                        } else if (videoPorcetUso > 80) {
                            registros.get(indexVideoUso).addAlerta(new Alerta("INTERMEDIARIO", "GPU"));
                        } else if (videoPorcetUso > 70) {
                            registros.get(indexVideoUso).addAlerta(new Alerta("MODERADO", "GPU"));
                        }
                    }

                    if (gpuMemDisp != null) {
                        if (gpuMemDisp < 1000) {
                            registros.get(indexVideoMemDisp).addAlerta(new Alerta("CRITICO", "GPU"));
                        } else if (gpuMemDisp < 2000) {
                            registros.get(indexVideoMemDisp).addAlerta(new Alerta("INTERMEDIARIO", "GPU"));
                        } else if (gpuMemDisp < 3000) {
                            registros.get(indexVideoMemDisp).addAlerta(new Alerta("MODERADO", "GPU"));
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Obter IDs relacionados à memória no banco de dados
            Integer idComponenteMemoria = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'RAM'", Integer.class, memoriaNome);
            Integer idCompHasCompMemoria = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponenteMemoria);

            // Calcular e adicionar registros de memória disponível e em uso à lista
            Long memoriaDisponivel = memoria.getDisponivel();
            Long memoriaEmUso = memoria.getEmUso();
            registros.add(new Registro(idCompHasCompMemoria, "Memória Disponível", Utilitarios.formatBytesToDouble(memoriaDisponivel), Utilitarios.formatBytes(memoriaDisponivel), Utilitarios.getUnidadeBytes(memoriaDisponivel)));
            registros.add(new Registro(idCompHasCompMemoria, "Memória em Uso", Utilitarios.formatBytesToDouble(memoriaEmUso), Utilitarios.formatBytes(memoriaEmUso), Utilitarios.getUnidadeBytes(memoriaEmUso)));

            // Obter índice do registro de memória disponível
            Integer indexMemDisp = 0;

            for (Registro registro : registros) {
                if (registro.getTipo().equals("Memória Disponível")) {
                    indexMemDisp = registros.indexOf(registro);
                }
            }

            // Adicionar alertas de memória disponível e em uso à lista
            if (memoriaDisponivel < 1) {
                registros.get(indexMemDisp).addAlerta(new Alerta("CRITICO", "RAM"));
            } else if (memoriaDisponivel < 2) {
                registros.get(indexMemDisp).addAlerta(new Alerta("INTERMEDIARIO", "RAM"));
            } else if (memoriaDisponivel < 3) {
                registros.get(indexMemDisp).addAlerta(new Alerta("MODERADO", "RAM"));
            }

            // Obter IDs relacionados ao processador no banco de dados
            Integer idComponenteProcessador = jdbcTemplate.queryForObject("SELECT idComponente FROM componente WHERE nome = ? AND tipo = 'CPU'", Integer.class, processadorNome);
            Integer idCompHasCompProcessador = jdbcTemplate.queryForObject("SELECT idCompHasComp FROM computadorhascomponente WHERE fkComputador = ? AND fkComponente = ? ", Integer.class, idComputador, idComponenteProcessador);

            // Obter e adicionar registros de uso da CPU e temperatura à lista
            Double usoCpu = processador.getUso();
            registros.add(new Registro(idCompHasCompProcessador, "Uso da CPU", Utilitarios.formatPercentagetoDouble(usoCpu), Utilitarios.formatPercentage(usoCpu), "%"));

            // Obter índice do registro de uso da CPU
            Integer indexUsoCpu = 0;

            for (Registro registro : registros) {
                if (registro.getTipo().equals("Uso da CPU")) {
                    indexUsoCpu = registros.indexOf(registro);
                }
            }

            // Adicionar alertas de uso da CPU à lista
            if (usoCpu != null) {
                if (usoCpu > 90) {
                    registros.get(indexUsoCpu).addAlerta(new Alerta("CRITICO", "CPU"));
                } else if (usoCpu > 80) {
                    registros.get(indexUsoCpu).addAlerta(new Alerta("INTERMEDIARIO", "CPU"));
                } else if (usoCpu > 70) {
                    registros.get(indexUsoCpu).addAlerta(new Alerta("MODERADO", "CPU"));
                }
            }

            // Iterar sobre os registros e alertas e inserir no banco de dados
            for (Registro registro : registros) {
                if (registro.getValor() != null) {
                    Integer idRegistro = conexao.inserirERetornarIdGerado("INSERT INTO registro (fkCompHasComp, tipo, dadoValor, dadoFormatado, dadoUnidade, dataHora) VALUES (?, ?, ?, ?, ?, GETDATE())", registro.getFkCompHasComp(), registro.getTipo(), registro.getValor(), registro.getValorFormatado(), registro.getUnidade());
                    if (registro.getAlerta() != null) {
                        conexao.inserirERetornarIdGerado("INSERT INTO alerta (fkRegistro, grauAlerta, tipoComponente, dataHora) VALUES (?, ?, ?, GETDATE())", idRegistro, registro.getAlerta().getGrauAlerta(), registro.getAlerta().getTipoComponente());
                    }
                }
            }

            System.out.println("Registros inseridos com sucesso!");
            try {
                // Limpar a lista de registros e aguardar por 5 segundos antes da próxima iteração
                registros.clear();
                Thread.sleep(14000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
