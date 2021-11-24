package org.gs4tr.termmanager.tests;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.IMap;

public class EnableProjectTaskHandlerTest extends AbstractSpringServiceTests {

    @Autowired
    private CacheGatewaySessionUpdaterService _cacheGatewaySessionUpdaterService;

    /**
     * If there is only one enabled project throw UserException
     *
     * @throws Exception
     */
    @Test(expected = UserException.class)
    public void disableProjectWithOnlyOneProjectEnabledInTheListTest() throws Exception {
	TmProject otherProject = getProjectService().findById(2L);
	ProjectInfo projectInfo = otherProject.getProjectInfo();
	projectInfo.setEnabled(false);
	getProjectService().updateProject(2L, projectInfo);

	TmProject project = getProjectService().findById(1L);
	Boolean enabled = project.getProjectInfo().isEnabled();
	Assert.assertTrue(enabled);

	TmProject secondProject = getProjectService().findById(2L);
	enabled = secondProject.getProjectInfo().isEnabled();
	Assert.assertTrue(!enabled);

	ManualTaskHandler taskHandler = getHandler("enable project");

	taskHandler.processTasks(new Long[] { 1L }, null, null, null);
    }

    @Test
    public void enableProjectWithOnlyOneProjectEnabledInTheListTest() throws Exception {
	TmProject otherProject = getProjectService().findById(1L);
	ProjectInfo projectInfo = otherProject.getProjectInfo();
	projectInfo.setEnabled(false);
	getProjectService().updateProject(1L, projectInfo);

	TmProject project = getProjectService().findById(1L);
	Boolean enabled = project.getProjectInfo().isEnabled();
	Assert.assertTrue(!enabled);

	TmProject secondProject = getProjectService().findById(2L);
	enabled = secondProject.getProjectInfo().isEnabled();
	Assert.assertTrue(enabled);

	ManualTaskHandler taskHandler = getHandler("enable project");

	TaskResponse response = taskHandler.processTasks(new Long[] { 1L }, null, null, null);

	Assert.assertNotNull(response);
	Assert.assertEquals(Boolean.TRUE, getProjectService().findById(1L).getProjectInfo().isEnabled());
    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void multiThreadRemoveSessionOnDisableProject() {
	Long projectId = 1L;

	int numberOfThreads = 10;

	TmProject project = getProjectService().findById(projectId);

	ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Remove all sessions before test
	sessionCache.clear();

	// Login users
	for (int i = 0; i < 100; i++) {
	    ConnectionInfoHolder connectionInfoHolder = getConnectionHandler("en-US", "de-DE", 1L);
	    sessionCache.put(Integer.toString(i), connectionInfoHolder);
	}

	for (int i = 0; i < numberOfThreads; i++) {
	    executor.execute(() -> _cacheGatewaySessionUpdaterService.removeOnDisableProject(project));
	}

	waitServiceThreadPoolThreads();

	IMap<String, ConnectionInfoHolder> sessionCacheAfter = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	Assert.assertEquals(0, sessionCacheAfter.size());
    }

    @Before
    public void resetProjectsStateToEnable() {
	Long[] idList = new Long[] { 1L, 2L };

	List<TmProject> allProjects = getProjectService().findProjectByIds(Arrays.asList(idList));

	for (TmProject project : allProjects) {
	    project.getProjectInfo().setEnabled(true);
	    getProjectService().update(project);
	}
    }

    @Test
    public void testEnableProjectPost() throws Exception {

	TmProject project = getProjectService().findById(1L);
	Boolean enabled = project.getProjectInfo().isEnabled();
	Assert.assertTrue(enabled);

	ManualTaskHandler taskHandler = getHandler("enable project");

	TaskResponse response = taskHandler.processTasks(new Long[] { 1L }, null, null, null);
	Assert.assertNotNull(response);
	Assert.assertEquals(Boolean.FALSE, getProjectService().findById(1L).getProjectInfo().isEnabled());
    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void testRemoveSessionFromCashOnDisableProject() {
	Long project1Id = 1L;
	Long project2Id = 2L;

	TmProject project = getProjectService().findById(project1Id);
	Boolean enabled = project.getProjectInfo().isEnabled();
	Assert.assertTrue(enabled);

	String session1 = "session1";
	String session2 = "session2";

	ConnectionInfoHolder connectionInfoHolder1 = new ConnectionInfoHolder();
	connectionInfoHolder1.setProjectId(project1Id);

	ConnectionInfoHolder connectionInfoHolder2 = new ConnectionInfoHolder();
	connectionInfoHolder2.setProjectId(project2Id);

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	Assert.assertEquals(2, sessionCache.size());

	ManualTaskHandler taskHandler = getHandler("enable project");

	TaskResponse response = taskHandler.processTasks(new Long[] { project1Id }, null, null, null);

	Assert.assertNotNull(response);

	waitServiceThreadPoolThreads();

	IMap<String, ConnectionInfoHolder> sessionCacheAfter = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	Assert.assertEquals(1, sessionCacheAfter.size());

	// Enabled project users should be logged in
	Assert.assertNotNull(sessionCache.get(session2));

	// Disabled project users should be removed/should not be logged in
	Assert.assertNull(sessionCache.get(session1));
    }

    private ConnectionInfoHolder getConnectionHandler(String sourceKey, String targetKey, Long projectId) {
	ConnectionInfoHolder connectionInfoHolder = new ConnectionInfoHolder();
	connectionInfoHolder.setProjectId(projectId);
	connectionInfoHolder.setSource(sourceKey);
	connectionInfoHolder.setTarget(targetKey);
	return connectionInfoHolder;
    }
}
