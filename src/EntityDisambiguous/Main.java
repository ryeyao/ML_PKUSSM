package EntityDisambiguous;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.classifiers.Evaluation;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.SingleClustererEnhancer;
import weka.clusterers.XMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import EntityDisambiguous.utils.ArffFileFilter;
import EntityDisambiguous.utils.ChineseWordSeg;
import EntityDisambiguous.utils.TextToArff;

public class Main {

	public static String charset = "utf-8";
	public static void doWordSegmentation(String dirFrom, String dirTo) throws IOException {
		// Word Segmentation
		ChineseWordSeg cws = new ChineseWordSeg();
		File dirs = new File(dirFrom);
		for (String n : dirs.list()) {

			String filePathFrom = dirFrom + "/" + n;
			String filePathTo = dirTo + "/" + n;
			File f = new File(filePathFrom);
			if (f.isDirectory()) {
				cws.doSegFromDir(filePathFrom, filePathTo);
			}
		}
	}
	
	public static void doWriteArffFromDir(String dirFrom, String dirTo) throws IOException {
		TextToArff toArff = new TextToArff();
		File dirs = new File(dirFrom);
		for (String n : dirs.list()) {

			String filePathFrom = dirFrom + "/" + n;
			String filePathTo = dirTo + "/" + n;
			File f = new File(filePathFrom);
			if (f.isDirectory()) {
				toArff.writeArffFromDir(filePathFrom, filePathTo + ".arff");
			}
		}
	}
	
	public void createCenterFromAns (String fileFrom, String fileTo) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileFrom)), this.charset));
		
		String line;
		String res_str = "";
		HashMap<String, String> lines = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			res_str += line;
			lines.put(line.split(" ")[0], line.split(" ")[1]);
		}
		
	}

	public static void main(String[] args) throws Exception {

		String rawTextDir = "ner/train";
		String datasetDir = "ner/train_seg";
//		doWordSegmentation(rawTextDir, datasetDir);
//		doWriteArffFromDir(datasetDir, datasetDir);
		HashMap<String, Instances> datasets = new HashMap<String, Instances>();
		
		FilenameFilter filter = new ArffFileFilter();
		File dir = new File(datasetDir);
		for (File file: dir.listFiles(filter)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			String dataset_str = "";
			String line = "";
			
			while ((line = br.readLine()) != null) {
				dataset_str += line;
				dataset_str += "\n";
			}
			br.close();
			ArffLoader aloader = new ArffLoader();
			InputStream stris = new ByteArrayInputStream(dataset_str.getBytes());
			
			aloader.setSource(stris);
			datasets.put(file.getName().replace(".arff", ""), aloader.getDataSet());
//			System.out.println(aloader.getStructure().toString());
		}
		
		Results res = new Results();
		
		for (String name: datasets.keySet()) {
//			System.out.println("name: " + name);
//			SimpleKMeans km = new SimpleKMeans();
			XMeans clusterer = new XMeans();
			clusterer.setInputCenterFile(new File(datasetDir + "/" + name + ".ans"));
			clusterer.setMaxNumClusters(10);
			NER ner = new NER(clusterer, datasets.get(name));
			ner.train();
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(ner.getClusterer());
			eval.evaluateClusterer(datasets.get(name));
			System.out.println("================================================================");
			System.out.println(name + " Evals: \n" + eval.clusterResultsToString());
			System.out.println();
			System.out.println("================================================================");
		}
		
//		
//		
//		Clusterer trained = new SimpleKMeans();
//		
//		NER ner = new NER(trained, dataset);
//		ner.train();
//		trained = ner.getClusterer();
//	
//		System.out.println("Cluster: " + trained.toString());
////		Evaluation eval = new Evaluation(dataset);
		
	}
}
