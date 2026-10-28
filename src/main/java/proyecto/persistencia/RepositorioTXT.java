package proyecto.persistencia;

import proyecto.Evento;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RepositorioTXT {
    private final Path rutaFichero;

    public RepositorioTXT(String ruta) {
        this.rutaFichero = Paths.get(ruta);
        crearDirectorioSiNoExiste();
    }

    private void crearDirectorioSiNoExiste() {
        try {
            if (rutaFichero.getParent() != null) {
                Files.createDirectories(rutaFichero.getParent());
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }
    }

    public void registrarEvento(Evento evento) {
        // StandardOpenOption.APPEND para añadir al final sin sobrescribir
        try (BufferedWriter writer = Files.newBufferedWriter(rutaFichero,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(evento.formatearParaTxt());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en operaciones.txt: " + e.getMessage());
        }
    }
}
