package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.model.serializer.JsonIO;

public class DbSubmissionTermEntryConverter {

    public static List<DbSubmissionTermEntry> convertToDbTermEntries(Collection<TermEntry> termEntries) {
	List<DbSubmissionTermEntry> dbEntries = new ArrayList<>();
	if (CollectionUtils.isEmpty(termEntries)) {
	    return dbEntries;
	}

	termEntries.forEach(t -> dbEntries.add(convertToDbTermEntry(t)));

	return dbEntries;
    }

    public static DbSubmissionTermEntry convertToDbTermEntry(TermEntry termEntry) {
	if (termEntry == null) {
	    return null;
	}

	String termEntryId = termEntry.getUuId();

	DbSubmissionTermEntry dbEntry = new DbSubmissionTermEntry();
	Long dateCreated = termEntry.getDateCreated();
	if (dateCreated != null) {
	    dbEntry.setDateCreated(new Date(dateCreated));
	}
	Long dateModified = termEntry.getDateModified();
	if (dateModified != null) {
	    dbEntry.setDateModified(new Date(dateModified));
	}
	dbEntry.setProjectId(termEntry.getProjectId());
	dbEntry.setProjectName(termEntry.getProjectName());
	dbEntry.setShortCode(termEntry.getShortCode());
	dbEntry.setUserCreated(termEntry.getUserCreated());
	dbEntry.setUserModified(termEntry.getUserModified());
	dbEntry.setUuId(termEntryId);

	dbEntry.setParentUuId(termEntry.getParentUuId());
	dbEntry.setSubmissionId(termEntry.getSubmissionId());
	dbEntry.setSubmissionName(termEntry.getSubmissionName());
	dbEntry.setSubmitter(termEntry.getSubmitter());
	dbEntry.setSubmissionTerms(DbSubmissionTermConverter.convertToDbTerms(termEntryId, termEntry.ggetAllTerms()));
	dbEntry.setDescriptions(DbSubmissionTermEntryDescriptionConverter.convertToDescriptions(termEntryId,
		termEntry.getDescriptions()));

	// int revisionId = 1;
	int revisionId = Objects.nonNull(termEntry.getRevisionId()) ? termEntry.getRevisionId().intValue() : 1;
	dbEntry.setRevisionId(revisionId);

	DbSubmissionTermEntryHistory historyRevision = createHistory(null, dbEntry, revisionId);
	if (Objects.isNull(dbEntry.getHistory())) {
	    dbEntry.setHistory(new HashSet<>());
	}
	dbEntry.getHistory().add(historyRevision);

	return dbEntry;
    }

    public static TermEntry convertToTermEntry(DbSubmissionTermEntry submissionTermEntry) {
	if (submissionTermEntry == null) {
	    return null;
	}

	String termEntryId = submissionTermEntry.getUuId();

	TermEntry termEntry = new TermEntry();
	Date dateCreated = submissionTermEntry.getDateCreated();
	if (dateCreated != null) {
	    termEntry.setDateCreated(dateCreated.getTime());
	}
	Date dateModified = submissionTermEntry.getDateModified();
	if (dateModified != null) {
	    termEntry.setDateModified(dateModified.getTime());
	}
	termEntry.setProjectId(submissionTermEntry.getProjectId());
	termEntry.setProjectName(submissionTermEntry.getProjectName());
	termEntry.setShortCode(submissionTermEntry.getShortCode());
	termEntry.setUserCreated(submissionTermEntry.getUserCreated());
	termEntry.setUserModified(submissionTermEntry.getUserModified());
	termEntry.setUuId(termEntryId);

	termEntry.setParentUuId(submissionTermEntry.getParentUuId());
	termEntry.setSubmissionId(submissionTermEntry.getSubmissionId());
	termEntry.setSubmissionName(submissionTermEntry.getSubmissionName());
	termEntry.setSubmitter(submissionTermEntry.getSubmitter());
	Collection<Term> terms = DbSubmissionTermConverter.convertToTerms(termEntryId,
		submissionTermEntry.getSubmissionTerms());
	for (Term term : terms) {
	    termEntry.addTerm(term);
	}
	termEntry.setDescriptions(
		DbSubmissionTermEntryDescriptionConverter.convertToDescriptions(submissionTermEntry.getDescriptions()));

	return termEntry;
    }

