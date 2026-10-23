package zonas;

public class Zona {
    static int numId=1;
    private String id;
    private String nombre;
    private EstadoZona estado;


    public Zona(String nombre) {
        this.nombre = nombre;
        this.estado = EstadoZona.OPERATIVA;
        this.id = "Z"+numId;
        setNumId();
    }

    public EstadoZona getEstado() {
        return estado;
    }

    public void setEstado(EstadoZona estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }

    public static void setNumId(int numId) {
        Zona.numId = numId;
    }

    private void setNumId(){
        numId+=1;
    }

}
