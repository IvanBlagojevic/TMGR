package org.gs4tr.termmanager.glossaryV2.blacklist;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.glossaryV2.BaseOperationsFactory;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperationsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("blacklistOperationsFactory")
public class BlacklistOperationsFactory extends BaseOperationsFactory implements ITmgrBlacklistOperationsFactory {

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Override
    public ITmgrBlacklistOperations getOperations(TmgrKey key) throws OperationsException {

	ConnectionInfoHolder info = getConnectionInfo(key);

	ResolverContext context = new ResolverContext();
	context.setSourceLocale(Locale.get(key.getSource()));
	context.setTargetLocale(Locale.get(key.getTarget()));
	context.setConnectionName(info.getProjectShortCode());
	context.setReadOnly(isReadOnly(info));

	int segmentSearchThreadPoolSize = getSegmentSearchThreadPoolSize().getThreadPoolSize();

	return new BlacklistOperations(key, context, info, getTermEntryService(), getGlossaryStrategyUpdater(),
		getBlacklistStrategyUpdater(), getCacheGateway(), getLogEventExecutor(), getSynonymNumber(),
		segmentSearchThreadPoolSize);
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }
}