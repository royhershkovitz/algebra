package NTC;

import java.math.BigDecimal;
import java.util.List;

import ELEMENTS.Element;
import ELEMENTS.Number;
import ELEMENTS.Sub_exper;
import parser.Parser;

public class parn_nt implements Non_terminal_combinator<List<Element>>{
	String rest;
	List<Element> sub_exper;
	@Override
	public boolean isExpr(String expr) {
		if(expr.charAt(0) == '(') {
			Pair<List<Element>, String> pair = (new Parser()).tag(expr.substring(1));
			sub_exper = pair.getLeft();
			rest = pair.getRight();
			if(rest.charAt(0) == ')')	{
				rest = rest.substring(1);
				return true;			
			}
		}
		return false;
	}

	@Override
	public Pair<List<Element>, String> getElemnt(String expr) {
			if(!isExpr(expr))	return null;
			return new Pair(new Sub_exper(sub_exper), rest);
	}
}
