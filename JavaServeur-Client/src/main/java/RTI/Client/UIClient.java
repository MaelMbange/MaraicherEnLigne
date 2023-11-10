package RTI.Client;

import RTI.PROJET.requetesNet.*;
import RTI.PROJET.structureDonnees.Facture;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    private List<Facture> tf;
    private Socket csocket;
    private int idClient;
    private String IPaddr;
    private int port;


    public UIClient(String ipADDR, int port){
        try {
            initComponent(ipADDR, port);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }

    private void initComponent(String ipADDR, int port) throws IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel1);

        tf = new ArrayList<>(){};
        tableFacture.setModel(new TableFactureModel(tf));
        tableFacture.setVisible(true);
        tableFacture.setCellEditor(null);
        tableFacture.setDragEnabled(false);
        tableFacture.setShowGrid(true);

        this.IPaddr = ipADDR;
        this.port = port;
        csocket = null;

        pack();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    csocket = new Socket(ipADDR,port);
                    //
                    mynet.EnvoyerLogin(csocket,textFieldUsername.getText(),textFieldPassword.getText());
                    NewReponse reponse = mynet.RecevoirReponse(csocket);
                    idClient = Integer.parseInt(reponse.getContent().split("/")[1]);
                    setOptionPane(reponse.getContent());

                    csocket.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                setLoginOK();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    csocket = new Socket(ipADDR,port);
                    mynet.EnvoyerLogout(csocket);
                    NewReponse reponse = mynet.RecevoirReponse(csocket);
                    csocket.close();
                    setOptionPane(reponse.getContent());
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                setLogoutOK();
            }
        });

        payerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textFieldPROPRIETAIRE.getText().isEmpty() && textFieldPassword.getText().isEmpty()){
                    JOptionPane.showMessageDialog(UIClient.this,"Champs manquants pour le paiement : Numero carte/Nom proprietaire");
                }
                else{

                    try {
                        csocket = new Socket(ipADDR,port);
                        mynet.EnvoyerPayerFacture(csocket,1,"Mael","05628229");
                        NewReponse reponse = mynet.RecevoirReponse(csocket);
                        csocket.close();
                        setOptionPane(reponse.getContent());
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        afficherLesFacturesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    csocket = new Socket(ipADDR,port);
                    mynet.EnvoyerGetFactures(csocket,idClient);
                    NewReponse reponse = mynet.RecevoirReponse(csocket);
                    csocket.close();
                    setOptionPane(reponse.getContent());
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        setLogoutOK();
    }

    public void setLoginOK(){
        loginButton.setEnabled(false);
        logoutButton.setEnabled(true);
        payerButton.setEnabled(true);
    }

    public void setLogoutOK(){
        loginButton.setEnabled(true);
        logoutButton.setEnabled(false);
        payerButton.setEnabled(false);
        tf.clear();
    }

    public void setOptionPane(String message){
        JOptionPane.showMessageDialog(this,message);
    }

    private void treatement(NewReponse reponse){

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
}
