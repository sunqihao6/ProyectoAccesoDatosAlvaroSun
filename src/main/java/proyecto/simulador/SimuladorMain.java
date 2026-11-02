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
import proyecto.zonas.sensor.Sensor;
import proyecto.zonas.sensor.TipoMedicionSensor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class SimuladorMain {
    private static final Logger logger = LogManager.getLogger(SimuladorMain.class);

    public static void main(String[] args) {
        Random random = new Random();
        limpiarDatosAnteriores();

        Parque parque = new Parque();
        RepositorioTXT repoTXT = new RepositorioTXT("datos/operaciones.txt");
        RepositorioBinario repoBIN = new RepositorioBinario("datos/sensores.bin");
        RepositorioXML repoXML = new RepositorioXML("datos/parque.xml");
        GestorIncidentes gestor = new GestorIncidentes(repoTXT, repoBIN, repoXML);

        try {
            repoXML.guardarEstadoParque(parque);
            logger.info("Sistema de control de Jurassic Park iniciado.");

            List<Zona> zonas = parque.getZonas();

            for (int ciclo = 1; ciclo <= 200; ciclo++) {

                Zona zona = zonas.get(random.nextInt(zonas.size()));
                Sensor sensor = zona.getSensor();
                double medida = sensor.medir(random);
                TipoIncidente incidente = detectarIncidente(zona, sensor.getTipoMedicion(), medida, random);

                logger.info("Ciclo {}: inspección de {}", ciclo, zona.getNombre());

                if (Double.isFinite(medida)) {
                    logger.info("Lectura independiente de {}: {}", sensor.getId(), medida);
                }

                if (incidente != null) {
                    logger.warn("Lectura anómala detectada por {}: {}", sensor.getId(), incidente);
                    gestor.provocarIncidente(incidente, parque, medida);
                }
                Thread.sleep(500);
            }
            logger.info("Fin del turno: no se detectó ningún incidente.");
        } catch (EventoCriticoException e) {
            logger.error("Protocolo de emergencia activado: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("El simulador fue interrumpido.");
        } catch (Exception e) {
            logger.fatal("Error inesperado en la simulación", e);
        } finally {
            logger.info("Simulación finalizada.");
        }
    }

    private static TipoIncidente detectarIncidente(Zona zona, TipoMedicionSensor tipoMedicion,
                                                   double medida, Random random) {

        if (random.nextInt(100) >= 3) {
            return null;
        }

        switch (tipoMedicion) {
            case TENSION: {
                if (medida <= 100.0) {
                    return TipoIncidente.FALLO_ELECTRICO;
                }
            }
            case TEMPERATURA_MOTOR: {
                if (medida >= 120.0) {
                    return TipoIncidente.AVERIA_VEHICULO;
                }
            }
            case RACHA_VIENTO: {
                if (medida > 100.0) {
                    return TipoIncidente.TORMENTA;
                }
            }
            case DIGITAL: {

            }
        }

        switch (zona.getId()) {
            case "Z02": {
                return TipoIncidente.FUGA;
            }
            case "Z03": {
                return TipoIncidente.PUERTA_ATASCADA;
            }
            case "Z05": {
                return TipoIncidente.ACCESO_NO_AUTORIZADO;
            }
            default :{
                return null;
            }
        }
    }

    private static void limpiarDatosAnteriores() {
        try {
            Files.deleteIfExists(Paths.get("datos/operaciones.txt"));
            Files.deleteIfExists(Paths.get("datos/sensores.bin"));
            Files.deleteIfExists(Paths.get("datos/parque.xml"));
        } catch (IOException e) {
            logger.warn("No se pudieron limpiar los ficheros anteriores.", e);
        }
    }
}
