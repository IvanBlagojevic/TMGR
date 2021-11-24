package org.gs4tr.termmanager.glossaryV2;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperationsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("glossaryOperationsFactory")
public class GlossaryOperationsFactory extends BaseOperationsFactory implements ITmgrGlossaryOperationsFactory {

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Override
    public ITmgrGlossaryOperations getOperations(TmgrKey key) throws OperationsException {

	ConnectionInfoHolder info = getConnectionInfo(key);

	ResolverContext context = new ResolverContext();
	context.setSourceLocale(Locale.get(key.getSource()));
	context.setTargetLocale(Locale.get(key.getTarget()));
	context.setConnectionName(info.getProjectShortCode());
	context.setReadOnly(isReadOnly(info));

	int segmentSearchThreadPoolSize = getSegmentSearchThreadPoolSize().getThreadPoolSize();

	return new GlossaryOperations(key, context, info, getTermEntryService(), getGlossaryStrategyUpdater(),
		getBlacklistStrategyUpdater(), getCacheGateway(), getLogEventExecutor(), getSynonymNumber(),
		segmentSearchThreadPoolSize);
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }
}