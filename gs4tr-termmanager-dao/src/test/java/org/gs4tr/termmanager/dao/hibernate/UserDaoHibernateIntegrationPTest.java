package org.gs4tr.termmanager.dao.hibernate;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Assert;
import org.junit.Test;

public class UserDaoHibernateIntegrationPTest extends AbstractSpringDAOIntegrationTest {

    private static final String EXPECTED_UPDATE_DATA_SET_NAME = "expectedUpdateUser";

    private static final String USER_TABLE_NAME = "TM_USER_PROFILE";

    @Test
    public void testDelete() throws Exception {
	TmUserProfile user = getUserProfileDAO().findById(new Long(1));
	Long userId = user.getUserProfileId();
	TmUserProfile user2 = getUserProfileDAO().findById(userId);
	assertNotNull(user2);

	getUserProfileDAO().delete(user2);
	assertEquals(user, user2);
    }

    @Test
    public void testFindAllNonGenerciUsernames() {
	List<String> result = getUserProfileDAO().findAllNonGenerciUsernames();
	Assert.assertNotNull(result);
	Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void testFindGenericUserByProjectId() {
	List<TmUserProfile> users = getUserProfileDAO().findGenericUserByProjectId(1L);
	Assert.assertTrue(CollectionUtils.isNotEmpty(users));
	for (TmUserProfile user : users) {
	    Assert.assertTrue(user.isGenericUser());

	}
    }

    @Test
    public void testFindUsernamesByProjectId() {
	TmUserProfile user1 = getUserProfileDAO().findById(Long.valueOf(1l));
	TmUserProfile user2 = getUserProfileDAO().findById(Long.valueOf(2l));
	// TmUserProfile user3 = getUserProfileDAO().findById(Long.valueOf(3l));

	Stream<TmUserProfile> users = Stream.of(user1, user2);

	List<String> projectUserNames = users.map(user -> user.getUserName()).collect(toList());
	List<String> userNames = getUserProfileDAO().findUsernamesByProjectId(Long.valueOf(3l));

	assertTrue(userNames.stream().allMatch(projectUserNames::contains));

	TmUserProfile user3 = getUserProfileDAO().findById(Long.valueOf(3l));

	users = Stream.of(user1, user3);

	projectUserNames = users.map(user -> user.getUserName()).collect(toList());
	userNames = getUserProfileDAO().findUsernamesByProjectId(Long.valueOf(1l));

	assertTrue(userNames.stream().allMatch(projectUserNames::contains));
    }

    @Test
    public void testFindUsernamesByType() {

	List<String> usernames = getUserProfileDAO().findUsernamesByType(UserTypeEnum.POWER_USER);

	assertTrue(usernames.isEmpty());

	TmUserProfile user1 = getUserProfileDAO().findById(Long.valueOf(1l));
	TmUserProfile user2 = getUserProfileDAO().findById(Long.valueOf(2l));
	TmUserProfile user3 = getUserProfileDAO().findById(Long.valueOf(3l));
	TmUserProfile user4 = getUserProfileDAO().findById(Long.valueOf(4l));

	List<String> current = Stream.of(user1, user2, user3, user4).map(TmUserProfile::getUserName).collect(toList());

	usernames = getUserProfileDAO().findUsernamesByType(UserTypeEnum.ORGANIZATION);

	assertEquals(4, usernames.size());
	assertTrue(usernames.stream().allMatch(current::contains));

	user1.getUserInfo().setUserType(UserTypeEnum.POWER_USER);
	user3.getUserInfo().setUserType(UserTypeEnum.POWER_USER);
	getUserProfileDAO().save(user1);
	getUserProfileDAO().save(user3);

	usernames = getUserProfileDAO().findUsernamesByType(UserTypeEnum.POWER_USER);

	assertEquals(2, usernames.size());
    }

    @Test
    public void testReadNoUser() throws Exception {
	Long nonExistantId = new Long(576);
	TmUserProfile u = getUserProfileDAO().findById(nonExistantId);
	assertNull(u);
    }

    @Test
    public void testReadSavedUser() throws Exception {
	TmUserProfile user = new TmUserProfile();

	user.setSystemRoles(new HashSet<Role>());
	UserInfo userInfo = new UserInfo();
	userInfo.setUserName("bob_marley");
	userInfo.setAccountNonExpired(true);
	userInfo.setCredentialsNonExpired(true);
	userInfo.setEmailAddress("foo@yahoo.com");
	userInfo.setEnabled(true);
	userInfo.setFirstName("Bob");
	userInfo.setLastName("Marley");
	userInfo.setUserType(UserTypeEnum.VENDOR);
	user.setUserInfo(userInfo);

	user = getUserProfileDAO().save(user);
	Long id = user.getUserProfileId();
	flushSession();
	TmUserProfile user2 = getUserProfileDAO().findById(id);
	assertNotNull(user2);
	assertEquals(user, user2);
    }

    @Test
    public void testSave() throws Exception {
	TmUserProfile user = new TmUserProfile();

	user.setSystemRoles(new HashSet<Role>());
	UserInfo userInfo = new UserInfo();
	userInfo.setUserName("bob_marley");
	userInfo.setAccountNonExpired(true);
	userInfo.setCredentialsNonExpired(true);
	userInfo.setEmailAddress("foo@yahoo.com");
	userInfo.setEnabled(true);
	userInfo.setFirstName("Bob");
	userInfo.setLastName("Marley");
	userInfo.setUserType(UserTypeEnum.VENDOR);
	user.setUserInfo(userInfo);
	String oldUsername = user.getUserName();

	user = getUserProfileDAO().save(user);

	Long newId = user.getUserProfileId();
	assertNotNull(newId);
	assertTrue(newId != 1);

	// make sure the username is the same
	assertEquals(oldUsername, user.getUserName());

    }

    @Test
    public void testSearchByUserName() {
	String userName = new String("bob_jones");
	TmUserProfile user = getUserProfileDAO().findByName(userName);
	assertNotNull(user);
	Set<Role> roles = user.getSystemRoles();
	assertEquals(1, roles.size());

    }

    @Test
    public void testUpdate() throws Exception {
	TmUserProfile user = getUserProfileDAO().findById(new Long(1));
	Long userId = user.getUserProfileId();
	String lastname = user.getUserInfo().getLastName();
	String newLastname = "Mac" + lastname;
	user.getUserInfo().setLastName(newLastname);

	getUserProfileDAO().merge(user);

	TmUserProfile user2 = getUserProfileDAO().findById(userId);
	assertNotNull(user2);
	assertNotSame(lastname, user2.getUserInfo().getLastName());

	assertDatabaseTables(USER_TABLE_NAME, EXPECTED_UPDATE_DATA_SET_NAME);
    }

    @Test
    public void testUpdateUserByProperties() {
	Long userId = new Long(1);

	Map<String, Object> propertyMap = new HashMap<String, Object>();
	propertyMap.put("firstName", "Bill");
	propertyMap.put("lastName", "Nebaa");

	getUserProfileDAO().updateProperties(userId, propertyMap);
	TmUserProfile user = getUserProfileDAO().load(userId);

	Assert.assertEquals(user.getUserInfo().getFirstName(), "Bill");
	Assert.assertEquals(user.getUserInfo().getLastName(), "Nebaa");
    }
}
