package org.reichel.start.filefilter;

import java.io.File;
import java.io.FileFilter;

import org.reichel.start.helper.UpdateHelper;

public class PropertiesFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname != null 
				&& pathname.isFile() 
				&& pathname.getAbsolutePath().toLowerCase().endsWith(UpdateHelper.PROPERTIES_EXTENSION) 
				&& pathname.getAbsolutePath().toLowerCase().contains(UpdateHelper.CONFIG_DIR)
				&& !pathname.getAbsolutePath().toLowerCase().contains(UpdateHelper.UPDATE_DIR)
				&& !pathname.getAbsolutePath().toLowerCase().contains(UpdateHelper.BACAKUP_DIR)){
			return true;
		} else if(pathname != null && pathname.isDirectory()){
			return true;
		}
		return false;
	}

}
