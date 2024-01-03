package org.secure.utils.requetes;

import org.secure.utils.interfaces.Requete;

public class RequeteLogin implements Requete {
    private String login;
    private String password;

    public RequeteLogin(String login, String password){
        this.login = login;
        this.password = password;
    }

    public String getLogin(){
        return this.login;
    }

    public String getPassword() {
        return password;
    }
}
