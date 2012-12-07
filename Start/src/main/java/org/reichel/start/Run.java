package org.reichel.start;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reichel.start.helper.FileHelper;
import org.reichel.start.helper.UpdateHelper;
import org.reichel.start.log.Log;
import org.reichel.start.update.Update;
import org.reichel.start.update.UpdateCurrentJar;

public class Run {

	private final UpdateHelper updateHelper;
	
	private final Log log;
	
	private final FileHelper fileHelper;
	
	private final List<String> parameters;
	
	private final String log4jConfiguration;
	
	private final String updateManager = "org.reichel.update.gui.UpdateManager";
	
	public Run(String[] parameters) throws IOException{
		if(parameters == null){
			throw new IllegalArgumentException("Parametros nulo.");
		}
		this.updateHelper = new UpdateHelper();
		this.log = new Log(updateHelper);
		log.startingLog(this.getClass());
		this.parameters = new ArrayList<String>();
		this.fileHelper = new FileHelper(log);
		String log4jConfiguration = System.getProperty("log4j.configuration"); 
		if(log4jConfiguration == null){
			log4jConfiguration = new File(this.updateHelper.getCurrentDirectory().getAbsolutePath() + File.separatorChar + "config" + File.separatorChar + "log4j.properties").toURI().toString();
			System.setProperty("log4j.configuration", log4jConfiguration);
		}
		this.log4jConfiguration = log4jConfiguration;
		for(String parameter : parameters){
			log("Parametro recebido: " + parameter);
			this.parameters.add(parameter);
		}
	}
	
	public void applyUpdatesAndRestart(){
		log("Executando applyUpdatesAndRestart()");
		removeParameter("applyUpdates");
		updateCurrentJar();
		updateApplication();
		try {
			this.parameters.add("checkUpdates");
			restart(updateHelper.getCurrentJarFile().getAbsolutePath() + ";" + getLibPath());
		} catch (IOException e) {
			log("Problemas ao iniciar execução de processo. " + e.getMessage());
			log("Terminando aplicação.");
			System.exit(1);
		}
	}

	protected void updateCurrentJar(){
		log("Executando updateCurrentJar()");
		UpdateCurrentJar updateCurrentJar = new UpdateCurrentJar(updateHelper, fileHelper, log);
		if(updateCurrentJar.hasOldUpdate()){
			log("Existe start.jar de atualização antiga, preparando para excluí-lo.");
			updateCurrentJar.deleteOldUpdate();
		}
		
		if(updateCurrentJar.hasUpdate()){
			log("Existe update a ser aplicado, preparando para aplica-lo.");
			try {
				restart(updateCurrentJar.renameUpdateJar());
			} catch (IOException e) {
				log("Problemas ao iniciar execução de processo. " + e.getMessage());
				log("Terminando aplicação.");
				System.exit(1);
			}
		} else if(updateCurrentJar.isRunningFromUpdate()){
			log("Executando aplicação de atualização.");
			try {
				restart(updateCurrentJar.update());
			} catch (IOException e) {
				log("Problemas ao iniciar execução de processo. " + e.getMessage());
				log("Terminando aplicação.");
				System.exit(1);
			}
		} 
	}

