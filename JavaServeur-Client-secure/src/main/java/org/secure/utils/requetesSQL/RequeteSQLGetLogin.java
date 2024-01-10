package org.secure.utils.requetesSQL;

public class RequeteSQLGetLogin implements RequeteSQL {
    private String Login;
    private String Password;

    public RequeteSQLGetLogin(String login, String password){
        this.Login = login;
        this.Password = password;
    }

    @Override
    public String getRequest() {
        return "SELECT EXISTS (SELECT 1 FROM clients WHERE login = '" + this.Login + "' AND password = '" + this.Password + "'), id , password " +
                "FROM clients WHERE login = '" + this.Login + "' AND password = '" + this.Password + "'";
    }
}
