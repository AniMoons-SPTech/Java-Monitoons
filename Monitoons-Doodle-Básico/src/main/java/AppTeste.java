import org.example.gui.App;
import org.example.gui.components.Usuario;
import org.example.gui.services.ComponenteService;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class AppTeste {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Boolean logou = false;

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Modo console");
            do {
                System.out.println("Informe o seu email");
                String email = scanner.nextLine();

                System.out.println("Informe a sua senha");
                String senha = scanner.nextLine();

                Usuario usuario = new Usuario(email, senha);

                if (usuario.logar()) {
                    System.out.println("Seja bem vindo!");
                    logou = true;
                } else {
                    System.out.println("Usuario ou senha incorretos!");
                }
            }while(!logou);
            if(logou){
                ComponenteService componenteService = new ComponenteService(1);
            }
        } else {
            App app = new App();
            app.setTitle("Monitoons");
            app.setResizable(false);
        }
    }
}
