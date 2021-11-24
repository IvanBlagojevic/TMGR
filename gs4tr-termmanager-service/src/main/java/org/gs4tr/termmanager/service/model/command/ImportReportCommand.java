package org.gs4tr.termmanager.service.model.command;

import java.util.Set;

public class ImportReportCommand {

    private Set<String> _importThreadNames;

    private Long _projectId;

    public Set<String> getImportThreadNames() {
	return _importThreadNames;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public void setImportThreadNames(Set<String> importThreadNames) {
	_importThreadNames = importThreadNames;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }
}
