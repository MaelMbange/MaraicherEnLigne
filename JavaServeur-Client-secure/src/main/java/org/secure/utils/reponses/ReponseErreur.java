package org.secure.utils.reponses;

import org.secure.utils.interfaces.Reponse;

public class ReponseErreur implements Reponse {
    private String error;

    public ReponseErreur(String error){
        this.error = error;
    }
    public String getError(){
        return this.error;
    }
}
