package proyecto.zonas.recinto;

public class Sistema {

    static int numId=1;
    private String id;
    private String nombre;
    private boolean comprometido;

    public Sistema(String nombre) {
        this.id = "SI"+numId;
        this.nombre = nombre;
        this.comprometido = false;
        setNumId();
    }

    private void setNumId(){
        numId+=1;
    }

    public boolean isComprometido() {
        return comprometido;
    }

    public void setComprometido(boolean comprometido) {
        this.comprometido = comprometido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }
}
