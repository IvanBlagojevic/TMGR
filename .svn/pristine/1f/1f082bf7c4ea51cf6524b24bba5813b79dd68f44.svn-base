package org.gs4tr.termmanager.glossaryV2;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.eventlogging.api.EventThreadContext;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.spring.utils.SpringProfileUtils;
import org.gs4tr.foundation.modules.usermanager.model.AbstractUserProfile;
import org.gs4tr.foundation.modules.usermanager.oauth.TptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.usermanager.service.impl.AbstractUserProfileServiceImpl;
import org.gs4tr.foundation.modules.webmvc.logging.util.builder.HttpRequestThreadContextBuilder;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.glossaryV2.blacklist.update.BlacklistStrategyUpdater;
import org.gs4tr.termmanager.glossaryV2.logevent.LogEventExecutor;
import org.gs4tr.termmanager.glossaryV2.logging.util.GlossaryEventActionConstants;
import org.gs4tr.termmanager.glossaryV2.mbeans.SegmentSearchThreadPoolSize;
import org.gs4tr.termmanager.glossaryV2.update.GlossaryStrategyUpdater;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class BaseOperationsFactory {

    @Autowired
    private BlacklistStrategyUpdater _blacklistStrategyUpdater;

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ConnectionInfoHolder> _cacheGateway;

    @Autowired
    private CacheManager _cacheManager;

    @Autowired
    private GlossaryStrategyUpdater _glossaryStrategyUpdater;

    @Autowired
    private LogEventExecutor _logEventExecutor;

    @Autowired(required = false)
    private TptOAuthUserManagerClient _oAuthClient;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SegmentSearchThreadPoolSize _segmentSearchThreadPoolSize;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileService _userProfileService;

    @LogEvent(action = GlossaryEventActionConstants.GET_CONNECTION_INFO, actionCategory = TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2)
    public ConnectionInfoHolder getConnectionInfo(TmgrKey key) throws OperationsException {

	String username = key.getUser();
	/*
	 * If it's OAUTH profile enabled, we might use username as security ticket if
	 * security ticket is null or empty. (TERII-5554)
	 */
	String session = Objects.nonNull(key.getSession()) ? key.getSession() : key.getUser();

	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_USER_KEY, username);
	EventThreadContext.addProperty(HttpRequestThreadContextBuilder.HTTP_SESSION_ID_KEY, session);

	try {
	    ConnectionInfoHolder info = getConnectionInfoHolder(session);
	    if (info == null) {

		String password = key.getPass();
		loginUser(username, password);

		String shortcode = key.getProject();
		TmProject project = findProject(shortcode);
		Long projectId = project.getProjectId();

		validateLanguageDirections(key, projectId);

		info = new ConnectionInfoHolder();
		info.setProjectId(projectId);
		info.setProjectName(project.getProjectInfo().getName());
		info.setProjectShortCode(shortcode);
		info.setUserProfile(TmUserProfile.getCurrentUserProfile());
		info.setSource(key.getSource());
		info.setTarget(key.getTarget());
		info.setAuthentication(SecurityContextHolder.getContext().getAuthentication());

		info.getExportableStatuses().addAll(ServiceUtils.getExportableStatusList(project));

		getCacheGateway().put(CacheName.V2_GLOSSARY_SESSIONS, session, info);

	    } else {
		getSessionService().registerAuthentication(info.getAuthentication());
		// 24-May-2016: as per [Issue#TERII-4162]
		validateLanguageDirections(key, info.getProjectId());
	    }

	    return info;

	} catch (Exception exception) {
	    /* Caught exception is immediately rethrown */
	    throw exception;
	}
    }

    public LogEventExecutor getLogEventExecutor() {
	return _logEventExecutor;
    }

    private TmProject findProject(String shortcode) throws OperationsException {
	TmProject project = getProjectService().findProjectByShortCode(shortcode);
	if (project == null) {
	    throw new OperationsException(String.format(Messages.getString("BaseOperationsFactory.0"), shortcode), //$NON-NLS-1$
		    OperationsExceptionValue.AUTHORIZATION);
	}
	return project;
    }

    private CacheManager getCacheManager() {
	return _cacheManager;
    }

    private ConnectionInfoHolder getConnectionInfoHolder(String sessionKey) {
	return getCacheGateway().get(CacheName.V2_GLOSSARY_SESSIONS, sessionKey);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private TptOAuthUserManagerClient getoAuthClient() {
	return _oAuthClient;
    }

    private boolean isSSO(String username, String password) {
	return SpringProfileUtils.checkIfSpringProfileIsActive(SpringProfileUtils.OAUTH_AUTHENTICATION_PROFILE)
		&& getoAuthClient().authenticate(username, password);
    }

    private void loginUser(String username, String password) throws OperationsException {
	/* Issue TERII-5912 */
	getCacheManager().getCache(AbstractUserProfileServiceImpl.USER_PROFILE_CACHE).evict(username);

	if (isSSO(username, password)) {
	    UserDetails userDetails;
	    try {
		userDetails = getUserProfileService().loadUserByEmail(username);
	    } catch (Exception e) {
		throw new OperationsException(Messages.getString("BaseOperationsFactory.6"), //$NON-NLS-1$
			OperationsExceptionValue.AUTHENTICATION);
	    }
	    getSessionService().registerAuthentication(
		    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
	} else {
	    loginUserFromTmgr(username, password);
	}
    }

    private void loginUserFromTmgr(String username, String password) throws OperationsException {
	AbstractUserProfile userProfile = getSessionService().login(username, password);
	if (userProfile == null) {
	    throw new OperationsException(Messages.getString("BaseOperationsFactory.1"),
		    OperationsExceptionValue.AUTHENTICATION);
	}
    }

    private void validateLanguageDirections(TmgrKey key, Long projectId) throws OperationsException {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> userLanguageIds = userProfile.getProjectUserLanguages().get(projectId);

	if (CollectionUtils.isEmpty(userLanguageIds)) {
	    throw new OperationsException(String.format(Messages.getString("BaseOperationsFactory.3"), //$NON-NLS-1$
		    userProfile.getUserName()), OperationsExceptionValue.AUTHORIZATION);
	}
	if (!userLanguageIds.contains(key.getSource())) {
	    throw new OperationsException(String.format(Messages.getString("BaseOperationsFactory.4"), // $NON-NLS-1$
		    key.getSource()), OperationsExceptionValue.AUTHORIZATION);
	}
	if (!userLanguageIds.contains(key.getTarget())) {
	    throw new OperationsException(String.format(Messages.getString("BaseOperationsFactory.5"), //$NON-NLS-1$
		    key.getTarget()), OperationsExceptionValue.AUTHORIZATION);
	}
    }

    protected BlacklistStrategyUpdater getBlacklistStrategyUpdater() {
	return _blacklistStrategyUpdater;
    }

    protected CacheGateway<String, ConnectionInfoHolder> getCacheGateway() {
	return _cacheGateway;
    }

    protected GlossaryStrategyUpdater getGlossaryStrategyUpdater() {
	return _glossaryStrategyUpdater;
    }

    protected SegmentSearchThreadPoolSize getSegmentSearchThreadPoolSize() {
	return _segmentSearchThreadPoolSize;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected boolean isReadOnly(ConnectionInfoHolder info) {
	TmUserProfile user = info.getUserProfile();

	String[] policies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };

	return !user.containsContextPolicies(info.getProjectId(), policies);
    }
}
