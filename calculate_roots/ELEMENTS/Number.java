package ELEMENTS;
import java.math.BigDecimal;

public class Number extends Element<BigDecimal>{

	private static final String TAG = "NUM";
	private static final String TAG2 = "CON";
	private String constant_name = null;
	
	public Number(String st) {
		super.TAG = TAG;
		value = new BigDecimal(st);
	}
	
	public Number(BigDecimal bd) {
		super.TAG = TAG;
		value = bd;
	}	
	
	public Number(String st, BigDecimal bd) {
		super.TAG = TAG2;
		constant_name = st;
		value = bd;
	}
	
	@Override
	public String toString() {
		if(constant_name == null) super.toString();
		return "<" + TAG2 + " " + constant_name + ">";
	}
}
