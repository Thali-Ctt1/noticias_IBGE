import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Classe que implementa a API para buscar notícias do IBGE

public class IBGENoticiasAPI {

    // DateTimeFormatter para parsear as datas da API (YYYY-MM-DD HH:MM:SS)
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // TypeAdapter para serializar/desserializar LocalDateTime com GSON
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(API_DATE_FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String dateString = in.nextString();
            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }
            // Tenta parsear. Em caso de falha, retorna null ou lança uma exceção, dependendo do comportamento desejado.
            try {
                return LocalDateTime.parse(dateString, API_DATE_FORMATTER);
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println("Erro ao parsear data JSON: " + dateString + ". " + e.getMessage());
                return null; // Retorna null se a data não puder ser parseada
            }
        }
    }

    /**
     * Busca notícias da API do IBGE com base em um mapa de parâmetros.
     * Ex: {"q": "termo de busca"}, {"de": "2023-01-01", "ate": "2023-01-31"}
     * @param params Mapa de parâmetros para a requisição.
     * @return Lista de Noticia.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação com a API.
     */
    public static List<Noticia> buscarNoticias(Map<String, String> params) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("https://servicodados.ibge.gov.br/api/v3/noticias/?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                      .append("=")
                      .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                      .append("&");
        }
        // Remove o último '&' se houver parâmetros
        if (params.size() > 0) {
            urlBuilder.setLength(urlBuilder.length() - 1);
        }

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Tratamento de erros HTTP
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = conn.getErrorStream();
            String errorResponse = null;
            if (errorStream != null) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"))) {
                    StringBuilder errorJson = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorJson.append(errorLine);
                    }
                    errorResponse = errorJson.toString();
                }
            }
            throw new IOException("Erro na requisição à API: Código " + responseCode + " - " + conn.getResponseMessage() + (errorResponse != null ? "\nDetalhes: " + errorResponse : ""));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder json = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            json.append(linha);
        }
        reader.close();

        // Configura o Gson para usar o LocalDateTimeAdapter
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        JsonObject jsonObj = JsonParser.parseString(json.toString()).getAsJsonObject();
        JsonArray items = jsonObj.getAsJsonArray("items");

        List<Noticia> lista = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();
            // Deserializa diretamente usando Gson para aproveitar o TypeAdapter
            Noticia noticia = gson.fromJson(obj, Noticia.class);
            lista.add(noticia);
        }
        return lista;
    }

    /**
     * Método auxiliar para buscar notícias por termo de busca (q).
     * @param termo O termo de busca.
     * @return Lista de Noticia.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação com a API.
     */
    public static List<Noticia> buscarNoticiasPorTermo(String termo) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("q", termo);
        return buscarNoticias(params);
    }

    /**
     * Método auxiliar para buscar notícias por um período de data.
     * @param dataInicial Data de início no formato "YYYY-MM-DD".
     * @param dataFinal Data de fim no formato "YYYY-MM-DD".
     * @return Lista de Noticia.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação com a API.
     */
    public static List<Noticia> buscarNoticiasPorData(String dataInicial, String dataFinal) throws IOException {
        Map<String, String> params = new HashMap<>();
        // A API espera o formato YYYYMMDD para 'de' e 'ate', sem hifens.
        params.put("de", dataInicial.replace("-", ""));
        params.put("ate", dataFinal.replace("-", ""));
        return buscarNoticias(params);
    }
}  