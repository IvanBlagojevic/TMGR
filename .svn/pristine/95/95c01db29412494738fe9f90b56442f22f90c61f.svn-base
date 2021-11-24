package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.junit.Test;

public class HzCacheGatewayWsTest extends AbstractSpringCacheGatewayTest<String, String> {

    private static final String KEY_1 = "key1";

    @Test
    public void putGetGlossaryV2Test() {
	CacheName v2GlossaryCache = CacheName.V2_GLOSSARY_SESSIONS;

	assertNull(getCacheGateway().get(v2GlossaryCache, KEY_1));

	String value = UUID.randomUUID().toString();

	getCacheGateway().put(v2GlossaryCache, KEY_1, value);

	String uuid = getCacheGateway().get(v2GlossaryCache, KEY_1);

	assertNotNull(uuid);
	assertEquals(value, uuid);
    }

    @Test
    public void putGetWsV1Test() {
	CacheName wsV1Cache = CacheName.WS_V1_USERS_SESSIONS;

	assertNull(getCacheGateway().get(wsV1Cache, KEY_1));

	String value = UUID.randomUUID().toString();

	getCacheGateway().put(wsV1Cache, KEY_1, value);

	String uuid = getCacheGateway().get(wsV1Cache, KEY_1);

	assertNotNull(uuid);
	assertEquals(value, uuid);
    }

    @Test
    public void putGetWsV2Test() {
	CacheName wsV1Cache = CacheName.WS_V2_USERS_SESSIONS;

	assertNull(getCacheGateway().get(wsV1Cache, KEY_1));

	String value = UUID.randomUUID().toString();

	getCacheGateway().put(wsV1Cache, KEY_1, value);

	String uuid = getCacheGateway().get(wsV1Cache, KEY_1);

	assertNotNull(uuid);
	assertEquals(value, uuid);
    }
}
