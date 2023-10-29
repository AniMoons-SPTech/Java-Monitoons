public class Componente {

    private Integer id_componente;

    private String tipo;
    private String nome;
    private Integer numNucleo;
    private Long capacidade;
    private Double velocidade;

    public Componente( String tipo, Integer numNucleo,Long capacidade, Double velocidade,String nome) {
        this.id_componente = null;
        this.tipo = tipo;
        this.numNucleo = numNucleo;
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

    public Integer getNumNucleo() {
        return numNucleo;
    }

    public void setNumNucleo(Integer numNucleo) {
        this.numNucleo = numNucleo;
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
                ", numNucleo=" + numNucleo +
                ", capacidade=" + capacidade +
                ", velocidade=" + velocidade +
                '}';
    }
}
