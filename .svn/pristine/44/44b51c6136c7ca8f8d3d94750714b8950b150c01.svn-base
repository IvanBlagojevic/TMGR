package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.Language;

public class LanguageConverter {

    public static String fromDtoToLanguageCode(org.gs4tr.termmanager.model.dto.Language dtoEntity) {
	if (dtoEntity == null || StringUtils.isBlank(dtoEntity.getLocale())) {
	    return null;
	}

	return dtoEntity.getLocale();

    }

    public static List<String> fromDtoToLanguageCode(org.gs4tr.termmanager.model.dto.Language[] dtoEntities) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	List<String> internalEntities = new ArrayList<String>();
	for (int i = 0; i < dtoEntities.length; i++) {
	    internalEntities.add(fromDtoToLanguageCode(dtoEntities[i]));
	}

	return internalEntities;
    }

    public static org.gs4tr.termmanager.model.dto.Language[] fromInternalToDto(Collection<String> internalEntities) {
	if (internalEntities != null) {
	    List<org.gs4tr.termmanager.model.dto.Language> resultEntities = new ArrayList<org.gs4tr.termmanager.model.dto.Language>();
	    for (String language : internalEntities) {
		if (language != null) {
		    resultEntities.add(fromInternalToDto(language));
		}
	    }

	    return resultEntities.toArray(new org.gs4tr.termmanager.model.dto.Language[resultEntities.size()]);
	} else {
	    return null;
	}
    }

    public static org.gs4tr.termmanager.model.dto.Language fromInternalToDto(Language internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Language dtoEntity = new org.gs4tr.termmanager.model.dto.Language();

	dtoEntity.setLocale(internalEntity.getLanguageId());

	dtoEntity.setValue(internalEntity.getDisplayName());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Language fromInternalToDto(Locale internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Language dtoEntity = new org.gs4tr.termmanager.model.dto.Language();

	dtoEntity.setLocale(internalEntity.getCode());

	dtoEntity.setValue(internalEntity.getDisplayName());

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Language fromInternalToDto(String internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Language dtoEntity = new org.gs4tr.termmanager.model.dto.Language();

	Locale locale = Locale.get(internalEntity);

	dtoEntity.setLocale(internalEntity);

	dtoEntity.setValue(locale.getDisplayName());

	dtoEntity.setIsRTL(locale.isRTL());

	return dtoEntity;
    }
}
