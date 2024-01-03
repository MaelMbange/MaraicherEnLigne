package org.secure.utils.donnees;

import java.io.Serializable;
import java.time.LocalDate;

public class Facture implements Serializable {
    private int idFacture;
    private LocalDate date;
    private float montant;
    private int paye;

    public Facture(int idFacture,LocalDate date, float montant, int paye){
        this.idFacture = idFacture;
        this.date = date;
        this.montant = montant;
        this.paye = paye;
    }

    public int getIdFacture(){
        return this.idFacture;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public float getMontant(){
        return this.montant;
    }

    public int getPaye(){
        return this.paye;
    }

    @Override
    public String toString() {
        return "Facture:" +
                "idFacture=" + idFacture +
                ", date=" + date +
                ", montant=" + montant +
                ", paye=" + paye;
    }
}
