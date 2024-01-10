package org.secure.serveur;

import org.secure.protocoles.ProtocoleRTI;
import org.secure.protocoles.Vespaps;
import org.secure.utils.interfaces.Logs;
import org.secure.utils.interfaces.Protocol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

public class Graphic extends JFrame implements Logs {
    private JButton startButton;
    private JButton stopButton;
    private JTable tableLog;
    private JButton clearButton;
    private JPanel panel1;

    private ServeurMain threadServer;
    private String ipServeurDataBase;
    private int portServeur;

    public Graphic(){
        tableLog.setModel(new DefaultTableModel(new Object[]{"Threads","Action"},0));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel1);

        loadProperties();

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadServer.interrupt();

                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    //IP_ADDR est pour le lien avec la base de donnée
                    //ProtocoleRTI protocole = new ProtocoleRTI(Graphic.this,ipServeurDataBase);
                    Protocol protocole = new Vespaps(Graphic.this,ipServeurDataBase);

                    threadServer = new ServeurMain(portServeur,protocole,Graphic.this);

                    videLogs();
                    threadServer.start();

                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(startButton.getParent(),"Erreur de Port et/ou taille Pool !","Erreur...",JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(startButton.getParent(),ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(startButton.getParent(),ex.getMessage(),"Erreur...",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videLogs();
            }
        });

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        pack();
    }

    private void videLogs(){
        DefaultTableModel modele = (DefaultTableModel)tableLog.getModel();
        modele.setRowCount(0);
    }

    private void loadProperties(){
        try {
            if(!new File("./properties").exists()){
                saveProperties();
                JOptionPane.showMessageDialog(this,"Please Configure the properties file.");
                System.exit(1);
            }
            FileInputStream fis = new FileInputStream("./properties");
            Properties prop = new Properties();
            prop.load(fis);
            if(prop.size() < 2){
                saveProperties();
                JOptionPane.showMessageDialog(this,"Please Configure the properties file.");
                System.exit(1);
            }
            if(prop.getProperty("DB_IP_ADDR").equalsIgnoreCase("__PUT_SERVER_ADDRESS__")){
                JOptionPane.showMessageDialog(this,"Please Configure the database server address.");
                System.exit(1);
            }
            ipServeurDataBase = prop.getProperty("DB_IP_ADDR");
            portServeur = Integer.parseInt(prop.getProperty("PORT_PAIEMENT_SECURE"));
            fis.close();

            System.out.println("ipDB = " + ipServeurDataBase);
            System.out.println("port serveur = " + portServeur);
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
            prop.setProperty("PORT_PAIEMENT_SECURE","6500");
            prop.setProperty("DB_IP_ADDR","__PUT_SERVER_ADDRESS__");
            prop.store(fos,null);
            fos.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void writeLog(String message) {
        DefaultTableModel modele = (DefaultTableModel)tableLog.getModel();
        Vector<String> ligne = new Vector<>();
        ligne.add(Thread.currentThread().getName());
        ligne.add(message);
        modele.insertRow(modele.getRowCount(),ligne);
    }

    public static void main(String[] args){
        Graphic sg = new Graphic();
        sg.setVisible(true);
    }
}
