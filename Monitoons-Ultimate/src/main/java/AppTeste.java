
import componentsUltimate.MonitoramentoUltimateMsSQL;
import componentsUltimate.MonitoramentoUltimateMySQL;
import gui.App;
import gui.Usuario;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class AppTeste {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        Boolean logou = false;
        Usuario usuario;

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Bem vindo ao Monitoons! \n");
            String plano = "";
            do {
                System.out.println("Informe o seu email");
                String email = scanner.nextLine();

                System.out.println("Informe a sua senha");
                String senha = scanner.nextLine();

                usuario = new Usuario(email, senha);
                plano = usuario.logar();
                System.out.println(plano);
                if (plano != null) {
                    System.out.println("Seja bem vindo!");
                    logou = true;
                } else {
                    System.out.println("gui.Usuario ou senha incorretos!");
                }
            } while (!logou);
            if (logou) {
                Timer timer = new Timer();
                Usuario finalUsuario = usuario;
                MonitoramentoUltimateMySQL monitoramentoUltimateMySQL = new MonitoramentoUltimateMySQL();
                MonitoramentoUltimateMsSQL monitoramentoUltimateMsSQL = new MonitoramentoUltimateMsSQL();
                TimerTask tarefa = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            monitoramentoUltimateMySQL.comecarMonitoramentoUltimate(finalUsuario);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            monitoramentoUltimateMsSQL.comecarMonitoramentoUltimate(finalUsuario);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                timer.scheduleAtFixedRate(tarefa, 0, 15000);
            }
        } else {
            App app = new App();
            app.setTitle("Monitoons");
            app.setResizable(false);
        }
    }
}
