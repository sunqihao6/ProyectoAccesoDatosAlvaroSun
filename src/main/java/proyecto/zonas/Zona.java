package proyecto.zonas;

import proyecto.zonas.sensor.Sensor;
import proyecto.zonas.sensor.TipoMedicionSensor;

public class Zona {
    private final String id;
    private final String nombre;
    private final Sensor sensor;
    private EstadoZona estado;

    public Zona(String id, String nombre, String idSensor,
                TipoMedicionSensor tipoMedicion) {
        this.id = id;
        this.nombre = nombre;
        this.estado = EstadoZona.OPERATIVA;
        this.sensor = new Sensor(idSensor, "Sensor de " + nombre, id, tipoMedicion);
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

    public Sensor getSensor() {
        return sensor;
    }
}
