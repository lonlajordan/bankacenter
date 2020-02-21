package model;

public class Formatter {
    public static String monetaire(String montant){
        String valeur = "0";
        int sig = 1;
        int n = Integer.parseInt(montant.replace(" ", ""));
        if(n <0){
            n = -n;
            sig = -1;
        }
        if(n != 0){
            do{
                int j = n%1000;
                String t = j+"";
                while(t.length() != 3){
                    t = "0"+t;
                }
                if(n == sig*Integer.parseInt(montant.replace(" ", ""))){
                    valeur = ""+t;
                }else{
                    valeur = t+" "+valeur;
                }
                n = n/1000;
            }while(n!=0);

            while(valeur.charAt(0) == '0'){
                valeur = valeur.substring(1);
            }
            if(sig == -1) valeur = "-"+valeur;
        }

        return valeur;
    }
}
