/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

/**
 *
 * @author Rye
 */
public class WordTag {
    private String id = null;
    private String pos = null;
    private String token = null;
    
    public void setID (String id) {
        this.id = id;
    }
    
    public String getID () {
        return this.id;
    }
    
    public void setPOS (String pos) {
        this.pos = pos;
    }
    
    public String getPOS () {
        return this.pos;
    }
    
    public void setToken (String token) {
        this.token = token;
    }
    
    public String getToken () {
        return this.token;
    }
    
    @Override
    public String toString() {
    	String str = "Word id: " + id;
    	str += "\n";
    	str += "pos: " + pos;
    	str += "\n";
    	str += "token: " + token;
    	str += "\n";
    	return str;
    }
}
