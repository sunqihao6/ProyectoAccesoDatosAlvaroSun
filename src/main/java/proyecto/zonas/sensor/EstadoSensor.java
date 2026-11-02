package proyecto.zonas.sensor;

public enum EstadoSensor {
    OPERATIVO(0);

    private final byte codigo;

    EstadoSensor(int codigo) {
        this.codigo = (byte) codigo;
    }

    public byte getCodigo() {
        return codigo;
    }

}
