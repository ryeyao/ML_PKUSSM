/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

import java.util.ArrayList;
import java.util.HashMap;



/**
 *
 * @author Rye
 */
public class POSTagging {
    private HashMap<WordTag, String> postagging = new HashMap<WordTag, String>();
    private ArrayList<WordTag> wordList = new ArrayList<WordTag>();
    
    public void addWordTag (WordTag wordTag, String id) {
        this.postagging.put(wordTag, id);
        this.wordList.add(wordTag);
    }
    
    public HashMap<WordTag, String> getPOSTagging() {
    	return this.postagging;
    }
    
    public WordTag getWordTagByID(int id) {
    	return this.wordList.get(id);
    }
    
    @Override
    public String toString() {
    	String str = "";
    	for(WordTag w: postagging.keySet()) {
    		str += w.toString();
    	}
    	return str;
    	
    }
}
