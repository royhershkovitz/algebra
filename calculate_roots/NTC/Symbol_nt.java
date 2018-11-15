package NTC;
import ELEMENTS.Var_name;

public class Symbol_nt implements Non_terminal_combinator<String>{

	private int index = -1;
	
	@Override
	public int isExpr(String expr) {
		index = 0;
		boolean stop = false;
		char ch;
		while(!stop & index < expr.length()) {
			ch = expr.charAt(index);
			if((ch >= '0' & ch <= '9') || (ch >= 'a' & ch <= 'z') || (ch >= 'A' & ch <= 'Z'))
				index ++;
			else 
				stop = true;
		}			
		return index;
	}

	@Override
	public Pair<String, String> getElemnt(String expr) {
			isExpr(expr);
			if(index == 0)	return null;
			return new Pair(new Var_name(expr.substring(0, index)), expr.substring(index));
	}
}
