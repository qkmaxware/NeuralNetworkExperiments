 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import static networks.XORindexedNetwork.rng;
import static networks.XORnetwork.Out;
import static networks.XORnetwork.in_data;
import static networks.XORnetwork.out_data;

/**
 *
 * @author Colin
 */
public class IndexedNetwork {

    private int size;
    
    private double bias = 1;
    
    private double[][][] weights;
    private double[][] biases;
    private double[][] Xs;
    private double[][] Ss;
    private double[][] Zs;
    
    public static void main(String[] args){
        Out("XOR (2-2-1) Neural Network");
        
        IndexedNetwork net = new IndexedNetwork();
        net.Randomize();
        
        int epochs = 500;
        int iterations = 2000;
        double accuracy = 0.1;
        for(int e = 0; e < epochs; e++){
            //Randomize weights
            net.Randomize();
            
            //Train network
            for(int i = 0; i < iterations; i++){
                int ind = rng.nextInt(in_data.length);
                double[] in = in_data[ind];
                double out = out_data[ind];

                net.Backpropogate(in, new double[]{out}, 0.1);
            }

            //Test network
            double teztAccuracy = 0;
            for(int i = 0; i < in_data.length; i++){
                double[] y = net.Feed(in_data[i]);
                double a =(Math.round(Math.abs(y[0] - out_data[i]) * 100.0) / 100.0 );
                teztAccuracy += a;
                
            }
            teztAccuracy /= in_data.length;
        
            //If bad, resume epoch
            if(teztAccuracy < accuracy){
                Out("Network successfully trained after "+(e+1)+" epochs to an accuracy of: "+((1-teztAccuracy)*100)+"%");
                break;
            }
            else if(e == epochs-1){
                Out("Network failed to be trained to accuracy("+accuracy+")"+". Current accuracy: "+((1-teztAccuracy)*100)+"%");
            }
        }
        
        Scanner s = new Scanner(System.in);
        Out("Try network with: comma separated input. Type \"quit\" to stop testing");
        while(true){
            String in = s.nextLine().trim().toLowerCase();
            if(in.equals("quit")){
                break;
            }
            else{
                String[] ins = in.split(",");
                double one = 0; double two = 0;
                for(int i = 0; i < Math.min(2, ins.length); i++){
                    if(i == 0)
                        one = Double.parseDouble(ins[i]);
                    else
                        two = Double.parseDouble(ins[i]);
                }
                double[] o = net.Feed(new double[]{one, two});
                double ro = (Math.round(o[0] * 100.0) / 100.0);
                Out("Calculated result: " +ro);
            }
        }
    }
    
    public IndexedNetwork(){
        int inputSize = 2;
        int outputSize = 1;
        int[] layers = new int[]{3, outputSize};
    
        this.weights = new double[layers.length][][];
        this.Xs = new double[layers.length][];
        this.Ss = new double[layers.length][];
        this.Zs = new double[layers.length][];
        this.biases = new double[layers.length][];
        
        int height = inputSize;
        for(int i = 0; i < weights.length; i++){
            this.Xs[i] = new double[height];
            this.weights[i] = new double[height][layers[i]];
            this.Ss[i] = new double[layers[i]];
            this.Zs[i] = new double[layers[i]];
            this.biases[i] = new double[layers[i]];
            height = layers[i];
        }
        
        Randomize();
        
        size = this.weights.length;
    }
    
    public static Random rng = new Random(); 
    
    public static double Range(double min, double max){
        double t = rng.nextDouble();
        return (1 - t) * min + t * max;
    }
    
    public void Randomize(){
        for(int i = 0; i < this.weights.length; i++){
            double[][] weight = this.weights[i];
            for(int x = 0; x < weight.length; x++){
                for(int y = 0; y < weight[x].length; y++){
                    weight[x][y] = Range(-1,1);
                }
            }
        }
        
        for(int i = 0; i < this.biases.length; i++){
            double[] bias = this.biases[i];
            for(int x = 0; x < bias.length; x++){
                bias[x] = Range(-1,1);
            }
        }
    }
    
