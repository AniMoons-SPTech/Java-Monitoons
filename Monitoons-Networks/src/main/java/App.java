import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Boolean logou = false;
        Usuario usuario;

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
                System.out.println("Usuario ou senha incorretos!");
            }
        } while (!logou);
        if (logou) {
            Timer timer = new Timer();
            Usuario finalUsuario = usuario;
            Monitoramento monitoramentoMySQL = new Monitoramento();
            MonitoramentoSQLServer monitoramentoSQLServer = new MonitoramentoSQLServer();
            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    try {
                        monitoramentoMySQL.comecarMonitoramento(finalUsuario);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        monitoramentoSQLServer.comecarMonitoramentoSQLServer(finalUsuario);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, 0, 15000);
        }
    }
}

