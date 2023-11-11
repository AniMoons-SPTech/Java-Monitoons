import org.example.gui.App;
import org.example.gui.components.Usuario;

import java.util.Scanner;

public class AppTeste {
    public static void main(String[] args) {
        App app = new App();
        app.setTitle("Monitoons");
        app.setResizable(false);

        Scanner scanner = new Scanner(System.in);
        Boolean logou = false;

        do {
            System.out.println("Informe o seu email");
            String email = scanner.nextLine();

            System.out.println("Informe a sua senha");
            String senha = scanner.nextLine();

            app.getLoginPanel().setCampoEmailText(email);
            app.getLoginPanel().setCampoSenhaText(senha);
            logou = app.getLoginPanel().executarBotao();
            if (logou) {
                System.out.println("Seja bem vindo!");
                System.out.println(logou);
            } else {
                System.out.println("Usuario ou senha incorretos!");
                System.out.println(logou);
            }
        }while(!logou);

    }
}
