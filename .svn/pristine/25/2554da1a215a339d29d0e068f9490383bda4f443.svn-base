package org.gs4tr.termmanager.service.utils;

import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Term;

public class UpdateCommandUtils {

    public static UpdateCommand createRemoveCommandFromTerm(Term term) {
	return createUpdateCommandFromTerm(term, CommandEnum.REMOVE);
    }

    public static UpdateCommand createUpdateCommandFromTerm(Term term) {
	return createUpdateCommandFromTerm(term, CommandEnum.UPDATE);
    }

    public static UpdateCommand createUpdateCommandFromTerm(Term term, CommandEnum commandEnum) {
	UpdateCommand updateCommand = new UpdateCommand();
	updateCommand.setCommand(commandEnum.getName());
	updateCommand.setItemType(TypeEnum.TERM.getName());
	updateCommand.setLanguageId(term.getLanguageId());
	updateCommand.setParentMarkerId(term.getTermEntryId());
	updateCommand.setMarkerId(term.getUuId());
	updateCommand.setProjectId(term.getProjectId());
	return updateCommand;
    }
}
