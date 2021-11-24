package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.UserInfoConverter;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.UserCommand;
import org.gs4tr.termmanager.service.model.command.UserInfoCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoUserCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class EditUserTaskHandler extends AbstractManualTaskHandler {

    private static final String ALLOW_LDAP_PASSWORD_CHANGE = "allowPasswordChange"; //$NON-NLS-1$
    private static final String DESCRIPTION = "desciption";
    private static final String GENERIC = "generic";
    private static final Log LOGGER = LogFactory.getLog(EditUserTaskHandler.class);
    private static final String REASONS = "reasons";
    private static final String SSO_ENABLED_KEY = "ssoEnabled";
    private static final String TYPE = "type";
    private static final String USERPROFILE_PASSWORD_KEY = "password"; //$NON-NLS-1$
    private static final String USER_INFO = "userInfo"; //$NON-NLS-1$

    @Autowired(required = false)
    private TptOAuthUserManagerClient _oAuthClient;

    @Value("${oauth.support.mailAddress}")
    private String _oauthSupportMail;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoUserCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	UserProfileService userProfileService = getUserProfileService();

	Long parentId = parentIds[0];

	TmUserProfile userProfile = userProfileService.load(parentId);

	TaskModel newTaskModel = new TaskModel(null, new Ticket(userProfile.getUserProfileId()));

	newTaskModel.addObject(USERPROFILE_PASSWORD_KEY, StringUtils.EMPTY);
	newTaskModel.addObject(ALLOW_LDAP_PASSWORD_CHANGE, null);
	newTaskModel.addObject(GENERIC, userProfile.getGeneric());
	newTaskModel.addObject(USER_INFO, UserInfoConverter.fromInternalToDto(userProfile.getUserInfo()));

	boolean samlEnabled = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.SAML_AUTHENTICATION_PROFILE);
	boolean tptAuthEnabled = SpringProfileUtils
		.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE);

	newTaskModel.addObject(SSO_ENABLED_KEY, samlEnabled || tptAuthEnabled || ServiceUtils.supportsLdap());

	return new TaskModel[] { newTaskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	UserProfileService userProfileService = getUserProfileService();

	TmUserProfile userProfile = userProfileService.load(taskIds[0]);

	UserInfo existingUserInfo = userProfile.getUserInfo();

	UserCommand editCommand = (UserCommand) command;

	UserInfoCommand userInfoCommand = editCommand.getUserInfoCommand();

	UserInfo userInfo = createUserInfo(userInfoCommand, userProfile);

	String password = userInfoCommand.getPassword();
	String newUserName = userInfo.getUserName();

	userInfo.setDateLastFailedLogin(existingUserInfo.getDateLastFailedLogin());
	userInfo.setDateLastLogin(existingUserInfo.getDateLastLogin());

	boolean isUserNameChanged = !existingUserInfo.getUserName().equals(newUserName);

	if (isUserNameChanged) {
	    if (userProfileService.findUserProfileByUsername(newUserName) != null) {
		String message = String.format(MessageResolver.getMessage("EditUserTaskHandler.11"), newUserName);//$NON-NLS-1$
		throw new UserException(MessageResolver.getMessage("EditUserTaskHandler.1"), message); //$NON-NLS-1$
	    }
	}

	ManualTaskHandlerUtils.updateLdapPassword(newUserName, null, password, userInfo);

	TaskResponse response = new TaskResponse(new Ticket(userProfile.getUserProfileId()));

	if (StringUtils.isNotEmpty(password)) {
	    if (!userInfo.isSsoUser()) {
		userInfo.setPassword(password);
	    } else if (SpringProfileUtils
		    .checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE)) {
		try {
		    Boolean isUserPasswordUpdated = getoAuthClient().updateUserPassword(userInfo.getEmailAddress(),
			    password);
		    if (!isUserPasswordUpdated) {
			addMessageToResponse(response, UserMessageTypeEnum.ERROR,
				MessageResolver.getMessage("tptAuth.updatePasswordFailed.message"),
				String.format(MessageResolver.getMessage("tptAuth.updatePasswordFailed.description"),
					getOauthSupportMail()));

		    }
		} catch (Exception e) {
		    String updatePasswordFailedDescription = String.format(
			    MessageResolver.getMessage("tptAuth.updatePasswordFailed.description"),
			    getOauthSupportMail());

		    addMessageToResponse(response, UserMessageTypeEnum.ERROR,
			    MessageResolver.getMessage("tptAuth.updatePasswordFailed.message"),
			    updatePasswordFailedDescription);
		    LOGGER.error(updatePasswordFailedDescription, e);

		}
	    }
	}

	userProfileService.updateUserProfile(taskIds[0], userInfo);

	return response;
    }

    private void addMessageToResponse(TaskResponse response, UserMessageTypeEnum messageType, String reasons,
	    String description) {
	response.addObject(REASONS, reasons);
	response.addObject(DESCRIPTION, description);
	response.addObject(TYPE, messageType);
    }

    private UserInfo createUserInfo(UserInfoCommand userInfoCommand, TmUserProfile userProfile) {
	UserInfo userInfo = userProfile.getUserInfo();

	userInfo.setUserType(userInfoCommand.getUserType());
	userInfo.setAddress(userInfoCommand.getAddress());
	userInfo.setDepartment(userInfoCommand.getDepartment());
	userInfo.setEmailAddress(userInfoCommand.getEmailAddress());
	userInfo.setEmailNotification(userInfoCommand.getEmailNotification());
	userInfo.setFax(userInfoCommand.getFax());
	userInfo.setFirstName(userInfoCommand.getFirstName());
	userInfo.setLastName(userInfoCommand.getLastName());
	userInfo.setPhone1(userInfoCommand.getPhone1());
	userInfo.setPhone2(userInfoCommand.getPhone2());
	userInfo.setTimeZone(userInfoCommand.getTimeZone());
	userInfo.setEnabled(userInfoCommand.isEnabled());
	userInfo.setDescription(userInfoCommand.getDescription());
	userInfo.setShowDescription(userInfoCommand.getShowDescription());
	Boolean ssoUser = userInfoCommand.isSsoUser();
	userInfo.setSsoUser(ssoUser);
	userInfo.setPassword(userInfoCommand.getPassword());

	return userInfo;
    }

    private String getOauthSupportMail() {
	return _oauthSupportMail;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private TptOAuthUserManagerClient getoAuthClient() {
	return _oAuthClient;
    }
}
