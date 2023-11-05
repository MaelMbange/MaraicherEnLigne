package RTI.PROJET.requetesSQL;

public class RequeteSQLGetFactures implements RequeteSQL {
    private String Table;
    private int IdClient;

    public RequeteSQLGetFactures(String table, int idClient){
        this.Table = table;
        this.IdClient = idClient;
    }

    @Override
    public String getRequest() {
        return "select * from " + this.Table + " where idClient = " + this.IdClient + " ;";
    }
}
