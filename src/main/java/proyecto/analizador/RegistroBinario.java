package proyecto.analizador;

/** Registro de sensor leído del fichero binario, con sus bytes originales. */
class RegistroBinario {
    final String idSensor;
    final String idZona;
    final byte estadoSensor;
    final double medidaPrincipal;
    final byte[] bytesRegistro;

    RegistroBinario(String idSensor, String idZona, byte estadoSensor,
                    double medidaPrincipal, byte[] bytesRegistro) {
        this.idSensor = idSensor;
        this.idZona = idZona;
        this.estadoSensor = estadoSensor;
        this.medidaPrincipal = medidaPrincipal;
        this.bytesRegistro = bytesRegistro;
    }
}
