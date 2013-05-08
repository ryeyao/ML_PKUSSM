package EntityDisambiguous;

import java.util.ArrayList;

public class Results extends ArrayList {
	
	
	public String toResultString (String wordDelimiter, String lineDelimiter) {
		
		if (wordDelimiter == null) {
			wordDelimiter = " ";
		}
		if (lineDelimiter == null) {
			lineDelimiter = "\n";
		}
		
		String res = "";
		for (int i = 0; i < this.size(); i++) {
			String line = "";
			String id = String.valueOf(i);
			while (id.length() < 3) {
				id = "0" + id;
			}
			line = id + wordDelimiter + this.get(i);
			res += line;
			res += lineDelimiter;
		}
		return res;
	}
}
