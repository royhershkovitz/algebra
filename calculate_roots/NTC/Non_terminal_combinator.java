package NTC;

public interface Non_terminal_combinator<T> {
	public boolean isExpr(String expr);
	public Pair<T,String>  getElemnt(String expr);
}