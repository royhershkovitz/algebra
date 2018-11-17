package parser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ELEMENTS.*;
import ELEMENTS.Number;
import NTC.*;

public class Parser {
	private static final int verbose = 1;
	private static final Non_terminal_combinator[] parsers = {new parn_nt(), new Number_nt(), new Constant_nt(), new Symbol_nt(), new Operator_nt()};
	//will trim the spaces, return null if string is empty
	private static String star_space(String st) {
		int i = 0;
		while(i < st.length() && st.charAt(i) == ' ')
			i++;
		st = st.substring(i);
		if(st.isEmpty()) return null;
		return st;
	}
		
	//will return true if solved the equation
	public static boolean solve(String st)  {
		Pair<List<Element>,String> sol = tag(st);
		int len = st.length();
		st = sol.getRight();
		if(st != null && !st.isEmpty()) {
			System.err.println("unknown element starting at (" +  
				(len - st.length()) + ") '" + st.charAt(0) + "'");
			return false;
		}
		
		//System.out.println(sol.getLeft().toString());	
		Map<String, BigDecimal> values = new HashMap<String, BigDecimal>();
		values.put("x", new BigDecimal("2"));
				
		System.out.println(operations_parser(sol.getLeft(), values));
	    
		return false;		
	}
	
	//will return list of tagged expressions
	public static Pair<List<Element>,String> tag(String st) {
		List<Element> elements = new LinkedList<Element>();
		Pair<Element,String> toAdd;
		boolean run = true;
		while(run & (st = star_space(st)) != null) {
			toAdd = null;
			for(int i = 0; toAdd == null && i < parsers.length; i++) {
				toAdd = parsers[i].getElemnt(st);
			}
			if(toAdd != null) {
				st = toAdd.getRight();
				elements.add(toAdd.getLeft());
			}
			else run = false;
		}
		if(verbose > 1)			System.out.println(elements.toString());
		return new Pair<List<Element>, String>(elements, st);		
	}
	
	//naive solving with the values map
	public static BigDecimal operations_parser(List<Element> lst, Map<String,BigDecimal> values)  {
		
		MathOp pow = (BigDecimal a,BigDecimal b) -> new BigDecimal(Math.pow(a.doubleValue(), b.doubleValue()));
		MathOp mul = (BigDecimal a,BigDecimal b) -> a.multiply(b);
		MathOp div = (BigDecimal a,BigDecimal b) -> a.divide(b, 50, BigDecimal.ROUND_HALF_EVEN);
		MathOp add = (BigDecimal a,BigDecimal b) -> a.add(b);
		MathOp dif = (BigDecimal a,BigDecimal b) -> a.subtract(b);
		lst = placing(lst, values);
		if(verbose > 1)			System.out.println(lst);
		lst = eval_op(new String[] {"^"}, new MathOp[] {pow}, lst);
		if(verbose > 1)			System.out.println(lst);
		lst = eval_op(new String[] {"*", "/"}, new MathOp[] {mul,div}, lst);
		if(verbose > 1)			System.out.println(lst);
		lst = eval_op(new String[] {"+", "-"}, new MathOp[] {add,dif}, lst);	
		if(verbose > 1)			System.out.println(lst);
		if(lst.size() != 1) {
			System.err.println("failed to parse - you write args wrong, or break math rules? (" +  
					lst + ")");
			return null;
		}
		return (BigDecimal) lst.get(0).getValue();
	}
	
	private static List<Element> placing(List<Element> lst, Map<String, BigDecimal> values)  {
		Iterator<Element> iter = lst.iterator();
		BigDecimal bg;
		Element ele = null;
		Var_name var_n = null;
		List<Element> new_lst = new LinkedList<>();
		boolean isLastNum = false;
		while(iter.hasNext())			
		{
			ele = iter.next();
			if(ele.getTag() == "S.E") new_lst.add(new Number(operations_parser((List<Element>) ele.getValue(), values)));
			else if(ele.getTag() == "VAR") {
				var_n = (Var_name) ele;				
				bg = values.get(var_n.getValue());
				if(bg != null) {
					if(!var_n.getSign())
						bg = bg.multiply(new BigDecimal(-1));
					if(isLastNum)
						new_lst.add(new Var_name("*"));
					new_lst.add(new Number(bg));
				}
				else new_lst.add(ele);
			}
			else {
				new_lst.add(ele);				
			}
			isLastNum = ele.getTag() == "NUM";
		}
		return new_lst;
	}
	
