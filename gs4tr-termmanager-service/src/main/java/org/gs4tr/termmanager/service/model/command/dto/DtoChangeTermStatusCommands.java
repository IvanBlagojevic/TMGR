package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.service.model.command.ChangeTermStatusCommands;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;

public class DtoChangeTermStatusCommands extends BaseDtoTermActionCommand
	implements DtoTaskHandlerCommand<ChangeTermStatusCommands> {

    private DtoTermCommandPerProject[] _changeStatusPerProject;

    private String _termStatus;

    @Override
    public ChangeTermStatusCommands convertToInternalTaskHandlerCommand() {
	ChangeTermStatusCommands changeTermStatusCommands = new ChangeTermStatusCommands();

	DtoTermCommandPerProject[] deletePerProject = getChangeStatusPerProject();
	if (ArrayUtils.isNotEmpty(deletePerProject)) {
	    changeTermStatusCommands.setChangeStatusCommands(new ArrayList<>());
	    for (DtoTermCommandPerProject dtoPerProject : deletePerProject) {
		TermCommandPerProject deleteTermCommand = dtoPerProject.convertToInternalTaskHandlerCommand();
		changeTermStatusCommands.getChangeStatusCommands().add(deleteTermCommand);
	    }
	}

	changeTermStatusCommands.setIncludeSourceSynonyms(isIncludeSourceSynonyms());

	changeTermStatusCommands.setIncludeTargetSynonyms(isIncludeTargetSynonyms());

	changeTermStatusCommands.setSourceLanguage(getSourceLanguage());

	changeTermStatusCommands.setTermStatus(getTermStatus());

	List<String> targetLanuages = new ArrayList<>();
	if (ArrayUtils.isNotEmpty(getTargetLanguages())) {
	    for (int i = 0; i < getTargetLanguages().length; i++) {
		if (StringUtils.isNotEmpty(getTargetLanguages()[i])) {
		    targetLanuages.add(getTargetLanguages()[i]);
		}
	    }
	}
	changeTermStatusCommands.setTargetLanguages(targetLanuages);

	return changeTermStatusCommands;
    }

    public DtoTermCommandPerProject[] getChangeStatusPerProject() {
	return _changeStatusPerProject;
    }

    public String getTermStatus() {
	return _termStatus;
    }

    public void setChangeStatusPerProject(DtoTermCommandPerProject[] changeStatusPerProject) {
	_changeStatusPerProject = changeStatusPerProject;
    }

    public void setTermStatus(String termStatus) {
	_termStatus = termStatus;
    }
}
