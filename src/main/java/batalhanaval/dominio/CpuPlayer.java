package batalhanaval.dominio;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class CpuPlayer extends Jogador{
    // Atributo que representa a estratégia de ataque da CPU (RANDOM, HUNT, PARITY)
    private String estrategia; // receberá o valor do game.properties
    private Random random;

    // Memória da CPU para a estratégia HUNT
    private List<Coordenada> alvosPendentes; // Lista de coordenadas vizinhas a um acerto para tentar depois

    //Construtor herdando os atributos da classe abstrata e recebendo a estratégia da CPU
    public CpuPlayer(String nome, Tabuleiro tabuleiro, String estrategia) {
        super(nome, tabuleiro);
        this.estrategia = estrategia;
        this.random = new Random();
        this.alvosPendentes = new ArrayList<>();
    }

    @Override
    public Coordenada prepararJogada(Coordenada coordenadaDaUI){
        // A CPU ignora completamente a "coordenadaDaUI"
        int tamanhoTabuleiro = this.getTabuleiro().getSize();

        if("RANDOM".equalsIgnoreCase(estrategia)){
            return gerarTiroAleatorio(tamanhoTabuleiro);
        }
        else if("HUNT".equalsIgnoreCase(estrategia)){
            // Se tem alvos na fila (vizinhos de um acerto anterior), atira neles primeiro
            while (!alvosPendentes.isEmpty()) {
                Coordenada alvo = alvosPendentes.remove(0);
                
                // IMPORTANTE: Adicione essa checagem na sua classe para evitar atirar onde já atirou
                // if (!getTabuleiro().jaAtirou(alvo)) { 
                    return alvo;
                // }
            }
            // Se a fila está vazia (não está caçando), volta a atirar aleatório
            return gerarTiroAleatorio(tamanhoTabuleiro);
        }
        else if("PARITY".equalsIgnoreCase(estrategia)){
            Coordenada c;
            do {
                // Sorteia aleatório até achar uma coordenada onde a soma de X e Y seja Par
                c = gerarTiroAleatorio(tamanhoTabuleiro);
            } while ((c.getX() + c.getY()) % 2 != 0);
            
            return c;
    }

    // Padrao de seguranca
    return gerarTiroAleatorio(tamanhoTabuleiro);
    }

    /**
     * Novo método: A classe Jogo chamará isso quando a CPU acertar um tiro
     * para ela calcular os vizinhos (Cima, Baixo, Esquerda, Direita).
     */
    public void registrarAcertoHUNT(Coordenada acerto) {
        int x = acerto.getX();
        int y = acerto.getY();
        int tamanho = getTabuleiro().getSize();

        // Adiciona os vizinhos na fila respeitando as bordas do tabuleiro
        if (x > 0) alvosPendentes.add(new Coordenada(x - 1, y)); // Esquerda
        if (x < tamanho - 1) alvosPendentes.add(new Coordenada(x + 1, y)); // Direita
        if (y > 0) alvosPendentes.add(new Coordenada(x, y - 1)); // Cima
        if (y < tamanho - 1) alvosPendentes.add(new Coordenada(x, y + 1)); // Baixo
    }    

    // Metodo auxiliar isolando a logica aleatoria
    private Coordenada gerarTiroAleatorio(int limite){
        int x = random.nextInt(limite);
        int y = random.nextInt(limite);
        return new Coordenada(x, y);
    }
}