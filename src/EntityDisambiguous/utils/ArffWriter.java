package EntityDisambiguous.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

//import weka.core.Attribute;
//import weka.core.FastVector;
//import weka.core.Instance;
//import weka.core.Instances;

public class ArffWriter {

	static enum DataType {
		numeric,
		string,
		date,
		relational
	}
	
	private static final String relTag = "@relation";
	private static final String attrTag = "@attribute";
	private static final String endTag = "@end";
	private static final String dataTag = "@data";
	private static final String lineDelimiter = "\n";
	private static final String wordDelimiter = ",";
	
	private String relationName; // Relation name
	private String fileName;
	private String charset = "utf-8";
	
	private String headerString = "";
	private String dataString = "";
	
	public ArffWriter (String file_name) {
		this.relationName = file_name;
		this.fileName = file_name;
	}
	
	public void setRelationName (String relationName) {
		this.relationName = relationName;
	}
	
	public void setCharset (String charset) {
		this.charset = charset;
	}
	
	public void setAttribute (String attName, List<String> attVal) {
		String attLine = "";
		attLine += this.attrTag + " " + attName + " ";
		attLine += "{";
		for (String v: attVal) {
			attLine += v;
			attLine += this.wordDelimiter;
		}
		attLine += "}";
		attLine.replace(",}", "}");
		attLine += this.lineDelimiter;
		this.headerString += attLine;
	}
	
	public void setAttribute (String attName, DataType dataType) {
		String attLine = "";
		attLine += this.attrTag + " " + attName + " ";
		attLine += dataType.name();
		attLine += this.lineDelimiter;
		this.headerString += attLine;
		
	}
	
	public void setDateAttribute (String attName, String dateFormat) {
		String attLine = "";
		attLine += this.attrTag + " " + attName + " ";
		attLine += DataType.date.name();
		attLine += " ";
		attLine += dateFormat;
		attLine += this.lineDelimiter;
		this.headerString += attLine;
	}
	
	public void setDataInstance (List<String> dataInstance) {
		String attLine = "";
		for (String v: dataInstance) {
			attLine += v;
			attLine += this.wordDelimiter;
		}
		attLine += ",";
		attLine = attLine.replace(",,", ",");
		attLine += this.lineDelimiter;
		this.dataString += attLine;
	}

	public void writeArff () throws IOException {
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter (
						new FileOutputStream(new File(this.fileName)), 
								Charset.forName(this.charset)
						)
				);
		
		bw.write(this.toString());
		bw.flush();
		bw.close();
	}
	
	@Override
	public String toString () {
		String arffString = "";
		arffString += this.relationName;
		arffString += this.lineDelimiter;
		
		arffString += this.lineDelimiter;
		
		arffString += this.headerString;
		
		arffString += this.lineDelimiter;
		
		arffString += this.dataTag;
		arffString += this.dataString;
		
		return arffString;
	}
	
}
