package proyecto.persistencia;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proyecto.Parque;
import proyecto.vehiculo.Vehiculo;
import proyecto.zonas.Zona;
import proyecto.zonas.recinto.Recinto;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class RepositorioXML {

    private final String rutaFichero;

    public RepositorioXML(String ruta) {
        this.rutaFichero = ruta;
    }

    public void guardarEstadoParque(Parque parque) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Elemento raíz <parque id="JP01">
            Element rootElement = doc.createElement("parque");
            rootElement.setAttribute("id", parque.getId());
            doc.appendChild(rootElement);

            // Recorrer las zonas y serializarlas
            for (Zona zona : parque.getZonas()) {
                Element zonaElement = doc.createElement("zona");
                zonaElement.setAttribute("id", zona.getId());
                zonaElement.setAttribute("nombre", zona.getNombre());
                zonaElement.setAttribute("estado", zona.getEstado().name());

                if (zona.getRecintos() != null) {
                    for (Recinto recinto : zona.getRecintos()) {
                        Element recintoElement = doc.createElement("recinto");
                        recintoElement.setAttribute("id", recinto.getId());
                        recintoElement.setAttribute("nombre", recinto.getNombre());
                        recintoElement.setAttribute("estado", recinto.getEstado().name());
                        zonaElement.appendChild(recintoElement);
                    }
                }
                rootElement.appendChild(zonaElement);
            }

            // Recorrer los vehículos del parque
            if (parque.getVehiculos() != null && !parque.getVehiculos().isEmpty()) {
                Element vehiculosElement = doc.createElement("vehiculos");
                for (Vehiculo v : parque.getVehiculos()) {
                    Element vehiculoElement = doc.createElement("vehiculo");
                    vehiculoElement.setAttribute("id", v.getId());
                    vehiculoElement.setAttribute("nombre", v.getNombre());
                    vehiculoElement.setAttribute("estado", v.getEstado().name());
                    vehiculoElement.setAttribute("velocidad", String.valueOf(v.getVelocidad()));
                    vehiculoElement.setAttribute("temperaturaMotor", String.valueOf(v.getTemperaturaMotor()));
                    vehiculosElement.appendChild(vehiculoElement);
                }
                rootElement.appendChild(vehiculosElement);
            }

            File archivo = new File(rutaFichero);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

        } catch (Exception e) {
            System.err.println("Error al generar el XML: " + e.getMessage());
        }
    }
}
