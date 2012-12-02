package org.reichel.start.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.reichel.start.filefilter.JarFileFilter;
import org.reichel.start.log.Log;

public class FileHelper {

	private final Log log;
	
	public FileHelper(Log log){
		this.log = log;
	}

	public List<File> listJarFiles(File targetPath, List<File> jars){
		return listFiles(targetPath, jars, new JarFileFilter());
	}

	public List<File> listFiles(File targetPath, List<File> jars, FileFilter fileFilter){
		if(targetPath.isDirectory()){
			File[] files = null;
			if(fileFilter == null){
				files = targetPath.listFiles();
			} else {
				files = targetPath.listFiles(fileFilter);
			}
			for(File file : files){
				if(file.isDirectory()){
					listFiles(file, jars, fileFilter);
				} else {
					jars.add(file);
				}
			}
		} 
		return jars;
	}

	public void deleteEmptyFolder(File targetPath){
		log.log(this.getClass(), "Excluindo diretórios vazios de: " + targetPath.getAbsolutePath());
		deleteAll(targetPath, true);
	}
	
	public void deleteAll(File targetPath){
		deleteAll(targetPath, false);
	}
	
	private void deleteAll(File targetPath, boolean onlyEmpty){
		if(targetPath.isDirectory()){
			File[] files = targetPath.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					deleteAll(file, onlyEmpty);
				}
				if(onlyEmpty == false && file.exists()){
					if(file.delete()){
						log.log(this.getClass(),"Arquivo excluído com sucesso: " + targetPath.getAbsolutePath());
					} else {
						log.log(this.getClass(), "Impossível excluir arquivo: " + targetPath.getAbsolutePath());
					}
				}
			}
			if(targetPath.listFiles().length == 0 && targetPath.exists()){
				if(targetPath.delete()){
					log.log(this.getClass(),"Diretório excluído com sucesso: " + targetPath.getAbsolutePath());
				} else {
					log.log(this.getClass(),"Impossível excluir diretório: " + targetPath.getAbsolutePath());
				}
			}
		}
	}

	public String getRelativePath(File from, File file) {
		return file.getAbsolutePath().substring(from.getAbsolutePath().length());
	}

	public boolean copyFile(File jarFile, File parentJarFile) {
		log.log(this.getClass(), "Copiando arquivo: '" + jarFile.getAbsolutePath() + "' para: '" + parentJarFile.getAbsolutePath() + "'");
		boolean result = false;
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(parentJarFile);
			FileInputStream fis = new FileInputStream(jarFile);
			while(fis.read(buffer) != -1){
				fos.write(buffer);
			}
			fos.flush();
			fos.close();
			fis.close();
			result = true;
		} catch (FileNotFoundException e) {
			log.log(this.getClass(), "Problema ao copiar arquivo: '" + jarFile.getAbsolutePath() + "' para: '" + parentJarFile.getAbsolutePath() + "' " + e.getMessage());
		} catch (IOException e) {
			log.log(this.getClass(), "Problema ao copiar arquivo: '" + jarFile.getAbsolutePath() + "' para: '" + parentJarFile.getAbsolutePath() + "' " + e.getMessage());
		}
		log.log(this.getClass(), "Arquivo copiado com sucesso.");
		return result;
	}

	public void moveAll(File from, File to) {
		if(from.isDirectory()){
			for(File newFrom : from.listFiles()){
				if(newFrom.isDirectory()){
					File newTo = new File(to.getAbsolutePath() + this.getRelativePath(from, newFrom));
					log.log(this.getClass(), "Preparando para mover: '" + newFrom.getAbsolutePath() + "' para: '" + newTo.getAbsolutePath() + "'.");
					moveAll(newFrom, newTo);
				} else if(newFrom.isFile()){
					File newTo = new File(to.getAbsolutePath() + this.getRelativePath(from, newFrom));
					log.log(this.getClass(), "Movendo: '" + newFrom.getAbsolutePath() + "' para: '" + newTo.getAbsolutePath() + "'.");
					moveFile(newFrom, newTo);
				}
			}
		} else if(from.isFile()){
			log.log(this.getClass(), "Movendo: '" + from.getAbsolutePath() + "' para: '" + to.getAbsolutePath() + "'.");
			moveFile(from, to);
		}
	}

	private void moveFile(File from, File to) {
		if(!to.getAbsoluteFile().getParentFile().exists()){
			if(to.getAbsoluteFile().getParentFile().mkdirs()){
				log.log(this.getClass(), "Diretório criado com sucesso: " + to.getAbsoluteFile().getParentFile());
			} else {
				log.log(this.getClass(), "Impossível criar diretório: " + to.getAbsoluteFile().getParentFile());
			}
		}
		
		if(from.renameTo(to)){
			log.log(this.getClass(), "Arquivo '" + from.getAbsolutePath() + "' movido com sucesso para '" + to.getAbsolutePath() + "'.");
		} else {
			log.log(this.getClass(), "Impossível mover arquivo '" + from.getAbsolutePath() + "' para '" + to.getAbsolutePath() + "'.");
		}
	}
	
}
