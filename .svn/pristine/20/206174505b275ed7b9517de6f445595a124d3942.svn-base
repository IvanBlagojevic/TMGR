package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.model.command.ChangeTermStatusCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoChangeTermStatusCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

@SystemTask(priority = TaskPriority.LEVEL_FIVE)
public class ChangeTermStatusTaskHandler extends AbstractTermActionTaskHandler {

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoChangeTermStatusCommands.class;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	ChangeTermStatusCommands changeTermStatusCommands = (ChangeTermStatusCommands) command;

	// Find all termEntries
	List<TermEntry> termEntries = findTermEntries(changeTermStatusCommands.getChangeStatusCommands());

	List<Term> terms = collectTerms(changeTermStatusCommands, termEntries);

	// Validate if there is term in workflow
	validateTermsNotInWorkflow(terms);

	// Change status
	performTermsAction(changeTermStatusCommands.getSourceLanguage(), terms,
		changeTermStatusCommands.getTermStatus(), UpdateCommand.CommandEnum.UPDATE);

	return new TaskResponse(null);
    }

    private List<Term> collectTerms(ChangeTermStatusCommands cmd, List<TermEntry> termEntries) {
	String sourceLanguage = cmd.getSourceLanguage();
	List<String> targetLanguages = cmd.getTargetLanguages();

	boolean changeSourceStatus = StringUtils.isNotEmpty(sourceLanguage);

	List<Term> terms = new ArrayList<>();

	List<String> termIds = getTermIdsFromCommand(cmd.getChangeStatusCommands());

	if (changeSourceStatus) {
	    List<Term> sourceTerms = new ArrayList<>();
	    termEntries.forEach(t -> addAllTermsByLanguage(sourceTerms, t.getLanguageTerms().get(sourceLanguage)));

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

    private void validateTermsNotInWorkflow(List<Term> terms) {
	if (areTermsInWorkflow(terms)) {
	    throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"),
		    MessageResolver.getMessage("TermsInWorkflowDeleteErrorMessage"));
	}
    }
}
