package org.gs4tr.termmanager.service.impl;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {

    private static final Log LOG = LogFactory.getLog(StatisticsServiceImpl.class);

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Override
    @Transactional
    public void decrementStatistics(Statistics statistics) {
	if (isNull(statistics)) {
	    LogHelper.warn(LOG, Messages.getString("statistics.is.null"));
	    return;
	}
	getStatisticsDAO().decrementStatistics(statistics);
    }

    @Override
    @Transactional
    public void decrementStatistics(Collection<Statistics> statistics) {
	if (isNull(statistics)) {
	    LogHelper.warn(LOG, Messages.getString("collection.of.statistics.is.null"));
	    return;
	}

	statistics.forEach(s -> decrementStatistics(s));
    }

    @Override
    @Transactional
    public List<Statistics> findStatisticsByProjectId(Long projectId, final Class<?>... classesToFetch) {
	return getStatisticsDAO().getStatisticsByProjectId(projectId, classesToFetch);
    }

    @Override
    @Transactional
    public void updateStatistics(Collection<StatisticsInfo> statisticsInfos) {

	if (isNull(statisticsInfos)) {
	    LogHelper.warn(LOG, Messages.getString("statisticsinfos.is.null"));
	    return;
	}

	try {
	    statisticsInfos.forEach(statisticsInfo -> getStatisticsDAO().updateStatistics(statisticsInfo));
	} catch (Exception e) {
	    LOG.error(e, e);
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    @Transactional
    public void updateStatistics(StatisticsInfo statisticsInfo) {

	if (isNull(statisticsInfo)) {
	    return;
	}

	try {
	    getStatisticsDAO().updateStatistics(statisticsInfo);
	} catch (Exception e) {
	    LOG.error(e, e);
	    throw new RuntimeException(e.getMessage(), e);
	}

    }

    @Override
    @Transactional
    public void updateStatisticsOnImport(Long projectId, ImportSummary importSummary) {
	Map<String, ImportSummary.CountWrapper> countMap = importSummary.getNoImportedTermsPerLanguage();

	Set<Map.Entry<String, ImportSummary.CountWrapper>> importedLanguages = countMap.entrySet();

	if (CollectionUtils.isEmpty(importedLanguages)) {
	    return;
	}

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();

	createAndAddStatisticsInfos(statisticsInfos, projectId, importedLanguages);

	// Upate daily/weekly email notifications after import
	for (StatisticsInfo s : statisticsInfos) {
	    String languageId = s.getLanguageId();
	    for (Map.Entry<String, ImportSummary.CountWrapper> language : importedLanguages) {
		if (languageId.equals(language.getKey())) {
		    ImportSummary.CountWrapper countWrapper = language.getValue();
		    MutableInt deletedTerms = countWrapper.getDeletedTerms();
		    int updatedTerms = countWrapper.getUpdatedTermIds().size();
		    s.getDeletedCount().set(deletedTerms.intValue());
		    s.getUpdatedCount().set(updatedTerms);

		    MutableInt addedPending = countWrapper.getAddedPendingStatisticsCount();
		    s.getAddedPendingApprovalCount().set(addedPending.intValue());
		    MutableInt addedApproved = countWrapper.getAddedApprovedStatisticsCount();
		    s.getAddedApprovedCount().set(addedApproved.intValue());

		    s.getAddedBlacklistedCount().set(countWrapper.getAddedBlacklistedStatisticsCount().intValue());
		    s.getAddedOnHoldCount().set(countWrapper.getAddedOnHoldStatisticsCount().intValue());
		}
	    }

	}

	updateStatistics(statisticsInfos);
    }

    private void createAndAddStatisticsInfos(Set<StatisticsInfo> statisticsInfos, Long projectId,
	    Set<Map.Entry<String, ImportSummary.CountWrapper>> languages) {
	if (CollectionUtils.isEmpty(languages) || Objects.isNull(projectId)) {
	    return;
	}

	languages.forEach(language -> statisticsInfos.add(new StatisticsInfo(projectId, language.getKey())));
    }

    private StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }
}
