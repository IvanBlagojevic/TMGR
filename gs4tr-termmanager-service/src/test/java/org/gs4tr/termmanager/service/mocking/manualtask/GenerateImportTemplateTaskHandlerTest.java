package org.gs4tr.termmanager.service.mocking.manualtask;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.manualtask.GenerateImportTemplateTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.GenerateImportTemplateCommand;
import org.gs4tr.termmanager.service.xls.XlsReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class GenerateImportTemplateTaskHandlerTest extends AbstractManualtaskTest {

    private static final String LANGUAGES = "languages";
    private static final String TASK_NAME = "generate import template";

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private GenerateImportTemplateTaskHandler _taskHandler;

    private TaskResponse _taskResponse;

    @After
    public void callCleanupCallback() {
	if (Objects.nonNull(_taskResponse)) {
	    RepositoryItem repositoryItem = _taskResponse.getRepositoryItem();
	    repositoryItem.getCleanupCallback().cleanup();
	}
    }

    @Test
    @TestCase("generateImportTemplate")
    public void generateImportReportWithMultipleTEAttributesMultipleLanguagesMultipleTermAttributesAndMultipleNotesTest() {
	GenerateImportTemplateCommand command = getModelObject("command2", GenerateImportTemplateCommand.class);

	TmProject tmProject = getModelObject("tmProject3", TmProject.class);

	List<String> expectedHeaders = getModelObject("expectedHeaders3", List.class);

	when(getProjectService().findProjectById(command.getProjectId(), Attribute.class)).thenReturn(tmProject);

	_taskResponse = getTaskHandler().processTasks(null, null, command, null);

	verify(getProjectService()).findProjectById(command.getProjectId(), Attribute.class);

	String templateName = _taskResponse.getResponseTicket().getTicketId();
	assertTrue(templateName.contains(tmProject.getProjectInfo().getName()));
	assertTrue(templateName.contains(tmProject.getProjectInfo().getShortCode()));

	XlsReader reader = new XlsReader(_taskResponse.getRepositoryItem().getInputStream(), true, true);
	List<String> headerColumns = reader.getHeaderColumns();

	assertTrue(CollectionUtils.isEqualCollection(headerColumns, expectedHeaders));
    }

    @Test
    @TestCase("generateImportTemplate")
    public void generateImportReportWithOneLanguageOneTermAttributeAndOneNoteTest() {
	GenerateImportTemplateCommand command = getModelObject("command1", GenerateImportTemplateCommand.class);

	TmProject tmProject = getModelObject("tmProject2", TmProject.class);

	List<String> expectedHeaders = getModelObject("expectedHeaders2", List.class);

	when(getProjectService().findProjectById(command.getProjectId(), Attribute.class)).thenReturn(tmProject);

	_taskResponse = getTaskHandler().processTasks(null, null, command, null);

	verify(getProjectService()).findProjectById(command.getProjectId(), Attribute.class);

	String templateName = _taskResponse.getResponseTicket().getTicketId();
	assertTrue(templateName.contains(tmProject.getProjectInfo().getName()));
	assertTrue(templateName.contains(tmProject.getProjectInfo().getShortCode()));

	XlsReader reader = new XlsReader(_taskResponse.getRepositoryItem().getInputStream(), true, true);
	List<String> headerColumns = reader.getHeaderColumns();

	assertTrue(CollectionUtils.isEqualCollection(headerColumns, expectedHeaders));
    }

    @Test
    @TestCase("generateImportTemplate")
    public void generateImportReportWithTermEntryAttributesAndOneLanguageTest() {
	GenerateImportTemplateCommand command = getModelObject("command1", GenerateImportTemplateCommand.class);

	TmProject tmProject = getModelObject("tmProject1", TmProject.class);

	List<String> expectedHeaders = getModelObject("expectedHeaders1", List.class);

	when(getProjectService().findProjectById(command.getProjectId(), Attribute.class)).thenReturn(tmProject);

	_taskResponse = getTaskHandler().processTasks(null, null, command, null);

	verify(getProjectService()).findProjectById(command.getProjectId(), Attribute.class);

	String templateName = _taskResponse.getResponseTicket().getTicketId();
	assertTrue(templateName.contains(tmProject.getProjectInfo().getName()));
	assertTrue(templateName.contains(tmProject.getProjectInfo().getShortCode()));

	XlsReader reader = new XlsReader(_taskResponse.getRepositoryItem().getInputStream(), true, true);
	List<String> headerColumns = reader.getHeaderColumns();

	assertTrue(CollectionUtils.isEqualCollection(headerColumns, expectedHeaders));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getTaskInfosTest() {
	TaskModel taskModel = getTaskHandler().getTaskInfos(null, TASK_NAME, null)[0];

	List<Language> targetLanguages = (List<Language>) taskModel.getModel().get(LANGUAGES);

	List<String> targetLanguageCodes = getLanguageCodes(targetLanguages, Language::getLocale);
	List<String> allLanguageCodes = getLanguageCodes(
		org.gs4tr.termmanager.model.Language.getAllAvailableLanguages(),
		org.gs4tr.termmanager.model.Language::getLanguageId);

	assertTrue(CollectionUtils.isEqualCollection(targetLanguageCodes, allLanguageCodes));
    }

    @Before
    public void resetProjectService() {
	reset(getProjectService());
    }

    private <T> List<String> getLanguageCodes(Collection<T> languages, Function<T, String> mapper) {
	return languages.stream().map(mapper).collect(toList());
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private GenerateImportTemplateTaskHandler getTaskHandler() {
	return _taskHandler;
    }
}
