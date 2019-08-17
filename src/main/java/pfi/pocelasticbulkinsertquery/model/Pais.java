package pfi.pocelasticbulkinsertquery.model;

public enum Pais {
    ARGENTINA(1, "Argentina");

    private int id;
    private String name;

    Pais(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
}