    public double[] Feed(double[] inputs){
        this.Xs[0] = inputs;
        
        for(int i = 0; i < weights.length; i++){
            double[] X = this.Xs[i];
            double[] B = this.biases[i];
            double[][] W = this.weights[i];
            double[] S = this.Ss[i];
            double[] Z = this.Zs[i];
            
            for(int s = 0; s < S.length; s++){
                //Compute sums
                S[s] = 0;
                
                for(int x = 0; x < X.length; x++){
                    S[s] += W[x][s] * X[x];
                }
                
                S[s] += bias * B[s];
                
                //Compute activations
                Z[s] = sigmoid(S[s]);
            }
            if(i != weights.length - 1)
                Xs[i+1] = Z;
        }
        
        return this.Zs[this.Zs.length - 1];
    } 
    
    public void Backpropogate(double[] inputs, double[] outputs, double eta){
        double[] predicted = Feed(inputs);
        double[] actual = outputs;
        
        double[][] del = new double[size][];
        double[][] dwb = new double[size][];
        double[][][] dw = new double[size][][];
        
        //For each neuron in the output layer
        double[] delta3 = new double[predicted.length];
        double[][] dw2 = new double[this.weights[size - 1].length][this.weights[size - 1][0].length];
        double[] dwb2 = new double[this.biases[size - 1].length];
        for(int i = 0; i < predicted.length; i++){
            delta3[i] = (actual[i] - predicted[i]) * sigmoidPrime(this.Ss[size - 1][i]);
        }
        for(int r = 0; r < dw2.length; r++){
            for(int c = 0; c < dw2[r].length; c++){
                dw2[r][c] = eta * delta3[c] * this.Xs[size - 1][r];
            }
        }
        for(int i = 0; i < this.biases[size - 1].length; i++){
            dwb2[i] = eta * delta3[i] * bias;
        }
        del[size - 1] = delta3;
        dw[size - 1] = dw2;
        dwb[size - 1] = dwb2;
        
        for(int l = size - 2; l >= 0; l--){
            //For each neuron in the hidden layer
            double[] delta2 = new double[this.Zs[l].length];
            double[][] dw1 = new double[this.weights[l].length][this.weights[l][0].length];
            double[] dwb1 = new double[this.biases[l].length];
            for(int i = 0; i < delta2.length; i++){
                double sum = 0;
                for(int c = 0; c < this.weights[l+1][i].length; c++){
                    sum += this.weights[l+1][i][c] * del[l+1][c]; //delta3[c];
                }
                delta2[i] = sigmoidPrime(this.Ss[l][i]) * sum;
            }
            
            for(int r = 0; r < dw1.length; r++){
                for(int c = 0; c < dw1[r].length; c++){
                    dw1[r][c] = eta * delta2[c] * Xs[l][r];
                }
            }
            for(int i = 0; i < this.biases[l].length; i++){
                dwb1[i] = eta * delta2[i] * bias;
            }
            
            del[l] = delta2;
            dw[l] = dw1;
            dwb[l] = dwb1;
        }
        
        
        //Apply weights
        for(int i = 0; i < dw.length; i++){
            double[][] w = dw[i];
            for(int r = 0; r < w.length; r++){
                for(int c = 0; c < w[r].length; c++){
                    this.weights[i][r][c] +=  w[r][c];
                }
            }
            
            for(int b = 0; b < dwb[i].length; b++){
                this.biases[i][b] += dwb[i][b];
            }
        }
    }
    
    public double sigmoid(double x){
        double r = 1.7 * Math.tanh(0.6*x);
        return r;
    }
    
    public double sigmoidPrime(double x){
        //4.08 * (Math.cosh(0.6*x) * Math.cosh(0.6*x)) / ((Math.cosh(1.2 * x) + 1) * (Math.cosh(1.2 * x) + 1))
        double r = 4.08 * (Math.cosh(0.6*x) * Math.cosh(0.6*x)) / ((Math.cosh(1.2 * x) + 1) * (Math.cosh(1.2 * x) + 1));
        return r;
    }
}
