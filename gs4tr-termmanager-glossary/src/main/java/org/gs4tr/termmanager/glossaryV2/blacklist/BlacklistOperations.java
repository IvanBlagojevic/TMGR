package org.gs4tr.termmanager.glossaryV2.blacklist;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.glossaryV2.BaseOperations;
import org.gs4tr.termmanager.glossaryV2.blacklist.update.BlacklistStrategyUpdater;
import org.gs4tr.termmanager.glossaryV2.converter.BlacklistTermConverter;
import org.gs4tr.termmanager.glossaryV2.logevent.LogEventExecutor;
import org.gs4tr.termmanager.glossaryV2.update.GlossaryStrategyUpdater;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.LiteSegment;
import org.gs4tr.tm3.api.LiteSegmentSearchQuery;
import org.gs4tr.tm3.api.QueryMatchLocation;
import org.gs4tr.tm3.api.ServerInfo;
import org.gs4tr.tm3.api.TermsHolder;
import org.gs4tr.tm3.api.TmException;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.gs4tr.tm3.httpconnector.resolver.model.BlacklistUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.gs4tr.tm3.httpconnector.tmgr.TmgrBlacklistHttpConnectionInfo;

public class BlacklistOperations extends BaseOperations implements ITmgrBlacklistOperations {

    private static final boolean BLACKLIST_SEARCH = true;

    private static final Log LOGGER = LogFactory.getLog(BlacklistOperations.class);

    public BlacklistOperations(TmgrKey key, ResolverContext resolverContext, ConnectionInfoHolder infoHolder,
	    TermEntryService termEntryService, GlossaryStrategyUpdater updaterStrategy,
	    BlacklistStrategyUpdater blacklistStrategy, CacheGateway<String, ConnectionInfoHolder> cacheGateway,
	    LogEventExecutor logEventExecutor, int synonymNumber, int segmentSearchThreadPoolSize) {

	super(key, resolverContext, infoHolder, termEntryService, updaterStrategy, blacklistStrategy, cacheGateway,
		logEventExecutor, synonymNumber, segmentSearchThreadPoolSize);
    }

    @Override
    public ServerInfo connect(TmgrBlacklistHttpConnectionInfo tmgrBlacklistHttpConnectionInfo)
	    throws OperationsException {
	return connectInternal(TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2_BLACKLIST);
    }

    @Override
    public void delete(BlacklistTerm term) throws OperationsException {
	deleteTermInternal(term.getId(), term.getText(), null, term.getLocale().getCode(), null,
		TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2_BLACKLIST);
    }

    @Override
    public void disconnect() throws OperationsException {
	disconnectInternal(TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2_BLACKLIST);
    }

    @Override
    public ResolverContext getResolverContext() throws OperationsException {
	return getInternalResolverContext();
    }

    /**
     * @implNote This implementation can be better (i.e segment search for batch of
     *           segments can run faster) because there is exploitable parallelism
     *           within a single batch segment search request. But that isn't a
     *           trivially so avoid premature optimization until it can be measured.
     */
    @Override
    @SuppressWarnings("unchecked")
    // Log Event
    public List<TermsHolder<BlacklistTerm>> segmentSearch(LiteSegmentSearchQuery query) throws OperationsException {
	return (List<TermsHolder<BlacklistTerm>>) getLogEventExecutor().logEvent(() -> segmentSearchExec(query),
		TMGREventActionConstants.ACTION_SEGMENT_SEARCH,
		TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2_BLACKLIST);
    }

    @Override
    // Log Event
    public BatchProcessResult update(BlacklistUpdateRequest request) throws OperationsException {
	return (BatchProcessResult) getLogEventExecutor().logEvent(() -> updateExec(request),
		TMGREventActionConstants.ACTION_ADD_TERM, TMGREventActionConstants.ACTION_TYPE_GLOSSARY_V2_BLACKLIST);
    }

    private List<BlacklistTerm> convertToBlacklistTerms(List<TermEntry> termEntries, QueryMatchLocation location,
	    String username) {
	if (CollectionUtils.isEmpty(termEntries)) {
	    return new ArrayList<>();
	}
	Locale locale = resolveLocale(location);

	return termEntries.stream()
		.map(termEntry -> BlacklistTermConverter.buildBlacklistTermFromTermEntry(termEntry, locale, username,
			getInfoHolder().getExportableStatuses()))
		.filter(CollectionUtils::isNotEmpty).flatMap(List::stream).collect(toList());
    }

    private Locale resolveLocale(QueryMatchLocation location) {
	return QueryMatchLocation.SOURCE == location ? Locale.get(getKey().getSource())
		: Locale.get(getKey().getTarget());
    }

    private List<TermsHolder<BlacklistTerm>> segmentSearchExec(LiteSegmentSearchQuery query)
	    throws OperationsException {
	addProjectAndLanguageInfosToEventProperties();

	Validate.notNull(query, Messages.getString("BlacklistOperations.0")); //$NON-NLS-1$
	List<LiteSegment> liteSegments = query.getSegments();
	if (CollectionUtils.isEmpty(liteSegments)) {
	    return new ArrayList<>(0);
	}
	QueryMatchLocation location = query.getLocation();
	String username = TmUserProfile.getCurrentUserName();
	boolean includeFuzzy = query.isFuzzySearchEnabled();
	int length = query.getMaxResults();

	List<TermsHolder<BlacklistTerm>> results = new ArrayList<>();

	for (final LiteSegment ls : liteSegments) {
	    TmgrSearchFilter filter = createSegmentSearchFilter(ls, location, length, includeFuzzy, BLACKLIST_SEARCH);
	    List<TermEntry> termEntries = searchSegmentInternal(filter);
	    List<BlacklistTerm> terms = convertToBlacklistTerms(termEntries, location, username);

	    TermsHolder<BlacklistTerm> holder = new TermsHolder<>(terms);
	    results.add(holder);
	}

	return results;
    }

    private BatchProcessResult updateExec(BlacklistUpdateRequest request) throws OperationsException {
	addProjectAndLanguageInfosToEventProperties();

	ConnectionInfoHolder info = getInfoHolder();

	validateWriteAccess(info);

	BlacklistUpdateRequestExt requestExt = new BlacklistUpdateRequestExt(request);
	requestExt.setProjectId(info.getProjectId());
	requestExt.setShortCode(info.getProjectShortCode());

	TmgrKey key = getKey();

	requestExt.setSourceLanguageId(key.getSource());
	requestExt.setTargetLanguageId(key.getTarget());

	BatchProcessResult result = new BatchProcessResult();

	try {
	    result = getBlacklistStrategy().update(requestExt);
	} catch (Exception e) {
	    String message = e.getMessage();
	    result.addErrorMessage(message);
	    if (e instanceof TmException) {
		TmException tmEx = (TmException) e;
		result.setError(tmEx);
	    }
	    LOGGER.error(e);
	}

	return result;

    }
}
