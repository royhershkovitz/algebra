package longPrimes;

public class LongListLine {
	private static final int lineSize = 1000; 
	private LongListLine prev = null; 
	LongListLine next = null; 
	long[] line;
	private int current; 
	LongListLine(LongListLine prev) {
		current = 0;
		this.prev = prev;
		line = new long[lineSize]; 
	}
	
	LongListLine addLast(long toAdd) {
		line[current++] = toAdd;
		if(lineSize == current) next = new LongListLine(this);
		return next;
	}
	
	int size(){
		return current;		
	}
}