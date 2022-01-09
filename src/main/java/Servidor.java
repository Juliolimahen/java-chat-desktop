
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author julio
 */
public class Servidor extends Thread {

    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket servidor;
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader isr;
    private BufferedReader bfr;

    private Servidor(Socket con) {
        this.con = con;
        try {
            this.in = this.con.getInputStream();
            this.isr = new InputStreamReader(this.in);
            this.bfr = new BufferedReader(this.isr);
            System.out.print("Novo cliente conectado - ");
            this.nome = this.bfr.readLine();
            System.out.println(this.nome);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        JLabel lblMsg = new JLabel("Porta do servidor");
        JTextField txtPorta = new JTextField("12345");
        Object[] aux = {lblMsg, txtPorta};
        JOptionPane.showMessageDialog(null, aux);

        try {
            servidor = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();

            while (true) {
                System.out.println("Servidor está aguardando uma conexão...");
                Socket con = servidor.accept();
                Servidor s = new Servidor(con);
                s.start();
                //System.out.println("Conexão estabelecida.");
            }

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarPTodos(String msg, BufferedWriter bwAtual) throws IOException {
        for (BufferedWriter bw : clientes) {
            /**/
            if (bw != bwAtual) {
                bw.write(this.nome + ": " + msg + "\n");
                bw.flush();
            }
        }
    }

    @Override
    public void run() {

        try {
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);
            String msg;

            while (!(msg = this.bfr.readLine()).equalsIgnoreCase("sair") && (msg != null)) {
                System.out.println(this.nome + ": " + msg);
                enviarPTodos(msg, bfw);
            }
            if (msg.equalsIgnoreCase("sair")) {
                clientes.remove(bfw);
                bfw.close();
                ouw.close();
                ou.close();
                bfr.close();
                isr.close();
                in.close();
                con.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
