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
    }

    public void registrarEvento(Evento evento) throws IOException {
        if (rutaFichero.getParent() != null) {
            Files.createDirectories(rutaFichero.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(rutaFichero,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(evento.formatearParaTxt());
            writer.newLine();
        }
    }
}
