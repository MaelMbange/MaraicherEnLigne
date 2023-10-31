package classes.UI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import classes.Utile.Article;
import classes.Utile.Utilisateur;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import classes.lib.mynet;


public class ClientWindow extends JFrame {
    private JPanel panel1;
    private JTextField InsertLogin;
    private JTextField InsertPassword;
    private JButton bLoggin;
    private JButton bLoggout;
    private JCheckBox checkBoxNouveauClient;
    private JButton bPrecedent;
    private JButton bSuivant;
    private JButton bAcheter;
    private JLabel ImageArticle;
    private JTextField Article;
    private JTextField prix;
    private JTextField stock;
    private JButton bViderPanier;
    private JButton bRetirerElement;
    private JButton bPayer;
    private JTextField totalAPayer;
    private JTable tableArticle;
    private JSpinner quantite;

    private Utilisateur UT;
    private Article article;
    private float total_caddie = 0.0f;
    private Socket socket;
    private List<Article> ta;

    private static final int MESSAGE_MAX_TAILLE = 10000;

    public ClientWindow()
    {
        this(null);
    }

    public ClientWindow(Socket csocket)
    {
        super("Client");
        socket = csocket;


        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        //setVisible(true);
        initComponent();
        firstRequest();
        pack();
    }

    private void firstRequest(){
        try {
            mynet.EnvoyerData(socket,"CONSULT#1#");
            String retour = mynet.RecevoirData(socket);
            Analyser(retour);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(bLoggin.getParent(),e.getMessage(),
                    "IOException", JOptionPane.WARNING_MESSAGE,null);
        }
    }

    private void setImageArticle(String path){
        Image image = new ImageIcon(path).getImage();
        Image resisedImage = image.getScaledInstance(200,200, Image.SCALE_SMOOTH);
        ImageArticle.setIcon(new ImageIcon(resisedImage));
    }

    private void setArticleUi(String[] split)
    {
        article.Id = Integer.parseInt(split[1]);
        article.Intitule = split[2];
        article.Prix = Float.parseFloat(split[3]);
        article.Stock = Integer.parseInt(split[4]);
        article.Image = split[5];

        Article.setText(article.Intitule);
        prix.setText(String.valueOf(article.Prix));
        stock.setText(String.valueOf(article.Stock));
    }

