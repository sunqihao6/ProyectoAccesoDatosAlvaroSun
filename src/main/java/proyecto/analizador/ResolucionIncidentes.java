package proyecto.analizador;

import proyecto.incidentes.TipoIncidente;

import java.util.ArrayList;
import java.util.List;

public class ResolucionIncidentes {
    private final TipoIncidente incidenteDetectado;
    private final String zonaAfectadaId;
    private final String zonaAfectadaNombre;
    private final String estadoZona;
    private final String sensorId;
    private final String sistemaComprometido;
    private final double valorSensor;
    private final long idEvento;
    private final List<String> accionesRecomendadas = new ArrayList<>();

    public ResolucionIncidentes(TipoIncidente incidenteDetectado,
                                String zonaAfectadaId,
                                String zonaAfectadaNombre,
                                String estadoZona,
                                String sensorId,
                                String sistemaComprometido,
                                double valorSensor,
                                long idEvento) {

        this.incidenteDetectado = incidenteDetectado;
        this.zonaAfectadaId = zonaAfectadaId;
        this.zonaAfectadaNombre = zonaAfectadaNombre;
        this.estadoZona = estadoZona;
        this.sensorId = sensorId;
        this.sistemaComprometido = sistemaComprometido;
        this.valorSensor = valorSensor;
        this.idEvento = idEvento;
    }

    public void agregarAccion(String accion) {
        accionesRecomendadas.add(accion);
    }

    public TipoIncidente getIncidenteDetectado() {
        return incidenteDetectado;
    }

    public String getZonaAfectadaId() {
        return zonaAfectadaId;
    }

    public String getZonaAfectadaNombre() {
        return zonaAfectadaNombre;
    }

    public String getEstadoZona() {
        return estadoZona;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getSistemaComprometido() {
        return sistemaComprometido;
    }

    public double getValorSensor() {
        return valorSensor;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public List<String> getAccionesRecomendadas() {
        return accionesRecomendadas;
    }
}
