package org.reichel.start;

import java.lang.reflect.InvocationTargetException;

public class Run {

	public static void main(String[] args) {
		if(args == null || args.length == 0){
			throw new IllegalArgumentException("Parametro inválido.");
		} 
		
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
	}
}
