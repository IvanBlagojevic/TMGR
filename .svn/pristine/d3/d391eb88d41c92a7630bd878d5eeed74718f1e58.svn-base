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
import org.gs4tr.termmanager.model.search.ItemPaneEnum;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.gs4tr.termmanager.service.SubmissionLanguageDetailViewService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("submissionLanguageDetailSearchController")
public class SubmissionLanguageDetailSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String URL = UrlConstants.SUBMISSION_LANGUAGE_DETAIL_SEARCH;

    @ClientBean
    private SubmissionLanguageDetailViewService _submissionLanguageDetailViewService;

    public SubmissionLanguageDetailViewService getSubmissionLanguageDetailViewService() {
	return _submissionLanguageDetailViewService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("submissionLanguageDetailSearch")
    @TestUser(roleName = RoleNameEnum.SYSTEM_TRANSLATOR_USER)
    public void submissionLanguageDetailSearchTest() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<SubmissionLanguageDetailView> taskPagedList = getModelObject("taskPagedList",
		TaskPagedList.class);

	when(getSubmissionLanguageDetailViewService().search(any(SubmissionLanguageDetailRequest.class),
		any(PagedListInfo.class))).thenReturn(taskPagedList);

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("ticket", IdEncrypter.encryptGenericId(1));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getSubmissionLanguageDetailViewService()).search(any(SubmissionLanguageDetailRequest.class),
		any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {
	ItemStatusType inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

	String encryptGenericId = IdEncrypter.encryptGenericId(1);
	JSONValidator submissionLanguageDetailView = dtoEntities.getObjectFromArray("ticket", encryptGenericId);

	submissionLanguageDetailView.assertProperty("assignee", "translator").assertProperty("languageId", "de-DE")
		.assertProperty("termCanceledCount", "0").assertProperty("termCompletedCount", "0")
		.assertProperty("termCount", "1").assertProperty("termInFinalReviewCount", "0")
		.assertProperty("termInTranslationCount", "1").assertProperty("dateCompleted", "0")
		.assertProperty("canceled", "false").assertProperty("dateSubmitted", "1448352947000")
		.assertProperty("dateModified", "1448352948000")
		.assertProperty("status", inTranslationReview.getName());
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	unionTasks.assertArrayFinish();
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator languageId = gridConfig.getObjectFromArray("dataIndex", "languageId");
	languageId.assertProperty("header", "languageNameDetails").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "100");

	JSONValidator status = gridConfig.getObjectFromArray("dataIndex", "status");
	status.assertProperty("header", "status").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "40");

	JSONValidator termCount = gridConfig.getObjectFromArray("dataIndex", "termCount");
	termCount.assertProperty("header", "termCount").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "135");

	JSONValidator termInTranslationCount = gridConfig.getObjectFromArray("dataIndex", "termInTranslationCount");
	termInTranslationCount.assertProperty("header", "termInTranslationCountDetails")
		.assertProperty("sortProperty", StringUtils.EMPTY).assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135");

	JSONValidator termInFinalReviewCount = gridConfig.getObjectFromArray("dataIndex", "termInFinalReviewCount");
	termInFinalReviewCount.assertProperty("header", "inFinalReviewCountDetails")
		.assertProperty("sortProperty", StringUtils.EMPTY).assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135");

	JSONValidator termCompletedCount = gridConfig.getObjectFromArray("dataIndex", "termCompletedCount");
	termCompletedCount.assertProperty("header", "completedCountDetails")
		.assertProperty("sortProperty", StringUtils.EMPTY).assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135");

	JSONValidator termCanceledCount = gridConfig.getObjectFromArray("dataIndex", "termCanceledCount");
	termCanceledCount.assertProperty("header", "canceledCountDetails")
		.assertProperty("sortProperty", StringUtils.EMPTY).assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135");

	JSONValidator assignee = gridConfig.getObjectFromArray("dataIndex", "assignee");
	assignee.assertProperty("header", "assignee").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "240");

	JSONValidator comments = gridConfig.getObjectFromArray("dataIndex", "comments");
	comments.assertProperty("header", "comments").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "40");

	JSONValidator dateSubmitted = gridConfig.getObjectFromArray("dataIndex", "dateSubmitted");
	dateSubmitted.assertProperty("header", "submittedDate").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "114");

	JSONValidator dateModified = gridConfig.getObjectFromArray("dataIndex", "dateModified");
	dateModified.assertProperty("header", "modificationDate").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "114");

	JSONValidator dateCompleted = gridConfig.getObjectFromArray("dataIndex", "dateCompleted");
	dateCompleted.assertProperty("header", "completedDate").assertProperty("sortProperty", StringUtils.EMPTY)
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "114");
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", "false");
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("totalPageCount", "1").assertProperty("hasPrevious", "false")
		.assertProperty("hasNext", "false").assertProperty("totalCount", "1")
		.assertPropertyNull("totalCountPerSource").assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedListInfo = gridContentInfo.getObject("pagedListInfo");

	pagedListInfo.assertProperty("sortDirection", "ASCENDING").assertProperty("ascending", "true")
		.assertProperty("index", "0").assertProperty("indexesSize", "5").assertProperty("size", "25")
		.assertPropertyNull("sortProperty");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "languageId"));
	assertNotNull(readerColumns.getObjectFromArray("name", "status"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termInTranslationCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termInFinalReviewCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termCompletedCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termCanceledCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "assignee"));
	assertNotNull(readerColumns.getObjectFromArray("name", "comments"));

	assertNotNull(readerColumns.getObjectFromArray("name", "dateSubmitted"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateModified"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateCompleted"));

    }

    @Override
    protected String getMetadataFolderPath() {
	return null;
    }

    @Override
    protected String getMetadataKey() {
	return ItemPaneEnum.SUBMISSIONLANGUAGEDETAILS.name().toLowerCase();
    }
}
