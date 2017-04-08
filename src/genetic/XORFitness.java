/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import networks.XORnetwork;

/**
 *
 * @author Colin
 */
public class XORFitness implements IFitnessTest{
    
    private double[][] xor_data = new double[][]{
            {-1,-1,-1},
            {-1,1,1},
            {1,-1,1},
            {1,1,-1}
    };
    
    @Override
    public double TestFitness(IGenome genome) {
        DoubleGenome weights = (DoubleGenome)genome;
        
        //Convert to XORnetwork
        XORnetwork net = ConvertGenomeToNetwork(weights);
        
        //Run on XOR training
        double error = FindError(net);
        
        return error;
    }
    
    public double FindError(XORnetwork net){
        double error = 0;
        for(int i = 0; i < xor_data.length; i++){
            //FF on xor 
            double real = net.Feed(xor_data[i][0], xor_data[i][1]);
            error += Math.abs(real - xor_data[i][2]);
        }
        
        //Calculate average error
        error /= xor_data.length;
        return error;
    }
   
    public XORnetwork ConvertGenomeToNetwork(DoubleGenome genome){
        XORnetwork net = new XORnetwork();
        
        net.w1_11 = genome.GetGene(0);
        net.w1_12 = genome.GetGene(1);
        net.w1_21 = genome.GetGene(2);
        net.w1_22 = genome.GetGene(3);
        net.w1_b1 = genome.GetGene(4);
        net.w1_b2 = genome.GetGene(5);
        net.w2_11 = genome.GetGene(6);
        net.w2_21 = genome.GetGene(7);
        net.w2_b1 = genome.GetGene(8);
        
        return net;
    }
    
    public DoubleGenome ConvertNetworkToGenome(XORnetwork net){
        double[] weights = new double[]{
            net.w1_11,
            net.w1_12,
            net.w1_21,
            net.w1_22,
            net.w1_b1,
            net.w1_b2,
            net.w2_11,
            net.w2_21,
            net.w2_b1
        };
        DoubleGenome dg = new DoubleGenome(weights);
        return dg;
    }
    
}
