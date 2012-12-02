package org.reichel.start.filefilter;

import java.io.File;
import java.io.FileFilter;

public class OnlyFilesFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return pathname != null && pathname.isFile();
	}

}
