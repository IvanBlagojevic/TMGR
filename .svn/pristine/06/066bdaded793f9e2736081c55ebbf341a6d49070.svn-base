package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

public abstract class AbstractSearchGridControllerTest extends AbstractMvcTest {

    private static final String GRID_CONFIG = "gridConfig";
    private static final String GRID_CONTENT_INFO = "gridContentInfo";
    private static final String ITEMS_KEY = "items";
    private static final String READER_COLUMNS = "readerColumns";
    private static final String READER_CONFIG = "readerConfig";
    private static final String TASKS_KEY = "tasks";

    static final String TASK_NAME = "taskName";

    @Captor
    protected ArgumentCaptor<OrganizationSearchRequest> _captor;

    @Override
    public void beforeTest() throws Exception {
	super.beforeTest();
	MockitoAnnotations.initMocks(this);
    }

    protected abstract void assertAllProjectsSearch(JSONValidator responseContent);

    protected abstract void assertDtoEntities(JSONValidator dtoEntities);

    protected abstract void assertDtoUnionTasks(JSONValidator dtoUnionTasks);

    protected abstract void assertGridConfig(JSONValidator gridConfig);

    final void assertGridConfigContent(JSONValidator responseContent) {

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	assertGridContentInfo(responseContent.getObject(GRID_CONTENT_INFO));

	assertNotNull(responseContent.getObject(READER_CONFIG));

	assertReaderColumns(responseContent.getObject(READER_COLUMNS));

	assertGridConfig(responseContent.getObject(GRID_CONFIG));

	assertDtoEntities(responseContent.getObject(ITEMS_KEY));

	assertDtoUnionTasks(responseContent.getObject(TASKS_KEY));

	assertGridConfigFromMetadata(responseContent);

	assertAllProjectsSearch(responseContent);
    }

    protected abstract void assertGridConfigFromMetadata(JSONValidator responseContent);

    protected abstract void assertGridContentInfo(JSONValidator gridContentInfo);

    protected abstract void assertReaderColumns(JSONValidator readerColumns);

    protected abstract String getMetadataFolderPath();

    protected abstract String getMetadataKey();

    @Override
    TmUserProfile initializeTmUserProfile(String methodName) {
	TmUserProfile currentUserProfile = super.initializeTmUserProfile(methodName);
	String metadataFolderPath = getMetadataFolderPath();
	if (StringUtils.isBlank(metadataFolderPath)) {
	    return currentUserProfile;
	}
	String jsonMetadata = getJsonData(metadataFolderPath);
	Metadata metadata = new Metadata(getMetadataKey(), jsonMetadata);
	currentUserProfile.setMetadata(Arrays.asList(metadata));

	return currentUserProfile;
    }
}
