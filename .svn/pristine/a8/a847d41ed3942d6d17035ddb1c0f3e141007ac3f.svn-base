package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.service.UuidGeneratorService;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class GetUUIDControllerTest extends AbstractMvcTest {

    private static final String CONTORLLER_MAPPING = "getUUIDs.ter";

    private static final int DEFAULT_NUMBER_OF_UUIDS = 10;

    @ClientBean
    private UuidGeneratorService _uuidGeneratorService;

    public String[] generateUUID(int number) {
	final String[] uuids = new String[number];
	for (int i = 0; i < number; i++) {
	    uuids[i] = UUID.randomUUID().toString();
	}
	return uuids;
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void getDefaultNumberOfUuidsTest() throws JsonProcessingException, IOException {

	when(getUuidGeneratorService().generateUUID(anyInt())).thenReturn(generateUUID(DEFAULT_NUMBER_OF_UUIDS));

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("numberOf", "");

	Request request = createPostRequest(CONTORLLER_MAPPING, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode modelMapResponse = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(true, modelMapResponse.get(SUCCESS_KEY).asBoolean());

	verify(getUuidGeneratorService()).generateUUID(DEFAULT_NUMBER_OF_UUIDS);

	JsonNode randomUuids = modelMapResponse.get("availableUUIDs");

	assertNotNull(randomUuids);

	assertEquals(DEFAULT_NUMBER_OF_UUIDS, randomUuids.size());
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void getUuidsTest() throws JsonProcessingException, IOException {

	when(getUuidGeneratorService().generateUUID(1001)).thenReturn(generateUUID(1001));

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("numberOf", "1001");

	Request request = createPostRequest(CONTORLLER_MAPPING, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode modelMapResponse = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(true, modelMapResponse.get(SUCCESS_KEY).asBoolean());

	verify(getUuidGeneratorService()).generateUUID(1001);

	JsonNode randomUuids = modelMapResponse.get("availableUUIDs");

	assertNotNull(randomUuids);

	assertEquals(1001, randomUuids.size());
    }

    @Test
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void getUuidTest() throws JsonProcessingException, IOException {

	when(getUuidGeneratorService().generateUUID(1)).thenReturn(generateUUID(1));

	Map<String, String> parameters = new HashMap<String, String>();
	parameters.put("numberOf", "1");

	Request request = createPostRequest(CONTORLLER_MAPPING, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode modelMapResponse = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(true, modelMapResponse.get(SUCCESS_KEY).asBoolean());

	verify(getUuidGeneratorService()).generateUUID(1);

	JsonNode randomUuids = modelMapResponse.get("availableUUIDs");

	assertNotNull(randomUuids);

	assertEquals(1, randomUuids.size());
    }

    @Before
    public void setUp() {
	reset(getUuidGeneratorService());
    }

    private UuidGeneratorService getUuidGeneratorService() {
	return _uuidGeneratorService;
    }
}
