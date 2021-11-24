package org.gs4tr.termmanager.model.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.event.StatisticsInfo;

public class StatisticsInfoIOConverter {

    public static Set<StatisticInfoIO> fromInternalToIo(Set<StatisticsInfo> infos) {

	if (CollectionUtils.isEmpty(infos)) {
	    return null;
	}

	Set<StatisticInfoIO> infoDetails = new HashSet<>();

	for (StatisticsInfo info : infos) {
	    StatisticInfoIO details = new StatisticInfoIO();
	    details.setAddedApprovedCount(info.getAddedApprovedCount().longValue());
	    details.setAddedBlacklistedCount(info.getAddedBlacklistedCount().longValue());
	    details.setAddedOnHoldCount(info.getAddedOnHoldCount().longValue());
	    details.setAddedPendingApprovalCount(info.getAddedPendingApprovalCount().longValue());
	    details.setApprovedCount(info.getApprovedCount().longValue());
	    details.setBlackListedCount(info.getBlackListedCount().longValue());
	    details.setDeletedCount(info.getDeletedCount().longValue());
	    details.setLanguageId(info.getLanguageId());
	    details.setOnHoldCount(info.getOnHoldCount().longValue());
	    details.setPendingApprovalCount(info.getPendingApprovalCount().longValue());
	    details.setProjectId(info.getProjectId());
	    details.setUpdatedCount(info.getUpdatedCount().longValue());
	    infoDetails.add(details);
	}

	return infoDetails;
    }

}
