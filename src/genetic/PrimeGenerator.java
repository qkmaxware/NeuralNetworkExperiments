/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Colin
 */
public class PrimeGenerator {

    public static void main(String[] args){
        long time = System.currentTimeMillis();
        LinkedList<Integer> ints = (Erastosthenes(1000000));
        long time2 = System.currentTimeMillis();
        System.out.println("Time "+((time2 - time)*0.001));
        //for(Integer in : ints){
            //System.out.println(in);
        //}
    }
    
    public static LinkedList<Integer> Erastosthenes(int upper_limit) {
        ArrayList<Integer> numbers = new ArrayList<>();
        LinkedList<Integer> prime_numbers = new LinkedList<>();
        prime_numbers.addLast(2);

        for (int i = 1; i < upper_limit; i += 2) {
            numbers.add(i);
        }

        int[] marked_for_removal = new int[numbers.size()];

        for (int k = 3; k < upper_limit; k += 2) {
            if (marked_for_removal[(k / 2)] == 0) {
                prime_numbers.addLast(k);
                for (int i = k * k; i < numbers.size() && i > 0; i += 2 * k) {
                    marked_for_removal[(i / 2)]++;
                }
            }
        }
        
        return prime_numbers;
    }

}
