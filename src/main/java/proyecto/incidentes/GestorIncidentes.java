package proyecto.incidentes;

import proyecto.Evento;
import proyecto.Parque;
import proyecto.persistencia.RepositorioBinario;
import proyecto.persistencia.RepositorioTXT;
import proyecto.persistencia.RepositorioXML;
import proyecto.vehiculo.EstadoVehiculo;
import proyecto.vehiculo.Vehiculo;
import proyecto.zonas.EstadoZona;
import proyecto.zonas.Zona;
import proyecto.zonas.sensor.Sensor;

import java.io.IOException;

public class GestorIncidentes {
    private final RepositorioTXT repoTXT;
    private final RepositorioBinario repoBIN;
    private final RepositorioXML repoXML;

    public GestorIncidentes(RepositorioTXT repoTXT, RepositorioBinario repoBIN, RepositorioXML repoXML) {
        this.repoTXT = repoTXT;
        this.repoBIN = repoBIN;
        this.repoXML = repoXML;
    }

    public void provocarIncidente(TipoIncidente tipo, Parque parque, double medidaPrincipal) throws EventoCriticoException, IOException {
        if (tipo.requiereMedidaPrincipal() != Double.isFinite(medidaPrincipal)) {
            throw new IOException("La medida recibida no corresponde al tipo de incidente " + tipo + ".");
        }
        switch (tipo) {
            case TORMENTA : {
                registrarEvidencias(tipo, parque, "Z01", EstadoZona.EN_ALERTA, medidaPrincipal);
                throw new EventoCriticoException("INC-01: Tormenta severa en el Centro de Control (Z01)");
            }

            case FUGA : {
                registrarEvidencias(tipo, parque, "Z02", EstadoZona.EN_EMERGENCIA, medidaPrincipal);
                throw new EventoCriticoException("INC-02: Fuga en la zona de Herbívoros (Z02)");
            }

            case PUERTA_ATASCADA : {
                registrarEvidencias(tipo, parque, "Z03", EstadoZona.EN_EMERGENCIA, medidaPrincipal);
                throw new EventoCriticoException("INC-03: Puerta atascada en Velociraptores (Z03)");
            }

            case FALLO_ELECTRICO : {
                registrarEvidencias(tipo, parque, "Z04", EstadoZona.FUERA_DE_SERVICIO, medidaPrincipal);
                throw new EventoCriticoException("INC-04: Caída de suministro eléctrico en T. Rex (Z04)");
            }

            case ACCESO_NO_AUTORIZADO : {
                registrarEvidencias(tipo, parque, "Z05", EstadoZona.EN_ALERTA, medidaPrincipal);
                throw new EventoCriticoException("INC-05: Acceso no autorizado al Laboratorio (Z05)");
            }

            case AVERIA_VEHICULO : {
                Vehiculo vehiculo = new Vehiculo("Explorer 01");
                vehiculo.setEstado(EstadoVehiculo.AVERIADO);
                vehiculo.setTemperaturaMotor(medidaPrincipal);
                parque.setVehiculo(vehiculo);
                registrarEvidencias(tipo, parque, "Z06", EstadoZona.EN_ALERTA, medidaPrincipal);
                throw new EventoCriticoException("INC-06: Vehículo Explorer 01 averiado en Mantenimiento (Z06)");
            }
        }
    }

    private void registrarEvidencias(TipoIncidente tipo, Parque parque, String idZona,
                                     EstadoZona estadoZona, double medidaPrincipal) throws IOException {

        Zona zona = parque.buscarZonaPorId(idZona);
        if (zona == null){
            throw new IOException("No existe la zona " + idZona + " en el parque.");
        }

        Sensor sensor = zona.getSensor();
        Evento evento = new Evento(tipo);
        zona.setEstado(estadoZona);

        // TXT guarda el incidente
        repoTXT.registrarEvento(evento);

        // XML guarda las zonas y sus estados
        repoXML.guardarEstadoParque(parque);

        // BIN vincula el sensor con la zona y con el ID de evento.
        repoBIN.altaRegistro(sensor.getId(), sensor.getIdZona(), sensor.getEstado().getCodigo(),
                medidaPrincipal, evento.getNumeroId());
    }
}
