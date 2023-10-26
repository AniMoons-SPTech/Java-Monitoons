import java.time.LocalDateTime;

public class Registro {
    LocalDateTime dthora = LocalDateTime.now();
    private Integer idRegistro;

    private Integer fkComponente;
    private Double uso;
    private Double velCpu;
    private Double velGrav;
    private Double velLeit;
    private Double memUso;
    private Double memDisp;
    private LocalDateTime dataHora;

    private Double temperatura;




    public Registro(Componente componente, Double uso, Double velGrav, Double velLeit, Double frequencia, Double memUso, Double memDisp, Double temp) {
        this.idRegistro = null;
        this.fkComponente = componente.getId_componente();
        this.uso = uso;
        this.velGrav = velGrav;
        this.velLeit = velLeit;
        this.velCpu = frequencia;
        this.memUso = memUso;
        this.memDisp = memDisp;
        this.dataHora = dthora;
        this.temperatura = temp;
    }

    public LocalDateTime getDthora() {
        return dthora;
    }

    public void setDthora(LocalDateTime dthora) {
        this.dthora = dthora;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
    }

    public Double getUso() {
        return uso;
    }

    public void setUso(Double uso) {
        this.uso = uso;
    }

    public Double getVelCpu() {
        return velCpu;
    }

    public void setVelCpu(Double velCpu) {
        this.velCpu = velCpu;
    }

    public Double getVelGrav() {
        return velGrav;
    }

    public void setVelGrav(Double velGrav) {
        this.velGrav = velGrav;
    }

    public Double getVelLeit() {
        return velLeit;
    }

    public void setVelLeit(Double velLeit) {
        this.velLeit = velLeit;
    }

    public Double getMemUso() {
        return memUso;
    }

    public void setMemUso(Double memUso) {
        this.memUso = memUso;
    }

    public Double getMemDisp() {
        return memDisp;
    }

    public void setMemDisp(Double memDisp) {
        this.memDisp = memDisp;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Registro{" +
                ", idRegistro=" + idRegistro +
                ", fkComponente=" + fkComponente +
                ", uso=" + uso +
                ", velCpu=" + velCpu +
                ", velGrav=" + velGrav +
                ", velLeit=" + velLeit +
                ", memUso=" + memUso +
                ", memDisp=" + memDisp +
                ", dataHora=" + dataHora +
                '}';
    }
}
