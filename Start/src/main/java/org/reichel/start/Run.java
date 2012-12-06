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

	public static void main(String[] args) throws IOException {
		if(args == null || args.length < 2){
			throw new IllegalArgumentException("Parametro inválido.");
		} 
//		String[] args2 = new String[]{"file:///D:/Documents/Markus/git/URei/URei/target/release","D:\\Documents\\Markus\\git\\UpdateGui\\UpdateGui\\target\\ambiente\\update"};

		UpdateHelper updateHelper = new UpdateHelper();
		Log log = new Log(updateHelper);
		FileHelper fileHelper = new FileHelper(log);
		if("applyUpdates".equals(args[0])){
			
			updateCurrentJar(args, updateHelper, fileHelper, log); 
			updateApplication(updateHelper, log, fileHelper);
			//inicie utilizando update.jar
			System.out.println(System.getProperty("log4j.configuration"));
			restart(new String[]{"checkUpdates",args[1]}, "\"" + updateHelper.getCurrentJarFile().getAbsolutePath() + ";" + updateHelper.getCurrentJarFile().getParentFile().getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar + "*\"", log, System.getProperty("log4j.configuration"));
		} else if("checkUpdates".equals(args[0])){
			try {
				Object isUpdated = Class.forName("org.reichel.update.gui.UpdateManager").getMethod("doUpdate", String.class).invoke(null, (Object)updateHelper.getCurrentDirectory().getAbsolutePath());
				log.log(Run.class, "Resposta da classe org.reichel.update.gui.UpdateManager.doUpdate('" +updateHelper.getCurrentDirectory().getAbsolutePath() + "') : " + isUpdated.toString());
				if(isUpdated instanceof Boolean){
					//this means that it has updates to apply, so restart application...
					if(((Boolean) isUpdated) == false){
						//não esta atualizado, reinicie utilizando apenas start.jar
						restart(new String[]{"applyUpdates",args[1]}, "\"" + updateHelper.getCurrentJarFile().getAbsolutePath() + "\"", log, System.getProperty("log4j.configuration"));
					} else {
						//nada a fazer reinicie com o classpath completo
						restart(new String[]{"application",args[1]}, "\"" + updateHelper.getCurrentJarFile().getAbsolutePath() + "\";" + updateHelper.getCurrentJarFile().getParentFile().getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar + "*", log, System.getProperty("log4j.configuration"));
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if("application".equals(args[0])){
			//nothing else to do, start application
			try {
				Class.forName(args[0]).getMethod("main", String[].class).invoke(null, (Object)null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			StringBuilder sb = new StringBuilder("Usage:");
			sb.append(System.getProperty("line.separator"))
			  .append(" java -cp start.jar org.reichel.start.Run applyUpdates org.reichel.ambiente.gui.Manager")
			  .append(System.getProperty("line.separator"))
			  .append(" java -cp start.jar;lib/update.jar org.reichel.start.Run checkUpdates org.reichel.ambiente.gui.Manager");
			throw new IllegalArgumentException(sb.toString());
		}
	}

	private static void updateApplication(UpdateHelper updateHelper, Log log, FileHelper fileHelper) {
		Update update = new Update(updateHelper, fileHelper, log);
		if(update.hasUpdateToInstall()){
			update.update();
		}
	}

	private static void updateCurrentJar(String[] args, UpdateHelper updateHelper, FileHelper fileHelper, Log log) throws IOException {
		UpdateCurrentJar updateCurrentJar = new UpdateCurrentJar(updateHelper, fileHelper, log);
		if(updateCurrentJar.hasOldUpdate()){
			log.log(Run.class, "Existe start.jar de atualização antiga, preparando para excluí-lo.");
			updateCurrentJar.deleteOldUpdate();
		}
		
		if(updateCurrentJar.hasUpdate()){
			log.log(Run.class, "Existe update a ser aplicado, preparando para aplica-lo.");
			restart(args, updateCurrentJar.renameUpdateJar(), log);
		} else if(updateCurrentJar.isRunningFromUpdate()){
			log.log(Run.class, "Executando aplicação de atualização.");
			restart(args, updateCurrentJar.update(), log);
		} 
	}
	
//	private static void restart(String[] args, UpdateHelper updateHelper, Log log) throws IOException{
//		restart(args, updateHelper.getCurrentJarFile(), log);
//	}

	private static void restart(String[] args, File startJar, Log log) throws IOException {
		String[] restartCommand = getRestartCommand(args, startJar);
		log.log(Run.class, "Reiniciando aplicativo utilizando o comando:" + Arrays.toString(restartCommand));
		log.log(Run.class, "-----------------------------");
		final ProcessBuilder processBuilder = new ProcessBuilder(getRestartCommand(args, startJar));
		processBuilder.start();
		log.end();
		System.exit(0);
	}

	private static void restart(String[] args, String cp, Log log, String log4jConfiguration) throws IOException {
		String[] restartCommand = getRestartCommand(args, cp, log4jConfiguration);
		log.log(Run.class, "Reiniciando aplicativo utilizando o comando:" + Arrays.toString(restartCommand));
		log.log(Run.class, "-----------------------------");
		final ProcessBuilder processBuilder = new ProcessBuilder(getRestartCommand(args, cp, log4jConfiguration));
		processBuilder.start();
		log.end();
		System.exit(0);
	}
	
	private static String[]  getRestartCommand(String[] args, String cp, String log4jConfiguration) {
		List<String> command = new ArrayList<String>();
		command.add("\"" + System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java.exe\"");
		command.add("-Dlog4j.configuration=" + log4jConfiguration);
		command.add("-cp");
		command.add(cp);
		command.add(Run.class.getName());
		for(String arg : args){
			command.add(arg);
		}
		
		return command.toArray(new String[command.size()]);
	}
	
	private static String[] getRestartCommand(String[] args, File startJar) {
		List<String> command = new ArrayList<String>();
		command.add("\"" + System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java.exe\"");
		command.add("-cp");
		command.add("\"" + startJar.getAbsolutePath() + "\"");
		command.add(Run.class.getName());
		for(String arg : args){
			command.add(arg);
		}
		return command.toArray(new String[command.size()]);
	}

}
