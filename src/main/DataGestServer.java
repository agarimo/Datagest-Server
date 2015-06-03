package main;

import Hilos.HiloHtml;
import Hilos.HiloPdf;
import entidades.Conexion;
import entidades.Descarga;
import entidades.Edicto;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vista.TrayGUI;

/**
 *
 * @author Agárimo
 */
public class DataGestServer {

    public static Conexion con;
    public static int estado = 0;
    public static TrayGUI tray;

    public static void main(String[] args) {

        keyStore();
        creaDirectorios();
        cargarConstantes();
//        con = new Conexion("LocalHost", "localhost", "3306", "admin", "admin");

        if (testCon()) {
            tray = new TrayGUI();
            try {
                comprobar();
            } catch (SQLException ex) {
                Logger.getLogger(DataGestServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void keyStore() {
        System.setProperty("javax.net.ssl.trustStore", "keystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "Carras-24");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    /**
     * Método que inicia un hilo de procesado.
     */
    private static void iniciaHiloHtml(List descargas) {
        HiloHtml html = new HiloHtml(descargas);
        html.start();
    }

    private static void iniciaHiloPdf(List edictos) {
        HiloPdf pdf = new HiloPdf(edictos);
        pdf.start();
    }

    /**
     * Método que comprueba si existen nuevos edictos para procesar e inicia el
     * Hilo de descarga si fuera necesario.
     */
    private static void comprobar() throws SQLException {
        Sql bd = new Sql(con);
        List<Descarga> descargas = bd.listaDescarga(Descarga.listaDescarga(0));
        List<Edicto> edictos = bd.listaEdicto(Edicto.listaEdicto(1));
        bd.close();

        if (descargas.size() > 0 || edictos.size() > 0) {
            setEnUso(descargas, 0);
            iniciaHiloHtml(descargas);
            setEnUso(edictos, 1);
            iniciaHiloPdf(edictos);
            if (estado < 5) {
                comprobar();
            } else {
                System.out.println("en espera");
                while (estado > 4) {
                    esperar();
                }
            }
        } else {
//            System.out.println("NO HAY EDICTOS PARA DESCARGAR");
            System.out.println(".......INICIANDO ESPERA......");
            if (estado == 0) {
                limpiarDirectorio(new File("edictos\\sinprocesar\\"));
            }
            esperar();
        }
        comprobar();
    }

    public static void limpiarDirectorio(File directorio) {
        File[] ficheros = directorio.listFiles();

        for (int i = 0; i < ficheros.length; i++) {
            if (ficheros[i].isDirectory()) {
                limpiarDirectorio(ficheros[i]);
            }
            ficheros[i].delete();
        }
    }

    /**
     * Método que establece el estado a -1 (en uso) los elementos pasados como
     * parámetros.
     *
     * @param descargas Lista de elementos a establecer.
     * @param tipo tipo de descargas 0=descarga 1=edicto.
     * @throws SQLException
     */
    private static void setEnUso(List lista, int tipo) throws SQLException {
        Descarga descarga;
        Edicto edicto;
        Sql bd = new Sql(DataGestServer.con);
        Iterator it = lista.iterator();

        while (it.hasNext()) {

            switch (tipo) {
                case 0:
                    descarga = (Descarga) it.next();
                    descarga.setEstado(-1);
                    bd.ejecutar(descarga.editaDescarga());
                    break;
                case 1:
                    edicto = (Edicto) it.next();
                    edicto.setEstado(-1);
                    bd.ejecutar(edicto.editaEdicto());
                    break;
            }
        }
        bd.close();
    }

    public static void creaDirectorios() {
        File edictoP = new File("edictos\\procesado");
        if (!edictoP.exists()) {
            edictoP.mkdirs();
        }

        File edictoS = new File("edictos\\sinprocesar");
        if (!edictoS.exists()) {
            edictoS.mkdirs();
        }
    }

    private static void cargarConstantes() {
        try {
            Constantes cn = new Constantes();
            Class.forName("com.mysql.jdbc.Driver");
            cn.guardarConstantes();
            cn.cargarConstantes();
        } catch (IOException ex) {
            Logger.getLogger(DataGestServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataGestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean testCon() {
        boolean bool = false;
        try {
            Connection cone = DriverManager.getConnection(con.getRuta(), con.getUsuario(), con.getPass());
            bool = true;
            cone.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataGestServer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "No se puede conectar al servidor " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return bool;
    }

    public static String entrecomillar(String contenido) {
        return "'" + contenido + "'";
    }

    public static String imprimeFecha(Date cal) {
        String date;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        date = formato.format(cal);

        return date;
    }

    private static void esperar() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataGestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
