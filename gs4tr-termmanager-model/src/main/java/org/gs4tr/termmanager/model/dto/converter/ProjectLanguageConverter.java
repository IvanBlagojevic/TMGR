package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.ProjectLanguage;

public class ProjectLanguageConverter {
    public static ProjectLanguage fromDtoToInternal(org.gs4tr.termmanager.model.dto.ProjectLanguage dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	ProjectLanguage internalEntity = new ProjectLanguage();
	internalEntity.setLanguage(dtoEntity.getLanguage().getLocale());

	return internalEntity;
    }

    public static List<ProjectLanguage> fromDtoToInternal(
	    org.gs4tr.termmanager.model.dto.ProjectLanguage[] dtoEntities) {
	if (dtoEntities == null) {
	    return null;
	}

	List<ProjectLanguage> internalEntities = new ArrayList<ProjectLanguage>();
	for (org.gs4tr.termmanager.model.dto.ProjectLanguage projectLanguage : dtoEntities) {
	    if (projectLanguage != null) {
		internalEntities.add(fromDtoToInternal(projectLanguage));
	    }
	}

	return internalEntities;
    }
}
