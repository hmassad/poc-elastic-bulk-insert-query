package pfi.pocelasticbulkinsertquery.model;

public enum Provincia {
    CABA(24, "Ciudad de Buenos Aires", Pais.ARGENTINA),
    BUENOS_AIRES(1, "Buenos Aires", Pais.ARGENTINA);

    private int id;
    private String name;
    private Pais pais;

    Provincia(int id, String name, Pais pais) {
        this.id = id;
        this.name = name;
        this.pais = pais;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Pais getPais() {
        return this.pais;
    }
}
