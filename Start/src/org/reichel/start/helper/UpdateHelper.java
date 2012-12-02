package org.reichel.start.helper;

import java.io.File;

public class UpdateHelper {

	public static final String UPDATE_DIR = "update";
	
	public static final String CONFIG_DIR = "config";
	
	public static final String BACAKUP_DIR = "backup";
	
	public static final String PROPERTIES_EXTENSION = ".properties";
	
	public static final String JAR_EXTENSION = ".jar";
	
	public static final String DELETE_EXTENSION = ".delete";
	
	private final File currentDirectory;
	
	private final File updateDirectory;
	
	private final String currentJarName;

	public UpdateHelper(){
		this.currentDirectory = loadCurrentDirectory();
		this.updateDirectory = new File(this.currentDirectory.getAbsolutePath() + File.separatorChar + UPDATE_DIR);
		this.currentJarName = loadCurrentJarName();
	}

	private File loadCurrentDirectory(){
		String runningJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		runningJarPath = runningJarPath.replace("\\", Character.toString(File.separatorChar)).replace("/", Character.toString(File.separatorChar));
		if(runningJarPath != null && !"".equals(runningJarPath)){
			return new File(runningJarPath.substring(0, runningJarPath.lastIndexOf(File.separatorChar)));
		}
		return null;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public File getUpdateDirectory() {
		return updateDirectory;
	}
	
	public File getFileFromCurrentDirectory(String file){
		return getFile(this.currentDirectory, file);
	}
	
	public File getFileFromUpdateDirectory(String file){
		return getFile(this.updateDirectory, file);
	}
	
	private File getFile(File directory, String file){
		return new File(directory.getAbsolutePath() + File.separatorChar + file);
	}
	
	private String loadCurrentJarName() {
		String runningJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		runningJarPath = runningJarPath.replace("\\", Character.toString(File.separatorChar)).replace("/", Character.toString(File.separatorChar));
		if(runningJarPath != null && !"".equals(runningJarPath)){
			return runningJarPath.substring(runningJarPath.lastIndexOf(File.separatorChar) + 1);
		}
		return null;
	}

	public File getCurrentJarFile(){
		return new File(this.currentDirectory.getAbsolutePath() + File.separatorChar + this.currentJarName);
	}
	
	public String getCurrentJarName() {
		return currentJarName;
	}

	public boolean isRunningFromUpdate() {
		return currentDirectory != null && currentDirectory.getAbsolutePath() != null && currentDirectory.getAbsolutePath().endsWith("update");
	}
	
	public File getParentFile() {
		return new File(currentDirectory.getAbsolutePath().substring(0, currentDirectory.getAbsolutePath().lastIndexOf(Character.toString(File.separatorChar))));
	}

	public File getJarFile(File directory) {
		return new File(directory.getAbsolutePath() + File.separatorChar + currentJarName.replace("_", ""));
	}
	
}