	protected void application() {
		log("Executando application()");
		//Inicie a aplicação.
		String mainApplication = this.parameters.get(0);
		try {
			Class.forName(mainApplication).getMethod("main", String[].class).invoke(null, (Object)null);
		} catch (IllegalAccessException e) {
			log("Problemas ao instanciar: " + mainApplication + ". " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log("Problemas ao instanciar: " + mainApplication + ". " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (InvocationTargetException e) {
			log("Problemas ao instanciar: " + mainApplication + ". " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchMethodException e) {
			log("Problemas ao instanciar: " + mainApplication + ". " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (SecurityException e) {
			log("Problemas ao instanciar: " + mainApplication + ". " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e) {
			log("Aplicação não encontrada. " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} finally{
			log.endingLog(getClass());
		}
	}

	protected void checkUpdates() {
		log("Executando checkUpdates()");
		removeParameter("checkUpdates");
		try {
			Object result = Class.forName(this.updateManager).getMethod("doUpdate", String.class).invoke(null, (Object)updateHelper.getCurrentDirectory().getAbsolutePath());
			log("Executado: " + this.updateManager + ".doUpdate('" + updateHelper.getCurrentDirectory().getAbsolutePath() + "')");
			if(result instanceof Boolean){
				Boolean isUpdated = (Boolean) result; 
				//Aplicação esta atualizada, inicie.
				if(isUpdated){
					startApplication();
				} else {
					//Aplicação não esta atualizado, reinicie utilizando apenas start.jar
					log("Aplicação não está atualizada.");
					this.parameters.add("applyUpdates");
					try {
						restart(updateHelper.getCurrentJarFile().getAbsolutePath());
					} catch (IOException e) {
						log("Problemas ao iniciar execução de processo. " + e.getMessage());
						log("Terminando aplicação.");
						System.exit(1);
					}
				}
			}
		} catch (IllegalAccessException e) {
			log("Problemas ao instanciar: " + this.updateManager + ". " + e.getMessage());
		} catch (IllegalArgumentException e) {
			log("Problemas ao instanciar: " + this.updateManager + ". " + e.getMessage());			
		} catch (InvocationTargetException e) {
			log("Problemas ao instanciar: " + this.updateManager + ". " + e.getMessage());			
		} catch (NoSuchMethodException e) {
			log("Problemas ao instanciar: " + this.updateManager + ". " + e.getMessage());
		} catch (SecurityException e) {
			log("Problemas ao instanciar: " + this.updateManager + ". " + e.getMessage());			
		} catch (ClassNotFoundException e) {
			log("Biblioteca de update não encontrado. Desta forma a aplicação não buscará por atualizações. Aplicação continuará sem problemas. " + e.getMessage());
			startApplication();
		} 
	}

	private void startApplication() {
		//nada a fazer reinicie com o classpath completo
		log("Aplicação está atualizada.");
		this.parameters.add("application");
		try {
			restart(updateHelper.getCurrentJarFile().getAbsolutePath() + ";" + getLibPath());
		} catch (IOException e) {
			log("Problemas ao iniciar execução de processo. " + e.getMessage());
			log("Terminando aplicação.");
			System.exit(1);
		}
	}
	
	protected void updateApplication() {
		log("Executando updateApplication()");
		Update update = new Update(updateHelper, fileHelper, log);
		if(update.hasUpdateToInstall()){
			update.update();
		}
	}

	private String[]  getRestartCommand(String cp) {
		List<String> command = new ArrayList<String>();
		command.add("\"" + System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java.exe\"");
		if(log4jConfiguration != null){
			command.add("-Dlog4j.configuration=" + log4jConfiguration);
		}
		command.add("-cp");
		command.add("\"" + cp + "\"");
		command.add(Run.class.getName());
		for(String arg : this.parameters){
			command.add(arg);
		}
		
		return command.toArray(new String[command.size()]);
	}
	
	protected void restart(String cp) throws IOException{
		String[] restartCommand = getRestartCommand(cp);
		log("Reiniciando aplicativo utilizando o comando:" + Arrays.toString(restartCommand));
		final ProcessBuilder processBuilder = new ProcessBuilder(restartCommand);
		processBuilder.start();
		log.endingLog(getClass());
		System.exit(0);
	}

	protected void restart(File startJar) throws IOException {
		restart(startJar.getAbsolutePath() + ";");
	}

	private void removeParameter(String parameter) {
		try {
			this.parameters.remove(parameter);
		} catch(Exception e) {
			log("Problemas ao remover parametro '" + parameter + "' da lista de parametros" + e.getMessage());
		}
	}

	protected static void showUsage() {
		System.out.println("Use: <descrever como utilizar>");
	}

	protected String getLibPath() {
		return this.updateHelper.getCurrentJarFile().getParentFile().getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar + "*";
	}

	public void log(String message){
		this.log.log(this.getClass(), message);
	}

	public static void main(String[] args) throws IOException{
		if(args == null || args.length == 0){
			showUsage();
		}
		Run run = new Run(args);
		if(args.length == 1 || "applyUpdates".equals(args[args.length - 1])){
			run.applyUpdatesAndRestart();
		} else if("checkUpdates".equals(args[args.length - 1])){
			run.checkUpdates();
		} else if("application".equals(args[args.length - 1])){
			run.application();
		} else {
			showUsage();
		}
	}
	
}
