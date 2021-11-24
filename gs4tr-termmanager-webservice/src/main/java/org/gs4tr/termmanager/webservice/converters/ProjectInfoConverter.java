package org.gs4tr.termmanager.webservice.converters;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.termmanager.webservice.model.response.ProjectInfoDto;

public class ProjectInfoConverter {

    public static ProjectInfoDto convertProjectInfoToDto(ProjectInfo projectInfo) {
	ProjectInfoDto dtoProjectInfo = new ProjectInfoDto();
	dtoProjectInfo.setName(projectInfo.getName());
	dtoProjectInfo.setShortCode(projectInfo.getShortCode());
	dtoProjectInfo.setEnabled(projectInfo.isEnabled());

	return dtoProjectInfo;
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private ProjectInfoConverter() {
    }
}
