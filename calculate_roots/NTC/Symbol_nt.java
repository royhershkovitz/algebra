package NTC;
import ELEMENTS.Var_name;

public class Symbol_nt implements Non_terminal_combinator<String>{

	private int index = -1;
	private boolean sign;//ture is pos, false is neg
	
	@Override
	public boolean isExpr(String expr) {
		index = 0;
		sign = true;
		boolean stop = false;
		char ch = expr.charAt(index);
		if(ch == '-') {
			sign = false;
			index ++;
		}
		while(!stop & index < expr.length()) {
			ch = expr.charAt(index);
			if((ch >= '0' & ch <= '9') || (ch >= 'a' & ch <= 'z') || (ch >= 'A' & ch <= 'Z'))
				index ++;
			else 
				stop = true;
		}			
		return (sign & index>0)||(!sign & index>1);
	}

	@Override
	public Pair<String, String> getElemnt(String expr) {
			if(!isExpr(expr))	return null;
			int startIndex = 0;
			if(!sign) startIndex++;//the symbol does not contain '-'
			return new Pair(new Var_name(expr.substring(startIndex, index), sign), expr.substring(index));
	}
}
