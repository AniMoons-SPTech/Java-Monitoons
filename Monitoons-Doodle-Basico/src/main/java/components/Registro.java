package components;

import java.time.LocalDateTime;

public class Registro {
    private Integer fkCompHasComp;
    private String tipo;
    private Double valor;
    private String valorFormatado;
    private String unidade;
    private LocalDateTime dataHora;

    public Registro(Integer fkCompHasComp, String tipo, Double valor, String valorFormatado, String unidade) {
        this.fkCompHasComp = fkCompHasComp;
        this.tipo = tipo;
        this.valor = valor;
        this.valorFormatado = valorFormatado;
        this.unidade = unidade;
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

    public String getValorFormatado() {
        return valorFormatado;
    }

    public void setValorFormatado(String valorFormatado) {
        this.valorFormatado = valorFormatado;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    @Override
    public String toString(){
        return """
                fkCompHasComp=%d,
                tipo= %s,
                valor= %s,
                valorFormatado =%s,
                unidade= %s,
                dataHora= %s
                """.formatted(
                this.fkCompHasComp,
                this.tipo,
                this.valor,
                this.valorFormatado,
                this.unidade,
                this.dataHora
        );
    }
}
