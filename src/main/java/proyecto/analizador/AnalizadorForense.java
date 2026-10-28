package proyecto.analizador;

import proyecto.incidentes.ResolucionIncidentes;
import proyecto.incidentes.TipoIncidente;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AnalizadorForense {
    public void analizarYGenerarInforme() {
        boolean hayOrdenCierretxt = false;
        boolean hayErrorBinario = false;
        String zonaAfectada = "Z03";

        // 1. Lectura del TXT de operaciones
        Path rutaTxt = Paths.get("datos/operaciones.txt");
        if (Files.exists(rutaTxt)) {
            try (BufferedReader reader = Files.newBufferedReader(rutaTxt)) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (linea.contains("CIERRE_NO_CONFIRMADO")) {
                        hayOrdenCierretxt = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 2. Lectura del Binario de telemetría
        File fBin = new File("datos/sensores.bin");
        if (fBin.exists() && fBin.length() >= 44) {
            try (RandomAccessFile raf = new RandomAccessFile(fBin, "r")) {
                raf.seek(0);
                long timestamp = raf.readLong();
                int idSensor = raf.readInt();
                int idZona = raf.readInt();
                byte tipo = raf.readByte();
                byte estado = raf.readByte(); // 1 = Abierto tras orden

                if (estado == 1) {
                    hayErrorBinario = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 3. Correlación de evidencias e identificación
        if (hayOrdenCierretxt && hayErrorBinario) {
            ResolucionIncidentes resolucion = new ResolucionIncidentes(TipoIncidente.PUERTA_ATASCADA, zonaAfectada);
            resolucion.agregarAccion("AISLAR_ACCESO");
            resolucion.agregarAccion("ACTIVAR_CONTENCION_ALTERNATIVA");
            resolucion.agregarAccion("SOLICITAR_MANTENIMIENTO");

            generarInformeSalida(resolucion);
            generarEvidenciasBinaria();
            generarXmlAfectados(resolucion);
        } else {
            System.out.println("Diagnóstico: INDETERMINADO - Evidencias insuficientes.");
        }
    }

    private void generarInformeSalida(ResolucionIncidentes res) {
        try {
            Files.createDirectories(Paths.get("salida"));
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("salida/informe.txt"))) {
                writer.write("INCIDENTE DETECTADO: " + res.getIncidenteDetectado());
                writer.newLine();
                writer.write("Zona afectada: " + res.getZonaAfectadaId());
                writer.newLine();
                writer.write("Evidencias: PUERTA_ATASCADA confirmada por TXT y BIN.");
                writer.newLine();
                writer.write("Resolución recomendada:");
                writer.newLine();
                for (String accion : res.getAccionesRecomendadas()) {
                    writer.write(" - " + accion);
                    writer.newLine();
                }
                writer.write("Estado del diagnóstico: CONFIRMADO");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarEvidenciasBinaria() {
        try {
            Path origen = Paths.get("datos/sensores.bin");
            Path destino = Paths.get("salida/evidencias.bin");
            if (Files.exists(origen)) {
                Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarXmlAfectados(ResolucionIncidentes res) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("salida/elementos_afectados.xml"))) {
            writer.write("<analisis incidente=\"" + res.getIncidenteDetectado() + "\">\n");
            writer.write("  <zona id=\"" + res.getZonaAfectadaId() + "\" estado=\"EMERGENCIA\"/>\n");
            writer.write("  <resolucion>\n");
            for (String accion : res.getAccionesRecomendadas()) {
                writer.write("    <accion>" + accion + "</accion>\n");
            }
            writer.write("  </resolucion>\n");
            writer.write("</analisis>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
