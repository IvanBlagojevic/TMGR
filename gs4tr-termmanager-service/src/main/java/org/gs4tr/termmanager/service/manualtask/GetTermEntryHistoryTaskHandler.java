package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.TermEntryDifferences;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.comparator.TermEntryComparator;
import org.gs4tr.termmanager.service.model.command.TermEntryHistoryCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTermEntryHistoryCommand;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class GetTermEntryHistoryTaskHandler extends AbstractManualTaskHandler {

    /**
     * Shared empty term entry instance used to be compared with first added term
     * entry in history.
     */
    private static final TermEntry EMPTY_TERM_ENTRY = new TermEntry();
    private static final String TERM_ENTRY_HISTORY = "revisions";
    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoTermEntryHistoryCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	Validate.notEmpty(parentIds, "Parameter parentIds can not be empty.");

	List<Long> projectIds = Arrays.asList(parentIds);

	TermEntryHistoryCommand cmd = (TermEntryHistoryCommand) command;

	List<TermEntry> history = getTermEntryHistory(cmd);
	if (CollectionUtils.isEmpty(history)) {
	    // no history for a term entry
	    return createResponseData(Collections.emptyList());
	}
	List<String> languages = getLanguages(cmd, projectIds.get(0));
	filterHistoryByLanguages(history, languages);

	List<TermEntryPair> termEntryPairs = new ArrayList<>(history.size());
	Iterator<TermEntry> termEntries = history.iterator();
	TermEntry current = termEntries.next();
	while (termEntries.hasNext()) {
	    TermEntry previous = termEntries.next();
	    TermEntryPair tePair = new TermEntryPair(previous, current);
	    termEntryPairs.add(tePair);
	    current = previous;
	}
	List<TermEntryDifferences> revisions = new ArrayList<>(termEntryPairs.size());
	for (final TermEntryPair termEntryPair : termEntryPairs) {
	    TermEntryDifferences revision = processTermEntryPair(termEntryPair);
	    if (Objects.nonNull(revision)) {
		revision.sortLanguageModifications(languages);
		revisions.add(revision);
	    }
	}
	return createResponseData(revisions);
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	throw new UnsupportedOperationException();
    }

    private Set<String> copy(final Set<String> set) {
	return set == null ? Collections.emptySet() : new HashSet<>(set);
    }

    private TaskModel[] createResponseData(List<TermEntryDifferences> revisions) {
	TaskModel taskModel = new TaskModel();
	taskModel.addObject(TERM_ENTRY_HISTORY, revisions);
	return new TaskModel[] { taskModel };
    }

    private void filterHistoryByLanguages(List<TermEntry> history, List<String> languages) {
	Iterator<TermEntry> termEntries = history.iterator();
	while (termEntries.hasNext()) {
	    TermEntry termEntry = termEntries.next();
	    Map<String, Set<Term>> map = termEntry.getLanguageTerms();
	    if (MapUtils.isNotEmpty(map)) {
		Set<String> keys = map.keySet();
		keys.retainAll(languages);
	    }
	}
    }

    private List<String> getAllLanguages(TermEntryHistoryCommand command, Long projectId) {
	List<String> gridLanguages = command.getGridLanguages();
	List<String> languages = new ArrayList<>(gridLanguages);
	Set<String> projectUserLanguages = getProjectUserLanguages(projectId);
	projectUserLanguages.removeAll(gridLanguages);
	languages.addAll(sortLanguagesByDisplayName(projectUserLanguages));
	return languages;
    }

    private List<String> getLanguages(TermEntryHistoryCommand command, final Long projectId) {
	/*
	 * This method returns list of all languages with a specific order where: 1.
	 * Grid (filtered) languages are first in the list and should be ordered left to
	 * right as they are filtered in the grid. 2. Below grid languages, if user want
	 * to see all languages, list will contain project user languages sorted in
	 * alphabetical order (i.e sorted by language display name).
	 */
	return command.showAllLanguages() ? getAllLanguages(command, projectId) : command.getGridLanguages();
    }

    private Set<String> getProjectUserLanguages(final Long projectId) {
	return copy(TmUserProfile.getCurrentUserProfile().getProjectUserLanguages().get(projectId));
    }

    private List<TermEntry> getTermEntryHistory(TermEntryHistoryCommand command) {
	List<TermEntry> history = getTermEntryService().findHistoryByTermEntryId(command.getTermEntryId());
	if (CollectionUtils.isEmpty(history)) {
	    return Collections.emptyList();
	}
	/*
	 * Add one more (empty) termEntry in history in order to create valid first
	 * revision. (i.e first added term entry in history need to be compared with
	 * empty instance)
	 */
	history.add(EMPTY_TERM_ENTRY);

	return history;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private TermEntryDifferences processTermEntryPair(TermEntryPair tePair) {
	TermEntry previous = tePair.getPrevious();
	TermEntry current = tePair.getCurrent();
	return TermEntryComparator.INSTANCE.compare(previous, current);
    }

    private List<String> sortLanguagesByDisplayName(Set<String> languageCodes) {
	return languageCodes.stream().map(Language::valueOf).sorted(Comparator.comparing(Language::getDisplayName))
		.map(Language::getLanguageId).collect(Collectors.toList());
    }

    private static class TermEntryPair {

	private final TermEntry _current;
	private final TermEntry _previous;

	TermEntryPair(TermEntry previous, TermEntry current) {
	    _current = current;
	    _previous = previous;
	}

	TermEntry getCurrent() {
	    return _current;
	}

	TermEntry getPrevious() {
	    return _previous;
	}
    }
}
