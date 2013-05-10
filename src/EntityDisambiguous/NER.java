package EntityDisambiguous;

import weka.classifiers.trees.J48;
import weka.clusterers.Clusterer;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class NER {
	
	private Clusterer cluster;
	private Instances rawData;

	public NER (Clusterer cluster, Instances rawData) {
		this.cluster = cluster;
		this.rawData = rawData;
	}
	
	public void train() throws Exception {
		 // apply the StringToWordVector
	    // (see the source code of setOptions(String[]) method of the filter
	    // if you want to know which command-line option corresponds to which
	    // bean property)
	    StringToWordVector filter = new StringToWordVector();
	    filter.setInputFormat(this.rawData);
	    Instances dataFiltered = Filter.useFilter(this.rawData, filter);
	    //System.out.println("\n\nFiltered data:\n\n" + dataFiltered);

		this.cluster.buildClusterer(dataFiltered);
//	    System.out.println("\n\nCluster model:\n\n" + cluster);
	}
	
	public Clusterer getClusterer () {
		return this.cluster;
	}
	
}
