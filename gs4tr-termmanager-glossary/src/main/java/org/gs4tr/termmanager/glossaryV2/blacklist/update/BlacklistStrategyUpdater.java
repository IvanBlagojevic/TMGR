package org.gs4tr.termmanager.glossaryV2.blacklist.update;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.glossaryV2.blacklist.BlacklistUpdateRequestExt;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("blacklistStrategyUpdater")
public class BlacklistStrategyUpdater {

    private static final Log _logger = LogFactory.getLog(BlacklistStrategyUpdater.class);

    private static final Map<UpdateOption, BlacklistUpdater> UPDATER_MAP = new HashMap<UpdateOption, BlacklistUpdater>();

    @Autowired
    @Qualifier("blacklistAppendUpdater")
    private BlacklistUpdater _blacklistAppendUpdater;

    @Autowired
    @Qualifier("blacklistDoNotOverwriteIdenticalAttributesUpdater")
    private BlacklistUpdater _blacklistDoNotOverwriteIdenticalAttributesUpdater;

    @Autowired
    @Qualifier("blacklistDoNotOverwriteUpdater")
    private BlacklistUpdater _blacklistDoNotOverwriteUpdater;

    @Autowired
    @Qualifier("blacklistOverwriteIdenticalAttributesUpdater")
    private BlacklistUpdater _blacklistOverwriteIdenticalAttributesUpdater;

    @Autowired
    @Qualifier("blacklistOverwriteUpdater")
    private BlacklistUpdater _blacklistOverwriteUpdater;

    @Autowired
    @Qualifier("blacklistSkipUpdater")
    private BlacklistUpdater _blacklistSkipUpdater;

    @PostConstruct
    public void populateUpdaterMap() {
	UPDATER_MAP.put(UpdateOption.APPEND, getBlacklistAppendUpdater());
	UPDATER_MAP.put(UpdateOption.DONTOVERWRITE, getBlacklistDoNotOverwriteUpdater());
	UPDATER_MAP.put(UpdateOption.OVERWRITE, getBlacklistOverwriteUpdater());
	UPDATER_MAP.put(UpdateOption.DONTOVERWRITE_WHEN_IDENTICAL_ATTRIBUTES,
		getBlacklistDoNotOverwriteIdenticalAttributesUpdater());
	UPDATER_MAP.put(UpdateOption.OVERWRITE_WHEN_IDENTICAL_ATTRIBUTES,
		getBlacklistOverwriteIdenticalAttributesUpdater());
	UPDATER_MAP.put(UpdateOption.SKIP, getBlacklistSkipUpdater());
    }

    public BatchProcessResult update(BlacklistUpdateRequestExt request) throws OperationsException {
	UpdateOption option = request.getOption();
	if (option == null) {
	    String message = Messages.getString("BlacklistStrategyUpdater.0"); //$NON-NLS-1$
	    _logger.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	BlacklistUpdater updater = UPDATER_MAP.get(option);
	if (updater == null) {
	    String message = String.format(Messages.getString("BlacklistStrategyUpdater.1"), //$NON-NLS-1$
		    option.name());
	    _logger.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	LogHelper.debug(_logger,
		String.format(Messages.getString("BlacklistStrategyUpdater.2"), //$NON-NLS-1$
			TmUserProfile.getCurrentUserName(), request.getOption().name(), request.getShortCode(),
			request.size()));

	return updater.update(request);
    }

    private BlacklistUpdater getBlacklistAppendUpdater() {
	return _blacklistAppendUpdater;
    }

    private BlacklistUpdater getBlacklistDoNotOverwriteIdenticalAttributesUpdater() {
	return _blacklistDoNotOverwriteIdenticalAttributesUpdater;
    }

    private BlacklistUpdater getBlacklistDoNotOverwriteUpdater() {
	return _blacklistDoNotOverwriteUpdater;
    }

    private BlacklistUpdater getBlacklistOverwriteIdenticalAttributesUpdater() {
	return _blacklistOverwriteIdenticalAttributesUpdater;
    }

    private BlacklistUpdater getBlacklistOverwriteUpdater() {
	return _blacklistOverwriteUpdater;
    }

    private BlacklistUpdater getBlacklistSkipUpdater() {
	return _blacklistSkipUpdater;
    }
}
