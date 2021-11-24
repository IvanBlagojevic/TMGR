package org.gs4tr.termmanager.model.dto.converter;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.Language;

public class ProjectConverter {

    public static org.gs4tr.termmanager.model.dto.Project[] fromInternalToDto(List<TmProject> internalEntities) {
	return (fromInternalToDto(internalEntities, true));

    }

    public static org.gs4tr.termmanager.model.dto.Project[] fromInternalToDto(List<TmProject> internalEntities,
	    boolean convertOrganization) {
	if (isEmpty(internalEntities)) {
	    return null;
	}

	List<org.gs4tr.termmanager.model.dto.Project> projectsTemp = new ArrayList<org.gs4tr.termmanager.model.dto.Project>();
	for (TmProject tmProject : internalEntities) {
	    if (tmProject != null) {
		projectsTemp.add(fromInternalToDto(tmProject, convertOrganization));
	    }
	}

	if (projectsTemp.size() == 0) {
	    return null;
	} else {
	    return projectsTemp.toArray(new org.gs4tr.termmanager.model.dto.Project[projectsTemp.size()]);
	}

    }

    public static org.gs4tr.termmanager.model.dto.Project fromInternalToDto(TmProject internalEntity) {
	return fromInternalToDto(internalEntity, true);
    }

    public static org.gs4tr.termmanager.model.dto.Project fromInternalToDto(TmProject internalEntity,
	    boolean converOrganization) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Project dtoEntity = new org.gs4tr.termmanager.model.dto.Project();

	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getProjectId()));
	dtoEntity.setProjectInfo(ProjectInfoConverter.fromInternalToDto(internalEntity.getProjectInfo()));

	if (converOrganization) {
	    TmOrganization organization = internalEntity.getOrganization();
	    if (organization != null) {
		dtoEntity.setOrganizationName(organization.getOrganizationInfo().getName());
	    }
	}

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Project fromInternalToDtoWS(TmProject internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Project dtoEntity = new org.gs4tr.termmanager.model.dto.Project();

	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getProjectId()));
	dtoEntity.setProjectInfo(ProjectInfoConverter.fromInternalToDto(internalEntity.getProjectInfo()));

	TmOrganization organization = internalEntity.getOrganization();
	if (organization != null) {
	    dtoEntity.setOrganizationName(organization.getOrganizationInfo().getName());
	}

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Project fromInternalToDtoWS(TmProject internalEntity,
	    Set<String> languageSet) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Project dtoEntity = new org.gs4tr.termmanager.model.dto.Project();

	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getProjectId()));
	dtoEntity.setProjectInfo(ProjectInfoConverter.fromInternalToDto(internalEntity.getProjectInfo()));

	TmOrganization organization = internalEntity.getOrganization();
	if (organization != null) {
	    dtoEntity.setOrganizationName(organization.getOrganizationInfo().getName());
	}

	if (CollectionUtils.isNotEmpty(languageSet)) {
	    Language[] dtoLanguages = LanguageConverter.fromInternalToDto(languageSet);

	    dtoEntity.setLanguages(dtoLanguages);
	}

	return dtoEntity;
    }
}
