package org.gs4tr.termmanager.webservice.converters;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.webservice.model.response.ProjectDto;

public class ProjectConverter {

    public static ProjectDto[] convertProjectsToDto(List<TmProject> projects,
	    Map<Long, Set<String>> projectUserLanguages) {
	return projects.stream().map(project -> toProjectDto(project, projectUserLanguages))
		.toArray(length -> new ProjectDto[length]);
    }

    public static ProjectDto toProjectDto(TmProject project, Map<Long, Set<String>> projectUserLanguages) {

	final ProjectDto dtoProject = new ProjectDto();

	ProjectInfo projectInfo = project.getProjectInfo();

	dtoProject.setProjectInfo(ProjectInfoConverter.convertProjectInfoToDto(projectInfo));

	TmOrganization organization = project.getOrganization();
	if (organization != null) {
	    dtoProject.setOrganizationName(organization.getOrganizationInfo().getName());
	}

	final Long projectId = project.getProjectId();

	dtoProject.setProjectTicket(TicketConverter.fromInternalToDto(projectId));

	if (projectUserLanguages != null) {
	    Set<String> languageIds = projectUserLanguages.get(projectId);

	    dtoProject.setLanguages(LanguageConverter.convertLanguagesToDto(languageIds));
	}

	return dtoProject;
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private ProjectConverter() {
    }
}
