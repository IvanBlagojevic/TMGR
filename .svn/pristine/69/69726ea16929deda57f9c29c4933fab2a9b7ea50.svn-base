package org.gs4tr.termmanager.service.manualtask;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.MergeCommands;
import org.gs4tr.termmanager.service.model.command.MultipleTermEntriesMergeCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoMergeCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.termentry.synchronization.EqualsBuilderHelper;
import org.gs4tr.termmanager.service.utils.UpdateCommandUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class MergeManualTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoMergeCommands.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	MergeCommands commands = (MergeCommands) command;

	try {
	    mergeSelectedTermEntries(commands);
	} catch (Exception e) {
	    throw new UserException(e.getMessage(), e);
	}

	return new TaskResponse(null);
    }

    private TermEntry findOldestTermEntry(List<TermEntry> termEntries) {
	return termEntries.stream().min(Comparator.comparingLong(TermEntry::getDateCreated)).get();

    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private TranslationUnit getTranslationUnitsForHighestTermsStatuses(TermEntry existing, TermEntry incoming,
	    String sourceLangId, EqualsBuilderHelper equalsBuilderHelper) {

	TranslationUnit tu = new TranslationUnit();

	List<Term> existingTerms = existing.ggetAllTerms();
	List<Term> incomingTerms = incoming.ggetAllTerms();

	for (Term existingTerm : existingTerms) {
	    Term incomingTerm = incomingTerms.stream()
		    .filter(te -> equalsBuilderHelper.isEqualsIgnoreCase(te, existingTerm)).findFirst().orElse(null);

	    if (Objects.isNull(incomingTerm)) {
		continue;
	    }

	    if (isExistingTermStatusHasHigherLvl(existingTerm, incomingTerm)) {
		continue;
	    }

	    UpdateCommand updateCommand = UpdateCommandUtils.createUpdateCommandFromTerm(existingTerm,
		    UpdateCommand.CommandEnum.UPDATE);
	    updateCommand.setValue(existingTerm.getName());
	    updateCommand.setStatus(incomingTerm.getStatus());

	    tu.setTermEntryId(existing.getUuId());

	    if (sourceLangId.equals(existingTerm.getLanguageId())) {
		tu.addSourceTermUpdateCommand(updateCommand);
	    } else {
		tu.addTargetTermUpdateCommand(updateCommand);
	    }

	    if (Objects.nonNull(incomingTerm)) {
		existingTerm.setStatus(incomingTerm.getStatus());
	    }
	}
	return tu;
    }

    private boolean isExistingTermStatusHasHigherLvl(Term existingTerm, Term incomingTerm) {

	if (Objects.isNull(existingTerm.getStatus()) || Objects.isNull(incomingTerm.getStatus())) {
	    return true;
	}

	int existingTermStatusLvl = ItemStatusTypeHolder.getItemStatusTypeLevel(existingTerm.getStatus());
	int incomingTermStatusLvl = ItemStatusTypeHolder.getItemStatusTypeLevel(incomingTerm.getStatus());

	boolean isExistingBlacklisted = existingTerm.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName());
	boolean isIncomingBlacklisted = incomingTerm.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName());

	if (isExistingBlacklisted || isIncomingBlacklisted) {
	    return true;
	}

	return existingTermStatusLvl < incomingTermStatusLvl;
    }

    private void mergeSelectedTermEntries(MergeCommands commands) throws Exception {

	MultipleTermEntriesMergeCommand command = commands.getMultipleTermEntriesMergeCommand();
	if (Objects.isNull(command)) {
	    return;
	}

	List<String> termEntryIds = command.getTermEntryIds();
	if (CollectionUtils.isEmpty(termEntryIds)) {
	    return;
	}

	Long projectId = command.getProjectId();

	TermEntryService service = getTermEntryService();

	List<TermEntry> incoming = service.findTermentriesByIds(termEntryIds, projectId);

	validateByProject(incoming);

	validateByStatus(incoming);

	TermEntry existing = findOldestTermEntry(incoming);

	incoming.removeIf(te -> te.getUuId().equals(existing.getUuId()));

	resolveTermEntryTermStatus(existing, incoming, command.getSourceLanguage(), projectId);

	service.mergeTermEntries(existing, incoming);
    }

    private void resolveTermEntryTermStatus(TermEntry existing, List<TermEntry> incoming, String sourceLanguage,
	    Long projectId) {

	EqualsBuilderHelper equalsBuilderHelper = new EqualsBuilderHelper();

	List<TranslationUnit> tusForNewStatus = new ArrayList<>();

	for (TermEntry incomingTermEntry : incoming) {
	    tusForNewStatus.add(getTranslationUnitsForHighestTermsStatuses(existing, incomingTermEntry, sourceLanguage,
		    equalsBuilderHelper));
	}

	TermEntryService service = getTermEntryService();
	service.updateTermEntries(tusForNewStatus, sourceLanguage, projectId, Action.EDITED);
    }

    private void validateByProject(List<TermEntry> incoming) {

	Set<Long> projectIds = incoming.stream().map(TermEntry::getProjectId).collect(toSet());
	if (projectIds.size() > 1) {
	    throw new UserException(MessageResolver.getMessage("MergingFromDifferentProjectsError"));
	}
    }

    private void validateByStatus(List<TermEntry> termEntries) {

	for (TermEntry termEntry : termEntries) {

	    List<Term> terms = termEntry.ggetTerms();
	    for (Term term : terms) {
		if (ItemStatusTypeHolder.isInWorkflow(term)) {
		    throw new UserException(Messages.getString("TermInWorkflowError"));
		}
	    }
	}
    }
}
