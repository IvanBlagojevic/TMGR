package org.gs4tr.termmanager.glossaryV2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.glossaryV2.blacklist.update.BlacklistStrategyUpdater;
import org.gs4tr.termmanager.glossaryV2.logevent.LogEventExecutor;
import org.gs4tr.termmanager.glossaryV2.logging.builder.LanguageInfoEventDataBuilder;
import org.gs4tr.termmanager.glossaryV2.logging.builder.ProjectInfoEventDataBuilder;
import org.gs4tr.termmanager.glossaryV2.update.GlossaryStrategyUpdater;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.utils.UpdateCommandUtils;
import org.gs4tr.tm3.api.LiteSegment;
import org.gs4tr.tm3.api.QueryMatchLocation;
import org.gs4tr.tm3.api.ServerInfo;
import org.gs4tr.tm3.api.glossary.GlossaryConcordanceQuery;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseOperations {

    private static final String[] ADD_POLICIES_ARRAY = new String[] {
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };

    private static final List<String> BLACKLIST_OPERATIONS_STATUSES = new ArrayList<>(
	    Collections.singletonList(ItemStatusTypeHolder.BLACKLISTED.getName()));

    private static final List<String> GLOSSARY_OPERATIONS_STATUSES = new ArrayList<>(
	    Arrays.asList(ItemStatusTypeHolder.ON_HOLD.getName(), ItemStatusTypeHolder.PROCESSED.getName(),
		    ItemStatusTypeHolder.WAITING.getName()));

    private static final Log LOGGER = LogFactory.getLog(BaseOperations.class);

    private static final String VERSION = "1"; //$NON-NLS-1$

    private BlacklistStrategyUpdater _blacklistStrategy;

    private CacheGateway<String, ConnectionInfoHolder> _cacheGateway;

    private ConnectionInfoHolder _infoHolder;

    private TmgrKey _key;

    private LogEventExecutor _logEventExecutor;

    private ResolverContext _resolverContext;

    private int _segmentSearchThreadPoolSize;

    private int _synonymNumber;

    private TermEntryService _termEntryService;

    private GlossaryStrategyUpdater _updaterStrategy;

    public BaseOperations(TmgrKey key, ResolverContext resolverContext, ConnectionInfoHolder infoHolder,
	    TermEntryService termEntryService, GlossaryStrategyUpdater updaterStrategy,
	    BlacklistStrategyUpdater blacklistStrategy, CacheGateway<String, ConnectionInfoHolder> cacheGateway,
	    LogEventExecutor logEventExecutor, int synonymNumber, int segmentSearchThreadPoolSize) {

	_key = key;
	_resolverContext = resolverContext;
	_termEntryService = termEntryService;
	_infoHolder = infoHolder;
	_updaterStrategy = updaterStrategy;
	_blacklistStrategy = blacklistStrategy;
	_cacheGateway = cacheGateway;
	_logEventExecutor = logEventExecutor;
	_synonymNumber = synonymNumber;
	_segmentSearchThreadPoolSize = segmentSearchThreadPoolSize;
    }

    // Log Event
    public ServerInfo connectInternal(String category) throws OperationsException {
	return (ServerInfo) getLogEventExecutor().logEvent(this::connectInternalExec,
		TMGREventActionConstants.ACTION_CONNECT, category);
    }

    // Log Event
    public void deleteTermInternal(String termEntryId, String sourceText, String targetText, String sourceCode,
	    String targetCode, String category) throws OperationsException {
	getLogEventExecutor().logEvent(
		() -> deleteTermInternalExec(termEntryId, sourceText, targetText, sourceCode, targetCode),
		TMGREventActionConstants.ACTION_DELETE_TERM, category);
    }

    // Log Event
    public void disconnectInternal(String category) throws OperationsException {
	getLogEventExecutor().logEvent(this::disconnectInternalExec, TMGREventActionConstants.ACTION_DISCONNECT,
		category);
    }

    public void disconnectInternalExec() throws OperationsException {
	addProjectAndLanguageInfosToEventProperties();

	String session = getKey().getSession();
	getCacheGateway().remove(CacheName.V2_GLOSSARY_SESSIONS, session);
	clearContext();
    }

    public LogEventExecutor getLogEventExecutor() {
	return _logEventExecutor;
    }

    private void clearContext() {
	UserProfileContext.clearContext();
	SecurityContextHolder.clearContext();
    }

    private List<UpdateCommand> collectCommands(String termText, Set<Term> terms) {
	return terms.stream().filter(t -> t.getName().equals(termText))
		.map(UpdateCommandUtils::createRemoveCommandFromTerm).collect(Collectors.toList());
    }

    private ServerInfo connectInternalExec() {
	TmgrKey key = getKey();
	ServerInfo info = new ServerInfo();

	addProjectAndLanguageInfosToEventProperties();

	info.setSourceLocale(Locale.get(key.getSource()));
	info.setTargetLocale(Locale.get(key.getTarget()));

	info.setReadOnly(getInternalResolverContext().getReadOnly());
	info.setName(key.getProject());
	info.setVersion(VERSION);

	return info;
    }

    private List<TranslationUnit> createTranslationUnits(TermEntry termEntry, String sourceText, String targetText,
	    String sourceCode, String targetCode) {

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	TranslationUnit tu = new TranslationUnit();
	tu.setTermEntryId(termEntry.getUuId());

	if (sourceText != null && sourceCode != null) {
	    Set<Term> sourceTerms = languageTerms.get(sourceCode);
	    if (CollectionUtils.isNotEmpty(sourceTerms)) {
		tu.setSourceTermUpdateCommands(collectCommands(sourceText, sourceTerms));
	    }
	}

	if (targetText != null && targetCode != null) {
	    Set<Term> targetTerms = languageTerms.get(targetCode);
	    if (CollectionUtils.isNotEmpty(targetTerms)) {
		tu.setTargetTermUpdateCommands(collectCommands(targetText, targetTerms));
	    }
	}

	List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
	tus.add(tu);

	return tus;
    }

    private void deleteTermInternalExec(String termEntryId, String sourceText, String targetText, String sourceCode,
	    String targetCode) throws OperationsException {

	addProjectAndLanguageInfosToEventProperties();

	if (StringUtils.isEmpty(termEntryId)) {
	    String message = Messages.getString("BaseOperations.0"); //$NON-NLS-1$
	    LOGGER.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	Long projectId = null;

	ConnectionInfoHolder info = getInfoHolder();
	if (info != null) {
	    projectId = info.getProjectId();
	}

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	if (termEntry == null) {
	    String message = Messages.getString("BaseOperations.1"); //$NON-NLS-1$
	    LOGGER.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms == null) {
	    return;
	}

	try {
	    List<TranslationUnit> tus = createTranslationUnits(termEntry, sourceText, targetText, sourceCode,
		    targetCode);
	    if (tus != null) {
		getTermEntryService().updateTermEntries(tus, sourceCode, projectId, Action.EDITED_REMOTELY);
	    }
	} catch (Exception e) {
	    String message = e.getMessage();
	    LOGGER.error(message);
	    throw new OperationsException(message, OperationsExceptionValue.EXECUTION);
	}
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private boolean userHasWriteAccess(final Long projectId) {
	return TmUserProfile.getCurrentUserProfile().containsContextPolicies(projectId, ADD_POLICIES_ARRAY);
    }

    protected void addProjectAndLanguageInfosToEventProperties() {
	Map<String, Object> infoEventDataBuilderMap = new LanguageInfoEventDataBuilder(getKey()).build();
	infoEventDataBuilderMap.entrySet().stream()
		.forEach(entry -> EventLogger.addProperty(entry.getKey(), entry.getValue()));

	infoEventDataBuilderMap = new ProjectInfoEventDataBuilder(getInfoHolder()).build();
	infoEventDataBuilderMap.entrySet().stream()
		.forEach(entry -> EventLogger.addProperty(entry.getKey(), entry.getValue()));
    }

    /* All statuses are added due to new Working Terminology */
    protected TmgrSearchFilter createBasicFilter(QueryMatchLocation location, int maxResults,
	    boolean isBlacklistSearch) {
	TmgrSearchFilter filter = new TmgrSearchFilter();

	List<Long> projectIds = new ArrayList<Long>();
	projectIds.add(getInfoHolder().getProjectId());

	filter.setProjectIds(projectIds);

	List<String> statuses = createStatuses(isBlacklistSearch);

	filter.setStatuses(statuses);

	String source = getKey().getSource();
	String target = getKey().getTarget();

	if (location == null) {
	    location = QueryMatchLocation.SOURCE_AND_TARGET;
	}

	if (QueryMatchLocation.SOURCE == location || QueryMatchLocation.SOURCE_AND_TARGET == location) {
	    filter.setSourceLanguage(source);
	}

	if (QueryMatchLocation.TARGET == location || QueryMatchLocation.SOURCE_AND_TARGET == location) {
	    filter.setTargetLanguages(Arrays.asList(target));
	}

	List<String> languageIds = new ArrayList<String>();
	languageIds.add(source);
	languageIds.add(target);

	filter.addLanguageResultField(true, getSynonymNumber(), languageIds.toArray(new String[languageIds.size()]));

	filter.setPageable(new TmgrPageRequest(0, maxResults, null));

	return filter;
    }

    protected TmgrSearchFilter createConcordanceSearchFilter(GlossaryConcordanceQuery query)
	    throws OperationsException {
	QueryMatchLocation location = query.getLocation();

	TmgrSearchFilter filter = createBasicFilter(location, query.getMaxResults(), false);

	String text = query.getQuery();

	TextFilter textFilter;
	try {
	    textFilter = new TextFilter(text, query.isMatchWholeWords(), query.isCaseSensitive());
	} catch (IllegalArgumentException e) {
	    throw new OperationsException(e.getMessage(), e, OperationsExceptionValue.EXECUTION);
	}

	filter.setTextFilter(textFilter);

	return filter;
    }

    protected TmgrSearchFilter createSegmentSearchFilter(LiteSegment liteSegment, QueryMatchLocation location,
	    final int length, final boolean includeFuzzy, final boolean isBlacklistSearch) {
	TextFilter textFilter = createTextFilter(getSegmentText(liteSegment, location), true, includeFuzzy);
	TmgrSearchFilter searchFilter = createBasicFilter(location, length, isBlacklistSearch);
	searchFilter.setTextFilter(textFilter);
	return searchFilter;
    }

    protected List<String> createStatuses(boolean isBlacklistSearch) {
	if (isBlacklistSearch) {
	    return BLACKLIST_OPERATIONS_STATUSES;
	} else {
	    return GLOSSARY_OPERATIONS_STATUSES;
	}
    }

    protected TextFilter createTextFilter(String segmentText, boolean segmentSearch, boolean includeFuzzy) {
	TextFilter textFilter = new TextFilter(segmentText, !includeFuzzy);
	textFilter.setSegmentSearch(segmentSearch);
	textFilter.setFuzzyMatch(includeFuzzy);
	return textFilter;
    }

    protected BlacklistStrategyUpdater getBlacklistStrategy() {
	return _blacklistStrategy;
    }

    protected CacheGateway<String, ConnectionInfoHolder> getCacheGateway() {
	return _cacheGateway;
    }

    protected ConnectionInfoHolder getInfoHolder() {
	return _infoHolder;
    }

    protected ResolverContext getInternalResolverContext() {
	return _resolverContext;
    }

    protected TmgrKey getKey() {
	return _key;
    }

    protected int getSegmentSearchThreadPoolSize() {
	return _segmentSearchThreadPoolSize;
    }

    protected String getSegmentText(LiteSegment segment, QueryMatchLocation location) {
	return QueryMatchLocation.TARGET == location ? segment.getTarget() : segment.getSource();
    }

    protected GlossaryStrategyUpdater getStrategyUpdater() {
	return _updaterStrategy;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    protected List<TermEntry> searchInternal(TmgrSearchFilter searchFilter) {
	return getTermEntryService().searchTermEntries(searchFilter).getResults();
    }

    protected List<TermEntry> searchSegmentInternal(TmgrSearchFilter searchFilter) {
	return getTermEntryService().segmentTMSearch(searchFilter).getResults();
    }

    protected void validateWriteAccess(ConnectionInfoHolder infoHolder) throws OperationsException {
	if (!userHasWriteAccess(infoHolder.getProjectId())) {
	    throw new OperationsException(
		    String.format(Messages.getString("BaseOperations.2"), TmUserProfile.getCurrentUserName()),
		    OperationsExceptionValue.AUTHORIZATION);
	}
    }
}
