import java.util.ArrayList;
import java.util.List;

// Classe que representa um usuário do sistema de notícias

public class Usuario {
    private String nome;
    private List<Noticia> favoritos;
    private List<Noticia> lidas;
    private List<Noticia> paraLerDepois;

    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.lidas = new ArrayList<>();
        this.paraLerDepois = new ArrayList<>();
    }

    // Construtor padrão para permitir a criação de um usuário sem nome
    public Usuario() {
        this.favoritos = new ArrayList<>();
        this.lidas = new ArrayList<>();
        this.paraLerDepois = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Noticia> getFavoritos() {
        return favoritos;
    }

    public List<Noticia> getLidas() {
        return lidas;
    }

    public List<Noticia> getParaLerDepois() {
        return paraLerDepois;
    }

    // Métodos para definir as listas de notícias
    public void setFavoritos(List<Noticia> favoritos) {
        this.favoritos = favoritos;
    }

    public void setLidas(List<Noticia> lidas) {
        this.lidas = lidas;
    }

    public void setParaLerDepois(List<Noticia> paraLerDepois) {
        this.paraLerDepois = paraLerDepois;
    }
}