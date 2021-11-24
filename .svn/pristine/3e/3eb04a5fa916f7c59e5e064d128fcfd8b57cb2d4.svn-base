package org.gs4tr.termmanager.io.edd.handler;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.UpdateCountEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.springframework.stereotype.Component;

@Component
public class StatisticsUpdateHandler extends AbstractHandler implements Handler<UpdateCountEvent> {

    @Override
    public void onEvent(UpdateCountEvent event) throws EventException {
	validateUpdateCountEvent(event);

	Set<StatisticInfoIO> statisticsInfo = event.getStatisticsInfo();
	if (CollectionUtils.isEmpty(statisticsInfo)) {
	    return;
	}

	long projectId = statisticsInfo.iterator().next().getProjectId();

	logMessage(String.format("Updating statistics for project [%d] STARTED.", projectId));

	if (CollectionUtils.isNotEmpty(statisticsInfo)) {
	    statisticsInfo.forEach(s -> getStatisticsDAO().updateStatistics(s));
	}

	logMessage(String.format("Updating statistics for project [%d] STARTED.", projectId));

    }

    private void validateUpdateCountEvent(UpdateCountEvent e) {
	Validate.notNull(e);
	Validate.notNull(e.getStatisticsInfo(), "The data that need to be processed, can't be null!");
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}
