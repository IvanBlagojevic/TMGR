package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.termmanager.service.SubmissionDetailViewService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("submissionDetailSearchController")
public class SubmissionDetailSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String SUBMISSION_DETAILS_FOLDER_METADATA_JSON = "submissionDetailsFolderMetadata.json";

    private static final String URL = UrlConstants.SUBMISSION_DETAIL_SEARCH;

    @ClientBean
    private SubmissionDetailViewService _submissionDetailViewService;

    public SubmissionDetailViewService getSubmissionDetailViewService() {
	return _submissionDetailViewService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("submissionDetailSearch")
    @TestUser(roleName = RoleNameEnum.SYSTEM_TRANSLATOR_USER)
    public void submissionDetailSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<SubmissionDetailView> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	SubmissionDetailViewService detailViewService = getSubmissionDetailViewService();
	when(detailViewService.search(any(SubmissionSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String inputJson = getJsonData("submissionDetailSearch.json");

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(detailViewService).search(any(SubmissionSearchRequest.class), any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {
	ItemStatusType inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

	final String encryptGenericId = IdEncrypter.encryptGenericId(1);

	JSONValidator submissionDetailView = dtoEntities.getObjectFromArray("ticket", encryptGenericId);
	submissionDetailView.assertProperty("assignees", "translator").assertProperty("projectName", "Skype")
		.assertProperty("sourceLanguageId", "en-US").assertProperty("submissionId", "1")
		.assertProperty("submissionName", "Test submission").assertProperty("submitter", "super")
		.assertProperty("targetLanguageIds", "de-DE").assertProperty("termEntryCount", "1")
		.assertProperty("canceled", "false").assertProperty("dateSubmitted", "1448352947000")
		.assertProperty("dateModified", "1448352948000").assertProperty("dateCompleted", "0")
		.assertProperty("projectTicket", encryptGenericId)
		.assertProperty("status", inTranslationReview.getName());
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "add comment"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "cancel translation"));
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator submissionName = gridConfig.getObjectFromArray("dataIndex", "submissionName");
	submissionName.assertProperty("sortProperty", "sub.name").assertProperty("header", "submissionName")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "240");

	JSONValidator submissionId = gridConfig.getObjectFromArray("dataIndex", "submissionId");
	submissionId.assertProperty("sortProperty", "sub.submissionId").assertProperty("header", "submissionId")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "30");

	JSONValidator status = gridConfig.getObjectFromArray("dataIndex", "status");
	status.assertProperty("header", "status").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "40")
		.assertProperty("sortProperty", "entityStatusPriority.status");

	JSONValidator projectName = gridConfig.getObjectFromArray("dataIndex", "projectName");
	projectName.assertProperty("header", "projectName").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "240")
		.assertProperty("sortProperty", "project.projectInfo.name");

	JSONValidator sourceLanguageId = gridConfig.getObjectFromArray("dataIndex", "sourceLanguageId");
	sourceLanguageId.assertProperty("header", "sourceLanguage").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "122")
		.assertProperty("sortProperty", "sub.sourceLanguageId");

	JSONValidator targetLanguageIds = gridConfig.getObjectFromArray("dataIndex", "targetLanguageIds");
	targetLanguageIds.assertProperty("header", "targetLanguages").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "174")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator termEntryCount = gridConfig.getObjectFromArray("dataIndex", "termEntryCount");
	termEntryCount.assertProperty("header", "termEntryCount").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator submitter = gridConfig.getObjectFromArray("dataIndex", "submitter");
	submitter.assertProperty("header", "submitter").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "151")
		.assertProperty("sortProperty", "sub.submitter");

	JSONValidator assignees = gridConfig.getObjectFromArray("dataIndex", "assignees");
	assignees.assertProperty("header", "assignees").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "178")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator comments = gridConfig.getObjectFromArray("dataIndex", "comments");
	comments.assertProperty("header", "comments").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "40")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator dateSubmitted = gridConfig.getObjectFromArray("dataIndex", "dateSubmitted");
	dateSubmitted.assertProperty("header", "submittedDate").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "114")
		.assertProperty("sortProperty", "sub.dateSubmitted");

	JSONValidator dateModified = gridConfig.getObjectFromArray("dataIndex", "dateModified");
	dateModified.assertProperty("header", "modificationDate").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "114")
		.assertProperty("sortProperty", "sub.dateModified");
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", "true");
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("hasPrevious", "false").assertProperty("hasNext", "false")
		.assertProperty("totalCount", "1").assertProperty("totalPageCount", "1")
		.assertPropertyNull("totalCountPerSource").assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedListInfo = gridContentInfo.getObject("pagedListInfo");

	pagedListInfo.assertProperty("ascending", "true").assertProperty("index", "0")
		.assertProperty("indexesSize", "5").assertProperty("size", "50")
		.assertProperty("sortDirection", "ASCENDING");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "submissionId"));
	assertNotNull(readerColumns.getObjectFromArray("name", "status"));
	assertNotNull(readerColumns.getObjectFromArray("name", "projectName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "submitter"));
	assertNotNull(readerColumns.getObjectFromArray("name", "assignees"));
	assertNotNull(readerColumns.getObjectFromArray("name", "comments"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateSubmitted"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateModified"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateCompleted"));

	assertNotNull(readerColumns.getObjectFromArray("name", "sourceLanguageId"));
	assertNotNull(readerColumns.getObjectFromArray("name", "targetLanguageIds"));
	assertNotNull(readerColumns.getObjectFromArray("name", "submissionName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termEntryCount"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return SUBMISSION_DETAILS_FOLDER_METADATA_JSON;
    }

    @Override
    protected String getMetadataKey() {
	return ItemFolderEnum.SUBMISSIONDETAILS.name().toLowerCase();
    }
}
