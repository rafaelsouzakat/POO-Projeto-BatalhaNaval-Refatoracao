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
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                this.grid[i][j] = '.';
            }
        }
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
            if(y + len > size){
                return false;
            }
            for(int i = 0; i < len; i++){
                if(grid[y + i][x] != '.'){
                    return false;
                }
            }
        }
        return true; // Se passou por todas as checagens, é possível posicionar o navio
    }

    // Substitui o antigo placeShip(...)
    public void posicionarNavio(Coordenada c, Navio navio, boolean horizontal){
        if(!podePosicionar(c, navio, horizontal)){
            throw new IllegalArgumentException("Posição inválida para o navio.");
        }

        int x = c.getX();
        int y = c.getY();

        for(int i = 0; i < navio.getTamanho(); i++){
            if(horizontal){
                grid[y][x + i] = 'S'; // Marca a posição do navio no grid
                naviosPosicionados[y][x + i] = navio; // Associa o navio àquela célula
            }
            else{
                grid[y + i][x] = 'S'; // Marca a posição do navio no grid
                naviosPosicionados[y + i][x] = navio;
            }
        }
    }
}
