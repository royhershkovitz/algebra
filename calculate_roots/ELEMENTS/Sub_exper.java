package ELEMENTS;

import java.util.List;

public class Sub_exper extends Element<List<Element>>{

	private static final String TAG = "S.E";
	
	public Sub_exper(List<Element> lst) {
		super.TAG = TAG;
		value = lst;
	}
}