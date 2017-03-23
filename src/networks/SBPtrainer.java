/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.util.Random;
import plus.system.Debug;

/**
 *
 * @author Colin
 */
public class SBPtrainer {
 
    public static boolean Train(NeuralNetwork net, double[][] input, double[][] output, int epochs, int iterations, double accuracy, double learningRate){
        for(int e = 0; e < epochs; e++){
            Debug.Log("--- Starting epoch "+e);
            
            //Randomize network weights
            net.Randomize();
            
            //Train network
            for(int i = 0; i < iterations; i++){
                int ind = rng.nextInt(input.length);
                double[] in = input[ind];
                double[] out = output[ind];

                net.Backpropagate(in, out, learningRate);
            }
            
            //Test network
            double teztAccuracy = 0;
            for(int i = 0; i < input.length; i++){
                double[] y = net.Feed(input[i]).GetData();
                double a = 0;
                for(int j = 0; j < y.length; j++){
                    a += Math.abs(y[j] - output[i][j]);
                }
                teztAccuracy += (a / y.length);
            }
            teztAccuracy /= input.length;
            
            if(teztAccuracy < accuracy){
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean Validate(NeuralNetwork net, double accuracy, double[][] validInput, double[][] validOutput){
        float acc = 0;
                    
        for(int k= 0; k < validInput.length; k++){
            double[] in = validInput[k];
            double[] vout = validOutput[k];
            
            double[] rout = net.Feed(in).GetData();
            
            double teztAccuracy = 0;
            for(int i = 0; i < in.length; i++){
                double a = 0;
                for(int j = 0; j < vout.length; j++){
                    a += Math.abs(vout[j] - rout[j]);
                }
                teztAccuracy += (a / vout.length);
            }
            teztAccuracy /= in.length;
            acc += teztAccuracy;
        }
        
        acc /= validInput.length;
        
        return acc <= accuracy;
    }
    
    public static Random rng = new Random(); 
    
    public static double Range(double min, double max){
        double t = rng.nextDouble();
        return (1 - t) * min + t * max;
    }
    
}
