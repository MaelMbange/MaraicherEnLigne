package org.secure.utils.reponses;

import org.secure.utils.interfaces.Reponse;

public class ReponseLogin implements Reponse {
    private boolean succeed;
    private int idClient;

    public ReponseLogin(boolean succeed,int idClient) {
        this.succeed = succeed;
        this.idClient = idClient;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public int getIdClient() {
        return idClient;
    }
}
