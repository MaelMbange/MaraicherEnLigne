package org.secure.utils.requetesSQL;

public class RequeteSQLGetLoginv2 implements RequeteSQL{
    private String Login;

    public RequeteSQLGetLoginv2(String login){
        this.Login = login;
    }

    @Override
    public String getRequest() {
        return "SELECT EXISTS (SELECT 1 FROM clients WHERE login = '" + this.Login + "'), id , password FROM clients WHERE login = '" + this.Login + "'";
    }
}
