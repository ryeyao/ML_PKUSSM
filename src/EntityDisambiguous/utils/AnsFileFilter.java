package EntityDisambiguous.utils;

import java.io.File;
import java.io.FilenameFilter;

public class AnsFileFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		if (name.endsWith(".ans")) {
			return true;
		}
		return false;
	}

}
