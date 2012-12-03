package org.reichel.start.update;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.reichel.start.filefilter.JarFileFilter;
import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;

public class UpdateJar {

	private final UpdateHelper updateHelper;
	
	private final Log log;
	
	private final FileHelper fileHelper;
	
	public UpdateJar(UpdateHelper updateHelper, Log log){
		this.updateHelper = updateHelper;
		this.log = log;
		this.fileHelper = new FileHelper(log);
	}
	
	public void updateJarFiles() {
		List<File> jarFiles = this.fileHelper.listJarFiles(this.updateHelper.getUpdateDirectory(), new ArrayList<File>());
		if(jarFiles.size() > 0){
			log.log(this.getClass(), "Arquivos a serem atualizados:");
			for(File jarFile : jarFiles){
				log.log(this.getClass(), jarFile.getAbsolutePath());
			}
			log.log(this.getClass(), "-----------------------------");
			updateJarFiles(jarFiles);
			log.log(this.getClass(), "Arquivos atualizados com sucesso.");
		}
	}

	private void updateJarFiles(List<File> updateFiles) {
		boolean result;
		for(File updateFile : updateFiles){
			if(updateFile.exists() && updateFile.isFile()){
				log.log(this.getClass(), "Arquivo de atualiza��o: " + updateFile.getAbsolutePath());
				File applicationFile = getApplicationFile(updateFile);
				result = deleteApplicationFile(updateFile, applicationFile);
				if(result){
					result = createApplicationTargetFolder(applicationFile);
				}
				if(result){
					result = moveUpdateFileToApplicationTargetFolder(updateFile,	applicationFile);
				}
				if(result){
					log.log(this.getClass(), "Atualiza��o aplicada com sucesso.");
				} else {
					log.log(this.getClass(), "N�o foi poss�vel aplicar a atualiza��o.");
				}
				log.log(this.getClass(), "-----------------------------");
			}
		}
	}

	private boolean moveUpdateFileToApplicationTargetFolder(File updateFile, File applicationFile) {
		boolean result = false;
		if(updateFile.getAbsolutePath().endsWith(UpdateHelper.DELETE_EXTENSION)){
			result = true;
		} else {
			if(updateFile.renameTo(applicationFile)){
				log.log(this.getClass(), "Arquivo '" + updateFile.getAbsolutePath() + "' movido com sucesso para '" + applicationFile.getAbsolutePath() + "'.");
				result = true;
			} else {
				log.log(this.getClass(), "Imposs�vel mover o arquivo '" + updateFile.getAbsolutePath() + "' para '" + applicationFile.getAbsolutePath() + "'.");
				result = false;
			}
		}
		return result;
	}

	private boolean createApplicationTargetFolder(File applicationFile) {
		boolean result = false;
		File parent = applicationFile.getAbsoluteFile().getParentFile();
		if(parent != null && parent.exists()){
			result = true;
		} else if(parent != null && parent.mkdirs()){
			log.log(this.getClass(), "Diret�rios criados com sucesso: " + parent.getAbsolutePath());
			result = true;
		} else if(parent != null) {
			log.log(this.getClass(), "Imposs�vel criar diret�rios: " + parent.getAbsolutePath());
			result = false;
		} else {
			log.log(this.getClass(), "Imposs�vel criar diret�rios, diret�rio pai do arquivo '" + applicationFile.getAbsolutePath() + "'esta retornando null.");
			result = false;
		}
		return result;
	}

	private boolean deleteApplicationFile(File updateFile, File applicationFile) {
		boolean result = true;
		if(applicationFile != null && applicationFile.exists()){
			log.log(this.getClass(), "Excluindo arquivo: " + applicationFile.getAbsolutePath());
			if(applicationFile.delete()){
				log.log(this.getClass(), "Arquivo exclu�do com sucesso.");
				result = true;
			} else {
				log.log(this.getClass(), "Imposs�vel excluir arquivo: " + applicationFile.getAbsolutePath());
				result = false;
			}
		} else {
			log.log(this.getClass(), "Arquivo: '" + applicationFile.getAbsolutePath() + "' n�o encontrado.");
		}
		return result;
	}

	private File getApplicationFile(File updateFile) {
		File result = null;
		if(updateFile.getAbsolutePath().endsWith(".delete")){
			log.log(this.getClass(), "Arquivo '" + updateFile.getAbsolutePath() + "' � um arquivo oco, para marcar a exclus�o do arquivo da aplica��o.");
			result = new File(this.updateHelper.getCurrentDirectory() + this.fileHelper.getRelativePath(this.updateHelper.getUpdateDirectory(), new File(updateFile.getAbsolutePath().replace(UpdateHelper.DELETE_EXTENSION, ""))));
			if(updateFile.delete()){
				log.log(this.getClass(), "Arquivo oco '" + updateFile.getAbsolutePath() + "' exclu�do com sucesso.");
			} else {
				log.log(this.getClass(), "Imposs�vel excluir arquivo: " + updateFile.getAbsolutePath());
			}
		} else {
			result = new File(this.updateHelper.getCurrentDirectory() + this.fileHelper.getRelativePath(this.updateHelper.getUpdateDirectory(), updateFile));
		}
		log.log(this.getClass(), "Arquivo '" + result.getAbsolutePath() + "' marcado para exclus�o.");
		return result;
	}

	public File[] listJarUpdates(){
		return listUpdateFiles(new JarFileFilter());
	}

	public File[] listUpdateFiles(FileFilter filter){
		if(filter != null){
			return this.updateHelper.getUpdateDirectory().listFiles(filter);
		} 
		return this.updateHelper.getUpdateDirectory().listFiles(); 
	}
	
}
