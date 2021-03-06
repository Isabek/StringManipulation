package osmedile.intellij.stringmanip.escaping;

import org.apache.commons.text.StringEscapeUtils;
import osmedile.intellij.stringmanip.AbstractStringManipAction;

import java.util.Map;

/**
 * @author Olivier Smedile
 * @version $Id: EscapeHtmlAction.java 16 2008-03-20 19:21:43Z osmedile $
 */
public class EscapeXMLAction extends AbstractStringManipAction<Object> {

	@Override
	public String transformByLine(Map<String, Object> actionContext, String s) {
		return StringEscapeUtils.escapeXml11(s);
    }
}