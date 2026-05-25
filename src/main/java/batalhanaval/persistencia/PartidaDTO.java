package batalhanaval.persistencia;

public class PartidaDTO {
    private int id;
    private String inicio, fim, vencedor, seed;

    public PartidaDTO(int id, String inicio, String fim, String vencedor, String seed) {
        this.id = id; this.inicio = inicio; this.fim = fim; this.vencedor = vencedor; this.seed = seed;
    }
    public int getId() { return id; }
    public String getInicio() { return inicio; }
    public String getFim() { return fim; }
    public String getVencedor() { return vencedor; }
    public String getSeed() { return seed; }
}