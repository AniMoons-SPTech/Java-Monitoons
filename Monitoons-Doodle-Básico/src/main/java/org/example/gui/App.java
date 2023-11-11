package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class App extends JFrame {

    private LoginPanel loginPanel;
    private CapturaPanel capturaPanel;

    public App() {
        this.setBounds(0, 0, 1200, 700);


        CardLayout controleTela = new CardLayout();
        JPanel telas = new JPanel(controleTela);

        loginPanel = new LoginPanel(telas, this);
        capturaPanel = new CapturaPanel(telas, this);
        capturaPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                capturaPanel.iniciarCapturas();
            }
        });

        telas.add(loginPanel, "Tela Login");
        telas.add(capturaPanel, "Tela Principal");


        this.add(telas);

        this.setVisible(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // janela - TV
        // login - canal 1
        // principal canal 2
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public CapturaPanel getCapturaPanel() {
        return capturaPanel;
    }

    public void setCapturaPanel(CapturaPanel capturaPanel) {
        this.capturaPanel = capturaPanel;
    }
}
