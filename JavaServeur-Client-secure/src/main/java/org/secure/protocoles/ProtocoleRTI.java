package org.secure.protocoles;

import org.secure.utils.bean.JavaServerDAL;
import org.secure.utils.donnees.Facture;
import org.secure.utils.exceptions.EOCException;
import org.secure.utils.interfaces.Logs;
import org.secure.utils.interfaces.Protocol;
import org.secure.utils.interfaces.Reponse;
import org.secure.utils.interfaces.Requete;
import org.secure.utils.reponses.ReponseErreur;
import org.secure.utils.reponses.ReponseGetFactures;
import org.secure.utils.reponses.ReponseLogin;
import org.secure.utils.reponses.ReponsePayFacture;
import org.secure.utils.requetes.RequeteGetFactures;
import org.secure.utils.requetes.RequeteLogin;
import org.secure.utils.requetes.RequeteLogout;
import org.secure.utils.requetes.RequetePayFacture;
import org.secure.utils.requetesSQL.RequeteSQL;
import org.secure.utils.requetesSQL.RequeteSQLGetFactures;
import org.secure.utils.requetesSQL.RequeteSQLGetLogin;
import org.secure.utils.requetesSQL.RequeteSQLPayFacture;

import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProtocoleRTI implements Protocol {
    private Logs logger;
    private JavaServerDAL jsd;

    public ProtocoleRTI(Logs logger,String address) throws Exception {
        this.logger = logger;
        this.jsd = JavaServerDAL.getFactory(address);
    }

    @Override
    public String getName() {
        return "protocoleRTI";
    }

    @Override
    public synchronized Reponse analyse (Requete request) throws EOCException {
        Requete requete = (Requete)request;
        // --------------------------------
        try
        {
            if(requete instanceof RequeteLogin) {
                return traiteRequeteLogin((RequeteLogin)requete);
            }
            if(requete instanceof RequeteGetFactures) {
                return traiteRequeteGetFactures((RequeteGetFactures)requete);
            }
            if(requete instanceof RequetePayFacture){
                return traiteRequetePayFacture((RequetePayFacture)requete);
            }
            if(requete instanceof RequeteLogout){
                traiteRequeteLogout();
                return null;
            }
        }
        catch(RuntimeException | SQLException e){
            logger.writeLog( "SQL error : " + e.getMessage() + " !!! ici !!!");
        }
        return null;
    }

    private synchronized Reponse traiteRequeteLogin(RequeteLogin requete) throws EOCException, SQLException {
        logger.writeLog("REQUEST [LOGIN= RECIEVED]");
        ResultSet res = jsd.GET(new RequeteSQLGetLogin(requete.getLogin(),requete.getPassword()));
        if(res.next())
        {
            logger.writeLog("RESPONSE [LOGIN= " + String.valueOf(res.getBoolean(1)).toUpperCase() + "/" + res.getInt(2) + "]");
            return new ReponseLogin(res.getBoolean(1),res.getInt(2));
        }
        throw new EOCException(new ReponseErreur("Login Invalide"));
    }

    private synchronized void traiteRequeteLogout() throws EOCException{
        logger.writeLog("REQUEST [LOGOUT= RECIEVED] ");
        logger.writeLog("LOGOUT [STATUS= END_OF_CONNECTION] ");
        throw new EOCException(null);
    }

    private synchronized Reponse traiteRequeteGetFactures(RequeteGetFactures requete) throws SQLException {
        logger.writeLog("REQUEST [GET-FACTURES= RECIEVED]");

        ResultSet res = jsd.GET(new RequeteSQLGetFactures(requete.getIdClient()));
        // --------------------------------------------------------------
        List<Facture> factureList = new ArrayList<>();

        while(res.next()){
            System.out.println("id:" + res.getInt("Id"));
            System.out.println("date:" + res.getDate("date"));
            System.out.println("montant:" + res.getFloat("montant"));
            System.out.println("paye?:" + res.getInt("paye"));

            factureList.add(new Facture(res.getInt("Id"),res.getDate("date").toLocalDate(), res.getFloat("montant"),res.getInt("paye")));
        }
        logger.writeLog(factureList.toString());
        return new ReponseGetFactures(factureList);
    }

    private synchronized Reponse traiteRequetePayFacture(RequetePayFacture requete) throws SQLException {
        logger.writeLog("REQUEST [PAY-FACTURE= RECIEVED]");

        int rd = ThreadLocalRandom.current().nextInt(2)%2;
        if(rd == 1) {
            logger.writeLog("RESPONSE [PAY-FACTURE= FALSE]");
            return new ReponsePayFacture(false);
        }
        //return new ReponsePayFacture(false);
        int resultat = jsd.POSTUPDATE(new RequeteSQLPayFacture(requete.getIdFacture()));
        if(resultat > 0) {
            logger.writeLog("RESPONSE [PAY-FACTURE= TRUE]");
            return new ReponsePayFacture(true);
        }
        logger.writeLog("RESPONSE [PAY-FACTURE= FALSE]");
        return new ReponsePayFacture(false);
    }
}
