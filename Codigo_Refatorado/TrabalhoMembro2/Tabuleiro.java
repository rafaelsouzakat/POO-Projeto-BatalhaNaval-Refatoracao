// O tabuleiro terá como atributo a matriz e recebe os objetos Coordenada e Navio.
// O tabuleiro "TEM" navios e "TEM" coordenadas, relação de composição.

public class Tabuleiro{
    // Atributos da classe Tabuleiro
    private char[][] grid;
    private Navio[][] naviosPosicionados;  // Guarda qual navio está em qual célula
    private int size;

    // Construtor da classe Tabuleiro
    public Tabuleiro(int size){
        this.size = size;
        this.grid = new char[size][size];
        this.naviosPosicionados = new Navio[size][size];

        // Inicializa o grid com água (representada por '.')
    }

    // Substitui o antigo canPlace(...)
    public boolean podePosicionar(Coordenada c, Navio navio, boolean horizontal){
        int x = c.getX();
        int y = c.getY();
        int len = navio.getTamanho();

        if(horizontal){
            if(x + len > size){
                return false; // O navio ultrapassaria os limites do tabuleiro
            }
            for(int i = 0; i < len; i++){
                if(grid[y][x + i] != '.'){
                    return false; // Já existe um navio nessa posição
                }
            }
        }
        else{
            // Lógica similar para a posição vertical
            // Vou implementar depois.
        }
        return true; // Se passou por todas as checagens, é possível posicionar o navio
    }
}
