/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caixeiroviajante.model;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Caue Castello Ferreira
 */
public interface Graph extends Serializable{
    
    public void setEdge(int ori, int target, double weight);
    
    public ArrayList<Integer> getAdj(int node);
    
    public void printGraph();        
    
    public double getWeight(int fromVertex, int toVertex);
    
    public int getVertexNum();
}
