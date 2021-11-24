package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbPriority;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.model.serializer.JsonIO;

public class DbSubmissionTermConverter {

    public static DbSubmissionTerm convertToDbTerm(String termEntryUuid, Term term) {
	if (term == null) {
	    return null;
	}

	String termId = term.getUuId();

	DbSubmissionTerm dbTerm = new DbSubmissionTerm();
	Long dateCreated = term.getDateCreated();
	if (dateCreated != null) {
	    dbTerm.setDateCreated(new Date(dateCreated));
	}
	Long dateModified = term.getDateModified();
	if (dateCreated != null) {
	    dbTerm.setDateModified(new Date(dateModified));
	}
	dbTerm.setFirst(term.isFirst());
	dbTerm.setForbidden(term.isForbidden());
	dbTerm.setLanguageId(term.getLanguageId());
	dbTerm.setNameAsBytes(term.getName());
	dbTerm.setProjectId(term.getProjectId());
	dbTerm.setStatus(term.getStatus());
	dbTerm.setStatusOld(term.getStatusOld());
	dbTerm.setUserCreated(term.getUserCreated());
	dbTerm.setUserModified(term.getUserModified());
	dbTerm.setUuId(termId);
	dbTerm.setTermEntryUuid(termEntryUuid);

	dbTerm.setInTranslationAsSource(term.getInTranslationAsSource());

	dbTerm.setParentUuId(term.getParentUuId());
	dbTerm.setAssignee(term.getAssignee());
	dbTerm.setCanceled(term.getCanceled());
	dbTerm.setComments(DbCommentConverter.convertToDbComments(termId, term.getComments()));
	dbTerm.setCommited(term.getCommited());
	Long dateCompleted = term.getDateCompleted();
	if (dateCompleted != null) {
	    dbTerm.setDateCompleted(new Date(dateCompleted));
	}
	Long dateSubmitted = term.getDateSubmitted();
	if (dateSubmitted != null) {
	    dbTerm.setDateSubmitted(new Date(dateSubmitted));
	}

	DbPriority dbPriority = new DbPriority();

	Priority priority = term.getPriority();
	if (priority != null) {
	    dbPriority.setAssigneePriority(priority.getAssigneePriority());
	    dbPriority.setSubmitterPriority(priority.getSubmitterPriority());
	}

	dbTerm.setPriority(dbPriority);
	dbTerm.setReviewRequired(term.getReviewRequired());
	dbTerm.setSubmissionId(term.getSubmissionId());
	dbTerm.setSubmissionName(term.getSubmissionName());
	dbTerm.setSubmitter(term.getSubmitter());
	dbTerm.setTempText(term.getTempText());

	dbTerm.setDescriptions(
		DbSubmissionTermDescriptionConverter.convertToDbTermDescriptions(termId, term.getDescriptions()));

	if (Objects.isNull(dbTerm.getReviewRequired())) {
	    dbTerm.setReviewRequired(Boolean.TRUE);
	}

	return dbTerm;
    }

    public static Set<DbSubmissionTerm> convertToDbTerms(String termEntryId, Collection<Term> terms) {
	if (CollectionUtils.isEmpty(terms)) {
	    return Collections.EMPTY_SET;
	}

	Set<DbSubmissionTerm> dbTerms = new HashSet<>();

	for (Term term : terms) {
	    if (term == null) {
		continue;
	    }

	    dbTerms.add(convertToDbTerm(termEntryId, term));
	}

	return dbTerms;
    }

