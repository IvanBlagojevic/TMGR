package org.gs4tr.termmanager.service.utils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.event.StatisticsInfo;

public class StatisticsUtils {

    public static void incrementAddedStatisticByStatus(StatisticsInfo statisticsInfo, String termStatus) {
	String processed = ItemStatusTypeHolder.PROCESSED.getName();
	String pending = ItemStatusTypeHolder.WAITING.getName();
	String onHold = ItemStatusTypeHolder.ON_HOLD.getName();
	String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	if (termStatus.equals(processed)) {
	    statisticsInfo.getAddedApprovedCount().incrementAndGet();
	} else if (termStatus.equals(pending)) {
	    statisticsInfo.getAddedPendingApprovalCount().incrementAndGet();
	} else if (termStatus.equals(onHold)) {
	    statisticsInfo.getAddedOnHoldCount().incrementAndGet();
	} else if (termStatus.equals(blackListed)) {
	    statisticsInfo.getAddedBlacklistedCount().incrementAndGet();
	}
    }

    public static void incrementAddedStatistics(List<StatisticsInfo> statisticsInfos, Long projectId, String languageId,
	    String termStatus) {

	StatisticsInfo statisticsInfo = statisticsInfos.stream()
		.filter(st -> st.getProjectId().equals(projectId) && st.getLanguageId().equals(languageId)).findFirst()
		.orElse(null);

	if (Objects.isNull(statisticsInfo)) {
	    statisticsInfo = new StatisticsInfo(projectId, languageId);
	    statisticsInfos.add(statisticsInfo);
	}

	incrementAddedStatisticByStatus(statisticsInfo, termStatus);

    }

    public static void incrementStatisticByStatus(StatisticsInfo statisticsInfo, String termStatus) {
	String processed = ItemStatusTypeHolder.PROCESSED.getName();
	String pending = ItemStatusTypeHolder.WAITING.getName();
	String onHold = ItemStatusTypeHolder.ON_HOLD.getName();
	String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	if (termStatus.equals(processed)) {
	    statisticsInfo.getApprovedCount().incrementAndGet();
	} else if (termStatus.equals(pending)) {
	    statisticsInfo.getPendingApprovalCount().incrementAndGet();
	} else if (termStatus.equals(onHold)) {
	    statisticsInfo.getOnHoldCount().incrementAndGet();
	} else if (termStatus.equals(blackListed)) {
	    statisticsInfo.getBlackListedCount().incrementAndGet();
	}
    }

    public static void incrementStatistics(Set<StatisticsInfo> statisticsInfos, Long projectId, String languageId,
					   String termStatus) {

	StatisticsInfo statisticsInfo = statisticsInfos.stream()
		.filter(st -> st.getProjectId().equals(projectId) && st.getLanguageId().equals(languageId)).findFirst()
		.orElse(null);

	if (Objects.isNull(statisticsInfo)) {
	    statisticsInfo = new StatisticsInfo(projectId, languageId);
	    statisticsInfos.add(statisticsInfo);
	}

	incrementStatisticByStatus(statisticsInfo, termStatus);

    }
}
