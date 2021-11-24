package org.gs4tr.termmanager.service.notification.listeners;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.StringConstants;

public class NotificationListenerHelper {

    public static String buildTargetLanguagesString(Set<String> targetLanguages) {
	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    int size = targetLanguages.size();
	    int counter = 1;
	    StringBuilder builder = new StringBuilder();
	    for (String target : targetLanguages) {
		String displayName = Locale.get(target).getDisplayName();

		builder.append(displayName);
		if (counter != size) {
		    builder.append(StringConstants.COMMA);
		    builder.append(StringConstants.EMPTY);
		}
		counter++;
	    }
	    return builder.toString();
	} else {
	    return StringConstants.EMPTY;
	}
    }
}
