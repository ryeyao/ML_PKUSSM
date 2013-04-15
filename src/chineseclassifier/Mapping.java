package chineseclassifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Mapping {

	HashMap<String, String> meaning = new HashMap<String, String>();
	HashMap<String, String> kvMap = new HashMap<String, String>();
	HashMap<String, String> senseidMap = new HashMap<String, String>();
	HashMap<String, String> keyFileMap = new HashMap<String, String>();
	int senseidCount = 0;

	// int count = 0;

	private String convertBinary(int sum) {
		StringBuffer binary = new StringBuffer();
		if (sum == 0 || sum == 1) {
			binary.insert(0, sum);
			return binary.toString();
		}
		while (sum != 0 && sum != 1) {
			binary.insert(0, sum % 2);
			// System.out.println("sum=" + sum + "ำเสý=" + (sum % 2) + "ณýสý=" + sum
			// / 2);
			sum = sum / 2;
			if (sum == 0 || sum == 1) {
				binary.insert(0, ',');
				binary.insert(0, sum % 2);
			} else {
				binary.insert(0, ',');
			}

		}
		// System.out.println("BIN: " + binary.toString());
		return binary.toString();
	}

	public void parse(String filePath) throws IOException {
		System.out.println("Parsing " + filePath);
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		String line = in.readLine();
		int count = 0;
		while (line != null) {
			String[] words = line.split(": ");
			if (!meaning.containsKey(words[0]) && !kvMap.containsKey(words[0])) {
				meaning.put(words[0], words[1]);
				// kvMap.put(words[0], convertBinary(count));
				kvMap.put(words[0], String.valueOf(count));
				count++;
			}
			line = in.readLine();
		}
		// System.out.println("Total POS Tag count: " + String.valueOf(count));
		System.out.println("Parsing done!");

	}

	public void parseKeyFile(String filePath) throws IOException {
		System.out.println("Parsing " + filePath);
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		String line = in.readLine();
		int count = 0;
		while (line != null) {
			String[] s = line.split(" ");
			// System.out.println(s[1] + " : " + s[2]);
			if (s[1].contains(".0")) {
				s[1] = s[1].replace(".0", ".");
				
			}
//			System.out.println(s[1] + " : " + s[2]);
			this.keyFileMap.put(s[1], s[2]);
			line = in.readLine();
		}
		in.close();
		System.out.println("Parsing done!");
	}

	public void parse(ArrayList<Instance> instances) {
		// int count = 0;
		for (Instance i : instances) {
			if (!meaning.containsKey(i.getID())
					&& !senseidMap.containsKey(i.getAnswer())) {
				meaning.put(i.getID(), i.getAnswer().getSenseid());
				// senseidMap.put(i.getAnswer().getSenseid(),
				// convertBinary(senseidCount));
				senseidMap.put(i.getAnswer().getSenseid(),
						String.valueOf(senseidCount));
				senseidCount++;
				// count++;
			}
		}
		// System.out.println("Total senseid count: " +
		// String.valueOf(senseidCount));
	}

	public String getValue(String key) {

		if (kvMap.containsKey(key)) {
			// System.out.println("Get value of " + key);
			return kvMap.get(key);
		} else if (senseidMap.containsKey(key)) {
			return senseidMap.get(key);
		} else {
			System.out.println("Key [" + key + "] does not exist.");
			return "0";
		}
	}

	public HashMap<String, String> getSenseidMap() {
		return this.senseidMap;
	}

	public HashMap<String, String> getKVMap() {
		return this.kvMap;
	}

	public HashMap<String, String> getKeyFileMap() {
		return this.keyFileMap;
	}

}
