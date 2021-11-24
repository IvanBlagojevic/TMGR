package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.service.model.command.DeleteTermCommands;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;

public class DtoDeleteTermCommands extends BaseDtoTermActionCommand
	implements DtoTaskHandlerCommand<DeleteTermCommands> {

    private DtoTermCommandPerProject[] _deletePerProject;

    private boolean _deleteTermEntries = false;

    @Override
    public DeleteTermCommands convertToInternalTaskHandlerCommand() {

	DeleteTermCommands deleteTermCommands = new DeleteTermCommands();

	DtoTermCommandPerProject[] deletePerProject = getDeletePerProject();
	if (ArrayUtils.isNotEmpty(deletePerProject)) {
	    deleteTermCommands.setDeleteCommands(new ArrayList<>());
	    for (DtoTermCommandPerProject dtoPerProject : deletePerProject) {
		TermCommandPerProject deleteTermCommand = dtoPerProject.convertToInternalTaskHandlerCommand();
		deleteTermCommands.getDeleteCommands().add(deleteTermCommand);
	    }
	}

	deleteTermCommands.setIncludeSourceSynonyms(isIncludeSourceSynonyms());

	deleteTermCommands.setIncludeTargetSynonyms(isIncludeTargetSynonyms());

	deleteTermCommands.setDeleteTermEntries(isDeleteTermEntries());

	deleteTermCommands.setSourceLanguage(getSourceLanguage());

	List<String> targetLanuages = new ArrayList<>();
	if (ArrayUtils.isNotEmpty(getTargetLanguages())) {
	    for (int i = 0; i < getTargetLanguages().length; i++) {
		if (StringUtils.isNotEmpty(getTargetLanguages()[i])) {
		    targetLanuages.add(getTargetLanguages()[i]);
		}
	    }
	}
	deleteTermCommands.setTargetLanguages(targetLanuages);

	return deleteTermCommands;
    }

    public DtoTermCommandPerProject[] getDeletePerProject() {
	return _deletePerProject;
    }

    public boolean isDeleteTermEntries() {
	return _deleteTermEntries;
    }

    public void setDeletePerProject(DtoTermCommandPerProject[] deletePerProject) {
	_deletePerProject = deletePerProject;
    }

    public void setDeleteTermEntries(boolean deleteTermEntries) {
	_deleteTermEntries = deleteTermEntries;
    }

}
