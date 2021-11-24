package org.gs4tr.termmanager.glossaryV2.logging.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;

public class LanguageInfoEventDataBuilder {

    private TmgrKey _tmgrKey;

    public LanguageInfoEventDataBuilder(TmgrKey tmgrKey) {
	_tmgrKey = tmgrKey;
    }

    public Map<String, Object> build() {

	Map<String, Object> languageInfo = new LinkedHashMap<String, Object>();

	TmgrKey tmgrKey = getTmgrKey();
	if (tmgrKey != null) {

	    languageInfo.put(EventContextConstants.SOURCE_LANGUAGE, tmgrKey.getSource());
	    languageInfo.put(EventContextConstants.TARGET_LANGUAGE, tmgrKey.getTarget());
	}

	return languageInfo;
    }

    private TmgrKey getTmgrKey() {
	return _tmgrKey;
    }

}