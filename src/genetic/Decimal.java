/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Colin
 */
public class Decimal implements Comparable<Decimal>{

    public static enum Sign{
        Positive, Negative
    }
    
    protected byte[] digits;
    protected Sign sign = Sign.Positive;
    protected int scale;
    protected int precision;
    
    public static long MaxPrecision = 1000000;
    
    private static Pattern regex = Pattern.compile("(?<sign>[+-])?(?<whole>\\d+)(?:\\.(?<frac>\\d+))?"); //"(?'sign'[+-])?(?'whole'\d+)(?:\.(?'frac'\d+))?"
    
    public static void main(String[] args){
        Decimal a = Decimal.Parse("12.4");
        Decimal b = Decimal.Parse("3.2");
        Decimal c = Decimal.Parse("-1.12");
        Decimal d = Decimal.Parse("-4");
        
        System.out.println("A = "+a.toString());
        System.out.println("B = "+b.toString());
        System.out.println("C = "+c.toString());
        System.out.println("D = "+d.toString());
        
        System.out.println("A + B = "+add(a,b).toString());
        System.out.println("C + D = "+add(c,d).toString());
        System.out.println("B + C = "+add(b,c).toString());
        System.out.println("D + A = "+add(d,a).toString());
    }
    
    public Decimal(){
        this.digits = new byte[]{0};
        this.precision = 1;
        this.scale = 0;
        this.sign = Sign.Positive;
    }
    
    public Decimal(double value){
        Matcher m = regex.matcher(String.valueOf(value));
        String whole = "0";
        String frac = "";
        Sign sign = Sign.Positive;
        if(m.find()){
            String s = m.group("sign");
            String w = m.group("whole");
            String f = m.group("frac");
            if(f != null)
                frac = f.trim();
            if(w != null)
                whole = w.trim();
            if(s != null && s.trim().equals("-"))
                sign = Sign.Negative;
        }
        
        int precision = whole.length() + frac.length();
        int scale = frac.length();
        int offset = whole.length();
        
        byte[] digits = new byte[precision];
        
        for(int i = 0; i < whole.length(); i++){
            char c = whole.charAt(i);
            byte digit = (byte)Character.getNumericValue(c);
            digits[i] = digit;
        }
        
        for(int i = 0; i < frac.length(); i++){
            char c = frac.charAt(i);
            byte digit = (byte)Character.getNumericValue(c);
            digits[offset + i] = digit;
        }
        
        Decimal d = this;
        d.digits = digits;
        d.precision = precision;
        d.scale = scale;
        d.sign = sign;
    }
    
    public static Decimal Parse(String value){
        Matcher m = regex.matcher(value);
        String whole = "0";
        String frac = "";
        Sign sign = Sign.Positive;
        if(m.find()){
            String s = m.group("sign");
            String w = m.group("whole");
            String f = m.group("frac");
            if(f != null)
                frac = f.trim();
            if(w != null)
                whole = w.trim();
            if(s != null && s.trim().equals("-"))
                sign = Sign.Negative;
        }
        
        int precision = whole.length() + frac.length();
        int scale = frac.length();
        int offset = whole.length();
        
        byte[] digits = new byte[precision];
        
        for(int i = 0; i < whole.length(); i++){
            char c = whole.charAt(i);
            byte digit = (byte)Character.getNumericValue(c);
            digits[i] = digit;
        }
        
        for(int i = 0; i < frac.length(); i++){
            char c = frac.charAt(i);
            byte digit = (byte)Character.getNumericValue(c);
            digits[offset + i] = digit;
        }
        
        Decimal d = new Decimal();
        d.digits = digits;
        d.precision = precision;
        d.scale = scale;
        d.sign = sign;
        return d;
    }
    
    public int Precision(){
        return this.precision;
    }
    
    public int Scale(){
        return this.scale;
    }
    
    public Decimal Round(RoundingMode context, int scale){
        //int new_scale = Math.max(0, Math.min(this.scale, scale));
        
        //Wow lazy
        BigDecimal bd = new BigDecimal(this.toString());
        bd.setScale(scale, context);
        
        return Decimal.Parse(bd.toPlainString());
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(this.sign == Sign.Negative)
            builder.append("-");
        boolean hasDecimal = scale > 0;
        for(int i = this.digits.length - precision; i < this.digits.length; i++){
            if(hasDecimal && i > this.digits.length - 1 - scale){
                builder.append("."); hasDecimal = false;
            }
            builder.append(this.digits[i]);
        }
        return builder.toString();
    } 
    
    //--------------------------------------------------------------------------
    // Operators
    //--------------------------------------------------------------------------
    
