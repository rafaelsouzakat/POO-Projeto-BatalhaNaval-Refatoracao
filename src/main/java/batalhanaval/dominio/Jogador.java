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

    // metodo abstrato sem corpo, força as subclasses a implementarem sua própria lógica de ataque
    // A UI vai ler o teclado com Scanner e passar a Coordenada pronta para este método.
    // - O HumanPlayer apenas retornará essa coordenadaDaUI.
    // - O CpuPlayer ignorará a coordenadaDaUI e usará a estratégia (RANDOM, HUNT, etc) 
    //   configurada no game.properties para gerar sua própria coordenada.

    public abstract Coordenada prepararJogada(Coordenada coordenadaDaUI);
}