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
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.springframework.stereotype.Component;

@Component("regularHistoryCreator")
public class RegularHistoryCreatorImpl implements HistoryCreator {

    @Override
    public List<TermEntry> createHistory(DbBaseTermEntry dbEntry) {
	DbTermEntry dbTermEntry = (DbTermEntry) dbEntry;

	Set<DbTermEntryHistory> dbTermHistories = dbTermEntry.getHistory();
	if (dbTermHistories == null || dbTermHistories.isEmpty()) {
	    return createFirstRevision(dbTermEntry);
	}
	return createHistory(dbTermEntry, dbTermHistories);

    }

    private void addTermEntryData(DbTermEntry previous, DbTermEntryHistory dbTermEntryHistory, DbTermEntry revision) {

	revision.setAction(dbTermEntryHistory.getHistoryAction());
	revision.setDateCreated(previous.getDateCreated());
	revision.setUserCreated(previous.getUserCreated());
	revision.setDateModified(dbTermEntryHistory.getDateModified());
	revision.setUserModified(dbTermEntryHistory.getUserModified());
	byte[] attributeAsBytes = dbTermEntryHistory.getDescriptions();
	if (attributeAsBytes != null) {

	    Set<DbTermEntryDescription> descriptions = JsonUtils.readSet(attributeAsBytes,
		    DbTermEntryDescription.class);
	    revision.setDescriptions(descriptions);
	}
    }

    private List<TermEntry> createFirstRevision(DbTermEntry dbEntry) {
	List<TermEntry> entries = new ArrayList<>();
	TermEntry entry = DbTermEntryConverter.convertToTermEntry(dbEntry);
	entry.setRevisionId((long) 1);
	entries.add(entry);
	return entries;
    }

    private List<TermEntry> createHistory(DbTermEntry dbTermEntry, Set<DbTermEntryHistory> dbTermHistories) {

	List<TermEntry> termEntries = new ArrayList<>();

	List<DbTermEntryHistory> dbEntryHistories = sortHistoryByRevisionId(dbTermHistories);

	int maxRevision = dbEntryHistories.get(0).getRevisionId();
	Map<Integer, List<DbTerm>> mapedByRevision = mapDbTermsByRevisionID(dbTermEntry);

	DbTermEntry previous = dbTermEntry;

	for (DbTermEntryHistory dbTermEntryHistory : dbEntryHistories) {
	    DbTermEntry revision = createRevison(previous, dbTermEntryHistory, mapedByRevision);
	    TermEntry entry = DbTermEntryConverter.convertToTermEntry(revision);
	    entry.setRevisionId((long) dbTermEntryHistory.getRevisionId());
	    termEntries.add(entry);
	    previous = revision;
	}

	TermEntry mainTermEntry = DbTermEntryConverter.convertToTermEntry(dbTermEntry);
	mainTermEntry.setRevisionId((long) (maxRevision + 1));
	termEntries.add(mainTermEntry);
	termEntries.sort((o1, o2) -> o2.getRevisionId().compareTo(o1.getRevisionId()));
	return termEntries;
    }

    private DbTermEntry createRevison(DbTermEntry previous, DbTermEntryHistory dbTermEntryHistory,
	    Map<Integer, List<DbTerm>> maped) {

	DbTermEntry revision = new DbTermEntry();
	addTermEntryData(previous, dbTermEntryHistory, revision);

	Set<DbTerm> terms = new HashSet<>();
	if (maped.containsKey(dbTermEntryHistory.getRevisionId())) {
	    terms.addAll(maped.get(dbTermEntryHistory.getRevisionId()));
	}

	Set<String> uuids = JsonUtils.readSet(dbTermEntryHistory.getTermUUIDs(), String.class);
	for (String uuid : uuids) {
	    if (findTerm(terms, uuid) == null) {
		DbTerm term = findTerm(previous.getTerms(), uuid);
		if (term != null) {
		    terms.add(term);
		}
	    }
	}
	revision.setTerms(terms);
	return revision;
    }

    private DbTerm deserializeDbTerm(byte[] dbTermAsByte, String termEntryUuid) {
	DbTerm dbTerm = JsonUtils.readValue(dbTermAsByte, DbTerm.class);
	dbTerm.setTermEntryUuid(termEntryUuid);
	return dbTerm;

    }

    private DbTerm findTerm(Set<DbTerm> terms, String termUUId) {
	for (DbTerm dbTerm : terms) {
	    if (dbTerm.getUuId().equals(termUUId)) {
		return dbTerm;
	    }
	}
	return null;
    }

    private Map<Integer, List<DbTerm>> mapDbTermsByRevisionID(DbTermEntry dbEntry) {
	Map<Integer, List<DbTerm>> result = new HashMap<>();

	Set<DbTermEntryHistory> dbEntryHistories = dbEntry.getHistory();

	for (DbTermEntryHistory dbEntryHistory : dbEntryHistories) {
	    Set<DbTermHistory> entries = dbEntryHistory.getHistory();
	    for (DbTermHistory dbTermHistory : entries) {
		int revisionId = dbTermHistory.getRevisionId();
		List<DbTerm> dbTerms = result.get(revisionId);
		if (dbTerms == null) {
		    dbTerms = new ArrayList<>();
		}
		if (dbTermHistory.getRevision() != null) {
		    dbTerms.add(deserializeDbTerm(dbTermHistory.getRevision(), dbTermHistory.getTermEntryUuid()));
		    result.put(revisionId, dbTerms);
		}
	    }
	}
	return result;

    }

    private List<DbTermEntryHistory> sortHistoryByRevisionId(Set<DbTermEntryHistory> histories) {
	List<DbTermEntryHistory> sorted = new ArrayList<>();

	sorted.addAll(histories);
	sorted.sort(new Comparator<DbTermEntryHistory>() {
	    @Override
	    public int compare(DbTermEntryHistory o1, DbTermEntryHistory o2) {
		return o2.getRevisionId() - o1.getRevisionId();
	    }
	});
	return sorted;
    }

}
