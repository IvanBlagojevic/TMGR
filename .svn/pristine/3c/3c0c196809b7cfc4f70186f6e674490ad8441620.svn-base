package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LdapUserInfo;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.foundation.modules.security.ldap.LdapUserHandlerInterface;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.oauth.model.AddTptUserCommand;
import org.gs4tr.foundation.modules.usermanager.oauth.model.TptOauthUserResponse;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.UserCommand;
import org.gs4tr.termmanager.service.model.command.UserInfoCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoUserCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AddUserTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_PROJECT_ROLES_NAME_KEY = "allProjectRoles";

    private static final String ALL_SYSTEM_ROLES_NAME_KEY = "allSystemRoles"; //$NON-NLS-1$

    private static final String DESCRIPTION = "desciption";

    private static final Log LOGGER = LogFactory.getLog(AddUserTaskHandler.class);

    private static final String ORGANIZATION_KEY = "organizations"; //$NON-NLS-1$

    private static final String ORGANIZATION_NAME_KEY = "organizationName"; //$NON-NLS-1$

    private static final String ORGANIZATION_TICKET_KEY = "ticket"; //$NON-NLS-1$

    private static final String REASONS = "reasons";

    private static final String SSO_ENABLED_KEY = "ssoEnabled"; //$NON-NLS-1$

    private static final String TPT_OAUTH_ORGANIZATIONS = "tptOauthOrganizations"; //$NON-NLS-1$

    private static final String TPT_OAUTH_USER_ENABLED = "tptOauthUserEnabled"; //$NON-NLS-1$

    private static final String TYPE = "type";

    private static String REQUIRED_PASSWORD = "requiredPassword"; //$NON-NLS-1$

    @Autowired(required = false)
    private TptOAuthUserManagerClient _oAuthClient;

    @Value("${oauth.support.mailAddress}")
    private String _oauthSupportMail;

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private RoleService _roleService;

    @Value("#{'${tpt.addUser.clients}'.split(',')}")
    private String[] _tptAuthClients;

    @Value("${tpt.addUser.defaultOrganization}")
    private String _tptDefaultUserOrganization;

    @Value("#{'${tpt.addUser.organizations}'.split(',')}")
    private String[] _tptOauthOrganizations;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoUserCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	List<TmOrganization> enabledOrganizations = getOrganizationService().findAllEnabledOrganizations();
	if (enabledOrganizations.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("AddUserTaskHandler.5"), //$NON-NLS-1$
		    MessageResolver.getMessage("AddUserTaskHandler.2")); //$NON-NLS-1$
	}

	TaskModel model = new TaskModel();

	List<Object> organizations = new ArrayList<>();

	for (TmOrganization organization : enabledOrganizations) {
	    Map<String, Object> data = new HashMap<>();
	    data.put(ORGANIZATION_NAME_KEY, organization.getOrganizationInfo().getName());
	    data.put(ORGANIZATION_TICKET_KEY, new Ticket(organization.getOrganizationId()));
	    organizations.add(data);
	}

	model.addObject(ORGANIZATION_KEY, organizations);

	List<Role> allSystemRoles = getRoleService().findAllSystemRoles();

	List<String> allSystemRoleIds = new ArrayList<>();
	allSystemRoles.forEach(r -> allSystemRoleIds.add(r.getRoleId()));

	model.addObject(ALL_SYSTEM_ROLES_NAME_KEY, allSystemRoleIds);

	List<Role> allProjectRoles = getRoleService().findAllContextRoles();

	List<String> allProjectRoleIds = new ArrayList<>();
	allProjectRoles.forEach(r -> allProjectRoleIds.add(r.getRoleId()));

	model.addObject(ALL_PROJECT_ROLES_NAME_KEY, allProjectRoleIds);

	boolean samlEnabled = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.SAML_AUTHENTICATION_PROFILE);
	boolean tptAuthEnabled = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE);

	model.addObject(SSO_ENABLED_KEY, samlEnabled || tptAuthEnabled || ServiceUtils.supportsLdap());

	model.addObject(REQUIRED_PASSWORD, !samlEnabled);

	model.addObject(TPT_OAUTH_USER_ENABLED, tptAuthEnabled);

	if (tptAuthEnabled) {
	    model.addObject(TPT_OAUTH_ORGANIZATIONS, getTptOauthOrganizations());
	}

	return new TaskModel[] { model };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	UserCommand addCommand = (UserCommand) command;

	connectOrCreateLdapUser(addCommand);

	Long userId = createLocalUser(addCommand.getUserInfoCommand());

	TaskResponse response = new TaskResponse(new Ticket(userId));

	connectOrCreateTptAuthUser(addCommand.getUserInfoCommand(), response);

	return response;
    }

    private void addMessageToResponse(TaskResponse response, UserMessageTypeEnum messageType, String reasons,
	    String description) {
	response.addObject(REASONS, reasons);
	response.addObject(DESCRIPTION, description);
	response.addObject(TYPE, messageType);
    }

    private void connectOrCreateLdapUser(UserCommand addCommand) {
	UserInfoCommand userInfoCommand = addCommand.getUserInfoCommand();
	String userName = userInfoCommand.getUserName();
	String password = userInfoCommand.getPassword();

	boolean emptyPassword = StringUtils.isBlank(password);

	boolean supportsLdap = ServiceUtils.supportsLdap();
	boolean createLdapUser = addCommand.isCreateLdapUser();

	if ((emptyPassword || createLdapUser) && supportsLdap) {
	    try {
		LdapUserHandlerInterface ldapUserHandler = ManualTaskHandlerUtils.getLdapUserHandler();

		LdapUserInfo ldapUserInfo = ldapUserHandler.findUser(userName);

		if (ldapUserInfo != null) {
		    if (emptyPassword) {
			LOGGER.info(String.format(Messages.getString("AddUserTaskHandler.8"), //$NON-NLS-1$
				userName));
		    } else {
			throw new LdapUserAlreadyExistsException(userName);
		    }
		} else {
		    UserInfo userInfo = getUserInfoFromCommand(userInfoCommand);
		    LdapUserInfo newLdapUserInfo = ManualTaskHandlerUtils.createLdapUserInfo(userInfo);
		    ldapUserHandler.createUser(newLdapUserInfo);
		}
	    } catch (Exception e) {
		String ldapMessage = ManualTaskHandlerUtils.extractLdapMessage(e.getMessage());
		String errorMessage = (ldapMessage == null)
			? String.format(Messages.getString("AddUserTaskHandler.9"), userName) //$NON-NLS-1$
			: ldapMessage;
		throw new RuntimeException(errorMessage, e);
	    }
	}
    }

    private void connectOrCreateTptAuthUser(UserInfoCommand userInfoCommand, TaskResponse response) {
	boolean tptAuthEnabled = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE);
	if (tptAuthEnabled && userInfoCommand.isSsoUser()) {
	    try {
		TptOAuthUserManagerClient oauthClient = getoAuthClient();
		boolean existOnTptAuth = oauthClient.checkIfUserExists(userInfoCommand.getEmailAddress());
		if (!existOnTptAuth) {
		    if (createTptUser(userInfoCommand, userInfoCommand.getTptOauthOrganization())) {
			// connected to new tpt auth user
			addMessageToResponse(response, UserMessageTypeEnum.INFO,
				MessageResolver.getMessage("tptAuth.newUser.message"),
				MessageResolver.getMessage("tptAuth.newUser.description"));
		    } else {
			LogHelper.error(LOGGER,
				String.format("Tpt user creation failed %s", userInfoCommand.getEmailAddress()));
			addMessageToResponse(response, UserMessageTypeEnum.ERROR,
				MessageResolver.getMessage("tptAuth.creationFailed.message"),
				String.format(MessageResolver.getMessage("tptAuth.creationFailed.description"),
					getOauthSupportMail()));
		    }
		} else {
		    // connected to existing user
		    TptOauthUserResponse tptAuthUser = oauthClient
			    .retrieveSingleUser(userInfoCommand.getEmailAddress());

		    String[] clients = getTptAuthClients();
		    for (String client : clients)
			if (!ArrayUtils.contains(tptAuthUser.getClients(), client)) {
			    oauthClient.registerClient(tptAuthUser.getEmail(), client);
			}

		    addMessageToResponse(response, UserMessageTypeEnum.INFO,
			    MessageResolver.getMessage("tptAuth.userExist.message"),
			    MessageResolver.getMessage("tptAuth.userExist.description"));
		}
	    } catch (Exception ex) {
		LogHelper.error(LOGGER, ex.getMessage(), ex);
		addMessageToResponse(response, UserMessageTypeEnum.ERROR,
			MessageResolver.getMessage("tptAuth.creationFailed.message"),
			String.format(MessageResolver.getMessage("tptAuth.creationFailed.description"),
				getOauthSupportMail()));
	    }
	}
    }

    private Long createLocalUser(UserInfoCommand userInfoCommand) {
	UserProfileService userProfileService = getUserProfileService();

	UserInfo userInfo = getUserInfoFromCommand(userInfoCommand);

	if (!userInfo.isSsoUser() && StringUtils.isEmpty(userInfo.getPassword())) {
	    userInfo.setPassword(getTimestampPassword());
	} else if (userInfo.isSsoUser()) {
	    // local user should not contain password in db for tpt auth or saml profile
	    userInfo.setPassword(null);
	}

	return userProfileService.createUserProfile(userInfo);
    }

    private boolean createTptUser(UserInfoCommand userInfoCommand, String tptOauthUserOrganization) {
	boolean success;
	if (StringUtils.isNotEmpty(userInfoCommand.getEmailAddress())) {
	    if (StringUtils.isEmpty(tptOauthUserOrganization)) {
		tptOauthUserOrganization = getTptDefaultUserOrganization();
	    }

	    UserInfo userInfo = getUserInfoFromCommand(userInfoCommand);
	    AddTptUserCommand tptOauthUserToAdd = getoAuthClient().generateTptAddUserCommand(userInfo,
		    tptOauthUserOrganization);
	    success = getoAuthClient().createUser(tptOauthUserToAdd);
	} else {
	    throw new RuntimeException(Messages.getString("AddUserTaskHandler.10"));
	}

	return success;
    }

    private String getOauthSupportMail() {
	return _oauthSupportMail;
    }

    private OrganizationService getOrganizationService() {
	return _organizationService;
    }

    private RoleService getRoleService() {
	return _roleService;
    }

    private String getTimestampPassword() {
	long timeStamp = new Date().getTime();

	return String.valueOf(timeStamp);
    }

    private String[] getTptAuthClients() {
	return _tptAuthClients;
    }

    private String getTptDefaultUserOrganization() {
	return _tptDefaultUserOrganization;
    }

    private String[] getTptOauthOrganizations() {
	return _tptOauthOrganizations;
    }

    private UserInfo getUserInfoFromCommand(UserInfoCommand userInfoCommand) {
	UserInfo userInfo = new UserInfo();
	userInfo.setEmailNotification(userInfoCommand.getEmailNotification());
	userInfo.setFirstName(userInfoCommand.getFirstName());
	userInfo.setLastName(userInfoCommand.getLastName());
	userInfo.setEmailAddress(userInfoCommand.getEmailAddress());
	userInfo.setTimeZone(userInfoCommand.getTimeZone());
	userInfo.setAddress(userInfoCommand.getAddress());
	userInfo.setDepartment(userInfoCommand.getDepartment());
	userInfo.setPhone1(userInfoCommand.getPhone1());
	userInfo.setPhone2(userInfoCommand.getPhone2());
	userInfo.setFax(userInfoCommand.getFax());
	userInfo.setUserName(userInfoCommand.getUserName());
	userInfo.setUserType(userInfoCommand.getUserType());
	userInfo.setDescription(userInfoCommand.getDescription());
	userInfo.setShowDescription(userInfoCommand.getShowDescription());
	Boolean ssoUser = userInfoCommand.isSsoUser();
	userInfo.setSsoUser(ssoUser);
	userInfo.setPassword(userInfoCommand.getPassword());
	return userInfo;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private TptOAuthUserManagerClient getoAuthClient() {
	return _oAuthClient;
    }

    private static class LdapUserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 9187796722327614481L;

	private LdapUserAlreadyExistsException(String userName) {
	    super(String.format(Messages.getString("AddUserTaskHandler.1"), //$NON-NLS-1$
		    userName));
	}
    }
}
