package org.gs4tr.termmanager.webservice.converters;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.webservice.model.response.LanguageDto;

public class LanguageConverter {

    public static LanguageDto[] convertLanguagesToDto(Set<String> languages) {

	if (CollectionUtils.isEmpty(languages)) {
	    return new LanguageDto[0];
	}

	return languages.stream().map(LanguageConverter::toLanguageDto).toArray(size -> new LanguageDto[size]);
    }

    public static LanguageDto toLanguageDto(String language) {
	LanguageDto dtoLang = new LanguageDto();
	dtoLang.setLocale(language);
	dtoLang.setValue(Language.valueOf(language).getDisplayName());
	return dtoLang;
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private LanguageConverter() {
    }
}
