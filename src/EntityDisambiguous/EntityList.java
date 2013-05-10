package EntityDisambiguous;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityList extends HashMap<String, String> {

	private String name;
	
	@Override
	public String toString () {
		String ret = name + ": ";
		ret += "\n";
		for (String id: this.keySet()) {
			String line = "    ";
			line += "id: " + id;
			line += "\n";
			line += "    ";
			line += "text: " + this.get(id);
			line += "\n";
			ret += "\n";
		}
		return ret;
	}
}
