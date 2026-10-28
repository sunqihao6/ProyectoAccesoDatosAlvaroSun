package proyecto.zonas;

import proyecto.zonas.recinto.Recinto;

import java.util.ArrayList;
import java.util.List;

public class Zona {
    static int numId = 1;
    private String id;
    private String nombre;
    private EstadoZona estado;
    private List<Recinto> recintos;

    public Zona(String nombre) {
        this.nombre = nombre;
        this.estado = EstadoZona.OPERATIVA;
        this.id = "Z" + String.format("%02d", numId);
        this.recintos = new ArrayList<>();
        setNumId();
    }

    public List<Recinto> getRecintos() {
        return recintos;
    }

    public void agregarRecinto(Recinto recinto) {
        this.recintos.add(recinto);
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

    private void setNumId() {
        numId += 1;
    }

}
