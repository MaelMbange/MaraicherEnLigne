package org.secure.utils.requetes;

import org.secure.utils.interfaces.Requete;

public class RequeteGetFactures implements Requete {

    private int IdClient;

    public RequeteGetFactures(int idClient){
        this.IdClient = idClient;
    }

    public int getIdClient() {
        return IdClient;
    }
}
