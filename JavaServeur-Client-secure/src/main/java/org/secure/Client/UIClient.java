package org.secure.Client;

import org.bouncycastle.operator.OperatorCreationException;
import org.secure.utils.donnees.Facture;
import org.secure.utils.interfaces.Reponse;
import org.secure.utils.reponses.*;
import org.secure.utils.requetes.RequeteGetFactures;
import org.secure.utils.requetes.RequeteLogin;
import org.secure.utils.requetes.RequetePayFacture;
import org.secure.utilsCrypted.generator.CertificateGenerator;
import org.secure.utilsCrypted.reponses.*;
import org.secure.utilsCrypted.requetes.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UIClient extends JFrame{
    private JPanel panel1;
    private JTextField textFieldUsername;
    private JTextField textFieldPassword;
    private JButton loginButton;
    private JButton logoutButton;
    private JTable tableFacture;
    private JButton payerButton;
    private JTextField textFieldCARTE;
    private JTextField textFieldPROPRIETAIRE;
    private JButton afficherLesFacturesButton;
    private JCheckBox checkBoxSecure;

    private Socket csocket;
    private String IPaddr;
    private int port;

    private List<Facture> tf;
    private int idClient;

    ObjectInputStream ois   = null;
    ObjectOutputStream oos  = null;

    /************************************/
    //  Partie Cryptographie
    KeyPair         clePair;
    X509Certificate certificate;
    SecretKey       cleSession;
    /************************************/


    public UIClient(String ipADDR, int port){
        try {
            initComponent(ipADDR, port);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
            System.exit(1);
        }
    }

    private void initComponent(String ipADDR, int port) throws IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel1);

        tf = new ArrayList<>(){};
        tableFacture.setModel(new TableFactureModel(tf));
        tableFacture.setVisible(true);
        tableFacture.setDragEnabled(false);
        tableFacture.setShowGrid(true);

        this.IPaddr = ipADDR;
        this.port = port;
        csocket = null;

        idClient = 0;

        pack();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //if(csocket != null) csocket.close();
                    if(checkBoxSecure.isSelected())
                        csocket = new Socket(ipADDR,port);
                    else
                        csocket = new Socket(ipADDR,port);
                    oos = new ObjectOutputStream(csocket.getOutputStream());
                    ois = new ObjectInputStream(csocket.getInputStream());

                    EnvoyerLogin(textFieldUsername.getText(),textFieldPassword.getText());
                    AnalyseReponse(RecevoirReponse());
                }
                catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                       BadPaddingException | NoSuchProviderException | InvalidKeyException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    csocket.close();
                    oos = null;
                    ois = null;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                setLogoutOK();
            }
        });

        payerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setOptionPane("proprietaire:" + textFieldPROPRIETAIRE.getText().isBlank() + " password:" + textFieldPassword.getText().isBlank());
                if(textFieldPROPRIETAIRE.getText().isBlank() && textFieldCARTE.getText().isBlank()){
                    JOptionPane.showMessageDialog(UIClient.this,"Champs manquants pour le paiement : Numero carte/Nom proprietaire");
                }
                else{
                    //setOptionPane("Row = " + tableFacture.getSelectedRow());
                    if(tableFacture.getSelectedRow() < 0)
                    {
                        setOptionPane("Selectionner une facture !");
                    }
                    else
                    {
                        int facture = (int)tableFacture.getValueAt(tableFacture.getSelectedRow(),0);
                        //setOptionPane("idFacture = " + facture.getIdFacture());
                        EnvoyerPayerFacture(facture,textFieldPROPRIETAIRE.getText(),textFieldCARTE.getText());

                        try {
                            AnalyseReponse(RecevoirReponse());
                        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                                 BadPaddingException | IOException | NoSuchProviderException | InvalidKeyException |
                                 ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        afficherLesFacturesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tf.clear();
                tableFacture.setModel(new TableFactureModel(tf));
                if(idClient != 0)
                    EnvoyerGetFactures(idClient);
                else EnvoyerGetFactures(2);

                try {
                    AnalyseReponse(RecevoirReponse());
                } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                         BadPaddingException | IOException | NoSuchProviderException | InvalidKeyException |
                         ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                /*NewReponse reponse =  RecevoirReponse();
                setOptionPane(reponse.getContent());*/
            }
        });
        setLogoutOK();
    }

    public void setLoginOK(){
        loginButton.setEnabled(false);
        logoutButton.setEnabled(true);
        payerButton.setEnabled(true);
        textFieldUsername.setEnabled(false);
        textFieldPassword.setEnabled(false);
    }

    public void setLogoutOK(){
        loginButton.setEnabled(true);
        logoutButton.setEnabled(false);
        payerButton.setEnabled(false);
        textFieldUsername.setEnabled(true);
        textFieldPassword.setEnabled(true);
        tf.clear();
        tableFacture.setModel(new TableFactureModel(tf));
    }

    public void setOptionPane(String message){
        JOptionPane.showMessageDialog(this,message);
    }

    public void colorPrint(String message){
        System.out.println("\033[92m"+message+"\033[0m");
    }

    private static class TableFactureModel extends AbstractTableModel
    {
        private final String[] COLUMNS= {"ID","Date","Montant","Paye"};
        private List<Facture> factures;

        public TableFactureModel(List<Facture> panier)
        {
            this.factures = panier;
        }

        @Override
        public int getRowCount() {
            return factures.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Facture facture = factures.get(rowIndex);

            switch(columnIndex)
            {
                case 0:
                    return facture.getIdFacture();
                case 1:
                    return facture.getDate();
                case 2:
                    return facture.getMontant();
                case 3:
                    return facture.getPaye();
                default:
                    return "-";
            }
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }
    }

    public Reponse RecevoirReponse(){
        try{
            return (Reponse) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error I/O : " + e.getMessage());
        }
        return null;
    }

    //V
    public void EnvoyerGetFactures(int idClient){
        try {
            sRequeteGetFactures requete = new sRequeteGetFactures(idClient,cleSession,clePair.getPrivate());
            oos.writeObject(requete);
        }
        catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
               BadPaddingException | SignatureException | NoSuchProviderException | InvalidKeyException ex) {
            System.out.println("Error EnvoyerGetFacture : " + ex.getMessage());
        }
    }

    //V
    public void EnvoyerLogin(String username,String password){
        try {
            clePair = CertificateGenerator.generateKeyPair();
            certificate = CertificateGenerator.generateSelfSignedCertificate(username,clePair);
            sRequeteLogin requete = new sRequeteLogin(username,password,certificate);

            oos.writeObject(requete);
            colorPrint("Pair de clés: " + clePair.toString());
            colorPrint("Certificat: " + certificate);
        }
        catch (IOException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException |
               OperatorCreationException ex) {
            System.out.println("Error EnvoyerLogin : " + ex.getMessage());
        }
    }

    //V
    public void EnvoyerPayerFacture(int idFacture,String proprietaire,String NumeroCarte){
        try {
            sRequetePayFacture requete = new sRequetePayFacture(idFacture,proprietaire,NumeroCarte,cleSession);
            oos.writeObject(requete);
        }
        catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException |
               NoSuchAlgorithmException | BadPaddingException | NoSuchProviderException ex) {
            System.out.println("Error EnvoyerPayerFacture: " + ex.getMessage());
        }
    }

    public void AnalyseReponse(Reponse reponse) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, NoSuchProviderException, InvalidKeyException, ClassNotFoundException {

        if(reponse instanceof sReponseLogin) {
            cleSession = ((sReponseLogin) reponse).get_secret_key_decrypted(clePair.getPrivate(),"AES");
            if(((sReponseLogin) reponse).getSucced(cleSession)){
                idClient = ((sReponseLogin) reponse).getIdClient(cleSession);
                setOptionPane("Identifiant: " + idClient);
                colorPrint("Cle de session obtenue: " + cleSession);
                setLoginOK();

                EnvoyerGetFactures(idClient);
                AnalyseReponse(RecevoirReponse());
            }
            else
                setOptionPane("Informations de connexion erronnées!");
        }
        else if (reponse instanceof ReponseLogout) {
            try {
                oos = null;
                ois = null;
                csocket.close();
                setLogoutOK();

                certificate = null;
                cleSession = null;
                clePair = null;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (reponse instanceof sReponseGetFactures) {
            tf.clear();
            if(!((sReponseGetFactures) reponse).getFactureList(cleSession).isEmpty()){
                tf = ((sReponseGetFactures) reponse).getFactureList(cleSession);
                tableFacture.setModel(new TableFactureModel(tf));
                colorPrint("Factures: " + tf);
            }
        }
        else if (reponse instanceof sReponsePayFacture) {

            if(((sReponsePayFacture) reponse).VerifyHMAC(cleSession)){
                if(((sReponsePayFacture) reponse).isSucceed(cleSession)){
                    setOptionPane("Payement reussi!");
                    colorPrint("Payement: " + ((sReponsePayFacture) reponse).isSucceed(cleSession));
                    EnvoyerGetFactures(idClient);
                    AnalyseReponse(RecevoirReponse());
                }
                else setOptionPane("Payement échoué!");
            }
            else setOptionPane("Erreur dans le HMAC !");
        }
        else if (reponse instanceof ReponseErreur) {
            System.out.println("Erreur : " + ((ReponseErreur) reponse).getError());
            //certificate = null;
            //cleSession = null;
            //clePair = null;
        }
    }
}
