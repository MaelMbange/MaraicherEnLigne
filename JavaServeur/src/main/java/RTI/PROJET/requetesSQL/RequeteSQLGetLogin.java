package RTI.PROJET.requetesSQL;

public class RequeteSQLGetLogin implements RequeteSQL {
    private String Table;
    private String Login;
    private String Password;

    public RequeteSQLGetLogin(String table, String login, String password){
        this.Table = table;
        this.Login = login;
        this.Password = password;
    }

    @Override
    public String getRequest() {
        return "select * from " + this.Table + " where login like '" + this.Login + "' and password like '" + this.Password + "' ;";
    }
}
