package Hilos;

import entidades.Edicto;
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
public class HiloPdf extends Thread {

    List<Edicto> edictos;

    public HiloPdf(List<Edicto> ed) {
        this.edictos = ed;
    }

    @Override
    public void run() {
        if (edictos.size() > 0) {
            try {
                System.out.println("Inicio descarga Pdf");
                DataGestServer.estado++;
                procesoPDF();
                DataGestServer.estado--;
                System.out.println("Fin descarga Pdf");
            } catch (SQLException ex) {
                reinicio();
                Logger.getLogger(HiloPdf.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                reinicio();
                Logger.getLogger(HiloPdf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método que descarga los Edictos en PDF y almacena sus datos en la BBDD.
     *
     * @throws SQLException
     * @throws IOException
     */
    private void procesoPDF() throws SQLException, MalformedURLException, IOException {
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
            dw.descargaPDF(aux);

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

    private void reinicio() {
        System.out.println("Reiniciando Descargas");
        DataGestServer.estado--;
        String query = "UPDATE datagest.edicto SET estado=1 where estado=-1";
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
