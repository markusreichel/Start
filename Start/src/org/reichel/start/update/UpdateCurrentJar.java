package org.reichel.start.update;

import java.io.File;

import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;

public class UpdateCurrentJar {

	private final UpdateHelper updateHelper;
	
	private final FileHelper fileHelper;
	
	private final File updateJar;
	
	private final File oldJarUpdate;
	
	private final Log log;
	
	public UpdateCurrentJar(UpdateHelper updateHelper, FileHelper fileHelper, Log log){
		this.updateHelper = updateHelper;
		this.fileHelper = fileHelper;
		this.log = log;
		this.updateJar = this.updateHelper.getFileFromUpdateDirectory(this.updateHelper.getCurrentJarName());
		this.oldJarUpdate = this.updateHelper.getFileFromUpdateDirectory("_" + this.updateJar.getName()); 
	}
	
	public boolean hasUpdate(){
		return this.updateJar.exists();
	}
	
	public File renameUpdateJar(){
		if(this.updateJar.exists()){
			log.log(this.getClass(), "Renomeando arquivo: '" + this.updateJar.getAbsolutePath() + "' para: '" + this.oldJarUpdate.getAbsolutePath() + "'");
			if(!this.updateJar.renameTo(this.oldJarUpdate)){
				log.log(this.getClass(), "Problemas ao renomear arquivo: '" + this.updateJar.getAbsolutePath() + "' para: '" + this.oldJarUpdate.getAbsolutePath() + "'");
			} else {
				log.log(this.getClass(), "Arquivo renomeando com sucesso.");
			}
		}
		return this.oldJarUpdate;
	}
	
	public boolean hasOldUpdate(){
		return this.oldJarUpdate.exists();
	}
	
	public boolean isRunningFromUpdate() {
		return this.updateHelper.isRunningFromUpdate();
	}
	
	public File update(){
		File parentFile = this.updateHelper.getParentFile();
		if(parentFile.exists() && parentFile.isDirectory()){
			File parentJarFile = this.updateHelper.getJarFile(parentFile);
			File currentJarFile = this.updateHelper.getCurrentJarFile();
			if(parentJarFile.exists()){
				log.log(this.getClass(), "Excluindo arquivo: '" + parentJarFile.getAbsolutePath() + "'");
				if(!parentJarFile.delete()){
					log.log(this.getClass(), "Impossível excluir: " + parentJarFile.getAbsolutePath());
				} else {
					log.log(this.getClass(), "Arquivo excluído com sucesso.");
				}
			}
			return updateCurrentJar(currentJarFile, parentJarFile);
		}
		return null;
	}

	private File updateCurrentJar(File jarFile, File parentJarFile) {
		if(jarFile != null && jarFile.exists() && jarFile.isFile()){
			if(!this.fileHelper.copyFile(jarFile, parentJarFile)){
				log.log(this.getClass(), "Problemas ao atualizar arquivo: " + parentJarFile.getAbsolutePath());
			} 
		}
		return parentJarFile;
	}

	public void deleteOldUpdate() {
		log.log(this.getClass(), "Excluindo arquivo: '" + this.oldJarUpdate.getAbsolutePath() + "'");
		if(!this.oldJarUpdate.delete()){
			log.log(this.getClass(), "Problemas ao excluir arquivo: '" + this.oldJarUpdate.getAbsolutePath() + "'");
		} else {
			log.log(this.getClass(), "Arquivo excluído com sucesso.");
		}
	}

}
