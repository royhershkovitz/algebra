package NTC;

public interface Non_terminal_combinator<T> {
	public int isExpr(String expr);
	public Pair<T,String>  getElemnt(String expr);
}