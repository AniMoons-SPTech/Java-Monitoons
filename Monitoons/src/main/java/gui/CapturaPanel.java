package gui;

import componentsDoodle.MonitoramentoDoodleMsSQL;
import componentsDoodle.MonitoramentoDoodleMySQL;
import componentsSketchPro.MonitoramentoSketchMsSQL;
import componentsSketchPro.MonitoramentoSketchMySQL;
import componentsUltimate.MonitoramentoUltimateMsSQL;
import componentsUltimate.MonitoramentoUltimateMySQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CapturaPanel extends TelaPanel {
    public CapturaPanel(JPanel telas, JFrame janela) {
        super(telas, janela);
        JLabel txtMensagem = new JLabel("""
        Fique tranquilo, o Monitoons já está monitorando
        """);
        JLabel txtMensagem2 = new JLabel("""
                os componentes do seu computador!
                """);
        txtMensagem.setBounds(325, 205, 800, 35);
        txtMensagem2.setBounds(395, 240, 500, 35);
        txtMensagem.setForeground(Color.decode("#dcdcdc"));
        txtMensagem2.setForeground(Color.decode("#dcdcdc"));
        txtMensagem.setFont(new Font("Montserrat", Font.BOLD, 25));
        txtMensagem2.setFont(new Font("Montserrat", Font.BOLD, 25));
        this.add(txtMensagem);
        this.add(txtMensagem2);

    }
    public void executarBotao(ActionEvent e) {
        trocarTela("Tela Login");
    }
    public void iniciarCapturasDoodle(Usuario usuario) throws IOException, InterruptedException {
        Timer timer = new Timer();
        MonitoramentoDoodleMySQL monitoramentoDoodleMySQL = new MonitoramentoDoodleMySQL();
        MonitoramentoDoodleMsSQL monitoramentoDoodleMsSQL = new MonitoramentoDoodleMsSQL();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramentoDoodleMySQL.comecarMonitoramentoDoodle(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    monitoramentoDoodleMsSQL.comecarMonitoramentoDoodle(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.scheduleAtFixedRate(tarefa, 0, 5000);

    }
    public void iniciarCapturasSketchPro(Usuario usuario) throws IOException, InterruptedException {
        Timer timer = new Timer();
        MonitoramentoSketchMySQL monitoramentoSketchMySQL = new MonitoramentoSketchMySQL();
        MonitoramentoSketchMsSQL monitoramentoSketchMsSQL = new MonitoramentoSketchMsSQL();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramentoSketchMySQL.comecarMonitoramentoSketchPro(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    monitoramentoSketchMsSQL.comecarMonitoramentoSketchPro(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        timer.scheduleAtFixedRate(tarefa, 0, 5000);

    }
    public void iniciarCapturasUltimate(Usuario usuario) throws IOException, InterruptedException {
        Timer timer = new Timer();
        MonitoramentoUltimateMySQL monitoramentoUltimateMySQL = new MonitoramentoUltimateMySQL();
        MonitoramentoUltimateMsSQL monitoramentoUltimateMsSQL = new MonitoramentoUltimateMsSQL();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramentoUltimateMySQL.comecarMonitoramentoUltimate(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    monitoramentoUltimateMsSQL.comecarMonitoramentoUltimate(usuario);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.scheduleAtFixedRate(tarefa, 0, 5000);

    }
}
