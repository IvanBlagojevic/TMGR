package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.AttributeConverter;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.model.dto.converter.MultilingualTermConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.model.command.BaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.TranslationViewCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoBaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.TargetDescriptionModel;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class ViewPropertiesTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoBaseLingualCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	TaskModel taskModel = new TaskModel();

	if (parentIds == null || parentIds.length < 1) {
	    throw new RuntimeException("project ticket must not be null");
	}

	List<Long> projectIds = Arrays.asList(parentIds);

	Long projectId = parentIds[0];

	Validate.notNull(projectId, "Parameter projectTicket can not be null!");

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	BaseLingualCommand allAttributesCommand = (BaseLingualCommand) command;

	List<Attribute> attributes = project.getDescriptions();
	if (CollectionUtils.isNotEmpty(attributes)) {
	    attributes.removeIf(a -> a.getAttributeLevel() == AttributeLevelEnum.TERMENTRY);
	}

	List<Attribute> notes = project.getNotes();

	taskModel.addObject("allAttributes", AttributeConverter.fromInternalToDto(attributes));
	taskModel.addObject("allNotes", AttributeConverter.fromInternalToDto(notes));

	String sourceId = allAttributesCommand.getSourceId();
	List<String> termIds = new ArrayList<>();
	Map<String, TargetDescriptionModel> targetDescriptions = new HashMap<>();
	List<TranslationViewCommand> targetTermsCommand = allAttributesCommand.getTargetTerms();

	if (sourceId == null) {
	    String sourceLanguageCode = allAttributesCommand.getSourceLocale();

	    if (StringUtils.isNotBlank(sourceLanguageCode)) {
		addLanguageToModel(taskModel, sourceLanguageCode, "sourceLanguage", "sourceIsRTL");
	    }

	    if (CollectionUtils.isNotEmpty(targetTermsCommand)) {
		for (TranslationViewCommand translationViewCommand : targetTermsCommand) {
		    String targetId = translationViewCommand.getTargetId();
		    String targetLanguageCode = translationViewCommand.getTargetLocale();

		    if (targetId == null && StringUtils.isNotBlank(targetLanguageCode)) {
			TargetDescriptionModel targetDescriptionModel = new TargetDescriptionModel();

			addLanguageNameAndRTL(targetLanguageCode, targetDescriptionModel);

			targetDescriptions.put(targetLanguageCode, targetDescriptionModel);

		    } else if (targetId != null) {
			TargetDescriptionModel targetDescriptionModel = new TargetDescriptionModel();
			termIds.add(targetId);

			List<Term> terms = getTermService().findTermsByIds(termIds, projectIds);
			Term targetTerm = findTermById(terms, targetId);

			handleTargetTermData(targetLanguageCode, targetDescriptionModel, targetTerm);
			targetDescriptions.put(MultilingualTermConverter.replaceLocaleCodeDash(targetLanguageCode),
				targetDescriptionModel);
		    }
		}
		taskModel.addObject("targetDescriptions", targetDescriptions);
	    }
	    return new TaskModel[] { taskModel };
	} else {
	    termIds.add(sourceId);

	    if (CollectionUtils.isNotEmpty(targetTermsCommand)) {
		targetTermsCommand.stream().filter(t -> Objects.nonNull(t.getTargetId()))
			.forEach(t -> termIds.add(t.getTargetId()));
	    }
	}

	List<Term> terms = getTermService().findTermsByIds(termIds, projectIds);

	Term sourceTerm = findTermById(terms, sourceId);

	String sourceLanguageCode = sourceTerm.getLanguageId();
	addLanguageToModel(taskModel, sourceLanguageCode, "sourceLanguage", "sourceIsRTL");

	Set<Description> attributeDescriptions = getDescriptionByType(sourceTerm, Description.ATTRIBUTE);
	taskModel.addObject("sourceDescriptions",
		DescriptionConverter.fromInternalToDtoExtended(attributeDescriptions));

	Set<Description> noteDescriptions = getDescriptionByType(sourceTerm, Description.NOTE);
	taskModel.addObject("sourceNotes", DescriptionConverter.fromInternalToDtoExtended(noteDescriptions));

	if (CollectionUtils.isNotEmpty(targetTermsCommand)) {
	    for (TranslationViewCommand translationViewCommand : targetTermsCommand) {
		TargetDescriptionModel targetDescriptionModel = new TargetDescriptionModel();
		String targetLocale = translationViewCommand.getTargetLocale();

		String targetId = translationViewCommand.getTargetId();
		if (targetId != null) {
		    Term targetTerm = findTermById(terms, targetId);
		    String targetLanguageCode = targetTerm.getLanguageId();

		    handleTargetTermData(targetLanguageCode, targetDescriptionModel, targetTerm);
		    targetDescriptions.put(MultilingualTermConverter.replaceLocaleCodeDash(targetLanguageCode),
			    targetDescriptionModel);

		} else if (StringUtils.isNotBlank(targetLocale)) {
		    addLanguageNameAndRTL(targetLocale, targetDescriptionModel);
		    targetDescriptions.put(targetLocale, targetDescriptionModel);
		}
	    }
	}
	taskModel.addObject("targetDescriptions", targetDescriptions);

	return new TaskModel[] { taskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }

    private void addLanguageNameAndRTL(String targetLanguageCode, TargetDescriptionModel targetDescriptionModel) {
	Locale targetLocale = Locale.makeLocale(targetLanguageCode);

	String targetLanguage = targetLocale.getDisplayName();
	targetDescriptionModel.setTargetLanguage(targetLanguage);

	boolean isRTL = targetLocale.isRTL();
	targetDescriptionModel.setTargetIsRTL(isRTL);
    }

    private void addLanguageToModel(TaskModel taskModel, String languageCode, String languageNameProperty,
	    String rtlProperty) {
	Locale locale = Locale.makeLocale(languageCode);
	taskModel.addObject(languageNameProperty, locale.getDisplayName());
	taskModel.addObject(rtlProperty, locale.isRTL());
    }

    private Term findTermById(List<Term> terms, String termId) {
	for (Term term : terms) {
	    if (term.getUuId().equals(termId)) {
		return term;
	    }
	}
	throw new RuntimeException("Term does not exist");
    }

    private Set<Description> getDescriptionByType(Term term, final String baseType) {
	Set<Description> descriptions = new HashSet<>();

	if (CollectionUtils.isNotEmpty(term.getDescriptions())) {
	    for (Description desc : term.getDescriptions()) {
		if (desc.getBaseType().equals(baseType)) {
		    descriptions.add(desc);
		}
	    }
	}

	return descriptions;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermService getTermService() {
	return _termService;
    }

    private void handleTargetTermData(String targetLanguageCode, TargetDescriptionModel targetDescriptionModel,
	    Term targetTerm) {
	addLanguageNameAndRTL(targetLanguageCode, targetDescriptionModel);

	Set<Description> attributeDescriptions = getDescriptionByType(targetTerm, Description.ATTRIBUTE);
	targetDescriptionModel
		.setTargetDescriptions(DescriptionConverter.fromInternalToDtoExtended(attributeDescriptions));

	Set<Description> noteDescriptions = getDescriptionByType(targetTerm, Description.NOTE);
	targetDescriptionModel.setTargetNotes(DescriptionConverter.fromInternalToDtoExtended(noteDescriptions));
    }
}
