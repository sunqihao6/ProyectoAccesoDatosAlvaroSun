import vehiculo.Vehiculo;
import zonas.Zona;

import java.util.ArrayList;
import java.util.List;

public class Parque {

    private List<Zona> zonas;
    private List<Vehiculo> vehiculos;

    public Parque() {

        this.zonas = new ArrayList<>();
        this.vehiculos = new ArrayList<>();
        inicializarZonas();
    }

    // El documento indica 6 zonas estables (Z01 a Z06)
    private void inicializarZonas() {
        zonas.add(new Zona("Centro de Control")); // Z1
        zonas.add(new Zona("Herbívoros"));        // Z2
        zonas.add(new Zona("Velociraptores"));    // Z3
        zonas.add(new Zona("T. Rex"));            // Z4
        zonas.add(new Zona("Laboratorio"));       // Z5
        zonas.add(new Zona("Mantenimiento"));     // Z6
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        this.vehiculos.add(vehiculo);
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
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
}
