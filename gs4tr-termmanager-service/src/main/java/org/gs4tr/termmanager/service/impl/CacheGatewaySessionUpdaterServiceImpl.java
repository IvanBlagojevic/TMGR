package org.gs4tr.termmanager.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.concurrency.RunnableCallback;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.AssignUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.UserLanguageCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
 *TERII-4174 | TMGR should break connection if it becomes invalid
 */
@Service
public class CacheGatewaySessionUpdaterServiceImpl implements CacheGatewaySessionUpdaterService {

    private static final String ASYNC_ON_DISABLE_PROJECT = "CacheGatewaySessionUpdaterServiceImpl.removeOnDisableProject";

    private static final String ASYNC_ON_DISABLE_USER = "CacheGatewaySessionUpdaterServiceImpl.removeOnDisableUser";

    private static final String ASYNC_ON_EDIT_PROJECT = "CacheGatewaySessionUpdaterServiceImpl.removeOnEditProjectUser";

    private static final String ASYNC_ON_EDIT_PROJECT_LANGUAGE = "CacheGatewaySessionUpdaterServiceImpl.removeOnEditProjectLanguage";

    private static final String ASYNC_ON_EDIT_USER_LANGUAGE = "CacheGatewaySessionUpdaterServiceImpl.removeOnEditUserLanguage";

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ConnectionInfoHolder> _cacheGateway;

