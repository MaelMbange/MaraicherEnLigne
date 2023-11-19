package RTI.PROJET.requetesNet;


import RTI.PROJET.serverBase.utils.Response;

public class NewReponse implements Response {
    private String Header;
    private String content;


    public NewReponse(String header, String content){
        this.Header = header;
        this.content = content;
    }

    public String getHeader(){
        return Header;
    }
    public String getContent(){
        return content;
    }
}
