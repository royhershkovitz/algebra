package NTC;
import java.math.BigDecimal;

import ELEMENTS.Number;

public class Number_nt implements Non_terminal_combinator<BigDecimal>{
	
	private int index = -1;

	@Override
	public int isExpr(String expr) {
		index = 0;
		boolean stop = false;
		int ch;
		while(!stop & index < expr.length()) {
			ch = expr.charAt(index);
			if((ch >= '0' & ch <= '9') | ch == '.')
				index ++;
			else
				stop = true;
		}			
		return index;
	}

	@Override
	public Pair<BigDecimal, String> getElemnt(String expr) {
		isExpr(expr);
		if(index == 0)	return null;
		return new Pair(new Number(expr.substring(0, index)), expr.substring(index));
	}
}
