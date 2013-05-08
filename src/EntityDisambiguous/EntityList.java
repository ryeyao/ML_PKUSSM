package EntityDisambiguous;

import java.util.ArrayList;

public class EntityList extends ArrayList{

	private String name;
	
	@Override
	public String toString () {
		String ret = name + ": ";
		ret += "\n";
		for (int i = 0; i < this.size(); i++) {
			String line = "    ";
			String id = String.valueOf(i);
			while (id.length() < 2) {
				id = "0" + id;
			}
			line += "id: " + id;
			line += "\n";
			line += "    ";
			line += "text: " + this.get(i);
			line += "\n";
			ret += "\n";
		}
		return ret;
	}
}
