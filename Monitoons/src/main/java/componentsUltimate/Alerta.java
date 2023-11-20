package componentsUltimate;

import java.time.LocalDateTime;

public class Alerta extends Registro{

    private Integer indexRegistro;
    private String grauAlerta;
    private String tipoComponente;
    private LocalDateTime dataHora;

    public Alerta(Integer fkCompHasComp, String tipo, Double valor, String valorFormatado, String unidade,Integer indexRegistro, String grauAlerta, String tipoComponente) {
        super(fkCompHasComp, tipo, valor, valorFormatado, unidade);
        this.indexRegistro = indexRegistro;
        this.grauAlerta = grauAlerta;
        this.tipoComponente = tipoComponente;
        this.dataHora = LocalDateTime.now();
    }

    public Integer getIndexRegistro() {
        return indexRegistro;
    }

    public void setIndexRegistro(Integer indexRegistro) {
        this.indexRegistro = indexRegistro;
    }

    public String getGrauAlerta() {
        return grauAlerta;
    }

    public void setGrauAlerta(String grauAlerta) {
        this.grauAlerta = grauAlerta;
    }

    public String getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(String tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    @Override
    public String toString(){
        return """
                %s
                grauAlerta=%s,
                tipoComponente=%s,
                dataHora=%s
                """.formatted(
                this.grauAlerta,
                this.tipoComponente
        );
    }
}
