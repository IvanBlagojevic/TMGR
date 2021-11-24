package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;

import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.event.StatisticsInfo;

public interface StatisticsService {

    void decrementStatistics(Statistics statistics);

    void decrementStatistics(Collection<Statistics> statistics);

    List<Statistics> findStatisticsByProjectId(Long projectId, final Class<?>... classesToFetch);

    void updateStatistics(Collection<StatisticsInfo> statisticsInfos);

    void updateStatistics(StatisticsInfo statisticsInfo);

    void updateStatisticsOnImport(Long projectId, ImportSummary importSummary);
}
