package batalhanaval.dominio;

import java.util.Random;

public class CpuPlayer extends Jogador{
    // Atributo que representa a estratégia de ataque da CPU (RANDOM, HUNT, PARITY)
    private String estrategia; // receberá o valor do game.properties
    private Random random;

    //Construtor herdando os atributos da classe abstrata e recebendo a estratégia da CPU
    public CpuPlayer(String nome, Tabuleiro tabuleiro, String estrategia) {
        super(nome, tabuleiro);
        this.estrategia = estrategia;
        this.random = new Random();
    }

    @Override
    public Coordenada prepararJogada(Coordenada coordenadaDaUI){
        // A CPU ignora completamente a "coordenadaDaUI"
        int tamanhoTabuleiro = this.getTabuleiro().getSize();

        if("RANDOM".equalsIgnoreCase(estrategia)){
            return gerarTiroAleatorio(tamanhoTabuleiro);
        }
        else if("HUNT".equalsIgnoreCase(estrategia)){
            // TODO: Lógica de caça (Membro 3 ou 2 pode implementar a matemática depois)
            // Se acertou um tiro no turno passado, tenta os vizinhos. 
            // Por enquanto, fallback para aleatório:
            return gerarTiroAleatorio(tamanhoTabuleiro);
        }
        else if("PARITY".equalsIgnoreCase(estrategia)){
            // TODO: Lógica de tentar padrão x+y par (estabelecido no properties)
            // Por enquanto, fallback para aleatório:
            return gerarTiroAleatorio(tamanhoTabuleiro);
    }

    // Padrao de seguranca
    return gerarTiroAleatorio(tamanhoTabuleiro);
    }

    // Metodo auxiliar isolando a logica aleatoria
    private Coordenada gerarTiroAleantorio(int limite){
        int x = random.nextInt(limite);
        int y = random.nextInt(limite);
        return new Coordenada(x, y);
    }
}