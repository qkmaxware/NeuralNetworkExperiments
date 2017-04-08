/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import genetic.GeneticAlgorithm.Flex;
import networks.XORnetwork;

/**
 *
 * @author Colin
 */
public class XorGeneticTest {
    
    static int initial_pop_size = 100;
    static int generations = 1000;
    
    public static void main(String[] args){
        Flex flex = new Flex();
            flex.crossover = 0;
            flex.elite = 1;     //0.2 = 1/5
            flex.mutation = 4;  //0.8 = 4/5

        GeneticAlgorithm ga = new GeneticAlgorithm(flex);
        
        XORFitness fitness = new XORFitness();
        
        IGenome[] initial_pop = new IGenome[initial_pop_size];
        for(int i = 0; i < initial_pop_size; i++){
            initial_pop[i] = fitness.ConvertNetworkToGenome(new XORnetwork()); //automatically randomizes
        }
        
        System.out.println("Starting evolution");
        IGenome[] final_pop = ga.Evolve(initial_pop, generations, fitness);
        System.out.println("Evolution competed");
        
        XORnetwork trained = fitness.ConvertGenomeToNetwork((DoubleGenome)final_pop[0]);
        System.out.println("First Genome is accurate to within: "+fitness.FindError(trained));
    }
    
}
