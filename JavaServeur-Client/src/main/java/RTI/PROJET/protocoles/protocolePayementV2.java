package RTI.PROJET.protocoles;

import RTI.PROJET.bean.JavaServerDAL;
import RTI.PROJET.requetesNet.*;
import RTI.PROJET.requetesSQL.RequeteSQLGetFactures;
import RTI.PROJET.requetesSQL.RequeteSQLGetLogin;
import RTI.PROJET.requetesSQL.RequeteSQLPayFacture;
import RTI.PROJET.structureDonnees.Facture;
import rti.utils.*;

import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class protocolePayementV2 implements Protocol {
    private Logs logger;
    private JavaServerDAL jsd;

    public protocolePayementV2(Logs logger,String address) throws Exception {
        this.logger = logger;
        this.jsd = JavaServerDAL.getFactory(address);
    }

    @Override
    public String getName() {
        return "protocolePayementV2";
    }

    @Override
    public Response treatment(Request request, Socket socket) throws EndConnexionException {
        NewRequest requete = (NewRequest)request;
        logger.writeLog("Requete = " + requete.getHeader() + " : " + requete.getContent());
        try
        {
            if(requete.getHeader().equals(NewMessageDataType.LOGIN)) {
                return traiteRequeteLogin(requete);
            }
            if(requete.getHeader().equals(NewMessageDataType.LOGOUT)) {
                traiteRequeteLogout();
            }
            if(requete.getHeader().equals(NewMessageDataType.GET_FACTURES)){
                return traiteRequeteGetFactures(requete);
            }
            if(requete.getHeader().equals(NewMessageDataType.PAY_FACTURE)){
                return traiteRequetePayFacture(requete);
            }
        }
        catch(RuntimeException | SQLException e){
            logger.writeLog( "SQL error : " + e.getMessage() + " !!! ici !!!");
        }
        return null;
    }

    private synchronized NewReponse traiteRequeteLogin(NewRequest request) throws EndConnexionException, SQLException {
        logger.writeLog("REQUEST [LOGIN= RECIEVED]");

        String[] split = request.getContent().split("/");
        ResultSet res = jsd.GET(new RequeteSQLGetLogin(split[0],split[1]));

        if(res.next())
        {
            logger.writeLog("RESPONSE [LOGIN= " + String.valueOf(res.getBoolean(1)).toUpperCase() + "/" + res.getInt(2) + "]");
            return new NewReponse("LOGIN",res.getBoolean(1)+"/"+res.getInt(2));
        }
        logger.writeLog("RESPONSE [LOGIN= FALSE]");
        throw new EndConnexionException(new NewReponse(NewMessageDataType.LOGIN,"false/0"));
    }

    private synchronized void traiteRequeteLogout() throws EndConnexionException{
        logger.writeLog("REQUEST [LOGOUT= RECIEVED] ");
        logger.writeLog("LOGOUT [STATUS= END_OF_CONNECTION] ");
        throw new EndConnexionException(new NewReponse(NewMessageDataType.LOGOUT,""));
    }

    private synchronized NewReponse traiteRequeteGetFactures(NewRequest request) throws SQLException {
        logger.writeLog("REQUEST [GET-FACTURES= RECIEVED]");

        String[] split = request.getContent().split("/");
        ResultSet res = jsd.GET(new RequeteSQLGetFactures(Integer.parseInt(split[0])));
        // --------------------------------------------------------------
        String content = "";
        boolean Exist = false;
        while(res.next()){
            Exist = true;
            System.out.println("id:" + res.getInt("Id"));
            System.out.println("date:" + res.getDate("date"));
            System.out.println("montant:" + res.getFloat("montant"));
            System.out.println("paye?:" + res.getInt("paye"));

            content += res.getInt("Id") + "/" + res.getDate("date").toLocalDate() + "/" +
                    res.getFloat("montant") + "/" + (res.getInt("paye") == 1) + "/";
        }
        if(!Exist) content = "false";
        logger.writeLog("RESPONSE [GET-FACTURES= "+content.toUpperCase()+"]");
        return new NewReponse(NewMessageDataType.GET_FACTURES,content);
    }

    private synchronized NewReponse traiteRequetePayFacture(NewRequest request) throws EndConnexionException, SQLException {
        logger.writeLog("REQUEST [PAY-FACTURE= RECIEVED]");

        String[] split = request.getContent().split("/");
        int rd = ThreadLocalRandom.current().nextInt(2)%2;
        if(rd == 1)
            return new NewReponse(NewMessageDataType.PAY_FACTURE,"false");
            //return new ReponsePayFacture(false);
        int resultat = jsd.POSTUPDATE(new RequeteSQLPayFacture(Integer.parseInt(split[0])));
        if(resultat > 0) {
            logger.writeLog("RESPONSE [PAY-FACTURE= TRUE]");
            return new NewReponse(NewMessageDataType.PAY_FACTURE, "true");
        }
            //return new ReponsePayFacture(true);
        //return new ReponsePayFacture(false);
        logger.writeLog("RESPONSE [PAY-FACTURE= FALSE]");
        return new NewReponse(NewMessageDataType.PAY_FACTURE,"false");
    }
}
