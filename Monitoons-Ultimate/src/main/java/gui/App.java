package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class App extends JFrame {
    public App() {
        this.setBounds(0, 0, 1200, 700);


        CardLayout controleTela = new CardLayout();
        JPanel telas = new JPanel(controleTela);

        LoginPanel loginPanel = new LoginPanel(telas, this);
        CapturaPanel capturaUltimatePanel = new CapturaPanel(telas, this);
        capturaUltimatePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Entrei no plano Ultimate");
                try {
                    capturaUltimatePanel.iniciarCapturasUltimate(loginPanel.getUsuario());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        telas.add(loginPanel, "Tela Login");
        telas.add(capturaUltimatePanel, "Tela Monitoramento Ultimate");


        this.add(telas);

        this.setVisible(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
