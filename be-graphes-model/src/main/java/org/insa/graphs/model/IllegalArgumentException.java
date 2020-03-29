package org.insa.graphs.model;

public class IllegalArgumentException extends Exception { 
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Si deux noeuds consécutif ne sont pas liés, l'exception est levée
	 * @param String Précisant les noeuds concernés
	 */

	public IllegalArgumentException(String arg){
		  super(arg);
		  System.out.println(arg);
	  }  
}
