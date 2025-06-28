import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Classe que representa uma notícia do IBGE
public class Noticia {
    private String id;
    private String titulo;
    private String introducao;
    private LocalDateTime dataPublicacao; // Alterado para LocalDateTime
    private String link;
    private String tipo;

    public Noticia() {
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getIntroducao() { return introducao; }
    public LocalDateTime getDataPublicacao() { return dataPublicacao; } // Getter para LocalDateTime
    public String getLink() { return link; }
    public String getTipo() { return tipo; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setIntroducao(String introducao) { this.introducao = introducao; }
    // Setter para LocalDateTime
    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }
    // Opcional: Setter que recebe String para compatibilidade se necessário, mas o ideal é converter na API
    public void setDataPublicacao(String dataPublicacaoStr) {
        // Formato da API: "YYYY-MM-DD HH:MM:SS"
        // Criar um formatador para o formato da string da API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            this.dataPublicacao = LocalDateTime.parse(dataPublicacaoStr, formatter);
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Erro ao parsear data: " + dataPublicacaoStr + ". Usando null. " + e.getMessage());
            this.dataPublicacao = null; // Ou alguma data padrão
        }
    }

    public void setLink(String link) { this.link = link; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        // Formata a data para exibição
        String dataFormatada = (dataPublicacao != null) ? dataPublicacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "Data indisponível";
        return String.format("Título: %s\nIntrodução: %s\nData: %s\nLink: %s\nTipo: %s\nFonte: IBGE",
                titulo, introducao, dataFormatada, link, tipo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Noticia noticia = (Noticia) o;
        return Objects.equals(id, noticia.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}