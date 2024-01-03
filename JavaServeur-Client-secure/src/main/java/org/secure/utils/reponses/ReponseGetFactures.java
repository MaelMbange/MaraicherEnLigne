package org.secure.utils.reponses;

import org.secure.utils.donnees.Facture;
import org.secure.utils.interfaces.Reponse;

import java.util.List;

public class ReponseGetFactures implements Reponse {
    private List<Facture> factureList;

    public ReponseGetFactures(List<Facture> factureList) {
        this.factureList = factureList;
    }

    public List<Facture> getFactureList() {
        return factureList;
    }
}
