package Hilos;

import entidades.Descarga;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.DataGestServer;
import main.Download;
import main.Sql;

/**
 *
 * @author Agárimo
 */
public class HiloHtml extends Thread {

    List<Descarga> descargas;

    public HiloHtml(List<Descarga> ds) {
        this.descargas = ds;
    }

    @Override
    public void run() {
        if (descargas.size() > 0) {
            try {
                System.out.println("Inicio descarga HTML");
                DataGestServer.estado++;
                procesoHTML();
                DataGestServer.estado--;
                System.out.println("Fin descarga HTML");
            } catch (Exception ex) {
                reinicio();
                Logger.getLogger(HiloHtml.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    private void reinicio() {
        System.out.println("Reiniciando Descargas");
        DataGestServer.estado--;
        String query = "UPDATE datagest.descarga SET estado=0 where estado=-1";
        Sql bd;

        try {
            bd = new Sql(DataGestServer.con);
            bd.ejecutar(query);
            bd.close();
        } catch (SQLException ex) {
            Logger.getLogger(HiloHtml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
