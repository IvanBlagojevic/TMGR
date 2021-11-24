package org.gs4tr.termmanager.io.edd.handler;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.io.edd.event.AbstractDataEvent;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHandler {

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    protected ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    protected ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    protected ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }

    protected StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }

    protected abstract void logMessage(String message);

    protected void validate(AbstractDataEvent e) {
	Validate.notNull(e, "Event can't be null!");
	Validate.notNull(e.getData(), "The data that need to be processed, can't be null!");
	Validate.notEmpty(e.getData(), "The data that need to be processed, can't be empty!");
    }
}
