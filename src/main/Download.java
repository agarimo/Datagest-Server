package main;

import entidades.Descarga;
import entidades.Edicto;
import entidades.Origen;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * Clase que gestiona la descarga de edictos del testra.
 *
 * @author Agárimo
 */
public class Download {

    static int ARRAY_SIZE = 16384;
    static int BUFFER_SIZE = 1024;

    //<editor-fold defaultstate="collapsed" desc="Descarga html y comprobación">
    /**
     * Método que comprueba una ruta de edicto pasada como parámetro y la
     * inserta en la base de datos una vez comprobada.
     *
     * @param descarga Descarga asociada al Edicto.
     * @throws MalformedURLException
     * @throws IOException
     * @throws SQLException
     */
    public void comprobar(Descarga descarga) throws MalformedURLException, IOException, SQLException {
        Sql bd = new Sql(DataGestServer.con);
        String datos = descargaHTML(generaEnlace(descarga.getParametros(), false));
        String idEdicto, origen, tipo, csv;
        csv=getCsv(datos);

        if (!"INVALIDO".equals(csv)) {
            idEdicto = getId(datos);
            origen = splitOrigen(idEdicto);
            tipo = getTipo(datos);
            csv = getCsv(datos);

            descarga.setCsv(csv);
            bd.ejecutar(descarga.editaDescarga());

            Edicto aux = new Edicto(idEdicto, descarga.getIdDescarga(), origen, descarga.getFecha(), tipo);
            aux.setEstado(1);
            Origen orig = new Origen(origen, "desconocido");


            if (bd.buscar(orig.buscaOrigen()) == -1) {
                bd.ejecutar(orig.creaOrigen());
            }
            if (bd.buscar(aux.buscaBoletin()) == 0) {
                bd.ejecutar(aux.creaBoletin());
            }

            if (bd.buscar(aux.buscaEdicto()) == -1) {
                bd.ejecutar(aux.creaEdicto());
            }
        } else {
            descarga.setEstado(4);
            bd.ejecutar(descarga.editaDescarga());
        }
        bd.close();
    }

