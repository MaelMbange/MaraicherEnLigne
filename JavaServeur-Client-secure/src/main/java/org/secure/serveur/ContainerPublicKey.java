package org.secure.serveur;

import java.security.PublicKey;

public class ContainerPublicKey {
    private PublicKey clePublic;


    public synchronized void setClePublic(PublicKey clePublic){
        this.clePublic = clePublic;
    }

    public synchronized PublicKey getClePublic() {
        return clePublic;
    }
}
