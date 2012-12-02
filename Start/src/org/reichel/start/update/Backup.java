package org.reichel.start.update;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;

public class Backup {

	private final Log log;
	
	private final UpdateHelper updateHelper;
	
	private final FileHelper fileHelper;
	
	public Backup(UpdateHelper updateHelper, Log log, FileHelper fileHelper){
		this.updateHelper = updateHelper;
		this.log = log;
		this.fileHelper = fileHelper;
	}
	
	public void backup(FileFilter fileFilter){
		List<File> applicationFilesToBackup = this.fileHelper.listFiles(this.updateHelper.getCurrentDirectory(), new ArrayList<File>(), fileFilter);
		if(applicationFilesToBackup.size() > 0){
			log.log(this.getClass(), "-----------------------------");
			File backup = new File(this.updateHelper.getCurrentDirectory().getAbsolutePath() + File.separatorChar + UpdateHelper.BACAKUP_DIR);
			createBackupTargetFolder(backup);
			log.log(this.getClass(), "Arquivos para backup:");
			for(File backupFile : applicationFilesToBackup){
				log.log(this.getClass(), backupFile.getAbsolutePath());
			}
			log.log(this.getClass(), "-----------------------------");
			File backupFile = null;
			for(File applicationFile : applicationFilesToBackup){
				 backupFile = getBackupFile(applicationFile, backup);
				 if(backupFile.getAbsoluteFile().getParentFile() != null && backupFile.getAbsoluteFile().getParentFile().exists()){
					 doBackupProperties(backupFile, applicationFile);
				 }
			}
		}
	}

	
	
	private void createBackupTargetFolder(File backup) {
		if(!backup.exists()){
			if(backup.mkdirs()){
				log.log(this.getClass(), "Diretório de backup criado com sucesso: " + backup.getAbsolutePath());
			} else {
				log.log(this.getClass(), "Impossível criar diretório de backup: " + backup.getAbsolutePath());
			}
		}
	}

	private void doBackupProperties(File backupFile, File applicationFile) {
		if(!backupFile.getAbsoluteFile().getParentFile().exists()){
			if(backupFile.getAbsoluteFile().getParentFile().mkdirs()){
				 log.log(this.getClass(), "Diretório criado com sucesso: " + backupFile.getAbsoluteFile().getParentFile());
			 } else {
				 log.log(this.getClass(), "Impossível criar diretório: " + backupFile.getAbsoluteFile().getParentFile());
			 }
		}
		
		if(backupFile.getAbsoluteFile().getParentFile().exists()){
			 if(this.fileHelper.copyFile(applicationFile, backupFile)) {
				 log.log(this.getClass(), "Arquivo '" + applicationFile.getAbsolutePath() + "' copiado com sucesso para '" + backupFile.getAbsolutePath() + "'.");
			 } else {
				 log.log(this.getClass(), "Impossível copiar arquivo '" + applicationFile.getAbsolutePath() + "' para '" + backupFile.getAbsolutePath() + "'.");
			 }
		} else {
			 log.log(this.getClass(), "Diretório de backup inexistente: " + backupFile.getAbsoluteFile().getParentFile().getAbsolutePath());
		}
	}
	
	private File getBackupFile(File applicationFile, File backup) {
		File backupFile = null;
		int i = 1;
		while(backupFile == null){
			backupFile = new File(backup.getAbsolutePath() + File.separatorChar + this.fileHelper.getRelativePath(this.updateHelper.getCurrentDirectory(), applicationFile).replace(UpdateHelper.PROPERTIES_EXTENSION, "_backup_" + new SimpleDateFormat("yyyyMMdd").format(new Date())+ "_" + i + UpdateHelper.PROPERTIES_EXTENSION));
			if(backupFile.exists()){
				backupFile = null;
			}
			i++;
		}
		return backupFile;
	}

}
