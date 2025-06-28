import java.time.LocalDate;
import java.time.YearMonth; 
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

// Classe GerenciadorNoticias que gerencia as operações de busca e exibição de notícias

public class GerenciadorNoticias {
    private Usuario usuario;
    private List<Noticia> ultimasNoticiasBuscadas; // Armazena as últimas notícias buscadas

    public GerenciadorNoticias(Usuario usuario) {
        this.usuario = usuario;
        this.ultimasNoticiasBuscadas = new ArrayList<>();
    }

    public void buscarNoticias(Scanner sc) {
        System.out.println("\nEntão, como você gostaria de buscar as notícias?\n");
        System.out.println("1 - Por termo (título, palavras-chave)");
        System.out.println("2 - Por data (período)");
        System.out.println("0 - Voltar\n");
        System.out.print("Escolha uma opção: ");

        try {
            int tipoBusca = Integer.parseInt(sc.nextLine());
            List<Noticia> resultados = new ArrayList<>();

            switch (tipoBusca) {
                case 1:
                    System.out.print("\nDigite um termo de busca: ");
                    String termo = sc.nextLine();

                    if (termo.trim().isEmpty()) {
                        System.out.println("\nO termo de busca não pode ser vazio.");
                        return;
                    }

                    resultados = IBGENoticiasAPI.buscarNoticiasPorTermo(termo);
                    if (resultados.isEmpty()) {
                        System.out.println("\nNenhuma notícia encontrada para o termo: \"" + termo + "\".");
                        return;
                    }
                    break;

                case 2:
                    System.out.println("\n--- Digite a Data Inicial ---");
                    LocalDate dataInicial = coletarData(sc);
                    if (dataInicial == null) {
                        System.out.println("Coleta da data inicial cancelada ou inválida.");
                        return;
                    }

                    System.out.println("\n--- Digite a Data Final ---");
                    LocalDate dataFinal = coletarData(sc);
                    if (dataFinal == null) {
                        System.out.println("Coleta da data final cancelada ou inválida.");
                        return;
                    }

                    // Valida a data inicial, não pode ser depois da final
                    if (dataInicial.isAfter(dataFinal)) {
                        System.out.println("\nA data inicial não pode ser posterior à data final. Por favor, tente novamente.");
                        return;
                    }

                    // Formata as datas para o formato aceito pela API
                    String dataInicialApi = dataInicial.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String dataFinalApi = dataFinal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    resultados = IBGENoticiasAPI.buscarNoticiasPorData(dataInicialApi, dataFinalApi);

                    if (resultados.isEmpty()) {
                        System.out.println("\nNenhuma notícia encontrada para o período de " + dataInicialApi + " a " + dataFinalApi + ".");
                        return;
                    }
                    break;

                case 0:
                    return; // Volta ao menu anterior

                default:
                    System.out.println("Opção inválida. Voltando ao menu.");
                    return;
            }

            ultimasNoticiasBuscadas = resultados;
            exibirNoticiasComOpcoes(ultimasNoticiasBuscadas, sc, "\nResultados da Busca\n");

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
        } catch (java.io.IOException e) {
            System.out.println("\nErro ao buscar notícias: " + e.getMessage());
            System.out.println("Por favor, verifique sua conexão ou tente novamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro de argumento: " + e.getMessage());
            System.out.println("Certifique-se de que os dados inseridos estão corretos.");
        } catch (Exception e) {
            System.out.println("\nOcorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Método auxiliar para coletar uma data do usuário com validação
    private LocalDate coletarData(Scanner sc) {
        int ano, mes, dia;
        LocalDate data = null;
        boolean dataValida = false;

        while (!dataValida) {
            try {
                System.out.print(" Ano (YYYY): ");
                ano = Integer.parseInt(sc.nextLine());
                if (ano < 1900 || ano > LocalDate.now().getYear() + 1) { 
                    System.out.println("Ano inválido. Digite um ano entre 1900 e o próximo ano.");
                    continue;
                }

                System.out.print(" Mês (1-12): ");
                mes = Integer.parseInt(sc.nextLine());
                if (mes < 1 || mes > 12) {
                    System.out.println("Mês inválido. Digite um número entre 1 e 12.");
                    continue;
                }

                System.out.print(" Dia (1-31): ");
                dia = Integer.parseInt(sc.nextLine());
                if (dia < 1 || dia > 31) {
                    System.out.println("Dia inválido. Digite um número entre 1 e 31.");
                    continue;
                }
                YearMonth yearMonth = YearMonth.of(ano, mes);
                if (dia < 1 || dia > yearMonth.lengthOfMonth()) {
                    System.out.println("Dia inválido para o mês e ano informados. Mês " + mes + " de " + ano + " tem no máximo " + yearMonth.lengthOfMonth() + " dias.");
                    continue;
                }

                data = LocalDate.of(ano, mes, dia);
                dataValida = true; 
                
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número para ano, mês e dia.");
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Por favor, verifique se a data inserida é real. " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado ao coletar a data: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return data;
    }

    // Método auxiliar para exibir notícias de qualquer lista e permitir interação (seleção, adicionar/remover, etc.)
    private void exibirNoticiasComOpcoes(List<Noticia> lista, Scanner sc, String tituloLista) {
        System.out.println("\n--- " + tituloLista + " ---");
        if (lista.isEmpty()) {
            System.out.println(" \n Nenhuma notícia disponível para exibição \n");
            return;
        }

        // Exibe as notícias numeradas
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("\n[" + (i + 1) + "] " + lista.get(i).getTitulo());
            System.out.println("    Introdução: " + lista.get(i).getIntroducao());
            // Mostra a data formatada
            if (lista.get(i).getDataPublicacao() != null) {
                System.out.println("    Data: " + lista.get(i).getDataPublicacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            } else {
                System.out.println("    Data: Data indisponível");
            }
        }

        System.out.print("\nSelecione uma notícia pelo número para ver mais detalhes e opções, ou digite 0 para voltar: ");
        String opcaoStr = sc.nextLine();
        try {
            int opcao = Integer.parseInt(opcaoStr);

            if (opcao > 0 && opcao <= lista.size()) {
                Noticia n = lista.get(opcao - 1);
                System.out.println("\n--- Detalhes da Notícia --- \n");
                System.out.println(n.toString());
                System.out.println("----------------------------------------");

                System.out.println("\nOpções:\n");
                System.out.println("1 - Adicionar aos favoritos");
                System.out.println("2 - Marcar como lida");
                System.out.println("3 - Adicionar para ler depois");
                
                // Opção de remover só aparece se for uma lista gerenciada pelo usuário
                if (tituloLista.contains("Favoritas") || tituloLista.contains("Lidas") || tituloLista.contains("Ler Depois")) {
                     System.out.println("4 - Remover da lista atual");
                }
                System.out.println("0 - Voltar ao menu anterior");

                System.out.print("Escolha uma ação: ");
                int acao = Integer.parseInt(sc.nextLine()); 

                switch (acao) {
                    case 1:
                        if (!usuario.getFavoritos().contains(n)) {
                            usuario.getFavoritos().add(n);
                            System.out.println("Notícia adicionada aos favoritos.");
                        } else {
                            System.out.println("Notícia já está nos favoritos.");
                        }
                        break;
                    case 2:
                        if (!usuario.getLidas().contains(n)) {
                            usuario.getLidas().add(n);
                            System.out.println("Notícia marcada como lida com sucesso");
                        } else {
                            System.out.println("Notícia já está nas lidas");
                        }
                        break;
                    case 3:
                        if (!usuario.getParaLerDepois().contains(n)) {
                            usuario.getParaLerDepois().add(n);
                            System.out.println("Notícia adicionada para ler depois com sucesso");
                        } else {
                            System.out.println("Notícia já está para ler depois");
                        }
                        break;
                    case 4:
                        if (tituloLista.contains("Favoritas")) {
                            removerNoticiaDaLista(usuario.getFavoritos(), n, "Notícias Favoritas");
                        } else if (tituloLista.contains("Lidas")) {
                            removerNoticiaDaLista(usuario.getLidas(), n, "Notícias Lidas");
                        } else if (tituloLista.contains("Ler Depois")) {
                            removerNoticiaDaLista(usuario.getParaLerDepois(), n, "Notícias Para Ler Depois");
                        } else {
                            System.out.println("Não é possível remover desta lista (resultados de busca). Por favor, adicione a notícia a uma lista específica antes de remover.");
                        }
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Ação inválida");
                }
            } else if (opcao == 0) {
                // Voltar
            } else {
                System.out.println("Opção de notícia inválida. Por favor, selecione um número válido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    // Método para remover uma notícia de uma lista específica
    private void removerNoticiaDaLista(List<Noticia> lista, Noticia noticiaParaRemover, String nomeLista) {
        if (lista.remove(noticiaParaRemover)) {
            System.out.println("Notícia \"" + noticiaParaRemover.getTitulo() + "\" removida da lista '" + nomeLista + "'.");
        } else {
            System.out.println("Erro: Notícia não encontrada na lista '" + nomeLista + "'.");
        }
    }

    // Método público para exibir as listas do usuário com opções de ordenação
    public void exibirLista(List<Noticia> lista, String nomeLista, Scanner sc) {
        System.out.println("\n--- " + nomeLista + " ---");
        if (lista.isEmpty()) {
            System.out.println("Lista vazia");
            return;
        }

        System.out.println("1 - Ordenar por Título (A-Z)");
        System.out.println("2 - Ordenar por Data (Mais Recente)");
        System.out.println("3 - Ordenar por Tipo/Categoria");
        System.out.println("4 - Exibir sem ordenar");
        System.out.println("0 - Voltar");
        System.out.print("\nEscolha uma opção: \n");
        String ordemOpcaoStr = sc.nextLine();
        
        List<Noticia> listaOrdenada = new ArrayList<>(lista);

        try {
            int ordemOpcao = Integer.parseInt(ordemOpcaoStr);
            switch (ordemOpcao) {
                case 1: // Ordenar por Título
                    listaOrdenada.sort(Comparator.comparing(Noticia::getTitulo));
                    exibirNoticiasComOpcoes(listaOrdenada, sc, nomeLista + " (Ordenada por Título)");
                    break;
                case 2: // Ordenar por Data (usando LocalDateTime)
                    // Garante que notícias com data nula não causem erro na comparação, colocando-as no final
                    listaOrdenada.sort(Comparator.nullsLast(Comparator.comparing(Noticia::getDataPublicacao).reversed()));
                    exibirNoticiasComOpcoes(listaOrdenada, sc, nomeLista + " (Ordenada por Data)");
                    break;
                case 3: // Ordenar por Tipo
                    // Garante que notícias com tipo nulo não causem erro na comparação, colocando-os no final
                    listaOrdenada.sort(Comparator.nullsLast(Comparator.comparing(Noticia::getTipo)));
                    exibirNoticiasComOpcoes(listaOrdenada, sc, nomeLista + " (Ordenada por Tipo)");
                    break;
                case 4: // Exibir sem ordenar
                    exibirNoticiasComOpcoes(lista, sc, nomeLista + " (Sem Ordenação)");
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção de ordenação inválida. Voltando sem exibir a lista.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número. Voltando sem exibir a lista.");
        }
    }
}