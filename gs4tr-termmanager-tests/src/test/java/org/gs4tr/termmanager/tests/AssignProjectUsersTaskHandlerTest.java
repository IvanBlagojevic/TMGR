package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.UserLanguageModel;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.UserLanguageCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

import com.hazelcast.core.IMap;

@TestSuite("assign_project_users")
public class AssignProjectUsersTaskHandlerTest extends AbstractSpringServiceTests {

    private static final String ALL_USERS = "allUsers";

    private static final String GENERIC_USER_ROLE = "generic_user";

    @Test
    @TestCase("get_task_infos")
    public void assignAttributesGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign project users");

	AssignProjectUserLanguageCommand command = new AssignProjectUserLanguageCommand();

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, "assign project users", command);

	assertNotNull(taskInfos);

    }

    /*
     * TERII-4414 TPT4 | Application error while switching tabs on Edit Project
     * Note: We have optimized generic user search so it returns only generic user
     * assigned to specific project. This will reduce generic user search time.
     */
    @Test
    @TestCase("get_task_infos")
    public void readGenericUser() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign project users");

	AssignProjectUserLanguageCommand command = new AssignProjectUserLanguageCommand();

	// Find and check if generic user exists on the project
	List<TmUserProfile> users = getUserProfileService().findGenericUserByProjectId(1L);
	assertEquals(1, users.size());
	assertEquals("generic1", users.get(0).getUserName());

	// Read only generic user assigned to project
	command.setShowGenericUsers(true);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, "assign project users", command);

	assertNotNull(taskInfos);

	assertEquals(1, taskInfos.length);

	TaskModel taskModel = taskInfos[0];

	List<UserLanguageModel> genericUsers = (List<UserLanguageModel>) taskModel.getModel().get(ALL_USERS);
	UserLanguageModel genericUser = genericUsers.get(0);

	assertEquals("generic1", genericUser.getUserName());
    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void removeSessionOnRemoveUserFromProjectAndRemoveLanguageTest() {
	Long projectId = 1L;

	ManualTaskHandler taskHandler = getHandler("assign project users");

	AssignProjectUserLanguageCommand command = new AssignProjectUserLanguageCommand();

	command.setProjectId(projectId);

	UserLanguageCommand userLanguageCommand1 = getUserLanguageCommand(3L, Arrays.asList("en-US", "de-DE"));

	command.setUsers(Arrays.asList(userLanguageCommand1));

	TmUserProfile userProfile1 = getUserProfileService().findById(3L);
	TmUserProfile userProfile2 = getUserProfileService().findById(6L);
	Assert.assertNotNull(userProfile1);
	Assert.assertNotNull(userProfile2);

	ConnectionInfoHolder connectionInfoHolder1 = getConnectionInfoHolder(projectId, userProfile1, "en-US", "fr-FR");
	ConnectionInfoHolder connectionInfoHolder2 = getConnectionInfoHolder(projectId, userProfile2, "en-US", "de-DE");

	String session1 = "session1";
	String session2 = "session2";

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	taskHandler.processTasks(new Long[] { projectId }, null, command, null);

	waitServiceThreadPoolThreads();

	// User should be removed because fr language is removed
	Assert.assertNull(sessionCache.get(session1));

	// User should be removed because it's removed from the project
	Assert.assertNull(sessionCache.get(session2));
    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void removeSessionOnRemoveUserFromProjectTest() {
	Long projectId = 1L;

	ManualTaskHandler taskHandler = getHandler("assign project users");

	AssignProjectUserLanguageCommand command = new AssignProjectUserLanguageCommand();

	command.setProjectId(projectId);

	UserLanguageCommand userLanguageCommand1 = getUserLanguageCommand(6L, Arrays.asList("en-US", "de-DE", "fr-FR"));

	command.setUsers(Arrays.asList(userLanguageCommand1));

	TmUserProfile userProfile1 = getUserProfileService().findById(3L);
	TmUserProfile userProfile2 = getUserProfileService().findById(6L);
	Assert.assertNotNull(userProfile1);
	Assert.assertNotNull(userProfile2);

	ConnectionInfoHolder connectionInfoHolder1 = getConnectionInfoHolder(projectId, userProfile1, "en-US", "de-DE");
	ConnectionInfoHolder connectionInfoHolder2 = getConnectionInfoHolder(projectId, userProfile2, "en-US", "de-DE");

	String session1 = "session1";
	String session2 = "session2";

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	taskHandler.processTasks(new Long[] { projectId }, null, command, null);

	waitServiceThreadPoolThreads();

	// User should be removed because it's removed from the project
	Assert.assertNull(sessionCache.get(session1));

	/*
	 * User should not be removed because languages are not removed and it's not
	 * removed from the project
	 */
	Assert.assertNotNull(sessionCache.get(session2));

    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    public void removeSessionOnRemoveUserProjectLanguageTest() {
	Long projectId = 1L;

	ManualTaskHandler taskHandler = getHandler("assign project users");

	AssignProjectUserLanguageCommand command = new AssignProjectUserLanguageCommand();

	command.setProjectId(projectId);

	UserLanguageCommand userLanguageCommand1 = getUserLanguageCommand(3L, Arrays.asList("en-US", "fr-FR", "sr-SR"));
	UserLanguageCommand userLanguageCommand2 = getUserLanguageCommand(6L, Arrays.asList("en-US", "de-DE"));

	command.setUsers(Arrays.asList(userLanguageCommand1, userLanguageCommand2));

	TmUserProfile userProfile1 = getUserProfileService().findById(3L);
	TmUserProfile userProfile2 = getUserProfileService().findById(6L);
	Assert.assertNotNull(userProfile1);
	Assert.assertNotNull(userProfile2);

	ConnectionInfoHolder connectionInfoHolder1 = getConnectionInfoHolder(projectId, userProfile1, "en-US", "de-DE");
	ConnectionInfoHolder connectionInfoHolder2 = getConnectionInfoHolder(projectId, userProfile2, "en-US", "de-DE");

	String session1 = "session1";
	String session2 = "session2";

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	taskHandler.processTasks(new Long[] { projectId }, null, command, null);

	waitServiceThreadPoolThreads();

	// User should be removed because fr language is removed
	Assert.assertNull(sessionCache.get(session1));

	// User should not be removed because languages are not removed
	Assert.assertNotNull(sessionCache.get(session2));

    }

    private ConnectionInfoHolder getConnectionInfoHolder(Long projectId, TmUserProfile userProfile, String source,
	    String target) {
	ConnectionInfoHolder connectionInfoHolder = new ConnectionInfoHolder();
	connectionInfoHolder.setProjectId(projectId);
	connectionInfoHolder.setUserProfile(userProfile);
	connectionInfoHolder.setSource(source);
	connectionInfoHolder.setTarget(target);
	return connectionInfoHolder;
    }

    private UserLanguageCommand getUserLanguageCommand(Long userId, List<String> userLanguages) {
	UserLanguageCommand userLanguageCommand = new UserLanguageCommand();
	userLanguageCommand.setUserId(userId);
	userLanguageCommand.setGenericUser(Boolean.FALSE);
	userLanguageCommand.setRoleId("super_user");
	userLanguageCommand.setUserInfo(new UserInfo());
	userLanguageCommand.setUserLanguages(userLanguages);
	return userLanguageCommand;
    }

}
