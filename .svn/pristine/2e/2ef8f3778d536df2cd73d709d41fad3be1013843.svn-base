package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBlacklistUpdater implements BlacklistUpdater {

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public abstract BatchProcessResult update(BlacklistUpdateRequestExt request);

    protected BatchProcessResult addInternal(BlacklistUpdateRequestExt request, int size) {
	List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
	for (BlacklistTerm term : request) {
	    String termMarkerId = UUID.randomUUID().toString();
	    tus.addAll(createTranslationUnits(CommandEnum.ADD, termMarkerId, term));
	}

	String languageId = request.getTargetLanguageId();
	getTermEntryService().updateTermEntries(tus, languageId, request.getProjectId(), Action.ADDED_REMOTELY);

	return new BatchProcessResult(size, tus.size(), null);
    }

    protected UpdateCommand createCommand(CommandEnum cmd, TypeEnum type, String subType, String parentMarkerId,
	    String markerId, String languageId, String value, String status) {
	UpdateCommand command = new UpdateCommand();
	command.setCommand(cmd.getName());
	command.setItemType(type.getName());
	command.setLanguageId(languageId);
	command.setMarkerId(markerId);
	command.setParentMarkerId(parentMarkerId);
	command.setValue(value);
	command.setSubType(subType);
	command.setStatus(status);
	return command;
    }

    protected List<TranslationUnit> createTranslationUnits(CommandEnum cmd, String termMarkerId, BlacklistTerm term) {
	String termEntryId = term.getId();

	TranslationUnit tu = new TranslationUnit();
	tu.setTermEntryId(termEntryId);

	// create update commands
	String localeCode = term.getLocale().getCode();
	UpdateCommand command = createCommand(cmd, TypeEnum.TERM, null, termEntryId, termMarkerId, localeCode,
		term.getText(), ItemStatusTypeHolder.BLACKLISTED.getName());
	tu.addSourceTermUpdateCommand(command);

	List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
	tus.add(tu);

	return tus;
    }

    protected BlacklistTerm findTerm(List<BlacklistTerm> terms, TermEntry termEntry) {
	String termEntryId = termEntry.getUuId();
	for (BlacklistTerm term : terms) {
	    if (termEntryId.equals(term.getId())) {
		return term;
	    }
	}
	return null;
    }

    protected TermEntry findTermEntry(List<TermEntry> termEntries, BlacklistTerm term) {
	String termEntryId = term.getId();
	for (TermEntry termEntry : termEntries) {
	    if (termEntryId.equals(termEntry.getUuId())) {
		return termEntry;
	    }
	}
	return null;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}
