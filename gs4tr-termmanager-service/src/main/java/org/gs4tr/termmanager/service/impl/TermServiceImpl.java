package org.gs4tr.termmanager.service.impl;

import static org.gs4tr.termmanager.service.utils.ServiceUtils.deleteDescriptions;
import static org.gs4tr.termmanager.service.utils.ServiceUtils.getDescriptionsByType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.types.BaseItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.counter.StrategyTermCounter;
import org.gs4tr.termmanager.service.utils.StatisticsUtils;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("termService")
public class TermServiceImpl extends AbstractNotifyingService implements TermService {

    @Autowired
    private StrategyTermCounter _strategyTermCounter;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Override
    public void approveTerms(List<String> termIds, Long projectId) {
	if (CollectionUtils.isEmpty(termIds)) {
	    return;
	}

	List<TermEntry> termEntries = findTermEntriesByTermIds(termIds, projectId);

	String userName = TmUserProfile.getCurrentUserName();
	Date dateModified = new Date();

	List<Term> terms = collectTermsByIds(termIds, termEntries, userName, dateModified);

	if (terms.isEmpty()) {
	    return;
	}

	ProjectDetailInfo detailInfo = new ProjectDetailInfo(projectId);
	detailInfo.setDateModified(dateModified);

	String status = ItemStatusTypeHolder.PROCESSED.getName();

	StrategyTermCounter strategyTermCounter = getStrategyTermCounter();

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();

	for (Term term : terms) {
	    String currentStatus = term.getStatus();
	    strategyTermCounter.updateTermCount(detailInfo, status, term, currentStatus);

	    StatisticsUtils.incrementStatistics(statisticsInfos, term.getProjectId(), term.getLanguageId(), status);

	    term.setStatus(status);
	    term.setUserModified(userName);
	    term.setDateModified(dateModified.getTime());
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("TermServiceImpl.12"), //$NON-NLS-1$
			userName, term.getName()));
	    }
	}

	updateRegularTermEntries(projectId, Action.EDITED.name(),
		new TransactionalUnit(termEntries, detailInfo, statisticsInfos));
    }

    @Override
    public void changeForbiddStatus(List<String> termIds, Long projectId) {
	TmProject project = getProjectService().load(projectId);

	List<Term> terms = new ArrayList<>();

	List<TermEntry> termEntries = findTermEntriesByTermIds(termIds, projectId);

	String userName = TmUserProfile.getCurrentUserName();
	Date dateModified = new Date();

	for (TermEntry termEntry : termEntries) {
	    for (String id : termIds) {
		Term term = termEntry.ggetTermById(id);
		if (term != null) {
		    terms.add(term);
		    termEntry.setAction(Action.EDITED);
		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);
		}
	    }
	}

	if (terms.isEmpty()) {
	    return;
	}

	ProjectDetailInfo detailInfo = new ProjectDetailInfo(projectId);
	detailInfo.setDateModified(dateModified);

	for (Term term : terms) {
	    Boolean forbidden = term.isForbidden();
	    String languageId = term.getLanguageId();
	    String oldStatus = term.getStatus();
	    String defaultTermsStatus = project.getDefaultTermStatus().getName();

	    String newStatus = forbidden ? defaultTermsStatus : ItemStatusTypeHolder.BLACKLISTED.getName();

	    term.setForbidden(!forbidden);
	    term.setDateModified(dateModified.getTime());
	    term.setUserModified(userName);
	    term.setStatus(newStatus);
	    term.setStatusOld(oldStatus);

	    // TODO: move this logic to BlacklistedCounterImpl
	    if (!forbidden) {
		detailInfo.incrementForbiddenTermCount(languageId);
		if (ItemStatusTypeHolder.PROCESSED.getName().equals(oldStatus)) {
		    detailInfo.decrementApprovedTermCount(languageId);
		}
	    } else {
		detailInfo.decrementForbiddenTermCount(languageId);
		if (ItemStatusTypeHolder.PROCESSED.getName().equals(newStatus)) {
		    detailInfo.incrementApprovedTermCount(languageId);
		}
	    }
	}
	updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(termEntries, detailInfo));
    }

    @Override
    @Transactional
    public void deleteTermDescriptionsByType(List<String> type, Long projectId, String baseType,
	    List<String> languageIds) {
	String sourceLanguageId = languageIds.get(0);

	List<String> targetLanguages = new ArrayList<>(languageIds);
	targetLanguages.remove(sourceLanguageId);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);
	filter.addAttributeType(type.toArray(new String[type.size()]));
	filter.setSourceLanguage(sourceLanguageId);
	filter.setTargetLanguages(targetLanguages);

	List<TermEntry> termEntries = browseTermEntries(filter);
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}

	final String userName = TmUserProfile.getCurrentUserName();
	final Date dateModified = new Date();

	Set<String> languages = new HashSet<>(languageIds.size() + 1);
	List<TermEntry> updatedTermEntries = new ArrayList<>();
	for (TermEntry termEntry : termEntries) {
	    List<Term> terms = termEntry.ggetTerms();
	    if (CollectionUtils.isEmpty(terms)) {
		continue;
	    }

	    boolean isTermEntryAddedForUpdated = false;
	    for (final Term term : terms) {
		Set<Description> descriptions = term.getDescriptions();
		if (deleteDescriptions(descriptions, type, baseType)) {
		    termEntry.setAction(Action.EDITED);
		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);
		    if (!isTermEntryAddedForUpdated) {
			updatedTermEntries.add(termEntry);
		    }
		    isTermEntryAddedForUpdated = true;
		    term.setDateModified(dateModified.getTime());
		    term.setUserModified(userName);
		    languages.add(term.getLanguageId());
		}
	    }
	}

	if (CollectionUtils.isNotEmpty(updatedTermEntries)) {
	    updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(updatedTermEntries));
	    // 31-May-2017, as per [Improvement#TERII-4225]:
	    getProjectDetailService().updateProjectAndLanguagesDateModified(projectId, languages, dateModified);
	}
    }

    @Override
    public void demoteTerms(List<String> termIds, Long projectId) {
	if (CollectionUtils.isEmpty(termIds)) {
	    return;
	}

	List<TermEntry> termEntries = findTermEntriesByTermIds(termIds, projectId);

	String userName = TmUserProfile.getCurrentUserName();
	Date dateModified = new Date();

	List<Term> terms = collectTermsByIds(termIds, termEntries, userName, dateModified);

	if (terms.isEmpty()) {
	    return;
	}

	ProjectDetailInfo detailInfo = new ProjectDetailInfo(projectId);
	detailInfo.setDateModified(dateModified);

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();

	String status = BaseItemStatusTypeHolder.WAITING.getName();

	for (Term term : terms) {
	    StatisticsUtils.incrementStatistics(statisticsInfos, term.getProjectId(), term.getLanguageId(), status);

	    String languageId = term.getLanguageId();

	    term.setStatus(status);
	    term.setDateModified(dateModified.getTime());
	    term.setUserModified(userName);
	    detailInfo.decrementApprovedTermCount(languageId);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("TermServiceImpl.11"), //$NON-NLS-1$
			userName, term.getName()));
	    }
	}

	updateRegularTermEntries(projectId, Action.EDITED.name(),
		new TransactionalUnit(termEntries, detailInfo, statisticsInfos));
    }

    @Override
    public Term findTermById(String termId, Long projectId) {
	if (StringUtils.isEmpty(termId)) {
	    return null;
	}

	try {
	    return getBrowser(getRegularCollection()).findTermById(termId, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public List<Term> findTermsByIds(Collection<String> termIds, List<Long> projectIds) {
	if (CollectionUtils.isEmpty(termIds)) {
	    return null;
	}

	try {
	    return getBrowser(getRegularCollection()).findTermsByIds(termIds, projectIds);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public List<Term> getAllTermsForMerge(Long projectId, String languageId, List<String> termNames) {
	List<String> lowercaseTermNames = new ArrayList<>();
	StringBuilder builder = new StringBuilder();
	for (String termName : termNames) {
	    builder.append(termName);
	    builder.append(StringConstants.SPACE);

	    lowercaseTermNames.add(termName.toLowerCase());
	}

	final String status = ItemStatusTypeHolder.PROCESSED.getName();

	List<String> statusList = new ArrayList<>();
	statusList.add(status);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);
	filter.setSourceLanguage(languageId);
	filter.setStatuses(statusList);

	TextFilter textFilter = new TextFilter(builder.toString(), true, false);
	filter.setTextFilter(textFilter);

	filter.addLanguageResultField(true, getSynonymNumber(), languageId);

	Page<TermEntry> page = searchTermEntries(filter);

	List<Term> machedTerms = new ArrayList<>();
	List<TermEntry> termEntries = page.getResults();
	if (CollectionUtils.isNotEmpty(termEntries)) {
	    for (TermEntry termEntry : termEntries) {
		for (Term term : termEntry.getLanguageTerms().get(languageId)) {
		    if (lowercaseTermNames.contains(term.getName().toLowerCase())) {
			machedTerms.add(term);
		    }
		}
	    }
	}

	CollectionUtils.filter(machedTerms, object -> {
	    Term term = (Term) object;
	    if (term.getInTranslationAsSource()) {
		return false;
	    }
	    return term.getStatus().equals(status);
	});

	return machedTerms;
    }

    @Override
    public List<Term> getAllTermsInTermEntry(String termEntryId, Long projectId) {
	List<Term> terms = new ArrayList<>();
	TermEntry termEntry = findTermEntryById(getRegularCollection(), termEntryId, projectId);

	if (termEntry == null) {
	    return terms;
	}
	Map<String, Set<Term>> termsMap = termEntry.getLanguageTerms();

	for (Map.Entry<String, Set<Term>> entry : termsMap.entrySet()) {
	    terms.addAll(entry.getValue());
	}
	return terms;
    }

    @Override
    public List<Term> getTermsByTermEntryIds(Collection<String> termEntryIds, List<Long> projectIds) {
	List<Term> terms = new ArrayList<>();
	List<TermEntry> termEntryList;
	try {
	    termEntryList = getBrowser(getRegularCollection()).findByIds(termEntryIds, projectIds);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

	if (CollectionUtils.isEmpty(termEntryList)) {
	    return terms;
	}
	for (TermEntry termEntry : termEntryList) {
	    Map<String, Set<Term>> termsMap = termEntry.getLanguageTerms();
	    if (termsMap != null) {
		for (Map.Entry<String, Set<Term>> entry : termsMap.entrySet()) {
		    terms.addAll(entry.getValue());
		}
	    }
	}
	return terms;
    }

    @Override
    public void renameTermDescriptions(Long projectId, final String baseType, List<Attribute> attributes,
	    List<String> languageIds) {
	List<String> types = new ArrayList<>();
	for (Attribute attribute : attributes) {
	    types.add(attribute.getName());
	}

	String sourceLanguageId = languageIds.get(0);

	List<String> targetLanguages = new ArrayList<>(languageIds);
	targetLanguages.remove(sourceLanguageId);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	String[] arrLocal = types.toArray(new String[types.size()]);

	if (baseType.equals(Description.NOTE)) {
	    filter.addNoteType(arrLocal);
	} else {
	    filter.addAttributeType(arrLocal);
	}

	filter.setSourceLanguage(sourceLanguageId);
	filter.setTargetLanguages(targetLanguages);

	List<TermEntry> termEntries = browseTermEntries(filter);
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}

	final String userName = TmUserProfile.getCurrentUserName();
	final Date dateModified = new Date();

	List<TermEntry> updatedTermEntries = new ArrayList<>();
	for (TermEntry termEntry : termEntries) {
	    List<Term> terms = termEntry.ggetTerms();
	    if (CollectionUtils.isEmpty(terms)) {
		continue;
	    }

	    boolean isTermEntryAddedForUpdated = false;
	    for (final Term term : terms) {
		Set<Description> descriptions = getDescriptionsByType(term, baseType);
		if (renameTermDescriptions(descriptions, attributes)) {
		    termEntry.setAction(Action.EDITED);
		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);
		    if (!isTermEntryAddedForUpdated) {
			updatedTermEntries.add(termEntry);
		    }
		    isTermEntryAddedForUpdated = true;

		    term.setDateModified(dateModified.getTime());
		    term.setUserModified(userName);
		}
	    }
	}

	if (CollectionUtils.isNotEmpty(updatedTermEntries)) {
	    // 31-May-2017, as per [Improvement#TERII-4225]:
	    ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	    info.setDateModified(new Date());
	    updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(updatedTermEntries, info));
	}
    }

    @Override
    public void updateUserLatestChangedTerms(Long userId, List<Long> projectIds) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(projectIds);
	filter.setUserLatestChange(userId);

	Map<Long, List<TermEntry>> updatedTermEntries = new HashMap<>();

	try {
	    List<TermEntry> termEntries = getBrowser(getRegularCollection()).browse(filter);
	    for (TermEntry termEntry : termEntries) {
		List<Term> terms = termEntry.ggetTerms();
		if (CollectionUtils.isNotEmpty(terms)) {
		    for (Term term : terms) {
			if (userId.equals(term.getUserLatestChange())) {
			    term.setUserLatestChange(null);
			    List<TermEntry> updated = updatedTermEntries.get(termEntry.getProjectId());
			    if (Objects.isNull(updated)) {
				updated = new ArrayList<>();
				updatedTermEntries.putIfAbsent(termEntry.getProjectId(), updated);
			    }
			    updated.add(termEntry);
			}
		    }
		}
	    }
	    if (!updatedTermEntries.isEmpty()) {
		for (Map.Entry<Long, List<TermEntry>> entry : updatedTermEntries.entrySet()) {
		    updateRegularTermEntriesInIndex(entry.getValue());
		}
	    }
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private List<Term> collectTermsByIds(List<String> termIds, List<TermEntry> termEntries, String userName,
	    Date dateModified) {
	List<Term> terms = new ArrayList<>();

	for (TermEntry termEntry : termEntries) {
	    for (String id : termIds) {
		Term term = termEntry.ggetTermById(id);
		if (term != null && !term.isForbidden()) {
		    terms.add(term);
		    termEntry.setAction(Action.EDITED);
		    termEntry.setDateModified(dateModified.getTime());
		    termEntry.setUserModified(userName);
		}
	    }
	}
	return terms;
    }

    private List<TermEntry> findTermEntriesByTermIds(List<String> termIds, Long projectId) {
	try {
	    return getBrowser(getRegularCollection()).findByTermIds(termIds, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private StrategyTermCounter getStrategyTermCounter() {
	return _strategyTermCounter;
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private boolean renameTermDescriptions(Set<Description> descriptions, List<Attribute> attributes) {
	boolean modified = false;
	for (Description description : descriptions) {
	    for (Attribute attribute : attributes) {
		modified |= setNewType(description, attribute);
	    }
	}
	return modified;
    }

    private Page<TermEntry> searchTermEntries(TmgrSearchFilter filter) {
	try {
	    return getSearcher(getRegularCollection()).concordanceSearch(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private boolean setNewType(Description description, Attribute attribute) {
	String renameValue = attribute.getRenameValue();
	if (StringUtils.isEmpty(renameValue)) {
	    return false;
	}
	String type = description.getType();
	if (type.equals(attribute.getName())) {
	    LogHelper.debug(LOGGER, String.format(Messages.getString("TermServiceImpl.10"), type, renameValue)); //$NON-NLS-1$
	    description.setType(renameValue);
	    return true;
	}
	return false;
    }
}
