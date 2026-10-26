package vehiculo;

public class Vehiculo {
    private static int numId = 1;
    private String id;
    private String nombre;
    private EstadoVehiculo estado;
    private double velocidad;
    private double temperaturaMotor;

    public Vehiculo(String nombre) {
        this.id = "V"+numId;
        this.nombre = nombre;
        this.estado = EstadoVehiculo.OPERATIVO;
        this.velocidad = 0.0;
        this.temperaturaMotor = 90.0; // Temperatura normal
        setNumId();
    }

    private void setNumId() {
        numId += 1;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public double getTemperaturaMotor() {
        return temperaturaMotor;
    }

    public void setTemperaturaMotor(double temperaturaMotor) {
        this.temperaturaMotor = temperaturaMotor;
    }
}
