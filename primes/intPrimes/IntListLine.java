package intPrimes;

public class IntListLine {
	private static final int lineSize = 1000; 
	private IntListLine prev = null; 
	IntListLine next = null; 
	int[] line;
	private int current; 
	IntListLine(IntListLine prev) {
		current = 0;
		this.prev = prev;
		line = new int[lineSize]; 
	}
	
	IntListLine addLast(int toAdd) {
		line[current++] = toAdd;
		if(lineSize == current) next = new IntListLine(this);
		return next;
	}
	
	int size(){
		return current;		
	}
}