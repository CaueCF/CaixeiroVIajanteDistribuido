/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caixeiroviajante;

import caixeiroviajante.model.AdjMatrix;
import caixeiroviajante.model.Graph;
import java.util.ArrayList;

/**
 *
 * @author Cauê Castello Ferreira
 */
public class Server {

    public static ArrayList<Integer> melhor;
    public static double menor;
    public static MeuServerSocket mss;

    public static void main(String[] args) {

        mss = new MeuServerSocket();

        System.out.println("Antes de conectar com o cliente");
        
        
        System.out.println("socket: "+mss);
        
        
        mss.startConnection(80);
        
        System.out.println("Conexão criada");
        
        menor = Integer.MAX_VALUE;
        melhor = new ArrayList<Integer>();

        //System.out.println(mss.recebeMensagem());
        while (true) {

            menor = Integer.MAX_VALUE;
            melhor = new ArrayList<Integer>();

            Graph graph = (AdjMatrix) mss.recebeObj();
//            System.out.println("Graph: " + graph);

            ArrayList<Integer> caminho = (ArrayList<Integer>) mss.recebeObj();
//            System.out.println("Arraylist: " + caminho);

            double custo = (Double) mss.recebeObj();
//            System.out.println("Double: " + custo);

            boolean visitado[] = new boolean[graph.getVertexNum()];

            for (int i : caminho) {
                visitado[i] = true;
            }

            busca(graph, caminho, custo, visitado);

            System.out.println("\n---Enviando resultado---\n");
            
//            System.out.println("Enviando melhor: " + melhor);
            mss.enviaObj(melhor);

//            System.out.println("Enviando: " + custo+"\n\n");
            mss.enviaObj(menor);

        }

    }

    public static void busca(Graph g, ArrayList<Integer> caminho, double custo, boolean v[]) {

        System.out.println("\n ---Entrou na chamada---\n");
//        System.out.println("Parcial: "+ caminho+"\n peso: "+custo+"\n");
        
        if ((chegou(v) == caminho.size()) && (g.getWeight(caminho.get(caminho.size() - 1), caminho.get(0)) > 0)) {

            custo = custo + g.getWeight(caminho.get(caminho.size() - 1), caminho.get(0));

            caminho.add(0);

            if (custo <= menor) {

                setMelhor(caminho);
                menor = custo;
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
                        
//                        System.out.println("\n----- Recursividade -----");
//                        System.out.println("Caminho: "+caminho+" de "+ultimo+" para "+n+" com custo "+(custo + g.getWeight(ultimo, n)));
                        
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
            i++;
        }
        return i;
    }

    public static void zeraVisitados(boolean v[]) {
        for (boolean i : v) {
            i = false;
        }
    }

    public static void setMelhor(ArrayList<Integer> m) {
        melhor.clear();
        melhor.addAll(m);
    }

}
