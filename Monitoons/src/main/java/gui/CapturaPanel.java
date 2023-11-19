package gui;

import componentsDoodle.Monitoramento;
import componentsDoodle.Usuario;
import componentsSketchPro.MonitoramentoSketch;
import componentsUltimate.MonitoramentoUltimate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.util.List;
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
        Monitoramento monitoramento = new Monitoramento();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramento.comecarMonitoramentoDoodle(usuario);
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
        MonitoramentoSketch monitoramentoSketch = new MonitoramentoSketch();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramentoSketch.comecarMonitoramentoSketchPro(usuario);
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
        MonitoramentoUltimate monitoramentoUltimate = new MonitoramentoUltimate();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    monitoramentoUltimate.comecarMonitoramentoUltimate(usuario);
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
