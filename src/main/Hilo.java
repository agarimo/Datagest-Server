package main;

import entidades.Descarga;
import entidades.Edicto;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo de descarga y procesado de Edictos.
 *
 * @author Agarimo
 */
public class Hilo extends Thread {

    List<Descarga> descargas;
    List<Edicto> edictos;

    public Hilo(List<Descarga> ds, List<Edicto> ed) {
        this.descargas = ds;
        this.edictos = ed;
    }

    @Override
    /**
     * Constructor de clase.
     */
    public void run() {
        try {
            System.out.println("inicio descarga");
            DataGestServer.estado++;
            procesoHTML();
            procesoPDF();
            DataGestServer.estado--;
            System.out.println("fin de descarga");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            reinicio();
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reinicio() {
        System.out.println("Reiniciando descarga");
        DataGestServer.estado--;
        try {
            reset();
        } catch (SQLException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    /**
     * Método que procesa una descarga y extrae la información del Edicto a
     * través de HTML.
     *
     * @throws SQLException
     * @throws MalformedURLException
     * @throws IOException
     */
    private void procesoHTML() throws SQLException, MalformedURLException, IOException {
        System.out.println("procesoHTML");
        Descarga aux;
        Download dw = new Download();
        Iterator it = descargas.iterator();
        int contador = 1;
        int total = descargas.size();

        while (it.hasNext()) {
            System.out.println("Html " + contador + " de " + total);

            aux = (Descarga) it.next();
            System.out.println(aux.getParametros());
            dw.comprobar(aux);
            aux.setEstado(1);
            Sql bd = new Sql(DataGestServer.con);
            bd.ejecutar(aux.editaDescarga());
            bd.close();
            contador++;
        }
    }

    /**
     * Método que descarga los Edictos en PDF y almacena sus datos en la BBDD.
     *
     * @throws SQLException
     * @throws IOException
     */
    private void procesoPDF() throws SQLException, MalformedURLException {
        System.out.println("procesoPDF");
        Edicto aux;
        Download dw = new Download();
        Iterator it = edictos.iterator();
        int contador = 1;
        int total = edictos.size();

        while (it.hasNext()) {
            System.out.println("Pdf " + contador + " de " + total);
            aux = (Edicto) it.next();
            System.out.println(aux.getIdEdicto());
            try {
                dw.descargaPDF(aux);
            } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (dw.almacenaPDF(aux)) {
                aux.setEstado(2);
            } else {
                aux.setEstado(4);
            }
            Sql bd = new Sql(DataGestServer.con);
            bd.ejecutar(aux.editaEdicto());
            bd.close();
            contador++;
        }
    }

    public void reset() throws SQLException {
        resetDownload();
        resetEdictos();
    }

    private void resetDownload() throws SQLException {
        String query = "UPDATE datagest.descarga SET estado=0 where estado=-1";
        Sql bd = new Sql(DataGestServer.con);
        bd.ejecutar(query);
        bd.close();
    }

    private void resetEdictos() throws SQLException {
        String query = "UPDATE datagest.edicto SET estado=1 where estado=-1";
        Sql bd = new Sql(DataGestServer.con);
        bd.ejecutar(query);
        bd.close();
    }
}
