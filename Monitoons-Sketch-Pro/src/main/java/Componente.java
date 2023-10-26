public class Componente {

    private Integer id_componente;

    private String tipo;
    private String nome;
    private Integer num_nucleo;
    private Long capacidade;
    private Double velocidade;

    public Componente( String tipo, Integer num_nucleo,Long capacidade, Double velocidade,String nome) {
        this.id_componente = null;
        this.tipo = tipo;
        this.num_nucleo = num_nucleo;
        this.capacidade = capacidade;
        this.velocidade = velocidade;
        this.nome = nome;
    }

    public Integer getId_componente() {
        return id_componente;
    }

    public void setId_componente(Integer id_componente) {
        this.id_componente = id_componente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNum_nucleo() {
        return num_nucleo;
    }

    public void setNum_nucleo(Integer num_nucleo) {
        this.num_nucleo = num_nucleo;
    }

    public Long getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Long capacidade) {
        this.capacidade = capacidade;
    }

    public Double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(Double velocidade) {
        this.velocidade = velocidade;
    }

    @Override
    public String toString() {
        return "Componente{" +
                "id_componente=" + id_componente +
                ", tipo='" + tipo + '\'' +
                ", nome='" + nome + '\'' +
                ", num_nucleo=" + num_nucleo +
                ", capacidade=" + capacidade +
                ", velocidade=" + velocidade +
                '}';
    }
}
