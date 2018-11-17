package ELEMENTS;
public class Var_name extends Element<String>{

	private static final String TAG = "VAR";
	private final boolean symSign;//true pos, false neg
	
	public Var_name(String string) {
		super.TAG = TAG;
		symSign = false;
		value = string;
	}
	
	public Var_name(String string, boolean sign) {
		super.TAG = TAG;
		symSign = sign;
		value = string;
	}
	
	public boolean getSign() {
		return symSign;
	}
}
