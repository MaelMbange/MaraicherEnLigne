package RTI.Server;

import RTI.PROJET.protocoles.protocolePayementV2;
import RTI.PROJET.serverBase.base.AbstractMainServerThread;
import RTI.PROJET.serverBase.base.OnDemandMainServerThread;
import RTI.PROJET.serverBase.base.OnPoolMainServerThread;
import RTI.PROJET.serverBase.utils.Logs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

public class ServerGui extends JFrame implements Logs {
    private JPanel panel1;
    private JLabel LabelProtocolV2;
    private JRadioButton radioButtonDemande;
    private JRadioButton radioButtonPool;
    private JButton buttonDemarrer;
    private JButton buttonStop;
    private JTable tableLogs;
    private JButton ButtonViderLogs;
    private JLabel LabelPort;
    private JLabel LabelThreads;

    private AbstractMainServerThread threadServer;
    private String IPAddress;
    private int port;
    private int port_secure;

    private ButtonGroup bg;
    private Logs logger = new Logs() {
        @Override
        public void writeLog(String s) {
            try(FileWriter fos = new FileWriter("./logs",true))
            {
                fos.append(s);
                fos.append("\n");
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    };

    public ServerGui() {
        initcomponnent();

        threadServer = null;
    }

    private void initcomponnent(){
        String[] columnNames = {"Threads","Action"};
        tableLogs.setModel(new DefaultTableModel(new Object[]{"Threads","Action"},0));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel1);
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
                    protocolePayementV2 protocole = new protocolePayementV2(ServerGui.this,IPAddress);

                    if (radioButtonDemande.isSelected())
                        threadServer = new OnDemandMainServerThread(port_secure,protocole,ServerGui.this);

                    if (radioButtonPool.isSelected())
                    {
                        int taillePool = Integer.parseInt(LabelThreads.getText());
                        threadServer = new OnPoolMainServerThread(port,protocole,taillePool,ServerGui.this);
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
                    JOptionPane.showMessageDialog(buttonDemarrer.getParent(),ex.getMessage());
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

        buttonDemarrer.setEnabled(true);
        buttonStop.setEnabled(false);
        pack();
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
                JOptionPane.showMessageDialog(this,"Please Configure the properties file.");
                System.exit(1);
            }
            FileInputStream fis = new FileInputStream("./properties");
            Properties prop = new Properties();
            prop.load(fis);
            if(prop.size() < 4){
                saveProperties();
                JOptionPane.showMessageDialog(this,"Please Configure the properties file.");
                System.exit(1);
            }
            if(prop.getProperty("DB_IP_ADDR").equalsIgnoreCase("__PUT_SERVER_ADDRESS__")){
                JOptionPane.showMessageDialog(this,"Please Configure the database server address.");
                System.exit(1);
            }
            LabelPort.setText(prop.getProperty("PORT_ACHAT") + "/S-" + prop.getProperty("PORT_ACHAT_SECURE"));
            LabelThreads.setText(prop.getProperty("THREADS"));
            IPAddress = prop.getProperty("DB_IP_ADDR");
            port = Integer.parseInt(prop.getProperty("PORT_ACHAT"));
            port_secure = Integer.parseInt(prop.getProperty("PORT_ACHAT_SECURE"));
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
            prop.setProperty("PORT_ACHAT","5500");
            prop.setProperty("PORT_ACHAT_SECURE","6500");
            prop.setProperty("THREADS","10");
            prop.setProperty("DB_IP_ADDR","__PUT_SERVER_ADDRESS__");
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
