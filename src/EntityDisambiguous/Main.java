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
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import weka.classifiers.Evaluation;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.SingleClustererEnhancer;
import weka.clusterers.XMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import EntityDisambiguous.utils.AnsFileFilter;
import EntityDisambiguous.utils.ArffFileFilter;
import EntityDisambiguous.utils.ArffWriter;
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
	
	public static void createArffFromKBXML (String dirFrom, String dirTo) throws ParserConfigurationException, IOException {
		
		// Parse xml
		Parser parser = new Parser("utf-8");
		HashMap<String, EntityList> kbs = parser.parseKB(dirFrom);
		for (String fn: kbs.keySet()) {
			ArffWriter aff = new ArffWriter(dirTo + "/" + fn);
			aff.setRelationName("KB_" + fn);
			aff.setAttribute("classid", ArffWriter.DataType.string);
			aff.setAttribute("content", ArffWriter.DataType.string);
			
			for (String id: kbs.get(fn).keySet()) {
				ArrayList<String> data = new ArrayList<String>();
				data.add(id);
				String classid = kbs.get(fn).get(id);
				data.add(ChineseWordSeg.doSegFromString(classid));
				aff.setDataInstance(data);
			}
			aff.writeArff();
			
		}
	}
	public static void createCenterFileFromAns (String fileFrom, String fileTo) throws IOException, ParserConfigurationException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileFrom)), charset));
		
		String line;
		String res_str = "";
		HashMap<String, String> lines = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			res_str += line;
			lines.put(line.split(" ")[0], line.split(" ")[1]);
		}
		// Parse xml
		Parser parser = new Parser("utf-8");
		HashMap<String, EntityList> anses = parser.parseKB("ner/KB");
		
		ArffWriter aff = new ArffWriter(fileTo);
		aff.setRelationName("Center_File_" + fileFrom);
		aff.setAttribute("content", ArffWriter.DataType.string);
		for (String fn: lines.keySet()) {
			ArrayList<String> data = new ArrayList<String>();
			String content;
			if (lines.get(fn) == "other" || lines.get(fn).contains("Out_")) {
				content = lines.get(fn);
			} else {
				content = (String)anses.get(fileFrom.replace(".ans", ".xml")).get(lines.get(fn)); 
			}
			data.add(lines.get(fn));
			aff.setDataInstance(data);
		}
		aff.writeArff();
	}

	public static void main(String[] args) throws Exception {

		String rawTextDir = "ner/train";
		String datasetDir = "ner/train_seg";
		
		doWordSegmentation(rawTextDir, datasetDir);
		doWriteArffFromDir(datasetDir, datasetDir);
		createArffFromKBXML("ner/KB", "ner/KB");
		File ansdir = new File("ner/train_seg");
		FilenameFilter ansfilter = new AnsFileFilter();
		for (File file: ansdir.listFiles(ansfilter)) {
			createCenterFileFromAns(file.getCanonicalPath(), file.getCanonicalPath() + ".arff");
		}
		
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
