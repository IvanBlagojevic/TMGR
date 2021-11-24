package org.gs4tr.termmanager.service.xls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gs4tr.foundation.modules.entities.model.StringConstants;

public class XlsReaderHelper {

    /* carriage return CR */
    private static final String CR = "\r";

    /* form feed FF */
    private static final String FF = "\f";

    /* horizontal tab HT */
    private static final String HT = "\t";

    /* line break LB */
    private static final String LB_U2028 = "\u2028";

    /* line break LB */
    private static final String LB_U2029 = "\u2029";

    /* multiple space */
    private static final String MULTIPLE_SPACE = "\\s+";

    /* new line NL */
    private static final String NL = "\n";

    private static final Pattern SPECIAL_CHARS_REGEX = Pattern
	    .compile("[" + CR + FF + HT + NL + LB_U2028 + LB_U2029 + "]");

    public static String removeMultipleWhiteSpace(String cellValue) {
	return cellValue.replaceAll(MULTIPLE_SPACE, StringConstants.SPACE);
    }

    public static String replaceBrakingLineCharacters(String cellValue) {
	Matcher matcher = SPECIAL_CHARS_REGEX.matcher(cellValue);

	if (matcher.find()) {
	    cellValue = matcher.replaceAll(StringConstants.SPACE);
	}

	return cellValue;
    }
}
