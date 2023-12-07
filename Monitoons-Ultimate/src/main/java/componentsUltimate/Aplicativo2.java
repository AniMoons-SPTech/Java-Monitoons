package componentsUltimate;

public class Aplicativo2 {
    private Integer PID;
    private String titulo;
    private String comando;
    private String usoCPU;
    private String usoMemoria;
    private Double dataHora;

    public Aplicativo2(Integer PID, String titulo, String comando, String usoCPU, String usoMemoria, Double dataHora) {
        this.PID = PID;
        this.titulo = titulo;
        this.comando = comando;
        this.usoCPU = usoCPU;
        this.usoMemoria = usoMemoria;
        this.dataHora = dataHora;
    }
    public Aplicativo2(){}

    public Integer getPID() {
        return PID;
    }

    public void setPID(Integer PID) {
        this.PID = PID;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getUsoCPU() {
        return usoCPU;
    }

    public void setUsoCPU(String usoCPU) {
        this.usoCPU = usoCPU;
    }

    public String getUsoMemoria() {
        return usoMemoria;
    }

    public void setUsoMemoria(String usoMemoria) {
        this.usoMemoria = usoMemoria;
    }


    public Double getDataHora() {
        return dataHora;
    }

    public void setDataHora(Double dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return """ 
                Aplicativo: 
                PID: %d 
                Titulo: %s
                Comando: %s
                UsoCPU: %s
                UsoMemoria: %s
                dataHora: %s
                """.formatted(PID, titulo, comando, usoCPU, usoMemoria, dataHora);
    }
}
