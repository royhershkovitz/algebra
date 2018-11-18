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
	private static int verbose;
	private static int presicion = 100;
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
	public static BigDecimal operations_parser(List<Element> lst, BigDecimal x)  {			
			Map<String,BigDecimal> val = new HashMap<>();
			val.put("x", x);
			return operations_parser(lst, val);
	}
	
	//naive solving with the values map
	public static BigDecimal operations_parser(List<Element> lst, Map<String,BigDecimal> values)  {
		
		MathOp pow = (BigDecimal a,BigDecimal b) -> new BigDecimal(Math.pow(a.doubleValue(), b.doubleValue()));
		MathOp mul = (BigDecimal a,BigDecimal b) -> a.multiply(b);
		MathOp div = (BigDecimal a,BigDecimal b) -> a.divide(b, presicion, BigDecimal.ROUND_HALF_EVEN);
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
		verbose = 1;;
		BigDecimal eps = new BigDecimal("1E-6");
		BigDecimal x0 = new BigDecimal("0");
		BigDecimal a = new BigDecimal("0");
		BigDecimal b = new BigDecimal("2");
		String st = "x ^ 3 +13.8* x ^ 2 + 42.2* x - 9 ";
		String st2 = "3x^2 +27.6x +42.2  ";
		newton_raphson(st, st2, x0, eps);
		halfing_method(st, a, b, eps);
		wrong_placement_method(st, a, b, eps);
		illinois_method(st, a, b, eps);
	}
	
	private static int loops;
	private static final String XnextErrorPrint = "%.20s %.20f%n";
	public static BigDecimal halfing_method(String st1, BigDecimal a, BigDecimal b, BigDecimal epsilon)  {
		List<Element> func = tag(st1).getLeft();
		if(verbose > 0)
			System.out.println("halfing method\nfunc " + st1 + "; error " + epsilon);
		loops = 0;
		BigDecimal source = halfing_method(func, a, b, 
				operations_parser(func, a), epsilon);
		if(verbose > 0)
			System.out.format("halfing method find source s = %.15s %nf(x)=%s%nin %d iterations%n%n",
						source, operations_parser(func, source), loops);
		return source;		
	}
	
	private static final BigDecimal BDTwo = new BigDecimal("2");
	public static BigDecimal halfing_method(List<Element> func, BigDecimal a, BigDecimal b, BigDecimal fa, BigDecimal epsilon)  {		
		loops++;
		BigDecimal alpha = a.add(b).divide(BDTwo, presicion, BigDecimal.ROUND_HALF_EVEN);		
		BigDecimal falpha =  operations_parser(func, alpha);
		
		if(falpha.compareTo(BigDecimal.ZERO) == 0) return alpha;
		BigDecimal error = (b.subtract(alpha).abs());
		if(verbose > 0)
			System.out.format(XnextErrorPrint, alpha, error);
		if(error.compareTo(epsilon) < 0) return alpha;
		
		boolean comp = false;
		if(fa.compareTo(BigDecimal.ZERO) > 0)
			{if(falpha.compareTo(BigDecimal.ZERO) < 0) comp = true;}
		else if (falpha.compareTo(BigDecimal.ZERO) > 0) comp = true;
		
		if(comp)	return halfing_method(func, a, alpha, fa, epsilon);	
		return	halfing_method(func, alpha, b, falpha, epsilon);	
	}
	
	
	public static BigDecimal wrong_placement_method(String st1, BigDecimal a, BigDecimal b, BigDecimal epsilon)  {
		List<Element> func = tag(st1).getLeft();
		if(verbose > 0)
			System.out.println("wrong placement method\nfunc " + st1 + "; error " + epsilon);
		loops = 0;
		BigDecimal source = wrong_placement_method(func, a, b, operations_parser(func, a),
				operations_parser(func, b), epsilon);
		if(verbose > 0)
			System.out.format("wrong placement method find source s = %.15s %nf(x)=%s%nin %d iterations%n%n",
					source, operations_parser(func, source), loops);
		return source;		
	}
	
	//we can reach a value which
	public static BigDecimal wrong_placement_method(List<Element> func, 
			BigDecimal a, BigDecimal b, BigDecimal fa, BigDecimal fb, BigDecimal epsilon)  {		
		loops++;
		BigDecimal alpha = a.multiply(fb).subtract(b.multiply(fa)).divide(fb.subtract(fa), presicion, BigDecimal.ROUND_HALF_EVEN);	
		BigDecimal falpha =  operations_parser(func, alpha);
		
		if(falpha.compareTo(BigDecimal.ZERO) == 0) return alpha;
		BigDecimal error = (b.subtract(a).abs());
		if(verbose > 0)
			System.out.format(XnextErrorPrint, alpha, error);
		//System.out.format(XnextErrorPrint, a, b);
		//System.out.format(XnextErrorPrint, fa, fb);
		//if(falpha.abs().compareTo(epsilon) < 0) return alpha;// |f(alpha) - f(s)| = |f(alpha)| < tol
		if(error.compareTo(epsilon) < 0) return alpha;//problematic divide precision blocks it from running till very small area
		
		boolean comp = false;
		if(fa.compareTo(BigDecimal.ZERO) > 0)
			{if(falpha.compareTo(BigDecimal.ZERO) < 0) comp = true;}
		else if (falpha.compareTo(BigDecimal.ZERO) > 0) comp = true;
		
		if(comp)	return wrong_placement_method(func, a, alpha, fa, falpha, epsilon);		
		return	wrong_placement_method(func, alpha, b, falpha, fb, epsilon);	
	}
	
	public static BigDecimal illinois_method(String st1, BigDecimal a, BigDecimal b, BigDecimal epsilon)  {
		List<Element> func = tag(st1).getLeft();
		if(verbose > 0)
			System.out.println("illanois method\nfunc " + st1 + "; error " + epsilon);
		loops = 0;
		BigDecimal source = illinois_method(func, a, b, operations_parser(func, a),
				operations_parser(func, b), epsilon);
		if(verbose > 0)
			System.out.format("illanois method find source s = %.15s %nf(x)=%s%nin %d iterations%n%n",
					source, operations_parser(func, source), loops);
		return source;		
	}
	
	private static final BigDecimal BDHalf = new BigDecimal("0.5");
	private static boolean lasta = false;
	private static boolean lastb = false;
	private static boolean next = false;
	//we can reach a value which
	public static BigDecimal illinois_method(List<Element> func, 
			BigDecimal a, BigDecimal b, BigDecimal fa, BigDecimal fb, BigDecimal epsilon)  {		
		loops++;
		BigDecimal gamma = BigDecimal.ONE;
		if(next) {
			if(lasta) gamma = BDTwo;
			else gamma = BDHalf;		
		}
		BigDecimal alpha = gamma.multiply(a.multiply(fb)).subtract(b.multiply(fa)).divide(gamma.multiply(fb).subtract(fa), presicion, BigDecimal.ROUND_HALF_EVEN);	
		BigDecimal falpha =  operations_parser(func, alpha);
		
		if(falpha.compareTo(BigDecimal.ZERO) == 0) return alpha;
		BigDecimal error = (b.subtract(a).abs());
		if(verbose > 0)
			System.out.format(XnextErrorPrint, alpha, error);
		//System.out.format(XnextErrorPrint, a, b);
		//System.out.format(XnextErrorPrint, fa, fb);
		//if(falpha.abs().compareTo(epsilon) < 0) return alpha;// |f(alpha) - f(s)| = |f(alpha)| < tol
		if(error.compareTo(epsilon) < 0) return alpha;//problematic divide precision blocks it from running till very small area
		
		boolean comp = false;
		if(fa.compareTo(BigDecimal.ZERO) > 0)
			{if(falpha.compareTo(BigDecimal.ZERO) < 0) comp = true;}
		else if (falpha.compareTo(BigDecimal.ZERO) > 0) comp = true;
		
		if(comp)	{
			next = lastb;
			lasta = false;
			lastb = true;
			return illinois_method(func, a, alpha, fa, falpha, epsilon);		
		}
		next = lasta;
		lasta = true;
		lastb = false;
		return	illinois_method(func, alpha, b, falpha, fb, epsilon);	
	}
	
	
	public static BigDecimal newton_raphson(String st1, String st2, BigDecimal x0, BigDecimal epsilon)  {
		List<Element> func = tag(st1).getLeft();
		List<Element> derivation = tag(st2).getLeft();
		Map<String, BigDecimal> values = new HashMap<String, BigDecimal>();
		values.put("x", x0);		
		if(verbose > 0)
			System.out.println("newton raphson\nfunc " + st1 + "\nderivation " + st2 + "\nx0 " + x0 + "; error " + epsilon);
		loops = 0;
		BigDecimal source = newton_raphson(func, derivation, epsilon, values);
		if(verbose > 0)
			System.out.format("newton raphson find source s = %.15s %nf(x)=%s%nin %d iterations%n%n",
					source, operations_parser(func, source), loops);
		return source;		
	}
	
	public static BigDecimal newton_raphson(List<Element> func, List<Element> derivation, BigDecimal epsilon, Map<String, BigDecimal> values)  {		
		loops++;
		BigDecimal xn =  values.get("x");
		BigDecimal xnext =  xn.subtract(operations_parser(func, values).divide(operations_parser(derivation, values), 100, BigDecimal.ROUND_HALF_EVEN));	
		BigDecimal error = (xnext.subtract(xn).abs());
		if(verbose > 0)
			System.out.format(XnextErrorPrint, xnext, error);
		if(error.compareTo(epsilon) > 0) {
			values.replace("x", xn, xnext);			
			return newton_raphson(func, derivation, epsilon, values);
		}			
		return xnext;		
	}
}
