package NTC;
import java.math.BigDecimal;

import ELEMENTS.Number;

public class Number_nt implements Non_terminal_combinator<BigDecimal>{
	
	private int index = -1;

	public boolean isExpr(String expr) {
		index = 0;
		boolean stop = false;
		boolean negSign = true;
		char ch = expr.charAt(index);
		if(ch == '-') {
			negSign = false;
			index ++;
		}
		while(!stop & index < expr.length()) {
			ch = expr.charAt(index);
			if((ch >= '0' & ch <= '9') | ch == '.')
				index ++;
			else
				stop = true;
		}	
		return (negSign & index>0)||(!negSign & index>1);
	}

	public Pair<BigDecimal, String> getElemnt(String expr) {
		if(!isExpr(expr))	return null;
		return new Pair(new Number(expr.substring(0, index)), expr.substring(index));
	}
}
