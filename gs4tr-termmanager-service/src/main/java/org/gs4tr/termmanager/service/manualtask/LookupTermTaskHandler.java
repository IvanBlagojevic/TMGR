package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.LookupTermCommand;
import org.gs4tr.termmanager.service.model.command.LookupTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoLookupTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.tm3.api.Page;
import org.springframework.beans.factory.annotation.Autowired;

public class LookupTermTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoLookupTermCommands.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	LookupTermCommands cmds = (LookupTermCommands) command;

	validateCommand(cmds);

	TermEntryService termEntryService = getTermEntryService();

	boolean matches = false;

	// this is null safe
	for (LookupTermCommand cmd : cmds.getCommands()) {
	    TmgrSearchFilter filter = createFilter(cmd);
	    if (filter == null) {
		continue;
	    }

	    Page<TermEntry> page = termEntryService.searchTermEntries(filter);

	    List<TermEntry> termEntries = page.getResults();

	    // filter termEntry matches by status in order to avoid to match
	    // termEntries that are in workflow
	    filterTermEntries(cmd, termEntries);

	    if (CollectionUtils.isEmpty(termEntries)) {
		continue;
	    }

	    Map<String, List<TermEntry>> groupTermEntries = groupTermEntryMatches(cmd.getLanguageId(), cmd.getTerms(),
		    termEntries);

	    Set<String> termNames = cmd.getTerms();

	    for (String termName : termNames) {

		List<TermEntry> matchedTermEntries = groupTermEntries.get(termName);

		if (CollectionUtils.isNotEmpty(matchedTermEntries)) {
		    getTermEntryService().updateTermEntriesLatestChanges(matchedTermEntries);
		    if (matchedTermEntries.size() > 1) {
			matches = true;
		    }
		}

	    }

	}

	TaskModel model = new TaskModel();
	model.addObject("matches", matches); //$NON-NLS-1$

	return new TaskModel[] { model };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }

    private TmgrSearchFilter createFilter(LookupTermCommand cmd) {
	Long projectId = cmd.getProjectId();
	String languageId = cmd.getLanguageId();
	Set<String> terms = cmd.getTerms();

	if (terms.isEmpty()) {
	    return null;
	}

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);
	filter.setSourceLanguage(languageId);

	filter.addLanguageResultField(true, getSynonymNumber(), languageId);

	List<String> statuses = Arrays.asList(ItemStatusTypeHolder.PROCESSED.getName(),
		ItemStatusTypeHolder.BLACKLISTED.getName(), ItemStatusTypeHolder.WAITING.getName(),
		ItemStatusTypeHolder.ON_HOLD.getName());
	filter.setStatuses(statuses);

	StringBuilder builder = new StringBuilder();
	for (String term : terms) {
	    builder.append(term);
	    builder.append(StringConstants.SPACE);
	}

	filter.setTextFilter(new TextFilter(builder.toString(), true, false));
	filter.setPageable(new TmgrPageRequest(0, 20, null));

	return filter;
    }

    private List<Map<String, Object>> createMatchList(String searchedTerm, List<TermEntry> termEntryMatches,
	    LookupTermCommand cmd) {
	List<Map<String, Object>> matchedList = new ArrayList<Map<String, Object>>();

	if (CollectionUtils.isNotEmpty(termEntryMatches)) {
	    for (TermEntry termEntry : termEntryMatches) {

		Term termHit = findTermHit(searchedTerm, cmd, termEntry);
		if (termHit == null) {
		    continue;
		}

		Map<String, Object> match = new HashMap<String, Object>();
		match.put("projectName", termEntry.getProjectName()); //$NON-NLS-1$
		match.put("projectTicket", TicketConverter //$NON-NLS-1$
			.fromInternalToDto(termEntry.getProjectId()));
		match.put("termEntryTicket", termEntry.getUuId()); //$NON-NLS-1$
		match.put("locale", termHit.getLanguageId()); //$NON-NLS-1$
		match.put("termHit", termHit.getName()); //$NON-NLS-1$
		match.put("termStatus", termHit.getStatus()); //$NON-NLS-1$
		Set<Description> descriptions = termHit.getDescriptions();
		match.put("descriptions", descriptions != null ? descriptions //$NON-NLS-1$
			: new ArrayList<Description>());

		matchedList.add(match);
	    }
	}
	return matchedList;
    }

    private void filterTermEntries(LookupTermCommand cmd, List<TermEntry> termEntries) {
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}
	String languageId = cmd.getLanguageId();

	Iterator<TermEntry> iterator = termEntries.iterator();
	while (iterator.hasNext()) {
	    TermEntry termEntry = iterator.next();

	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	    if (languageTerms == null) {
		continue;
	    }

	    Set<Term> terms = languageTerms.get(languageId);
	    if (terms == null) {
		continue;
	    }

	    boolean inTranslation = terms.stream().anyMatch(t -> ItemStatusTypeHolder.isInWorkflow(t));
	    if (inTranslation) {
		iterator.remove();
	    }
	}
    }

    private Term findTermHit(String searchedTerm, LookupTermCommand cmd, TermEntry termEntry) {
	Term termHit = null;

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms == null) {
	    return termHit;
	}

	Set<Term> terms = languageTerms.get(cmd.getLanguageId());
	if (CollectionUtils.isEmpty(terms)) {
	    return termHit;
	}

	for (Term term : terms) {
	    if (searchedTerm.equalsIgnoreCase(term.getName())) {
		termHit = term;
		break;
	    }
	}
	return termHit;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    // group termEntry matches by searched term name
    private Map<String, List<TermEntry>> groupTermEntryMatches(String languageId, Set<String> termNames,
	    List<TermEntry> termEntries) {
	Map<String, List<TermEntry>> map = new HashMap<>();

	if (CollectionUtils.isNotEmpty(termEntries)) {
	    for (String termName : termNames) {

		List<TermEntry> entries = map.get(termName);
		if (entries == null) {
		    entries = new ArrayList<>();
		    map.put(termName, entries);
		}

		for (TermEntry termEntry : termEntries) {
		    Set<Term> terms = termEntry.getLanguageTerms().get(languageId);

		    if (CollectionUtils.isEmpty(terms)) {
			continue;
		    }

		    for (Term term : terms) {
			if (termName.equalsIgnoreCase(term.getName())) {
			    entries.add(termEntry);
			    break;
			}
		    }
		}
	    }
	}

	return map;
    }

    private void validateCommand(LookupTermCommands cmds) {
	for (LookupTermCommand cmd : cmds.getCommands()) {
	    Long projectId = cmd.getProjectId();
	    String languageId = cmd.getLanguageId();
	    if (projectId == null || StringUtils.isBlank(languageId)) {
		throw new UserException(Messages.getString("LookupTermTaskHandler.2"), //$NON-NLS-1$
			Messages.getString("LookupTermTaskHandler.7")); //$NON-NLS-1$
	    }
	}
    }
}
