import java.nio.charset.Charset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class App {

    static String nomeArquivoDados;
    static Scanner teclado;
    
    // Árvores de suporte para a oficina
    static ABB<Integer, Produto> produtosPorID;
    static ABB<String, Produto> produtosPorNome;

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("\nDigite enter para continuar...");
        teclado.nextLine();
    }

    static void cabecalho() {
        System.out.println("AEDs II - OFICINA DE ÁRVORES BINÁRIAS DE BUSCA");
        System.out.println("==============================================");
    }

    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos (via arquivo)");
        System.out.println("2 - Testar Recorte (por ID e por Nome)");
        System.out.println("3 - Localizar Produto por ID (Tarefa 4)");
        System.out.println("4 - Localizar Produto por Nome (Tarefa 4)");
        System.out.println("5 - Remover Produto por ID (Tarefa 5)");
        System.out.println("6 - Remover Produto por Nome (Tarefa 5)");
        System.out.println("0 - Finalizar");
        System.out.print("Digite sua opção: ");
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void carregarDados(String arquivoDados) {
        produtosPorID = new ABB<>();
        produtosPorNome = new ABB<>();
        Scanner arquivo = null;

        try {
            arquivo = new Scanner(new File(arquivoDados), Charset.forName("UTF-8"));
            int numProdutos = Integer.parseInt(arquivo.nextLine());

            for (int i = 0; i < numProdutos; i++) {
                String linha = arquivo.nextLine();
                Produto p = Produto.criarDoTexto(linha);
                if (p != null) {
                    produtosPorID.inserir(p.hashCode(), p);
                    produtosPorNome.inserir(p.descricao.toLowerCase(), p);
                }
            }
            System.out.println("Dados carregados com sucesso nas árvores!");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de dados: " + e.getMessage());
        } finally {
            if (arquivo != null) arquivo.close();
        }
    }

    private static void mostrarResultadoBusca(Produto produto, IMedicao medicao) {
        if (produto != null) {
            System.out.println("\nProduto Encontrado: " + produto);
        } else {
            System.out.println("\nProduto não encontrado.");
        }
        System.out.println("Total de comparações: " + medicao.getComparacoes());
        System.out.printf("Tempo gasto: %.4f ms\n", medicao.getTempo());
    }

    // ==========================================
    // TAREFA 4: Métodos Genéricos de Localização
    // ==========================================
    public static <K extends Comparable<K>> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
        try {
            return produtosCadastrados.pesquisar(procurado);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        System.out.print("Digite o ID (código) do produto: ");
        int id = Integer.parseInt(teclado.nextLine());
        Produto p = localizarProduto(produtosCadastrados, id);
        mostrarResultadoBusca(p, produtosCadastrados);
        return p;
    }

    public static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        System.out.print("Digite o nome (descrição) do produto: ");
        String nome = teclado.nextLine().toLowerCase();
        Produto p = localizarProduto(produtosCadastrados, nome);
        mostrarResultadoBusca(p, produtosCadastrados);
        return p;
    }

    // ==========================================
    // TAREFA 5: Métodos Genéricos de Remoção
    // ==========================================
    public static <K extends Comparable<K>> Produto removerProduto(ABB<K, Produto> produtosCadastrados, K chave) {
        Produto p = localizarProduto(produtosCadastrados, chave);
        if (p != null) {
            produtosCadastrados.remover(chave);
            System.out.println("Produto removido com sucesso da ABB!");
        } else {
            System.out.println("Produto não encontrado para remoção.");
        }
        return p;
    }

    public static Produto removerProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        System.out.print("Digite o ID do produto a remover: ");
        int id = Integer.parseInt(teclado.nextLine());
        return removerProduto(produtosCadastrados, id);
    }

    public static Produto removerProdutoNome(ABB<String, Produto> produtosCadastrados) {
        System.out.print("Digite o nome do produto a remover: ");
        String nome = teclado.nextLine().toLowerCase();
        return removerProduto(produtosCadastrados, nome);
    }

    // Teste de Recorte
    static void testarRecorte() {
        System.out.println("\n--- TESTE DE RECORTE POR NOME ---");
        System.out.print("Digite o nome inicial: ");
        String inicio = teclado.nextLine().toLowerCase();
        System.out.print("Digite o nome final: ");
        String fim = teclado.nextLine().toLowerCase();

        List<Produto> resultado = produtosPorNome.recortar(inicio, fim);
        System.out.println("Produtos encontrados no intervalo (" + inicio + " até " + fim + "):");
        for (Produto p : resultado) {
            System.out.println(" - " + p);
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        carregarDados(nomeArquivoDados);

        int opcao = -1;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> carregarDados(nomeArquivoDados);
                case 2 -> testarRecorte();
                case 3 -> localizarProdutoID(produtosPorID);
                case 4 -> localizarProdutoNome(produtosPorNome);
                case 5 -> removerProdutoID(produtosPorID);
                case 6 -> removerProdutoNome(produtosPorNome);
                case 0 -> System.out.println("Encerrando o programa...");
                default -> System.out.println("Opção inválida!");
            }
            if (opcao != 0) pausa();
        } while (opcao != 0);

        teclado.close();
    }
}