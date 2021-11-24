package org.gs4tr.termmanager.tests;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.junit.Assert;
import org.junit.Test;

import com.hazelcast.core.IMap;

public class EnableUserTaskHandlerTest extends AbstractSpringServiceTests {

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void testRemoveSessionFromCashOnDisableProject() {

	Long user1Id = 3L;
	Long user2Id = 6L;

	TmUserProfile userProfile1 = getUserProfileService().findById(user1Id);
	Assert.assertTrue(userProfile1.getUserInfo().isEnabled());

	TmUserProfile userProfile2 = getUserProfileService().findById(user2Id);
	Assert.assertTrue(userProfile2.getUserInfo().isEnabled());

	ManualTaskHandler taskHandler = getHandler("enable user");

	ConnectionInfoHolder connectionInfoHolder1 = new ConnectionInfoHolder();
	connectionInfoHolder1.setProjectId(user1Id);
	connectionInfoHolder1.setUserProfile(userProfile1);

	ConnectionInfoHolder connectionInfoHolder2 = new ConnectionInfoHolder();
	connectionInfoHolder2.setProjectId(user2Id);
	connectionInfoHolder2.setUserProfile(userProfile2);

	String session1 = "session1";
	String session2 = "session2";

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	TaskResponse response = taskHandler.processTasks(new Long[] { user1Id }, null, null, null);

	Assert.assertNotNull(response);

	waitServiceThreadPoolThreads();

	// Enabled users should not be removed
	Assert.assertNotNull(sessionCache.get(session2));

	// Disabled users should be removed/should not be logged in
	Assert.assertNull(sessionCache.get(session1));

    }

}