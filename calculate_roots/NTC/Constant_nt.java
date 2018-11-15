package NTC;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import ELEMENTS.Number;;

public class Constant_nt implements Non_terminal_combinator<BigDecimal>{
	
	private static final Map<String, BigDecimal> Constants ;
    static
    {
    	Constants = new HashMap<String, BigDecimal>();
    	Constants.put("E", new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696"
    			+ "7627724076630353547594571382178525166427427466391932003059921817413596629043572900334"
    			+ "2952605956307381323286279434907632338298807531952510190115738341879307021540891499348"
    			+ "8416750924476146066808226480016847741185374234544243710753907774499206955170276183860"
    			+ "626133138458300075204"));
    	Constants.put("PI", new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749"
    			+ "4459230781640628620899862803482534211706798214808651328230664709384460955058223172535"
    			+ "94081284811174502841027019385211055596446229489549303819644288109756659334461284756482"
    			+ "33786783165271201909145648566923460348610454326648213393607260249141273724587006606315"
    			+ "5881748815209209628"));
    }
    
	private int index = -1;
	
	@Override
	public int isExpr(String expr) {
		index = 0;
		boolean stop = false;
		char ch;
		while(!stop & index < expr.length()) {
			ch = expr.charAt(index);
			if((ch >= 'a' & ch <= 'z') || (ch >= 'A' & ch <= 'Z'))
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
			String con = expr.substring(0, index).toUpperCase();
			if(!Constants.containsKey(con)) return null;
			return new Pair(new Number(con, Constants.get(con)), expr.substring(index));
	}
}
