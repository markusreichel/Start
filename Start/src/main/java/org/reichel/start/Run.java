package org.reichel.start;

import java.io.File;
import java.io.IOException;
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
		if(args == null || args.length == 0){
			throw new IllegalArgumentException("Parametro inválido.");
		} 
		
//		String[] args2 = new String[]{"file:///D:/Documents/Markus/git/URei/URei/target/release","D:\\Documents\\Markus\\git\\UpdateGui\\UpdateGui\\target\\ambiente\\update"};
		UpdateHelper updateHelper = new UpdateHelper();
		Log log = new Log(updateHelper);
		FileHelper fileHelper = new FileHelper(log);
		
		updateCurrentJar(args, updateHelper, fileHelper, log); 
		Update update = new Update(updateHelper, fileHelper, log);
		if(update.hasUpdate()){
			update.update();
		} 
		
		
		//TODO: check for updates;
		//TODO: if there is updates restart application
		//TODO: else restart using a argument without update to avoid new updates.
		
//		try {
//			Class.forName("org.reichel.update.gui.UpdateManager").getMethod("main", String[].class).invoke(null, (Object)args2);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}

		
//		try {
//			Class.forName(args[0]).getMethod("main", String[].class).invoke(null, (Object)null);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
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

	private static void restart(String[] args, File startJar, Log log) throws IOException {
		String[] restartCommand = getRestartCommand(args, startJar);
		log.log(Run.class, "Reiniciando aplicativo utilizando o comando:" + Arrays.toString(restartCommand));
		log.log(Run.class, "-----------------------------");
		final ProcessBuilder processBuilder = new ProcessBuilder(getRestartCommand(args, startJar));
		processBuilder.start();
		log.end();
		System.exit(0);
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
