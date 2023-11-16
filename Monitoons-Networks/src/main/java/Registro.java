import java.time.LocalDateTime;

public class Registro {
    private Integer fkCompHasComp;
    private String tipo;
    private Double valor;
    private LocalDateTime dataHora;

    public Registro(Integer fkCompHasComp, String tipo, Double valor) {
        this.fkCompHasComp = fkCompHasComp;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Integer getFkCompHasComp() {
        return fkCompHasComp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString(){
        return """
                fkCompHasComp=%d,
                tipo=%s,
                valor=%f,
                dataHora=%s
                """.formatted(
                this.fkCompHasComp,
                this.tipo,
                this.valor,
                this.dataHora
        );
    }
}
