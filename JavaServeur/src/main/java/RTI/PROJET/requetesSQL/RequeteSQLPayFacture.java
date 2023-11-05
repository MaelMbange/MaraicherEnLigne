package RTI.PROJET.requetesSQL;

public class RequeteSQLPayFacture implements RequeteSQL {
    private String Table;
    private int IdFacture;
    private String NomFacturation;
    private long NumeroVisa;


    public RequeteSQLPayFacture(String table, int idFacture, String nomFacturation, long numeroVisa){
        this.Table = table;
        this.IdFacture = idFacture;
        this.NomFacturation = nomFacturation;
        this.NumeroVisa = numeroVisa;
    }

    @Override
    public String getRequest() {
        return "update " + this.Table + " set paye = 1 where Id = " + this.IdFacture + " ;";
    }
}
