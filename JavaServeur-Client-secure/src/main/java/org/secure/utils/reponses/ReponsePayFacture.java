package org.secure.utils.reponses;

import org.secure.utils.interfaces.Reponse;

public class ReponsePayFacture implements Reponse {
    private boolean succeed;

    public ReponsePayFacture(boolean succeed) {
        this.succeed = succeed;
    }

    public boolean isSucceed() {
        return succeed;
    }
}
