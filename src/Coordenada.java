package batalhanaval.dominio;

public class Coordenada {
    private int x;
    private int y;

    public Coordenada(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordenada não pode ser negativa.");
        }

        this.x = x;
        this.y = y;
    }

    public static Coordenada parse(String s, int boardSize) {
        if (s == null) {
            throw new IllegalArgumentException("Coordenada não pode ser nula.");
        }

        String t = s.trim().toUpperCase();

        if (t.length() < 2 || t.length() > 3) {
            throw new IllegalArgumentException("Formato de coordenada inválido.");
        }

        int x = t.charAt(0) - 'A';

        int y;
        try {
            y = Integer.parseInt(t.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número da coordenada inválido.");
        }

        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new IllegalArgumentException("Coordenada fora dos limites do tabuleiro.");
        }

        return new Coordenada(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
