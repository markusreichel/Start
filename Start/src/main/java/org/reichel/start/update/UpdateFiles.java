package org.reichel.start.update;

import java.io.File;

import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;

public class UpdateFiles {

	private final UpdateHelper updateHelper;
	
	private final FileHelper fileHelper;
	
	private final Log log;
	
	public UpdateFiles(UpdateHelper updateHelper, FileHelper fileHelper, Log log){
		this.updateHelper = updateHelper;
		this.fileHelper = fileHelper;
		this.log = log;
	}
	
	public void update(){
		File updateDirectory = this.updateHelper.getUpdateDirectory();
		if(updateDirectory != null){
			if(updateDirectory.exists() && updateDirectory.isDirectory()){
				File[] listFiles = updateDirectory.listFiles();
				if(listFiles.length > 0){
					log.log(this.getClass(), "-----------------------------");
					log.log(this.getClass(), "Arquivos a serem atualizados:");
					for(File file : listFiles){
						log.log(this.getClass(), file.getAbsolutePath());
					}
					log.log(this.getClass(), "-----------------------------");
		
					for(File file : listFiles){
						File applicationPath = new File(this.updateHelper.getCurrentDirectory().getAbsolutePath() + this.fileHelper.getRelativePath(this.updateHelper.getUpdateDirectory(), file));
						log.log(this.getClass(), "Preparando para mover tudo de '" + file.getAbsolutePath() + "' para '" + applicationPath.getAbsolutePath() + "'.");
						if(file.isDirectory()){
							log.log(this.getClass(), "Excluindo diretório e sub-diretórios: " + applicationPath.getAbsolutePath());
							this.fileHelper.deleteAll(applicationPath);
						} else if(file.isFile() && applicationPath.exists()){
							if(applicationPath.delete()){
								log.log(this.getClass(), "Arquivo excluído com sucesso: " + applicationPath.getAbsolutePath());
							} else {
								log.log(this.getClass(), "Impossível excluir arquivo: " + applicationPath.getAbsolutePath());
							}
						}
						log.log(this.getClass(), "Movendo tudo de '" + file.getAbsolutePath() + "' para '" + applicationPath.getAbsolutePath() + "'.");
						this.fileHelper.moveAll(file, applicationPath);
					}
				}
			} else {
				log.log(getClass(), "Diretório de update não existe. Não existem atualizações. " + updateDirectory.getAbsolutePath());
			}
		}
	}
}
