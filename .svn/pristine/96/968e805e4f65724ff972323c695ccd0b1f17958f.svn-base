package org.gs4tr.termmanager.persistence.solr.query;

import org.gs4tr.termmanager.persistence.solr.query.Sort.Direction;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TmgrPageRequest extends PageRequest {

    private static final long serialVersionUID = 7010621099522219633L;

    public TmgrPageRequest() {
	super(DEFAULT_PAGE, DEFAULT_SIZE, Direction.DESC, SolrParentDocFields.DATE_CREATED_INDEX_STORE);
    }

    public TmgrPageRequest(Direction direction, String... properties) {
	super(DEFAULT_PAGE, DEFAULT_SIZE, direction, properties);
    }

    public TmgrPageRequest(int page, int size, Direction direction, String... properties) {
	super(page, size, direction, properties);
    }

    public TmgrPageRequest(int page, int size, Sort sort) {
	super(page, size, sort);
    }

    public TmgrPageRequest(Sort sort) {
	super(DEFAULT_PAGE, DEFAULT_SIZE, sort);
    }
}
