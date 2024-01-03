package org.secure.utils.requetes;

import org.secure.utils.interfaces.Requete;

public class RequetePayFacture implements Requete {
    private int idFacture;
    private String NomCarte;
    private String NumeroCarte;

    public RequetePayFacture(int idFacture, String nomCarte, String numeroCarte) {
        this.idFacture = idFacture;
        NomCarte = nomCarte;
        NumeroCarte = numeroCarte;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public String getNomCarte() {
        return NomCarte;
    }

    public String getNumeroCarte() {
        return NumeroCarte;
    }
}
