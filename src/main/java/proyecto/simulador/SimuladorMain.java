package proyecto.simulador;

import proyecto.Evento;
import proyecto.Parque;
import proyecto.incidentes.EventoCriticoException;
import proyecto.incidentes.TipoIncidente;
import proyecto.persistencia.RepositorioBinario;
import proyecto.persistencia.RepositorioTXT;
import proyecto.persistencia.RepositorioXML;
import proyecto.zonas.EstadoZona;
import proyecto.zonas.Zona;
import proyecto.zonas.recinto.EstadoRecinto;
import proyecto.zonas.recinto.Recinto;

import java.time.Instant;

public class SimuladorMain {
    public static void main(String[] args) {
        Parque parque = new Parque();

        // Agregar un recinto inicial a la zona Z03 (Velociraptores)
        Zona z03 = parque.buscarZonaPorId("Z03");
        Recinto recintoVelo = new Recinto("Recinto Velociraptores");
        if (z03 != null) {
            z03.agregarRecinto(recintoVelo);
        }

        RepositorioTXT repoTXT = new RepositorioTXT("datos/operaciones.txt");
        RepositorioBinario repoBIN = new RepositorioBinario("datos/sensores.bin");
        RepositorioXML repoXML = new RepositorioXML("datos/parque.xml");

        TipoIncidente incidenteAProvocar = TipoIncidente.PUERTA_ATASCADA;

        // Lectura de argumentos de línea de comandos (modo prueba)
        if (args.length >= 2 && args[0].equalsIgnoreCase("-incidente")) {
            try {
                incidenteAProvocar = TipoIncidente.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo de incidente no reconocido. Usando PUERTA_ATASCADA por defecto.");
            }
        }

        try {
            System.out.println("Ejecutando simulador en modo de prueba para: " + incidenteAProvocar);

            if (incidenteAProvocar == TipoIncidente.PUERTA_ATASCADA) {
                // 1. Evidencia TXT
                Evento evt1 = new Evento("Z03", "P03", "ORDEN_CIERRE");
                Evento evt2 = new Evento("Z03", "P03", "CIERRE_NO_CONFIRMADO");
                repoTXT.registrarEvento(evt1);
                repoTXT.registrarEvento(evt2);

                // 2. Evidencia BIN (Registro de 44 bytes con la puerta abierta/bloqueada)
                repoBIN.altaRegistro(
                        Instant.now().toEpochMilli(),
                        3,  // idSensor (S03)
                        3,  // idZona (Z03)
                        (byte) 2, // Tipo de medición
                        (byte) 1, // Estado puerta abierta / atascada
                        (short) 0,
                        0.0, 0.0,
                        43L // idEvento
                );

                // 3. Evidencia XML
                if (z03 != null) {
                    z03.setEstado(EstadoZona.EN_EMERGENCIA);
                    recintoVelo.setEstado(EstadoRecinto.CON_BRECHA_DE_SEGURIDAD);
                }
                repoXML.guardarEstadoParque(parque);

                throw new EventoCriticoException("INC-02: Puerta P03 atascada en zona Z03");
            }

        } catch (EventoCriticoException e) {
            System.err.println("ALERTA MÁXIMA: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
