package org.gs4tr.termmanager.service.project.terminology.counts;

import java.util.List;

import org.gs4tr.termmanager.model.ProjectTerminologyCounts;

public interface ProjectTerminologyCountsProvider {

    ProjectTerminologyCounts getProjectTerminologyCounts(Long projectId, List<String> projectLanguageCodes);
}
