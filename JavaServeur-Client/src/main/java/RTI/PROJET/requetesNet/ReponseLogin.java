package RTI.PROJET.requetesNet;

import rti.utils.Response;

public class ReponseLogin implements Response {
    private boolean reponse;
    private int id;

    public ReponseLogin(boolean reponse,int id){
        this.reponse = reponse;
        this.id = id;
    }

    public boolean getReponse(){
        return this.reponse;
    }
    public int getId(){
        return this.id;
    }
}
