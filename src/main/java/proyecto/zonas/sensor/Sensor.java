package proyecto.zonas.sensor;

import java.util.Random;

public class Sensor {
    private final String id;
    private final String nombre;
    private final String idZona;
    private final TipoMedicionSensor tipoMedicion;
    private EstadoSensor estado;

    public Sensor(String id, String nombre, String idZona, TipoMedicionSensor tipoMedicion) {
        this.id = id;
        this.nombre = nombre;
        this.idZona = idZona;
        this.tipoMedicion = tipoMedicion;
        this.estado = EstadoSensor.OPERATIVO;
    }

    public String getId() {
        return id;
    }

    public String getIdZona() {
        return idZona;
    }

    public TipoMedicionSensor getTipoMedicion() {
        return tipoMedicion;
    }

    public EstadoSensor getEstado() {
        return estado;
    }



    public double medir(Random random) {
        boolean anomalia = random.nextInt(100) < 3;
        switch (tipoMedicion) {
            case TENSION: {
                if (anomalia) {
                    return random.nextDouble() * 100.0;
                } else {
                    return 215.0 + random.nextDouble() * 15.0;
                }
            }
            case TEMPERATURA_MOTOR :{
                if(anomalia){
                    return 120.0 + random.nextDouble() * 30.0;
                } else{
                    return 80.0 + random.nextDouble() * 30.0;
                }
            }
            case RACHA_VIENTO :{
                if(anomalia){
                    return 101.0 + random.nextDouble() * 30.0;
                } else{
                    return random.nextDouble() * 90.0;
                }
            }
            case DIGITAL :{
                return Double.NaN;
            }
        }
        return Double.NaN;
    }

    //Para el simuladorPrueba
    public double medirConAnomalia() {
        switch (tipoMedicion) {
            case TENSION: {
                return 0.0;
            }
            case TEMPERATURA_MOTOR: {
                return 125.0;
            }
            case RACHA_VIENTO: {
                return 120.0;
            }
            case DIGITAL: {
                return Double.NaN;
            }
        }

        return Double.NaN;
    }
}
