//Classe abstrata que representa um jogador
package batalhanaval.dominio;

public abstract class Jogador{
    // modificador protected permite que as subclasses acessem diretamente os atributos
    protected String nome;
    protected Tabuleiro tabuleiro;

    // Construtor do jogador, recebe o nome e o tabuleiro
    public Jogador(String nome, Tabuleiro tabuleiro) {
        this.nome = nome;
        this.tabuleiro = tabuleiro;
    }

    //Encapsulamento com metodos getters
    public String getNome() {
        return nome;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    /**
     * Posiciona a frota automaticamente de forma aleatória.
     * @param tamanhosNavios Array com o tamanho de cada navio (ex: [5-8])
     */
    public void posicionarFrota(int[] tamanhosNavios) {
        java.util.Random random = new java.util.Random();
        int boardSize = this.tabuleiro.getGrid().length;

        // Para cada tamanho de navio exigido pelas regras
        for (int tamanho : tamanhosNavios) {
            boolean posicionado = false;
            
            // Fica em loop tentando sortear até achar uma posição válida
            while (!posicionado) {
                // Sorteia o X e Y dentro do tamanho do tabuleiro
                int x = random.nextInt(boardSize);
                int y = random.nextInt(boardSize);
                Coordenada c = new Coordenada(x, y);

                // Sorteia a direção (50% de chance de ser Horizontal ou Vertical)
                String direcao = random.nextBoolean() ? "H" : "V";

                // Tenta adicionar no tabuleiro. 
                // Se bater em outro navio, adicionarNavio retorna false e o while repete o sorteio!
                posicionado = this.tabuleiro.adicionarNavio(c, tamanho, direcao);
            }
        }
    }

    // metodo abstrato sem corpo, força as subclasses a implementarem sua própria lógica de ataque
    // A UI vai ler o teclado com Scanner e passar a Coordenada pronta para este método.
    // - O HumanPlayer apenas retornará essa coordenadaDaUI.
    // - O CpuPlayer ignorará a coordenadaDaUI e usará a estratégia (RANDOM, HUNT, etc) 
    //   configurada no game.properties para gerar sua própria coordenada.

    public abstract Coordenada prepararJogada(Coordenada coordenadaDaUI);
}