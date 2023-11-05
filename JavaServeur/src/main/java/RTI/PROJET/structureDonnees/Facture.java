package RTI.PROJET.structureDonnees;

import java.time.LocalDate;

public class Facture {
    private int idFacture;
    private LocalDate date;
    private float montant;
    private boolean paye;

    public Facture(int idFacture,LocalDate date, float montant, boolean paye){
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

    public boolean getPaye(){
        return this.paye;
    }
}
