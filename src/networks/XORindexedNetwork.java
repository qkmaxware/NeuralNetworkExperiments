/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.util.Random;
import java.util.Scanner;
import static networks.XORnetwork.Out;
import static networks.XORnetwork.in_data;
import static networks.XORnetwork.out_data;

/**
 *
 * @author Colin
 */
public class XORindexedNetwork {
    
    private double bias = 1;
    
    private double[] Xs = new double[2];
    private double[] B1 = new double[2];
    private double[][] W1 = new double[2][2];
    private double[] Zs = new double[2];
    private double[] Hs = new double[2];
    
    private double[] B2 = new double[1];
    private double[][] W2 = new double[2][1];
    private double[] Zo = new double[1];
    private double[] Oo = new double[1];
    
    public static Random rng = new Random(); 
    
    public static double Range(double min, double max){
        double t = rng.nextDouble();
        return (1 - t) * min + t * max;
    }
    
    public static void main(String[] args){
        Out("XOR (2-2-1) Neural Network");
        
        XORindexedNetwork net = new XORindexedNetwork();
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

                net.Backpropogate(in[0], in[1], out, 0.1);
            }

            //Test network
            double teztAccuracy = 0;
            for(int i = 0; i < in_data.length; i++){
                double y = net.Feed(in_data[i][0], in_data[i][1]);
                double a =(Math.round(Math.abs(y - out_data[i]) * 100.0) / 100.0 );
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
                double o = net.Feed(one, two);
                double ro = (Math.round(o * 100.0) / 100.0);
                Out("Calculated result: " +ro);
            }
        }
    }
    
    public void Randomize(){
        W1[0][0] = Range(-1,1);
        W1[0][1] = Range(-1,1);
        W1[1][0] = Range(-1,1);
        W1[1][1] = Range(-1,1);
        B1[0] = Range(-1,1);
        B2[0] = Range(-1,1);
        
        W1[0][0] = Range(-1,1);
        W2[1][0] = Range(-1,1);
        B2[0] = Range(-1,1);
    }
    
    public double Feed(double x1, double x2){
        Xs[0] = x1;
        Xs[1] = x2;
        
        //First layer
        Zs[0] = Xs[0] * W1[0][0] + Xs[1] * W1[1][0] + bias * B1[0];
        Zs[1] = Xs[0] * W1[0][1] + Xs[1] * W1[1][1] + bias * B1[1];
        
        Hs[0] = sigmoid(Zs[0]);
        Hs[1] = sigmoid(Zs[1]);
        
        //Output layer
        Zo[0] = Hs[0] * W2[0][0] + Hs[1] * W2[1][0] + bias * B2[0];
        Oo[0] = sigmoid(Zo[0]);
        
        return Oo[0];
    }
    
    public void Backpropogate(double x1, double x2, double y,double eta){
        double prediction = Feed(x1,x2);
        double actual = y;
        
        double delta3 = (actual - prediction) * sigmoidPrime(Zo[0]);            //Error at output
        double[][] dw2 = new double[2][1];                                      
        dw2[0][0] = eta*delta3*Hs[0];                                           //Weight update for h1->out
        dw2[1][0] = eta*delta3*Hs[1];                                           //Weight update for h2->out
        double dwb2 = eta*delta3*bias;                                          //Weight update for bias->out
        
        double[] delta2 = new double[2];
        delta2[0] = sigmoidPrime(Zs[0]) * (W2[0][0]*delta3);                    //Error at hidden node 1
        delta2[1] = sigmoidPrime(Zs[1]) * (W2[1][0]*delta3);                    //Error at hidden node 2
        
        double[][] dw1 = new double[2][2];
        dw1[0][0] = eta * delta2[0] * Xs[0];                                    //Weight update from x1->h1
        dw1[0][1] = eta * delta2[1] * Xs[0];                                    //Weight update from x1->h2
        dw1[1][0] = eta * delta2[0] * Xs[1];                                    //Weight update from x2->h1
        dw1[1][1] = eta * delta2[1] * Xs[1];                                    //Weight update from x2->h2
        
        double[] dwb1 = new double[2];
        dwb1[0] = eta * delta2[0] * bias;                                       //Weight update from bias->h1
        dwb1[1] = eta * delta2[1] * bias;                                       //Weight update from bias->h2
        
        //Update weights
        W1[0][0] += dw1[0][0];
        W1[0][1] += dw1[0][1];
        W1[1][0] += dw1[1][0];
        W1[1][1] += dw1[1][1];
        
        B1[0] += dwb1[0];
        B1[1] += dwb1[1];
        
        W2[0][0] += dw2[0][0];
        W2[1][0] += dw2[1][0];
        
        B2[0] += dwb2;
        
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
