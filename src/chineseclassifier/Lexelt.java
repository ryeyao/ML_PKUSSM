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
public class Lexelt {
    private String item = null;
    private ArrayList<Instance> instances = new ArrayList<Instance>();
    
    public void setItem (String item) {
        this.item = item;
    }
    
    public String getItem () {
        return this.item;
    }
    
    public void addInstance (Instance instance) {
        this.instances.add(instance);
    }
    
    public ArrayList<Instance> getInstances () {
    	return this.instances;
    }
    
    @Override
    public String toString () {
    	String str = "Lexelt: " + item;
    	str += "\n";
    	for (Instance i: instances) {
    		str += i.toString();
    		str += "\n";
    	}
    	return str;
    }
}
