package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createGetRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createMultipartRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.filters.AppVersionValidationFilter;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation.modules.webmvc.test.model.FilePart;
import org.gs4tr.foundation.modules.webmvc.test.model.ParameterPart;
import org.gs4tr.foundation.modules.webmvc.test.model.Part;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.UserInfoConverter;
import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.manualtask.AutoSaveTermManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.AssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.BaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAutoSaveTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoBaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoUserCommand;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.webmvc.Version;
import org.gs4tr.termmanager.webmvc.controllers.DownloadUtils;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.commands.TaskCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@TestSuite("taskController")
public class TaskControllerTest extends AbstractMvcTest {

    private static final String TASK_POST_URL = "taskPost.ter";

    private static final String URL = "task.ter";

    @ClientBean
    private PluginsService _pluginsService;

    @Mock
    private ManualTaskHandler _taskHandler;

    @Captor
    protected ArgumentCaptor<List<UploadedRepositoryItem>> _captor;

    public PluginsService getPluginsService() {
	return _pluginsService;
    }

    public ManualTaskHandler getTaskHandler() {
	return _taskHandler;
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    @TestCase("onSubmitMultipartRequest")
    public void onSubmitMultipartRequestTest() throws Exception {

	TaskCommand command = getModelObject("taskCommand", TaskCommand.class);

	RepositoryItem repositoryItem = getModelObject("repositoryItem", RepositoryItem.class);
	TaskResponse taskResponse = getModelObject("taskResponse", TaskResponse.class);

	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(getTaskHandler());
	when(getTaskHandler().processTasks(any(Long[].class), any(Long[].class),
		any(AssignTermEntryAttributesCommand.class), anyListOf(UploadedRepositoryItem.class)))
			.thenReturn(taskResponse);

	Answer<?> answer = new Answer<Class<DtoAssignTermEntryAttributesCommand>>() {
	    @Override
	    public Class<DtoAssignTermEntryAttributesCommand> answer(InvocationOnMock invocation) throws Throwable {
		return DtoAssignTermEntryAttributesCommand.class;
	    }
	};
	when(getTaskHandler().getCommandClass()).thenAnswer(answer);

	List<Part> parts = new ArrayList<Part>();

	FilePart filePart = new FilePart(repositoryItem.getResourceInfo().getName(), repositoryItem.getInputStream());
	parts.add(filePart);

	ParameterPart parameterPart = new ParameterPart("jsonData", command.getJsonTaskData());
	parts.add(parameterPart);

	Request request = createMultipartRequest(URL, parts, getSessionParameters());

	request.setHeader(AppVersionValidationFilter.APP_VERSION_PARAM, Version.getVersion());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	assertTrue(StringUtils.equalsIgnoreCase(DownloadUtils.DEFAULT_MIME_TYPE, response.get(CONTENT_TYPE_KEY)));

	verify(getTaskHandler()).processTasks(any(Long[].class), any(Long[].class),
		any(AssignTermEntryAttributesCommand.class), _captor.capture());

	List<UploadedRepositoryItem> repositoryItems = _captor.getValue();

	UploadedRepositoryItem uploadedRepositoryItem = repositoryItems.get(0);
	String nameParameter = uploadedRepositoryItem.getNameParameter();
	assertEquals("file", nameParameter);

	RepositoryItem item = uploadedRepositoryItem.getRepositoryItem();
	assertNotNull(item.getInputStream());

	ResourceInfo resourceInfo = item.getResourceInfo();
	assertEquals("application/octet-stream", resourceInfo.getMimeType());
	assertEquals("15-beach-sea-photography.jpg", resourceInfo.getName());

	JSONValidator responseContent = new JSONValidator(response.getContent());
	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator taskResponseJson = responseContent.getObject("taskResponse");
	JSONValidator responseTicket = taskResponseJson.getObject("responseTicket");
	responseTicket.assertProperty("ticketId", taskResponse.getResponseTicket().getTicketId());
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	reset(getTaskHandler());
    }

    @Test
    @TestCase("showEditUserForm")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void showEditUserFormTest() throws Exception {

	TaskModel taskModel = getModelObject("taskModel", TaskModel.class);

	TaskCommand command = getModelObject("taskCommand", TaskCommand.class);

	final UserInfo userInfo = getCurrentUserProfile().getUserInfo();
	taskModel.addObject("userInfo", UserInfoConverter.fromInternalToDto(userInfo));

	Answer<Class<DtoUserCommand>> answer = new Answer<Class<DtoUserCommand>>() {
	    @Override
	    public Class<DtoUserCommand> answer(InvocationOnMock invocation) throws Throwable {
		return DtoUserCommand.class;
	    }
	};

	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(getTaskHandler());
	when(getTaskHandler().getTaskInfos(any(Long[].class), anyString(), any(DtoTaskHandlerCommand.class)))
		.thenReturn(new TaskModel[] { taskModel });

	when(getTaskHandler().getCommandClass()).thenAnswer(answer);

	Map<String, String> parameters = new HashMap<String, String>(1);
	String commandJson = OBJECT_MAPPER.writeValueAsString(command);
	parameters.put(JSON_DATA_KEY, commandJson);

	Request request = createPostRequest(TASK_POST_URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator taskInfos = responseContent.getObject("taskInfos");

	JSONValidator taskInfo = taskInfos.getObjectFromArray("parentEntityTicket", "null");

	JSONValidator model = taskInfo.getObject("model");
	model.assertProperty("password", StringUtils.EMPTY).assertProperty("generic", "false")
		.assertProperty("ssoEnabled", "false");

	JSONValidator userInfoJson = model.getObject("userInfo");
	userInfoJson.assertProperty("userName", "sdulin").assertProperty("accountNonExpired", "true")
		.assertProperty("userType", "ORGANIZATION").assertProperty("enabled", "true")
		.assertProperty("credentialsNonExpired", "true").assertProperty("isSamlUser", "false")
		.assertProperty("autoClaimMultipleTasks", "false").assertProperty("claimMultipleJobTasks", "false");
    }

    @Test
    @TestCase("viewPropertiesGet")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void showFormGetTest() throws Exception {
	TaskModel[] taskModels = getModelObject("taskModels", TaskModel[].class);

	Answer<?> answer = new Answer<Class<DtoBaseLingualCommand>>() {
	    @Override
	    public Class<DtoBaseLingualCommand> answer(InvocationOnMock invocation) throws Throwable {
		return DtoBaseLingualCommand.class;
	    }
	};
	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(getTaskHandler());
	when(getTaskHandler().getTaskInfos(any(Long[].class), anyString(), any(BaseLingualCommand.class)))
		.thenReturn(taskModels);

	when(getTaskHandler().getCommandClass()).thenAnswer(answer);

	Request request = createGetRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator taskInfos = responseContent.getObject("taskInfos");

	JSONValidator taskInfo = taskInfos.getObjectFromArray("taskTicket", "null");
	JSONValidator model = taskInfo.getObject("model");

	model.getObject("sourceNotes").assertArrayFinish().getObject("sourceDescriptions").assertArrayFinish()
		.getObject("allAttributes").assertArrayFinish().getObject("allNotes").assertArrayFinish();
	model.assertProperty("sourceIsRTL", String.valueOf(false));

	JSONValidator history = model.getObject("history");
	JSONValidator historyItem = history.getObjectFromArray("user", "sdulin");
	historyItem.assertProperty("bold", "true").assertProperty("isRTL", "false").assertProperty("fieldName", "TERM")
		.assertProperty("newValue", "new source term").assertProperty("newStatus", "PROCESSED")
		.assertProperty("path", "English").assertProperty("oldStatus", StringUtils.EMPTY);
	assertNotNull(historyItem.getObject("date"));
    }

    @Test
    @TestCase("showImportForm")
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void showFormPostTest() throws Exception {

	TaskModel[] taskModels = getModelObject("taskModels", TaskModel[].class);

	Answer<?> answer = new Answer<Class<DtoImportCommand>>() {
	    @Override
	    public Class<DtoImportCommand> answer(InvocationOnMock invocation) throws Throwable {
		return DtoImportCommand.class;
	    }
	};
	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(getTaskHandler());
	when(getTaskHandler().getTaskInfos(any(Long[].class), anyString(), any(ImportCommand.class)))
		.thenReturn(taskModels);

	when(getTaskHandler().getCommandClass()).thenAnswer(answer);

	String inputJson = getJsonData("importJsonData.json");
	Map<String, String> parameters = new HashMap<>(1);
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(TASK_POST_URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator taskInfos = responseContent.getObject("taskInfos");
	JSONValidator taskInfo = taskInfos.getObjectFromArray("taskTicket", "null");

	JSONValidator model = taskInfo.getObject("model");

	model.assertProperty("defaultImportType", SyncOption.OVERWRITE.name()).assertProperty("ignoreCase", "true");

	JSONValidator importTypes = model.getObject("importTypes");
	importTypes.assertSimpleArrayElement(SyncOption.APPEND.name());
	importTypes.assertSimpleArrayElement(SyncOption.OVERWRITE.name());

	JSONValidator projects = model.getObject("projects");
	JSONValidator skype = projects.getObjectFromArray("projectName", "Skype");
	assertNotNull(skype);

	JSONValidator projectTicket = skype.getObject("projectTicket");
	projectTicket.assertProperty("ticketId", IdEncrypter.encryptGenericId(1));

	JSONValidator projectLanguages = skype.getObject("projectLanguages");
	assertNotNull(projectLanguages);

	JSONValidator defaultTermStatus = skype.getObject("defaultTermStatus");
	defaultTermStatus.assertProperty("name", ItemStatusTypeHolder.PROCESSED.getName());
	defaultTermStatus.assertProperty("value", ItemStatusTypeHolder.PROCESSED.getName());

	JSONValidator termStatuses = skype.getObject("termStatuses");

	assertNotNull(termStatuses.getObjectFromArray("name", ItemStatusTypeHolder.PROCESSED.getName()));
	assertNotNull(termStatuses.getObjectFromArray("name", ItemStatusTypeHolder.WAITING.getName()));
    }

    @Test
    @TestCase("autosaveTranslation")
    @TestUser(roleName = RoleNameEnum.SYSTEM_TRANSLATOR_USER)
    public void showFormPostTest_1() throws Exception {

	TaskCommand command = getModelObject("taskCommand", TaskCommand.class);

	Answer<?> answer = new Answer<Class<DtoAutoSaveTermCommands>>() {
	    @Override
	    public Class<DtoAutoSaveTermCommands> answer(InvocationOnMock invocation) throws Throwable {
		return DtoAutoSaveTermCommands.class;
	    }
	};
	when(getPluginsService().getUserTaskHandler(anyString())).thenReturn(getTaskHandler());
	when(getTaskHandler().processTasks(any(Long[].class), any(Long[].class),
		any(AutoSaveTermManualTaskHandler.class), anyListOf(UploadedRepositoryItem.class)))
			.thenReturn(new TaskResponse(null));

	when(getTaskHandler().getCommandClass()).thenAnswer(answer);

	Map<String, String> parameters = new HashMap<String, String>(1);
	String commandJson = OBJECT_MAPPER.writeValueAsString(command);
	parameters.put(JSON_DATA_KEY, commandJson);

	Request request = createPostRequest("task.ter", null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator taskResponse = responseContent.getObject("taskResponse");
	assertNotNull(taskResponse);
	assertNotNull(taskResponse.getObject("model"));
    }
}
