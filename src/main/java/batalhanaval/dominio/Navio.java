// Em vez de matrizes de inteiros shipId[][] e checagens soltas, o navio guarda seu próprio estado de vida.
package batalhanaval.dominio;
public class Navio{
    // Atributos da classe Navio
    private int id;
    private String nome;
    private int tamanho;
    private int hitsRecebidos;

    // Construtor da classe Navio
    public Navio(int id, String nome, int tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
        this.hitsRecebidos = 0; // Inicialmente, o navio não recebeu nenhum hit
    }

    public void registrarAcerto(){
        this.hitsRecebidos++;
    }

    public boolean isAfundado(){
        return this.hitsRecebidos >= this.tamanho;
    }

    // Getters para os atributos id, nome e tamanho
    public int getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public int getTamanho(){
        return tamanho;
    }
}