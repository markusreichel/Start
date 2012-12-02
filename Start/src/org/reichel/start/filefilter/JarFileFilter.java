package org.reichel.start.filefilter;

import java.io.File;
import java.io.FileFilter;

import org.reichel.start.helper.UpdateHelper;

public class JarFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname != null && pathname.isFile() && (pathname.getAbsolutePath().toLowerCase().endsWith(UpdateHelper.JAR_EXTENSION) || pathname.getAbsolutePath().toLowerCase().endsWith(UpdateHelper.DELETE_EXTENSION))){
			return true;
		} else if(pathname != null && pathname.isDirectory()){
			return true;
		}
		return false;
	}

}
