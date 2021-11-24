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
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.search.ItemPaneEnum;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("projectLanguageDetailSearchController")
public class ProjectLanguageDetailSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String URL = UrlConstants.PROJECT_LANGUAGE_DETAIL_SEARCH;

    @ClientBean
    private ProjectLanguageDetailService _projectLanguageDetailService;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectLanguageDetailSearch")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void projectSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<ProjectLanguageDetailView> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	when(getProjectLanguageDetailService().search(any(), any())).thenReturn(taskPagedList);

	Map<String, String> parameters = new HashMap<>();
	parameters.put("ticket", IdEncrypter.encryptGenericId(1));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectLanguageDetailService()).search(any(ProjectLanguageDetailRequest.class),
		any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    private ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {
	final String zero = String.valueOf(0);

	JSONValidator englishView = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(1));
	englishView.assertProperty("termCount", zero);
	englishView.assertProperty("activeSubmissionCount", zero);
	englishView.assertProperty("completedSubmissionCount", zero);
	englishView.assertProperty("approvedTermCount", zero);
	englishView.assertProperty("forbiddenTermCount", zero);
	englishView.assertProperty("termInSubmissionCount", zero);

	assertNotNull(englishView.getObject("dateModified"));

	JSONValidator english = englishView.getObject("language");
	english.assertProperty("locale", Locale.US.getCode());

	JSONValidator germanyView = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(2));
	germanyView.assertProperty("termCount", zero);
	germanyView.assertProperty("activeSubmissionCount", zero);
	germanyView.assertProperty("completedSubmissionCount", zero);
	germanyView.assertProperty("approvedTermCount", zero);
	germanyView.assertProperty("forbiddenTermCount", zero);
	germanyView.assertProperty("termInSubmissionCount", zero);

	assertNotNull(germanyView.getObject("dateModified"));

	JSONValidator germany = germanyView.getObject("language");
	germany.assertProperty("locale", Locale.GERMANY.getCode());

	JSONValidator frenchView = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(3));
	frenchView.assertProperty("termCount", zero);
	frenchView.assertProperty("activeSubmissionCount", zero);
	frenchView.assertProperty("completedSubmissionCount", zero);
	frenchView.assertProperty("approvedTermCount", zero);
	frenchView.assertProperty("forbiddenTermCount", zero);
	frenchView.assertProperty("termInSubmissionCount", zero);

	assertNotNull(frenchView.getObject("dateModified"));

	JSONValidator french = frenchView.getObject("language");
	french.assertProperty("locale", Locale.FRANCE.getCode());
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator dtoUnionTasks) {
	dtoUnionTasks.assertArrayFinish();
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator languageNameDetails = gridConfig.getObjectFromArray("dataIndex", "language");
	languageNameDetails.assertProperty("header", "languageNameDetails").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "640")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator termCount = gridConfig.getObjectFromArray("dataIndex", "termCount");
	termCount.assertProperty("header", "termCount").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator approvedTermCount = gridConfig.getObjectFromArray("dataIndex", "approvedTermCount");
	approvedTermCount.assertProperty("header", "approvedTermCountDetails").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator forbiddenTermCount = gridConfig.getObjectFromArray("dataIndex", "forbiddenTermCount");
	forbiddenTermCount.assertProperty("header", "forbiddenTermCountDetails").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator termInSubmissionCount = gridConfig.getObjectFromArray("dataIndex", "termInSubmissionCount");
	termInSubmissionCount.assertProperty("header", "termInSubmissionCountDetails")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator activeSubmissionCount = gridConfig.getObjectFromArray("dataIndex", "activeSubmissionCount");
	activeSubmissionCount.assertProperty("header", "activeSubmissionCount").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator completedSubmissionCount = gridConfig.getObjectFromArray("dataIndex", "completedSubmissionCount");
	completedSubmissionCount.assertProperty("header", "completedSubmissionCount")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "false").assertProperty("width", "135")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator dateModified = gridConfig.getObjectFromArray("dataIndex", "dateModified");
	dateModified.assertProperty("header", "modificationDate").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "114")
		.assertProperty("sortProperty", StringUtils.EMPTY);
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", String.valueOf(false));
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("hasPrevious", String.valueOf(false));
	gridContentInfo.assertProperty("hasNext", String.valueOf(false));
	gridContentInfo.assertProperty("totalCount", String.valueOf(3));
	gridContentInfo.assertProperty("totalPageCount", String.valueOf(1));

	gridContentInfo.assertPropertyNull("totalCountPerSource");
	gridContentInfo.assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedList = gridContentInfo.getObject("pagedListInfo");

	pagedList.assertProperty("ascending", String.valueOf(true));
	pagedList.assertProperty("index", String.valueOf(0));
	pagedList.assertProperty("indexesSize", String.valueOf(5));
	pagedList.assertProperty("size", String.valueOf(1000));
	pagedList.assertProperty("sortDirection", SortDirection.ASCENDING.name());
	pagedList.assertProperty("sortProperty", "null");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "language"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "approvedTermCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "forbiddenTermCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "termInSubmissionCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "activeSubmissionCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "completedSubmissionCount"));
	assertNotNull(readerColumns.getObjectFromArray("name", "dateModified"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return null;
    }

    @Override
    protected String getMetadataKey() {
	return ItemPaneEnum.PROJECTLANGUAGEDETAILS.name().toLowerCase();
    }
}
