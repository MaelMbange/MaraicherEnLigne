package RTI.PROJET.serverBase.utils;

public class EndConnexionException extends Exception{
    private final Response response;

    public EndConnexionException(Response response){
        super("End of connexion decided by protocol");
        this.response = response;
    }

    public Response getResponse(){
        return this.response;
    }
}
