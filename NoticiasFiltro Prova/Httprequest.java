import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// Classe utilizada para enviar requisições HTTP para a API de notícias do IBGE

public class Httprequest {

      public static void main(String[] args) throws Exception {
        
        HttpClient client = HttpClient.newHttpClient();
        URI url = new URI("https://servicodados.ibge.gov.br/api/v3/noticias/?q=");
        
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String body = response.body();
            System.out.println("Resposta da API: " + body);
        } else {
            System.out.println("Erro na requisição: " + response.statusCode());
        }
    }
}