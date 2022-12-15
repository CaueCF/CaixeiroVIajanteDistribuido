/*
 * To change this license header, choose License Headers stringIn Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template stringIn the editor.
 */
package caixeiroviajante.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cauê Castello Ferreira
 */
public class MeuSocket {

    private Socket meuSocket;
    private PrintWriter stringOut;
    private BufferedReader stringIn;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public void startConnection(String ip, int port) {
        try {
            meuSocket = new Socket(ip, port);                        
            objOut = new ObjectOutputStream(meuSocket.getOutputStream());
            objIn = new ObjectInputStream(meuSocket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(MeuSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void enviaMensagem(String msg) {
        stringOut.println(msg);
    }

    public String recebeMensagem() {

        String resp = "";

        try {
            resp = stringIn.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MeuSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return resp;
    }

    public Object recebeObj() {

        Object obj = null;

        try {
            obj = objIn.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MeuSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return obj;
    }

    public void enviaObj(Object obj) {

        try {
            objOut.writeObject(obj);            

        } catch (IOException ex) {
            Logger.getLogger(MeuSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stopConnection() {      

        try {
            objIn.close();
            objOut.close();
            meuSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(MeuSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
