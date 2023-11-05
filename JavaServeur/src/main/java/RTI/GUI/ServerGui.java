package RTI.GUI;

import rti.serveur.AbstractMainServerThread;
import RTI.PROJET.protocoles.*;
import rti.serveur.OnDemandMainServerThread;
import rti.serveur.OnPoolMainServerThread;
import rti.utils.Logs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

public class ServerGui extends JFrame implements Logs {
    private JPanel panel1;
    private JLabel LabelProtocol;
    private JRadioButton radioButtonDemande;
    private JRadioButton radioButtonPool;
    private JTextField textFieldNombreThreads;
    private JButton buttonDemarrer;
    private JButton buttonStop;
    private JTable tableLogs;
    private JButton ButtonViderLogs;
    private JLabel LabelPort;
    private JLabel LabelThreads;

    private AbstractMainServerThread threadServer;
    private String IPAddress;
    private int port;
    private ButtonGroup bg;

    public ServerGui() {
        initcomponnent();

        threadServer = null;
    }

    private void initcomponnent(){
        String[] columnNames = {"Threads","Action"};
        tableLogs.setModel(new DefaultTableModel(new Object[]{"Threads","Action"},0));

        bg = new ButtonGroup();
        bg.add(radioButtonDemande);
        bg.add(radioButtonPool);

        loadProperties();

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadServer.interrupt();

                buttonDemarrer.setEnabled(true);
                buttonStop.setEnabled(false);
                radioButtonDemande.setEnabled(true);
                radioButtonPool.setEnabled(true);
            }
        });

        buttonDemarrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    //IP_ADDR eest pour le lien avec la base de donnée
                    protocolePayement protocole = new protocolePayement((Logs)buttonDemarrer.getParent(),IPAddress);

                    if (radioButtonDemande.isSelected())
                        threadServer = new OnDemandMainServerThread(port,protocole,(Logs)buttonDemarrer.getParent());

                    if (radioButtonPool.isSelected())
                    {
                        int taillePool = Integer.parseInt(textFieldNombreThreads.getText());
                        threadServer = new OnPoolMainServerThread(port,protocole,taillePool,(Logs)buttonDemarrer.getParent());
                    }

                    videLogs();
                    threadServer.start();

                    buttonDemarrer.setEnabled(false);
                    buttonStop.setEnabled(true);
                    radioButtonDemande.setEnabled(false);
                    radioButtonPool.setEnabled(false);
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(buttonDemarrer.getParent(),"Erreur de Port et/ou taille Pool !","Erreur...",JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(buttonDemarrer.getParent(),"Erreur I/O !","Erreur...",JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(buttonDemarrer.getParent(),ex.getMessage(),"Erreur...",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ButtonViderLogs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videLogs();
            }
        });
    }

    @Override
    public void writeLog(String s) {
        DefaultTableModel modele = (DefaultTableModel)tableLogs.getModel();
        Vector<String> ligne = new Vector<>();
        ligne.add(Thread.currentThread().getName());
        ligne.add(s);
        modele.insertRow(modele.getRowCount(),ligne);
    }

    private void videLogs()
    {
        DefaultTableModel modele = (DefaultTableModel)tableLogs.getModel();
        modele.setRowCount(0);
    }

    private void loadProperties(){
        try {
            if(!new File("./properties").exists()){
                saveProperties();
            }
            FileInputStream fis = new FileInputStream("./properties");
            Properties prop = new Properties();
            prop.load(fis);
            LabelPort.setText(prop.getProperty("PORT_ACHAT"));
            LabelThreads.setText(prop.getProperty("THREADS"));
            IPAddress = prop.getProperty("IP_ADDR");
            port = Integer.parseInt(prop.getProperty("PORT_ACHAT"));
            fis.close();
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveProperties(){
        try {
            FileOutputStream fos = new FileOutputStream("./properties");
            Properties prop = new Properties();
            prop.setProperty("PORT_ACHAT","5000");
            prop.setProperty("THREADS","10");
            prop.setProperty("IP_ADDR","localhost");
            prop.store(fos,null);
            fos.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args){
        ServerGui sg = new ServerGui();
        sg.setVisible(true);
    }

}
