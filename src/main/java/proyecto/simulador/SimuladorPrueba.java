package proyecto.simulador;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import proyecto.Parque;
import proyecto.incidentes.EventoCriticoException;
import proyecto.incidentes.GestorIncidentes;
import proyecto.incidentes.TipoIncidente;
import proyecto.persistencia.RepositorioBinario;
import proyecto.persistencia.RepositorioTXT;
import proyecto.persistencia.RepositorioXML;
import proyecto.zonas.Zona;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SimuladorPrueba {

    private static final Logger logger = LogManager.getLogger(SimuladorPrueba.class);

    public static void main(String[] args) {
        limpiarDatosAnteriores();

        Parque parque = new Parque();
        RepositorioTXT repoTXT = new RepositorioTXT("datos/operaciones.txt");
        RepositorioBinario repoBIN = new RepositorioBinario("datos/sensores.bin");
        RepositorioXML repoXML = new RepositorioXML("datos/parque.xml");
        GestorIncidentes gestor = new GestorIncidentes(repoTXT, repoBIN, repoXML);

        //Seleccionamos el incidente que queramos probar
        TipoIncidente incidenteAProvocar = TipoIncidente.AVERIA_VEHICULO;

        try {
            logger.info("==========================================================");
            logger.info("MODO PRUEBA INICIADO - Forzando error: {}", incidenteAProvocar);
            logger.info("==========================================================");

            Zona zona = parque.buscarZonaPorId(zonaDePrueba(incidenteAProvocar));
            double medida = zona.getSensor().medirConAnomalia();
            gestor.provocarIncidente(incidenteAProvocar, parque, medida);

        } catch (EventoCriticoException e) {
            logger.error("EXCEPCIÓN CRÍTICA CAPTURADA: {}", e.getMessage());
            logger.error("Protocolo de emergencia activado. Cerrando simulador.");
        } catch (Exception e) {
            logger.fatal("Error inesperado en la simulación", e);
        } finally {
            logger.info("Simulación de prueba finalizada.");
        }
    }

    private static String zonaDePrueba(TipoIncidente incidente) {
        return switch (incidente) {
            case FALLO_ELECTRICO -> "Z04";
            case PUERTA_ATASCADA -> "Z03";
            case FUGA -> "Z02";
            case TORMENTA -> "Z01";
            case ACCESO_NO_AUTORIZADO -> "Z05";
            case AVERIA_VEHICULO -> "Z06";
        };
    }

    private static void limpiarDatosAnteriores() {
        try {
            Files.deleteIfExists(Paths.get("datos/operaciones.txt"));
            Files.deleteIfExists(Paths.get("datos/sensores.bin"));
            Files.deleteIfExists(Paths.get("datos/parque.xml"));
        } catch (IOException e) {
            logger.warn("No se pudieron limpiar los ficheros.", e);
        }
    }
}
