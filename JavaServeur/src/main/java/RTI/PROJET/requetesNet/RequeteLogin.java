package RTI.PROJET.requetesNet;


import rti.utils.Request;

public class RequeteLogin implements Request {
    private String login;
    private String password;

    public RequeteLogin(String login, String password){
        this.login = login;
        this.password = password;
    }

    public String getLogin(){
        return this.login;
    }

    public String getPassword(){
        return this.password;
    }
}
