package com.github.krasa.stringmanipulation.intellij.styles;

import com.github.krasa.stringmanipulation.commons.style.Style;

public class ToCamelCaseAction extends AbstractCaseConvertingAction {
	public ToCamelCaseAction() {
	}

	public ToCamelCaseAction(boolean setupHandler) {
		super(setupHandler);
	}

	@Override
	public String transformByLine(String s) {
		Style from = Style.from(s);
		if (from != Style.CAMEL_CASE) {
			return Style.CAMEL_CASE.transform(from, s);
		}
		return s;
	}
}
