import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import ELEMENTS.Element;
import NTC.*;

public class Parser {
	private static final Non_terminal_combinator[] parsers = {new Number_nt(), new Constant_nt(), new Symbol_nt(), new Operator_nt()};
	//will trim the spaces, return null if string is empty
	private String star_space(String st) {
		int i = 0;
		while(i < st.length() && st.charAt(i) == ' ')
			i++;
		st = st.substring(i);
		if(st.isEmpty()) return null;
		return st;
	}
		
	//will return true if solved the equation
	public boolean solve(String st) throws Exception {
		List<Element> elements = new LinkedList<Element>();
		Pair<Element,String> toAdd;
		while((st = star_space(st)) != null) {
			toAdd = null;
			for(int i = 0; toAdd == null && i < parsers.length; i++) {
				toAdd = parsers[i].getElemnt(st);
			}
			if(toAdd == null)
				throw new Exception("unknown element "+ st.charAt(0));			
			st = toAdd.getRight();
			//System.out.println(st);
			elements.add(toAdd.getLeft());				
		}
		System.out.println(elements.toString());
		return false;		
	}
	
	public static void main(String[] args) throws Exception {
		//System.out.println(new BigDecimal(16).multiply(new BigDecimal("0.00000000000000000000000000000000000000000000000000000000000000000000003")));
		String st = "E - pI +  x ^ 3 +13.8 x ^ 2 + 42.2 x -9  ";
		Parser pr =new Parser();
		pr.solve(st);
	}
}
