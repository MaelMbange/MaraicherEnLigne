package RTI.PROJET.protocoles;

import RTI.PROJET.bean.JavaServerDAL;
import RTI.PROJET.requetesNet.*;
import RTI.PROJET.requetesSQL.*;
import rti.utils.*;

import java.net.Socket;
import java.util.HashMap;

public class protocolePayement implements Protocol {
    private Logs logger;
    private JavaServerDAL jsd;

    public protocolePayement(Logs logger,String address) throws Exception {
        this.logger = logger;
        this.jsd = JavaServerDAL.getFactory(address);
    }

    @Override
    public String getName() {
        return "protocolePayement";
    }

    @Override
    public synchronized Response treatment(Request request, Socket socket) throws EndConnexionException {
        if(request instanceof RequeteLogin)
            System.out.println("peanuts");
        if(request instanceof RequeteLogout)
            System.out.println("peanuts");
        if(request instanceof RequeteGetFactures)
            System.out.println("peanuts");
        if(request instanceof RequetePayFacture)
            System.out.println("peanuts");

        return null;
    }

    private synchronized ReponseLogin traiteRequeteLogin(RequeteLogin request, Socket socket) throws EndConnexionException{

    }

    /*private synchronized ReponseLogout traiteRequeteLogout(RequeteLogout request, Socket socket) throws EndConnexionException{

    }

    private synchronized ReponseGetFactures traiteRequeteGetFactures(RequeteGetFactures request, Socket socket) throws EndConnexionException{

    }

    private synchronized ReponsePayFacture traiteRequetePayFacture(RequetePayFacture request, Socket socket) throws EndConnexionException{

    }*/
}
