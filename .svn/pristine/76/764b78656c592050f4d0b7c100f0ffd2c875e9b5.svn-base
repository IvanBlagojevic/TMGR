package org.gs4tr.termmanager.service.history;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.springframework.stereotype.Component;

@Component("submissionHistoryCreator")
public class SubmissionHistoryCreatorImpl implements HistoryCreator {

    @Override
    public List<TermEntry> createHistory(DbBaseTermEntry dbBaseEntry) {

	DbSubmissionTermEntry dbSubmissionEntry = (DbSubmissionTermEntry) dbBaseEntry;

	Set<DbSubmissionTermEntryHistory> dbTermHistories = dbSubmissionEntry.getHistory();

	if (dbTermHistories == null || dbTermHistories.isEmpty()) {
	    return createFirstRevision(dbSubmissionEntry);
	}
	return createHistory(dbSubmissionEntry, dbTermHistories);

    }

    private void addTermEntryData(DbSubmissionTermEntry previous, DbSubmissionTermEntryHistory dbTermEntryHistory,
	    DbSubmissionTermEntry revision) {

	revision.setAction(dbTermEntryHistory.getHistoryAction());
	revision.setDateCreated(previous.getDateCreated());
	revision.setUserCreated(previous.getUserCreated());
	revision.setDateModified(dbTermEntryHistory.getDateModified());
	revision.setUserModified(dbTermEntryHistory.getUserModified());
	byte[] attributeAsBytes = dbTermEntryHistory.getDescriptions();
	if (attributeAsBytes != null) {

	    Set<DbSubmissionTermEntryDescription> descriptions = JsonUtils.readSet(attributeAsBytes,
		    DbSubmissionTermEntryDescription.class);

	    revision.setDescriptions(descriptions);
	}
    }

    private List<TermEntry> createFirstRevision(DbSubmissionTermEntry dbEntry) {
	List<TermEntry> entries = new ArrayList<>();
	TermEntry entry = DbSubmissionTermEntryConverter.convertToTermEntry(dbEntry);
	entry.setRevisionId((long) 1);
	entries.add(entry);
	return entries;
    }

    private List<TermEntry> createHistory(DbSubmissionTermEntry dbSubmissionEntry,
	    Set<DbSubmissionTermEntryHistory> dbTermHistories) {

	List<TermEntry> termEntries = new ArrayList<>();
	List<DbSubmissionTermEntryHistory> dbEntryHistories = sortHistoryByRevisionId(dbTermHistories);

	int maxRevision = dbEntryHistories.get(0).getRevisionId();
	Map<Integer, List<DbSubmissionTerm>> mapedByRevision = mapTermsByRevisionID(dbSubmissionEntry);

	DbSubmissionTermEntry previous = dbSubmissionEntry;

	for (DbSubmissionTermEntryHistory dbTermEntryHistory : dbEntryHistories) {
	    DbSubmissionTermEntry revision = createRevison(previous, dbTermEntryHistory, mapedByRevision);
	    TermEntry entry = DbSubmissionTermEntryConverter.convertToTermEntry(revision);
	    entry.setRevisionId((long) dbTermEntryHistory.getRevisionId());
	    termEntries.add(entry);
	    previous = revision;
	}

	TermEntry mainTermEntry = DbSubmissionTermEntryConverter.convertToTermEntry(dbSubmissionEntry);
	mainTermEntry.setRevisionId((long) (maxRevision + 1));
	termEntries.add(mainTermEntry);
	termEntries.sort((o1, o2) -> o2.getRevisionId().compareTo(o1.getRevisionId()));

	return termEntries;
    }

    private DbSubmissionTermEntry createRevison(DbSubmissionTermEntry previous,
	    DbSubmissionTermEntryHistory dbTermEntryHistory, Map<Integer, List<DbSubmissionTerm>> maped) {

	DbSubmissionTermEntry revision = new DbSubmissionTermEntry();
	addTermEntryData(previous, dbTermEntryHistory, revision);

	Set<DbSubmissionTerm> terms = new HashSet<>();
	if (maped.containsKey(dbTermEntryHistory.getRevisionId())) {
	    terms.addAll(maped.get(dbTermEntryHistory.getRevisionId()));
	}

	Set<String> uuids = JsonUtils.readSet(dbTermEntryHistory.getTermUUIDs(), String.class);
	for (String uuid : uuids) {
	    if (findTerm(terms, uuid) == null) {
		DbSubmissionTerm term = findTerm(previous.getSubmissionTerms(), uuid);
		if (term != null) {
		    terms.add(term);
		}
	    }
	}
	revision.setSubmissionTerms(terms);
	return revision;
    }

    private DbSubmissionTerm deserializeDbSubmissionTerm(DbSubmissionTermHistory dbTermHistory) {
	DbSubmissionTerm dbTerm = JsonUtils.readValue(dbTermHistory.getRevision(), DbSubmissionTerm.class);
	dbTerm.setTermEntryUuid(dbTermHistory.getTermEntryUuid());
	return dbTerm;
    }

    private DbSubmissionTerm findTerm(Set<DbSubmissionTerm> terms, String termUUId) {
	for (DbSubmissionTerm dbTerm : terms) {
	    if (dbTerm.getUuId().equals(termUUId)) {
		return dbTerm;
	    }
	}
	return null;
    }

    private Map<Integer, List<DbSubmissionTerm>> mapTermsByRevisionID(DbSubmissionTermEntry dbEntry) {
	Map<Integer, List<DbSubmissionTerm>> result = new HashMap<>();

	Set<DbSubmissionTermEntryHistory> dbEntryHistories = dbEntry.getHistory();

	for (DbSubmissionTermEntryHistory dbEntryHistory : dbEntryHistories) {
	    Set<DbSubmissionTermHistory> entries = dbEntryHistory.getHistory();
	    for (DbSubmissionTermHistory dbTermHistory : entries) {
		int revisionId = dbTermHistory.getRevisionId();
		List<DbSubmissionTerm> dbTerms = result.get(revisionId);
		if (dbTerms == null) {
		    dbTerms = new ArrayList<>();
		}
		DbSubmissionTerm dbTerm = deserializeDbSubmissionTerm(dbTermHistory);
		dbTerms.add(dbTerm);
		result.put(revisionId, dbTerms);
	    }

	}
	return result;

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
