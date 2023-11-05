package RTI.PROJET.requetesSQL;

public class RequeteSQLGetFactures implements RequeteSQL {
    private int IdClient;

    public RequeteSQLGetFactures(int idClient){
        this.IdClient = idClient;
    }

    @Override
    public String getRequest() {
        return "select * from factures where idClient = " + this.IdClient + " ;";
    }
}
