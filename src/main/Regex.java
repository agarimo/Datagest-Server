package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Agárimo
 */
public class Regex {
    
    private static Pattern pt;
    private static Matcher mt;
    
    public static String buscar(String patron, String str){
        String aux;
        pt=Pattern.compile(patron);
        mt= pt.matcher(str);
        
        if(mt.find()){
            aux=mt.group();
        }else{
            aux="NOT FOUND";
        }
        return aux;
    }
}
