package RTI.PROJET.requetesNet;

import rti.utils.Response;

public class ReponseLogin implements Response {
    private boolean reponse;

    public ReponseLogin(boolean reponse){
        this.reponse = reponse;
    }

    public boolean getReponse(){
        return this.reponse;
    }
}
