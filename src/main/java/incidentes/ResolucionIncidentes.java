package incidentes;

import java.util.ArrayList;
import java.util.List;

public class ResolucionIncidentes {
    private TipoIncidente incidenteDetectado;
    private String zonaAfectadaId;
    private List<String> accionesRecomendadas;

    public ResolucionIncidentes(TipoIncidente incidenteDetectado, String zonaAfectadaId) {
        this.incidenteDetectado = incidenteDetectado;
        this.zonaAfectadaId = zonaAfectadaId;
        this.accionesRecomendadas = new ArrayList<>();
    }

    public void agregarAccion(String accion) {
        this.accionesRecomendadas.add(accion);
    }

    public TipoIncidente getIncidenteDetectado() {
        return incidenteDetectado;
    }

    public void setIncidenteDetectado(TipoIncidente incidenteDetectado) {
        this.incidenteDetectado = incidenteDetectado;
    }

    public String getZonaAfectadaId() {
        return zonaAfectadaId;
    }

    public void setZonaAfectadaId(String zonaAfectadaId) {
        this.zonaAfectadaId = zonaAfectadaId;
    }

    public List<String> getAccionesRecomendadas() {
        return accionesRecomendadas;
    }
}
