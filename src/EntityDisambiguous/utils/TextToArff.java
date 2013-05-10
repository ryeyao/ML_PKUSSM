package EntityDisambiguous.utils;

import weka.core.*;
import weka.core.converters.*;
import weka.classifiers.trees.*;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.filters.*;
import weka.filters.unsupervised.attribute.*;

import java.io.*;

/**
 * Example class that converts HTML files stored in a directory structure into
 * and ARFF file using the TextDirectoryLoader converter. It then applies the
 * StringToWordVector to the data and feeds a J48 classifier with it.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextToArff {

	private String charset = "utf-8";

	public TextToArff() {

	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Instances writeArffFromDirClassified(String dirFrom, String fileTo)
			throws IOException {
		File dir = new File(dirFrom);
		if (!dir.isDirectory()) {
			System.out.println("Not a directory");
			return null;
		}
		// convert the directory into a dataset
		TextDirectoryLoader loader = new TextDirectoryLoader();
		loader.setDirectory(dir);
		loader.setCharSet("utf-8");
		Instances dataRaw = loader.getDataSet();
		this.writeToFile(fileTo, dataRaw.toString());

		// System.out.println("\n\nImported data:\n\n" + dataRaw);
		return dataRaw;
	}

	public Instances writeArffFromDir(String dirFrom, String fileTo) throws IOException {
		FastVector atts = new FastVector(2);
//		atts.addElement(new Attribute("filename", (FastVector) null));
		atts.addElement(new Attribute("contents", (FastVector) null));
		Instances data = new Instances("text_files_in_" + dirFrom, atts, 0);

		File dir = new File(dirFrom);
		String[] files = dir.list();
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".txt")) {
				try {
					double[] newInst = new double[2];
					newInst[0] = (double) data.attribute(0).addStringValue(
							files[i].substring(0, files[i].lastIndexOf(".")));
					File txt = new File(dirFrom + File.separator + files[i]);
					InputStreamReader is;
					is = new InputStreamReader(new FileInputStream(txt), this.charset);
					BufferedReader br = new BufferedReader(is);
					
					String line;
					StringBuffer txtStr = new StringBuffer();
					while ((line = br.readLine()) != null) {
						txtStr.append(line);
					}
					
					newInst[1] = (double) data.attribute(1).addStringValue(
							txtStr.toString());
					data.add(new Instance(1.0, newInst));
					
					is.close();
					br.close();
				} catch (Exception e) {
					// System.err.println("failed to convert file: " +
					// directoryPath + File.separator + files[i]);
				}
			}
		}
		this.writeToFile(fileTo, data.toString());
		return data;
	}

	private void writeToFile(String filename, String content)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(filename)), this.charset));
		bw.write(content);
		bw.flush();
		bw.close();
	}
}