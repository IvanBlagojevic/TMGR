package org.gs4tr.termmanager.service.solr.restore;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by emisia on 6/12/17.
 */
@Component("restoreInfoProcessor")
public class RestoreInfoProcessor {

    private static final String DELETE_QUERY = "DELETE FROM TM_VERSION_REINDEX_INFO;"; //$NON-NLS-1$

    private static final String DISABLE_RE_INDEX = "INSERT INTO TM_VERSION_REINDEX_INFO (ENABLE_REINDEX, FIRST_REINDEX) VALUES (0, 0);"; //$NON-NLS-1$

    private static final String DISABLE_RE_INDEX_ON_EXCEPTION = "INSERT INTO TM_VERSION_REINDEX_INFO (ENABLE_REINDEX, FIRST_REINDEX) VALUES (0, 1);";

    private static final String GET_RE_INDEX_INFO_QUERY = "SELECT ENABLE_REINDEX FROM TM_VERSION_REINDEX_INFO;"; //$NON-NLS-1$

    private static final Log LOGGER = LogFactory.getLog(RestoreInfoProcessor.class);

    @Autowired(required = false)
    private JdbcTemplate _jdbcTemplate;

    public void disableRebuildIndex() {
	getJdbcTemplate().execute(DELETE_QUERY);
	getJdbcTemplate().execute(DISABLE_RE_INDEX);
    }

    public void disableRebuildIndexOnException() {
	getJdbcTemplate().execute(DELETE_QUERY);
	getJdbcTemplate().execute(DISABLE_RE_INDEX_ON_EXCEPTION);
    }

    public boolean isRebuildIndexEnabled() {
	try {
	    JdbcTemplate jdbcTemplate = getJdbcTemplate();
	    return Objects.nonNull(jdbcTemplate) ? jdbcTemplate.queryForObject(GET_RE_INDEX_INFO_QUERY, Boolean.class)
		    : false;
	} catch (Exception e) {
	    LogHelper.error(LOGGER, e.getMessage(), e);
	    if (e instanceof EmptyResultDataAccessException) {
		disableRebuildIndexOnException();
	    }
	    return false;
	}
    }

    private JdbcTemplate getJdbcTemplate() {
	return _jdbcTemplate;
    }
}
