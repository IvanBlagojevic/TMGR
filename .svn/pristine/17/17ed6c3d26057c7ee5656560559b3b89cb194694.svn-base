package org.gs4tr.termmanager.glossaryV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.tm3.api.ServerInfo;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.gs4tr.tm3.httpconnector.tmgr.TmgrGlossaryHttpConnectionInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

@TestSuite("glossary")
public class ConnectTest extends AbstractV2GlossaryServiceTest {

    @Test
    @TestCase("connect")
    public void testConnect() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	TmgrGlossaryHttpConnectionInfo connectionInfoinfo = new TmgrGlossaryHttpConnectionInfo();
	connectionInfoinfo.setLoginUser("user");
	connectionInfoinfo.setPassword("pass");

	ServerInfo info = operations.connect(connectionInfoinfo);

	Assert.assertNotNull(info);
	Assert.assertEquals(key.getSource(), info.getSourceLocale().getCode());
	Assert.assertEquals(key.getTarget(), info.getTargetLocale().getCode());
	Assert.assertNotNull(info.getVersion());

	ResolverContext resolverContext = operations.getResolverContext();
	Assert.assertNotNull(resolverContext);
	Assert.assertNotNull(resolverContext.getReadOnly());
    }

    @Test(expected = OperationsException.class)
    @TestCase("connect")
    public void testConnectWithWrongLanguageDirections() throws OperationsException {
	TmgrKey key = createKey();
	key.setSource("ja-JP");
	key.setTarget("zh-CN");

	getGlossaryFactory().getOperations(key);
    }

    /*
     ******************************************************************************
     * TERII-5098 WF5/TS | WF does not know a connection is writable if only 'On
     * Hold' policy is selected
     ******************************************************************************
     */
    @Test
    @TestCase("connect")
    public void testReadOnlyAddOnHoldStatusSelected() throws Exception {

	Policy policy = getModelObject("policy7", Policy.class);

	remockUserWithSpecificPolicy(policy);

	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	TmgrGlossaryHttpConnectionInfo connectionInfoinfo = new TmgrGlossaryHttpConnectionInfo();
	connectionInfoinfo.setLoginUser("user");
	connectionInfoinfo.setPassword("pass");

	ServerInfo info = operations.connect(connectionInfoinfo);

	Assert.assertNotNull(info);

	ResolverContext resolverContext = operations.getResolverContext();
	Assert.assertNotNull(resolverContext);

	/* Read only should not be checked */
	Assert.assertFalse(resolverContext.getReadOnly());
    }

    @Test
    @TestCase("connect")
    public void testReadOnlyAddPendingApprovalSelected() throws Exception {

	Policy policy = getModelObject("policy5", Policy.class);

	remockUserWithSpecificPolicy(policy);

	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	TmgrGlossaryHttpConnectionInfo connectionInfoinfo = new TmgrGlossaryHttpConnectionInfo();
	connectionInfoinfo.setLoginUser("user");
	connectionInfoinfo.setPassword("pass");

	ServerInfo info = operations.connect(connectionInfoinfo);

	Assert.assertNotNull(info);

	ResolverContext resolverContext = operations.getResolverContext();
	Assert.assertNotNull(resolverContext);

	/* Read only should not be checked */
	Assert.assertFalse(resolverContext.getReadOnly());
    }

    @SuppressWarnings("unchecked")
    private void remockUserWithSpecificPolicy(Policy policy) {
	UserProfileContext.clearContext();

	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	List<Role> roles = getModelObject("rolesBasic", List.class);

	roles.get(0).getPolicies().add(policy);

	Map<Long, List<Role>> rolesMap = new HashMap<Long, List<Role>>();
	rolesMap.put(1L, roles);

	List<FolderPolicy> foldersPolicies = getModelObject("foldersPolicies", List.class);
	List<FolderPolicy> adminFoldersPolicies = getModelObject("adminFolderPolocies", List.class);
	List<ProjectUserLanguage> projectUserLanguages = getModelObject("projectUserLanguages", List.class);

	userProfile.initializeOrganizationUser(rolesMap, foldersPolicies, adminFoldersPolicies, projectUserLanguages,
		new ArrayList<Submission>());
	userProfile.setSystemRoles(new HashSet<Role>(roles));

	Mockito.when(getSessionService().login(Mockito.anyString(), Mockito.anyString())).thenReturn(userProfile);

	UserProfileContext.setCurrentUserProfile(userProfile);
    }
}