    public static DbSubmissionTermEntryHistory createHistory(DbSubmissionTermEntry incoming,
	    DbSubmissionTermEntry existing, int newRevision) {
	HashSet<String> uuids = new HashSet<>();
	Set<DbSubmissionTerm> terms = existing.getSubmissionTerms();
	for (DbSubmissionTerm dbTerm : terms) {
	    uuids.add(dbTerm.getUuId());
	}

	DbSubmissionTermEntryHistory submissionHistory = new DbSubmissionTermEntryHistory();
	submissionHistory.setRevisionId(newRevision);
	submissionHistory.setTermEntryUUid(existing.getUuId());
	submissionHistory.setHistoryAction(existing.getAction());
	submissionHistory.setDateModified(existing.getDateModified());
	submissionHistory.setUserModified(existing.getUserModified());

	if (existing.getDescriptions() != null) {
	    Set<DbSubmissionTermEntryDescription> descriptions = new HashSet<>(existing.getDescriptions());
	    submissionHistory.setDescriptions(JsonIO.writeValueAsBytes(descriptions));
	}

	Set<DbSubmissionTermHistory> termHistory = DbSubmissionTermConverter.findDifferentSubmissionTerms(incoming,
		existing, newRevision);
	submissionHistory.setHistory(termHistory);

	submissionHistory.setTermUUIDs(JsonIO.writeValueAsBytes(uuids));

	return submissionHistory;
    }

    public static int getLastRevisionId(Set<DbSubmissionTermEntryHistory> history) {
	int revisionId = 1;
	if (CollectionUtils.isNotEmpty(history)) {
	    for (DbSubmissionTermEntryHistory rev : history) {
		revisionId = revisionId < rev.getRevisionId() ? rev.getRevisionId() : revisionId;
	    }
	}
	return revisionId;
    }

    public static DbSubmissionTermEntry mergeWithExistingDbTermEntry(DbSubmissionTermEntry incoming,
	    DbSubmissionTermEntry existing) {
	int newRevision = existing.getRevisionId() + 1;

	addHistory(incoming, existing, newRevision);

	existing.setRevisionId(incoming.getRevisionId());
	existing.setProjectId(incoming.getProjectId());
	existing.setProjectName(incoming.getProjectName());
	existing.setShortCode(incoming.getShortCode());
	if (existing.getDateCreated() == null) {
	    existing.setDateCreated(incoming.getDateCreated());
	}
	existing.setDateModified(incoming.getDateModified());

	Set<DbSubmissionTermEntryDescription> incomingDescs = incoming.getDescriptions();
	Set<DbSubmissionTermEntryDescription> existingDescs = existing.getDescriptions();
	if (existingDescs != null) {
	    DbSubmissionTermEntryDescriptionConverter.mergeWithExistingDescriptions(incomingDescs, existingDescs);
	} else {
	    existing.setDescriptions(incomingDescs);
	}

	Set<DbSubmissionTerm> incomingTerms = incoming.getSubmissionTerms();
	Set<DbSubmissionTerm> existingTerms = existing.getSubmissionTerms();
	if (existingTerms != null) {
	    DbSubmissionTermConverter.mergeWithExistingTerms(incomingTerms, existingTerms);
	} else {
	    existing.setSubmissionTerms(incomingTerms);
	}

	if (existing.getUserCreated() == null) {
	    existing.setUserCreated(incoming.getUserCreated());
	}
	existing.setUserModified(incoming.getUserModified());

	existing.setParentUuId(incoming.getParentUuId());
	existing.setSubmissionId(incoming.getSubmissionId());
	existing.setSubmissionName(incoming.getSubmissionName());
	existing.setSubmitter(incoming.getSubmitter());

	return existing;
    }

    private static void addHistory(DbSubmissionTermEntry incoming, DbSubmissionTermEntry existing,
	    int existingRevisionId) {
	if (incoming.isActionRollback()) {
	    removeLastRevision(existing, existingRevisionId);
	} else {
	    DbSubmissionTermEntryHistory history = createHistory(incoming, existing, existingRevisionId);
	    if (existing.getHistory() == null) {
		existing.setHistory(new HashSet<>());
	    }
	    existing.getHistory().add(history);
	}
    }

    private static void removeLastRevision(DbSubmissionTermEntry existing, int lastRevisionId) {
	Long lastRevision = Long.valueOf(lastRevisionId);
	Iterator<DbSubmissionTermEntryHistory> iterator = existing.getHistory().iterator();
	while (iterator.hasNext()) {
	    DbSubmissionTermEntryHistory next = iterator.next();
	    if (next.getRevisionId() == lastRevision) {
		iterator.remove();
	    }
	}
    }
}
