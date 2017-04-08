/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import plus.system.Random;

/**
 *
 * @author Colin
 */
public class DoubleGenome implements IGenome{
    
    private double[] genes;
    
    public DoubleGenome(double... genes){
        this.genes = genes;
    }

    public double GetGene(int i){
        return genes[i];
    }
    
    @Override
    public int Size(){
        return genes.length;
    }
    
    @Override
    public double Difference(IGenome other){
        DoubleGenome o = (DoubleGenome)other;
        double dif = 0;
        for(int g = 0; g < genes.length; g++){
            dif += Math.abs(this.genes[g] - o.genes[g]);
        }
        return dif;
    }
    
    @Override
    public IGenome Mutate() {
        double[] ng = new double[genes.length];
        for(int i = 0; i < ng.length; i++){
            ng[i] = genes[i] + Random.Range(-1.0f, 1.0f);
        }
        return new DoubleGenome(ng);
    }

    @Override
    public IGenome[] Crossover(IGenome other) {
        if(!(other instanceof DoubleGenome))
            throw new RuntimeException("Invalid crossover");
        
        DoubleGenome og = (DoubleGenome)other;
        
        double[] c1 = new double[genes.length];
        double[] c2 = new double[genes.length];
        
        for(int i = 0; i < c1.length; i++){
            int mask = Random.Range(0, 1);
            if(mask == 0){
                c1[i] = genes[i];
                c2[i] = og.genes[i];
            }
            else{
                c1[i] = og.genes[i];
                c2[i] = genes[i];
            }
        }
        
        return new DoubleGenome[]{new DoubleGenome(c1), new DoubleGenome(c2)};
    }
    
    
    
}
