package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.CustomCollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.DescriptionPreviewModel;
import org.gs4tr.termmanager.model.dto.LanguagePreviewModel;
import org.gs4tr.termmanager.model.dto.TermPreviewModel;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TermDescriptionConverter;
import org.gs4tr.termmanager.model.dto.converter.TermEntryDescriptionConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.TermEntryPreviewCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTermEntryPreviewCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewXmlTermEntryTaskHandler extends AbstractManualTaskHandler {

    private static final String LANGUAGE_PREVIEW_MODELS = "languagePreviewModels";

    private static final String MULTIMEDIA = "multimediaTickets";

    private static final String NOTES_CONFIGURED = "notesConfigured";

    private static final String TERM_ATTS_CONFIGURED = "termAttributesConfigured";

    private static final String TERM_ENTRY_DESCRIPTIONS = "termEntryDescriptions";

    private static final String TE_ATTS_CONFIGURED = "termEntryAttributesConfigured";

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoTermEntryPreviewCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	TermEntryPreviewCommand previewCommand = (TermEntryPreviewCommand) command;
	String termEntryId = previewCommand.getTermEntryId();
	Long projectId = parentIds[0];

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	List<String> languages = getLanguages(previewCommand, projectId);
	filterLanguageTermsByLanguages(termEntry.getLanguageTerms(), languages);

	Map<String, List<Term>> languageTermMap = new HashMap<>();
	fillTermsMap(languageTermMap, termEntry.ggetTerms(), languages);

	List<LanguagePreviewModel> languagePreviews = new ArrayList<>();
	fillLanguages(languagePreviews, languageTermMap);
	Collection<LanguagePreviewModel> sortedLanguagePreviews = sortLanguagePreviewsByLanguages(languagePreviews,
		languages);

	org.gs4tr.termmanager.model.dto.Description[] termEntryDescriptions = TermEntryDescriptionConverter
		.fromInternalToDto(termEntry.getDescriptions());

	TaskModel taskModel = new TaskModel(null, new Ticket(termEntryId));
	taskModel.addObject(LANGUAGE_PREVIEW_MODELS, sortedLanguagePreviews);
	taskModel.addObject(TERM_ENTRY_DESCRIPTIONS, termEntryDescriptions);
	List<TermEntryResourceTrack> resourceTracks = getTermEntryService()
		.findResourceTracksByTermEntryById(termEntryId);
	taskModel.addObject(MULTIMEDIA, ManualTaskHandlerUtils.collectMultimedia(resourceTracks));

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	taskModel.addObject(NOTES_CONFIGURED, isNotesConfigured(project));
	taskModel.addObject(TE_ATTS_CONFIGURED, isTermEntryAttributesConfigured(project));
	taskModel.addObject(TERM_ATTS_CONFIGURED, isTermAttributesConfigured(project));

	return new TaskModel[] { taskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {
	return null;
    }

    private void fillAttributes(Term term, TermPreviewModel termPreviewModel) {
	Set<Description> attributes = ServiceUtils.getDescriptionsByType(term, Description.ATTRIBUTE);

	if (CollectionUtils.isEmpty(attributes)) {
	    return;
	}

	org.gs4tr.termmanager.model.dto.Description[] dtoDescriptions = TermDescriptionConverter
		.fromInternalToDto(attributes);
	CustomCollectionUtils.sortDescriptions(dtoDescriptions);

	for (org.gs4tr.termmanager.model.dto.Description description : dtoDescriptions) {
	    List<DescriptionPreviewModel> descriptionPreviewModels = termPreviewModel.getDescriptionPreviewModels();

	    DescriptionPreviewModel descriptionPreviewModel = findSameDescription(description.getType(),
		    descriptionPreviewModels);
	    if (descriptionPreviewModel == null) {
		descriptionPreviewModel = new DescriptionPreviewModel();
		descriptionPreviewModel.setType(description.getType());
		descriptionPreviewModels.add(descriptionPreviewModel);
	    }

	    descriptionPreviewModel.getValues().add(StringEscapeUtils.escapeHtml(description.getValue()));
	}
    }

    private void fillLanguages(List<LanguagePreviewModel> languagePreviewModels,
	    Map<String, List<Term>> languageTermMap) {

	for (Map.Entry<String, List<Term>> entry : languageTermMap.entrySet()) {
	    String languageId = entry.getKey();
	    Locale locale = Locale.makeLocale(languageId);
	    List<Term> terms = entry.getValue();
	    LanguagePreviewModel model = new LanguagePreviewModel();
	    model.setIsRTL(locale.isRTL());
	    model.setLanguage(LanguageConverter.fromInternalToDto(locale));
	    fillTerms(terms, model);
	    languagePreviewModels.add(model);
	}
    }

    private void fillNotes(Term term, TermPreviewModel termPreviewModel) {
	Set<Description> notes = ServiceUtils.getDescriptionsByType(term, Description.NOTE);

	if (CollectionUtils.isEmpty(notes)) {
	    return;
	}

	org.gs4tr.termmanager.model.dto.Description[] termNotes = TermDescriptionConverter.fromInternalToDto(notes);
	CustomCollectionUtils.sortDescriptions(termNotes);

	for (org.gs4tr.termmanager.model.dto.Description description : termNotes) {
	    List<DescriptionPreviewModel> notePreviewModels = termPreviewModel.getNotePreviewModels();

	    DescriptionPreviewModel descriptionPreviewModel = findSameDescription(description.getType(),
		    notePreviewModels);
	    if (descriptionPreviewModel == null) {
		descriptionPreviewModel = new DescriptionPreviewModel();
		descriptionPreviewModel.setType(description.getType());
		notePreviewModels.add(descriptionPreviewModel);
	    }

	    descriptionPreviewModel.getValues().add(StringEscapeUtils.escapeHtml(description.getValue()));
	}
    }

    private void fillTerms(List<Term> terms, LanguagePreviewModel languagePreviewModel) {
	for (Term term : terms) {
	    TermPreviewModel termPreviewModel = new TermPreviewModel();
	    termPreviewModel.setName(term.getName());
	    termPreviewModel.setBlacklisted(term.isForbidden());
	    termPreviewModel.setTicket(TicketConverter.fromInternalToDto(term.getUuId()));
	    termPreviewModel.setMarkerId(term.getUuId());
	    termPreviewModel.setTermStatus(term.getStatus());

	    boolean inTranslationAsSource = term.getInTranslationAsSource() != null ? term.getInTranslationAsSource()
		    : false;
	    boolean inTranslation = ItemStatusTypeHolder.isTermInTranslation(term);
	    termPreviewModel.setInTranslation(inTranslation || inTranslationAsSource);
	    termPreviewModel.setSourceInTranslation(inTranslationAsSource);

	    fillAttributes(term, termPreviewModel);
	    fillNotes(term, termPreviewModel);

	    LinkedList<TermPreviewModel> termPreviewModels = languagePreviewModel.getTermPreviewModels();
	    if (term.isFirst()) {
		termPreviewModels.addFirst(termPreviewModel);
	    } else {
		termPreviewModels.add(termPreviewModel);
	    }
	}
    }

    private void fillTermsMap(Map<String, List<Term>> languageTermMap, List<Term> terms,
	    List<String> projectUserLanguages) {

	for (Term term : terms) {
	    String languageId = term.getLanguageId();
	    List<Term> addedTerms = languageTermMap.get(languageId);
	    if (CollectionUtils.isNotEmpty(addedTerms)) {
		addedTerms.add(term);
	    } else {
		List<Term> newTerms = new ArrayList<>();
		newTerms.add(term);
		languageTermMap.put(languageId, newTerms);
	    }
	}

	for (String projectUserLang : projectUserLanguages) {
	    languageTermMap.computeIfAbsent(projectUserLang, k -> new ArrayList<>());
	}
    }

    private void filterLanguageTermsByLanguages(Map<String, Set<Term>> languageTerms, List<String> languages) {
	if (MapUtils.isNotEmpty(languageTerms)) {
	    Set<String> keys = languageTerms.keySet();
	    keys.retainAll(languages);
	}
    }

    private DescriptionPreviewModel findSameDescription(String type,
	    List<DescriptionPreviewModel> descriptionPreviewModels) {
	for (DescriptionPreviewModel descriptionPreviewModel : descriptionPreviewModels) {
	    if (type.equals(descriptionPreviewModel.getType())) {
		return descriptionPreviewModel;
	    }
	}

	return null;
    }

    private List<String> getAllLanguages(TermEntryPreviewCommand command, Long projectId) {
	List<String> gridLanguages = command.getGridLanguages();
	List<String> languages = new ArrayList<>(gridLanguages);
	Set<String> projectUserLanguages = getProjectUserLanguages(projectId);
	projectUserLanguages.removeAll(gridLanguages);
	languages.addAll(sortLanguagesByDisplayName(projectUserLanguages));
	return languages;
    }

    private List<String> getLanguages(TermEntryPreviewCommand command, final Long projectId) {
	/*
	 * This method returns list of all languages with a specific order where: 1.
	 * Grid (filtered) languages are first in the list and should be ordered left to
	 * right as they are filtered in the grid. 2. Below grid languages, if user want
	 * to see all languages, list will contain project user languages sorted in
	 * alphabetical order (i.e sorted by language display name).
	 */
	return command.showAllLanguages() ? getAllLanguages(command, projectId) : command.getGridLanguages();
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private Set<String> getProjectUserLanguages(final Long projectId) {
	return nullSafeCopy(TmUserProfile.getCurrentUserProfile().getProjectUserLanguages().get(projectId));
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private boolean isNotesConfigured(TmProject project) {
	List<Attribute> descriptions = project.getAttributes();
	if (CollectionUtils.isEmpty(descriptions)) {
	    return false;
	}
	return descriptions.stream().anyMatch(description -> BaseTypeEnum.NOTE == description.getBaseTypeEnum());
    }

    private boolean isTermAttributesConfigured(TmProject project) {
	List<Attribute> attributes = project.getAttributes();
	if (CollectionUtils.isNotEmpty(attributes)) {
	    for (Attribute attribute : attributes) {
		if (AttributeLevelEnum.LANGUAGE == attribute.getAttributeLevel()) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean isTermEntryAttributesConfigured(TmProject project) {
	List<Attribute> attributes = project.getAttributes();

	if (CollectionUtils.isNotEmpty(attributes)) {
	    for (Attribute attribute : attributes) {
		if (AttributeLevelEnum.TERMENTRY == attribute.getAttributeLevel()) {
		    return true;
		}
	    }
	}
	return false;
    }

    private Set<String> nullSafeCopy(final Set<String> set) {
	return set == null ? Collections.emptySet() : new HashSet<>(set);
    }

    private Collection<LanguagePreviewModel> sortLanguagePreviewsByLanguages(
	    List<LanguagePreviewModel> languagePreviews, List<String> languageIds) {
	Map<Integer, LanguagePreviewModel> sorted = new TreeMap<>();
	for (LanguagePreviewModel languagePreview : languagePreviews) {
	    String languageId = languagePreview.getLanguage().getLocale();
	    sorted.put(languageIds.indexOf(languageId), languagePreview);
	}
	return sorted.values();
    }

    private List<String> sortLanguagesByDisplayName(final Set<String> languageIds) {
	return languageIds.stream().map(Language::valueOf).sorted(Comparator.comparing(Language::getDisplayName))
		.map(Language::getLanguageId).collect(Collectors.toList());
    }
}
