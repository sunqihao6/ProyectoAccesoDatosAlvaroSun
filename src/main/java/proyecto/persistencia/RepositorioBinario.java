package proyecto.persistencia;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class RepositorioBinario {
    public static final int TAM_REGISTRO = 37;
    private final String rutaFichero;

    public RepositorioBinario(String ruta) {
        this.rutaFichero = ruta;
        crearDirectorioSiNoExiste(Paths.get(ruta));
    }

    private void crearDirectorioSiNoExiste(Path ruta) {
        try {
            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }
    }

    public void altaRegistro(String idSensor, String idZona, byte estadoSensor,
                             double valorPrincipal, long idEvento) throws IOException {
        try (RandomAccessFile fichero = new RandomAccessFile(rutaFichero, "rw")) {
            fichero.seek(fichero.length());
            fichero.write(formatearString(idSensor, 10));
            fichero.write(formatearString(idZona, 10));
            fichero.writeByte(estadoSensor);
            fichero.writeDouble(valorPrincipal);
            fichero.writeLong(idEvento);
        }
    }

    private byte[] formatearString(String texto, int longitud) {
        byte[] salida = new byte[longitud];
        Arrays.fill(salida, (byte) ' ');
        byte[] entrada = texto.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(entrada, 0, salida, 0, Math.min(entrada.length, longitud));
        return salida;
    }
}
