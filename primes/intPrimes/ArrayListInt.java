package intPrimes;

public class ArrayListInt {
	private IntListLine currentLine = null; 
	private IntListLine first = null; 
	private int size = 0;
	public ArrayListInt() {
		currentLine = new IntListLine(null);
		first = currentLine;
	}
	
	public void addLast(int toAdd) {
		IntListLine next;
		size++;
		if((next = currentLine.addLast(toAdd)) != null)
			currentLine = next;
	}
	
	public int size() {
		return size;
	}
	
	//will set up an iterator
	private IntListLine IterCurrentLine;
	private int IterCurrentIndex;
	public void Itrartor() {
		IterCurrentLine = first;
		IterCurrentIndex = 0;
	}
	
	public boolean hasNext() {			
		return IterCurrentLine.next != null || IterCurrentLine.size() > IterCurrentIndex;
	}
	
	public int next() {
		if(IterCurrentLine.size() == IterCurrentIndex) {
			IterCurrentIndex = 0;
			IterCurrentLine = IterCurrentLine.next;
		}
		if(IterCurrentLine == null) return -1;
		return IterCurrentLine.line[IterCurrentIndex++];
	}
}
