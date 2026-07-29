package mk.pallatidasmave.whms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

/**
 * Pika hyrese e aplikacionit WHMS Desktop.
 * Ky aplikacion punon si nje server lokal i vogel (Spring Boot) qe hapet automatikisht
 * ne shfletuesin e paracaktuar te kompjuterit - pa kerkuar internet apo instalim MySQL.
 * Te dhenat ruhen ne nje skedar lokal (dosja "whms-data") prane vete aplikacionit.
 */
@SpringBootApplication
public class WhmsApplication {

    public static void main(String[] args) {
        // Nisim ne menyre "headless=false" qe te lejohet hapja e shfletuesit dhe System Tray
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(WhmsApplication.class, args);
    }

    /**
     * Kur serveri i brendshem (Tomcat) nis me sukses, hap automatikisht shfletuesin
     * ne adresen lokale te aplikacionit dhe (nese sistemi operativ e mbeshtet) shton
     * nje ikone ne System Tray per ta mbyllur aplikacionin lehte.
     */
    @Component
    static class DesktopLauncher {

        @EventListener
        public void onWebServerReady(WebServerInitializedEvent event) {
            int port = event.getWebServer().getPort();
            String url = "http://127.0.0.1:" + port + "/";
            openBrowser(url);
            setupSystemTray(url, event.getApplicationContext());
        }

        private void openBrowser(String url) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    System.out.println("Hapni manualisht shfletuesin ne: " + url);
                }
            } catch (Exception e) {
                System.out.println("Nuk u hap automatikisht shfletuesi. Hapeni manualisht ne: " + url);
            }
        }

        private void setupSystemTray(String url, WebServerApplicationContext context) {
            if (!SystemTray.isSupported()) return;
            try {
                SystemTray tray = SystemTray.getSystemTray();
                Image icon = createTrayIconImage();

                PopupMenu menu = new PopupMenu();

                MenuItem openItem = new MenuItem("Hap WHMS ne shfletues");
                openItem.addActionListener(e -> openBrowser(url));
                menu.add(openItem);

                menu.addSeparator();

                MenuItem exitItem = new MenuItem("Mbyll Aplikacionin");
                exitItem.addActionListener(e -> {
                    tray.remove(tray.getTrayIcons()[0]);
                    if (context instanceof ConfigurableApplicationContext configurableContext) {
                        configurableContext.close();
                    }
                    System.exit(0);
                });
                menu.add(exitItem);

                TrayIcon trayIcon = new TrayIcon(icon, "Pallati i Dasmave - WHMS", menu);
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(e -> openBrowser(url));
                tray.add(trayIcon);
            } catch (Exception e) {
                // System Tray eshte opsionale - nese deshton, aplikacioni vazhdon normalisht ne shfletues
                System.out.println("System Tray nuk eshte i disponueshem ne kete sistem (jo problem, aplikacioni punon normalisht).");
            }
        }

        /** Krijon nje ikone te thjeshte (rreth bordo me shkronjat "PD") direkt ne memorie, pa pasur nevoje per skedar te jashtem. */
        private Image createTrayIconImage() {
            int size = 32;
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x7C2A3A));
            g.fillOval(0, 0, size, size);
            g.setColor(new Color(0xD8BD86));
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            String text = "PD";
            int tx = (size - fm.stringWidth(text)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, tx, ty);
            g.dispose();
            return img;
        }
    }
}
