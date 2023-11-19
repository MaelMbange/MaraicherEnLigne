package RTI.Client;

import RTI.PROJET.requetesNet.NewMessageDataType;
import RTI.PROJET.requetesNet.NewReponse;
import RTI.PROJET.requetesNet.NewRequest;
import RTI.PROJET.structureDonnees.Facture;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    private Socket csocket;
    private String IPaddr;
    private int port;

    private List<Facture> tf;
    private int idClient;

    ObjectInputStream ois   = null;
    ObjectOutputStream oos  = null;


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
                    csocket = new Socket(ipADDR,port);
                    oos = new ObjectOutputStream(csocket.getOutputStream());
                    ois = new ObjectInputStream(csocket.getInputStream());

                    EnvoyerLogin(textFieldUsername.getText(),textFieldPassword.getText());
                    AnalyseReponse(RecevoirReponse());
                }
                catch (IOException ex) {
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
                        EnvoyerPayerFacture(facture,textFieldPROPRIETAIRE.getText(),textFieldPassword.getText());

                        AnalyseReponse(RecevoirReponse());
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

                AnalyseReponse(RecevoirReponse());
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
    }

    public void setOptionPane(String message){
        JOptionPane.showMessageDialog(this,message);
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

    public NewReponse RecevoirReponse(){
        try{
            return (NewReponse)ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error I/O : " + e.getMessage());
        }
        return null;
    }

    public void EnvoyerGetFactures(int idClient){
        try {
            oos.writeObject(new NewRequest(NewMessageDataType.GET_FACTURES,String.valueOf(idClient)));
        }
        catch (IOException ex) {
            System.out.println("Error I/O : " + ex.getMessage());
        }
    }

    public void EnvoyerLogin(String username,String password){
        try {
            oos.writeObject(new NewRequest(NewMessageDataType.LOGIN,username+"/"+password));
        }
        catch (IOException ex) {
            System.out.println("Error I/O : " + ex.getMessage());
        }
    }

    public void EnvoyerPayerFacture(int idFacture,String proprietaire,String NumeroCarte){
        try {
            oos.writeObject(new NewRequest(NewMessageDataType.PAY_FACTURE,idFacture+"/"+proprietaire+"/"+NumeroCarte));
        }
        catch (IOException ex) {
            System.out.println("Error I/O : " + ex.getMessage());
        }
    }

    public void AnalyseReponse(NewReponse reponse){

        String[] sp = reponse.getContent().split("/");

        switch(reponse.getHeader()){
            case "LOGIN" :
                    if(Boolean.parseBoolean(sp[0])){
                        idClient = Integer.parseInt(sp[1]);
                        setOptionPane("Identifiant: " + idClient);
                        setLoginOK();

                        EnvoyerGetFactures(idClient);
                        AnalyseReponse(RecevoirReponse());
                    }
                    else
                        setOptionPane("Informations de connexion erronnées!");
                break;

            case "LOGOUT":
                try {
                    oos = null;
                    ois = null;
                    csocket.close();
                    setLogoutOK();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "GETFACTURES" :
                tf.clear();
                if(!sp[0].equals("false")){
                    for(int i = 0; i < sp.length-1;i += 4){
                        tf.add(new Facture(Integer.parseInt(sp[i]),LocalDate.parse(sp[i+1]),Float.parseFloat(sp[i+2]),Boolean.parseBoolean(sp[i+3])));
                    }
                    tableFacture.setModel(new TableFactureModel(tf));
                }
                break;

            case "PAYFACTURE":
                if(Boolean.parseBoolean(sp[0])){
                    setOptionPane("Payement reussi!");

                    EnvoyerGetFactures(idClient);
                    AnalyseReponse(RecevoirReponse());
                }
                else setOptionPane("Payement échoué!");
                break;

            default:
                System.out.println("Erreur : Bad response recieved!");
                break;
        }

    }
}
