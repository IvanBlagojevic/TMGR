package org.gs4tr.termmanager.io.edd.event;

import java.util.Set;

import org.gs4tr.termmanager.io.edd.api.Event;
import org.gs4tr.termmanager.io.edd.api.ProjectInfoEvent;
import org.gs4tr.termmanager.io.edd.api.StatisticsInfoEvent;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;

public class UpdateCountEvent implements ProjectInfoEvent<ProjectDetailsIO>, StatisticsInfoEvent<StatisticInfoIO> {

    private ProjectDetailsIO _projectDetails;

    private Set<StatisticInfoIO> _statisticInfo;

    public UpdateCountEvent(ProjectDetailsIO projectDetails, Set<StatisticInfoIO> statisticDetails) {
	_projectDetails = projectDetails;
	_statisticInfo = statisticDetails;
    }

    @Override
    public ProjectDetailsIO getProjectDetails() {
	return _projectDetails;
    }

    @Override
    public Set<StatisticInfoIO> getStatisticsInfo() {
	return _statisticInfo;
    }

    @Override
    public Class<? extends Event> getType() {
	return getClass();
    }
}
