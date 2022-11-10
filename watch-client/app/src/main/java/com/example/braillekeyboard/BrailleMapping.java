package com.example.braillekeyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class BrailleMapping {
     private Map<String, String> alphabetMapping;

     BrailleMapping() {
         alphabetMapping = new HashMap<String, String>() {{
             put("A", "1");
             put("B", "12");
             put("C", "14");
             put("D", "145");
             put("E", "15");
             put("F", "124");
             put("G", "1245");
             put("H", "125");
             put("I", "24");
             put("J", "245");
             put("K", "13");
             put("L", "123");
             put("M", "134");
             put("N", "1345");
             put("O", "135");
             put("P", "1234");
             put("Q", "12345");
             put("R", "1235");
             put("S", "234");
             put("T", "2345");
             put("U", "136");
             put("V", "1236");
             put("W", "2456");
             put("X", "1346");
             put("Y", "13456");
             put("Z", "1356");
         }};
     }

     public Map<String,String> getBrailleMap(){
         return alphabetMapping;
     }

     public String getNumbering(String alphabet){
         return alphabetMapping.get(alphabet);
     }

     public String getAlphabet(String numbering){
         char numArray[] = numbering.toCharArray();
         Arrays.sort(numArray);
         String sorted_numbers =  new String(numArray);

         for(Map.Entry<String, String> entry: alphabetMapping.entrySet()) {
             if(entry.getValue().equals(sorted_numbers)) {
                 return entry.getKey();
             }
         }
         System.out.println("No Matching Alphabet Found");
         return null;
     }

//     public static void main(String args[]){
//         BrailleMapping BM = new BrailleMapping();
//         System.out.print(BM.getNumbering("A"));
//     }
}

