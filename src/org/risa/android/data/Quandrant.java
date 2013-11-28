package org.risa.android.data;

/**
 * Class that represents the quandrant with a cartesian plane a focal point is in.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public enum Quandrant {

	ONE("Q1"), TWO("Q2"), THREE("Q3"), FOUR("Q4"); 
	
	private final String name;
	
	private Quandrant(String s) {
		name = s;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
