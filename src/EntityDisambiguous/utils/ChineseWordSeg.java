package EntityDisambiguous.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class ChineseWordSeg {

	private String charset = "utf-8";

	public ChineseWordSeg() {
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void doSegFromFile(String fileFrom, String fileTo)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(fileFrom)), this.charset));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(fileTo)), this.charset));
		
		String line;
		
		while ((line = br.readLine()) != null && line != "\n" && line != "") {
			String out_line = "";
			StringReader line_reader = new StringReader(line);
			IKSegmenter iks = new IKSegmenter(line_reader, false);
			Lexeme lexeme = null;
			
			while ((lexeme = iks.next()) != null) {
				out_line += lexeme.getLexemeText();
				out_line += " ";
			}
			out_line += "{]";
			out_line = out_line.replace(" {]", "");
			bw.write(out_line);
			bw.newLine();
		}
		bw.flush();
		br.close();
		bw.close();
	}
	
	public void doSegFromDir (String dirFrom, String dirTo) throws IOException {
		
		File dirFileFrom = new File(dirFrom);
		File dirFileTo = new File(dirTo);
		String appender = "";
		
		if (!dirFileFrom.exists()) {
			dirFileFrom.mkdirs();
		}
		if (!dirFileTo.exists()) {
			dirFileTo.mkdirs();
		}
		if (!dirFileFrom.isDirectory() || !dirFileTo.isDirectory()) {
			System.out.println("Not directories or not exists");
			return ;
		}
		
		if (dirFileFrom == dirFileTo) {
			appender = "_seg";
		}
		
		String[] filesFrom = dirFileFrom.list();

		for (String fn: filesFrom) {
			this.doSegFromFile(dirFrom + "/" + fn, dirTo + "/" + fn + appender);
		}
		
	}
	
	public static String doSegFromString (String strToSeg) throws IOException {
		String strSeged = "";
		StringReader line_reader = new StringReader(strToSeg);
		IKSegmenter iks = new IKSegmenter(line_reader, true);
		Lexeme lexeme = null;
		
		while ((lexeme = iks.next()) != null) {
			strSeged += lexeme.getLexemeText();
			strSeged += " ";
		}
		strSeged += "{]";
		strSeged = strSeged.replace(" {]", "");
		return strSeged;
	}
}
