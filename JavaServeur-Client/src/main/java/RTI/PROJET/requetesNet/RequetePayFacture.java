package RTI.PROJET.requetesNet;

import rti.utils.Request;

public class RequetePayFacture implements Request {
    private int idFacture;
    private String NomVISA;
    private String NumeroVISA;

    public RequetePayFacture(int idClient){
        this.idFacture = idClient;
    }

    public int getIdFacture(){
        return this.idFacture;
    }

    public String NomVISA(){
        return this.NomVISA;
    }

    public String NumeroVISA(){
        return this.NumeroVISA;
    }
}
