/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caixeiroviajante;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cauê Castello Ferreira
 */
public class MeuServerSocket {

    private ServerSocket serverSocket;
    private Socket meuSocket;
    private PrintWriter stringOut;
    private BufferedReader stringIn;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public void startConnection(int port) {
        try {
            serverSocket = new ServerSocket(port);
            meuSocket = serverSocket.accept();                        
            objOut = new ObjectOutputStream(meuSocket.getOutputStream());
            objIn = new ObjectInputStream(meuSocket.getInputStream());

        } catch (IOException ex) {
            System.out.println("Erro: " + ex);
        }

    }

    public void stopConnection() {

        try {
            objIn.close();
            objOut.close();            
            meuSocket.close();

        } catch (IOException ex) {
            System.out.println("Erro: " + ex);
        }

    }

    public void enviaMensagem(String msg) {
        try {
            stringOut = new PrintWriter(meuSocket.getOutputStream(), true);
            stringOut.println(msg);

        } catch (IOException ex) {
            Logger.getLogger(MeuServerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String recebeMensagem() {

        String resp = "";

        try {

            resp = stringIn.readLine();

        } catch (IOException ex) {
            System.out.println("Erro: " + ex);
        }

        return resp;
    }

    public Object recebeObj() {

        Object obj = null;

        try {
            obj = objIn.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erro: " + ex);
        }

        return obj;
    }

    public void enviaObj(Object obj) {

        try {

            objOut.writeObject(obj);            

        } catch (IOException ex) {
            System.out.println("Erro: " + ex);
        }

    }

}
