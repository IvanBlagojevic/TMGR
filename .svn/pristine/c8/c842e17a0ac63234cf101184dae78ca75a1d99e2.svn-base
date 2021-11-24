package org.gs4tr.termmanager.glossaryV2.update;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.glossaryV2.GlossaryUpdateRequestExt;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("glossaryStrategyUpdater")
public class GlossaryStrategyUpdater {

    private static final Log _logger = LogFactory.getLog(GlossaryStrategyUpdater.class);

    private static final Map<UpdateOption, GlossaryUpdater> UPDATER_MAP = new HashMap<UpdateOption, GlossaryUpdater>();

    @Autowired
    @Qualifier("appendUpdater")
    private GlossaryUpdater _appendUpdater;

    @Autowired
    @Qualifier("doNotOverwriteIdenticalAttributesUpdater")
    private GlossaryUpdater _doNotOverwriteIdenticalAttributesUpdater;

    @Autowired
    @Qualifier("doNotOverwriteUpdater")
    private GlossaryUpdater _doNotOverwriteUpdater;

    @Autowired
    @Qualifier("overwriteIdenticalAttributesUpdater")
    private GlossaryUpdater _overwriteIdenticalAttributesUpdater;

    @Autowired
    @Qualifier("overwriteUpdater")
    private GlossaryUpdater _overwriteUpdater;

    @Autowired
    @Qualifier("skipUpdater")
    private GlossaryUpdater _skipUpdater;

    @PostConstruct
    public void populateUpdaterMap() {
	UPDATER_MAP.put(UpdateOption.APPEND, getAppendUpdater());
	UPDATER_MAP.put(UpdateOption.DONTOVERWRITE, getDoNotOverwriteUpdater());
	UPDATER_MAP.put(UpdateOption.DONTOVERWRITE_WHEN_IDENTICAL_ATTRIBUTES,
		getDoNotOverwriteIdenticalAttributesUpdater());
	UPDATER_MAP.put(UpdateOption.OVERWRITE, getOverwriteUpdater());
	UPDATER_MAP.put(UpdateOption.OVERWRITE_WHEN_IDENTICAL_ATTRIBUTES, getOverwriteIdenticalAttributesUpdater());
	UPDATER_MAP.put(UpdateOption.SKIP, getSkipUpdater());
    }

    public BatchProcessResult update(GlossaryUpdateRequestExt request) throws OperationsException {
	UpdateOption option = request.getOption();
	if (option == null) {
	    String message = Messages.getString("GlossaryStrategyUpdater.0"); //$NON-NLS-1$
	    _logger.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	GlossaryUpdater updater = UPDATER_MAP.get(option);
	if (updater == null) {
	    String message = String.format(Messages.getString("GlossaryStrategyUpdater.1"), //$NON-NLS-1$
		    option.name());
	    _logger.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	LogHelper.debug(_logger,
		String.format(Messages.getString("GlossaryStrategyUpdater.2"), //$NON-NLS-1$
			TmUserProfile.getCurrentUserName(), request.getOption().name(), request.getShortCode(),
			request.size()));

	return updater.update(request);
    }

    private GlossaryUpdater getAppendUpdater() {
	return _appendUpdater;
    }

    private GlossaryUpdater getDoNotOverwriteIdenticalAttributesUpdater() {
	return _doNotOverwriteIdenticalAttributesUpdater;
    }

    private GlossaryUpdater getDoNotOverwriteUpdater() {
	return _doNotOverwriteUpdater;
    }

    private GlossaryUpdater getOverwriteIdenticalAttributesUpdater() {
	return _overwriteIdenticalAttributesUpdater;
    }

    private GlossaryUpdater getOverwriteUpdater() {
	return _overwriteUpdater;
    }

    private GlossaryUpdater getSkipUpdater() {
	return _skipUpdater;
    }
}
