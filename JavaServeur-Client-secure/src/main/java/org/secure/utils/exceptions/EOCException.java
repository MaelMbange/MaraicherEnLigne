package org.secure.utils.exceptions;

import org.secure.utils.interfaces.Reponse;

public class EOCException extends Exception{
    private final Reponse reponse;

    public EOCException(Reponse reponse){
        super("Fin de connection du au protocole");
        this.reponse = reponse;
    }

    public Reponse getReponse() {
        return reponse;
    }
}
