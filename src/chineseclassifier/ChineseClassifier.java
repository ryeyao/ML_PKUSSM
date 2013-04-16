/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;

//import org.xml.sax.SAXException;

/**
 * 
 * @author Rye
 */
public class ChineseClassifier {

	Document document;
	ArrayList<Corpus> corpuses = new ArrayList<Corpus>();
	String filePath = null;
	String specfile = null;
	Mapping mapping = new Mapping();

	public ChineseClassifier(String filePath, String specfile) {
		this.filePath = filePath;
		this.specfile = specfile;
	}

	public void parse() throws IOException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new File(this.filePath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NodeList corpusNodeList = document.getElementsByTagName("corpus");
		// Parse corpuses.
		for (int i = 0; i < corpusNodeList.getLength(); i++) {
			Element currCorpus = (Element) corpusNodeList.item(i);
			Corpus corp = new Corpus();
			String lang = currCorpus.getAttribute("lang");
			corp.setLang(lang);
			mapping.parse(this.specfile);
			mapping.parseKeyFile("pos\\ChineseLS.test.key");
			// Parse lexelts
			// System.out.println("Parse corpuses ran " + i + " times.");
			NodeList lexeltNodeList = currCorpus.getElementsByTagName("lexelt");
			for (int j = 0; j < lexeltNodeList.getLength(); j++) {
				Element currLexelt = (Element) lexeltNodeList.item(j);
				Lexelt lexelt = new Lexelt();
				String item = currLexelt.getAttribute("item");
				lexelt.setItem(item);

				// Parse Instances.
				NodeList instanceNodeList = currLexelt
						.getElementsByTagName("instance");
				for (int k = 0; k < instanceNodeList.getLength(); k++) {
					Element currInstance = (Element) instanceNodeList.item(k);
					Instance instance = new Instance();
					String id = currInstance.getAttribute("id");
					instance.setID(id);

					Element ansElement = (Element) currInstance
							.getElementsByTagName("answer").item(0);
					Answer answer = new Answer();
					String instanceName = ansElement.getAttribute("instance");
					String senseid = ansElement.getAttribute("senseid");
					answer.setInstance(instanceName);
					answer.setSenseid(senseid);

					instance.setAnswer(answer);

					Element contElement = (Element) currInstance
							.getElementsByTagName("context").item(0);
					String context = contElement.getTextContent();
					String head = contElement.getElementsByTagName("head")
							.item(0).getTextContent();
					// String head = context.substring(context.indexOf(">"),
					// context.lastIndexOf("<"));
					instance.setContext(context);
					instance.setHead(head);

					// Parse posttagging.
					NodeList posTaggingNodeList = currInstance
							.getElementsByTagName("postagging");
					for (int l = 0; l < posTaggingNodeList.getLength(); l++) {
						Element currPOSTagging = (Element) posTaggingNodeList
								.item(l);
						POSTagging posTagging = new POSTagging();

						// Parse wordTag
						NodeList wordTagNodeList = currPOSTagging
								.getElementsByTagName("word");
						for (int m = 0; m < wordTagNodeList.getLength(); m++) {
							Element currWordTag = (Element) wordTagNodeList
									.item(m);
							WordTag wordTag = new WordTag();
							String wordTagID = currWordTag.getAttribute("id");
							String pos = currWordTag.getAttribute("pos");
							String token = currWordTag
									.getElementsByTagName("token").item(0)
									.getTextContent();
							wordTag.setID(wordTagID);
							wordTag.setPOS(pos);
							wordTag.setToken(token);

							posTagging.addWordTag(wordTag, wordTagID);
						}// end of Parse wordTag
						instance.setPOSTagging(posTagging);
					}// end of Parse posttagging.
					lexelt.addInstance(instance);
				}// end of Parse Instances
				mapping.parse(lexelt.getInstances());
				corp.addLexelt(lexelt);
			}// end of Parse lexelt
			corpuses.add(corp);
		}// end of Parse corpus

	}

