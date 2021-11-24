package org.gs4tr.termmanager.service.manualtask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.model.command.ProjectShortCodeCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoProjectShortCodeCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateProjectShortCodeTaskHandler extends AbstractManualTaskHandler {

    public static final char ID_PADDING_CHAR = '0';
    public static final int ID_PADDING_SIZE = 6;
    private static final String PROJECT_SHORT_CODE_KEY = "projectShortCode";

    @Autowired
    ProjectService _projectService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoProjectShortCodeCommand.class;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	ProjectShortCodeCommand projectShortCodeCommand = (ProjectShortCodeCommand) command;

	String projectName = removeSpaces(projectShortCodeCommand.getProjectName());
	try {
	    projectName = URLEncoder.encode(projectName, StandardCharsets.UTF_8.name());
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

	StringBuilder projectShortCode = new StringBuilder(projectName);

	long numberOfProjects = getProjectService().countProjects() + 1;
	projectShortCode.append(StringUtils.leftPad(Long.toString(numberOfProjects), ID_PADDING_SIZE, ID_PADDING_CHAR));

	TaskModel model = new TaskModel();
	model.addObject(PROJECT_SHORT_CODE_KEY, projectShortCode.toString());
	return new TaskModel[] { model };

    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }

    public String removeSpaces(String s) {
	StringTokenizer st = new StringTokenizer(s, " ", false);
	StringBuilder t = new StringBuilder();
	while (st.hasMoreElements()) {
	    t.append(st.nextElement());
	}
	if (t.length() >= 3) {
	    return t.substring(0, 3).toUpperCase();
	} else {
	    return t.toString().toUpperCase();
	}
    }

}
