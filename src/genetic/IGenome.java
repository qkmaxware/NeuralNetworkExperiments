/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

/**
 *
 * @author Colin
 */
public interface IGenome {
    
    public IGenome Mutate();
    public IGenome[] Crossover(IGenome other);
    public int Size();
    
    public double Difference(IGenome other);
    
}
