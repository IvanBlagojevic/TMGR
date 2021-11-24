package org.gs4tr.termmanager.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.event.StatisticsInfo;

public interface StatisticsDAO extends GenericDao<Statistics, Long> {

    void addOrUpdateProjectUserLangStatistics(ProjectUserLanguage pul, String weeklyReport, String dailyReport);

    void clearUserStatistics(Collection<Long> statisticsIds);

    void decrementStatistics(Statistics statistics);

    List<Statistics> getStatisticsByProjectAndLanguage(Long projectId, String language);

    List<Statistics> getStatisticsByProjectAndLanguages(Long projectId, Set<String> languages);

    List<Statistics> getStatisticsByProjectId(Long projectId, final Class<?>... classesToFetch);

    List<Statistics> getStatisticsByUserId(Long userId, String reportType);

    boolean updateStatistics(StatisticInfoIO statisticInfo);

    void updateStatistics(StatisticsInfo statisticsInfo);

}
