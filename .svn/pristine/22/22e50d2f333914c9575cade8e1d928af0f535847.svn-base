package org.gs4tr.termmanager.persistence;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.update.CommitOption;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryUpdater extends IGenericUpdater<TermEntry, String, TmException> {

    void deleteAll() throws TmException;

    void deleteByProjects(List<Long> projectIds) throws TmException;

    boolean revertLastHistory(Long projectId, TermEntry entity) throws TmException;

    void saveSolrDocs(String route, CommitOption commitOption, List<SolrInputDocument> docs) throws TmException;
}
