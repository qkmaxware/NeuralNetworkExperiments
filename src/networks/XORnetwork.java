/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Colin Halseth
 */
public class XORnetwork {

    //inputs
    public double x1;
    public double x2;
    public double bias = 1.0;
    
    //weights 1
    public double w1_11 = Range(-1,1);
    public double w1_12 = Range(-1,1);
    public double w1_21 = Range(-1,1);
    public double w1_22 = Range(-1,1);
    public double w1_b1 = Range(-1,1);
    public double w1_b2 = Range(-1,1);
    
    //Hidden out
    public double z1;
    public double z2;
    public double h1;
    public double h2;
    
    //weights 2
    public double w2_11 = Range(-1,1);
    public double w2_21 = Range(-1,1);
    public double w2_b1 = Range(-1,1);
    
    //output
    public double zy;
    public double y;
    
    public static Random rng = new Random(); 
    
    public static double Range(double min, double max){
        double t = rng.nextDouble();
        return (1 - t) * min + t * max;
    }
    
    public static void Out(Object o){
        System.out.println(o.toString());
    }
    
    public static double[][] in_data = new double[][]{
        {-1,-1},
        {-1,1},
        {1,-1},
        {1,1}
    };
    public static double[] out_data = new double[]{
        -1,
        1,
        1,
        -1
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Out("XOR (2-2-1) Neural Network");
        
        //Make network
        XORnetwork net = new XORnetwork();
        
        int epochs = 500;
        int iterations = 2000;
        double accuracy = 0.1;
        for(int e = 0; e < epochs; e++){
            //Randomize weights
            net.w1_11 = Range(-1,1);
            net.w1_12 = Range(-1,1);
            net.w1_21 = Range(-1,1);
            net.w1_22 = Range(-1,1);
            net.w1_b1 = Range(-1,1);
            net.w1_b2 = Range(-1,1);
            net.w2_11 = Range(-1,1);
            net.w2_21 = Range(-1,1);
            net.w2_b1 = Range(-1,1);
            
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
    
    public double Feed(double x1, double x2){
        this.x1 = x1;
        this.x2 = x2;
        
        z1 = x1*w1_11 + x2*w1_21 + bias*w1_b1;
        z2 = x1*w1_12 + x2*w1_22 + bias*w1_b2;
        h1 = sigmoid(z1);
        h2 = sigmoid(z2);
        
        zy = h1*w2_11 + h2*w2_21 + bias*w2_b1;
        y = sigmoid(zy);
        
        return y;
    }
    
    public void Backpropogate(double in1, double in2, double ans, double eta){
        
        double prediction = Feed(in1,in2);
        double actual = ans;
        
        //Compute weight updates
        double delta3 = (actual - prediction) * sigmoidPrime(zy);     //Error at output
        double dW21 = eta*delta3*h1;                                  //Weight update for h1->out
        double dW22 = eta*delta3*h2;                                  //Weight update for h2->out
        double dW2b = eta*delta3*bias;                                //Weight update for bias->out
        
        double delta21 = sigmoidPrime(z1) * Sum(w2_11 * delta3);      //Error at hidden node 1
        double delta22 = sigmoidPrime(z2) * Sum(w2_21 * delta3);      //Error at hidden node 2
        double dW111 = eta * delta21 * x1;                            //Weight update from x1->h1
        double dW122 = eta * delta22 * x2;                            //Weight update from x2->h2
        double dW112 = eta * delta22 * x1;                            //Weight update from x1->h2
        double dW121 = eta * delta21 * x2;                            //Weight update from x2->h1
        double dW1b1 = eta * delta21 * bias;                          //Weight update from bias->h1
        double dW1b2 = eta * delta22 * bias;                          //Weight update from bias->h2
        
        //Apply updates
        w2_11 += dW21;
        w2_21 += dW22;
        w2_b1 += dW2b;
        
        w1_11 += dW111;
        w1_12 += dW112;
        w1_21 += dW121;
        w1_22 += dW122;
        w1_b1 += dW1b1;
        w1_b2 += dW1b2;
        
    }
    
    public double Sum(double... params){
        double s = 0;
        for(int i = 0; i < params.length; i++)
            s+= params[i];
        return s;
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