	public static String extendTo(String str, int num) {
		StringBuffer buf = new StringBuffer(str);
		int len = (str.length() + 1) / 2;
		for (int i = 0; i < num - len; i++) {
			buf.insert(0, ",");
			buf.insert(0, "0");
		}
		// System.out
		// .println("Extended [" + str + "] to [" + buf.toString() + "]");
		return buf.toString();
	}

	public void geterateDataSet(String path) throws IOException {
		HashMap<String, HashMap<String, String>> results = new HashMap<String, HashMap<String, String>>();
		ArrayList<String> dataSet = new ArrayList<String>();
		HashMap<String, String> kvmap = mapping.getKVMap();
		HashMap<String, String> senseidmap = mapping.getSenseidMap();
		HashMap<String, String> keyfilemap = mapping.getKeyFileMap();
		int len = 6;
		int max_len = 12;
		for (Corpus corpus : corpuses) {
			ArrayList<Lexelt> lexeltList = corpus.getLexelts();

			for (Lexelt lexelt : lexeltList) {
				String keyWord = lexelt.getItem();
				ArrayList<Instance> instanceList = lexelt.getInstances();
				HashMap<String, String> match = new HashMap<String, String>();

				BufferedWriter bw = new BufferedWriter(new FileWriter(path
						+ keyWord + ".arff"));
				// Write header
				bw.write("@relation " + keyWord.hashCode());
				bw.newLine();
				bw.newLine();
				for (int i = 1; i <= 4; i++) {
					bw.write("@attribute " + i + " {");
					int count = 0;
					for (String s : mapping.getKVMap().keySet()) {
						bw.write(s);
						if (count != mapping.getKVMap().size() - 1) {
							bw.write(",");
						} else {
							bw.write("}");
						}
						count++;

					}
					bw.newLine();
				}

				// 5th line
				String fifthline = "@attribute " + 5 + " {";
				ArrayList<String> dataLine = new ArrayList<String>();
				for (int i = 0; i < instanceList.size(); i++) {
					Instance instance = instanceList.get(i);
					Answer ans = instanceList.get(i).getAnswer();
					String senseID = ans.getSenseid();
					String value;
					if (senseID == "") {
						value = "";
					} else {
						value = mapping.getValue(senseID);
					}
					if (!fifthline.contains(value)) {
						fifthline += value;
						if (i < instanceList.size() - 1) {
							if (!fifthline.endsWith(",")) {
								fifthline += ",";
							}
						}
					}

					for (WordTag wordTag : instance.getPOSTTagging()
							.getPOSTagging().keySet()) {
						String dataStr = "";
						WordTag[] vec_w = new WordTag[4];
						String[] pos_str = new String[4];
						if (Integer.parseInt(wordTag.getID()) > 2
								&& Integer.parseInt(wordTag.getID()) > 1
								&& Integer.parseInt(wordTag.getID()) + 1 < instance
										.getPOSTTagging().getPOSTagging()
										.size()
								&& Integer.parseInt(wordTag.getID()) + 2 < instance
										.getPOSTTagging().getPOSTagging()
										.size()) {
							vec_w[0] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) - 2);
							vec_w[1] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) - 1);
							vec_w[2] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) + 1);
							vec_w[3] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) + 2);

							// extend binary str
							int c = 0;
							for (WordTag vw : vec_w) {
								String str_bin = vw.getPOS();
								int str_len = (str_bin.length() + 1) / 2;
								pos_str[c] = str_bin;
								if (str_len < len) {
									pos_str[c] = str_bin;
								}
								c++;
							}
							dataStr += pos_str[0] + "," + pos_str[1] + ","
									+ pos_str[2] + "," + pos_str[3] + ",";
							if (ans.getSenseid() != "") {
								dataStr += mapping.getValue(ans.getSenseid());
							} else {
								// dataStr += "?";
								dataStr += keyfilemap.get(ans.getInstance());
							}
							dataLine.add(dataStr);
						}
					}
				}

				fifthline += "}";
				if (fifthline.endsWith(",}")) {
					fifthline = fifthline.replace(",}", "}");
				}
				bw.write(fifthline);
				bw.newLine();
				bw.newLine();
				bw.write("@data");
				bw.newLine();
				for (String d : dataLine) {
					bw.write(d);
					bw.newLine();
				}
				bw.flush();
				bw.close();
			}

		}
	}

	public static String test(weka.core.Instances testingSet,
			Classifier cModel_neorup) throws Exception {
		System.out.println("Testing started...");
		long start = System.currentTimeMillis();
		// Test the model
		Evaluation eTest = new Evaluation(testingSet);
		eTest.evaluateModel(cModel_neorup, testingSet);

		// Print the result ид la Weka explorer:

		String strSummary = eTest.toSummaryString();
		// String strSummary = eTest.toMatrixString();
		long end = System.currentTimeMillis();
		System.out.println("Testing done. " + (end - start) + "ms");
		// return strSummary.split("\\s+")[4];
		return strSummary.split("\n")[1].split("\\s+")[4];
	}

	public static Classifier train(weka.core.Instances isTrainingSet,
			String[] option) throws Exception {
		System.out.println("Training started...");
		long start = System.currentTimeMillis();
		// Classifier cModel = (Classifier)new NaiveBayes();
		Classifier cModel_neorup = new MultilayerPerceptron();
		cModel_neorup.setOptions(option);
		// for (String o:cModel_neorup.getOptions()) {
		// System.out.println("Option: " + o);
		// }
		cModel_neorup.buildClassifier(isTrainingSet);
		long end = System.currentTimeMillis();
		System.out.println("Training done. " + (end - start) + "ms");
		return cModel_neorup;
	}

	public HashMap<String, weka.core.Instances> genDataSets(String path,
			String excluPos) throws Exception {
		HashMap<String, HashMap<String, String>> results = new HashMap<String, HashMap<String, String>>();
		ArrayList<String> dataSet = new ArrayList<String>();
		HashMap<String, String> kvmap = mapping.getKVMap();
		HashMap<String, String> senseidmap = mapping.getSenseidMap();
		HashMap<String, String> keyfilemap = mapping.getKeyFileMap();

		int len = 6;
		int max_len = 12;

		FastVector fvNominalVal = new FastVector(kvmap.size());
		for (String k : kvmap.keySet()) {
			fvNominalVal.addElement(k);
		}
		// Declare two numeric attributes
		Attribute prev_pos_2 = new Attribute("prev_pos_2", fvNominalVal);
		Attribute prev_pos_1 = new Attribute("prev_pos_1", fvNominalVal);
		Attribute next_pos_1 = new Attribute("next_pos_1", fvNominalVal);
		Attribute next_pos_2 = new Attribute("next_pos_2", fvNominalVal);
		boolean usable = false;

		for (Corpus corpus : corpuses) {
			ArrayList<Lexelt> lexeltList = corpus.getLexelts();

			HashMap<String, weka.core.Instances> trainingSetsMap = new HashMap<String, weka.core.Instances>();
			for (Lexelt lexelt : lexeltList) {
				String keyWord = lexelt.getItem();
				ArrayList<Instance> instanceList = lexelt.getInstances();
				HashMap<String, String> match = new HashMap<String, String>();
				ArrayList<weka.core.Instances> trainingSets = new ArrayList<weka.core.Instances>();

				BufferedWriter bw = new BufferedWriter(new FileWriter(path
						+ keyWord + ".arff"));
				// BufferedWriter bw_res = new BufferedWriter(new
				// FileWriter(path
				// + keyWord + ".txt"));

				// Write header
				bw.write("@relation " + keyWord.hashCode());
				bw.newLine();
				bw.newLine();

				for (int i = 1; i <= 4; i++) {
					bw.write("@attribute " + i + " {");
					int count = 0;
					for (String s : mapping.getKVMap().keySet()) {

						// String bin_str = mapping.getKVMap().get(s);
						// String bin_str = mapping.getValue(s);
						// bw.write(bin_str);
						bw.write(s);
						// bw.write("\""
						// + extendTo(bin_str, len)
						// + "\"");
						// System.out.println("bin_str0: "
						// + extendTo(bin_str, len));
						if (count != mapping.getKVMap().size() - 1) {
							bw.write(",");
						} else {
							bw.write("}");
						}
						count++;

					}
					bw.newLine();
				}

				// Declare the class attribute along with its values
				FastVector fvClassVal = new FastVector();

				for (int i = 0; i < instanceList.size(); i++) {
					Instance instance = instanceList.get(i);
					Answer ans = instanceList.get(i).getAnswer();
					String senseID;
					if (ans.getSenseid() == "") {
						senseID = keyfilemap.get(ans.getInstance());
					} else {
						senseID = ans.getSenseid();
					}
					String value = mapping.getValue(senseID);
					if (!fvClassVal.contains(senseID)) {
						fvClassVal.addElement(senseID);
					}
				}
				Attribute classAttribute = new Attribute("senseid", fvClassVal);
				FastVector fvWekaAttributes = new FastVector(5);
				fvWekaAttributes.addElement(prev_pos_2);
				fvWekaAttributes.addElement(prev_pos_1);
				fvWekaAttributes.addElement(next_pos_1);
				fvWekaAttributes.addElement(next_pos_2);
				fvWekaAttributes.addElement(classAttribute);

				// Create an empty training set
				weka.core.Instances isTrainingSet = new weka.core.Instances(
						"Relation", fvWekaAttributes, instanceList.size());

				// Set class index
				isTrainingSet.setClassIndex(4);

				// 5th line
				ArrayList<String> classesAttr = new ArrayList<String>();

				String fifthline = "@attribute " + 5 + " {";
				ArrayList<String> dataLine = new ArrayList<String>();
				for (int i = 0; i < instanceList.size(); i++) {
					Instance instance = instanceList.get(i);
					Answer ans = instanceList.get(i).getAnswer();
					// match.put(ans.getInstance(), ans.getSenseid());
					// System.out.println("Senseid: " + ans.getSenseid());
					String senseID = ans.getSenseid();
					// System.out.println("senseID: " + senseID);
					String value;
					if (senseID == "") {
						value = keyfilemap.get(ans.getInstance());
					} else {
						value = mapping.getValue(senseID);
					}
					if (!fifthline.contains(value)) {
						fifthline += value;
						if (i < instanceList.size() - 1) {
							if (!fifthline.endsWith(",")) {
								fifthline += ",";
							}
						}
					}

					// Create the instance
					weka.core.Instance iExample = new weka.core.Instance(5);
					WordTag[] vec_w = new WordTag[4];
					String[] pos_str = new String[4];
					for (WordTag wordTag : instance.getPOSTTagging()
							.getPOSTagging().keySet()) {
						String dataStr = "";
						if (Integer.parseInt(wordTag.getID()) > 2
								&& Integer.parseInt(wordTag.getID()) > 1
								&& Integer.parseInt(wordTag.getID()) + 1 < instance
										.getPOSTTagging().getPOSTagging()
										.size()
								&& Integer.parseInt(wordTag.getID()) + 2 < instance
										.getPOSTTagging().getPOSTagging()
										.size()) {

							usable = true;
							vec_w[0] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) - 2);
							vec_w[1] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) - 1);
							vec_w[2] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) + 1);
							vec_w[3] = instance
									.getPOSTTagging()
									.getWordTagByID(
											Integer.parseInt(wordTag.getID()) + 2);
							if (excluPos != "") {
								if (vec_w[0].getPOS() == excluPos) {
									vec_w[0] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) - 3);
								}
								if (vec_w[1].getPOS() == excluPos) {
									vec_w[0] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) - 3);
									vec_w[1] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) - 2);
								}

								if (vec_w[2].getPOS() == excluPos) {
									vec_w[2] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) + 2);
									vec_w[3] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) + 3);
								}
								if (vec_w[3].getPOS() == excluPos) {
									vec_w[3] = instance.getPOSTTagging()
											.getWordTagByID(
													Integer.parseInt(wordTag
															.getID()) + 3);
								}
							}

							// extend binary str

							int c = 0;
							for (WordTag vw : vec_w) {
								// String str_bin =
								// mapping.getValue(vw.getPOS());
								String str_bin = vw.getPOS();
								int str_len = (str_bin.length() + 1) / 2;
								pos_str[c] = str_bin;
								if (str_len < len) {
									// pos_str[c] = extendTo(str_bin, len);
									pos_str[c] = str_bin;
									// System.out.println("pos_str[" + c +
									// "] : " + pos_str[c]);
								}
								c++;
							}

							dataStr += pos_str[0] + "," + pos_str[1] + ","
									+ pos_str[2] + "," + pos_str[3] + ",";
							if (ans.getSenseid() != "") {
								// String str_bin =
								// mapping.getValue(ans.getSenseid());
								dataStr += mapping.getValue(ans.getSenseid());
							} else {
								// dataStr += "?";
								dataStr += keyfilemap.get(ans.getInstance());
							}
							// System.out.println("DataStr: " + dataStr);
							dataLine.add(dataStr);
						}
					}
					if (usable) {
						// Create the instance
						for (int m = 0; m < pos_str.length; m++) {
							if (pos_str[m] == null) {
								// System.out.println("NULL!" + m);
								break;
							}
							iExample.setValue(
									(Attribute) fvWekaAttributes.elementAt(m),
									pos_str[m]);
						}
						// System.out.println("senseID: " + iExample.value(3));

						// if (ans.getInstance() == null) {
						// System.out.println("getInstance is null");
						// }
						// if (iExample == null) {
						// System.out.println("iExample is null");
						// }
						// if (keyfilemap.get(ans.getInstance()) == null) {
						// // System.out
						// //
						// .println("keyfilemap.get(ans.getInstance()) is null");
						// // System.out.println(ans.getInstance());
						// }
						if (keyfilemap.get(ans.getInstance()) != null) {
							// System.out.println(ans.getInstance() + " " +
							// fvWekaAttributes
							// .elementAt(pos_str.length));
							iExample.setValue((Attribute) fvWekaAttributes
									.elementAt(pos_str.length), keyfilemap
									.get(ans.getInstance()));
							// add the instance
							isTrainingSet.add(iExample);
						}
						usable = false;
					}
				}

				// every lexelt has an training set
				trainingSets.add(isTrainingSet);
				trainingSetsMap.put(keyWord, isTrainingSet);

				// bw_res.close();

				fifthline += "}";
				if (fifthline.endsWith(",}")) {
					fifthline = fifthline.replace(",}", "}");
				}

				bw.write(fifthline);
				bw.newLine();
				bw.newLine();
				bw.write("@data");
				bw.newLine();
				for (String d : dataLine) {
					bw.write(d);
					bw.newLine();
				}
				bw.flush();
				bw.close();
			}
			return trainingSetsMap;
		}
		return null;
	}

	@Override
	public String toString() {
		String str = "Corpus: ";
		str += "\n";

		for (Corpus c : corpuses) {
			str += c.toString();
			str += "\n";
		}
		return str;

	}

	public static HashMap<String, weka.core.Instances> doGenerate(
			String xmlFile, String outPath, String excluPos, boolean isTrain)
			throws Exception {
		ChineseClassifier cc = new ChineseClassifier(xmlFile,
				"pos\\SpecificationforPOSTags.txt");
		System.out.println("Start parsing...");
		cc.parse();
		System.out.println("Parse done!");
		System.out.println("Generating data set...");
		// ArrayList<String> myDataSet;
		// ArrayList<weka.core.Instances> trainingSets = new
		// ArrayList<weka.core.Instances>();
		// ArrayList<weka.core.Instances> testingSets = new
		// ArrayList<weka.core.Instances>();
		HashMap<String, weka.core.Instances> dataSets;
		if (isTrain) {

			dataSets = cc.genDataSets(outPath, excluPos);
		} else {
			dataSets = cc.genDataSets(outPath, excluPos);
		}
		System.out.println("Data set generated!");
		// System.out.println(cc.toString());
		return dataSets;
	}

	public static void tempWork() throws Exception {
		// TODO code application logic here
		HashMap<String, weka.core.Instances> trainingSetsMap;
		HashMap<String, weka.core.Instances> testingSetsMap;
		String excluPos = "w";
		trainingSetsMap = doGenerate("pos\\Chinese_train_pos.xml",
				"pos\\arff\\training\\", excluPos, true);
		System.out.println("===============================================");
		testingSetsMap = doGenerate("pos\\Chinese_test_pos.xml",
				"pos\\arff\\testing\\", excluPos, false);
		System.out.println("Training sets size: " + trainingSetsMap.size());
		System.out.println("Testing sets size: " + testingSetsMap.size());
		long totalTime = 0;

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"pos\\arff\\results\\result_without_comma_500_L0.5.csv")));
		// write csv header
		String header = "item,Momentum_0.0,Momentum_0.2";
		double micro_ave = 0.0;
		double micro_ave2 = 0.0;
		double macro_ave = 0.0;
		double macro_ave2 = 0.0;
		int micro_sum = 0;
		int micro_sum2 = 0;
		double macro_sum = 0.0;
		double macro_sum2 = 0.0;
		double macro_sum_all = 0;
		double macro_sum2_all = 0;
		bw.write(header);
		bw.newLine();
		for (String item : trainingSetsMap.keySet()) {

			weka.core.Instances trainingInstances = trainingSetsMap.get(item);
			// Percentage split
			int trainSize = (int) Math
					.round(trainingInstances.numInstances() * 0.8);
			int testSize = trainingInstances.numInstances() - trainSize;
			weka.core.Instances train = new weka.core.Instances(
					trainingInstances, 0, trainSize);
			weka.core.Instances test = new weka.core.Instances(
					trainingInstances, trainSize, testSize);

			weka.core.Instances testingInstances = testingSetsMap.get(item);
			String[] optChange = { "0.0", "0.5" };
			long start = System.currentTimeMillis();
			bw.write(item + ",");
			for (int i = 0; i < optChange.length; i++) {

				String[] opt = { "-L", "0.5", "-M", optChange[i], "-N", "500",
						"-V", "0", "-S", "3", "-E", "20", "-H", "a" };
				String rate = test(testingInstances,
						train(trainingInstances, opt));
				if (i == 0) {
					macro_sum += Double.parseDouble(rate) / 100;
					macro_sum_all += (Double.parseDouble(rate) / 100)
							* trainingInstances.numInstances();
					micro_sum += trainingInstances.numInstances();
				} else {
					macro_sum2 += Double.parseDouble(rate) / 100;
					micro_sum2 += trainingInstances.numInstances();
					macro_sum2_all += (Double.parseDouble(rate) / 100)
							* trainingInstances.numInstances();
				}
				bw.write(String.valueOf(Double.parseDouble(rate) / 100));
				if (i != optChange.length - 1) {
					bw.write(",");
				}
			}
			bw.newLine();
			long end = System.currentTimeMillis();
			totalTime += end - start;
		}
		macro_ave = macro_sum / trainingSetsMap.size();
		macro_ave2 = macro_sum2 / trainingSetsMap.size();
		micro_ave = macro_sum_all / micro_sum;
		micro_ave2 = macro_sum2_all / micro_sum2;
		bw.write("Micro_ave," + micro_ave + "," + micro_ave2);
		bw.newLine();
		bw.write("Macro_ave," + macro_ave + "," + macro_ave2);
		bw.flush();
		bw.close();

		System.out.println("Training and testing done (" + totalTime + "ms)");
		System.out.println("Micro_ave," + micro_ave + "," + micro_ave2);
		System.out.println("Macro_ave," + macro_ave + "," + macro_ave2);
	}

	public static void tempWork2() throws Exception {
		// TODO code application logic here
		HashMap<String, weka.core.Instances> trainingSetsMap;
		HashMap<String, weka.core.Instances> testingSetsMap;
		String excluPos = "";
		trainingSetsMap = doGenerate("pos\\Chinese_train_pos.xml",
				"pos\\arff\\training\\", excluPos, true);
		System.out.println("===============================================");
		testingSetsMap = doGenerate("pos\\Chinese_test_pos.xml",
				"pos\\arff\\testing\\", excluPos, false);
		System.out.println("Training sets size: " + trainingSetsMap.size());
		System.out.println("Testing sets size: " + testingSetsMap.size());
		long totalTime = 0;

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"pos\\arff\\results\\result_with_comma_500_L0.5.csv")));
		// write csv header
		String header = "item,Momentum_0.0,Momentum_0.2";
		double micro_ave = 0.0;
		double micro_ave2 = 0.0;
		double macro_ave = 0.0;
		double macro_ave2 = 0.0;
		int micro_sum = 0;
		int micro_sum2 = 0;
		double macro_sum = 0.0;
		double macro_sum2 = 0.0;
		double macro_sum_all = 0;
		double macro_sum2_all = 0;
		bw.write(header);
		bw.newLine();
		for (String item : trainingSetsMap.keySet()) {

			weka.core.Instances trainingInstances = trainingSetsMap.get(item);
			// Percentage split
			int trainSize = (int) Math
					.round(trainingInstances.numInstances() * 0.8);
			int testSize = trainingInstances.numInstances() - trainSize;
			weka.core.Instances train = new weka.core.Instances(
					trainingInstances, 0, trainSize);
			weka.core.Instances test = new weka.core.Instances(
					trainingInstances, trainSize, testSize);

			weka.core.Instances testingInstances = testingSetsMap.get(item);
			String[] optChange = { "0.0", "0.5" };
			long start = System.currentTimeMillis();
			bw.write(item + ",");
			for (int i = 0; i < optChange.length; i++) {

				String[] opt = { "-L", "0.5", "-M", optChange[i], "-N", "500",
						"-V", "0", "-S", "3", "-E", "20", "-H", "a" };
				String rate = test(testingInstances,
						train(trainingInstances, opt));
				if (i == 0) {
					macro_sum += Double.parseDouble(rate) / 100;
					macro_sum_all += (Double.parseDouble(rate) / 100)
							* trainingInstances.numInstances();
					micro_sum += trainingInstances.numInstances();
				} else {
					macro_sum2 += Double.parseDouble(rate) / 100;
					micro_sum2 += trainingInstances.numInstances();
					macro_sum2_all += (Double.parseDouble(rate) / 100)
							* trainingInstances.numInstances();
				}
				bw.write(String.valueOf(Double.parseDouble(rate) / 100));
				if (i != optChange.length - 1) {
					bw.write(",");
				}
			}
			bw.newLine();
			long end = System.currentTimeMillis();
			totalTime += end - start;
		}
		macro_ave = macro_sum / trainingSetsMap.size();
		macro_ave2 = macro_sum2 / trainingSetsMap.size();
		micro_ave = macro_sum_all / micro_sum;
		micro_ave2 = macro_sum2_all / micro_sum2;
		bw.write("Micro_ave," + micro_ave + "," + micro_ave2);
		bw.newLine();
		bw.write("Macro_ave," + macro_ave + "," + macro_ave2);
		bw.flush();
		bw.close();

		System.out.println("Training and testing done (" + totalTime + "ms)");
		System.out.println("Micro_ave," + micro_ave + "," + micro_ave2);
		System.out.println("Macro_ave," + macro_ave + "," + macro_ave2);
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		tempWork();
		tempWork2();
		// System.out.println("Correctly Classified Instances         683               66.6992 %".split("\\s+")[4]);
	}
}