    private void initComponent()
    {
        ta = new ArrayList<>();
        tableArticle.setModel(new TableArticleModel(ta));
        tableArticle.setCellEditor(null);
        tableArticle.setDragEnabled(false);
        tableArticle.setShowGrid(true);

        setImageArticle("Client/ressources/images/pommes.jpg");

        bLoggin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(InsertLogin.getText().isEmpty()){
                    JOptionPane.showMessageDialog(bLoggin.getParent(),"Le nom d'utilisateur ne peut pas être vide !",
                            "Etat de connexion", JOptionPane.WARNING_MESSAGE,null);
                }
                else if(InsertPassword.getText().isEmpty()){
                    JOptionPane.showMessageDialog(bLoggin.getParent(),"Le mot de passe ne peut pas être vide !",
                            "Etat de connexion", JOptionPane.WARNING_MESSAGE,null);
                }
                else{
                    if(checkBoxNouveauClient.isSelected()){
                        try {
                            mynet.EnvoyerData(socket,("LOGIN#" + InsertLogin.getText() + "#"+ InsertPassword.getText()+"#1#"));
                            String retour = mynet.RecevoirData(socket);
                            Analyser(retour);
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(bLoggin.getParent(),ex.getMessage(),
                                    "IOException", JOptionPane.WARNING_MESSAGE,null);
                        }
                    }
                    else{
                        try {
                            mynet.EnvoyerData(socket,("LOGIN#" + InsertLogin.getText() + "#"+ InsertPassword.getText()+"#0#"));
                            String retour = mynet.RecevoirData(socket);
                            Analyser(retour);
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(bLoggin.getParent(),ex.getMessage(),
                                    "IOException", JOptionPane.WARNING_MESSAGE,null);
                        }
                    }
                }
            }
        });

        bLoggout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bLoggin.setEnabled(true);
                bLoggout.setEnabled(false);
                checkBoxNouveauClient.setEnabled(true);
                checkBoxNouveauClient.setSelected(false);
                bPrecedent.setEnabled(false);
                bSuivant.setEnabled(false);
                bAcheter.setEnabled(false);
                quantite.setEnabled(false);
                bViderPanier.setEnabled(false);
                bRetirerElement.setEnabled(false);
                bPayer.setEnabled(false);
                InsertPassword.setEnabled(true);
                InsertLogin.setEnabled(true);
            }
        });

        bPayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        bViderPanier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        bRetirerElement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        bAcheter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if((int)quantite.getValue() != 0){
                        mynet.EnvoyerData(socket,("ACHAT#" + article.Id + "#" + quantite.getValue() + "#"));
                        String retour = mynet.RecevoirData(socket);
                        Analyser(retour);
                    }
                    else
                        JOptionPane.showMessageDialog(bAcheter.getParent(),"La quantite ne peut etre egal à 0",
                                "Etat achat", JOptionPane.WARNING_MESSAGE,null);
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(bAcheter.getParent(),ex.getMessage(),
                            "IOException", JOptionPane.WARNING_MESSAGE,null);
                }
            }
        });

        bPrecedent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mynet.EnvoyerData(socket,("CONSULT#" + (article.Id - 1) +"#"));
                    String retour = mynet.RecevoirData(socket);
                    Analyser(retour);
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(bPrecedent.getParent(),ex.getMessage(),
                            "IOException", JOptionPane.WARNING_MESSAGE,null);
                }
            }
        });


        bSuivant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mynet.EnvoyerData(socket,("CONSULT#" + (article.Id + 1) +"#"));
                    String retour = mynet.RecevoirData(socket);
                    Analyser(retour);
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(bSuivant.getParent(),ex.getMessage(),
                            "IOException", JOptionPane.WARNING_MESSAGE,null);
                }
            }
        });

        UT = new Utilisateur(0);

       bLoggin.setEnabled(true);
       bLoggout.setEnabled(false);
       checkBoxNouveauClient.setEnabled(true);
       bPrecedent.setEnabled(false);
       bSuivant.setEnabled(false);
       bAcheter.setEnabled(false);
       quantite.setModel(new SpinnerNumberModel(0, 0, null, 1));
       quantite.setEnabled(false);
       bViderPanier.setEnabled(false);
       bRetirerElement.setEnabled(false);
       bPayer.setEnabled(false);
       tableArticle.setDragEnabled(false);
    }

    private static class TableArticleModel extends AbstractTableModel
    {
        private final String[] COLUMNS= {"Article","Prix","Quantite"};
        private List<Article> panier;

        public TableArticleModel(List<Article> panier)
        {
            this.panier = panier;
        }

        @Override
        public int getRowCount() {
            return panier.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Article article = panier.get(rowIndex);

            switch(columnIndex)
            {
                case 0:
                    return article.Intitule;
                case 1:
                    return article.Prix;
                case 2:
                    return article.Stock;
                default:
                    return "-";
            }
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }
    }

    private void Analyser(String message){
        String[] split = message.split("#");

        switch(split[0]){

            case "LOGIN":
                    switch(split[1]){
                        case "OK":
                            UT.id = Integer.parseInt(split[2]);
                            JOptionPane.showMessageDialog(bLoggin.getParent(),"Connexion réussie !\nId : " + UT.id,
                                    "Etat de connexion", JOptionPane.INFORMATION_MESSAGE,null);
                                bLoggin.setEnabled(false);
                                bLoggout.setEnabled(true);
                                checkBoxNouveauClient.setEnabled(false);
                                bPrecedent.setEnabled(true);
                                bSuivant.setEnabled(true);
                                bAcheter.setEnabled(true);
                                quantite.setEnabled(true);
                                bViderPanier.setEnabled(true);
                                bRetirerElement.setEnabled(true);
                                bPayer.setEnabled(true);
                                InsertPassword.setEnabled(false);
                                InsertLogin.setEnabled(false);
                            break;
                        case "KO":
                            JOptionPane.showMessageDialog(bLoggin.getParent(),"Connexion échouée !",
                                    "Etat de connexion", JOptionPane.WARNING_MESSAGE,null);
                            break;
                    }

                break;

            case "CONSULT":
                    article = new Article();
                    if(Integer.parseInt(split[1]) != 0)
                    {
                        setArticleUi(split);
                        setImageArticle("Client/ressources/images/" + article.Image);
                    }
                break;
            
            case "ACHAT":
                    int id = Integer.parseInt(split[1]);
                    if(id != -1 && id != 0){
                        JOptionPane.showMessageDialog(this.getParent(),"Achat reussi !",
                                "Etat de la demande d'achat", JOptionPane.WARNING_MESSAGE,null);

                        try {
                            total_caddie = 0.0f;
                            mynet.EnvoyerData(socket,"CADDIE#");
                            String retour = mynet.RecevoirData(socket);
                            Analyser(retour);
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,ex.getMessage(),
                                    "IOException", JOptionPane.WARNING_MESSAGE,null);
                        }
                    }
                    else{
                        if(id == 0){
                            JOptionPane.showMessageDialog(this,"Quantité insuffisante!",
                                    "Etat de la demande d'achat", JOptionPane.WARNING_MESSAGE,null);
                        }
                        else {
                            JOptionPane.showMessageDialog(this,"Le caddie est plein!",
                                    "Etat de la demande d'achat", JOptionPane.WARNING_MESSAGE,null);
                        }
                    }
                break;
            
            case "CADDIE":
                    total_caddie = 0.0f;
                    String intitule_ = "";
                    int quantite_ = 0;
                    float prix_ = 0.0f;
                    ta.clear();

                    System.out.println("here: " + split.length);
                    for(int i = 1; i < split.length-1; i += 4)
                    {
                        intitule_ = split[i+1];
                        prix_ = Float.parseFloat(split[i+2]);
                        quantite_ = Integer.parseInt(split[i+3]);

                        ta.add(new Article(intitule_,prix_,quantite_));
                        total_caddie += quantite_ * prix_;
                    }
                    tableArticle.setModel(new TableArticleModel(ta));
                    totalAPayer.setText(String.valueOf(total_caddie));
                break;

            case "CANCEL":
                    if(split[1] == "OK")
                    {
                        try {
                            total_caddie = 0.0f;
                            mynet.EnvoyerData(socket,"CADDIE#");
                            String retour = mynet.RecevoirData(socket);
                            Analyser(retour);
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,ex.getMessage(),
                                    "IOException", JOptionPane.WARNING_MESSAGE,null);
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(this,"l'article n'a pas pu etre retiré!",
                                "Etat de l'annulation d'un article", JOptionPane.WARNING_MESSAGE,null);
                break;

            case "CANCEL_ALL":
                    total_caddie = 0.0f;
                    ta.clear();
                    tableArticle.setModel(new TableArticleModel(ta));
                    totalAPayer.setText(String.valueOf(total_caddie));
                break;

            case "CONFIRMER":
                    total_caddie = 0.0f;
                    totalAPayer.setText(String.valueOf(total_caddie));
                    ta.clear();
                    tableArticle.setModel(new TableArticleModel(ta));

                    if(Integer.parseInt(split[1]) != -1)
                    {
                        JOptionPane.showMessageDialog(this,"La commande a été comfirmée!",
                                "Etat de la validation de la commande", JOptionPane.WARNING_MESSAGE,null);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this,"La commande n'a pas été comfirmée suite à une erreur!",
                                "Etat de la validation de la commande", JOptionPane.WARNING_MESSAGE,null);
                    }
                break;
        }
    }


    public static void main(String[] args)
    {
        try {
            Socket sock = new Socket(args[0],Integer.parseInt(args[1]));
            ClientWindow c = new ClientWindow(sock);
            c.setVisible(true);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
