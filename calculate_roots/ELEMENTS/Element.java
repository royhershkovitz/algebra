package ELEMENTS;

import java.math.BigDecimal;

public class Element<T> {
	
	protected String TAG = "ELE";
	protected T value;

	public String getTag() {
		return TAG;
	}

	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "<" + TAG + " " + value + ">";
	}
}
