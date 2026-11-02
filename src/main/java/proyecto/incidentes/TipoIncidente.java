package proyecto.incidentes;

public enum TipoIncidente {
    FALLO_ELECTRICO("Alimentación y vallas eléctricas"),
    PUERTA_ATASCADA("Puertas de contención"),
    FUGA("Perímetro y detección de movimiento"),
    TORMENTA("Estación meteorológica"),
    AVERIA_VEHICULO("Flota de vehículos"),
    ACCESO_NO_AUTORIZADO("Control de accesos");

    private final String nombreSistema;

    TipoIncidente(String nombreSistema) {
        this.nombreSistema = nombreSistema;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    public boolean requiereMedidaPrincipal() {
        return this == FALLO_ELECTRICO || this == TORMENTA || this == AVERIA_VEHICULO;
    }
}
