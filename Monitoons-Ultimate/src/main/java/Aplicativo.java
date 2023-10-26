import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.janelas.JanelaGrupo;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.processos.ProcessoGrupo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aplicativo {
    private Conexao conexao;
    private JdbcTemplate con;
    private Looca looca;
    private Processo processo;
    private Janela janela;

    public Aplicativo() {
        this.conexao = new Conexao();
        this.con = this.conexao.getConexaoDoBanco();
        this.looca = new Looca();
    }

    public void capturarAplicativos() {
        Looca looca = new Looca();
        List<Janela> listaJanelas = new ArrayList();
        List<Processo> listaProcessos = new ArrayList();

        DecimalFormat df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(java.util.Locale.US));

        ProcessoGrupo processoGrupo = looca.getGrupoDeProcessos();
        JanelaGrupo janelaGrupo = looca.getGrupoDeJanelas();
        listaJanelas = janelaGrupo.getJanelas();
        listaProcessos = processoGrupo.getProcessos();
        Aplicativo2 aplicativo2;
        int PIDProcesso;
        int PIDJanela;
        long longPIDJanela;

        for (int i = 0; i < listaProcessos.size(); i++) {
            PIDProcesso = listaProcessos.get(i).getPid();
            for (int j = 0; j < listaJanelas.size(); j++) {
                longPIDJanela = listaJanelas.get(j).getPid();
                PIDJanela = (int) longPIDJanela;
                if (PIDJanela == PIDProcesso) {
                    this.processo = listaProcessos.get(i);
                    this.janela = listaJanelas.get(j);
                    aplicativo2 = new Aplicativo2();
                    aplicativo2.setPID(PIDJanela);
                    if ((janela.getTitulo() != "")) {
                        con.execute("USE animoons");
                        con.update("INSERT INTO aplicativosabertos (pid, nomeAplicativo, comando, usoCPU, usoDisco, dtHora) VALUES (%d, '%s', '%s', %s, %s, now() )".formatted(aplicativo2.getPID(), janela.getTitulo(), janela.getComando(), df.format(processo.getUsoCpu()), df.format(processo.getUsoMemoria())));
                    }
                }
            }
        }
    }

    public void validacao() {
        List<Map<String, Object>> resultado = con.queryForList("SELECT nomeAplicativo FROM aplicativosabertos");
        List<Map<String, Object>> proibidos = con.queryForList("SELECT nomeAplicativoProibido FROM aplicativosproibidos");

        for (int i = 0; i < resultado.size(); i++) {
            Map nomeAplicativo = resultado.get(i);
            String nome = String.valueOf(nomeAplicativo).toLowerCase().replace("{nomeaplicativo=", "").replace("}", "");
            boolean encontrado = false;

            for (int j = 0; j < proibidos.size(); j++) {
                Map nomeAplicativoProibido = proibidos.get(j);
                String nomeApp = String.valueOf(nomeAplicativoProibido).toLowerCase().replace("{nomeaplicativoproibido=", "").replace("}", "");
                nomeApp.replace("}", "");
                if (nome.contains(nomeApp) == true) {
                    encontrado = true;
                    break;
                }
            }

            if (encontrado) {
                System.out.println("O aplicativo " + nome + " é proibido.");
            } else {
                System.out.println("O aplicativo " + nome + " é permitido.");
            }
        }
    }
}
