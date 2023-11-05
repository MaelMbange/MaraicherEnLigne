package RTI.PROJET.requetesNet;

import rti.utils.Response;

public class ReponsePayFacture implements Response {
    private boolean resultat;

    public ReponsePayFacture(boolean resultat){
        this.resultat = resultat;
    }

    public boolean getResulat(){
        return this.resultat;
    }
}
