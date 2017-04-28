/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

/**
 *
 * @author VPC
 */
public class Location {
     public int m,n;
     public Location(){
          this.m = 0;
          this.n = 0;
     }
     public Location(int tm, int tn){
          this.m = tm;
          this.n  = tn;
     }
     public boolean isMatch(Location cmp){
          return ((this.m==cmp.m)&&(this.n == cmp.n));
     }
     public double distance(Location loc){
          return Math.sqrt(Math.pow(this.m-loc.m, 2)+ Math.pow(this.n - loc.n, 2));
     }
     @Override
     public boolean equals (Object o){
          Location x = (Location) o;
          return (x.m == this.m) && (x.n==this.n);
     }
}
