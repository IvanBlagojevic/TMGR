package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.LanguageComparator;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TermEntryTranslationUnit;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.DtoBaseUserProfile;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.ProjectConverter;
import org.gs4tr.termmanager.model.dto.converter.TermConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.dto.converter.UserProfileConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.model.command.SendToTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoSendToTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTermTranslation;
import org.gs4tr.termmanager.service.model.command.dto.DtoTranslationPair;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_FOUR)
public class SendToTranslationTaskHandler extends AbstractToSubmissionTaskHandler
	implements AvailableTaskValidatorFolder<AbstractItemHolder> {

    private static final String PROJECT = "project"; //$NON-NLS-1$

    private static final String SOURCE_LANGUAGE = "sourceLanguage"; //$NON-NLS-1$

    private static final String SOURCE_TERM_TICKETS = "sourceTermTickets"; //$NON-NLS-1$

    private static final String TARGETS = "targets"; //$NON-NLS-1$

    private static final String TERM_ENTRY_COUNT = "termEntryCount"; //$NON-NLS-1$

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoSendToTranslationCommand.class;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	SendToTranslationCommand translationCommand = (SendToTranslationCommand) command;
	Long projectId = translationCommand.getProjectId();
	final String sourceLanguage = translationCommand.getSourceLanguage();
	Set<String> termEntryIds = translationCommand.getTermEntryIds();

	validateParameters(projectId, sourceLanguage, termEntryIds);

	List<Term> terms = getTermService().getTermsByTermEntryIds(termEntryIds, Arrays.asList(projectId));

	List<Term> sourceTerms = new ArrayList<Term>();
	List<Term> excludedTerms = new ArrayList<Term>();
	collectSourceTerms(sourceLanguage, terms, sourceTerms, excludedTerms);

	if (CollectionUtils.isNotEmpty(excludedTerms)) {
	    terms.removeAll(excludedTerms);
	}

	Map<String, Set<Term>> groupedTerms = ManualTaskHandlerUtils.groupTermsByTermEntry(terms);
	validateTerms(projectId, sourceLanguage, groupedTerms, termEntryIds);

	TmProject project = getProjectService().load(projectId);

	Map<String, Set<TmUserProfile>> languagePowerUsers = getProjectService().getLanguagePowerUsers(projectId);
	Map<String, Set<TmUserProfile>> languageAssignees = getProjectService().getLanguageAssignees(projectId,
		sourceLanguage);

	Set<Entry<String, Set<TmUserProfile>>> entries = languagePowerUsers.entrySet();
	for (Map.Entry<String, Set<TmUserProfile>> entry : entries) {
	    Set<TmUserProfile> languageAssigneesValue = languageAssignees.get(entry.getKey());
	    if (languageAssigneesValue == null) {
		languageAssignees.put(entry.getKey(), entry.getValue());
	    } else {
		languageAssigneesValue.addAll(entry.getValue());
	    }
	}

	Set<String> sourceTermIds = new HashSet<String>();
	List<DtoTermTranslation> termTranslations = createTermTranslations(sourceLanguage, termEntryIds,
		languageAssignees, groupedTerms, sourceTerms, sourceTermIds);
	Collections.sort(termTranslations, createTermTranslationComparator(new LanguageComparator()));

	TaskModel model = new TaskModel();
	model.addObject(SOURCE_LANGUAGE, LanguageConverter.fromInternalToDto(sourceLanguage));
	model.addObject(PROJECT, ProjectConverter.fromInternalToDto(project, false));
	model.addObject(TARGETS, termTranslations);
	model.addObject(TERM_ENTRY_COUNT, termEntryIds.size());
	model.addObject(SOURCE_TERM_TICKETS, sourceTermIds);

	return new TaskModel[] { model };
    }

    @Override
    public boolean isTaskAvailableForFolder(AbstractItemHolder entity, ItemFolderEnum folder) {
	boolean isSubmitter = ServiceUtils.isSubmitterUser(TmUserProfile.getCurrentUserProfile(),
		entity.getProjectId());
	return isSubmitter;
    }

    @Override
    @RefreshUserContext
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	SendToTranslationCommand sendToTranslationCommand = (SendToTranslationCommand) command;

	Long projectId = sendToTranslationCommand.getProjectId();

	Long submissionId = sendToTranslationCommand.getSubmissionId();

	String submissionName = sendToTranslationCommand.getSubmissionName();

	boolean reviewIsRequired = sendToTranslationCommand.isReviewRequired();

	String sourceLanguage = sendToTranslationCommand.getSourceLanguage();

	String submissionMarkerId = sendToTranslationCommand.getSubmissionMarkerId();

	List<String> sourceTermIds = sendToTranslationCommand.getSourceTermIds();

	List<Term> sourceTerms = getTermService().findTermsByIds(sourceTermIds, Arrays.asList(projectId));

	List<TermEntryTranslationUnit> translationUnits = sendToTranslationCommand.getTermEntryTranslationUnits();

	Submission submission = getSubmissionService().createSubmission(projectId, translationUnits, submissionName,
		submissionMarkerId, sourceLanguage, reviewIsRequired, submissionId, sourceTerms);

	MutableInt numberOfTerms = new MutableInt(0);

	StringBuffer jobComment = new StringBuffer();

	List<String> assignees = new ArrayList<String>();

	Map<String, Set<String>> assigneeLanguages = new HashMap<String, Set<String>>();

	collectMessageData(translationUnits, submission, numberOfTerms, jobComment, assignees, assigneeLanguages);

	sendEmailMessage(submissionName, jobComment, numberOfTerms, sourceLanguage, assigneeLanguages);

	TaskResponse response = new TaskResponse(new Ticket(submission.getSubmissionId()));

	return response;
    }

    private void collectMessageData(List<TermEntryTranslationUnit> translationUnits, Submission submission,
	    MutableInt numberOfTerms, StringBuffer jobComment, List<String> assignees,
	    Map<String, Set<String>> assigneeLanguages) {
	for (TermEntryTranslationUnit unit : translationUnits) {
	    List<UpdateCommand> commands = unit.getUpdateCommands();
	    if (CollectionUtils.isNotEmpty(commands)) {
		for (UpdateCommand updateCommand : commands) {
		    if (updateCommand.getType().equals(TypeEnum.TERM.toString())) {
			numberOfTerms.increment();

			String asssignee = updateCommand.getAsssignee();

			Set<String> targetLanguages = assigneeLanguages.get(asssignee);
			if (targetLanguages == null) {
			    targetLanguages = new HashSet<String>();
			    assigneeLanguages.put(asssignee, targetLanguages);
			}

			targetLanguages.add(updateCommand.getLanguageId());

			assignees.add(asssignee);

		    } else if (updateCommand.getType().equals(TypeEnum.COMMENT.toString())) {
			if (submission.getMarkerId().equals(updateCommand.getParentMarkerId())) {
			    jobComment.append(updateCommand.getValue());
			}
		    }
		}
	    }
	}
    }

    private void collectSourceTerms(final String sourceLanguage, List<Term> terms, List<Term> sourceTerms,
	    List<Term> excludedTerms) {
	for (Term term : terms) {
	    if (sourceLanguage.equals(term.getLanguageId())) {
		if (term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName())) {
		    sourceTerms.add(term);
		} else {
		    excludedTerms.add(term);
		}
	    }
	}
    }

    private Comparator<DtoBaseUserProfile> createBaseUserProfileComparator() {
	return new Comparator<DtoBaseUserProfile>() {
	    @Override
	    public int compare(DtoBaseUserProfile u1, DtoBaseUserProfile u2) {
		return u1.getUserName().compareToIgnoreCase(u2.getUserName());
	    }
	};
    }

    private Comparator<DtoTermTranslation> createTermTranslationComparator(final LanguageComparator comparator) {
	return new Comparator<DtoTermTranslation>() {
	    @Override
	    public int compare(DtoTermTranslation t1, DtoTermTranslation t2) {
		return comparator.compare(t1.getLanguage(), t2.getLanguage());
	    }
	};
    }

    private List<DtoTermTranslation> createTermTranslations(String sourceLanguageId, Set<String> termEntryIds,
	    Map<String, Set<TmUserProfile>> languageAssignees, Map<String, Set<Term>> groupedTerm,
	    List<Term> selectedSourceTerms, Set<String> sourceTermIds) {

	String[] termEntryTickets = termEntryIds.toArray(new String[termEntryIds.size()]);

	List<DtoTermTranslation> termTranslations = new ArrayList<>();

	List<DtoTranslationPair> pairs = new ArrayList<>();

	for (Entry<String, Set<TmUserProfile>> entry : languageAssignees.entrySet()) {
	    String languageId = entry.getKey();

	    if (languageId.equals(sourceLanguageId)) {
		continue;
	    }
	    DtoTermTranslation termTranslation = new DtoTermTranslation();

	    Set<TmUserProfile> assignees = entry.getValue();
	    DtoBaseUserProfile[] convertedAssignees = UserProfileConverter.baseFromInternalToDto(assignees);
	    Arrays.sort(convertedAssignees, createBaseUserProfileComparator());
	    termTranslation.setAssignees(convertedAssignees);

	    termTranslation.setLanguage(LanguageConverter.fromInternalToDto(languageId));
	    termTranslation.setTermEntryTickets(termEntryTickets);

	    MutableInt totalTargetTermsCount = new MutableInt(0);
	    MutableInt blackListedTermCount = new MutableInt(0);
	    MutableInt termsInTranslationCount = new MutableInt(0);

	    for (Entry<String, Set<Term>> groupEntry : groupedTerm.entrySet()) {
		String termEntryId = groupEntry.getKey();
		Set<Term> terms = groupEntry.getValue();

		Map<String, Set<Term>> languageTermMap = groupTermsByLanguage(terms, sourceLanguageId, languageId);
		Set<Term> sourceTerms = languageTermMap.get(sourceLanguageId);
		Set<Term> targetTerms = languageTermMap.get(languageId);

		Set<Term> refTargetTerms = new HashSet<Term>();
		Set<Term> refSourceTerms = new HashSet<Term>();
		for (Term term : sourceTerms) {
		    if (selectedSourceTerms.contains(term)) {
			refSourceTerms.add(term);
		    }
		}

		DtoTranslationPair pair = new DtoTranslationPair();
		pair.setTermEntryTicket(TicketConverter.fromInternalToDto(termEntryId));

		boolean createPair = true;

		if (!targetTerms.isEmpty()) {
		    for (Term term : targetTerms) {
			totalTargetTermsCount.increment();

			if (term.isForbidden()) {
			    blackListedTermCount.increment();
			    continue;
			}
			if (ItemStatusTypeHolder.isTermInTranslation(term)) {
			    termsInTranslationCount.increment();
			    continue;
			}
			refTargetTerms.add(term);
		    }

		    if (refTargetTerms.isEmpty()) {
			createPair = false;
		    }
		} else {
		    totalTargetTermsCount.increment();
		}

		if (createPair) {
		    for (Term sourceTerm : refSourceTerms) {
			sourceTermIds.add(sourceTerm.getUuId());
		    }
		    pair.setSourceTerms(TermConverter.fromInternalToDto(refSourceTerms));
		    pair.setTargetTerms(TermConverter.fromInternalToDto(refTargetTerms));
		    pairs.add(pair);
		}
	    }

	    termTranslation.setTotalTargetTermsCount(totalTargetTermsCount.intValue());
	    termTranslation.setBlackListedTermCount(blackListedTermCount.intValue());
	    termTranslation.setTermsInTranslationCount(termsInTranslationCount.intValue());

	    termTranslation.setTranslationPairs(pairs.toArray(new DtoTranslationPair[pairs.size()]));

	    termTranslations.add(termTranslation);

	    pairs.clear();
	}

	return termTranslations;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermService getTermService() {
	return _termService;
    }

    private Map<String, Set<Term>> groupTermsByLanguage(Set<Term> terms, String sourceLanguageId,
	    String targetLanguageId) {
	Set<Term> sourceList = new HashSet<Term>();
	Set<Term> targetList = new HashSet<Term>();
	for (Term term : terms) {
	    if (term.getLanguageId().equals(sourceLanguageId)) {
		sourceList.add(term);
	    } else if (term.getLanguageId().equals(targetLanguageId)) {
		targetList.add(term);
	    }
	}

	Map<String, Set<Term>> languageTermMap = new HashMap<String, Set<Term>>();
	languageTermMap.put(sourceLanguageId, sourceList);
	languageTermMap.put(targetLanguageId, targetList);

	return languageTermMap;
    }

    private void validateParameters(Long projectId, String sourceLanguage, Set<String> termEntryIds) {
	if (projectId == null || sourceLanguage == null || CollectionUtils.isEmpty(termEntryIds)) {
	    throw new RuntimeException(Messages.getString("SendToTranslationTaskHandler.3")); //$NON-NLS-1$
	}
    }

    private void validateTerms(Long projectId, String sourceLanguage, Map<String, Set<Term>> groupedTerms,
	    Set<String> termEntryIds) {
	if (groupedTerms.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("SendToTranslationTaskHandler.5"), //$NON-NLS-1$
		    MessageResolver.getMessage("SendToTranslationTaskHandler.7")); //$NON-NLS-1$
	}

	Set<String> teIds = groupedTerms.keySet();
	for (String termEntryId : termEntryIds) {
	    if (!teIds.contains(termEntryId)) {
		throw new UserException(MessageResolver.getMessage("SendToTranslationTaskHandler.5"), //$NON-NLS-1$
			MessageResolver.getMessage("SendToTranslationTaskHandler.7")); //$NON-NLS-1$
	    }
	}

	Set<Term> sourceTerms = new HashSet<>();

	for (Entry<String, Set<Term>> entry : groupedTerms.entrySet()) {

	    for (Term term : entry.getValue()) {
		if (!term.getProjectId().equals(projectId)) {
		    throw new UserException(MessageResolver.getMessage("SendToTranslationTaskHandler.5"), //$NON-NLS-1$
			    MessageResolver.getMessage("SendToTranslationTaskHandler.2")); //$NON-NLS-1$
		}
		if (term.getLanguageId().equals(sourceLanguage)) {
		    sourceTerms.add(term);
		}
	    }

	    boolean flag = false;

	    for (Term term : sourceTerms) {
		if (term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName())) {
		    flag = true;
		    break;
		}
	    }

	    if (!flag) {
		throw new UserException(MessageResolver.getMessage("SendToTranslationTaskHandler.5"), //$NON-NLS-1$
			MessageResolver.getMessage("SendToTranslationTaskHandler.7")); //$NON-NLS-1$
	    }

	    sourceTerms.clear();
	}
    }
}