    /**
     * Método para la descarga de un documento html.
     *
     * @param enlace Link del documento html.
     * @return Devuelve un string con el contenido del documento html.
     * @throws MalformedURLException
     * @throws IOException
     */
    private String descargaHTML(String enlace) throws MalformedURLException, IOException {
        String inputLine;
        URL link = new URL(enlace);

        BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));
        StringBuilder buffer = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
            buffer.append(System.getProperty("line.separator"));
        }
        return buffer.toString();
    }

    /**
     * Método que extrae el id del edicto del documento pasado como parámetro.
     *
     * @param datos Edicto a comprobar.
     * @return devuelve un String con el id del Edicto.
     */
    private String getId(String datos) {
        String str = "";
        int a = datos.indexOf(". ");

        if (a > 0) {
            str = datos.substring(a + 2, a + 24 - 3);
        }
        return str;
    }

    private String getCsv(String datos) {
        String patron = "CSV: [A-Z0-9]{6}-[A-Z0-9]{6}-[A-Z0-9]{6}-[A-Z0-9]{6}";
        String str;

        str = Regex.buscar(patron, datos);
        str = str.replace("CSV: ", "");

        return str;
    }

    /**
     * Método que extrae el origen del id del edicto.
     *
     * @param idEdicto id del edicto.
     * @return String con el código del origen.
     */
    private String splitOrigen(String idEdicto) {
        String[] aux = idEdicto.split("-");
        return aux[1];
    }

    /**
     * Método que extrae el tipo de edicto.
     *
     * @param datos Edicto a comprobar
     * @return devuelve un String con el tipo de Edicto.
     */
    private String getTipo(String datos) throws SQLException {
        String tipo = "A revisar";

        if (datos.contains("Notificaciones de denuncia")) {
            tipo = "*711*";
        } else if (datos.contains("Resoluciones de sanciones")) {
            tipo = "*3.x*";
        } else if (datos.contains("Resoluciones de recursos")) {
            tipo = "*751*";
        }

        return tipo;

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Descarga pdf y conversión">
    /**
     * Método que almacena el contenido de un edicto en la BBDD.
     *
     * @param aux edicto a almacenar.
     * @return 
     * @throws SQLException
     */
    public boolean almacenaPDF(Edicto aux) throws SQLException {
        boolean correcto = false;
        Descarga des = new Descarga(aux.getIdDescarga());
        Sql bd = new Sql(DataGestServer.con);
        des = bd.getDescarga(des);
        String documento = convertirPDF("edictos\\sinprocesar\\" + aux.getRutaEdicto());

        if ("Header is corrupt".equals(documento)) {
            des.setDatos(documento);
            des.setEstado(4);
            bd.ejecutar(des.editaDescarga());
            borrarEdicto(aux);
        } else {
            documento = documento.replace("'", "´");
            des.setDatos(documento);
            des.setEstado(2);
            bd.ejecutar(des.editaDescarga());
            moverEdicto(aux);
            correcto = true;
        }
        bd.close();
        return correcto;
    }

    /**
     * Método que descarga en PFD el edicto pasado como parámetro.
     *
     * @param aux Edicto a descargar.
     * @throws MalformedURLException
     * @throws IOException
     * @throws SQLException
     */
    public void descargaPDF(Edicto aux) throws MalformedURLException, IOException, SQLException {
        Descarga descarga;
        Sql bd = new Sql(DataGestServer.con);
        descarga = bd.getDescarga(new Descarga(aux.getIdDescarga()));
        bd.close();

        File directorio = new File("edictos\\sinprocesar\\" + aux.getRuta());
        File fichero = new File("edictos\\sinprocesar\\" + aux.getRutaEdicto());
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        URL link = new URL(generaEnlace(descarga.getParametros(), true));
        URLConnection connection = link.openConnection();

        InputStream in = connection.getInputStream();

        OutputStream out = new DataOutputStream(new FileOutputStream(fichero));
        byte[] buffer = new byte[BUFFER_SIZE];
        int sizeRead;

        while ((sizeRead = in.read(buffer)) >= 0) {
            out.write(buffer, 0, sizeRead);
        }
        in.close();
        out.close();
    }

    /**
     * Método que convierte un PDF a texto plano y lo devuelve en un String.
     *
     * @param ruta Edicto a convertir.
     * @return Devuelve un String con el contenido del PDF en texto plano.
     * @throws IOException
     */
    private String convertirPDF(String ruta) {
        String pdfFile = ruta;
        String parseado = "";
        PDDocument document = null;
        try {
            try {
                document = PDDocument.load(pdfFile);
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                parseado = stripper.getText(document);
            } catch (Exception e) {
                String aux = e.getMessage();

                if (aux.contains("Header is corrupt")) {
                    return "Header is corrupt";
                } else {
//                    document = PDDocument.load(pdfFile);
                    parseado = aux;
                }

            }

        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException ex) {
                    Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return parseado;
    }

    /**
     * Método que mueve un edicto dado de la carpeta sin procesar a la carpeta
     * procesado
     *
     * @param aux Edicto a mover.
     * @throws IOException
     */
    private void moverEdicto(Edicto aux) {
        FileInputStream in = null;
        try {
            File outDir = new File("edictos\\procesado\\" + aux.getRuta());
            File inFile = new File("edictos\\sinprocesar\\" + aux.getRutaEdicto());
            File outFile = new File("edictos\\procesado\\" + aux.getRutaEdicto());
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            in = new FileInputStream(inFile);
            FileOutputStream out = new FileOutputStream(outFile);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
            if (inFile.exists()) {
                inFile.delete();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void borrarEdicto(Edicto aux) {
        File inFile = new File("edictos\\sinprocesar\\" + aux.getRutaEdicto());

        if (inFile.exists()) {
            inFile.delete();
        }
    }
    //</editor-fold>

    /**
     * Método que genera un enlace en base al tipo pasado como parámetro.
     *
     * @param param parámetros del enlace.
     * @param tipo tipo de enlace (True=Pdf, False=Html).
     * @return
     */
    public String generaEnlace(String param, boolean tipo) {
        if (tipo) {
            return main.Constantes.url + param + main.Constantes.pdf;
        } else {
            return main.Constantes.url + param + main.Constantes.html;
        }
    }
}
