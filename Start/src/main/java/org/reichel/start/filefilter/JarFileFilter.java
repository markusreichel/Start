package org.reichel.start.filefilter;

import java.io.File;
import java.io.FileFilter;

import org.reichel.start.helper.UpdateHelper;

public class JarFileFilter implements FileFilter {

	private final String[] exceptions;
	
	public JarFileFilter(String[] exceptions){
		this.exceptions = exceptions;
	}

	public JarFileFilter(){
		this.exceptions = null;
	}
	
	@Override
	public boolean accept(File pathname) {
		if(pathname != null){
			if(pathname.isFile() && (isJar(pathname) || isDelete(pathname) && !isExceptions(pathname))){
				return true;
			} else if( pathname.isDirectory() ) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isExceptions(File pathname) {
		if(exceptions != null){
			for(String exception : exceptions){
				if(exception != null){
					if(pathname.getAbsolutePath().endsWith(exception)){
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isDelete(File pathname) {
		return pathname.getAbsolutePath().toLowerCase().endsWith(UpdateHelper.DELETE_EXTENSION);
	}

	private boolean isJar(File pathname) {
		return pathname.getAbsolutePath().toLowerCase().endsWith(UpdateHelper.JAR_EXTENSION);
	}
}
