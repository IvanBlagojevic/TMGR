package org.gs4tr.termmanager.service.xls;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;

public class XlsHelper {

    public static final String COLON = StringConstants.COLON;

    public static final String DELIMITER = "<>";

    public static final String NOTE = ":Note:";

    public static final String STATUS = ":Status";

    public static final String TERM_ENTRY_ID = "TermEntryID";

    public static void addToHeader(List<String> header, String languageId, String delimiter, List<String> types) {
	if (CollectionUtils.isEmpty(types)) {
	    return;
	}

	for (String type : types) {
	    header.add(joinStrings(languageId, delimiter, type));
	}
    }

    public static String joinLanguageAndStatus(String languageId) {
	return languageId.concat(STATUS);
    }

    public static String joinStrings(String prefix, String delimiter, String suffix) {
	return new StringBuilder().append(prefix).append(delimiter).append(suffix).toString();
    }
}
