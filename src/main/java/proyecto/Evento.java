package proyecto;

import java.time.Instant;

public class Evento {
    private static int numId = 1;
    private String idEvento;
    private Instant marcaTemporal;
    private String idZona;
    private String entidad;
    private String accion;

    public Evento(String idZona, String entidad, String accion) {
        this.idEvento = "EVT-" + numId;
        this.marcaTemporal = Instant.now();
        this.idZona = idZona;
        this.entidad = entidad;
        this.accion = accion;
        setNumId();
    }

    private void setNumId() {
        numId += 1;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public Instant getMarcaTemporal() {
        return marcaTemporal;
    }

    public String getIdZona() {
        return idZona;
    }

    public String getEntidad() {
        return entidad;
    }

    public String getAccion() {
        return accion;
    }

    // Formatea el evento para escribirlo en el TXT de operaciones
    public String formatearParaTxt() {
        return marcaTemporal.toString() + "\n" +
                idEvento + " " + idZona + " " + entidad + " " + accion;
    }
}
