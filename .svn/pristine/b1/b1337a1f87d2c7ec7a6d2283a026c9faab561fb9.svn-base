package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.model.command.DeleteTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoDeleteTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

@SystemTask(priority = TaskPriority.LEVEL_SIX)
public class DeleteTermTaskHandler extends AbstractTermActionTaskHandler {

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoDeleteTermCommands.class;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	DeleteTermCommands deleteCommand = (DeleteTermCommands) command;

	// Find all termEntries
	List<TermEntry> termEntries = findTermEntries(deleteCommand.getDeleteCommands());

	List<Term> terms = collectTermsToDelete(deleteCommand, termEntries);

	// Validate Terms
	validateAllTermEntryTerms(termEntries, terms, deleteCommand);

	// Delete terms
	try {
	    performTermsAction(deleteCommand.getSourceLanguage(), terms, null, UpdateCommand.CommandEnum.REMOVE);
	} catch (Exception e) {
	    throw new UserException(e.getMessage(), e);
	}

	return new TaskResponse(null);
    }

    private void checkUserLanguagePrivileges(List<TermEntry> termEntries) throws UserException {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	if (user.isPowerUser()) {
	    return;
	}

	for (TermEntry termEntry : termEntries) {
	    Long projectId = termEntry.getProjectId();
	    Set<String> termEntryLanguages = termEntry.getLanguageTerms().keySet();

	    Set<String> userProjectLanguages = user.getProjectUserLanguages().get(projectId);

	    if (!userProjectLanguages.containsAll(termEntryLanguages)) {
		throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"),
			MessageResolver.getMessage("AbstractTermTaskHandler.9"));
	    }
	}
    }

    private List<Term> collectTermsToDelete(DeleteTermCommands cmd, List<TermEntry> termEntries) {
	List<Term> terms = new ArrayList<>();

	if (cmd.isDeleteTermEntries()) {
	    termEntries.forEach(termEntry -> terms.addAll(termEntry.ggetTerms()));
	} else {
	    terms.addAll(getTermsForDelete(cmd, termEntries));
	}

	return terms;
    }

    private Set<Term> findTermsBySourceLanguage(TermEntry entry, String sourceLanguage) {

	Set<Term> terms = entry.getLanguageTerms().get(sourceLanguage);
	return CollectionUtils.isNotEmpty(terms) ? terms : Collections.emptySet();
    }

    private List<Term> getTermsForDelete(DeleteTermCommands cmd, List<TermEntry> termEntries) {
	String sourceLanguage = cmd.getSourceLanguage();
	List<String> targetLanguages = cmd.getTargetLanguages();

	boolean deleteSource = StringUtils.isNotEmpty(sourceLanguage);

	List<Term> terms = new ArrayList<>();

	List<String> termIds = getTermIdsFromCommand(cmd.getDeleteCommands());

	if (deleteSource) {
	    List<Term> sourceTerms = new ArrayList<>();

	    termEntries.forEach(t -> sourceTerms.addAll(findTermsBySourceLanguage(t, sourceLanguage)));
	    termEntries.forEach(t -> addAllTermsByLanguage(sourceTerms, findTermsBySourceLanguage(t, sourceLanguage)));

	    if (cmd.isIncludeSourceSynonyms()) {
		terms.addAll(sourceTerms);
	    } else {
		terms.addAll(
			sourceTerms.stream().filter(s -> termIds.contains(s.getUuId())).collect(Collectors.toList()));
	    }
	}

	List<Term> targetTerms = new ArrayList<>();
	targetLanguages.forEach(
		l -> termEntries.forEach(t -> addAllTermsByLanguage(targetTerms, t.getLanguageTerms().get(l))));

	if (cmd.isIncludeTargetSynonyms()) {
	    terms.addAll(targetTerms);
	} else {

	    for (Term term : terms) {
		if (termIds.contains(term.getUuId())) {
		    termIds.remove(term.getUuId());
		}
	    }

	    termEntries.forEach(e -> termIds.forEach(t -> addTermToTermList(terms, e.ggetTermById(t))));
	}

	return terms;
    }

    private void validateAllTermEntryTerms(List<TermEntry> termEntries, List<Term> terms,
	    DeleteTermCommands deleteTermCommands) {
	validateTermsNotInWorkflow(terms);
	if (deleteTermCommands.isDeleteTermEntries()) {
	    checkUserLanguagePrivileges(termEntries);
	}
    }

    private void validateTermsNotInWorkflow(List<Term> terms) {
	if (areTermsInWorkflow(terms)) {
	    throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"),
		    MessageResolver.getMessage("TermsInWorkflowDeleteErrorMessage"));
	}
    }
}
