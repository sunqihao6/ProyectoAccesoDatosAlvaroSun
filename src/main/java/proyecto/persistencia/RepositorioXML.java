package proyecto.persistencia;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proyecto.Parque;
import proyecto.vehiculo.Vehiculo;
import proyecto.zonas.Zona;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RepositorioXML {

    private final String rutaFichero;

    public RepositorioXML(String ruta) {
        this.rutaFichero = ruta;
    }

    public void guardarEstadoParque(Parque parque) throws IOException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Elemento raíz parque
            Element rootElement = doc.createElement("parque");
            rootElement.setAttribute("id", parque.getId());
            doc.appendChild(rootElement);

            // Recorrer las zonas y serializarlas
            for (Zona zona : parque.getZonas()) {
                Element zonaElement = doc.createElement("zona");
                zonaElement.setAttribute("id", zona.getId());
                zonaElement.setAttribute("nombre", zona.getNombre());
                zonaElement.setAttribute("estado", zona.getEstado().name());

                rootElement.appendChild(zonaElement);
            }

            // El parque mantiene un único vehículo, creado cuando ocurre una avería.
            if (parque.getVehiculo() != null) {
                Element vehiculosElement = doc.createElement("vehiculos");
                Vehiculo v = parque.getVehiculo();
                Element vehiculoElement = doc.createElement("vehiculo");
                vehiculoElement.setAttribute("id", v.getId());
                vehiculoElement.setAttribute("nombre", v.getNombre());
                vehiculoElement.setAttribute("estado", v.getEstado().name());
                vehiculoElement.setAttribute("velocidad", String.valueOf(v.getVelocidad()));
                vehiculoElement.setAttribute("temperaturaMotor", String.valueOf(v.getTemperaturaMotor()));
                vehiculosElement.appendChild(vehiculoElement);
                rootElement.appendChild(vehiculosElement);
            }

            File archivo = new File(rutaFichero);
            if (archivo.getParentFile() != null) Files.createDirectories(archivo.getParentFile().toPath());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

        } catch (Exception e) {
            throw new IOException("Error al generar el XML: " + e.getMessage(), e);
        }
    }
}