    public static Term convertToTerm(String termEntryUuid, DbSubmissionTerm dbSubmissionTerm) {
	if (dbSubmissionTerm == null) {
	    return null;
	}

	String termId = dbSubmissionTerm.getUuId();
	Term term = new Term();
	Date dateCreated = dbSubmissionTerm.getDateCreated();
	if (dateCreated != null) {
	    term.setDateCreated(dateCreated.getTime());
	}
	Date dateModified = dbSubmissionTerm.getDateModified();
	if (dateCreated != null) {
	    term.setDateModified(dateModified.getTime());
	}
	term.setFirst(dbSubmissionTerm.getFirst());
	term.setForbidden(dbSubmissionTerm.getForbidden());
	term.setLanguageId(dbSubmissionTerm.getLanguageId());
	term.setName(dbSubmissionTerm.getNameAsString());
	term.setProjectId(dbSubmissionTerm.getProjectId());
	term.setStatus(dbSubmissionTerm.getStatus());
	term.setStatusOld(dbSubmissionTerm.getStatusOld());
	term.setUserCreated(dbSubmissionTerm.getUserCreated());
	term.setUserModified(dbSubmissionTerm.getUserModified());
	term.setUuId(termId);
	term.setTermEntryId(termEntryUuid);

	term.setInTranslationAsSource(dbSubmissionTerm.getInTranslationAsSource());

	term.setParentUuId(dbSubmissionTerm.getParentUuId());
	term.setAssignee(dbSubmissionTerm.getAssignee());
	term.setCanceled(dbSubmissionTerm.getCanceled());
	term.setComments(DbCommentConverter.convertToComments(dbSubmissionTerm.getComments()));
	term.setCommited(dbSubmissionTerm.getCommited());
	Date dateCompleted = dbSubmissionTerm.getDateCompleted();
	if (dateCompleted != null) {
	    term.setDateCompleted(dateCompleted.getTime());
	}
	Date dateSubmitted = dbSubmissionTerm.getDateSubmitted();
	if (dateSubmitted != null) {
	    term.setDateSubmitted(dateSubmitted.getTime());
	}

	Priority priority = new Priority();

	DbPriority dbPriority = dbSubmissionTerm.getPriority();
	if (dbPriority != null) {
	    priority.setAssigneePriority(dbPriority.getAssigneePriority());
	    priority.setSubmitterPriority(dbPriority.getSubmitterPriority());
	}

	term.setPriority(priority);
	term.setReviewRequired(dbSubmissionTerm.getReviewRequired());
	term.setSubmissionId(dbSubmissionTerm.getSubmissionId());
	term.setSubmissionName(dbSubmissionTerm.getSubmissionName());
	term.setSubmitter(dbSubmissionTerm.getSubmitter());
	term.setTempText(dbSubmissionTerm.getTempText());

	term.setDescriptions(
		DbSubmissionTermDescriptionConverter.convertToTermDescriptions(dbSubmissionTerm.getDescriptions()));

	return term;
    }

    public static Collection<Term> convertToTerms(String termEntryId, Set<DbSubmissionTerm> terms) {
	Set<Term> dbTerms = new HashSet<>();

	if (CollectionUtils.isNotEmpty(terms)) {
	    for (DbSubmissionTerm dbSubmissionTerm : terms) {
		if (dbSubmissionTerm == null) {
		    continue;
		}

		dbTerms.add(convertToTerm(termEntryId, dbSubmissionTerm));
	    }
	}

	return dbTerms;
    }

    public static void mergeWithExistingTerm(DbSubmissionTerm incoming, DbSubmissionTerm existing) {
	Date dateCreated = existing.getDateCreated();
	if (dateCreated == null) {
	    existing.setDateCreated(incoming.getDateCreated());
	}
	existing.setDateModified(incoming.getDateModified());

	Set<DbSubmissionTermDescription> incomingDescs = incoming.getDescriptions();
	Set<DbSubmissionTermDescription> existingDescs = existing.getDescriptions();
	if (existingDescs != null) {
	    DbSubmissionTermDescriptionConverter.mergeWithExistingDescriptions(incomingDescs, existingDescs);
	} else {
	    existing.setDescriptions(incomingDescs);
	}

	existing.setFirst(incoming.getFirst());
	existing.setForbidden(incoming.getForbidden());
	existing.setLanguageId(incoming.getLanguageId());
	existing.setName(incoming.getName());
	existing.setProjectId(incoming.getProjectId());
	existing.setStatus(incoming.getStatus());
	existing.setStatusOld(incoming.getStatusOld());
	if (existing.getUserCreated() == null) {
	    existing.setUserCreated(incoming.getUserCreated());
	}
	existing.setUserModified(incoming.getUserModified());

	existing.setInTranslationAsSource(incoming.getInTranslationAsSource());

	existing.setParentUuId(incoming.getParentUuId());
	existing.setAssignee(incoming.getAssignee());
	existing.setCanceled(incoming.getCanceled());

	Set<DbComment> incomingComments = incoming.getComments();
	Set<DbComment> existingComments = existing.getComments();
	if (existingComments != null) {
	    DbCommentConverter.mergeWithExistingComments(incomingComments, existingComments);
	} else {
	    existing.setComments(incomingComments);
	}

	existing.setCommited(incoming.getCommited());
	existing.setDateCompleted(incoming.getDateCompleted());
	existing.setDateSubmitted(incoming.getDateSubmitted());
	existing.setPriority(incoming.getPriority());
	existing.setReviewRequired(incoming.getReviewRequired());
	existing.setSubmissionId(incoming.getSubmissionId());
	existing.setSubmissionName(incoming.getSubmissionName());
	existing.setSubmitter(incoming.getSubmitter());
	existing.setTempText(incoming.getTempText());
    }

