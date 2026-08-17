package forense_json;

public class Personagem {

    private Integer id;
    private String nome;
    private String especie;
    private String comidaFavorita;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecie() {
        return especie;
    }

    public String getComidaFavorita() {
        return comidaFavorita;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setComidaFavorita(String comidaFavorita) {
        this.comidaFavorita = comidaFavorita;
    }

    public Personagem() {
    }

    public Personagem(String nome, String especie, String comidaFavorita) {
        this.nome = nome;
        this.especie = especie;
        this.comidaFavorita = comidaFavorita;
    }

    public Personagem(int id, String nome, String especie, String comidaFavorita) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.comidaFavorita = comidaFavorita;
    }
}