package RTI.PROJET.protocoles;

import RTI.PROJET.bean.JavaServerDAL;
import RTI.PROJET.requetesNet.*;
import RTI.PROJET.requetesSQL.*;
import RTI.PROJET.structureDonnees.Facture;
import rti.utils.*;

import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    private synchronized ReponseLogin traiteRequeteLogin(RequeteLogin request, Socket socket) throws EndConnexionException, SQLException {
        logger.writeLog("REQUEST [LOGIN= RECIEVED]");
        ResultSet res = jsd.GET(new RequeteSQLGetLogin(request.getLogin(), request.getPassword()));

        if(res.next())
        {
            logger.writeLog("RESPONSE [LOGIN= " + String.valueOf(res.getBoolean(1)).toUpperCase() + "/" + res.getInt(2) + "]");
            return new ReponseLogin(res.getBoolean(1),res.getInt(2));
        }
        logger.writeLog("RESPONSE [LOGIN= FALSE]");
        throw new EndConnexionException(new ReponseLogin(false,0));
    }

    private synchronized void traiteRequeteLogout(RequeteLogout request, Socket socket) throws EndConnexionException{
        logger.writeLog("REQUEST [LOGOUT= RECIEVED] ");
        logger.writeLog("LOGOUT [STATUS= END_OF_CONNECTION] ");
        throw new EndConnexionException(null);
    }

   private synchronized ReponseGetFactures traiteRequeteGetFactures(RequeteGetFactures request, Socket socket) throws EndConnexionException, SQLException {
        logger.writeLog("REQUEST [GET-FACTURE= RECIEVED]");
        ResultSet res = jsd.GET(new RequeteSQLGetFactures(request.getIdClient()));

       ReponseGetFactures rl;
       boolean Exist = false;
       List<Facture> lf = new ArrayList<>();
       while(res.next()){
           Exist = true;

           int idFacture = res.getInt("Id");
           java.sql.Date datesql = res.getDate("date");
           LocalDate date = datesql.toLocalDate();
           float montant = res.getFloat("montant");
           boolean paye = res.getBoolean("paye");
           lf.add(new Facture(idFacture,date,montant,paye));
       }
        if(Exist)
        {
            rl = new ReponseGetFactures(lf);
            logger.writeLog("RESPONSE [GET-FACTURE= " + String.valueOf(res.getBoolean(1)).toUpperCase() + "/" + res.getInt(2) + "]");
        }
        else
        {
            rl = new ReponseGetFactures(lf);
            logger.writeLog("RESPONSE [GET-FACTURE= NONE]");
        }
        return rl;
    }

    private synchronized ReponsePayFacture traiteRequetePayFacture(RequetePayFacture request, Socket socket) throws EndConnexionException, SQLException {
        logger.writeLog("REQUEST [PAY-FACTURE= RECIEVED]");

        int rd = ThreadLocalRandom.current().nextInt(2);
        if(rd == 1)
            return new ReponsePayFacture(false);
        int resultat = jsd.POSTUPDATE(new RequeteSQLPayFacture(request.getIdFacture()));
        if(resultat > 0)
            return new ReponsePayFacture(true);
        return new ReponsePayFacture(false);
    }
}
