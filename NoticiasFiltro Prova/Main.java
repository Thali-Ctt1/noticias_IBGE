import java.util.Scanner;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

// Importa as classes necessárias para o funcionamento do programa

public class Main { 

    public static void main(String[] args) {
        // Configura a formatação de saída para UTF-8
        try {
            System.setProperty("file.encoding", "UTF-8"); 
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Exception e) {
            System.err.println("Erro ao definir codificação UTF-8: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        Usuario usuario = Persistencia.carregarUsuario(); 

        if (usuario == null) { // Se não houver usuário salvo, cria um novo
            System.out.println("Nenhum usuário encontrado! \n" +
            "Vamos criar um novo perfil \n");
            System.out.print("Bem-vindo! Digite seu nome ou apelido: \n");
            String nome = sc.nextLine();
            
            if (nome == null || nome.trim().isEmpty()) {
                System.out.println("Nome inválido. Seu nome aqui será: 'Usuário Anônimo'.");
                nome = "Usuário Anônimo [0_0]"; 
            }
            usuario = new Usuario(nome);
            Persistencia.salvarUsuario(usuario); 
            System.out.println("Novo perfil de usuário criado e salvo: " + usuario.getNome());
        } else {
            System.out.println("\n Olá, " + usuario.getNome() + "! \n");
        }

        GerenciadorNoticias gerenciador = new GerenciadorNoticias(usuario);
        int opcao;

        do {
            System.out.println("\n--- Menu Principal ---\n");
            System.out.println("1 - Buscar notícias");
            System.out.println("2 - Ver favoritos");
            System.out.println("3 - Ver para ler depois");
            System.out.println("4 - Ver lidas");
            System.out.println("5 - Salvar e sair \n"); 
            System.out.print("Escolha uma opção: \n");

            try {
                opcao = Integer.parseInt(sc.nextLine());

                switch (opcao) {
                    case 1:
                        gerenciador.buscarNoticias(sc);
                        break;
                    case 2:
                        gerenciador.exibirLista(usuario.getFavoritos(), " Notícias Favoritas ", sc);
                        break;
                    case 3:
                        gerenciador.exibirLista(usuario.getParaLerDepois(), " Notícias Para Ler Depois [,u,] ", sc);
                        break;
                    case 4:
                        gerenciador.exibirLista(usuario.getLidas(), " Notícias Lidas [:p] ", sc);
                        break;
                    case 5:
                        Persistencia.salvarUsuario(usuario);
                        System.out.println("Saindo..." +  
                        "Até logo!");
                        break;
                    default:
                        System.out.println(" \n Opção inválida. Tente novamente. \n");
                }
            } catch (NumberFormatException e) {
                System.out.println(" \n Entrada inválida. Por favor, digite um número. \n");
                opcao = 0; 
            }
        } while (opcao != 5);

        sc.close();
    }
}