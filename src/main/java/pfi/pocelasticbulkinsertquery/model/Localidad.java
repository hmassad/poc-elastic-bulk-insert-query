package pfi.pocelasticbulkinsertquery.model;

public enum Localidad {
    COMUNA_1(1, "Comuna 1", Provincia.CABA),
    COMUNA_2(2, "Comuna 2", Provincia.CABA),
    SAN_ISIDRO(3, "San Isidro", Provincia.BUENOS_AIRES),
    LA_PLATA(4, "La Plata", Provincia.BUENOS_AIRES);

    private int id;
    private String name;
    private Provincia provincia;

    Localidad(int id, String name, Provincia provincia) {
        this.id = id;
        this.provincia = provincia;
        this.name = name;
    }

    public static Localidad findByName(String name) {
        for (Localidad localidad : values())
            if (localidad.name.equals(name))
                return localidad;
        throw new IllegalArgumentException(
                "No enum constant " + Localidad.class.getCanonicalName() + "." + name);
    }

    public static Localidad findById(int id) {
        for (Localidad localidad : values())
            if (localidad.id == id)
                return localidad;
        throw new IllegalArgumentException(
                "No enum constant");
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Provincia getProvincia() {
        return this.provincia;
    }
}
