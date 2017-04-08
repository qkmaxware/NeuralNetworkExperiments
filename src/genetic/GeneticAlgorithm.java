/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import plus.system.Random;

/**
 *
 * @author Colin
 * @param <T>
 */
public class GeneticAlgorithm{
  
    private static class TableRow{
        public IGenome genome;
        public double fitness;
        public int fitness_rank;
        public double probability_of_selection_from_fitness;
        public double diversity;
        public int diversity_rank;
        public double probability_of_selection_from_combined_rank;
        
        public int CombinedRank(){
            return fitness_rank + diversity_rank;
        }
        
    }
    
    public static class Flex{
        double mutation;
        double elite;
        double crossover;
        
        public double Total(){
            return mutation + elite + crossover;
        }
        
        public double PercentElite(){
            return elite / Total();
        }
        
        public double PercentMutation(){
            return mutation / Total();
        }
        
        public double PercentCrossover(){
            return crossover / Total();
        }
    }
    
    private static Comparator<TableRow> sort_by_fitness = new Comparator<TableRow>(){
        @Override
        public int compare(TableRow t, TableRow t1) {
            return ((Double)t.fitness).compareTo(t1.fitness);
        }
    };
    
    private static Comparator<TableRow> sort_by_diversity = new Comparator<TableRow>(){
        @Override
        public int compare(TableRow t, TableRow t1) {
            return ((Double)t.diversity).compareTo(t1.diversity);
        }
    };
    
    private static Comparator<TableRow> sort_by_combined = new Comparator<TableRow>(){
        @Override
        public int compare(TableRow t, TableRow t1) {
            return ((Integer)t.CombinedRank()).compareTo(t1.CombinedRank());
        }
    };

    private Flex flex;
    
    public GeneticAlgorithm(Flex flex){
        this.flex = flex;
    }
    
    
    public IGenome[] Evolve(IGenome[] initial_population, int generations, IFitnessTest evaluator){
        if(initial_population.length <= 0){
            throw new RuntimeException("Population size must be at least 1.");
        }
        if(generations <= 0){
            throw new RuntimeException("Numver of generations bust be at least 1.");
        }
        
        //Form population 0
        ArrayList<TableRow> table = ConstructTable(Arrays.asList(initial_population));
        
        for(int g = 0; g < generations; g++){
            
            //Test fitness
            for(int i = 0; i < table.size(); i++){
                table.get(i).fitness = TestFitness(table.get(i), evaluator);
            }
            
            //Sort on fitness
            Collections.sort(table, sort_by_fitness);
            
            //Assign fitness rank & probaility of selection
            double last_prob = 0;
            for(int i = 0; i < table.size(); i++){
                table.get(i).fitness_rank = i;
                last_prob = (0.667)*(1-last_prob);
                table.get(i).probability_of_selection_from_fitness = last_prob;
            }
            
            //Is best genome good enough?
            //if(table.get(0).fitness < 0.01){
                //break;
            //}
            
            //Select first elite (number line selection)
            double randoElite = Random.Range(0.0f, 1.0f);
            TableRow firstElite = null; double probTotal = 0;
            for(int i = 0; i < table.size(); i++){
                probTotal += table.get(i).probability_of_selection_from_fitness;
                if(randoElite <= probTotal){
                    firstElite = table.get(i);
                    break;
                }      
            }
    
            //Add first elite to the elite
            int number_of_elites = (int)(initial_population.length * this.flex.PercentElite());
            LinkedList<IGenome> elite = new LinkedList<IGenome>();
            table.remove(firstElite);
            elite.add(firstElite.genome);
            number_of_elites--;
            
            //Calculate diverity scores
            CalculateDiversityScores(table, elite);
            
            //Sort by diversity
            Collections.sort(table, sort_by_diversity);
            
            //Calculate diversity rank
            for(int i = 0; i < table.size(); i++){
                table.get(i).diversity_rank = i;
            }
            
            //Sort by combined score
            Collections.sort(table, sort_by_combined);
            
            //Calculate prob selection by combined scores
            last_prob = 0;
            for(int i = 0; i < table.size(); i++){
                last_prob = (0.667)*(1-last_prob);
                table.get(i).probability_of_selection_from_combined_rank = last_prob;
            }
            
            //Select top x pecent as elites
            while(number_of_elites > 0){
                double choice = Random.Next();
                
                TableRow el = null;
                
                double subTotal = 0;
                for(int i = 0; i < table.size(); i++){
                    subTotal += table.get(i).probability_of_selection_from_combined_rank;
                    if(choice < subTotal){
                       el = table.get(i);
                       elite.add(el.genome);
                       break;
                    }
                }
                table.remove(el);
                
                //Recalc combined probability of selection
                CalculateDiversityScores(table, elite);
                
                number_of_elites--;
            }
            
            
            //Pick children
            int number_of_crossover = (int)(initial_population.length * this.flex.PercentCrossover());
            LinkedList<IGenome> half_breeds = new LinkedList<IGenome>();
            for(int i = 0; i < number_of_crossover / 2; i++){
                int a = Random.Range(0, elite.size());
                int b = Random.Range(0, elite.size());
                
                IGenome[] children = elite.get(a).Crossover(elite.get(b));
                
                half_breeds.add(children[0]);
                half_breeds.add(children[1]);
            }
            
            //Pick mutants
            int number_of_mutants = initial_population.length - elite.size() - half_breeds.size();
            LinkedList<IGenome> mutants = new LinkedList<IGenome>();
            for(int i = 0; i < number_of_mutants; i++){
                mutants.add(elite.get(Random.Range(0, elite.size() - 1)).Mutate());
            }
            
            //Next population 
            LinkedList<IGenome> pop_next = new LinkedList<IGenome>();
            pop_next.addAll(elite);
            pop_next.addAll(half_breeds);
            pop_next.addAll(mutants);
         
            //Construct table for next wave
            table = ConstructTable(pop_next);
        }
        
        return TableToArray(table);
    }
    
    public IGenome[] TableToArray(ArrayList<TableRow> table){
        IGenome[] g = new IGenome[table.size()];
        for(int i = 0; i < g.length; i++){
            g[i] = table.get(i).genome;
        }
        return g;
    }
    
    public ArrayList<TableRow> ConstructTable(Collection<IGenome> initial_population){
        ArrayList<TableRow> table = new ArrayList<TableRow>(initial_population.size());
        for(IGenome gene : initial_population){
            TableRow row = new TableRow();
            row.genome = gene;
            table.add(row);
        }
        return table;
    }
    
    public double TestFitness(TableRow row, IFitnessTest test){
        return test.TestFitness(row.genome);
    }
    
    public void CalculateDiversityScores(ArrayList<TableRow> table, LinkedList<IGenome> elite){
        for(int h = 0; h < table.size(); h++){
            double div = 0;
            for(int i = 0; i < elite.size(); i++){
                div += elite.get(i).Difference(table.get(h).genome);
            }
            table.get(h).diversity = div;
        }
    }
    
}
