package org.gs4tr.termmanager.service.undoterm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.springframework.stereotype.Component;

@Component("undoTerm")
public class UndoTermImpl implements UndoTerm {

    @Override
    public TermEntry undoTerm(DbSubmissionTermEntry dbEntry) {
	if (dbEntry.getHistory() != null || !dbEntry.getHistory().isEmpty()) {
	    rollbackToPreviousRevision(dbEntry);
	}
	return DbSubmissionTermEntryConverter.convertToTermEntry(dbEntry);
    }

    private DbSubmissionTerm deserializeDbTerm(byte[] revision) {
	DbSubmissionTerm dbTerm = JsonUtils.readValue(revision, DbSubmissionTerm.class);
	return dbTerm;
    }

    private Set<DbSubmissionTerm> getSubmissionTermsFromLastRevison(DbSubmissionTermEntryHistory last) {
	Set<DbSubmissionTerm> histories = new HashSet<>();
	Set<DbSubmissionTermHistory> entries = last.getHistory();
	if (entries.size() > 0) {
	    for (DbSubmissionTermHistory dbSubmissionTermHistory : entries) {
		if (dbSubmissionTermHistory.getRevision() != null) {
		    histories.add(deserializeDbTerm(dbSubmissionTermHistory.getRevision()));
		}
	    }

	}
	return histories;
    }

    private Map<String, DbSubmissionTerm> mapTermsByUuid(Set<DbSubmissionTerm> dbTerms) {
	Map<String, DbSubmissionTerm> result = new HashMap<>();
	for (DbSubmissionTerm dbTerm : dbTerms) {
	    result.put(dbTerm.getUuId(), dbTerm);
	}
	return result;
    }

    /**
     * This function first find last revision from history. Then iterate over
     * current terms and revision terms. If termUUid from current term is equal
     * termUUid from revision term then use revision term, otherwise use current
     * term.
     * 
     */
    private void rollbackToPreviousRevision(DbSubmissionTermEntry dbEntry) {
	List<DbSubmissionTermEntryHistory> sortedHistory = sortHistoryByRevisionId(dbEntry.getHistory());

	if (!sortedHistory.isEmpty()) {
	    DbSubmissionTermEntryHistory last = sortedHistory.get(0);

	    Set<DbSubmissionTerm> lastRevisionTerms = getSubmissionTermsFromLastRevison(last);
	    Set<DbSubmissionTerm> currentTerms = dbEntry.getSubmissionTerms();
	    Set<DbSubmissionTerm> resultTerms = new HashSet<>();

	    Map<String, DbSubmissionTerm> currentMapTerms = mapTermsByUuid(currentTerms);
	    Map<String, DbSubmissionTerm> historyMapTerms = mapTermsByUuid(lastRevisionTerms);

	    for (DbSubmissionTerm dbTerm : currentTerms) {
		String uuid = dbTerm.getUuId();
		if (historyMapTerms.containsKey(uuid)) {
		    resultTerms.add(historyMapTerms.get(uuid));
		} else {
		    resultTerms.add(currentMapTerms.get(uuid));
		}
	    }
	    dbEntry.setSubmissionTerms(resultTerms);
	}
    }

    private List<DbSubmissionTermEntryHistory> sortHistoryByRevisionId(Set<DbSubmissionTermEntryHistory> histories) {
	List<DbSubmissionTermEntryHistory> sorted = new ArrayList<>();
	sorted.addAll(histories);
	sorted.sort(new Comparator<DbSubmissionTermEntryHistory>() {
	    @Override
	    public int compare(DbSubmissionTermEntryHistory o1, DbSubmissionTermEntryHistory o2) {
		return o2.getRevisionId() - o1.getRevisionId();
	    }
	});
	return sorted;
    }
}
