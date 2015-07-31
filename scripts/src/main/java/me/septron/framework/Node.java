package me.septron.framework;

/**
 * @author Septron
 * @since July 31, 2015
 */
public interface Node {

	default int priority() { return  1; }

	boolean validate();
	String status();
	int execute();
}
