

import components.MonitoramentoDoodleMsSQL;
import components.MonitoramentoDoodleMySQL;
import components.Usuario;
import gui.App;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class AppTeste {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        Boolean logou = false;

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Modo console");
            String plano = "";
            Usuario usuario;
            do {
                System.out.println("Informe o seu email");
                String email = scanner.nextLine();

                System.out.println("Informe a sua senha");
                String senha = scanner.nextLine();

                usuario = new Usuario(email, senha);
                plano = usuario.logar();
                if (plano != null) {
                    System.out.println("Seja bem vindo!");
                    logou = true;
                } else {
                    System.out.println("Usuario ou senha incorretos!");
                }
            }while(!logou);
            Usuario finalUsuario = usuario;
            if(logou){
                Timer timer = new Timer();
                MonitoramentoDoodleMySQL monitoramentoDoodleMySQL = new MonitoramentoDoodleMySQL();
                MonitoramentoDoodleMsSQL monitoramentoDoodleMsSQL = new MonitoramentoDoodleMsSQL();
                TimerTask tarefa = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            monitoramentoDoodleMySQL.comecarMonitoramentoDoodle(finalUsuario);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            monitoramentoDoodleMsSQL.comecarMonitoramentoDoodle(finalUsuario);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                timer.scheduleAtFixedRate(tarefa, 0, 5000);

            }
        } else {
            App app = new App();
            app.setTitle("Monitoons");
            app.setResizable(false);
        }
    }
}
