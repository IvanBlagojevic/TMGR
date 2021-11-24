package org.gs4tr.termmanager.service.manualtask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.Submission;
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
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.model.command.BaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.TranslationViewCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoBaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.TargetDescriptionModel;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class ViewTranslationPropertiesTaskHandler extends AbstractManualTaskHandler {

    public static final String ATTRIBUTE = "ATTRIBUTE";

    public static final String NOTE = "NOTE";

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private SubmissionTermService _submissionTermService;

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
	    throw new RuntimeException("Submission ticket must not be null");
	}

	Long submissionId = parentIds[0];

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);

	Long projectId = submission.getProject().getProjectId();

	if (projectId == null || projectId == 0) {
	    Set<Long> userProjectIds = ServiceUtils.findAllUserProjectIds();
	    Iterator<Long> iterator = userProjectIds.iterator();
	    if (iterator.hasNext()) {
		projectId = iterator.next();
	    }
	}

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	BaseLingualCommand allAttributesCommand = (BaseLingualCommand) command;

	List<Attribute> descriptions = project.getDescriptions();
	CollectionUtils.filter(descriptions, new Predicate() {
	    @Override
	    public boolean evaluate(Object arg0) {
		Attribute attribute = (Attribute) arg0;
		return AttributeLevelEnum.TERMENTRY != attribute.getAttributeLevel();
	    }
	});

	List<Attribute> notes = project.getNotes();

	taskModel.addObject("allAttributes", AttributeConverter.fromInternalToDto(descriptions));
	taskModel.addObject("allNotes", AttributeConverter.fromInternalToDto(notes));

	handleSourceTermData(taskModel, allAttributesCommand, projectId);

	handleTargetTermData(taskModel, allAttributesCommand, projectId);

	return new TaskModel[] { taskModel };
    }

    public TermService getTermService() {
	return _termService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }

    private void addLanguageToModel(TaskModel taskModel, String languageCode, String languageNameProperty,
	    String rtlProperty) {

	Locale targetLocale = Locale.makeLocale(languageCode);
	String language = targetLocale.getDisplayName();

	taskModel.addObject(languageNameProperty, language);
	taskModel.addObject(rtlProperty, targetLocale.isRTL());
    }

    private Set<Description> getDescriptionByBaseType(Term term, final String baseType) {
	Set<Description> descriptions = new HashSet<Description>();
	Set<Description> termDescriptions = term.getDescriptions();

	if (CollectionUtils.isNotEmpty(termDescriptions)) {
	    for (Description desc : termDescriptions) {
		String termBaseType = desc.getBaseType();
		if (termBaseType.equals(baseType)) {
		    descriptions.add(desc);
		}
	    }
	}
	return descriptions;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    private void handleSourceTermData(TaskModel taskModel, BaseLingualCommand allAttributesCommand, Long projectId) {

	String sourceId = allAttributesCommand.getSourceId();

	Term sourceTerm = getSubmissionTermService().findById(sourceId, projectId);

	String sourceLanguageCode = sourceTerm.getLanguageId();
	addLanguageToModel(taskModel, sourceLanguageCode, "sourceLanguage", "sourceIsRTL");

	Set<Description> sourceDescriptions = getDescriptionByBaseType(sourceTerm, ATTRIBUTE);
	taskModel.addObject("sourceDescriptions", DescriptionConverter.fromInternalToDtoExtended(sourceDescriptions));
	Set<Description> sourceNotes = getDescriptionByBaseType(sourceTerm, NOTE);
	taskModel.addObject("sourceNotes", DescriptionConverter.fromInternalToDtoExtended(sourceNotes));
    }

    private void handleTargetTermData(TaskModel taskModel, BaseLingualCommand allAttributesCommand, Long projectId) {
	Map<String, TargetDescriptionModel> targetDescriptions = new HashMap<String, TargetDescriptionModel>();

	List<TranslationViewCommand> targetTermsCommand = allAttributesCommand.getTargetTerms();
	if (CollectionUtils.isNotEmpty(targetTermsCommand)) {
	    for (TranslationViewCommand translationViewCommand : targetTermsCommand) {
		String targetId = translationViewCommand.getTargetId();
		Term targetTerm = getSubmissionTermService().findById(targetId, projectId);

		String targetLanguageCode = targetTerm.getLanguageId();
		Locale targetLocale = Locale.makeLocale(targetLanguageCode);
		String targetLanguage = targetLocale.getDisplayName();

		boolean commited = targetTerm.getCommited() != null ? targetTerm.getCommited() : Boolean.TRUE;
		TargetDescriptionModel targetDescriptionModel = new TargetDescriptionModel();
		targetDescriptionModel.setTargetLanguage(targetLanguage);

		boolean isRTL = targetLocale.isRTL();
		targetDescriptionModel.setTargetIsRTL(isRTL);

		Set<Description> targetDesc = getDescriptionByBaseType(targetTerm, ATTRIBUTE);
		targetDescriptionModel
			.setTargetDescriptions(DescriptionConverter.fromInternalToDtoExtended(targetDesc, commited));

		Set<Description> targetNotes = getDescriptionByBaseType(targetTerm, NOTE);
		targetDescriptionModel
			.setTargetNotes(DescriptionConverter.fromInternalToDtoExtended(targetNotes, commited));

		targetDescriptions.put(MultilingualTermConverter.replaceLocaleCodeDash(targetLanguageCode),
			targetDescriptionModel);
	    }

	    taskModel.addObject("targetDescriptions", targetDescriptions);
	}
    }
}
