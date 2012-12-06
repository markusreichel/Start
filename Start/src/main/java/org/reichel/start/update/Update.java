package org.reichel.start.update;

import org.reichel.start.Run;
import org.reichel.start.filefilter.PropertiesFileFilter;
import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;

public class Update {

	private final UpdateHelper updateHelper;
	
	private final FileHelper fileHelper;

	private final UpdateJar updateJar;
	
	private final UpdateFiles updateFiles;
	
	private final Backup backup;
	
	private final Log log;
	
	public Update(UpdateHelper updateHelper, FileHelper fileHelper, Log log){
		this.updateHelper = updateHelper;
		this.fileHelper = fileHelper;
		this.log = log;
		this.updateJar = new UpdateJar(updateHelper, log);
		this.backup = new Backup(updateHelper, log, fileHelper);
		this.updateFiles = new UpdateFiles(updateHelper, fileHelper, log);
	}
	
	public void update(){
		log.log(Run.class, "-----------------------------");
		log.log(Run.class, "Existem atualizações, preparando para aplicá-las.");
		this.updateJar.updateJarFiles();
		this.fileHelper.deleteEmptyFolder(this.updateHelper.getUpdateDirectory());
		this.backup.backup(new PropertiesFileFilter());
		this.updateFiles.update();
		this.fileHelper.deleteAll(this.updateHelper.getUpdateDirectory());
		log.log(Run.class, "Fim das atualizações.");
		log.log(Run.class, "-----------------------------");
	}
	
	public boolean hasUpdateToInstall(){
		return this.updateHelper.getUpdateDirectory().exists();
	}
}
