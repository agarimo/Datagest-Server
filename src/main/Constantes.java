package main;

import java.io.*;

/**
 * Clase encapsuladora para constantes del sistema.
 *
 * @author Agárimo
 */
public class Constantes {

    /*
     * MySql.
     */
    public static String rutaBBDD = "localhost";
    public static String usuario = "server";
    public static String pass = "server";
    /*
     * Edictos testra.
     */
    public static String url = "https://sedeapl.dgt.gob.es/WEB_TTRA_CONSULTA/ServletVisualizacion?params=";
    public static String html = "&formato=HTML";
    public static String pdf = "%26subidioma%3Des&formato=PDF";
    String[] lista = {rutaBBDD, usuario, pass, url, html, pdf};

    /**
     * Constructor simple de clase.
     */
    public Constantes() {
    }

    /**
     * Método que almacena las constantes en un archivo de configuración.
     *
     * @throws IOException
     */
    public void guardarConstantes() throws IOException {
        String[] titulo = {"Ruta    ", "Usuario ", "Pass    ", "url     ", "html    ", "pdf     "};
        File archivo = new File("configuracion.conf");

        if (!archivo.exists()) {
            BufferedWriter out = new BufferedWriter(new FileWriter(archivo));

            for (int i = 0; i < lista.length; i++) {
                out.write(titulo[i] + " ==\"" + lista[i] + "\"");
                out.write("\r\n");
            }
            out.close();
        }
    }

    /**
     * Método que carga las constantes desde un archivo de configuración.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void cargarConstantes() throws FileNotFoundException, IOException {
        File archivo = new File("configuracion.conf");
        BufferedReader in = new BufferedReader(new FileReader(archivo));
        String[] list = new String[6];
        String linea;

        for (int i = 0; i < list.length; i++) {
            linea = in.readLine();
            String[] split = linea.split("==");
            linea = split[1].replace("\"", "");
            list[i] = linea;
        }

        rutaBBDD = list[0];
        usuario = list[1];
        pass = list[2];
        url = list[3];
        html = list[4];
        pdf = list[5];
    }
}
