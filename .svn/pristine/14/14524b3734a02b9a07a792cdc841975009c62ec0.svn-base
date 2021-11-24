package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.model.command.ForbidTermCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoForbidTermCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTermTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidatorFolder<AbstractItemHolder> {

    private static final String TERMS = "terms"; //$NON-NLS-1$

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoForbidTermCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	List<Long> projectIds = Arrays.asList(parentIds);

	ForbidTermCommand internalCommand = (ForbidTermCommand) command;

	String termEntryId = internalCommand.getTermEntryId();

	List<String> sourceIds = internalCommand.getSourceIds();

	List<Term> terms = new ArrayList<Term>();
	if (termEntryId != null) {
	    Long projectId = projectIds.get(0);
	    terms = getTermService().getAllTermsInTermEntry(termEntryId, projectId);

	    excludeTermsInTranslation(terms);

	    if (CollectionUtils.isEmpty(terms)) {
		throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"), //$NON-NLS-1$
			MessageResolver.getMessage("AbstractTermTaskHandler.3")); //$NON-NLS-1$
	    }

	    Set<String> allowedLanguages = TmUserProfile.getCurrentUserProfile().getProjectUserLanguages()
		    .get(projectId);
	    filterTerms(terms, allowedLanguages);
	} else {
	    List<String> termIds = internalCommand.getTermIds();

	    terms = getTermService().findTermsByIds(termIds, projectIds);

	    if (CollectionUtils.isEmpty(terms)) {
		throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"), //$NON-NLS-1$
			MessageResolver.getMessage("AbstractTermTaskHandler.3")); //$NON-NLS-1$
	    }
	}

	Set<Map<String, Object>> termModels = new HashSet<Map<String, Object>>();

	Map<String, Set<Term>> groupedTerms = ManualTaskHandlerUtils.groupTermsByTermEntry(terms);

	if (!groupedTerms.isEmpty()) {
	    for (Entry<String, Set<Term>> entry : groupedTerms.entrySet()) {
		String category = StringConstants.EMPTY;
		String keyTermEntryId = entry.getKey();
		Set<Term> valueTerms = entry.getValue();
		for (Term term : valueTerms) {
		    String termId = term.getUuId();
		    if (CollectionUtils.isNotEmpty(sourceIds) && sourceIds.contains(termId)) {
			category = term.getName();
			break;
		    }
		}

		for (Term term : valueTerms) {
		    boolean isSource = false;
		    String termId = term.getUuId();
		    if (CollectionUtils.isNotEmpty(sourceIds) && sourceIds.contains(termId)) {
			isSource = true;
		    }

		    populateTermModelMap(termModels, term, isSource, category, keyTermEntryId);
		}
	    }
	}

	TaskModel model = new TaskModel();

	model.addObject(TERMS, termModels);

	return new TaskModel[] { model };
    }

    public abstract TaskResponse handleProcessTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files);

    @Override
    public boolean isTaskAvailableForFolder(AbstractItemHolder entity, ItemFolderEnum folder) {
	return !entity.isInTranslation();
    }

    public void populateTermModelMap(Set<Map<String, Object>> termModels, Term term, boolean isSource, String category,
	    String keyTermEntryId) {
	Ticket ticket = new Ticket(term.getUuId());
	String languageDisplayName = Language.valueOf(term.getLanguageId()).getDisplayName();
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("termTicket", ticket.toString()); //$NON-NLS-1$
	map.put("termName", term.getName()); //$NON-NLS-1$
	map.put("termLanguage", languageDisplayName); //$NON-NLS-1$
	map.put("isSource", isSource); //$NON-NLS-1$
	map.put("category", category); //$NON-NLS-1$
	map.put("termEntryTicket", keyTermEntryId); //$NON-NLS-1$
	map.put("status", term.getStatus()); //$NON-NLS-1$
	map.put("inTranslation", isTermInTranslation(term)); //$NON-NLS-1$
	map.put("isForbidden", term.isForbidden()); //$NON-NLS-1$
	termModels.add(map);
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return handleProcessTasks(parentIds, taskIds, command, files);
    }

    protected void excludeTermsInTranslation(List<Term> terms) {
	if (CollectionUtils.isNotEmpty(terms)) {
	    CollectionUtils.filter(terms, new Predicate() {
		@Override
		public boolean evaluate(Object o) {
		    Term term = (Term) o;
		    return !isTermInTranslation(term);
		}
	    });
	}
    }

    protected void filterTerms(List<Term> terms, final Set<String> languageIds) {
	CollectionUtils.filter(terms, new Predicate() {
	    @Override
	    public boolean evaluate(Object o) {
		Term term = (Term) o;
		boolean allowedByLanguage = languageIds.contains(term.getLanguageId());
		return allowedByLanguage;
	    }
	});
    }

    protected List<Term> findTerms(ForbidTermCommand command, List<Long> projectIds) {
	List<String> termIds = command.getTermIds();
	Validate.notEmpty(termIds, Messages.getString("AbstractTermTaskHandler.0")); //$NON-NLS-1$
	List<Term> terms = getTermService().findTermsByIds(termIds, projectIds);

	validateTerms(terms);

	return terms;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected TermService getTermService() {
	return _termService;
    }

    protected boolean isTermInTranslation(Term term) {
	String status = term.getStatus();
	return term.getInTranslationAsSource() || ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName().equals(status)
		|| ItemStatusTypeHolder.IN_FINAL_REVIEW.getName().equals(status);
    }

    protected void validateTerms(List<Term> terms) {
	if (CollectionUtils.isNotEmpty(terms)) {
	    for (Term term : terms) {
		if (isTermInTranslation(term)) {
		    throw new UserException(MessageResolver.getMessage("AbstractTermTaskHandler.7"), //$NON-NLS-1$
			    String.format(MessageResolver.getMessage("AbstractTermTaskHandler.8"), //$NON-NLS-1$
				    term.getName(), term.getLanguageId()));
		}
	    }
	}
    }
}
