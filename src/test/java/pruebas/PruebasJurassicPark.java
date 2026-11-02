package pruebas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import proyecto.Evento;
import proyecto.Parque;
import proyecto.analizador.AnalizadorForense;
import proyecto.incidentes.EventoCriticoException;
import proyecto.analizador.ResolucionIncidentes;
import proyecto.incidentes.TipoIncidente;
import proyecto.persistencia.RepositorioBinario;
import proyecto.persistencia.RepositorioTXT;
import proyecto.persistencia.RepositorioXML;
import proyecto.zonas.EstadoZona;
import proyecto.zonas.Zona;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebasJurassicPark {

    @Test
    @DisplayName("1. TXT: Escribir y verificar registro de eventos")
    void testPersistenciaTXT(@TempDir Path tempDir) throws IOException {
        Path txtPath = tempDir.resolve("datos\\operaciones.txt");
        RepositorioTXT repoTXT = new RepositorioTXT(txtPath.toString());
        Evento evento = new Evento(TipoIncidente.PUERTA_ATASCADA);
        repoTXT.registrarEvento(evento);
        List<String> lineas = Files.readAllLines(txtPath);

        assertFalse(lineas.isEmpty(), "El fichero TXT no debe estar vacío.");
        assertTrue(lineas.stream().anyMatch(l -> l.contains("| PUERTA_ATASCADA")),
                "El TXT debe contener el incidente.");
        assertFalse(lineas.get(0).contains("Z03"), "La zona no se duplica en el TXT.");
    }

    @Test
    @DisplayName("2. BIN alta: Añadir un registro fijo de 37 bytes")
    void testBinarioAltaRegistro(@TempDir Path tempDir) throws IOException {
        Path binPath = tempDir.resolve("sensores.bin");
        RepositorioBinario repoBIN = new RepositorioBinario(binPath.toString());
        repoBIN.altaRegistro("S01", "Z01", (byte) 0, 12.5, 100L);
        File fBin = binPath.toFile();
        assertTrue(fBin.exists());
        assertEquals(RepositorioBinario.TAM_REGISTRO, fBin.length(), "El tamaño debe coincidir con el formato BIN.");
    }

    @Test
    @DisplayName("3. BIN alta: Añadir un registro sin sobrescribir el anterior")
    void testBinarioAltaAnexaRegistros(@TempDir Path tempDir) throws IOException {
        Path binPath = tempDir.resolve("sensores.bin");
        RepositorioBinario repoBIN = new RepositorioBinario(binPath.toString());
        repoBIN.altaRegistro("S1", "Z01", (byte) 0, 10.0, 1L);
        assertEquals(RepositorioBinario.TAM_REGISTRO, Files.size(binPath));
        repoBIN.altaRegistro("S2", "Z01", (byte) 0, 20.0, 2L);
        assertEquals(2L * RepositorioBinario.TAM_REGISTRO, Files.size(binPath),
                "La segunda alta debe añadirse al final del BIN.");
    }

    @Test
    @DisplayName("4. XML: Generar y verificar estructura inicial del parque")
    void testPersistenciaXML(@TempDir Path tempDir) throws IOException {
        Path xmlPath = tempDir.resolve("parque.xml");
        RepositorioXML repoXML = new RepositorioXML(xmlPath.toString());
        Parque parque = new Parque();
        repoXML.guardarEstadoParque(parque);
        assertTrue(Files.exists(xmlPath), "El fichero parque.xml debe haberse creado.");
    }

    @Test
    @DisplayName("5. Excepción: Comprobar lanzamiento de EventoCriticoException tras persistencia")
    void testExcepcionCritica(@TempDir Path tempDir) {
        Path txtPath = tempDir.resolve("operaciones.txt");
        RepositorioTXT repoTXT = new RepositorioTXT(txtPath.toString());
        assertThrows(EventoCriticoException.class, () -> {
            repoTXT.registrarEvento(new Evento(TipoIncidente.FALLO_ELECTRICO));
            throw new EventoCriticoException("Prueba de fallo crítico.");
        });
        assertTrue(Files.exists(txtPath), "Las evidencias deben existir antes de lanzar la excepción.");
    }

    @ParameterizedTest
    @EnumSource(TipoIncidente.class)
    @DisplayName("6. Diagnóstico: Cobertura parametrizada de los seis incidentes posibles")
    void testReglasDiagnostico(TipoIncidente tipo) {
        ResolucionIncidentes res = new ResolucionIncidentes(tipo, "Z01", "Centro de Control",
                "EN_ALERTA", "S01", tipo.getNombreSistema(), 12.5, 1L);
        assertNotNull(res.getIncidenteDetectado());
        assertEquals(tipo, res.getIncidenteDetectado());
    }

}
