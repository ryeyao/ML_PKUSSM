/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chineseclassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
//		System.out
//				.println("Extended [" + str + "] to [" + buf.toString() + "]");
		return buf.toString();
	}

	public void geterateDataSet(String path) throws IOException {
		HashMap<String, HashMap<String, String>> results = new HashMap<String, HashMap<String, String>>();
		ArrayList<String> dataSet = new ArrayList<String>();
		HashMap<String, String> kvmap = mapping.getKVMap();
		HashMap<String, String> senseidmap = mapping.getSenseidMap();
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
				bw.write("@relation " + keyWord.hashCode() + "_predicted");
				bw.newLine();
				bw.newLine();
				for (int i = 1; i <= 4; i++) {
					bw.write("@attribute " + i + " {");
					int count = 0;
					for (String s : mapping.getKVMap().keySet()) {
//						String bin_str = mapping.getKVMap().get(s);
//						String bin_str = mapping.getValue(s);
//						bw.write(bin_str);
						bw.write(s);
//						bw.write("\""
//								+ extendTo(bin_str, len)
//								+ "\"");
//						System.out.println("bin_str0: "
//								+ extendTo(bin_str, len));
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
//					match.put(ans.getInstance(), ans.getSenseid());
					match.put(ans.getInstance(), mapping.getValue(ans.getSenseid()));
					if (!fifthline.contains(mapping.getValue(ans.getSenseid()))) {
						fifthline += mapping.getValue(ans.getSenseid());
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
//								String str_bin = mapping.getValue(vw.getPOS());
								String str_bin = vw.getPOS();
								int str_len = (str_bin.length() + 1) / 2;
								pos_str[c] = str_bin;
								if (str_len < len) {
//									pos_str[c] = extendTo(str_bin, len);
									pos_str[c] = str_bin;
									// System.out.println("pos_str[" + c +
									// "] : " + pos_str[c]);
								}
								c++;
							}
							// dataStr += vw0.getPOS() + "," + vw1.getPOS() +
							// ","
							// + vw2.getPOS() + "," + vw3.getPOS() + ",";

//							dataStr += "\"" + pos_str[0] + "\"" + "," + "\"" + pos_str[1] + "\"" + ","
//									+ "\"" + pos_str[2] + "\"" + "," + "\"" + pos_str[3] + "\"" + ",";
							dataStr += pos_str[0] + "," + pos_str[1] + "," + pos_str[2] + "," + pos_str[3] + ",";
							if (ans.getSenseid() != "") {
								// String str_bin =
								// mapping.getValue(ans.getSenseid());
								dataStr += mapping.getValue(ans.getSenseid());
							} else {
								dataStr += "?";
							}
							// System.out.println("DataStr: " + dataStr);
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

	// public ArrayList<int[]> geterateTestSet () {
	// HashMap<String, HashMap<String,String>> results = new HashMap<String,
	// HashMap<String, String>>();
	// ArrayList<int[]> testSet = new ArrayList<int[]>();
	//
	// for (Corpus corpus: corpuses) {
	// ArrayList<Lexelt> lexeltList = corpus.getLexelts();
	//
	// for (Lexelt lexelt: lexeltList) {
	// String keyWord = lexelt.getItem();
	// ArrayList<Instance> instanceList = lexelt.getInstances();
	// HashMap<String, String> match = new HashMap<String, String>();
	//
	// for (Instance instance: instanceList) {
	// Answer ans = instance.getAnswer();
	// match.put(ans.getInstance(), ans.getSenseid());
	//
	// for (WordTag wordTag: instance.getPOSTTagging().getPOSTagging().keySet())
	// {
	// // int[] vec = {0,0,0};
	// int len = (int)(Math.log(mapping.getSenseidMap().size()) / Math.log(2));
	// int[] vec = new int[len];
	// if (Integer.parseInt(wordTag.getID()) > 2) {
	// WordTag vw0 =
	// instance.getPOSTTagging().getWordTagByID(Integer.parseInt(wordTag.getID())
	// - 2);
	// int pos = mapping.getValue(vw0.getPOS());
	// vec[0] = pos;
	// }
	// if (Integer.parseInt(wordTag.getID()) > 1) {
	// WordTag vw1 =
	// instance.getPOSTTagging().getWordTagByID(Integer.parseInt(wordTag.getID())
	// - 1);
	// int pos = mapping.getValue(vw1.getPOS());
	// vec[1] = pos;
	// }
	// if (Integer.parseInt(wordTag.getID()) + 1 <
	// instance.getPOSTTagging().getPOSTagging().size()) {
	// WordTag vw2 =
	// instance.getPOSTTagging().getWordTagByID(Integer.parseInt(wordTag.getID())
	// + 1);
	// int pos = mapping.getValue(vw2.getPOS());
	// vec[2] = pos;
	// }
	// if (Integer.parseInt(wordTag.getID()) + 2 <
	// instance.getPOSTTagging().getPOSTagging().size()) {
	// WordTag vw3 =
	// instance.getPOSTTagging().getWordTagByID(Integer.parseInt(wordTag.getID())
	// + 2);
	// int pos = mapping.getValue(vw3.getPOS());
	// vec[3] = pos;
	// }
	// // vec[2] = mapping.getValue(ans.getSenseid());
	// vec[4] = mapping.getValue(ans.getSenseid());
	// testSet.add(vec);
	// }
	// }
	// }
	//
	// }
	// return testSet;
	// }
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

	public static void doGenerate(String xmlFile, String outPath,
			boolean isTrain) throws IOException {
		ChineseClassifier cc = new ChineseClassifier(xmlFile,
				"pos\\SpecificationforPOSTags.txt");
		System.out.println("Start parsing...");
		cc.parse();
		System.out.println("Parse done!");
		System.out.println("Generating data set...");
		ArrayList<String> myDataSet;
		// if (isTrain) {
		cc.geterateDataSet(outPath);
		// } else {
		// myDataSet = cc.geterateTestSet();
		// }
		System.out.println("Data set generated!");
		// System.out.println(cc.toString());
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO code application logic here
		doGenerate("pos\\Chinese_train_pos.xml", "pos\\arff\\training\\", true);
		doGenerate("pos\\Chinese_test_pos.xml", "pos\\arff\\testing\\", false);
	}

}
