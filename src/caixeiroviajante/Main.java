/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caixeiroviajante;

import caixeiroviajante.controller.FileManager;
import caixeiroviajante.model.AdjMatrix;
import caixeiroviajante.model.Graph;
import caixeiroviajante.model.MeuSocket;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.Thread;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.swing.JOptionPane;
/**
 *
 * @author Caue Castello Ferreira
 */
public class Main {

    public static ArrayList<Integer> melhor;
    public static double menor;
    public static ArrayList<MeuSocket> servers;
    public static boolean disponiveis[];
    private static ReentrantLock rLock;
    public static int resultado;

    public static void main(String[] args) {

        resultado = 0;

        rLock = new ReentrantLock();

        //System.out.println("Antes da conexão com o servidor");

        MeuSocket s1 = new MeuSocket();
        s1.startConnection("35.222.123.35", 80);
        
        MeuSocket s2= new MeuSocket();
        s2.startConnection("34.67.92.213", 80);

        servers = new ArrayList<MeuSocket>();

        servers.add(s1);
        servers.add(s2);

        disponiveis = new boolean[servers.size()];

        Arrays.fill(disponiveis, true);

        FileManager fileManager = new FileManager();
        ArrayList<String> text = fileManager.stringReader("./data/Teste.txt");

        int nVertex;
        Graph graph = new AdjMatrix(0);

        nVertex = Integer.parseInt(text.get(0));

        for (int i = 0; i < nVertex; i++) {

            String line = text.get(i);

            if (i == 0) {

                //Integer.parseInt(line.trim());
                graph = new AdjMatrix(nVertex);
            } else {

                int xi = Integer.parseInt(line.split(" ")[0]);
                int yi = Integer.parseInt(line.split(" ")[1]);

                int aux = i + 1;

                while (aux <= nVertex) {

                    String nextLine = text.get(aux);

                    int x2 = Integer.parseInt(nextLine.split(" ")[0]);
                    int y2 = Integer.parseInt(nextLine.split(" ")[1]);

                    double peso = calculaPeso(xi, yi, x2, y2);

                    graph.setEdge(i - 1, aux - 1, peso);
                    graph.setEdge(aux - 1, i - 1, peso);

                    aux++;
                }
            }
        }

        //graph.printGraph();
        ArrayList<Integer> caminho = new ArrayList<Integer>();
        melhor = new ArrayList<Integer>();

        boolean visitado[] = new boolean[nVertex];
        zeraVisitados(visitado);

        menor = Integer.MAX_VALUE;

        caminho.add(0);

        visitado[0] = true;
//        System.out.println("\n--Antes da chamada do backtracking--\n");

        busca(graph, caminho, 0, visitado);

        while (true) {            
            System.out.println("Esperando para fechar conexão...\n");
            if (resultado == nVertex-1) {      
                System.out.println("Fechando conexão....\n\n");
                for (MeuSocket ms : servers) {
                    ms.stopConnection();
                }
                break;
            }
        }

        System.out.println("O melhor caminho é " + melhor + " com o custo " + menor);
    }

    public static void busca(Graph g, ArrayList<Integer> caminho, double custo, boolean v[]) {

//        System.out.println("\nEstado: "+chegou(v)+"\n caminho parcial: "+caminho+" peso: "+custo+"\n");
        
        if (chegou(v) >= 1) {

            //encontra o ultimo vertice visitado           
            //recebe uma lista dos vértices adjacentes ao ultimo
            ArrayList<Integer> vizinhos = g.getAdj(caminho.get(caminho.size() - 1));

            //Percorre todos os vizinhos
            for (int n : vizinhos) {

                //caso ele já não esteja no caminho, é adicionado e seu custo é computado
                if (!v[n]) {

                    v[n] = true;

                    double c = custo + g.getWeight(caminho.get(caminho.size() - 1), n);

                    caminho.add(n);

                    //cria thread
                    Thread a;
                    a = new Thread() {
                        @Override
                        public void run() {

//                            System.out.println("\n --Entrou na thread--\n");

                            int k = 0;
                            int aux = -1;
                            ArrayList<Integer> cam = null;

                            while (true) {
                                if (k == servers.size()) {
                                    k = 0;
                                }

                                //System.out.println(disponiveis[k]);
                                synchronized (disponiveis) {
                                    if (disponiveis[k]) {
                                        //envia pro server
                                        disponiveis[k] = false;

                                        aux = k;
//                                        System.out.println("Aux: " + aux);
                                    }
                                }

                                if (aux != -1) {

//                                    System.out.println("\nEnviando grafo: " + g);
                                    servers.get(aux).enviaObj(g);

                                    synchronized (caminho) {
//                                        System.out.println("\nEnviando caminho: " + caminho);
                                        servers.get(aux).enviaObj(caminho);
                                    }

//                                    System.out.println("\nEnviando custo: " + c);
                                    servers.get(aux).enviaObj(c);

                                    cam = (ArrayList<Integer>) servers.get(aux).recebeObj();
                                    final double c = (Double) servers.get(aux).recebeObj();
                                    
                                    synchronized (disponiveis) {
                                        disponiveis[k] = true;
                                    }

                                    try {
                                        rLock.lock();

//                                        System.out.println("\n---Autualiza resultado---\n");
                                        resultado++;
                                        
                                        if (c < menor) {
//                                            System.out.println("\n--Atualizou o melhor--\n");
                                            setMelhor(cam);
                                            menor = c;                                            
                                        }                                       
                                    } finally {
                                        rLock.unlock();
                                    }                                    
                                    
                                    caminho.remove(caminho.size() - 1);
                                    
                                    break;
                                }

                                k++;
                            }
                        }
                    };

                    a.start();                    
                }
            }
        } else {
            if (custo < menor) {

                //encontra o ultimo vertice visitado
                int ultimo = caminho.get(caminho.size() - 1);

                //recebe uma lista dos vértices adjacentes ao ultimo
                ArrayList<Integer> vizinhos = g.getAdj(ultimo);

                //Percorre todos os vizinhos
                for (int n : vizinhos) {

                    //caso ele já não esteja no caminho, é adicionado e seu custo é computado
                    if (!v[n]) {

                        v[n] = true;

                        caminho.add(n);
                        busca(g, caminho, custo + g.getWeight(ultimo, n), v);
                        caminho.remove(caminho.size() - 1);

                        v[n] = false;
                    }
                }
            }
        }
    }

    public static int chegou(boolean v[]) {

        int i = 0;

        for (boolean n : v) {
            if (n) {
                i++;
            }
        }
        return i;
    }

    public static void zeraVisitados(boolean v[]) {
        for (boolean i : v) {
            i = false;
        }
    }

    public static void setMelhor(ArrayList<Integer> melhor) {
        Main.melhor.clear();
        Main.melhor.addAll(melhor);
    }

    public static double calculaPeso(int x1, int y1, int x2, int y2) {
        double res;

        res = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

        return res;
    }
}
