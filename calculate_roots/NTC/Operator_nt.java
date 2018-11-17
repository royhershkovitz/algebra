package NTC;
import ELEMENTS.Var_name;

public class Operator_nt implements Non_terminal_combinator<String>{
	private static String operators = "*/+-^";

	@Override
	public boolean isExpr(String expr) {
		char ch = expr.charAt(0);
		if(operators.indexOf(ch) == -1)
			return false;
		return true;
	}

	@Override
	public Pair<String, String> getElemnt(String expr) {
		char ch = expr.charAt(0);
		if(operators.indexOf(ch) == -1)	return null;
		return new Pair(new Var_name(ch + ""), expr.substring(1));
	}

}
