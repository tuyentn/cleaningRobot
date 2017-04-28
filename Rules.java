/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author VPC
 */
class Rules {
     public Scanner scan;
     public File ruleData;
     public int numberRules;
     public ArrayList<Rule> set_rules;
     public Rules(){
          ruleData = new File("src/rpg/resources/rules/rules.txt");
          set_rules = new ArrayList<>();
          int[] tempArr= {575,876};
          set_rules.add(new Rule(86,"Sàn gạch đá hoa","Đồ chơi trẻ em","Nhấc lên lau",tempArr,4));
          try {
               loadRules();
          } catch (IOException ex) {
               Logger.getLogger(Rules.class.getName()).log(Level.SEVERE, null, ex);
          }
     }
     void loadRules() throws FileNotFoundException, IOException{
          FileInputStream fstream = new FileInputStream(ruleData);
          BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

          String strLine;

          //Read File Line By Line
          while ( ((strLine = br.readLine()) != null)&&(set_rules.size()<70))   {
            String segments[] = strLine.split(":");  //Chia thanh cac doan nho
            for(String seg:segments){
                 System.out.print(seg+" ");
            }
               System.out.println("");
            Rule tempRule = new Rule(); 
            if(!Character.isDigit(segments[0].charAt(0))){
               segments[0] = segments[0].substring(1);
            }
            tempRule.maLuat = Integer.parseInt(segments[0]);      //doc ma
            tempRule.diaHinh = segments[1];
            tempRule.suKien = segments[2];
            tempRule.giaiQuyet = segments[3];
            
            String chuoiHD[] = segments[4].split(",");         //Chia nho cac ma hanh dong
            tempRule.maHD = new int[chuoiHD.length];     //convert sang int
            for (int i=0;i<chuoiHD.length;i++){
                 tempRule.maHD[i] = Integer.parseInt(chuoiHD[i]);
            }
            
            tempRule.trongSo = Integer.parseInt(segments[5]);
            this.set_rules.add(tempRule);
          }

          //Close the input stream
          br.close();
          
     }
     
     
     public class Rule{
          public int maLuat;
          public String diaHinh;
          public String suKien;
          public String giaiQuyet;
          public int[] maHD;
          public int trongSo;
          Rule(){
               
          }
          Rule(int mL, String dH, String sK, String gQ, int[] mHD, int tS){
               this.maLuat = mL;
               diaHinh = dH;
               suKien = sK;
               giaiQuyet = gQ;
               this.maHD = new int[mHD.length];
               for(int i=0;i<mHD.length;i++){
                    this.maHD[i] = mHD[i];
               }
               trongSo = tS;
          }
         
     }
}
