/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

import java.util.ArrayList;

/**
 *
 * @author Rye
 */
public class Corpus {
    private String lang = null;
    private ArrayList<Lexelt> lexelts = new ArrayList<Lexelt>();
    
    public void setLang (String lang) {
        this.lang = lang;
    }
    
    public String getLang () {
        return this.lang;
    }
    
    public void addLexelt (Lexelt lexelt) {
        this.lexelts.add(lexelt);
    }
    
    public ArrayList<Lexelt> getLexelts () {
    	return this.lexelts;
    }
    
    @Override
    public String toString () {
    	String str = "Corpus Language: " + lang;
    	str += "\n";
    	for (Lexelt l : lexelts) {
    		str += l.toString();
    		str += "\n";
    	}
    	return str;
    }
}
