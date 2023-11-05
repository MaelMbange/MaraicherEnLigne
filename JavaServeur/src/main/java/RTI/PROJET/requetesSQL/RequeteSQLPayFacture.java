package RTI.PROJET.requetesSQL;

public class RequeteSQLPayFacture implements RequeteSQL {
    private int IdFacture;


    public RequeteSQLPayFacture(int idFacture){
        this.IdFacture = idFacture;
    }

    @Override
    public String getRequest() {
        return "update factures set paye = 1 where Id = " + this.IdFacture + " ;";
    }
}
