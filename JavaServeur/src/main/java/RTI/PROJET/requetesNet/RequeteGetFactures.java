package RTI.PROJET.requetesNet;

import rti.utils.Request;

public class RequeteGetFactures implements Request {
    private int idClient;

    public RequeteGetFactures(int idClient){
        this.idClient = idClient;
    }

    public int getIdClient(){
        return this.idClient;
    }
}
