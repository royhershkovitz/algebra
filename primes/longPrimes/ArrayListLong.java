package longPrimes;

public class ArrayListLong {
	private LongListLine currentLine = null; 
	private LongListLine first = null; 
	private int size = 0;
	public ArrayListLong() {
		currentLine = new LongListLine(null);
		first = currentLine;
	}
	
	public void addLast(long toAdd) {
		LongListLine next;
		size++;
		if((next = currentLine.addLast(toAdd)) != null)
			currentLine = next;
	}
	
	public int size() {
		return size;
	}
	
	//will set up an iterator
	private LongListLine IterCurrentLine;
	private int IterCurrentIndex;
	public void Itrartor() {
		IterCurrentLine = first;
		IterCurrentIndex = 0;
	}
	
	public boolean hasNext() {			
		return IterCurrentLine.next != null || IterCurrentLine.size() > IterCurrentIndex;
	}
	
	public long next() {
		if(IterCurrentLine.size() == IterCurrentIndex) {
			IterCurrentIndex = 0;
			IterCurrentLine = IterCurrentLine.next;
		}
		if(IterCurrentLine == null) return -1;
		return IterCurrentLine.line[IterCurrentIndex++];
	}
}
