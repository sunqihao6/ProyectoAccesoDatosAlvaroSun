package proyecto.zonas.sensor;

public class Sensor {
    static int numId=1;
    private String id;
    private String nombre;
    private EstadoSensor estado;

    public Sensor(String nombre) {
        this.id = "S"+numId;
        this.nombre = nombre;
        this.estado=EstadoSensor.OPERATIVO;
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

    public EstadoSensor getEstado() {
        return estado;
    }

    public void setEstado(EstadoSensor estado) {
        this.estado = estado;
    }
}
