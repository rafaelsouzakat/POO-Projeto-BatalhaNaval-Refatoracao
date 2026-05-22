package batalhanaval.dominio;

public class HumanPlayer extends Jogador{
    // Construtor herdando os atributos da classe abstrata
    public HumanPlayer(String nome, Tabuleiro tabuleiro) {
        super(nome, tabuleiro);
    }

    @Override
    public Coordenada prepararJogada(Coordenada coordenadaDaUI) {
        // Para o jogador humano, a UI já fez o trabalho duro de ler o console, validar o formato da string (ex: "B5") e transformar neste objeto Coordenada.
        // O domínio apenas retorna a jogada para o motor do jogo processar.

        return coordenadaDaUI;
    }
}