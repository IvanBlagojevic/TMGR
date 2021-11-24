package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;
import org.gs4tr.termmanager.service.utils.UpdateCommandUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTermActionTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidatorFolder<AbstractItemHolder> {

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	return new TaskModel[0];
    }

    @Override
    public boolean isTaskAvailableForFolder(AbstractItemHolder entity, ItemFolderEnum folder) {
	return !entity.isInTranslation();
    }

    protected void addAllTermsByLanguage(List<Term> targetTerms, Set<Term> termsByLanguage) {
	if (Objects.nonNull(termsByLanguage)) {
	    targetTerms.addAll(termsByLanguage);
	}
    }

    protected void addTermToTermList(List<Term> terms, Term term) {
	if (Objects.nonNull(term)) {
	    terms.add(term);
	}
    }

    protected boolean areTermsInWorkflow(List<Term> terms) {
	if (CollectionUtils.isEmpty(terms)) {
	    return false;
	}

	for (Term term : terms) {
	    if (isTermInTranslation(term)) {
		return true;
	    }
	}
	return false;
    }

    protected List<TermEntry> findTermEntries(List<TermCommandPerProject> cmds) {

	TermEntryService termEntryService = getTermEntryService();

	List<TermEntry> termEntries = new ArrayList<>();

	cmds.forEach(cmd -> {
	    List<TermEntry> result = termEntryService.findTermentriesByIds(cmd.getTermEntryIds(), cmd.getProjectId());
	    termEntries.addAll(result);
	});

	return termEntries;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected List<String> getTermIdsFromCommand(List<TermCommandPerProject> cmds) {
	List<String> termIds = new ArrayList<>();
	cmds.forEach(d -> termIds.addAll(d.getTermIds()));
	return termIds;
    }

    protected void groupTranslationUnitsByProjectId(TranslationUnit translationUnit, long projectId,
	    Map<Long, List<TranslationUnit>> groupByProjectId) {
	List<TranslationUnit> translationUnits = groupByProjectId.computeIfAbsent(projectId, k -> new ArrayList<>());
	translationUnits.add(translationUnit);
    }

    protected boolean isTermInTranslation(Term term) {
	String status = term.getStatus();
	return term.getInTranslationAsSource() || ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName().equals(status)
		|| ItemStatusTypeHolder.IN_FINAL_REVIEW.getName().equals(status);
    }

    protected void performTermsAction(String sourceLanguageId, List<Term> terms, String status,
	    UpdateCommand.CommandEnum cmdEnum) {

	Map<String, Set<Term>> groupByTermEntry = ManualTaskHandlerUtils.groupTermsByTermEntry(terms);

	Map<Long, List<TranslationUnit>> groupByProjectId = new HashMap<>();

	for (Map.Entry<String, Set<Term>> tempTerms : groupByTermEntry.entrySet()) {
	    Long projectId = null;
	    List<UpdateCommand> sourceUpdateCommand = new ArrayList<>();

	    for (Term term : tempTerms.getValue()) {
		UpdateCommand updateCommand = UpdateCommandUtils.createUpdateCommandFromTerm(term, cmdEnum);
		updateCommand.setValue(term.getName());
		updateCommand.setStatus(status);
		sourceUpdateCommand.add(updateCommand);
		if (projectId == null) {
		    projectId = term.getProjectId();
		}
	    }

	    TranslationUnit translationUnit = new TranslationUnit();
	    translationUnit.setTermEntryId(tempTerms.getKey());
	    translationUnit.setSourceTermUpdateCommands(sourceUpdateCommand);

	    groupTranslationUnitsByProjectId(translationUnit, projectId, groupByProjectId);
	}

	for (Map.Entry<Long, List<TranslationUnit>> map : groupByProjectId.entrySet()) {
	    getTermEntryService().updateTermEntries(map.getValue(), sourceLanguageId, map.getKey(), Action.EDITED);
	}
    }
}
