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

        tf = new Vector<>();
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
                    mynet.EnvoyerLogin(csocket,textFieldUsername.getText(),textFieldPassword.getText());
                    NewReponse reponse = mynet.RecevoirReponse(csocket);
                    treatement(reponse);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mynet.EnvoyerLogout(csocket);
                NewReponse reponse = mynet.RecevoirReponse(csocket);
                treatement(reponse);
                //setLogoutOK();
            }
        });

        payerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textFieldPROPRIETAIRE.getText().isEmpty() && textFieldPassword.getText().isEmpty()){
                    JOptionPane.showMessageDialog(UIClient.this,"Champs manquants pour le paiement : Numero carte/Nom proprietaire");
                }
                else{
                    Facture f = (Facture) tableFacture.getValueAt(tableFacture.getSelectedRow(),tableFacture.getSelectedColumn());
                    mynet.EnvoyerPayerFacture(csocket,f.getIdFacture(),textFieldPROPRIETAIRE.getText(),textFieldCARTE.getText());
                    NewReponse reponse = mynet.RecevoirReponse(csocket);
                    treatement(reponse);
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

    public void setTableArticle(List<Facture> fa){
        tf = fa;
        tableFacture.setModel(new TableFactureModel(tf));
    }

    public List<Facture> refactorFacture(String content){
        String[] ref_content = content.split("/");
        List<Facture> f = new Vector<>();
        if(!ref_content[0].equals("false")) {
            for (int i = 0; i < ref_content.length - 1; i += 4) {
                f.add(new Facture(Integer.parseInt(ref_content[i]), LocalDate.parse(ref_content[i + 1]),
                        Float.parseFloat(ref_content[i + 2]), Boolean.parseBoolean(ref_content[i + 3])));
            }
            return f;
        }
        return new Vector<>();
    }

    private void treatement(NewReponse reponse){
        NewReponse nr = reponse;
        setOptionPane(nr.getHeader());

        String[] content = nr.getContent().split("/");

        if(nr.getHeader().equals(NewMessageDataType.LOGIN)){
            if(!content[0].equals("false"))
            {
                setOptionPane("Votre id est :" + content[1]);
                setLoginOK();
                idClient = (Integer.parseInt(content[1]));
                /*mynet.EnvoyerGetFactures(csocket,idClient);
                NewReponse reponse2 = mynet.RecevoirReponse(csocket);
                treatement(reponse2);*/
            }
            else
                setOptionPane("Le login a echoue!");
        }
        else if (nr.getHeader().equals(NewMessageDataType.GET_FACTURES)){
            List<Facture> fa = refactorFacture(nr.getContent());
            setTableArticle(fa);
        }
        else if (nr.getHeader().equals(NewMessageDataType.PAY_FACTURE)){
            if(content[0].equals("true")){
                setOptionPane("Paiement validé!");
                /*mynet.EnvoyerGetFactures(csocket,idClient);
                NewReponse reponse2 = mynet.RecevoirReponse(csocket);
                treatement(reponse2);*/
            }
            else
                setOptionPane("Paiement echoue!");
        }
        else if(nr.getHeader().equals(NewMessageDataType.LOGOUT)){
            setOptionPane("Logout!");
            setLogoutOK();
        }
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
