package proyecto.analizador;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import proyecto.incidentes.TipoIncidente;
import proyecto.persistencia.RepositorioBinario;
import proyecto.zonas.EstadoZona;
import proyecto.zonas.sensor.EstadoSensor;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//Junta incidente (TXT), estado de zonas (XML) y sensor asociado (BIN)
public class AnalizadorForense {
    private static final DateTimeFormatter FORMATO_MARCA_TEMPORAL =
            DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm:ss");

    private String directorioDatos = "datos";
    private String directorioSalida = "salida";

    public void analizarYGenerarInforme() {
        Path txt = Paths.get(directorioDatos, "operaciones.txt");
        Path bin = Paths.get(directorioDatos, "sensores.bin");
        Path xml = Paths.get(directorioDatos, "parque.xml");
        limpiarSalidas();

        try {
            EventoTxt evento = leerIncidente(txt);
            ZonaXml zona = leerZonaAfectada(xml);
            RegistroBinario sensor = buscarSensor(bin, evento.idEvento, zona.id);

            if (sensor.estadoSensor != EstadoSensor.OPERATIVO.getCodigo()) {
                throw new IOException("El sensor registrado en BIN no está operativo.");
            }
            validarMedida(evento.incidente, sensor.medidaPrincipal);

            ResolucionIncidentes resolucion = crearResolucion(evento, zona, sensor);
            generarInforme(resolucion, evento.marcaTemporal);
            generarXmlAfectados(resolucion, xml);
            copiarEvidencias(sensor);
            System.out.println("Diagnóstico confirmado al cruzar TXT, XML y BIN: " + evento.incidente);
            System.out.println("Zona: " + zona.id + " (" + zona.nombre + "); sensor: " + sensor.idSensor);

        } catch (Exception e) {
            String motivo = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            escribirInformeIndeterminado(motivo);
            System.out.println("Diagnóstico indeterminado: " + motivo);
        }
    }

