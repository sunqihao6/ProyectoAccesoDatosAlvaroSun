package proyecto.analizador;

public class AnalizadorMain {
    public static void main(String[] args) {
        System.out.println("Iniciando Análisis Forense de Jurassic Park...");
        AnalizadorForense analizador = new AnalizadorForense();
        analizador.analizarYGenerarInforme();
        System.out.println("Análisis finalizado. Revisa la carpeta /salida");
    }
}
