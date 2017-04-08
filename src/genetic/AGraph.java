/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import plus.system.functional.Action1;

/**
 *
 * @author Colin
 */
public class AGraph {
    
    private int states = 0;
    private int[] connectionMatrix;
    private double[] weightMatrix;
    
    public AGraph(int states){
        this.states = states;
        connectionMatrix = new int[states * states];
        weightMatrix = new double[states * states];
    }
    
    private int to1d(int x, int y){
        return y*states + x;
    }
    
    /**
     * Number of states in this graph
     * @return 
     */
    public int Count(){
        return states;
    }
    
    /**
     * Insert an un-connected state
     * @return 
     */
    public int InsertState(){
        int newStateId = this.states;
        
        //New parameters
        int newSize = this.states + 1;
        int[] nc = new int[newSize * newSize];
        double[] nw = new double[newSize * newSize];
        
        //Copy
        for(int i = 0; i < this.connectionMatrix.length; i++){
            int enabled = this.connectionMatrix[i];
            double value = this.weightMatrix[i];
            int py = i / this.states;   
            int px = i % this.states;
            int ind = py * newSize + px;
            nc[ind] = enabled;
            nw[ind] = value;
        }
        
        //Set new parameters
        this.states = newSize;
        this.connectionMatrix = nc;
        this.weightMatrix = nw;
        
        return newStateId;
    }
    
    /**
     * Insert a state between two connected states
     * @param startId
     * @param endId 
     * @return  
     */
    public int Split(int startId, int endId){
        int id = InsertState();
        
        //Set weights for the connections
        //Disable original connection, but preserve weight
        this.EnableConnection(startId, endId, false);
        double v = this.GetWeight(startId, endId);
        
        //Create a new connection from start to 'new' with the same weight
        this.EnableConnection(startId, id, true);
        this.SetWeight(startId, id, v);
        
        //Create a new connection from 'new' to end with a weight of 1
        this.EnableConnection(id, endId, true);
        this.SetWeight(id, endId, 1);
        
        return id;
    }
    
    /**
     * Enable or disable a connection between two states
     * @param startId
     * @param endId
     * @param enabled 
     */
    public void EnableConnection(int startId, int endId, boolean enabled){
        this.connectionMatrix[to1d(endId,startId)] = (enabled ? 1 : 0);
    }
    
    /**
     * Set the weight of the connection between two states
     * @param startId
     * @param endId
     * @param value 
     */
    public void SetWeight(int startId, int endId, double value){
        this.weightMatrix[to1d(endId,startId)] = value;
    }
    
    /**
     * Test if a connection exists between two states
     * @param startId
     * @param endId
     * @return 
     */
    public int IsEnabled(int startId, int endId){
        return this.connectionMatrix[to1d(endId, startId)];
    }
    
    /**
     * Get the weight value of a connection between two states
     * @param startId
     * @param endId 
     * @return  
     */
    public double GetWeight(int startId, int endId){
        return this.weightMatrix[to1d(endId, startId)];
    }
    
    /**
     * String representation of this graph 
     * @return 
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for(int i = 0; i < this.states; i++){
            for(int j = 0; j < this.states; j++){
                builder.append(this.GetWeight(i, j) * this.IsEnabled(i, j));
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
    
}