    public static void mergeWithExistingTerms(Collection<DbSubmissionTerm> incoming,
	    Collection<DbSubmissionTerm> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbSubmissionTerm> copyOfExisting = nullSafeCopy(existing);

	for (DbSubmissionTerm incomingTerm : incoming) {
	    DbSubmissionTerm existingTerm = findExistingDbSubmissionTermByUuid(incomingTerm.getUuId(), copyOfExisting);
	    if (existingTerm != null) {
		mergeWithExistingTerm(incomingTerm, existingTerm);
	    } else {
		existing.add(incomingTerm);
	    }
	}

	if (CollectionUtils.isNotEmpty(copyOfExisting)) {
	    copyOfExisting.forEach(existing::remove);
	}
    }

    private static DbSubmissionTermHistory createRevision(DbSubmissionTerm existing, int revisionId) {

	DbSubmissionTermHistory termHistory = new DbSubmissionTermHistory();

	termHistory.setRevision(JsonIO.writeValueAsBytes(existing));
	termHistory.setTermEntryUuid(existing.getTermEntryUuid());
	termHistory.setRevisionId(revisionId);

	return termHistory;
    }

    private static DbSubmissionTerm findExistingDbSubmissionTermByUuid(String termUuid,
	    final Collection<DbSubmissionTerm> copyOfExisting) {
	Iterator<DbSubmissionTerm> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbSubmissionTerm candidate = each.next();
	    if (termUuid.equals(candidate.getUuId())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static boolean hasDifference(DbSubmissionTerm incoming, DbSubmissionTerm existing) {
	if (!StringUtils.equals(incoming.getTempText(), existing.getTempText())) {
	    return true;
	}

	Set<DbSubmissionTermDescription> incomingDescs = incoming.getDescriptions();
	if (incomingDescs == null) {
	    incomingDescs = new HashSet<>();
	}
	Set<DbSubmissionTermDescription> existingDescs = existing.getDescriptions();
	if (existingDescs == null) {
	    existingDescs = new HashSet<>();
	}

	if (!CollectionUtils.isEqualCollection(incomingDescs, existingDescs)) {
	    return true;
	}

	if (!existing.getCanceled().equals(incoming.getCanceled())) {
	    return true;
	}

	if (!existing.getCommited().equals(incoming.getCommited())) {
	    return true;
	}

	return false;
    }

    private static Map<String, DbSubmissionTerm> mapTermsByTermUuId(Set<DbSubmissionTerm> terms) {
	Map<String, DbSubmissionTerm> map = new HashMap<>();
	for (DbSubmissionTerm dbTerm : terms) {
	    map.put(dbTerm.getUuId(), dbTerm);
	}
	return map;
    }

    private static Set<DbSubmissionTerm> nullSafeCopy(final Collection<DbSubmissionTerm> dbTerms) {
	return dbTerms == null ? Collections.emptySet() : new HashSet<>(dbTerms);
    }

    protected static Set<DbSubmissionTermHistory> findDifferentSubmissionTerms(DbSubmissionTermEntry incoming,
	    DbSubmissionTermEntry existing, int newRevisionId) {

	Set<DbSubmissionTermHistory> history = new HashSet<>();

	Set<DbSubmissionTerm> existingTerms = existing.getSubmissionTerms();
	if (Objects.isNull(incoming)) {
	    for (DbSubmissionTerm term : existingTerms) {
		history.add(createRevision(term, newRevisionId));
	    }
	    return history;
	}

	Set<DbSubmissionTerm> incomingTerms = incoming.getSubmissionTerms();
	Map<String, DbSubmissionTerm> existingMaped = mapTermsByTermUuId(existingTerms);
	for (DbSubmissionTerm dbTerm : incomingTerms) {
	    DbSubmissionTerm existingTerm = existingMaped.get(dbTerm.getUuId());
	    if (existingTerm != null) {
		if (hasDifference(dbTerm, existingTerm)) {
		    history.add(createRevision(existingTerm, newRevisionId));
		}
	    }
	}
	return history;
    }

}