	private static List<Element> eval_op(String[] op, MathOp[] lamb, List<Element> lst) {
		Iterator<Element> iter = lst.iterator();
		BigDecimal a, b;
		Element last_ele = null;
		Element ele = null;
		Element ele2;
		List<Element> new_lst = new LinkedList<>();
		boolean findOp = false;
		while(iter.hasNext())			
		{
			findOp = false;
			ele = iter.next();
			if(ele.getTag() == "VAR") {
				int i = 0;
				while(i < op.length && !((String) ele.getValue()).equals(op[i])) i++;
				if(i < op.length) {
					findOp = true;
					ele2 = iter.next();
						
					a = (BigDecimal) last_ele.getValue();
					b = (BigDecimal) ele2.getValue();
					
					if(verbose > 2)
						System.out.println(a + " " + op[i] + " " + b);
					last_ele = new Number(lamb[i].mathOp(a,b));
				}
			}
			if(!findOp) {
				if(last_ele != null)
					new_lst.add(last_ele);
				last_ele = ele;								
			}			
		}
		new_lst.add(last_ele);// if list is empty it will add nulls to the list
		return new_lst;
	}
	
	public static void main(String[] args)  {
		//System.out.println(new BigDecimal(16).multiply(new BigDecimal("0.00000000000000000000000000000000000000000000000000000000000000000000003")));
		//String st = "-1 + -y + -E - pI +  x ^ 3 +13.8 x ^ 2 + 42.2 x - 9  ";
		//String st = "(E - pI )+  x ^ 3 +13.8 x ^ 2 + 42.2 x - 9  ";
		//String st = "-E - pI +  -x ^ 3 +13.8 x ^ 2 + 42.2 x - 9  ";
		//String st = "x ^ 3 +13.8 x ^ 2 + 42.2 x - 9  ";
		//String st = "2 * (2 + 2) *(3+4)* 2";
		//String st = "(2 + 2) *(3+4)* 2";
		//String st = "2 *(3+4)* 2";
		//solve(st);
		//String st = "x ^ 3 +13.8* x ^ 2 + 42.2* x - 9  ";
		BigDecimal eps = new BigDecimal("1E-70");
		BigDecimal x0 = new BigDecimal("2");
		String st = "x ^ 3 +13.8* x ^ 2 + 42.2* x - 9 ";
		String st2 = "3x^2 +27.6x +42.2  ";
		newton_raphson(st, st2, x0, eps);
	}
	
	public static BigDecimal newton_raphson(String st1, String st2, BigDecimal x0, BigDecimal epsilon)  {
		List<Element> func = tag(st1).getLeft();
		List<Element> derivation = tag(st2).getLeft();
		Map<String, BigDecimal> values = new HashMap<String, BigDecimal>();
		values.put("x", x0);		
		if(verbose > 0)
			System.out.println("newton raphson\nfunc " + st1 + "\nderivation " + st2 + "\nx0 " + x0 + "; error " + epsilon);
		return newton_raphson(func, derivation, epsilon, values);		
	}
	
	public static BigDecimal newton_raphson(List<Element> func, List<Element> derivation, BigDecimal epsilon, Map<String, BigDecimal> values)  {		
		BigDecimal xn =  values.get("x");
		BigDecimal xnext =  xn.subtract(operations_parser(func, values).divide(operations_parser(derivation, values), 100, BigDecimal.ROUND_HALF_EVEN));	
		BigDecimal error = (xnext.subtract(xn).abs());
		if(verbose > 0)
			System.out.println(xnext + " " + error);
		if(error.compareTo(epsilon) > 0) {
			values.replace("x", xn, xnext);			
			return newton_raphson(func, derivation, epsilon, values);
		}			
		return xnext;		
	}
}
