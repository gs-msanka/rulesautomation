/**
 * 
 */
package com.gainsight.bigdata.rulesengine.pojo.enums;

/**
 * @author Abhilash Thaduka
 *
 */
public enum RedShiftFormulaType {

	FORMULA1("(A+B)*C");

	private final String formula;

	private RedShiftFormulaType(final String formula) {
		this.formula = formula;
	}
}