    @Override
    public void removeOnDisableProject(TmProject project) {

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		if (Objects.isNull(project)) {
		    return;
		}

		Iterator<Map.Entry<String, ConnectionInfoHolder>> itr = getCacheSessionsIterator();

		Set<String> sessionsToRemove = new HashSet<>();

		while (itr.hasNext()) {
		    Map.Entry<String, ConnectionInfoHolder> entry = itr.next();

		    ConnectionInfoHolder connectionInfoHolder = entry.getValue();

		    if (connectionInfoHolder.getProjectId().equals(project.getProjectId())) {
			sessionsToRemove.add(entry.getKey());
		    }
		}

		removeSessions(sessionsToRemove);
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_ON_DISABLE_PROJECT;
	    }
	}, SecurityContextHolder.getContext().getAuthentication());

    }

    @Override
    public void removeOnDisableUser(TmUserProfile user) {

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		if (Objects.isNull(user)) {
		    return;
		}

		Iterator<Map.Entry<String, ConnectionInfoHolder>> itr = getCacheSessionsIterator();

		Set<String> sessionsToRemove = new HashSet<>();

		while (itr.hasNext()) {
		    Map.Entry<String, ConnectionInfoHolder> entry = itr.next();

		    ConnectionInfoHolder connectionInfoHolder = entry.getValue();

		    Long userProfileId = connectionInfoHolder.getUserProfile().getUserProfileId();

		    if (userProfileId.equals(user.getUserProfileId())) {
			sessionsToRemove.add(entry.getKey());
		    }
		}

		removeSessions(sessionsToRemove);
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_ON_DISABLE_USER;
	    }

	}, SecurityContextHolder.getContext().getAuthentication());

    }

    @Override
    public void removeOnEditProjectLanguage(Long projectId, List<String> newProjectLanguages) {

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		if (Objects.isNull(projectId) || Objects.isNull(newProjectLanguages)) {
		    return;
		}

		Iterator<Map.Entry<String, ConnectionInfoHolder>> itr = getCacheSessionsIterator();

		Set<String> sessionsToRemove = new HashSet<>();

		while (itr.hasNext()) {
		    Map.Entry<String, ConnectionInfoHolder> entry = itr.next();

		    ConnectionInfoHolder connectionInfoHolder = entry.getValue();

		    Long connectionProjectId = connectionInfoHolder.getProjectId();

		    if (!connectionProjectId.equals(projectId)) {
			continue;
		    }

		    boolean isUserLangRemoved = isConnectionLangRemoved(connectionInfoHolder, newProjectLanguages);

		    if (isUserLangRemoved) {
			sessionsToRemove.add(entry.getKey());
		    }
		}

		removeSessions(sessionsToRemove);
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_ON_EDIT_PROJECT_LANGUAGE;
	    }
	}, SecurityContextHolder.getContext().getAuthentication());

    }

    /*
     * Remove session if project user language is removed or user is removed from
     * the project
     */
    @Override
    public void removeOnEditProjectUser(List<Long> userProfileIds, Long projectId,
	    AssignProjectUserLanguageCommand assignProjectUserLanguageCommand) {

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		if (Objects.isNull(userProfileIds) || Objects.isNull(projectId)
			|| Objects.isNull(assignProjectUserLanguageCommand)) {
		    return;
		}

		Iterator<Map.Entry<String, ConnectionInfoHolder>> itr = getCacheSessionsIterator();

		Set<String> sessionsToRemove = new HashSet<>();

		while (itr.hasNext()) {
		    Map.Entry<String, ConnectionInfoHolder> entry = itr.next();

		    ConnectionInfoHolder connectionInfoHolder = entry.getValue();

		    Long connectionProjectId = connectionInfoHolder.getProjectId();

		    if (!connectionProjectId.equals(projectId)) {
			continue;
		    }

		    Long connectionUserProfileId = connectionInfoHolder.getUserProfile().getUserProfileId();

		    if (!userProfileIds.contains(connectionUserProfileId)) {
			sessionsToRemove.add(entry.getKey());
			continue;
		    }

		    List<UserLanguageCommand> userLanguageCommands = assignProjectUserLanguageCommand.getUsers();
		    UserLanguageCommand userLanguageCommand = getCommandForUser(connectionUserProfileId,
			    userLanguageCommands);

		    if (Objects.nonNull(userLanguageCommand)) {

			boolean isUserLanguageRemoved = isConnectionLangRemoved(connectionInfoHolder,
				userLanguageCommand.getUserLanguages());

			if (isUserLanguageRemoved) {
			    sessionsToRemove.add(entry.getKey());
			}

		    }

		}

		removeSessions(sessionsToRemove);
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_ON_EDIT_PROJECT;
	    }
	}, SecurityContextHolder.getContext().getAuthentication());

    }

    @Override
    public void removeOnEditUserLanguage(Long userProfileId, AssignUserLanguageCommand assignCommand) {

	ServiceThreadPoolHandler.execute(new RunnableCallback() {
	    @Override
	    public void execute() {
		if (Objects.isNull(userProfileId) || Objects.isNull(assignCommand)) {
		    return;
		}

		List<String> newUserLanguages = assignCommand.getUserLanguages();

		if (CollectionUtils.isNotEmpty(newUserLanguages)) {

		    Iterator<Map.Entry<String, ConnectionInfoHolder>> itr = getCacheSessionsIterator();

		    Set<String> sessionsToRemove = new HashSet<>();

		    while (itr.hasNext()) {
			Map.Entry<String, ConnectionInfoHolder> connectionEntry = itr.next();

			ConnectionInfoHolder connectionInfoHolder = connectionEntry.getValue();

			Long connectionUserProfileId = connectionInfoHolder.getUserProfile().getUserProfileId();

			if (!connectionUserProfileId.equals(userProfileId)) {
			    continue;
			}

			String session = connectionEntry.getKey();

			boolean isUserLanguageRemoved = isConnectionLangRemoved(connectionInfoHolder, newUserLanguages);

			if (isUserLanguageRemoved) {
			    sessionsToRemove.add(session);
			}

		    }

		    removeSessions(sessionsToRemove);
		}
	    }

	    @Override
	    public String getRunnableOperation() {
		return ASYNC_ON_EDIT_USER_LANGUAGE;
	    }
	}, SecurityContextHolder.getContext().getAuthentication());
    }

    private CacheGateway<String, ConnectionInfoHolder> getCacheGateway() {
	return _cacheGateway;
    }

    private Iterator<Map.Entry<String, ConnectionInfoHolder>> getCacheSessionsIterator() {
	Set<Map.Entry<String, ConnectionInfoHolder>> entrySet = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS).entrySet();
	return entrySet.iterator();
    }

    private UserLanguageCommand getCommandForUser(Long userId, List<UserLanguageCommand> userLanguageCommands) {
	UserLanguageCommand userLanguageCommand = null;

	Optional<UserLanguageCommand> optional = userLanguageCommands.stream()
		.filter(ulc -> ulc.getUserId().equals(userId)).findFirst();

	if (optional.isPresent()) {
	    userLanguageCommand = optional.get();
	}
	return userLanguageCommand;
    }

    private boolean isConnectionLangRemoved(ConnectionInfoHolder infoHolder, Collection<String> newLanguages) {
	return !newLanguages.contains(infoHolder.getSource()) || !newLanguages.contains(infoHolder.getTarget());
    }

    private void removeSessions(Set<String> sessionsToRemove) {
	sessionsToRemove
		.forEach(sessionToRemove -> getCacheGateway().remove(CacheName.V2_GLOSSARY_SESSIONS, sessionToRemove));
    }

}
