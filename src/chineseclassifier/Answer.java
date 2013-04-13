/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

/**
 *
 * @author Rye
 */
public class Answer {
    private String instance = null;
    private String senseid = null;
    
    public void setInstance (String instance) {
        this.instance = instance;
    }
    
    public String getInstance () {
        return this.instance;
    }
    
    public void setSenseid (String senseid) {
        this.senseid = senseid;
    }
    
    public String getSenseid () {
        return this.senseid;
    }
    
    @Override
    public String toString() {
    	String str = "Answer: ";
    	str += "\n";
    	str += "instance: " + instance;
    	str += "\n";
    	str += "senseid: " + senseid;
    	str += "\n";
    	return str;
    	
    }
}
