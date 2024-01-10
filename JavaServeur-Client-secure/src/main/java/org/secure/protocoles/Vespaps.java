package org.secure.protocoles;

import org.secure.serveur.ContainerPublicKey;
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
import org.secure.utils.requetesSQL.RequeteSQLGetFactures;
import org.secure.utils.requetesSQL.RequeteSQLGetLogin;
import org.secure.utils.requetesSQL.RequeteSQLGetLoginv2;
import org.secure.utils.requetesSQL.RequeteSQLPayFacture;
import org.secure.utilsCrypted.reponses.sReponseGetFactures;
import org.secure.utilsCrypted.reponses.sReponseLogin;
import org.secure.utilsCrypted.reponses.sReponsePayFacture;
import org.secure.utilsCrypted.requetes.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Vespaps implements Protocol {
    private Logs logger;
    private JavaServerDAL jsd;

    public Vespaps(Logs logger,String address) throws Exception {
        this.logger = logger;
        this.jsd = JavaServerDAL.getFactory(address);
    }

    @Override
    public String getName() {
        return "protocoleRTI";
    }

    @Override
    public synchronized Reponse analyse (Requete request) throws EOCException {
        return null;
    }

    @Override
    public synchronized Reponse analyse(Requete request, SecretKey cleSession, ContainerPublicKey clePublicContainer) throws EOCException {
        Requete requete = (Requete)request;
        // --------------------------------
        try
        {
            if(requete instanceof sRequeteLogin) {
                return traiteRequeteLogin((sRequeteLogin)requete,cleSession,clePublicContainer);
            }
            if(requete instanceof sRequeteGetFactures) {
                return traiteRequeteGetFactures((sRequeteGetFactures)requete,cleSession,clePublicContainer);
            }
            if(requete instanceof sRequetePayFacture){
                return traiteRequetePayFacture((sRequetePayFacture)requete,cleSession);
            }
            if(requete instanceof RequeteLogout){
                traiteRequeteLogout();
                return null;
            }
        }
        catch(RuntimeException | SQLException e){
            logger.writeLog( "SQL error : " + e.getMessage() + " !!! ici !!!");
        } catch (NoSuchPaddingException | IllegalBlockSizeException | CertificateEncodingException |
                 NoSuchAlgorithmException | IOException | BadPaddingException | NoSuchProviderException |
                 InvalidKeyException |SignatureException e) {
            logger.writeLog( "Error : " + e.getMessage() + " !!! ici !!!");
        }
        return null;
    }

    //V
    private synchronized Reponse traiteRequeteLogin(sRequeteLogin requete, SecretKey cleSession, ContainerPublicKey clePublicContainer) throws EOCException, SQLException, CertificateEncodingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        logger.writeLog("REQUEST [LOGIN= RECIEVED]");
        ResultSet res = jsd.GET(new RequeteSQLGetLoginv2(requete.getLogin()));
        if(res.next())
        {
            if(requete.VerifyPassword(res.getString(3))){
                logger.writeLog("RESPONSE [LOGIN= " + String.valueOf(res.getBoolean(1)).toUpperCase() + "/" + res.getInt(2) + "]");
                //Recuperation cle publique
                clePublicContainer.setClePublic(requete.getCertificate().getPublicKey());

                return new sReponseLogin(true,res.getInt(2),cleSession, clePublicContainer.getClePublic());
            }
        }
        throw new EOCException(new ReponseErreur("Login Invalide"));
    }

    //V
    private synchronized void traiteRequeteLogout() throws EOCException{
        logger.writeLog("REQUEST [LOGOUT= RECIEVED] ");
        logger.writeLog("LOGOUT [STATUS= END_OF_CONNECTION] ");
        throw new EOCException(null);
    }

    //V
    private synchronized Reponse traiteRequeteGetFactures(sRequeteGetFactures requete,SecretKey cleSession,ContainerPublicKey clePublicContainer) throws SQLException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, NoSuchProviderException, InvalidKeyException, SignatureException, EOCException {
        logger.writeLog("REQUEST [GET-FACTURES= RECIEVED]");

        if(requete.VerifySignature(cleSession, clePublicContainer.getClePublic())){
            ResultSet res = jsd.GET(new RequeteSQLGetFactures(requete.getIdClient(cleSession)));
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
            return new sReponseGetFactures(factureList,cleSession);
        }
        logger.writeLog("Signature client INVALIDE !");
        throw new EOCException(new ReponseErreur("Signature client INVALIDE !"));
    }

    //V
    private synchronized Reponse traiteRequetePayFacture(sRequetePayFacture requete,SecretKey cleSession) throws SQLException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        logger.writeLog("REQUEST [PAY-FACTURE= RECIEVED]");

        int rd = ThreadLocalRandom.current().nextInt(2)%2;
        if(rd == 1) {
            logger.writeLog("RESPONSE [PAY-FACTURE= FALSE]");
            return new sReponsePayFacture(false,cleSession);
        }
        logger.writeLog("REQUEST [__CONTENU__]");
        logger.writeLog("REQUEST [ID_FACTURE ->" + requete.getIdFacture(cleSession) + "]");
        logger.writeLog("REQUEST [NOM_CARTE ->" + requete.getNomCarte(cleSession) + "]");
        logger.writeLog("REQUEST [NUMERO_CARTE ->" + requete.getNumeroCarte(cleSession) + "]");
        //Recherche SQL
        int resultat = jsd.POSTUPDATE(new RequeteSQLPayFacture(requete.getIdFacture(cleSession)));
        if(resultat > 0) {
            logger.writeLog("RESPONSE [PAY-FACTURE= TRUE]");
            return new sReponsePayFacture(true,cleSession);
        }
        logger.writeLog("RESPONSE [PAY-FACTURE= FALSE]");
        return new sReponsePayFacture(false,cleSession);
    }
}
