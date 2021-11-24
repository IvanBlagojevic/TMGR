package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.impl.ExportDocumentStatusInfo;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.commands.CancelDownloadExportCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Qualifier;

@TestSuite("cancelDownloadExportController")
public class CancelDownloadExportControllerTest extends AbstractMvcTest {

    private static final String URL = "cancelDownloadExport.ter";

    @ClientBean(name = "hzCacheGateway")
    private CacheGateway<String, ExportAdapter> _cacheGateway;

    @Test
    @TestCase("cancelDownloadExportCommand")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void cancelDownloadExportTest() throws JsonProcessingException {

	CancelDownloadExportCommand command = getModelObject("command", CancelDownloadExportCommand.class);

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(command));

	String threadName = command.getThreadName();

	getCacheGateway().put(CacheName.EXPORT_PROGRESS_STATUS, threadName, new ExportAdapter());

	HttpTester.Request request = createPostRequest(URL, parameters, getSessionParameters());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	ExportDocumentStatusInfo statusInfo = getCacheGateway().get(CacheName.EXPORT_PROGRESS_STATUS, threadName);

	assertNull(statusInfo);

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));
    }

    public CacheGateway<String, ExportAdapter> getCacheGateway() {
	return _cacheGateway;
    }

}
