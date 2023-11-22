

import components.Monitoramento;
import components.Usuario;
import gui.App;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;

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
            if(logou){
                Monitoramento monitoramento = new Monitoramento();
                monitoramento.comecarMonitoramentoDoodle(usuario);
            }
        } else {
            App app = new App();
            app.setTitle("Monitoons");
            app.setResizable(false);
        }
    }
}
