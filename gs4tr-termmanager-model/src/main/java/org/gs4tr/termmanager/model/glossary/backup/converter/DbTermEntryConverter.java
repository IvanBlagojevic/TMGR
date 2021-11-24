package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.model.serializer.JsonIO;

public class DbTermEntryConverter {

    public static List<DbTermEntry> convertToDbTermEntries(Collection<TermEntry> termEntries) {
	List<DbTermEntry> dbEntries = new ArrayList<>();
	if (CollectionUtils.isEmpty(termEntries)) {
	    return dbEntries;
	}

	termEntries.forEach(t -> dbEntries.add(convertToDbTermEntry(t)));

	return dbEntries;
    }

    public static DbTermEntry convertToDbTermEntry(TermEntry termEntry) {
	if (termEntry == null) {
	    return null;
	}

	String termEntryId = termEntry.getUuId();

	DbTermEntry dbEntry = new DbTermEntry();
	Action action = termEntry.getAction();
	if (action != null) {
	    dbEntry.setAction(action.name());
	}
	Long dateCreated = termEntry.getDateCreated();
	if (dateCreated != null) {
	    dbEntry.setDateCreated(new Date(dateCreated));
	}
	Long dateModified = termEntry.getDateModified();
	if (dateModified != null) {
	    dbEntry.setDateModified(new Date(dateModified));
	}
	dbEntry.setDescriptions(
		DbTermEntryDescriptionConverter.convertToDescriptions(termEntryId, termEntry.getDescriptions()));
	dbEntry.setProjectId(termEntry.getProjectId());
	dbEntry.setProjectName(termEntry.getProjectName());
	dbEntry.setShortCode(termEntry.getShortCode());
	dbEntry.setTerms(DbTermConverter.convertToDbTerms(termEntryId, termEntry.ggetAllTerms()));
	dbEntry.setUserCreated(termEntry.getUserCreated());
	dbEntry.setUserModified(termEntry.getUserModified());
	dbEntry.setUuId(termEntryId);

	// int revisionId = 1;
	int revisionId = Objects.nonNull(termEntry.getRevisionId()) ? termEntry.getRevisionId().intValue() : 1;
	dbEntry.setRevisionId(revisionId);

	DbTermEntryHistory historyRevision = createHistory(null, dbEntry, revisionId);
	if (Objects.isNull(dbEntry.getHistory())) {
	    dbEntry.setHistory(new HashSet<>());
	}
	dbEntry.getHistory().add(historyRevision);

	return dbEntry;
    }

    public static TermEntry convertToTermEntry(DbTermEntry dbEntry) {
	if (dbEntry == null) {
	    return null;
	}
	String dbId = dbEntry.getUuId();
	TermEntry entry = new TermEntry();

	String action = dbEntry.getAction();
	if (action != null) {
	    entry.setAction(Action.valueOf(action.toUpperCase()));
	}
	Date dateCreated = dbEntry.getDateCreated();
	if (dateCreated != null) {
	    entry.setDateCreated(dateCreated.getTime());
	}
	Date dateModified = dbEntry.getDateModified();
	if (dateModified != null) {
	    entry.setDateModified(dateModified.getTime());
	}
	entry.setDescriptions(DbTermEntryDescriptionConverter.convertToTermDescriptions(dbEntry.getDescriptions()));
	entry.setProjectId(dbEntry.getProjectId());
	entry.setProjectName(dbEntry.getProjectName());
	entry.setShortCode(dbEntry.getShortCode());
	entry.setUserCreated(dbEntry.getUserCreated());
	entry.setUserModified(dbEntry.getUserModified());
	List<Term> terms = DbTermConverter.convertToTerms(dbEntry.getTerms());
	for (Term term : terms) {
	    entry.addTerm(term);
	}
	entry.setUuId(dbId);
	return entry;
    }

    public static DbTermEntryHistory createHistory(DbTermEntry incoming, DbTermEntry existing, int newRevisionId) {
	Set<String> uuids = new HashSet<>();
	Set<DbTerm> terms = existing.getTerms();
	for (DbTerm dbTerm : terms) {
	    uuids.add(dbTerm.getUuId());
	}

	DbTermEntryHistory entryHistory = new DbTermEntryHistory();
	entryHistory.setHistoryAction(existing.getAction());
	entryHistory.setDateModified(existing.getDateModified());
	entryHistory.setUserModified(existing.getUserModified());
	entryHistory.setTermEntryUUid(existing.getUuId());
	entryHistory.setRevisionId(newRevisionId);
	if (existing.getDescriptions() != null) {
	    Set<DbTermEntryDescription> descriptions = new HashSet<>(existing.getDescriptions());
	    entryHistory.setDescriptions(JsonIO.writeValueAsBytes(descriptions));
	}
	entryHistory.setTermUUIDs(JsonIO.writeValueAsBytes(uuids));

	Set<DbTermHistory> history = DbTermConverter.findDifferentTerms(incoming, existing, newRevisionId);
	entryHistory.setHistory(history);

	return entryHistory;

    }

    public static int getLastRevisionId(Set<DbTermEntryHistory> history) {
	int revisionId = 1;
	if (CollectionUtils.isNotEmpty(history)) {
	    for (DbTermEntryHistory rev : history) {
		revisionId = revisionId < rev.getRevisionId() ? rev.getRevisionId() : revisionId;
	    }
	}
	return revisionId;
    }

    public static DbTermEntry mergeWithExistingDbTermEntry(DbTermEntry incoming, DbTermEntry existing) {

	int newRevision = existing.getRevisionId() + 1;

	DbTermEntryHistory history = createHistory(incoming, existing, newRevision);
	if (existing.getHistory() == null) {
	    existing.setHistory(new HashSet<>());
	}
	existing.getHistory().add(history);
	existing.setRevisionId(newRevision);
	existing.setAction(incoming.getAction());
	if (existing.getDateCreated() == null) {
	    existing.setDateCreated(incoming.getDateCreated());
	}
	existing.setDateModified(incoming.getDateModified());

	Set<DbTermEntryDescription> existingDescs = existing.getDescriptions();
	Set<DbTermEntryDescription> incomingDescs = incoming.getDescriptions();
	if (existingDescs != null) {
	    DbTermEntryDescriptionConverter.mergeWithExistingDescriptions(incomingDescs, existingDescs);
	} else {
	    existing.setDescriptions(incomingDescs);
	}

	Set<DbTerm> incomingTerms = incoming.getTerms();
	Set<DbTerm> existingTerms = existing.getTerms();
	if (existingTerms != null) {
	    DbTermConverter.mergeWithExistingTerms(incomingTerms, existingTerms);
	} else {
	    existing.setTerms(incomingTerms);
	}

	if (existing.getUserCreated() == null) {
	    existing.setUserCreated(incoming.getUserCreated());
	}
	existing.setUserModified(incoming.getUserModified());

	existing.setSubmissionId(incoming.getSubmissionId());
	existing.setSubmissionName(incoming.getSubmissionName());
	existing.setSubmitter(incoming.getSubmitter());

	return existing;
    }
}
