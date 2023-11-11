package org.example.gui.components;

public class Usuario {
    private String email;
    private String senha;

    public Usuario(String email, String senha){
        this.email = email;
        this.senha = senha;
    }

    public Boolean logar() {
        if (email.equals("monitoons@monitoons.com") && senha.equals("12345678")) {
            return true;
        } else {
            return false;
        }
    }
}
