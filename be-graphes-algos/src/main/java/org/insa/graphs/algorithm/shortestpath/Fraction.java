package org.insa.graphs.algorithm.shortestpath;

import java.math.BigInteger;
import java.lang.String;
import java.lang.IllegalArgumentException;
class Fraction {
    private long numerateur;
    private long denominateur;
    public Fraction(long num,long denom){
        this.numerateur = num;
        this.denominateur = denom;
    }
    public long get_num(){
        return this.numerateur;
    }
    public void add_num(long n){
        this.numerateur += n;
    }
    public void incr_num(){
        this.numerateur++;
    }
    public void incr_denom(){
        this.denominateur++;
    }
    public long get_denom(){
        return this.denominateur;
    }
    public void add_denom(long n){
        this.denominateur += n;
    }
    public void set_denom(long n){
        this.denominateur = n;
    }
    public String toString(){
        return this.numerateur+"/"+this.denominateur;
    }
    public void add(Fraction f2) throws IllegalArgumentException{
        
        if(this.denominateur==0 || f2.get_denom()==0){
            throw new IllegalArgumentException("Division par 0");
        }
        
        this.numerateur = this.numerateur*f2.get_denom()+f2.get_num()*this.denominateur;
        this.denominateur *= f2.get_denom();
        
        BigInteger num_tmp = new BigInteger(String.valueOf(this.numerateur));
        BigInteger den_tmp = new BigInteger(String.valueOf(this.denominateur));
        long pgcd = num_tmp.gcd(den_tmp).longValue();
        
        this.numerateur /= pgcd;
        this.denominateur /= pgcd;
        
    }
    public void subtract(Fraction f2) throws IllegalArgumentException{
        if(this.denominateur==0 || f2.get_denom()==0){
            throw new IllegalArgumentException("Division par 0");
        }
        this.numerateur = this.numerateur*f2.get_denom()-f2.get_num()*this.denominateur;
        this.denominateur *= f2.get_denom();
        BigInteger num_tmp = new BigInteger(String.valueOf(this.numerateur));
        BigInteger den_tmp = new BigInteger(String.valueOf(this.denominateur));
        long pgcd = num_tmp.gcd(den_tmp).longValue();
        this.numerateur /= pgcd;
        this.denominateur /= pgcd;
    }
    public double calcul()throws IllegalArgumentException{
        if(this.denominateur==0){
            throw new IllegalArgumentException("Division par 0");
        }
        return (double)(this.numerateur)/(double)(this.denominateur);
    }
}