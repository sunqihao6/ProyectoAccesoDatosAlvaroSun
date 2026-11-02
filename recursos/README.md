# Jurassic Park: simulador y análisis forense

Repositorio: [ProyectoAccesoDatosAlvaroSun](https://github.com/sunqihao6/ProyectoAccesoDatosAlvaroSun)

## Descripción

Aplicación de consola que simula la vigilancia de zonas de Jurassic Park. Cada zona tiene un sensor que genera mediciones; cuando se detecta un incidente, el sistema registra evidencias en ficheros TXT, BIN y XML. Una segunda aplicación lee esos ficheros y genera un diagnóstico forense.

El proyecto contiene dos aplicaciones independientes:

- **Simulador:** `proyecto.simulador.SimuladorMain`
- **Analizador forense:** `proyecto.analizador.AnalizadorMain`

## Incidentes y zonas

| Incidente | Zona | Sensor asociado |
|---|---|---|
| Tormenta | Z01 - Centro de Control | S01 - Racha de viento |
| Fuga | Z02 - Herbívoros | S02 - Digital |
| Puerta atascada | Z03 - Velociraptores | S03 - Digital |
| Fallo eléctrico | Z04 - T. Rex | S04 - Tensión |
| Acceso no autorizado | Z05 - Laboratorio | S05 - Digital |
| Avería de vehículo | Z06 - Mantenimiento | S06 - Temperatura del motor |

Los sensores digitales no generan una medida numérica: el fichero BIN guarda `NaN` para esos incidentes. Las medidas de tensión, temperatura del motor y racha de viento se guardan como valores `double`.

## Ficheros de datos

El simulador guarda los datos de entrada en `datos/`:

- **`operaciones.txt`** registra el identificador del evento, la marca temporal y el tipo de incidente. Por ejemplo:

  ```text
  EVT-1 | 02-11-2026--14:41:50 | AVERIA_VEHICULO

## Funcionamiento interno

### Simulador

`SimuladorMain` crea el parque, inicializa los repositorios TXT, BIN y XML, y elimina los datos de simulaciones anteriores. Después guarda el estado inicial del parque en `datos/parque.xml` y comienza a inspeccionar zonas al azar.

En cada ciclo:

1. Elige una zona y obtiene su sensor.
2. Llama a `Sensor.medir(random)`, que genera una lectura según el tipo de sensor:
    - Tensión: lectura normal entre 215 y 230; lectura anómala entre 0 y 100.
    - Temperatura del motor: normal entre 80 y 110; anómala entre 120 y 150.
    - Racha de viento: normal entre 0 y 90; anómala entre 101 y 131.
    - Digital: devuelve `NaN`, porque no mide una magnitud numérica.
3. Llama a `detectarIncidente(...)`. Si detecta un incidente, se lo entrega a `GestorIncidentes` y termina el turno mediante `EventoCriticoException`. Si no detecta ninguno, continúa hasta completar 200 ciclos.

`SimuladorPrueba` sirve para generar un incidente de forma controlada. Se elige el tipo en `incidenteAProvocar`; después obtiene la zona correspondiente, pide al sensor una medida anómala fija y llama a `GestorIncidentes`.

### Registro de un incidente

`GestorIncidentes.provocarIncidente(...)` comprueba primero si el incidente requiere una medida numérica. Después determina la zona y el estado que debe registrar.

Para una avería de vehículo, por ejemplo, crea el vehículo `Explorer 01`, lo marca como averiado, le asigna la temperatura recibida y registra la zona Z06 en alerta.

El método `registrarEvidencias(...)` realiza las escrituras:

1. Crea un `Evento`, que contiene el tipo de incidente y la marca temporal.
2. Cambia el estado de la zona afectada.
3. Llama a `RepositorioTXT.registrarEvento(...)` para añadir el incidente a `operaciones.txt`.
4. Llama a `RepositorioXML.guardarEstadoParque(...)` para guardar el estado de las zonas y, si corresponde, el vehículo.
5. Llama a `RepositorioBinario.altaRegistro(...)` para añadir el sensor, su zona, su estado, la medida y el ID del evento a `sensores.bin`.

Una vez registradas las evidencias, `GestorIncidentes` lanza `EventoCriticoException`. El simulador la captura, informa de la emergencia y finaliza.

### Analizador forense

`AnalizadorMain` crea `AnalizadorForense` y llama a `analizarYGenerarInforme()`. El analizador elimina primero los resultados anteriores de `salida/` y luego cruza los tres ficheros de `datos/`:

1. `leerIncidente(...)` interpreta el ID, la fecha y el tipo de incidente de `operaciones.txt`.
2. `leerZonaAfectada(...)` lee `parque.xml` y busca la única zona cuyo estado no sea `OPERATIVA`.
3. `buscarSensor(...)` lee el único registro de 37 bytes de `sensores.bin`. Comprueba que el ID del evento y el ID de zona coincidan con los del TXT y el XML.
4. Comprueba que el sensor esté operativo y llama a `validarMedida(...)` para verificar que la medida sea coherente con el incidente.
5. `crearResolucion(...)` reúne los datos confirmados y selecciona las acciones recomendadas.
6. `generarInforme(...)` escribe `salida/informe.txt`.
7. `generarXmlAfectados(...)` debe crear `salida/elementos_afectados.xml` con la zona afectada y, si procede, el vehículo.
8. `copiarEvidencias(...)` escribe en `salida/evidencias.bin` los bytes del registro del sensor que participó en el diagnóstico.

Si se produce un error en el análisis, el bloque `catch` llama a `escribirInformeIndeterminado(...)`, que escribe el motivo en `salida/informe.txt`.

### Flujo resumido

```text
SimuladorMain o SimuladorPrueba
              │
              ▼
       GestorIncidentes
         ├── operaciones.txt  (incidente)
         ├── parque.xml       (zonas y estados)
         └── sensores.bin     (sensor y medición)
                                │
                                ▼
                         AnalizadorMain
                                │
                                ▼
                       AnalizadorForense
                         ├── informe.txt
                         ├── evidencias.bin
                         └── elementos_afectados.xml