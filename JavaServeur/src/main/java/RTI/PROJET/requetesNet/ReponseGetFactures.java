package RTI.PROJET.requetesNet;

import RTI.PROJET.structureDonnees.Facture;
import rti.utils.Response;

import java.util.ArrayList;
import java.util.List;

public class ReponseGetFactures implements Response {
    private List<Facture> factureList;

    public ReponseGetFactures(List<Facture> facturelist){
        this.factureList = facturelist;
    }

    public List<Facture> getListeFactures(){
        return this.factureList;
    }
}
