/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

/**
 *
 * @author Rye
 */
public class Instance {
    private String id = null;
    private Answer answer = null;
    private String context = null;
    private String head = null;
    private POSTagging posTagging = null;
    
    public void setID (String id) {
        this.id = id;
    }
    
    public String getID (){
        return this.id;
    }
    
    public void setAnswer (Answer answer) {
        this.answer = answer;
    }
    
    public Answer getAnswer () {
        return this.answer;
    }
    
    public void setContext (String context) {
    	this.context = context;
    }
    
    public String getContext () {
    	return this.context;
    }
    
    public void setHead (String head) {
        this.head = head;
    }
    
    public String getHead () {
        return this.head;
    }
 
    public void setPOSTagging (POSTagging posTagging) {
        this.posTagging = posTagging;
    }
    
    public POSTagging getPOSTTagging () {
        return this.posTagging;
    }
    
    @Override
    public String toString () {
    	String str = "Instance id: " + id;
    	str += "\n";
    	str += answer.toString();
    	str += "\n";
    	str += "Context: " + context;
    	str += "\n";
    	str += "Head: " + head;
    	str += "\n";
    	str += "PosTagging: " + posTagging;
    	str += "\n";
    	return str;
    }
}
