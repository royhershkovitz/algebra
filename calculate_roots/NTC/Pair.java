package NTC;

public class Pair <T,G> {
	private T left;
	private G right;
	public Pair(T a, G b) {
		left = a;
		right = b;
	}
	
	public T getLeft() {
		return left;
	}
	
	public G getRight() {
		return right;
	}
}
