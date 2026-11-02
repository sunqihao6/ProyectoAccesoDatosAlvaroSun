package proyecto.analizador;

import proyecto.incidentes.TipoIncidente;

import java.time.LocalDateTime;

/** Datos del incidente extraídos del fichero operaciones.txt. */
class EventoTxt {
    final long idEvento;
    final LocalDateTime marcaTemporal;
    final TipoIncidente incidente;

    EventoTxt(long idEvento, LocalDateTime marcaTemporal, TipoIncidente incidente) {
        this.idEvento = idEvento;
        this.marcaTemporal = marcaTemporal;
        this.incidente = incidente;
    }
}
