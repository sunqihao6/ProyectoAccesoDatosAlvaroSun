package proyecto;

import proyecto.incidentes.TipoIncidente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Evento se guarda en el fichero txt
public class Evento {
    private static final DateTimeFormatter FORMATO_MARCA_TEMPORAL =
            DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm:ss");

    private final long numeroId;
    private final LocalDateTime marcaTemporal;
    private final TipoIncidente incidente;

    public Evento(TipoIncidente incidente) {
        this.numeroId = 1;
        this.marcaTemporal = LocalDateTime.now();
        this.incidente = incidente;
    }

    public long getNumeroId() {

        return numeroId;
    }

    public String getIdEvento() {

        return "EVT-" + numeroId;
    }

    public TipoIncidente getIncidente() {

        return incidente;
    }

    //guarda idEvento, marcaTemporal y tipoIncidente
    public String formatearParaTxt() {
        return getIdEvento() + " | " + marcaTemporal.format(FORMATO_MARCA_TEMPORAL) + " | " + incidente.name();
    }
}
