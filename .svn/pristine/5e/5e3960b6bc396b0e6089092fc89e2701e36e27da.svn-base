package org.gs4tr.termmanager.service.solr.restore.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

public class ReindexCommand {

    private List<SolrInputDocument> _buffer;

    private List<Long> _projectIds;

    private String _route;

    public ReindexCommand(String route, int bufferSize) {
	_buffer = new ArrayList<>(bufferSize);
	_projectIds = new ArrayList<>();
	_route = route;
    }

    public void addProjectId(Long projectId) {
	_projectIds.add(projectId);
    }

    public List<SolrInputDocument> getBuffer() {
	return _buffer;
    }

    public List<Long> getProjectIds() {
	return _projectIds;
    }

    public String getRoute() {
	return _route;

    }
}