    private EventoTxt leerIncidente(Path ruta) throws IOException {

        if (!Files.isRegularFile(ruta)){
            throw new IOException("No existe datos/operaciones.txt.");
        }

        EventoTxt evento = null;

        for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
            if (linea.isBlank()) {
                continue;
            }
            String[] campos = linea.split("\\|", -1);

            if (campos.length != 3 || !campos[0].trim().startsWith("EVT-")) {
                throw new IOException("Formato TXT inválido; se esperaba id|fecha|incidente, sin zona.");
            }
            if (evento != null) {
                throw new IOException("El TXT debe contener exactamente un incidente para cruzarlo con el estado actual del parque.");
            }
            try {
                String idTexto = campos[0].trim();
                long id = Long.parseLong(idTexto.substring(4));
                LocalDateTime fecha = LocalDateTime.parse(campos[1].trim(), FORMATO_MARCA_TEMPORAL);
                TipoIncidente incidente = TipoIncidente.valueOf(campos[2].trim());
                evento = new EventoTxt(id, fecha, incidente);
            } catch (RuntimeException e) {
                throw new IOException("No se pudo interpretar el incidente del TXT: " + linea, e);
            }
        }
        if (evento == null) {
            throw new IOException("El TXT debe contener exactamente un incidente para cruzarlo con el estado actual del parque.");
        }
        return evento;
    }

    private ZonaXml leerZonaAfectada(Path ruta) throws Exception {
        if (!Files.isRegularFile(ruta)){
            throw new IOException("No existe datos/parque.xml.");
        }
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document = builder.parse(ruta.toFile());

        NodeList zonas = document.getElementsByTagName("zona");
        List<ZonaXml> afectadas = new ArrayList<>();

        for (int i = 0; i < zonas.getLength(); i++) {
            Element elemento = (Element) zonas.item(i);
            String id = elemento.getAttribute("id");
            String nombre = elemento.getAttribute("nombre");
            String estado = elemento.getAttribute("estado");

            if (id.isBlank() || estado.isBlank()){
                throw new IOException("Hay una zona sin ID o estado en parque.xml.");
            }

            try {
                if (EstadoZona.valueOf(estado) != EstadoZona.OPERATIVA) {
                    afectadas.add(new ZonaXml(id, nombre, estado));
                }
            } catch (IllegalArgumentException e) {
                throw new IOException("Estado de zona desconocido en parque.xml: " + estado, e);
            }
        }
        if (afectadas.size() != 1) {
            throw new IOException("El XML debe identificar exactamente una zona no operativa; encontradas: " + afectadas.size() + ".");
        }
        return afectadas.get(0);
    }

    private RegistroBinario buscarSensor(Path ruta, long idEvento, String idZona) throws IOException {
        if (!Files.isRegularFile(ruta)) {
            throw new IOException("No existe datos/sensores.bin.");
        }
        byte[] bytesRegistro = Files.readAllBytes(ruta);
        if (bytesRegistro.length != RepositorioBinario.TAM_REGISTRO) {
            throw new IOException("sensores.bin debe contener exactamente un registro de "
                    + RepositorioBinario.TAM_REGISTRO + " bytes.");
        }

        String idSensor;
        String zonaRegistro;
        byte estadoSensor;
        double medidaPrincipal;
        long idRegistro;

        try (DataInputStream entrada = new DataInputStream(new ByteArrayInputStream(bytesRegistro))) {
            idSensor = leerTextoFijo(entrada, 10);
            zonaRegistro = leerTextoFijo(entrada, 10);
            estadoSensor = entrada.readByte();
            medidaPrincipal = entrada.readDouble();
            idRegistro = entrada.readLong();
        }

        if (idRegistro != idEvento || !zonaRegistro.equals(idZona)) {
            throw new IOException("El registro BIN no coincide con el evento del TXT y la zona del XML.");
        }

        if (idSensor.isBlank()) {
            throw new IOException("El registro BIN no contiene ID de sensor.");
        }

        return new RegistroBinario(idSensor, zonaRegistro, estadoSensor, medidaPrincipal, bytesRegistro);
    }

    private String leerTextoFijo(DataInputStream entrada, int longitud) throws IOException {
        byte[] bytes = new byte[longitud];
        entrada.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private void validarMedida(TipoIncidente incidente, double medida) throws IOException {
        if (incidente.requiereMedidaPrincipal() && !Double.isFinite(medida)) {
            throw new IOException("El incidente requiere una medida numérica del sensor.");
        }
        if (!incidente.requiereMedidaPrincipal() && !Double.isNaN(medida)) {
            throw new IOException("El incidente no debe incluir una medida que no se utiliza.");
        }
        switch (incidente) {
            case FALLO_ELECTRICO : {
                if (medida > 100.0){
                    throw new IOException("La tensión registrada no confirma una caída de suministro.");
                }
            }
            case AVERIA_VEHICULO : {
                if (medida < 120.0) {
                    throw new IOException("La temperatura registrada no alcanza el umbral de avería.");
                }
            }
            case TORMENTA : {
                if (medida <= 100.0) {
                    throw new IOException("La racha de viento registrada no supera el umbral de tormenta.");
                }
            }
            default : { }
        }
    }

    private ResolucionIncidentes crearResolucion(EventoTxt evento, ZonaXml zona, RegistroBinario sensor) {
        ResolucionIncidentes res = new ResolucionIncidentes(evento.incidente, zona.id, zona.nombre,
                zona.estado, sensor.idSensor, evento.incidente.getNombreSistema(),
                sensor.medidaPrincipal, evento.idEvento);

        switch (evento.incidente) {
            case FALLO_ELECTRICO : {
                res.agregarAccion("ACTIVAR_ALIMENTACION_DE_RESPALDO");
                res.agregarAccion("VERIFICAR_TENSION_SEGURA");
            }
            case PUERTA_ATASCADA : {
                res.agregarAccion("AISLAR_ACCESO");
                res.agregarAccion("SOLICITAR_MANTENIMIENTO");
            }
            case FUGA : {
                res.agregarAccion("AISLAR_AREA");
                res.agregarAccion("ACTIVAR_PROTOCOLO_DE_BUSQUEDA");
            }
            case TORMENTA : {
                res.agregarAccion("RESTRINGIR_ACCESOS_EXTERIORES");
                res.agregarAccion("INSPECCIONAR_DRENAJES_Y_VALLAS");
            }
            case AVERIA_VEHICULO : {
                res.agregarAccion("INMOVILIZAR_VEHICULO");
                res.agregarAccion("ENVIAR_ASISTENCIA_TECNICA");
            }
            case ACCESO_NO_AUTORIZADO : {
                res.agregarAccion("BLOQUEAR_CREDENCIAL_TEMPORALMENTE");
                res.agregarAccion("REVISAR_HISTORIAL_DE_ACCESOS");
            }
        }
        return res;
    }

    private void limpiarSalidas() {
        try {
            Files.createDirectories(Paths.get(directorioSalida));
            Files.deleteIfExists(Paths.get(directorioSalida, "informe.txt"));
            Files.deleteIfExists(Paths.get(directorioSalida, "elementos_afectados.xml"));
            Files.deleteIfExists(Paths.get(directorioSalida, "evidencias.bin"));
        } catch (IOException e) {
            System.err.println("No se pudieron limpiar las salidas anteriores: " + e.getMessage());
        }
    }

    private void generarInforme(ResolucionIncidentes res, LocalDateTime marcaTemporal) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(directorioSalida, "informe.txt"), StandardCharsets.UTF_8)) {
            writer.write("Estado del diagnóstico: CONFIRMADO"); writer.newLine();
            writer.write("Evento: EVT-" + res.getIdEvento()); writer.newLine();
            writer.write("Incidente (TXT): " + res.getIncidenteDetectado()); writer.newLine();
            writer.write("Fecha del incidente: " + marcaTemporal.format(FORMATO_MARCA_TEMPORAL)); writer.newLine();
            writer.write("Zona afectada (XML): " + res.getZonaAfectadaId() + " - " + res.getZonaAfectadaNombre()); writer.newLine();
            writer.write("Estado de la zona (XML): " + res.getEstadoZona()); writer.newLine();
            writer.write("Sistema comprometido (según incidente TXT): " + res.getSistemaComprometido()); writer.newLine();
            writer.write("Sensor asociado (BIN): " + res.getSensorId()); writer.newLine();
            writer.write("Medida principal del sensor (BIN): " + formatearMedida(res.getValorSensor())); writer.newLine();
            writer.write("Resolución recomendada:"); writer.newLine();
            for (String accion : res.getAccionesRecomendadas()) {
                writer.write(" - " + accion); writer.newLine();
            }
        }
    }

    private void escribirInformeIndeterminado(String motivo) {
        try {
            Files.createDirectories(Paths.get(directorioSalida));
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(directorioSalida, "informe.txt"), StandardCharsets.UTF_8)) {
                writer.write("Estado del diagnóstico: INDETERMINADO"); writer.newLine();
                writer.write("Motivo: " + motivo); writer.newLine();
                writer.write("No se ha podido confirmar el incidente cruzando TXT, XML y BIN."); writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir el informe: " + e.getMessage());
        }
    }

    private void copiarEvidencias(RegistroBinario registro) {
        try {
            Files.write(Paths.get(directorioSalida, "evidencias.bin"), registro.bytesRegistro);
        } catch (IOException e) {
            System.err.println("No se pudo escribir el registro relevante en evidencias.bin: " + e.getMessage());
        }
    }

    private void generarXmlAfectados(ResolucionIncidentes res, Path xmlOriginal) throws Exception {

        Document original = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlOriginal.toFile());
        Element parqueOriginal = original.getDocumentElement();
        Document salida = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element parqueAfectado = salida.createElement(parqueOriginal.getTagName());

        NamedNodeMap atributos = parqueOriginal.getAttributes();
        for (int i = 0; i < atributos.getLength(); i++) {
            parqueAfectado.setAttribute(atributos.item(i).getNodeName(), atributos.item(i).getNodeValue());
        }
        salida.appendChild(parqueAfectado);

        NodeList zonas = original.getElementsByTagName("zona");
        boolean zonaCopiada = false;

        for (int i = 0; i < zonas.getLength(); i++) {
            Element zona = (Element) zonas.item(i);

            if (zona.getAttribute("id").equals(res.getZonaAfectadaId())) {
                parqueAfectado.appendChild(salida.importNode(zona, true));
                zonaCopiada = true;
                break;
            }
        }
        if (!zonaCopiada){
            throw new IOException("No se encontró la zona afectada en el XML original.");
        }

        if (res.getIncidenteDetectado() == TipoIncidente.AVERIA_VEHICULO) {
            NodeList vehiculos = original.getElementsByTagName("vehiculos");
            if (vehiculos.getLength() > 0) parqueAfectado.appendChild(salida.importNode(vehiculos.item(0), true));
        }

        Files.createDirectories(Paths.get(directorioSalida));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(salida), new StreamResult(Paths.get(directorioSalida, "elementos_afectados.xml").toFile()));
    }

    private String formatearMedida(double medida) {
        return Double.isNaN(medida) ? "no-aplica" : Double.toString(medida);
    }

}
