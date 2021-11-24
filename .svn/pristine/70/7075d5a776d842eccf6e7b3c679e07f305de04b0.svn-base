package org.gs4tr.termmanager.model;

import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class TransactionalUnit {

    private ProjectDetailInfo _projectDetailInfo;

    private Set<StatisticsInfo> _statisticsInfo;

    private List<TermEntry> _termEntries;

    public TransactionalUnit() {
    }

    public TransactionalUnit(List<TermEntry> entries) {
	_termEntries = entries;
    }

    public TransactionalUnit(List<TermEntry> entries, ProjectDetailInfo projectDetailInfo) {
	this(entries);
	_projectDetailInfo = projectDetailInfo;
    }

    public TransactionalUnit(List<TermEntry> entries, ProjectDetailInfo projectDetailInfo,
	    Set<StatisticsInfo> statisticsInfo) {
	this(entries, projectDetailInfo);
	_statisticsInfo = statisticsInfo;
    }

    public ProjectDetailInfo getProjectDetailInfo() {
	return _projectDetailInfo;
    }

    public Set<StatisticsInfo> getStatisticsInfo() {
	return _statisticsInfo;
    }

    public List<TermEntry> getTermEntries() {
	return _termEntries;
    }

    public void setProjectDetailInfo(ProjectDetailInfo projectDetailInfo) {
	_projectDetailInfo = projectDetailInfo;
    }

    public void setStatisticsInfo(Set<StatisticsInfo> statisticsInfo) {
	_statisticsInfo = statisticsInfo;
    }

    public void setTermEntries(List<TermEntry> termEntries) {
	_termEntries = termEntries;
    }
}
