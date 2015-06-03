package vista;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import main.DataGestServer;

public class TrayGUI {

    public TrayGUI() {

        final TrayIcon trayIcon;

        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();

            ImageIcon im = new ImageIcon(TrayGUI.class.getResource("icono.png"));
            Image image = Toolkit.getDefaultToolkit().getImage("icono.png");

            ActionListener listener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String action = e.getActionCommand();
//                    DataGestServer.detener();
                    if (action.equals("Salir")) {
                        System.exit(0);
                    }
                }
            };

            PopupMenu popup = new PopupMenu();

            MenuItem item1 = new MenuItem("Salir");

            item1.addActionListener(listener);
            popup.add(item1);

            trayIcon = new TrayIcon(im.getImage(), "DataGest-Server", popup);

            ActionListener actionListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(e.getActionCommand());
                    String titulo = "Estado: ";
                    String estado;

                    if (DataGestServer.estado > 0) {
                        estado = "Descargando";
                    } else {
                        estado = "En espera";
                    }
                    trayIcon.displayMessage(titulo,
                            estado,
                            TrayIcon.MessageType.NONE);
                }
            };

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);

            try {
                tray.add(trayIcon);
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Tray no soportado por el sistema");
        }
    }
}
