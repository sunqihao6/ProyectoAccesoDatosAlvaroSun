package proyecto;

import proyecto.vehiculo.Vehiculo;
import proyecto.zonas.sensor.TipoMedicionSensor;
import proyecto.zonas.Zona;

import java.util.ArrayList;
import java.util.List;

public class Parque {

    private String id;
    private List<Zona> zonas;
    private Vehiculo vehiculo;

    public Parque() {

        this.id="JP1";
        this.zonas = new ArrayList<>();
        inicializarZonas();
    }
    
    private void inicializarZonas() {
        zonas.add(new Zona("Z01", "Centro de Control", "S01", TipoMedicionSensor.RACHA_VIENTO));

        zonas.add(new Zona("Z02", "Herbívoros", "S02", TipoMedicionSensor.DIGITAL));

        zonas.add(new Zona("Z03", "Velociraptores", "S03", TipoMedicionSensor.DIGITAL));

        zonas.add(new Zona("Z04", "T. Rex", "S04", TipoMedicionSensor.TENSION));

        zonas.add(new Zona("Z05", "Laboratorio", "S05", TipoMedicionSensor.DIGITAL));

        zonas.add(new Zona("Z06", "Mantenimiento", "S06", TipoMedicionSensor.TEMPERATURA_MOTOR));
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    // busca una zona por su ID
    public Zona buscarZonaPorId(String idZona) {
        for (Zona zona : zonas) {
            if (zona.getId().equals(idZona)) {
                return zona;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
