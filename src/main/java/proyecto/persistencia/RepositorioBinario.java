package proyecto.persistencia;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RepositorioBinario {
    private static final int TAM_REGISTRO = 44; //tamaño de registro fijo
    private final String rutaFichero;

    public RepositorioBinario(String ruta) {
        this.rutaFichero = ruta;
        crearDirectorioSiNoExiste(Paths.get(ruta));
    }

    private void crearDirectorioSiNoExiste(Path ruta) {
        try {
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }
    }

    public static long posicionRegistro(long indice) {
        return indice * TAM_REGISTRO;
    }

    // Metodo para modificar exclusivamente el valor principal (inicia en el byte 20)
    public void modificarValor(long indice, double nuevoValor) throws IOException {
        try (RandomAccessFile fichero = new RandomAccessFile(rutaFichero, "rw")) {
            long posicion = posicionRegistro(indice);
            fichero.seek(posicion + 20);
            fichero.writeDouble(nuevoValor);
        }
    }

    // Metodo para leer exclusivamente el valor principal
    public double leerValor(long indice) throws IOException {
        try (RandomAccessFile fichero = new RandomAccessFile(rutaFichero, "r")) {
            long posicion = posicionRegistro(indice);
            fichero.seek(posicion + 20);
            return fichero.readDouble();
        }
    }

    // Metodo para dar de alta un nuevo registro al final del fichero
    public void altaRegistro(long marcaTemporal, int idSensor, int idZona, byte tipoMedicion,
                             byte estado, short reservado, double valorPrincipal,
                             double valorAuxiliar, long idEvento) throws IOException {

        try (RandomAccessFile fichero = new RandomAccessFile(rutaFichero, "rw")) {
            // Nos posicionamos al final del fichero para no sobrescribir registros anteriores
            fichero.seek(fichero.length());

            // Escribimos los campos en el orden exacto para cumplir los 44 bytes
            fichero.writeLong(marcaTemporal);      // 8 bytes
            fichero.writeInt(idSensor);            // 4 bytes
            fichero.writeInt(idZona);              // 4 bytes
            fichero.writeByte(tipoMedicion);       // 1 byte
            fichero.writeByte(estado);             // 1 byte
            fichero.writeShort(reservado);         // 2 bytes
            fichero.writeDouble(valorPrincipal);   // 8 bytes (Inicia en el byte 20)
            fichero.writeDouble(valorAuxiliar);    // 8 bytes
            fichero.writeLong(idEvento);           // 8 bytes
        }
    }
}
