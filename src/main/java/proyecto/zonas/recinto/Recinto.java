package proyecto.zonas.recinto;

public class Recinto {

    static int numId=1;
    private String id;
    private String nombre;
    private EstadoRecinto estado;

    public Recinto(String nombre) {
        this.id = "R"+numId;
        this.nombre = nombre;
        this.estado=EstadoRecinto.SEGURO;
        setNumId();
    }

    private void setNumId(){
        numId+=1;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoRecinto getEstado() {
        return estado;
    }

    public void setEstado(EstadoRecinto estado) {
        this.estado = estado;
    }
}
