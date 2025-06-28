import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

// Classe responsável por persistir os dados do usuário em um arquivo JSON
public class Persistencia {
    private static final String DIRETORIO = "data";
    private static final String CAMINHO = DIRETORIO + File.separator + "usuario.json"; 

    // Configura o Gson com o TypeAdapter para LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new IBGENoticiasAPI.LocalDateTimeAdapter()) // Usa o TypeAdapter da API
            .create();

    public static void salvarUsuario(Usuario usuario) {
        try {
            File diretorio = new File(DIRETORIO);
            if (!diretorio.exists()) {
                if (diretorio.mkdirs()) {
                    System.out.println("Diretório '" + DIRETORIO + "' criado com sucesso.");
                } else {
                    System.err.println("Atenção: Não foi possível criar o diretório '" + DIRETORIO + "'. Verifique as permissões.");
                }
            }

            FileWriter writer = new FileWriter(CAMINHO);
            gson.toJson(usuario, writer);
            writer.close();
            System.out.println("Dados do usuário salvos com sucesso em: " + CAMINHO);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuário: " + e.getMessage());
            // e.printStackTrace(); // Para depuração
        }
    }

    public static Usuario carregarUsuario() {
        try {
            File file = new File(CAMINHO);
            if (!file.exists()) {
                System.out.println("Arquivo de usuário não encontrado em '" + CAMINHO + "'. Um novo usuário será criado.");
                return null;
            }
            FileReader reader = new FileReader(file);
            Type type = new TypeToken<Usuario>() {}.getType();
            Usuario usuario = gson.fromJson(reader, type);
            reader.close();
            System.out.println("Dados do usuário carregados com sucesso de: " + CAMINHO);
            return usuario;

        } catch (IOException e) {
            System.out.println("Erro de I/O ao carregar usuário: " + e.getMessage());
            // e.printStackTrace(); // Para depuração
            return null; 
        } catch (JsonSyntaxException e) {
            System.out.println("Erro de sintaxe JSON ao carregar usuário. O arquivo pode estar corrompido: " + e.getMessage());
            // e.printStackTrace(); // Para depuração
            return null; 
        } catch (Exception e) {
            System.out.println("Erro inesperado ao carregar usuário: " + e.getMessage());
            // e.printStackTrace(); // Para depuração
            return null;
        }
    }
}