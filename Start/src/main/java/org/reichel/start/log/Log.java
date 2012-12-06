package org.reichel.start.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.reichel.start.helper.UpdateHelper;

/**
 * Classe para logar as modificações de atualização.
 * Apesar de existir bibliotecas melhores para log, o intuito deste projeto é não depender de nenhuma biblioteca
 * para que seja possível fazer todas as atualizações sem maiores complicações.
 * @author Markus Reichel
 * <pre>
 * 	History: 01/12/2012 - Markus Reichel
 * </pre>
 *
 */
public class Log {

	private final UpdateHelper updateHelper;
	
	private final FileWriter fw;
	
	private final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public Log(UpdateHelper updateHelper) throws IOException {
		this.updateHelper = updateHelper;
		
		if(this.updateHelper.isRunningFromUpdate()){
			this.fw = new FileWriter(getLogFile(this.updateHelper.getParentFile()), true);
		} else {
			this.fw = new FileWriter(getLogFile(this.updateHelper.getCurrentDirectory()), true);
		}
	}
	
	private File getLogFile(File file) throws IOException {
		File targetLog = new File(file.getAbsolutePath() + File.separatorChar + "log");
		if(!targetLog.exists()){
			targetLog.mkdirs();
		}
		File log = new File(targetLog.getAbsolutePath() + File.separatorChar + "update.log");
		if(!log.exists()){
			log.createNewFile();
		}
		return log;
	}
	
	public Log log(Class<?> clazz, String log) {
		try {
			this.fw.append(getDate() + " " + clazz.getName() + " " + log + System.getProperty("line.separator"));
			this.fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	private String getDate(){
		return this.sf.format(new Date());
	}
	
	public void end(){
		try {
			this.fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
