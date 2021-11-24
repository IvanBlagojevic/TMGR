package org.gs4tr.termmanager.persistence;

import java.util.Collection;
import java.util.List;

import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryBrowser {

    List<TermEntry> browse(TmgrSearchFilter filter) throws TmException;

    List<TermEntry> findAll() throws TmException;

    TermEntry findById(String id, Long projectId, String... flParameters) throws TmException;

    List<TermEntry> findByIds(Collection<String> ids, List<Long> projectIds) throws TmException;

    List<TermEntry> findByProjectId(Long projectId) throws TmException;

    TermEntry findByTermId(String termId, Long projectId) throws TmException;

    List<TermEntry> findByTermIds(Collection<String> termIds, Long projectId) throws TmException;

//    List<TermEntry> findHistoryByTermEntryId(String termEntryId, Long projectId) throws TmException;

    Term findTermById(String termId, Long projectId) throws TmException;

    List<Term> findTermsByIds(Collection<String> termIds, List<Long> projectIds) throws TmException;

    List<Term> findTermsByLanguageIds(Long projectId, List<String> languageIds, int synonymNumber) throws TmException;

    List<Term> findTermsByProjectId(Long projectId) throws TmException;

    Long getNumberOfTermEntriesOnProject(TmgrSearchFilter filter) throws TmException;
}
