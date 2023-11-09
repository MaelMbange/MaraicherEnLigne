package RTI.PROJET.requetesNet;

import rti.utils.Request;

public class NewRequest implements Request {
    private String Header;
    private String content;

    public NewRequest(String header, String content){
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
