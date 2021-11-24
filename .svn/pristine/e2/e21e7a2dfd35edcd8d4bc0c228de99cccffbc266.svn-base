package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

public class GetProjectUserLanguagesTaskHandler extends AbstractManualTaskHandler {

    private static final String LANGUAGE_DISPLAY_NAME_KEY = "languageDisplayName";

    private static final String LANGUAGE_NAME_KEY = "languageLocale";

    private static final String LANGUAGES_KEY = "languages";

    @Autowired
    public ProjectService _projectService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	Long projectId = parentIds[0];

	Set<String> projectUserLanguages = TmUserProfile.getCurrentUserProfile().getProjectUserLanguages()
		.get(projectId);

	TaskModel taskModel = new TaskModel();

	List<Map<String, String>> languagesData = new ArrayList<Map<String, String>>();

	Language[] dtoLanguages = LanguageConverter.fromInternalToDto(projectUserLanguages);

	if (ArrayUtils.isNotEmpty(dtoLanguages)) {
	    for (Language projectUserLanguage : dtoLanguages) {

		Map<String, String> data = new LinkedHashMap<String, String>();

		data.put(LANGUAGE_NAME_KEY, projectUserLanguage.getLocale());

		data.put(LANGUAGE_DISPLAY_NAME_KEY, projectUserLanguage.getValue());

		languagesData.add(data);
	    }

	    Collections.sort(languagesData, new Comparator<Map<String, String>>() {
		@Override
		public int compare(Map<String, String> o1, Map<String, String> o2) {
		    return o1.get(LANGUAGE_DISPLAY_NAME_KEY).compareTo(o2.get(LANGUAGE_DISPLAY_NAME_KEY));
		}
	    });
	}

	taskModel.addObject(LANGUAGES_KEY, languagesData);

	return new TaskModel[] { taskModel };

    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }
}