    /**
     * Fast shorthand for multiplying by 10
     * @param a
     * @return 
     */
    public static Decimal shiftRight(Decimal a){
        Decimal b = new Decimal();
        b.precision = a.precision;
        b.scale = Math.max(a.scale - 1, 0);
        b.digits = a.digits.clone();
        b.sign = (a.sign == Sign.Positive ? Sign.Negative : Sign.Positive);
        return b;
    }
    
    /**
     * Fast shorthand for dividing by 10
     * @param a
     * @return 
     */
    public static Decimal shiftLeft(Decimal a){
        Decimal b = new Decimal();
        b.precision = a.precision;
        b.scale = Math.min(a.scale + 1, a.precision);
        b.digits = a.digits.clone();
        b.sign = (a.sign == Sign.Positive ? Sign.Negative : Sign.Positive);
        return b;
    }
    
    /**
     * Create a new decimal with the same value, but opposite sign
     * @param a
     * @return 
     */
    public static Decimal negate(Decimal a){
        Decimal b = new Decimal();
        b.precision = a.precision;
        b.scale = a.scale;
        b.digits = a.digits.clone();
        b.sign = (a.sign == Sign.Positive ? Sign.Negative : Sign.Positive);
        return b;
    }
    
    /**
     * Add two decimal values together O(N) runtime
     * @param a
     * @param b
     * @return 
     */
    public static Decimal add(Decimal a, Decimal b){
        int estimated_precision = Math.max(a.precision, b.precision) + 1;
        byte[] digits = new byte[estimated_precision];
        
        int new_scale = Math.max(a.scale, b.scale);
        
        //TODO
        Sign new_sign = Sign.Positive;
        if(a.sign == Sign.Positive && b.sign == Sign.Positive){
            new_sign = Sign.Positive;
        }else if(a.sign == Sign.Negative && b.sign == Sign.Negative){
            new_sign = Sign.Negative;
        }else{
            //larger value dominated the sign
        }
        
        byte carry = 0;
        int trim = 0;
        for(int i = digits.length - 1; i >= 0; i--){
            int distanceFromDecimal = (new_scale - (digits.length - 1 - i));
            int relativePositionInA = a.digits.length - 1 - a.scale + distanceFromDecimal;
            int relativePositionInB = b.digits.length - 1 - b.scale + distanceFromDecimal;
            
            byte ca = (relativePositionInA < a.digits.length && relativePositionInA >= 0) ? a.digits[relativePositionInA] : (byte)0;
            byte cb = (relativePositionInB < b.digits.length && relativePositionInB >= 0) ? b.digits[relativePositionInB] : (byte)0;
            
            
            byte result = (byte)((a.sign == Sign.Negative ? -1 : 1)*ca + (b.sign == Sign.Negative ? -1 : 1)*cb + carry);
            carry = 0;
            
            if((a.sign == Sign.Positive && b.sign == Sign.Positive) || 
                    (a.sign == Sign.Negative && b.sign == Sign.Negative)){
                //Adding positive numbers
                if(result >= 10){
                    carry = 1;
                    result -= (byte)10;
                }
                //Adding negative numbers
                if(result < 0){
                    result = (byte)(-result);
                }
            }
            else if((a.sign == Sign.Positive && b.sign == Sign.Negative) ||
                    (a.sign == Sign.Negative && b.sign == Sign.Positive)){
                //Positive negative addition
                if (result < 0){
                    result = (byte)(10 + result);
                    carry = -1;
                }
            }
            
            System.out.println("add: "+ca+" + "+cb+" = "+result);
            digits[i] = result;
            if(result == 0){
                trim += 1;
            }
            else{
                trim = 0;
            }
        }
        
        Decimal d = new Decimal();
        d.digits = digits;
        d.precision = estimated_precision - trim;
        d.scale = new_scale;
        d.sign = new_sign; //TODO
        
        return d;
    }
    
    public static Decimal sub(Decimal a, Decimal b){
        return Decimal.add(a,Decimal.negate(b));
    }
    
    public static Decimal mul(Decimal a, Decimal b){
        return null;
    }
    
    public boolean equals(Decimal b){
        //Short circuting
        if(this.precision != b.precision || this.scale != b.scale)
            return false;
        for(int i = 0; i < this.digits.length; i++){
            if(this.digits[i] != b.digits[i])
                return false;
        }
        if(this.sign != b.sign)
            return false;
        return true;
    }
    
    @Override // - <, 0 =, + >
    public int compareTo(Decimal t) {
        if(this.sign == Sign.Positive && t.sign == Sign.Negative){
            return 1;
        }
        else if(this.sign == Sign.Negative && t.sign == Sign.Positive){
            return -1;
        }else{
            //Compare magnitudes
            return 0; //TODO 
        }
    }
}
