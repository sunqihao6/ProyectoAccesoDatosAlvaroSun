package proyecto.analizador;

/** Datos de la zona afectada extraídos del fichero parque.xml. */
class ZonaXml {
    final String id;
    final String nombre;
    final String estado;

    ZonaXml(String id, String nombre, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
}
